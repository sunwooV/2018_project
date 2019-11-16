package com.example.sunwoo.together;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener{

    public static Activity AActivity;
    public String myText;
    //public String myText2;
    public String receive;
    public static int num;
    private TextToSpeech myTTS;
    //public String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        AActivity = MainActivity.this;
        super.onCreate(savedInstanceState);
        SlidingView sv = new SlidingView(this);
        View v1 = View.inflate(this, R.layout.slide1,null);
        View v2 = View.inflate(this, R.layout.slide2, null);
        sv.addView(v1);
        sv.addView(v2);
        setContentView(sv);




        Button launch1 = (Button)findViewById(R.id.btn1);
        launch1.setOnClickListener(v11 -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        });


        Button launch2 = (Button)findViewById(R.id.btn2);
        launch2.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v2){
                Intent intent = new Intent(MainActivity.this, FocusCamera.class);
                startActivity(intent);
            }
        });

        myTTS = new TextToSpeech(this, this);




    }


    public void onInit(int status){



        myText = SlidingView.getData();


        if(myText.equals("지페")||myText.equals("점자")){
            num = 0;

        }

        //myText = SlidingView.getData();
        TimerTask mTask = new TimerTask() {
            @Override
            public void run() {



                if(myText.equals("지폐")||myText.equals("점자")) {
                    if(num == 0){
                        myTTS.speak(myText, TextToSpeech.QUEUE_FLUSH, null);
                        num++;
                    }

                    else{

                        receive = SlidingView.getData();

                        if(receive.equals(myText)){
                           num++;

                        }
                        else if(!receive.equals(myText)){
                            myText = receive;
                            num = 0;
                        }
                    }
                    //count++;

                }


            }

            @Override
            public boolean cancel(){
                Log.d(TAG, "타이머 종료");
                return super.cancel();
            }

        };

        Timer mTimer = new Timer();
        mTimer.schedule(mTask, 0, 450);




        //myTTS.speak(myText, TextToSpeech.QUEUE_FLUSH, null);

    }
    @Override
    protected void onPause() {
        super.onPause();
        //super.onDestroy();
        myTTS.shutdown();
        //canc
    }

}

