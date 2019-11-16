/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.example.sunwoo.together;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
//import static com.example.android.tflitecamerademo.R.*;


/** Main {@code Activity} class for the Camera app. */
public class CameraActivity extends Activity implements OnInitListener{


    private TextToSpeech myTTS;
    private TextToSpeech TTS;
    private Camera camera;
    private Camera.Parameters p;
    private MediaPlayer player;
    public int count;
    public int Fcount = 0;
    public int tmp;


    MainActivity aActivity = (MainActivity)MainActivity.AActivity;
    //ImageClassifier test = (ImageClassifier)getApplicationContext();

    public ImageClassifier test;
    public String value;
    Vibrator vibrator = null;


    //boolean isflash = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aActivity.finish();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ((Button) findViewById(R.id.home_button))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        Intent intent = new Intent(getApplication(),MainActivity.class);
                        startActivity(intent);

                    }
                });

        if (null == savedInstanceState) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();

            myTTS = new TextToSpeech(this,this);
            TTS = new TextToSpeech(this,this);
            myTTS.setLanguage(Locale.KOREAN);
            myTTS.setLanguage(Locale.KOREAN);
            //SetLight(true);


            // Log.d(TAG,"finish!finish!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        }



    }

  /*private void SetLight(boolean ison) {
    if (ison)
    {
      camera = Camera.open(); // 카메라 객체 얻어옴
      p = camera.getParameters(); // 현재의 카메라 상태 얻어옴
      p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH); // 후라시를 켜는 상태로 변경
      camera.setParameters(p); //후라시 상태를 카메라객체에 적용
      camera.startPreview(); // 카메라상태에 맞게 카메라를 동작

      Log.d(TAG, "나 살아있다!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    else
    {
      p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF); //후라시 상태를 꺼진 상태로 변경
      camera.setParameters(p); // 후라시 상태를 카메라객체에 적용
      camera.stopPreview();//카메라상태에 맞게 카메라 중지
      camera.release(); // 카메라객체를 release 해줌
    }

  }*/

    public void onInit(int status){

        player = MediaPlayer.create(this, R.raw.sound);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        final TimerTask mTask = new TimerTask() {
            @Override
            public void run() {

                value = ImageClassifier.getData();
                String myText2;
                if (value.equals("10000") || value.equals("5000") || value.equals("50000") || value.equals("1000")) {
                    myText2 = value + " 원 ";
                    myTTS.speak(myText2, TextToSpeech.QUEUE_FLUSH, null);

                    if(value.equals("1000")){
                        count = 1000;
                        long[] pattern = {10,200};
                        vibrator.vibrate(pattern,-1);
                    }
                    else if(value.equals("5000")) {
                        count = 5000;
                        long[] pattern = {10,200,100,200};
                        vibrator.vibrate(pattern,-1);
                    }
                    else if(value.equals("10000")) {
                        count = 10000;
                        long[] pattern = {10,200,100,200,100,200};
                        vibrator.vibrate(pattern,-1);
                    }
                    else if(value.equals("50000")) {
                        count = 50000;
                        long[] pattern = {10,200,100,200,100,200,100,200};
                        vibrator.vibrate(pattern,-1);
                    }

                    else if(value.equals("etc")) {
                        count = 0;
                    }



                    else count = 0;

                } else {

                    count =0;

                    player.start();

                }



            }

        };

        Timer mTimer = new Timer();
        mTimer.schedule(mTask, 1500, 1800);


/*
    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        //ImageClassifier test = (ImageClassifier)getApplicationContext();
        value = ImageClassifier.getData();
        //inivalue = value;
        String myText1,myText2;
        if(value==null){
          myText1="오류입니다.";
          myTTS.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);
        }
        else{
          myText2 =value+"원 입니다.";
          myTTS.speak(myText2, TextToSpeech.QUEUE_FLUSH, null);
        }

        //turnOnFlashLight();
        Log.d(TAG, "still alive!!!!---------------");


      }
    }, 5000);

*/

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        TextView count_text;
        count_text = (TextView)findViewById(R.id.Count);
        String myText1;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                tmp = Fcount + count;
                Fcount = tmp;
                count_text.setText("총액: " + Fcount + "원");

                myTTS.shutdown();

                myText1 = Fcount + " 원 ";
                TTS.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);
                Log.d(TAG, "한번터치");

                break;


     /* case MotionEvent.ACTION_POINTER_DOWN:
        Log.d(TAG, "총액은 " + Fcount);*/


        }

        return super.onTouchEvent(event);

    }


/*
  public void turnOnFlashLight() {

    isflash=false;
    CameraManager mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    String cameraId;
    try {
      cameraId = mCameraManager.getCameraIdList()[0];

      Log.d(TAG, "------------flash is alive!!!!!!!--------------");

      try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          mCameraManager.setTorchMode(cameraId, true);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }*/

  /*public void turnOffFlashLight() {

      CameraManager mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
      String cameraId;
      try {
        cameraId = mCameraManager.getCameraIdList()[0];

        try {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCameraManager.setTorchMode(cameraId, false);

          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      } catch (CameraAccessException e) {
        e.printStackTrace();
      }
  }*/




    @Override
    protected void onPause() {
        myTTS.shutdown();
        player.stop();
        vibrator.cancel();
       // super.onStop();
        super.onPause();
        //TTS.shutdown();
       // super.onDestroy();
        //turnOffFlashLight();
        //SetLight(false);
        Log.d(TAG, "------flash is dead!!!!!-------");
    }


}
