package com.example.inhamap.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.inhamap.Models.AdjacentEdge;
import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.Models.VoicePathElement;
import com.example.inhamap.PathFindings.FindPath;
import com.example.inhamap.PathFindings.PassingNodeListMaker;
import com.example.inhamap.R;
import com.example.inhamap.Utils.AudioWriterPCM;
import com.example.inhamap.Utils.EdgeListMaker;
import com.example.inhamap.Utils.JSONFileParser;
import com.example.inhamap.Utils.NodeListMaker;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import static android.speech.tts.TextToSpeech.ERROR;
import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;

public class NaverTalkActivity extends Activity {

	private static final String TAG = NaverTalkActivity.class.getSimpleName();
	private static final String CLIENT_ID = "ksmO01UaRZy4atzO08RC";
    // 1. "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    // 2. build.gradle (Module:app)에서 패키지명을 실제 개발자센터 애플리케이션 설정의 '안드로이드 앱 패키지 이름'으로 바꿔 주세요

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;

   // private TextView txtResult;
    private Button btnStart2;
    private Button btnConfirm;
    private String mResult;

    private AudioWriterPCM writer;

    private int statFlag;//0일시에는 출발지, 1일시에는 목적지 인식
    private String source="";
    private String dest="";

    private TextToSpeech myTTS;

    private JSONObject mapData;
    private long destId;
    private EdgeList edges;
    private NodeListMaker list;
    private ArrayList<NodeItem> allNode;

    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
                //txtResult.setText("Connected");
                if(statFlag==1){
                    btnStart2.setText("Connected");
                }
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                //txtResult.setText(mResult);
                break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
            	SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
            	List<String> results = speechRecognitionResult.getResults();
            	StringBuilder strBuf = new StringBuilder();
            	for(String result : results) {
            		strBuf.append(result);
            		strBuf.append("\n");
            	}
                mResult = strBuf.toString();
                //btnConfirm.setText(mResult);
            	String temp1 = cutTalk(mResult, 0);
            	String temp2 = cutTalk(mResult, 1);

                if(statFlag==1){
            	    destId = findNodeId(temp1, temp2);
            	    dest = findDoorName(destId);
                    btnStart2.setText(dest);
                    informPoint(dest);
                }

                btnConfirm.setText(mResult);
                if(destId!=0){
                    btnConfirm.setEnabled(true);
                }

                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();

                //txtResult.setText(mResult);

                if(statFlag==1){
                    btnStart2.setText("목적지");
                    btnStart2.setEnabled(true);
                }
                btnConfirm.setText("확인");
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }

                if(statFlag==1){
                    btnStart2.setText(dest);
                    if(dest.equals("")){
                        btnStart2.setText("목적지");
                    }
                    btnStart2.setEnabled(true);
                }
                btnConfirm.setText("확인");
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        //txtResult = (TextView) findViewById(R.id.txt_result);
        btnStart2 = (Button) findViewById(R.id.btn_start2);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);

        JSONFileParser json = new JSONFileParser(this, "node_data");
        this.mapData = json.getJSON();
        list = new NodeListMaker(this.mapData);
        allNode = new ArrayList<NodeItem>();

        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    myTTS.setLanguage(Locale.KOREAN);
                }
                informUser();
            }
        });

        btnStart2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    statFlag=1;
                    btnStart2.setText("Connecting...");
                    //txtResult.setText("Connecting...");
                    //btnStart2.setText(R.string.str_stop);
                    naverRecognizer.recognize();
                } else {
                    Log.d(TAG, "stop and wait Final Result");
                    btnStart2.setEnabled(false);

                    naverRecognizer.getSpeechRecognizer().stop();
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent reIntent = new Intent();
                reIntent.putExtra("resultId", destId);
                setResult(Activity.RESULT_OK, reIntent);
                finish();
            }
        });



    }

    @Override
    protected void onStart() {
    	super.onStart();
    	// NOTE : initialize() must be called on start time.
    	naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mResult = "";
        //txtResult.setText("");
        btnStart2.setText("목적지");
        btnStart2.setEnabled(true);
        btnConfirm.setText("확인");
        btnConfirm.setEnabled(false);
    }

    @Override
    protected void onStop() {
    	super.onStop();
    	// NOTE : release() must be called on stop time.
    	naverRecognizer.getSpeechRecognizer().release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myTTS !=null){
            myTTS.stop();
            myTTS.shutdown();
            myTTS = null;
        }
    }

    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<NaverTalkActivity> mActivity;

        RecognitionHandler(NaverTalkActivity activity) {
            mActivity = new WeakReference<NaverTalkActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            NaverTalkActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    /*
* cutTalk은 handleMessage의 finalresult에서 strBuf.toString()과 mode를 parameter로 받는다.
* mode는 0일시에 건물을 detect, 1일시에 문을 detect
* 문과 건물의 결과값은 int형
* 건물 1호관~9호관까지 각 숫자 return 하이테크는 8, 60주년은 3, 학관은 10
* 문은 동문계열은 11~19 서문은 21~29 남은 31~39 북은 41~49
* */
    public String cutTalk(String s, int mode){
        String[] sentence = new String[7];
        StringTokenizer st = new StringTokenizer(s,"\n");
        int a=0;
        for(; st.hasMoreTokens(); a++){
            sentence[a] = st.nextToken();
        }

        String[] result = new String[7];
        if(mode==0) {
            for (int d = 0; d < a; d++) {
                int index = sentence[d].indexOf('관');
                if(sentence[d].contains("권")&&!sentence[d].contains("관")){
                    index = sentence[d].indexOf('권');
                }

                StringBuilder sb = new StringBuilder(sentence[d]);
                if(index>0){
                    sb.delete(index,sentence[d].length());
                }
                sentence[d] = sb.toString();
                Log.d(TAG+"Build", sb.toString());
                result[d] = buildingCheck(sentence[d]);
            }
        }
        else if(mode==1){
            for(int d=0;d<a;d++){
                int index = sentence[d].indexOf('관');
                if(sentence[d].contains("권")&&!sentence[d].contains("관")){
                    index = sentence[d].indexOf('권');
                }
                if(sentence[d].contains("테")){
                    index = sentence[d].indexOf('테');
                }
                else if(sentence[d].contains("텍")){
                    index = sentence[d].indexOf('텍');
                }
                StringBuilder sb = new StringBuilder(sentence[d]);
                if(index>0){
                    sb.delete(0,index);
                }
                sentence[d] = sb.toString();
                Log.d(TAG+"Door", sb.toString());
                result[d] = doorCheck(sentence[d]);
            }
        }
        String finresult="";
        for(int d=0;d<a;d++){
            if(finresult.equals("")&&!result[d].equals("")){
                finresult=result[d];
                break;
            }
        }

        return finresult;
    }

    public String buildingCheck(String s){
        if(s.contains("1")|s.contains("본")|s.contains("일")) return "본";
        else if(s.contains("테")|s.contains("텍")|s.contains("택")|s.contains("핫")|s.contains("합")|s.contains("팩")) return "테";
        else if(s.contains("2")|s.contains("이")|s.contains("유")|s.contains("요")|s.contains("보")) return "2호";
        else if(s.contains("주")|s.contains("년")) return "주";
        else if(s.contains("4")|s.contains("사")) return "4호";
        else if(s.contains("5")|s.contains("오")) return "5호";
        else if(s.contains("6")|s.contains("육")) return "6호";
        else if(s.contains("7")|s.contains("칠")) return "7호";
        else if(s.contains("9")|s.contains("구")) return "9호";
        else if(s.contains("학")|s.contains("비")) return "학";
        else if(s.contains("정")) return "정";
        else if(s.contains("후")) return "후";
        else return "";
    }

    public String doorCheck(String s){
        String result = "";
        if(s.contains("동")) result+="동";
        else if(s.contains("서")) result+="서";
        else if(s.contains("남")) result+="남";
        else if(s.contains("북")) result+="북";

        if(s.contains("고")|s.contains("포")|s.contains("코")) result+="고";
        else if(s.contains("저")|s.contains("처")) result+="저";
        else if(s.contains("지")|s.contains("치")|s.contains("시")) result+="지";

        if(s.contains("1")|s.contains("일")|s.contains("입")) result+= "1";
        else if(s.contains("2")|s.contains("이")) result+= "2";
        else if(s.contains("3")|s.contains("삼")) result+= "3";
        else if(s.contains("4")|s.contains("사")) result+= "4";
        else if(s.contains("5")|s.contains("오")) result+= "5";
        else if(s.contains("6")|s.contains("육")) result+= "6";
        else if(s.contains("7")|s.contains("칠")) result+= "7";
        else if(s.contains("8")|s.contains("팔")) result+= "8";
        else if(s.contains("9")|s.contains("칠")) result+= "9";

        return result+"문";
    }

    public void informUser(){
        String askDoor1 = "위쪽 파란버튼이 목적지 인식 버튼입니다. 목적지를 인식 후에 아래 확인 버튼을 누르십시오.";
        myTTS.speak(askDoor1, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void informPoint(String point){
        String askDoor1 = "으로 인식했습니다. 잘못 인식되었다면 다시 인식시켜 주십시오.";
        String ask2 = "인식하지 못하였습니다. 다시 확인하여 주십시오.";
        if(point.equals("")){
            myTTS.speak(ask2, TextToSpeech.QUEUE_FLUSH, null);
        }
        else{
            myTTS.speak(point+askDoor1, TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    public long findNodeId(String build, String door){
        long result = 0;
        ArrayList<NodeItem> items = list.getItems();
        ArrayList<NodeItem> tempList = new ArrayList<>();
        for(int i = 0; i < items.size(); i++){
            if(items.get(i).getNodeName().contains(build)&&items.get(i).getNodeStatus()==0){
                result = items.get(i).getNodeID();
                tempList.add(items.get(i));
            }
        }

        char[] defDoor;
        defDoor = door.toCharArray();

        int set;
        for(int i = 0; i < tempList.size(); i++){
            set = 0;
            for(int j = 0; j<door.length(); j++){
                if(tempList.get(i).getNodeName().contains(defDoor[j]+"")){
                    set++;
                }
            }
            if(set==door.length()){
                result = tempList.get(i).getNodeID();
                break;
            }
        }
        return result;
    }

    public String findDoorName(long nodeId){
        String result="";
        if(nodeId!=0){
            ArrayList<NodeItem> items = list.getItems();
            allNode = list.getItems();
            for(int i = 0; i < items.size(); i++){
                if(items.get(i).getNodeID()==nodeId){
                    result = items.get(i).getNodeName();
                    break;
                }
            }}
        return result;
    }

    private ArrayList<NodeItem> addNodes(EdgeList edges){
        ArrayList<NodeItem> list = new ArrayList<NodeItem>();
        for(int i = 0; i < edges.size(); i++){
            AdjacentEdge e = edges.getEdge(i);
            long n1 = e.getNodes()[0].getNodeID();
            long n2 = e.getNodes()[1].getNodeID();
            boolean c1 = false;
            boolean c2 = false;
            for(int j = 0; j < list.size(); j++){
                if(list.get(j).getNodeID() == n1){
                    c1 = true;
                }
            }
            for(int j = 0; j < list.size(); j++){
                if(list.get(j).getNodeID() == n2){
                    c2 = true;
                }
            }
            if(!c1){
                for(int j = 0; j < allNode.size(); j++){
                    if(allNode.get(j).getNodeID() == n1){
                        list.add(allNode.get(j));
                    }
                }
            }
            if(!c2){
                for(int j = 0; j < allNode.size(); j++){
                    if(allNode.get(j).getNodeID() == n2){
                        list.add(allNode.get(j));
                    }
                }
            }
        }
        return list;
    }
}
