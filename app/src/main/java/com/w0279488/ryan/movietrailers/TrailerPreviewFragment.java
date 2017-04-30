package com.w0279488.ryan.movietrailers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.w0279488.ryan.movietrailers.dummy.DummyContent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TrailerPreviewFragment extends Fragment implements AbsListView.OnItemClickListener {

    private List<TrailerPreviewItem> mTrailerPreviewList;
    private List<TrailerPreviewItemMaker> trailerPreviewMakerList; // list of object to pass to async task
    LoadImageFromUrl ldImgFrmUrl; // the async task object

    private int maxItem; // the amount of rows retrieved from database
    private int curItemIndex = 0; // the current index of the items from the database
    private int curTrailerId; // the id of the record in the database of this item

    private int curSelectedIndex; // the trailer that was just viewed

    private DbAdapter database; // the database object

    Cursor cursor; // object to be used when receiving data from db

    // removes the currently being viewed trailer, called from trailer view activity
    public void removeTrailer(){
        mTrailerPreviewList.remove(curSelectedIndex);
        ((BaseAdapter)mAdapter).notifyDataSetChanged(); // update the thing
        // if no items left show empty text view
        if(mTrailerPreviewList.size() == 0){
            mListView.setVisibility(View.GONE);
            ((TextView)getActivity().findViewById(R.id.txtVwEmpty)).setVisibility(View.VISIBLE);
        }
        // remove record from database
        database.open();

        String msg; // the message to pop from the toaster

        if(database.deletTrailer(curTrailerId)){
            msg = "Trailer Removed!";
        }else{
            msg = "Unable to Remove Trailer!";
        }

        database.close();

        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    // updates the rating of the current trailer
    public void updateRating(float newRating){
        mTrailerPreviewList.get(curSelectedIndex).setRating(newRating);
        ((BaseAdapter)mAdapter).notifyDataSetChanged(); // update the thing
        // update rating in database
        database.open();

        String msg; // the message to pop from the toaster

        // get this db object to not replace data
        cursor = database.getTrailer(curTrailerId);

        if(database.updateTrailer(curTrailerId, cursor.getString(1), cursor.getString(2), cursor.getString(3), newRating)){
            msg = "Rating Updated!";
        }else{
            msg = "Unable to Update Rating now!";
        }

        database.close();

        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    // adds a trailer to the list
    public void insertTrailer(String name, String description, String youtubeId){
        database.open();

        String msg;

        if(database.insertTrailer(name, description, youtubeId, -1f) >= 0){
            msg = "Trailer successfully added!";
        }
        else{
            msg = "Trailer could not be added!";
        }

        database.close();

        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

        // restart activity
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /// Important function needed for loading images with the async task
    public void updateAdapterList(TrailerPreviewItem item)
    {
        mTrailerPreviewList.add(item); // add the item from async task to list
        ((BaseAdapter)mAdapter).notifyDataSetChanged(); // update the thing

        curItemIndex++;
        if(curItemIndex < maxItem){
            ldImgFrmUrl = new LoadImageFromUrl();
            ldImgFrmUrl.execute(trailerPreviewMakerList.get(curItemIndex));
        }
    }

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    public static TrailerPreviewFragment newInstance() {
        TrailerPreviewFragment fragment = new TrailerPreviewFragment();
        //Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrailerPreviewFragment() {
    }
//
//    // copy the db to the file
//    public void CopyDB(InputStream inputStream,OutputStream outputStream)
//            throws IOException{
//        //copy 1k bytes at a time
//        byte[] buffer = new byte[1024];
//        int length;
//        while((length = inputStream.read(buffer)) > 0)
//        {
//            outputStream.write(buffer,0,length);
//        }
//        inputStream.close();
//        outputStream.close();
//
//    }//end method CopyDB

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        curItemIndex = 0;
        // create all trailer preview object
        mTrailerPreviewList = new ArrayList<>(); // used as reference in ui
        trailerPreviewMakerList = new ArrayList<>(); // temp stores maker objects

        URL url;
        ldImgFrmUrl = new LoadImageFromUrl(); // async task object

        // create the database object
        database = new DbAdapter(this.getActivity());
        database.open();

        cursor = database.getAllTrailers();

        // insert initial records into database, if the size is zero
        if(!cursor.moveToFirst()){
            System.out.println("INituial films!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            database.insertTrailer("Star Wars: The Force Awakens",
                    "A continuation of the saga created by George Lucas and set thirty years after Star Wars: Episode VI - Return of the Jedi (1983).",
                    "sGbxmsDFVnE",
                    -1f);
            database.insertTrailer("The Lord of the Rings: The Fellowship of the Ring",
                    "A meek Hobbit and eight companions set out on a journey to destroy the One Ring and the Dark Lord Sauron.",
                    "V75dMMIW2B4",
                    -1f);
            database.insertTrailer("The Brave Little Toaster",
                    "A toaster, a blanket, a lamp, a radio, and a vacuum cleaner journey to the city to find their master after being abandoned in their cabin in the woods.",
                    "rQ763bgw8Xc",
                    -1f);
            database.insertTrailer("Austin Powers: International Man of Mystery",
                    "A 1960s hipster secret agent is brought out of cryofreeze to oppose his greatest enemy in the 1990s, where his social attitudes are glaringly out of place.",
                    "Oze1bn4_pbk",
                    -1f);
            database.insertTrailer("Spaceballs",
                    "Planet Spaceballs' President Skroob sends Lord Dark Helmet to steal planet Druidia's abundant supply of air to replenish their own, and only Lone Starr can stop them.",
                    "uWVSVgU-I0s",
                    -1f);
        }
        database.close();

        // create items in the list from what is retrieved from the database
        database.open();

        cursor = database.getAllTrailers();
        if(cursor.moveToFirst()){
            do{
                try{
                    url = new URL("http://img.youtube.com/vi/" + cursor.getString(3) + "/sddefault.jpg");
                    trailerPreviewMakerList.add(new TrailerPreviewItemMaker(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            url,
                            cursor.getString(3),
                            cursor.getFloat(4)));
                }catch(MalformedURLException e){
                    e.printStackTrace();
                }
            }while(cursor.moveToNext());
        }

        // add each item to list in async task
        maxItem = trailerPreviewMakerList.size();
        ldImgFrmUrl.execute(trailerPreviewMakerList.get(0));

        database.close(); // close the db

        mAdapter = new TrailerPreviewListAdapter(getActivity(), mTrailerPreviewList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trailerpreview, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {

            curSelectedIndex = position;
            TrailerPreviewItem item = mTrailerPreviewList.get(position); // get the item

            curTrailerId = item.getDbId();

            Intent i = new Intent(this.getActivity(), TrailerViewActivity.class); //create intent object
            Bundle extras = new Bundle(); //create bundle object
            extras.putString(Config.MOVIE_TITLE_KEY, item.getMovieName()); //fill bundle
            extras.putString(Config.MOVIE_DESCRIPTION_KEY, item.getMovieDescription()); //fill bundle
            extras.putString(Config.VIDEO_ID_KEY, item.getYoutubeId()); //fill bundle
            extras.putFloat(Config.TRAILER_RATING_KEY, item.getRating()); //fill bundle
            i.putExtras(extras);
            startActivity(i);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(R.string.str_no_trailers);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    // seperate thread to get data from image, because main thread can't
    private class LoadImageFromUrl extends AsyncTask<TrailerPreviewItemMaker, Integer, Integer> {

        TrailerPreviewItem item; // created an item to be accessed in all methods

        protected Integer doInBackground(TrailerPreviewItemMaker... makers)
        {
            for(TrailerPreviewItemMaker maker : makers)
            {
                try {
                    // create the item from the maker
                    item = new TrailerPreviewItem(maker.getDbId(), maker.getName(), maker.getDescription(),
                            BitmapFactory.decodeStream(maker.getUrl().openConnection().getInputStream()),
                            maker.getYoutubeId(), maker.getRating());
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            return 0;
        }

        protected void onProgressUpdate(Integer i)
        {

        }

        protected void onPostExecute(Integer thing)
        {
            updateAdapterList(item); // call the update method to update the display after adding item
        }
    }

    // class that is used to hold information to be passed to async task to create TrailerPreviewItems
    private class TrailerPreviewItemMaker{

        private int dbId; // the id of the record in the db
        private String name;
        private String description;
        private URL url;
        private String youtubeId; // the unique id of youtube videos
        private float rating; // out of 5

        public TrailerPreviewItemMaker(int dbId, String name, String description, URL url, String youtubeId, float rating){
            this.dbId = dbId;
            this.name = name;
            this.description = description;
            this.url = url;
            this.youtubeId = youtubeId;
            this.rating = rating;
        }

        public int getDbId() {
            return dbId;
        }

        public void setDbId(int dbId) {
            this.dbId = dbId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public URL getUrl() {
            return url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }

        public String getYoutubeId() {
            return youtubeId;
        }

        public void setYoutubeId(String youtubeId) {
            this.youtubeId = youtubeId;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }
    }

}
