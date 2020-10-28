package com.example.netflinder;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<cards> {
    Context context;

    public arrayAdapter(Context context, int resourceId, List<cards> items){
        super(context, resourceId, items);
    }
    public View getView(int position, View convertView, ViewGroup parent){
        cards card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.movieTitle);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView genre = (TextView) convertView.findViewById(R.id.movieGenre);

        //image.setImageURI(null); This resets the URI, not needed for now

        Glide.with(getContext()).load(card_item.getMoviePoster()).into(image);
        name.setText(card_item.getTitle());
        genre.setText(card_item.getMovieGenre());



        return convertView;

    }
}
