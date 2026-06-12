package com.reely.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.reely.R;
import com.reely.activities.LoginActivity;
import com.reely.databinding.FragmentProfileBinding;
import com.reely.repository.MovieRepository;
import com.reely.utils.SessionManager;
import com.reely.utils.ThemeManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private MovieRepository repository;

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
        repository = MovieRepository.getInstance(requireContext());

        setupUserInfo();
        setupStats();
        setupThemeSelection();
        setupLogout();
    }

    private void setupUserInfo() {
        String username = sessionManager.getUsername();
        binding.tvProfileUsername.setText(username);

        if (!username.isEmpty()) {
            binding.tvAvatarInitial.setText(
                    username.substring(0, 1).toUpperCase());
        }
    }

    private void setupStats() {
        // Ambil jumlah asli dari database watchlist
        repository.getAllWatchlistMovies(movies -> {
            if (movies != null) {
                binding.tvWatchlistCount.setText(String.valueOf(movies.size()));
            }
        });
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
            binding.ivNightSelected.setVisibility(View.VISIBLE);
            binding.ivSoftSelected.setVisibility(View.GONE);
        } else {
            binding.ivNightSelected.setVisibility(View.GONE);
            binding.ivSoftSelected.setVisibility(View.VISIBLE);
        }
    }

    private void setupLogout() {
        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.profile_logout))
                .setMessage(getString(R.string.profile_logout_confirm))
                .setPositiveButton(getString(R.string.btn_yes),
                        (dialog, which) -> performLogout())
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void performLogout() {
        sessionManager.logout();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
