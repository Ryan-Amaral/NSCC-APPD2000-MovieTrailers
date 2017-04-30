package com.w0279488.ryan.movietrailers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ryan on 2015-12-05.
 */
public class DbAdapter {

    public static final String KEY_ROWID = "id";
    public static final String KEY_MOVIE_TITLE = "movie_title";
    public static final String KEY_MOVIE_DESCRIPTION ="movie_description";
    public static final String KEY_YOUTUBE_VIDEO_ID ="youtube_video_id";
    public static final String KEY_TRAILER_RATING ="trailer_rating";

    public static final String TAG = "DbAdapter";

    private static final String DATABASE_NAME = "MyDB";
    private static final String DATABASE_TABLE = "trailer";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE =
            "create table trailer(id integer primary key autoincrement,"
                    + "movie_title text not null, movie_description text not null,"
                    + "youtube_video_id text not null, trailer_rating real not null);";

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DbAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db)
        {
            try{
                db.execSQL(DATABASE_CREATE);
            }catch(SQLException e){
                e.printStackTrace();
            }
        }//end method onCreate

        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
        {
            Log.w(TAG, "Upgrade database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS trailer");
            onCreate(db);
        }//end method onUpgrade
    }

    //open the database
    public DbAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //close the database
    public void close()
    {
        DBHelper.close();
    }

    //insert a trailers into the database
    public long insertTrailer(String movie_title,String movie_description, String youtube_video_id, float trailer_rating)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_MOVIE_TITLE, movie_title);
        initialValues.put(KEY_MOVIE_DESCRIPTION, movie_description);
        initialValues.put(KEY_YOUTUBE_VIDEO_ID, youtube_video_id);
        initialValues.put(KEY_TRAILER_RATING, trailer_rating);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //delete a particular trailer
    public boolean deletTrailer(long rowId)
    {
        return db.delete(DATABASE_TABLE,KEY_ROWID + "=" + rowId,null) >0;
    }

    //retrieve all the trailers
    public Cursor getAllTrailers()
    {
        return db.query(DATABASE_TABLE,new String[]{KEY_ROWID, KEY_MOVIE_TITLE,
                KEY_MOVIE_DESCRIPTION, KEY_YOUTUBE_VIDEO_ID, KEY_TRAILER_RATING},
                null,null,null,null,null);
    }

    //retrieve a single trailer
    public Cursor getTrailer(long rowId) throws SQLException
    {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_MOVIE_TITLE,
                KEY_MOVIE_DESCRIPTION, KEY_YOUTUBE_VIDEO_ID, KEY_TRAILER_RATING},KEY_ROWID + "=" + rowId,null,null,null,null,null);
        if(mCursor != null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //updates a trailer
    public boolean updateTrailer(long rowId, String movie_title,String movie_description, String youtube_video_id, float trailer_rating)
    {
        ContentValues cval = new ContentValues();
        cval.put(KEY_MOVIE_TITLE, movie_title);
        cval.put(KEY_MOVIE_DESCRIPTION, movie_description);
        cval.put(KEY_YOUTUBE_VIDEO_ID, youtube_video_id);
        cval.put(KEY_TRAILER_RATING, trailer_rating);
        return db.update(DATABASE_TABLE, cval, KEY_ROWID + "=" + rowId,null) >0;
    }
}
