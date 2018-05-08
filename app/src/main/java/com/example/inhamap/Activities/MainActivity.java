package com.example.inhamap.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.health.PackageHealthStats;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.inhamap.Fragments.CustomMapFragment;
import com.example.inhamap.R;
import com.github.chrisbanes.photoview.PhotoView;

public class MainActivity extends AppCompatActivity {

    Toolbar myToolbar;
    ImageButton voiceBtn;

    private long source;
    private long dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* toolbar */
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("출발지 검색");

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
                        startActivityForResult(voiceRec, 111);
                    }

                }
                else{
                    Intent voiceRec = new Intent(v.getContext(), NaverTalkActivity.class);
                    startActivityForResult(voiceRec, 111);
                }

            }
        });
        /* photoView : 사진 확대 축소 가능하게 해주는 라이브러리 */
        /*
        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        photoView.setImageResource(R.drawable.test3);
        */

        // 지도 Fragment 불러오기
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_map_view, new CustomMapFragment());
        fragmentTransaction.commit();
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
            case R.id.action_search:
                // User chose the "Settings" item, show the app settings UI...
                Toast.makeText(getApplicationContext(), "찾기 버튼 클릭됨", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(this, StartingDoorActivity.class);
                //startActivity(intent);
                return true;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==111){
            if(resultCode==RESULT_OK){
                long[] temp = data.getLongArrayExtra("resultId");
                source = temp[0];
                dest = temp[1];
            }
        }
    }
}
