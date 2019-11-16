package com.example.sunwoo.together;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

public class Intro extends Activity implements TextToSpeech.OnInitListener{
    private TextToSpeech myTTS;

    @Override
    public void onCreate(Bundle savedInstanceState){

        myTTS = new TextToSpeech(this, this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(Intro.this, MainActivity.class);
            startActivity(intent);
            finish();
        },2000);
    }
    public void onInit(int status){
        String myText1 = "같이가 실행되었습니다.";


        myTTS.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        myTTS.shutdown();
    }
}