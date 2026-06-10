package com.reely.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.reely.databinding.ActivityMainBinding;
import com.reely.utils.ThemeManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ FIX: Status bar transparent & adaptif terhadap tema (Light/Dark)
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        
        // Memastikan konten tampil di bawah status bar (fullscreen effect)
        WindowCompat.setDecorFitsSystemWindows(window, false);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Mengatur warna ikon status bar berdasarkan tema
        updateStatusBarIcons();

        setupNavigation();
    }

    private void updateStatusBarIcons() {
        boolean isNight = ThemeManager.isNightCinema(this);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        
        // Jika BUKAN night mode (berarti Light), gunakan ikon gelap (appearanceLightStatusBars = true)
        windowInsetsController.setAppearanceLightStatusBars(!isNight);
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(com.reely.R.id.navHostFragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(
                    binding.bottomNavigation,
                    navController
            );
        }
    }

    @Override
    public void onBackPressed() {
        if (navController != null &&
                navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId()
                        == com.reely.R.id.homeFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
