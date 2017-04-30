package com.w0279488.ryan.movietrailers;

import android.graphics.Bitmap;

/**
 * Created by w0279488 on 12/4/2015.
 */
public class TrailerPreviewItem {

    private int dbId; // the id of the record in the db
    private String movieName;
    private String movieDescription;
    private Bitmap movieThumbnail;
    private String youtubeId; // the unique id of youtube videos
    private float rating; // out of 5

    public TrailerPreviewItem(int dbId, String movieName,String movieDescription, Bitmap movieThumbnail, String youtubeId, float rating){
        this.dbId = dbId;
        this.movieName = movieName;
        this.movieDescription = movieDescription;
        this.movieThumbnail = movieThumbnail;
        this.youtubeId = youtubeId;
        this.rating = rating;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }

    public Bitmap getMovieThumbnail() {
        return movieThumbnail;
    }

    public void setMovieThumbnail(Bitmap movieThumbnail) {
        this.movieThumbnail = movieThumbnail;
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
