package com.book.civiclink2o;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    private static final int SPLASH_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView dot1 = findViewById(R.id.dot1);
        ImageView dot2 = findViewById(R.id.dot2);
        ImageView dot3 = findViewById(R.id.dot3);

        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce_animation);


        dot1.startAnimation(bounce);

        ValueAnimator animator = ValueAnimator.ofInt(0, 2);
        animator.setDuration(900);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {

                dot1.clearAnimation();
                dot2.clearAnimation();
                dot3.clearAnimation();

                dot1.startAnimation(bounce);

                new Handler().postDelayed(() -> {
                    dot2.startAnimation(bounce);
                }, 300);

                new Handler().postDelayed(() -> {
                    dot3.startAnimation(bounce);
                }, 600);
            }
        });

        dot1.startAnimation(bounce);
        new Handler().postDelayed(() -> {
            dot2.startAnimation(bounce);
        }, 300);
        new Handler().postDelayed(() -> {
            dot3.startAnimation(bounce);
        }, 600);


        animator.start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, LanguageSelectionActivity.class);
            startActivity(mainIntent);

            finish();
        }, SPLASH_DURATION);
    }
}