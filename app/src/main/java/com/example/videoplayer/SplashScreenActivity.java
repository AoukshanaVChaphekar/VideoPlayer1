package com.example.videoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.File;
import java.lang.reflect.Method;

public class SplashScreenActivity extends AppCompatActivity {

    private Animation splashAnimation;
    private ImageView image;
    private TextView textView;

    private static int SPLASH_SCREEN=3000;

    private File directory;
    private String[] allPath;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        splashAnimation= AnimationUtils.loadAnimation(SplashScreenActivity.this,R.anim.splash_animation);
        image=findViewById(R.id.splashImage);
        image.setAnimation(splashAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
                finish();
            }
        },SPLASH_SCREEN );

    }
}