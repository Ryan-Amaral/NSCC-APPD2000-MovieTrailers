package com.w0279488.ryan.movietrailers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by w0279488 on 12/4/2015.
 */
public class TrailerPreviewListAdapter extends ArrayAdapter {
    private Context context;

    public TrailerPreviewListAdapter(Context context, List items){
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    // holds list items
    private class ViewHolder{
        TextView txtVwMovieName;
        TextView txtVwMovieDescription;
        ImageView imgVwMovieThumbnail;
        RatingBar rtngBrMovieTrailerRating;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder = null;
        TrailerPreviewItem item = (TrailerPreviewItem)getItem(position);
        View viewToUse = null;

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            // use the list item view
            viewToUse = mInflater.inflate(R.layout.trailer_preview_list_item, null);

            // put all controls in the holder
            holder = new ViewHolder();
            holder.txtVwMovieName = (TextView)viewToUse.findViewById(R.id.txtVwMovieName);
            holder.txtVwMovieDescription = (TextView)viewToUse.findViewById(R.id.txtVwMovieDescription);
            holder.imgVwMovieThumbnail = (ImageView)viewToUse.findViewById(R.id.imgVwMovieThumbnail);
            holder.rtngBrMovieTrailerRating = (RatingBar)viewToUse.findViewById(R.id.rtngBrMovieTrailerRating);
            viewToUse.setTag(holder);
        }else{
            viewToUse = convertView;
            holder = (ViewHolder)viewToUse.getTag();
        }

        // assign the values to teh controls
        holder.txtVwMovieName.setText(item.getMovieName());
        holder.txtVwMovieDescription.setText(item.getMovieDescription());
        holder.imgVwMovieThumbnail.setImageBitmap(item.getMovieThumbnail());
        holder.rtngBrMovieTrailerRating.setRating(item.getRating());

        return viewToUse;
    }
}
