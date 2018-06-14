package com.example.inhamap.Activities;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.R;
import com.example.inhamap.Utils.AudioWriterPCM;
import com.example.inhamap.Utils.JSONFileParser;
import com.example.inhamap.Utils.NodeListMaker;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
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

    private String dest = "";
    private int voiceStatus;
    private int optionPicked;
    private TextToSpeech myTTS;
    private HashMap TTSMap;

    private JSONObject mapData;
    private NodeListMaker list;
    private ArrayList<NodeItem> allNode;
    private long findNoId;
    private ArrayList<NodeItem> items;
    private ArrayList<NodeItem> tempList;
    private ArrayList<NodeItem> recElevList;
    private ArrayList<NodeItem> recSlpList;
    private ArrayList<NodeItem> recList;

    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
                //txtResult.setText("Connected");
                btnStart2.setText("Connected");
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER, 150);
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
                for (String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                mResult = strBuf.toString();
                //btnConfirm.setText(mResult);
                if (voiceStatus == 0) {//건물을 받는다
                    voiceStatus = 1;
                    String temp1 = cutTalk(mResult, 0);
                    if(temp1==""){
                        voiceStatus=0;
                        myTTS.speak("인식하지 못하였습니다. 다시 빨간 버튼을 눌러주세요.", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else{
                        String build = buildingSpeak(temp1);
                        btnConfirm.setEnabled(true);
                        dest = build;
                        informOption(build);
                        findNodeId(temp1);
                        if(tempList.size()==1){
                            voiceStatus=3;
                            myTTS.speak(tempList.get(0).getNodeName()+"으로 인식하였습니다.", TextToSpeech.QUEUE_FLUSH , null);
                            findNoId = tempList.get(0).getNodeID();
                        }
                    }
                } else if (voiceStatus == 1) {//옵션을 받는다(계단 혹은 이런것)
                    voiceStatus = 2;
                    String temp1 = cutTalk(mResult, 2);
                    confirmOption(temp1);
                    while (myTTS.isSpeaking()) ;
                    informPoint(0);//어떤 것이 있는지 알려주어야 함

                } else if (voiceStatus == 2) {
                    voiceStatus = 3;
                    //myTTS.speak(mResult, TextToSpeech.QUEUE_FLUSH, null);
                    String temp1 = cutTalk(mResult, 1);
                    if(temp1.equals("")){
                        voiceStatus = 2;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //롤리팝 이상부터 지원
                            myTTS.speak("다시 말씀해주세요.", TextToSpeech.QUEUE_FLUSH, null, String.valueOf(TTSMap));
                        }
                    }
                    else{
                        String output = findDoorName(temp1, optionPicked);
                        dest=output;
                        btnStart2.setText(dest);
                    }
                }
                //btnStart2.setText(dest);
                //btnConfirm.setText(mResult);
                if (voiceStatus == 3||voiceStatus==1) {
                    btnConfirm.setEnabled(true);
                }
                else {
                    btnConfirm.setEnabled(false);
                }

                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();

                //txtResult.setText(mResult);


                btnStart2.setText("목적지");
                btnStart2.setEnabled(true);

                btnConfirm.setText("확인");
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }

                btnStart2.setText(dest);
                if (dest.equals("")) {
                    btnStart2.setText("목적지");
                }
                btnStart2.setEnabled(true);

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
        voiceStatus = 0;

        JSONFileParser json = new JSONFileParser(this, "node_data_v2");
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
                boolean isInit = status == TextToSpeech.SUCCESS ? true : false;
                if (isInit) {
                    Log.d("TTS", "init 성공");

                    myTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {
                            Log.d(TAG, "progress on Start " + s);
                        }

                        @Override
                        public void onDone(String s) {
                            Log.d(TAG, "progress on Done " + s);
                            while(myTTS.isSpeaking()){
                                Log.d(TAG,"tts is speaking");
                            }
                            if (voiceStatus == 1) {
                                naverRecognizer.recognize();
                            } else if (voiceStatus == 2) {
                                naverRecognizer.recognize();
                            }
                        }

                        @Override
                        public void onError(String s) {
                            Log.d(TAG, "progress on Error " + s);
                        }
                    });
                } else {
                    Log.d("TTS", "init 실패");
                }
                TTSMap = new HashMap();
                TTSMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "talk");
                buttonInform();

            }
        });

        btnStart2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    voiceStatus = 0;
                    optionPicked = 0;
                    dest = "";
                    btnStart2.setText("Connecting...");
                    //txtResult.setText("Connecting...");
                    //btnStart2.setText(R.string.str_stop);
                    naverRecognizer.recognize();
                } else {
                    Log.d(TAG, "stop and wait Final Result");
                    btnStart2.setEnabled(false);
                    naverRecognizer.getSpeechRecognizer().stop();
                    //myTTS.speak("취소되었습니다. 다시버튼을 눌러 확인하여 주십시오.", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(voiceStatus==3) {
                    Intent reIntent = new Intent();
                    reIntent.putExtra("resultId", findNoId);
                    reIntent.putExtra("option", optionPicked);
                    setResult(Activity.RESULT_OK, reIntent);
                    finish();
                }
                else if(voiceStatus==1){
                    naverRecognizer.getSpeechRecognizer().stop();
                    btnConfirm.setEnabled(false);
                    voiceStatus=2;
                    optionPicked=0;
                    informPoint(optionPicked);
                }
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
        if (myTTS != null) {
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
    public String cutTalk(String s, int mode) {
        String[] sentence = new String[7];
        StringTokenizer st = new StringTokenizer(s, "\n");
        int a = 0;
        for (; st.hasMoreTokens(); a++) {
            sentence[a] = st.nextToken();
        }

        String[] result = new String[7];
        if (mode == 0) {
            for (int d = 0; d < a; d++) {
                int index = sentence[d].indexOf('관');
                if (sentence[d].contains("권") && !sentence[d].contains("관")) {
                    index = sentence[d].indexOf('권');
                }

                StringBuilder sb = new StringBuilder(sentence[d]);
                if (index > 0) {
                    sb.delete(index, sentence[d].length());
                }
                sentence[d] = sb.toString();
                Log.d(TAG + "Build", sb.toString());
                result[d] = buildingCheck(sentence[d]);
            }
        } else if (mode == 1) {
            for (int d = 0; d < a; d++) {
                int index = sentence[d].indexOf('관');
                if (sentence[d].contains("권") && !sentence[d].contains("관")) {
                    index = sentence[d].indexOf('권');
                }
                if (sentence[d].contains("테")) {
                    index = sentence[d].indexOf('테');
                } else if (sentence[d].contains("텍")) {
                    index = sentence[d].indexOf('텍');
                }
                StringBuilder sb = new StringBuilder(sentence[d]);
                if (index > 0) {
                    sb.delete(0, index);
                }
                sentence[d] = sb.toString();
                Log.d(TAG + "Door", sb.toString());
                result[d] = doorCheck(sentence[d]);
            }
        } else if (mode == 2) {
            for (int d = 0; d < a; d++) {
                result[d] = optionCheck(sentence[d]);
            }
        }
        String finresult = "";
        for (int d = 0; d < a; d++) {
            if (finresult.equals("") && !result[d].equals("")) {
                finresult = result[d];
                break;
            }
        }

        return finresult;
    }

    public void confirmOption(String s) {
        String speech = "";

        if (s.contains("제")) {
            optionPicked = 1;
            speech = "계단 제외 옵션을 선택하셨습니다.";
         }
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    public String optionCheck(String s) {
        String result = "";
        if (s.contains("제") | s.contains("재") | s.contains("회") |s.contains("외") | s.contains("계")| s.contains("개")) result += "제";
        return result;
    }

    public String buildingCheck(String s) {
//        if (s.contains("1") | s.contains("본") | s.contains("일")) return "본";
        if (s.contains("테") | s.contains("텍") | s.contains("택") | s.contains("핫") | s.contains("합") | s.contains("팩"))
            return "테";
        else if (s.contains("2") | s.contains("이") | s.contains("유") | s.contains("요") | s.contains("보"))
            return "2호";
//        else if (s.contains("주") | s.contains("년")) return "주";
        else if (s.contains("4") | s.contains("사")) return "4호";
//        else if (s.contains("5") | s.contains("오")) return "5호";
//        else if (s.contains("6") | s.contains("육")) return "6호";
//        else if (s.contains("7") | s.contains("칠")) return "7호";
//        else if (s.contains("9") | s.contains("구")) return "9호";
//        else if (s.contains("학") | s.contains("비")) return "학";
//        else if (s.contains("정")) return "정";
        else if (s.contains("후")) return "후";
        else return "";
    }

    public String buildingSpeak(String s) {
//        if (s.equals("본")) return "본관 1호관";
        if (s.equals("테")) return "하이테크 센터";
        else if (s.equals("2호")) return "2호관";
//        else if (s.equals("주")) return "60주년 기념관";
        else if (s.equals("4호")) return "4호관";
//        else if (s.equals("5호")) return "5호관";
//        else if (s.equals("6호")) return "6호관";
//        else if (s.equals("7호")) return "7호관";
//        else if (s.equals("9호")) return "9호관";
//        else if (s.equals("학")) return "학생회관";
//        else if (s.equals("정")) return "정문";
        else if (s.equals("후")) return "후문";
        else return "";
    }


    public String doorCheck(String s) {
        String result = "";
        if (s.contains("동")|s.contains("통")) result += "동쪽 ";
        else if (s.contains("서")) result += "서쪽 ";
        else if (s.contains("남")) result += "남쪽 ";
        else if (s.contains("북")|s.contains("부")) result += "북쪽 ";

        if (s.contains("저") | s.contains("처")| s.contains("자")| s.contains("칠")| s.contains("젖")) result += "저";
        else if (s.contains("고") | s.contains("친")| s.contains("포") | s.contains("코")| s.contains("굳")| s.contains("칭")) result += "고";
        else if (s.contains("sia")|s.contains("지") | s.contains("치") | s.contains("시")) result += "지";

        if (s.contains("1") | s.contains("일") | s.contains("입")|s.contains("길")) result += "1";
        else if (s.contains("2") | s.contains("이")) result += "2";
        else if (s.contains("4") | s.contains("사")|s.contains("읍")) result += "4";
        else if (s.contains("3") | s.contains("삼")| s.contains("산")) result += "3";
        else if (s.contains("5") | s.contains("오")| s.contains("공")|s.contains("정")) result += "5";
        else if (s.contains("6") | s.contains("육")) result += "6";
        else if (s.contains("7") | s.contains("칠")) result += "7";
        else if (s.contains("8") | s.contains("팔")) result += "8";
        else if (s.contains("9") | s.contains("칠")) result += "9";

        return result;
    }

    public void buttonInform() {
        String askDoor1 = "위쪽 빨강 버튼을 눌러 목적지를 말씀해주세요.";
        myTTS.speak(askDoor1, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void informOption(String build) {
        String askOption = "로 안내합니다. 계단을 제외한 경로를 안내받으시려면 계단 제외 라고 말씀해주세요. 필요없으시다면 확인버튼을 눌러주세요.";
        //String askOption ="짧음";
        if(!build.isEmpty()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //롤리팝 이상부터 지원
                myTTS.speak(build + askOption, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(TTSMap));
            }
        }
        else{
            mResult="";
            myTTS.speak("빨간 버튼을 다시 눌러 목적지를 말씀하세요.", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public int informPoint(int option) {
        String askDoor1 = "";
        String askElevSlp = "경사로와 엘레베이터가 있는 문은";
        String askElev = "엘레베이터가 있는 문은";
        String askSlp = "경사로가 있는 문은";
        String ask2 = "인식하지 못하였습니다. 다시 확인하여 주십시오.";
        String endSpeech = "이 있습니다. 이 문 중에 선택하여 주십시오.";
        String result="";

        for (int a = 0; a < tempList.size(); a++) {
            result+=tempList.get(a).getNodeName();
            if(tempList.get(a).getNodeElev()==1&&tempList.get(a).getNodeSlp()==1){
                result+="은 경사로와 엘레베이터가 있습니다 ";
            }
            else if(tempList.get(a).getNodeElev()==1){
                result+="은 엘레베이터가 있습니다 ";
            }
            else if(tempList.get(a).getNodeSlp()==1){
                result+="은 경사로가 있습니다 ";
            }
            else{
                result+="은 편의시설이 없습니다 ";
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myTTS.speak(result+"원하시는 출입구를 말씀하세요. 다시 시작시 빨간 버튼을 눌러주세요.", TextToSpeech.QUEUE_FLUSH, null, String.valueOf(TTSMap));
            while (myTTS.isSpeaking()) ;
        }
        return 0;
    }

    public long findNodeId(String build) {
        long result = 0;
        items = list.getItems();
        tempList = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getNodeName().contains(build) && items.get(i).getNodeStatus() == 0) {
                if(build.equals("2호")){
                    if(items.get(i).getNodeName().contains("1문")){
                        tempList.add(items.get(i));
                    }
                }
                else {
                    tempList.add(items.get(i));
                }
            }
        }
        return result;
    }

    public String findDoorName(String token, long flag) {
        String result = "";

        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).getNodeName().contains((token))) {
                result = tempList.get(i).getNodeName();
                findNoId = tempList.get(i).getNodeID();
                break;
            }
        }

        String fin = "으로 안내합니다. 다시시작은 빨간버튼, 길안내는 초록 버튼을 눌러주세요.";
        if(result.equals("")){
            myTTS.speak("빨간 버튼을 다시 누르고 목적지를 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);
        }
        else{
            while (myTTS.isSpeaking()) ;
            myTTS.speak(result + fin, TextToSpeech.QUEUE_FLUSH, null);
        }
        return result;
    }
}
