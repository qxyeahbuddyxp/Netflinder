package com.example.netflinder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchedTitle, mMatchedGenre;
    public ImageView mMatchedPoster;
    public MatchesViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchedTitle = (TextView) itemView.findViewById(R.id.matchtitle);
        mMatchedGenre = (TextView) itemView.findViewById(R.id.matchgenre);
        mMatchedPoster = (ImageView) itemView.findViewById(R.id.matchposter);
    }

    public void onClick(View view){

    }
}
