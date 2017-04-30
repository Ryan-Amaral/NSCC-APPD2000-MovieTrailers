package com.w0279488.ryan.movietrailers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class TrailerViewActivity extends AppCompatActivity {

    String curVideoId = "VIs00QjiJZQ"; // the id of the video to play when player is initialized

    // the controls to be filled with content
    TextView txtVwMovieName;
    RatingBar rtngBrMovieTrailerRating;
    TextView txtVwMoviDescription;
    Button btnBack;
    Button btnDeleteTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_view);

        // set the controls
        txtVwMovieName = (TextView)findViewById(R.id.txtVwMovieName);
        rtngBrMovieTrailerRating = (RatingBar)findViewById(R.id.rtngBrMovieTrailerRating);
        txtVwMoviDescription = (TextView)findViewById(R.id.txtVwMovieDescription);
        btnBack = (Button)findViewById(R.id.btnBack);
        btnDeleteTrailer = (Button)findViewById(R.id.btnDeleteTrailer);

        Bundle extras=getIntent().getExtras();
        if(extras != null)//if bundle has content
        {
            curVideoId = extras.getString(Config.VIDEO_ID_KEY); // set the video id
            txtVwMovieName.setText(extras.getString(Config.MOVIE_TITLE_KEY));
            rtngBrMovieTrailerRating.setRating(extras.getFloat(Config.TRAILER_RATING_KEY));
            txtVwMoviDescription.setText(extras.getString(Config.MOVIE_DESCRIPTION_KEY));
        }

        YouTubePlayerSupportFragment myYoutubeFragment = (YouTubePlayerSupportFragment)getSupportFragmentManager().findFragmentById(R.id.my_youtube_fragment);
        myYoutubeFragment.initialize(Config.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    player.loadVideo(curVideoId);
                    player.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        });

        rtngBrMovieTrailerRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // update the rating in the current view in other fragment
                TrailerPreviewFragment frgmnt = MainActivity.getTrailerPreviewFragment();
                frgmnt.updateRating(rating);
            }
        });

        // set the back button to go remove this activity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // finishes this activity
            }
        });

        // set the remove button to remove the trailer from the last and then go back to the list
        btnDeleteTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrailerPreviewFragment frgmnt = MainActivity.getTrailerPreviewFragment();
                frgmnt.removeTrailer();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trailer_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
