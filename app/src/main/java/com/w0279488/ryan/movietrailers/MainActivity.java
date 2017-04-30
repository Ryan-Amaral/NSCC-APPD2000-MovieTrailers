package com.w0279488.ryan.movietrailers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class MainActivity extends AppCompatActivity implements TrailerPreviewFragment.OnFragmentInteractionListener {

    public static TrailerPreviewFragment frgmnt;

    private Button btnAddTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frgmnt = (TrailerPreviewFragment)getFragmentManager().findFragmentById(R.id.frgmntTrailerPreviewList);

        btnAddTrailer = (Button)findViewById(R.id.btnAddTrailer);

        btnAddTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch add trailer activity
                loadTrailerCreate();
            }
        });
    }

    private void loadTrailerCreate(){
        // launch add trailer activity
        Intent i = new Intent(this, TrailerAddActivity.class); //create intent object
        startActivity(i);
    }

    public static TrailerPreviewFragment getTrailerPreviewFragment(){
        return frgmnt; // return fragment so other activity can call it
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onFragmentInteraction(String s){}
}
