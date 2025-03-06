package com.example.androidcicd;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.androidcicd.movie.Movie;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @BeforeClass
    public static void setup(){
        // Specific address for emulated device to access our localHost
        String androidLocalhost = "10.0.2.2";
        int portNumber = 8080;
        FirebaseFirestore.getInstance().useEmulator(androidLocalhost, portNumber);

    }

    @Before
    public void seedDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference moviesRef = db.collection("movies");
        Movie[] movies = {
                new Movie("Oppenheimer", "Thriller/Historical Drama", 2023),
                new Movie("Barbie", "Comedy/Fantasy", 2023)
        };

        for (Movie movie : movies) {
            DocumentReference docRef = moviesRef.document();
            movie.setId(docRef.getId());
            docRef.set(movie);
        }
    }

    @Test
    public void addMovieShouldAddValidMovieToMovieList() {
        // Click on button to open addMovie dialog
        onView(withId(R.id.buttonAddMovie)).perform(click());

        // Input Movie Details
        onView(withId(R.id.edit_title)).perform(ViewActions.typeText("Interstellar"));
        onView(withId(R.id.edit_genre)).perform(ViewActions.typeText("Science Fiction"));
        onView(withId(R.id.edit_year)).perform(ViewActions.typeText("2014"));

        // Submit Form
        onView(withId(android.R.id.button1)).perform(click());

        // Check that our movie list has our new movie
        onView(withText("Interstellar")).check(matches(isDisplayed()));
    }

    @Test
    public void addMovieShouldShowErrorForInvalidMovieName() {
        // Click on button to open addMovie dialog
        onView(withId(R.id.buttonAddMovie)).perform(click());

        // Add movie details, but no title
        onView(withId(R.id.edit_genre)).perform(ViewActions.typeText("Science Fiction"));
        onView(withId(R.id.edit_year)).perform(ViewActions.typeText("2014"));

        // Submit Form
        onView(withId(android.R.id.button1)).perform(click());

        // Check that an error is shown to the user
        onView(withId(R.id.edit_title)).check(matches(hasErrorText("Movie name cannot be empty!")));
    }

    @Test
    public void appShouldDisplayExistingMoviesOnLaunch() throws InterruptedException {
        Thread.sleep(3000);
        // Check that the initial data is loaded
        onView(withText("Oppenheimer")).check(matches(isDisplayed()));
        onView(withText("Barbie")).check(matches(isDisplayed()));

        // Click on Oppenheimer
        onView(withText("Oppenheimer")).perform(click());

        // Check that the movie details are displayed correctly
        onView(withId(R.id.edit_title)).check(matches(withText("Oppenheimer")));
        onView(withId(R.id.edit_genre)).check(matches(withText("Thriller/Historical Drama")));
        onView(withId(R.id.edit_year)).check(matches(withText("2023")));
    }

    @Test
    public void appShouldDeleteMovie() throws InterruptedException {
        Thread.sleep(2000);
        ViewInteraction view = onView(withText("Oppenheimer"));
        view.perform(longClick());
        onView(withText("Delete")).perform(click());

        view.check(doesNotExist());
    }

    @After
    public void tearDown() {
        String projectId = "cmput301l8w25";
        String database = "(default)";
        String encodedDatabase = URLEncoder.encode(database, StandardCharsets.UTF_8);
        URL url = null;
        try {
            url = new URL("http://10.0.2.2:8080/emulator/v1/projects/" + projectId + "/databases/" + encodedDatabase + "/documents");
        } catch (MalformedURLException exception) {
            Log.e("URLError", Objects.requireNonNull(exception.getMessage()));
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            int response = urlConnection.getResponseCode();
            Log.i("ResponseCode", "Response Code: " + response + "(" + urlConnection.getResponseMessage() + ")");
        } catch (IOException exception) {
            Log.e("IOError", Objects.requireNonNull(exception.getMessage()));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
