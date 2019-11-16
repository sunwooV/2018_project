package com.example.sunwoo.together;

//받아온 한글 표시&말하는 액티비티

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Roading extends Activity implements TextToSpeech.OnInitListener{

    private ImageView imgAndroid;
    private Animation anim;

    private TextToSpeech myTTS;
    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        myTTS = new TextToSpeech(this, this);

        initView();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Roading.this, Translate.class);
                startActivity(intent);
                finish();
            }
        }, 5000);//초는 여기 숫자에 초*1000 해서 적으면 됨
    }


    private void initView(){
        imgAndroid = (ImageView) findViewById(R.id.img_android);
        anim = AnimationUtils.loadAnimation(this, R.anim.loading_s);
        imgAndroid.setAnimation(anim);
    }

    public void onInit(int status){
        String myText1 = "로딩 중입니다.";

        myTTS.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);
       // myTTS.speak(myText2, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        myTTS.shutdown();
    }

}