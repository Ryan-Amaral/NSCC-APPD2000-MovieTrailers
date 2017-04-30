package com.w0279488.ryan.movietrailers;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TrailerAddActivity extends AppCompatActivity {

    EditText edtTxtYoutubeId;
    EditText edtTxtMovieName;
    EditText edtTxtMovieDescription;
    Button btnTestYoutube;
    Button btnActualAddTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_add);

        edtTxtYoutubeId = (EditText)findViewById(R.id.edtTxtYoutubeId);
        edtTxtMovieName = (EditText)findViewById(R.id.edtTxtMovieName);
        edtTxtMovieDescription = (EditText)findViewById(R.id.edtTxtMovieDescription);
        btnTestYoutube = (Button)findViewById(R.id.btnTestYoutube);
        btnActualAddTrailer = (Button)findViewById(R.id.btnActualAddTrailer);

        btnActualAddTrailer.setEnabled(false); // only enabled when youtube id is good

        edtTxtYoutubeId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // disable the add button
                btnActualAddTrailer.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnTestYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // test if youtube id is valid by trying to load image from it
                try {
                    URL url = new URL("http://img.youtube.com/vi/" +
                            edtTxtYoutubeId.getText().toString() + "/sddefault.jpg");
                    LoadImageFromUrl ldImgFrmUrl = new LoadImageFromUrl(); // async task object
                    ldImgFrmUrl.execute(url);
                } catch (MalformedURLException e) {
                    //
                }
            }
        });

        btnActualAddTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to the main activity and add trailer
                TrailerPreviewFragment frgmnt = MainActivity.getTrailerPreviewFragment();
                frgmnt.insertTrailer(edtTxtMovieName.getText().toString(),
                        edtTxtMovieDescription.getText().toString(),
                        edtTxtYoutubeId.getText().toString());
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trailer_add, menu);
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

    private void imageFailed(){
        // tell user the image failed to load
        Toast.makeText(this, "Youtube ID not working!", Toast.LENGTH_SHORT).show();
    }

    private void imageSucceeded(){
        // tell user the image succeeded loading
        btnActualAddTrailer.setEnabled(true);
        Toast.makeText(this, "Youtube ID success!", Toast.LENGTH_SHORT).show();
    }

    // seperate thread to get data from image, because main thread can't
    private class LoadImageFromUrl extends AsyncTask<URL, Integer, Integer> {

        boolean itWorked = false;

        protected Integer doInBackground(URL... urls)
        {
            for(URL url : urls)
            {
                try {
                    // try this, activate add button if succeed, if fail, tell user
                    BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    itWorked = true;
                }catch(IOException e){
                    itWorked = false;
                }
            }
            return 0;
        }

        protected void onProgressUpdate(Integer i)
        {

        }

        protected void onPostExecute(Integer thing)
        {
            if(itWorked){
                imageSucceeded();
            }else{
                imageFailed();
            }
        }
    }
}
