package com.reely.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.reely.activities.LoginActivity;
import com.reely.databinding.FragmentProfileBinding;
import com.reely.utils.SessionManager;
import com.reely.utils.ThemeManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        setupUserInfo();
        setupThemeSelection();
        setupLogout();
    }

    private void setupUserInfo() {
        String username = sessionManager.getUsername();
        binding.tvProfileUsername.setText(username);

        if (!username.isEmpty()) {
            String initial = username.substring(0, 1).toUpperCase();
            binding.tvAvatarInitial.setText(initial);
        }
    }

    private void setupThemeSelection() {
        boolean isNight = ThemeManager.isNightCinema(requireContext());
        updateThemeUI(isNight);

        binding.cardNightCinema.setOnClickListener(v -> {
            if (!ThemeManager.isNightCinema(requireContext())) {
                ThemeManager.setNightCinema(requireContext());
                requireActivity().recreate();
            }
        });

        binding.cardSoftCinema.setOnClickListener(v -> {
            if (ThemeManager.isNightCinema(requireContext())) {
                ThemeManager.setSoftCinema(requireContext());
                requireActivity().recreate();
            }
        });
    }

    private void updateThemeUI(boolean isNight) {
        if (isNight) {
            binding.cardNightCinema.setStrokeWidth(
                    (int) (2 * getResources().getDisplayMetrics().density));
            binding.cardNightCinema.setStrokeColor(
                    requireContext().getColor(com.reely.R.color.night_accent_primary));
            binding.ivNightSelected.setVisibility(View.VISIBLE);

            binding.cardSoftCinema.setStrokeWidth(
                    (int) (1 * getResources().getDisplayMetrics().density));
            binding.cardSoftCinema.setStrokeColor(
                    requireContext().getColor(com.reely.R.color.night_stroke));
            binding.ivSoftSelected.setVisibility(View.GONE);
        } else {
            binding.cardSoftCinema.setStrokeWidth(
                    (int) (2 * getResources().getDisplayMetrics().density));
            binding.cardSoftCinema.setStrokeColor(
                    requireContext().getColor(com.reely.R.color.soft_accent_primary));
            binding.ivSoftSelected.setVisibility(View.VISIBLE);

            binding.cardNightCinema.setStrokeWidth(
                    (int) (1 * getResources().getDisplayMetrics().density));
            binding.cardNightCinema.setStrokeColor(
                    requireContext().getColor(com.reely.R.color.night_stroke));
            binding.ivNightSelected.setVisibility(View.GONE);
        }
    }

    private void setupLogout() {
        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(com.reely.R.string.profile_logout))
                .setMessage(getString(com.reely.R.string.profile_logout_confirm))
                .setPositiveButton(getString(com.reely.R.string.btn_yes), (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton(getString(com.reely.R.string.btn_cancel), null)
                .show();
    }

    private void performLogout() {
        sessionManager.logout();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}