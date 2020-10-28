package com.example.netflinder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders> {
    private List<MatchesObject> matchesObjectList;
    private Context context;

    public MatchesAdapter(List<MatchesObject> matchesObjectList, Context context){
        this.matchesObjectList = matchesObjectList;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders((layoutView));

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolders holder, int position) {
        holder.mMatchedTitle.setText(matchesObjectList.get(position).getMovieTitle());
        holder.mMatchedGenre.setText(matchesObjectList.get(position).getMovieGenre());
        if(!matchesObjectList.get(position).getMovieURL().equals("default")){
            Glide.with(context).load(matchesObjectList.get(position).getMovieURL()).into(holder.mMatchedPoster);
         }
    }

    @Override
    public int getItemCount() {
        return this.matchesObjectList.size();
    }
}
