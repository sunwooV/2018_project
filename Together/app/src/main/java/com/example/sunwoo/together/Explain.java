package com.example.sunwoo.together;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class Explain extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explain);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        Intent intent = new Intent(Explain.this, Roading.class);
        startActivity(intent);
        return false;
    }
}
