package com.example.sunwoo.together;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Translate extends Activity implements TextToSpeech.OnInitListener{

    Button btn;
    TextView tv;

    public String text;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private MyHandler myHandler;

    //TextView textView;
    private TextToSpeech myTTS;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate);

        myTTS = new TextToSpeech(this, this);

        // StrictMode는 개발자가 실수하는 것을 감지하고 해결할 수 있도록 돕는 일종의 개발 툴
        // - 메인 스레드에서 디스크 접근, 네트워크 접근 등 비효율적 작업을 하려는 것을 감지하여
        //   프로그램이 부드럽게 작동하도록 돕고 빠른 응답을 갖도록 함, 즉  Android Not Responding 방지에 도움
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try {
            int port = 7770;
            String ip = "192.168.0.162";
            Socket clientSocket = new Socket(ip, port);
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        myHandler = new MyHandler();
        MyThread myThread = new MyThread();
        myThread.start();

       // btn = (Button) findViewById(R.id.btn);
        tv = (TextView) findViewById(R.id.tv);
        socketOut.println(123);

        ((Button) findViewById(R.id.home_button))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Intent intent = new Intent(getApplication(),MainActivity.class);
                        startActivity(intent);
                    }
                });

    }

    class MyThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    // InputStream의 값을 읽어와서 data에 저장
                    String data = socketIn.readLine();
                    // Message 객체를 생성, 핸들러에 정보를 보낼 땐 이 메세지 객체를 이용
                    Message msg = myHandler.obtainMessage();
                    msg.obj = data;
                    myHandler.sendMessage(msg);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            tv.setText(msg.obj.toString());

            text = msg.obj.toString();
        }
    }

    public void onInit(int status){

        String myText = text;

        myTTS.speak(myText, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        myTTS.shutdown();
    }
}
