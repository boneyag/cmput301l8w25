package com.example.androidcicd.movie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.androidcicd.R;

import java.util.ArrayList;

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;
    private Context context;

    public MovieArrayAdapter(Context context, ArrayList<Movie> movies){
        super(context, 0, movies);
        this.movies = movies;
        this.context = context;
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.layout_movie, parent, false);
        }

        Movie movie = movies.get(position);
        TextView movieName = view.findViewById(R.id.textMovieName);
        TextView movieGenre = view.findViewById(R.id.textMovieGenre);
        TextView movieYear = view.findViewById(R.id.textMovieYear);

        movieName.setText(movie.getTitle());
        movieGenre.setText(movie.getGenre());
        movieYear.setText(movie.getYear() + "");

        return view;
    }
}
