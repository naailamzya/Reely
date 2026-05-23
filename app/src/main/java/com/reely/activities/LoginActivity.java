package com.reely.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;
import com.reely.databinding.ActivityLoginBinding;
import com.reely.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        setupClickListeners();
    }

    private void setupClickListeners() {

        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        binding.etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        binding.etUsername.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvLoginError.setVisibility(View.GONE);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void attemptLogin() {
        String username = binding.etUsername.getText() != null
                ? binding.etUsername.getText().toString().trim()
                : "";

        if (TextUtils.isEmpty(username)) {
            binding.tvLoginError.setVisibility(View.VISIBLE);
            binding.tvLoginError.setText(getString(com.reely.R.string.login_error_empty));
            shakeField();
            return;
        }

        hideKeyboard();

        boolean success = sessionManager.login(username);

        if (success) {
            navigateToMain();
        }
    }

    private void navigateToMain() {
        binding.btnLogin.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    binding.btnLogin.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction(() -> {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out);
                            }).start();
                }).start();
    }

    private void shakeField() {
        android.animation.ObjectAnimator shake = android.animation.ObjectAnimator.ofFloat(
                binding.tilUsername, "translationX",
                0f, 16f, -16f, 12f, -12f, 8f, -8f, 0f);
        shake.setDuration(400);
        shake.start();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}