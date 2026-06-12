package com.reely.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.reely.R;
import com.reely.databinding.ActivityMainBinding;
import com.reely.utils.ThemeManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Membuat sistem bar transparan dan memungkinkan konten menggambar di bawahnya
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        
        WindowCompat.setDecorFitsSystemWindows(window, false);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(
                    binding.bottomNavigation,
                    navController
            );

            // Listener untuk menyesuaikan warna bar berdasarkan fragment yang aktif
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                updateSystemUIAppearance(destination.getId());
            });
        }
    }

    private void updateSystemUIAppearance(int destinationId) {
        boolean isNight = ThemeManager.isNightCinema(this);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        
        // 1. Atur Bottom Navigation View background
        // Kita gunakan warna background utama agar menyatu dengan konten fragment
        binding.bottomNavigation.setBackgroundColor(getResources().getColor(R.color.color_background, getTheme()));
        binding.bottomNavigation.setElevation(0f); // Hilangkan bayangan agar terlihat 'flat' dan modern

        // 2. Atur Ikon Status Bar
        if (destinationId == R.id.homeFragment) {
            // Di Home, ada Hero Banner (gambar). Kita ingin ikon status bar selalu putih (terang)
            controller.setAppearanceLightStatusBars(false);
        } else {
            // Di halaman lain, sesuaikan dengan tema (Light -> ikon gelap, Dark -> ikon putih)
            controller.setAppearanceLightStatusBars(!isNight);
        }

        // 3. Atur Ikon Navigation Bar (bawah)
        // Karena background navbar kita solid color_background, ikon harus mengikuti tema
        controller.setAppearanceLightNavigationBars(!isNight);
    }

    @Override
    public void onBackPressed() {
        if (navController != null &&
                navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() == R.id.homeFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
