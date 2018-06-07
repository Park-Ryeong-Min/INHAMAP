package com.example.inhamap.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.health.PackageHealthStats;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.inhamap.Commons.GlobalApplication;
import com.example.inhamap.Components.NodeImageButton;
import com.example.inhamap.Components.TestDrawingView;
import com.example.inhamap.Fragments.CustomMapFragment;
import com.example.inhamap.Models.AdjacentEdge;
import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.Models.VoicePathElement;
import com.example.inhamap.PathFindings.FindPath;
import com.example.inhamap.PathFindings.PassingNodeListMaker;
import com.example.inhamap.R;
import com.example.inhamap.Threads.VoiceNavigatingThread;
import com.example.inhamap.Utils.EdgeListMaker;
import com.example.inhamap.Utils.JSONFileParser;
import com.example.inhamap.Utils.NodeListMaker;
import com.example.inhamap.Utils.ValueConverter;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Toolbar myToolbar;
    ImageButton voiceBtn;

    private long source;
    private long dest;
    private boolean voiceFlag;

    private ArrayList<NodeItem> allNodes;
    private EdgeList edges;
    private CustomMapFragment fragment;

    public static ArrayList<NodeImageButton> imageButtons;
    public static NodeImageButton startButton;
    public static NodeImageButton destinationButton;

    public TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        source = 0;
        dest = 0;
        voiceFlag = false;

        this.allNodes = GlobalApplication.nodesExceptStairs;
        this.edges = GlobalApplication.edgesExceptStairs;
        /*
        allNodes = new ArrayList<NodeItem>();
        JSONFileParser json = new JSONFileParser(getApplicationContext(), "node_data_v2");
        NodeListMaker list = new NodeListMaker(json.getJSON());
        edges = new EdgeListMaker(json.getJSON(), 1).getEdges();
        //EdgeListMaker edges = new EdgeListMaker(this.mapData);
        //this.edges = edges.getEdges();
        ArrayList<NodeItem> items = list.getItems();
        for(int i = 0; i < items.size(); i++){
            this.allNodes.add(items.get(i));
        }
        */
        imageButtons = new ArrayList<NodeImageButton>();

        // 지도 Fragment 불러오기
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new CustomMapFragment();
        fragmentTransaction.replace(R.id.main_map_view, fragment);
        fragmentTransaction.commit();

        /* toolbar */
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 이게 네비쪽 버튼 관련된 함수인듯
        //getSupportActionBar().setTitle("출발지 검색");

        /*음성인식 버튼의 초기화 및 권한의 획득*/
        voiceBtn = (ImageButton) findViewById(R.id.imageButton);
        voiceBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){//마시멜로우 이상인지 체크

                    int permissionResult = checkSelfPermission(Manifest.permission.RECORD_AUDIO);

                    if(permissionResult == PackageManager.PERMISSION_DENIED){//허가 거부인지 체크
                        if(shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)){//
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("권한이 필요합니다.")
                                    .setMessage("이 기능을 사용하기 위해서는 단말기의 \"녹음\"권한이 필요합니다. 계속 하시겠습니까?")
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                                            }
                                        }
                                    })
                                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                        @Override public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(MainActivity.this, "기능을 취소했습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                        else {
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                        }
                    }
                    else{
                        Intent voiceRec = new Intent(v.getContext(), NaverTalkActivity.class);
                        startActivityForResult(voiceRec,111);
                    }

                }
                else{
                    Intent voiceRec = new Intent(v.getContext(), NaverTalkActivity.class);
                    startActivityForResult(voiceRec,111);
                }

            }
        });
        /* photoView : 사진 확대 축소 가능하게 해주는 라이브러리 */
        /*
        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        photoView.setImageResource(R.drawable.test3);
        */

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==111){
            if(resultCode==RESULT_OK){
                dest = data.getLongExtra("resultId", 0);
                voiceFlag = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MAIN", "On Resume.");
        if(voiceFlag){
            Log.e("MAIN", Long.toString(source) + " , " + Long.toString(dest));
            TestDrawingView view = fragment.getDrawingView();
            // Find my location to start.
            NodeItem cur = ValueConverter.getNearestNodeItem(GlobalApplication.myLocationLatitude, GlobalApplication.myLocationLongitude);
            source = cur.getNodeID();

            // Re - drawing
            ArrayList<NodeItem> node = addNodes(edges);
            // 나중에 dest 값을 변경해서 실행해야함
            // NaverTalkActivity 에서 검색하는 대상 파일을 바꿔야함
            dest = 1;
            FindPath find = new FindPath(node, edges, source, dest);
            EdgeList path = find.getPaths();
            if(getNodeImageButtonByID(source) != null){
                getNodeImageButtonByID(source).setBackgroundImageByStatus(3);
            }
            // 이것도 나중에 변경해야함
            getNodeImageButtonByID(dest).setBackgroundImageByStatus(4);
            ArrayList<NodeItem> passingNodes = find.getPassingNodes();
            //ArrayList<VoicePathElement> voices = new PassingNodeListMaker(getApplicationContext(), passingNodes).getVoiceElements();
            view.drawEdges(path);
            voiceFlag = false;
            //VoiceNavigatingThread thread = new VoiceNavigatingThread(getApplicationContext(), find.getPassingNodes(), path, voices, source, dest);
            VoiceNavigatingThread thread = new VoiceNavigatingThread(getApplicationContext(), tts, source, dest);
            thread.start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MAIN", "On start");
    }

    /* toolbar 생성하는 함수 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }


    //추가된 소스, ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            // 액션바 메뉴 재구성으로 인해 더 이상 필요가 없는 코드
            /*
            case R.id.action_search:
                // User chose the "Settings" item, show the app settings UI...
                Toast.makeText(getApplicationContext(), "찾기 버튼 클릭됨", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(this, StartingDoorActivity.class);
                //startActivity(intent);
                return true;
                */
            case R.id.action_settings2:
                Toast.makeText(getApplicationContext(), "항목 1 버튼 클릭됨", Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(this, NoiseDetectActivity.class);
                startActivity(intent2);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<NodeItem> addNodes(EdgeList edges){
        ArrayList<NodeItem> items = new ArrayList<NodeItem>();
        for(int i = 0; i < edges.size(); i++){
            AdjacentEdge e = edges.getEdge(i);
            long n1 = e.getNodes()[0].getNodeID();
            long n2 = e.getNodes()[1].getNodeID();
            boolean c1 = false;
            boolean c2 = false;
            for(int j = 0; j < items.size(); j++){
                if(items.get(j).getNodeID() == n1){
                    c1 = true;
                }
            }
            for(int j = 0; j < items.size(); j++){
                if(items.get(j).getNodeID() == n2){
                    c2 = true;
                }
            }
            if(!c1){
                for(int j = 0; j < allNodes.size(); j++){
                    if(allNodes.get(j).getNodeID() == n1){
                        items.add(allNodes.get(j));
                    }
                }
            }
            if(!c2){
                for(int j = 0; j < allNodes.size(); j++){
                    if(allNodes.get(j).getNodeID() == n2){
                        items.add(allNodes.get(j));
                    }
                }
            }
        }
        return items;
    }

    private NodeImageButton getNodeImageButtonByID(long id){
        for(int i = 0; i < imageButtons.size(); i++){
            if(imageButtons.get(i).getNodeID() == id){
                return imageButtons.get(i);
            }
        }
        return null;
    }
}
