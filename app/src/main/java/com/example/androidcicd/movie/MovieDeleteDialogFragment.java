package com.example.androidcicd.movie;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class MovieDeleteDialogFragment extends DialogFragment {
    private MovieProvider movieProvider;

    public static MovieDeleteDialogFragment newInstance(Movie movie){
        Bundle args = new Bundle();
        args.putSerializable("Movie", movie);

        MovieDeleteDialogFragment fragment = new MovieDeleteDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        Movie movie;
        movieProvider = MovieProvider.getInstance(FirebaseFirestore.getInstance());

        // Handle data validation
        if (bundle != null)
            movie = (Movie) bundle.getSerializable("Movie");
        else
            throw new RuntimeException("Bundle was not present!");
        if (movie == null)
            throw new RuntimeException("Movie was not in bundle!");

        return new AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to delete the movie " + movie.getTitle())
                .setPositiveButton("Delete", (dialog, which) -> {
                    movieProvider.deleteMovie(movie);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {})
                .create();
    }
}
