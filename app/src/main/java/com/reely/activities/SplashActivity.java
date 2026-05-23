package com.reely.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import com.reely.databinding.ActivitySplashBinding;
import com.reely.utils.Constants;
import com.reely.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        startSplashAnimation();
    }

    private void startSplashAnimation() {
        ObjectAnimator fadeInLogo = ObjectAnimator.ofFloat(
                binding.layoutLogoGroup, "alpha", 0f, 1f);
        fadeInLogo.setDuration(800);
        fadeInLogo.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator slideUpLogo = ObjectAnimator.ofFloat(
                binding.layoutLogoGroup, "translationY", 60f, 0f);
        slideUpLogo.setDuration(800);
        slideUpLogo.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator fadeInProgress = ObjectAnimator.ofFloat(
                binding.progressSplash, "alpha", 0f, 1f);
        fadeInProgress.setDuration(400);
        fadeInProgress.setStartDelay(600);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(fadeInLogo, slideUpLogo, fadeInProgress);
        animSet.start();

        new Handler(Looper.getMainLooper()).postDelayed(
                this::navigateToNext,
                Constants.SPLASH_DELAY_MS
        );
    }

    private void navigateToNext() {
        Intent intent;

        if (sessionManager.isLoggedIn()) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(
                binding.getRoot(), "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        fadeOut.start();
    }
}