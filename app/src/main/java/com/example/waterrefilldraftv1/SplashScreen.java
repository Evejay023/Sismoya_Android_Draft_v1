package com.example.waterrefilldraftv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstancesState){
        super.onCreate(savedInstancesState);
        setContentView(R.layout.splash_screen);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(1500);
                }catch (InterruptedException e){
                    throw new RuntimeException(e);
                }finally {
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }


            }

        };
        timer.start();


    }



}
