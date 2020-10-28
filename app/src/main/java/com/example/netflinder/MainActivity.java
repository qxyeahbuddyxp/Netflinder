package com.example.netflinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //Declare all the variables
    private cards cards_data[];
    private arrayAdapter arrayAdapter;
    private int i;
    private FirebaseAuth mAuth;
    private String currentUId, currentMail, matchedUser;
    private TextView mEmpty, mLikeDislike;
    private DatabaseReference usersDb, movieDb;
    private Button mConnect, mAddMovies, mMatches;

    List<cards> rowItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declare all textviews and buttons
        mConnect = (Button) findViewById(R.id.connect);
        mMatches = (Button) findViewById(R.id.matches);
        mEmpty = (TextView) findViewById(R.id.empty);
        mAddMovies = (Button) findViewById(R.id.addMovies);
        mLikeDislike = (TextView) findViewById(R.id.likedislike);

        //set current User ID and e-mail
        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();
        currentMail = mAuth.getCurrentUser().getEmail();
        //set usersDatabase and movieDatabase for easier access
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        movieDb = FirebaseDatabase.getInstance().getReference().child("Movies");

        //When a button is clicked, it starts that new activity
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ConnectionActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mAddMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddMoviesActivity.class);
                startActivity(intent);
                return;
            }
        });

        //if the admin is logged in, the button to add movies is added
        if(!currentMail.equals("ricardomeijer2@gmail.com")){
            mAddMovies.setVisibility(View.INVISIBLE);
        }

        //make an Adapter and Arraylist to find and show the movie posters
        rowItems = new ArrayList<cards>();
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        //when there is no movie poster to show, the like/dislike icons disappears and the
        // textview appears
        if (rowItems.isEmpty()){
            mEmpty.setVisibility(View.VISIBLE);
            mLikeDislike.setVisibility(View.INVISIBLE);
        }

        getMovies();

        //Use of the swipe option
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            //Remove the first movie poster when something is rated
            public void removeFirstObjectInAdapter() {
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
                if (rowItems.isEmpty()){
                    mEmpty.setVisibility(View.VISIBLE);
                    mLikeDislike.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            //When the poster is swiped left, add the movieID to Unliked
            public void onLeftCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String movieId = obj.getMovieId();
                movieDb.child(movieId).child("Unliked").child(currentUId).setValue(true);
            }
            @Override
            //When the poster is swiped right, add the movieID to Liked
            public void onRightCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                final String movieId = obj.getMovieId();
                movieDb.child(movieId).child("Liked").child(currentUId).setValue(true);
                //Declare who the matched user is
                usersDb.child(currentUId).child("Matched user").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        matchedUser = snapshot.getValue(String.class);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                //Go to all users who Liked this movie so we can check if the matched user also liked it
                //When this is true, the movie is set as Matched movie
                movieDb.child(movieId).child("Liked").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(Objects.equals(snapshot.getKey(), matchedUser)){
                            usersDb.child(currentUId).child("Matched movies").child(movieId).setValue(true);
                            usersDb.child(matchedUser).child("Matched movies").child(movieId).setValue(true);
                            Toast.makeText(MainActivity.this, "You got a movie to watch!", Toast.LENGTH_SHORT).show();}
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }
            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });

    }

    public void getMovies(){
        //Get every movie that is not rated yet and get its name and poster
        movieDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists() && !snapshot.child("Liked").hasChild(currentUId) && !snapshot.child("Unliked").hasChild(currentUId)){
                    mEmpty.setVisibility(View.INVISIBLE);
                    mLikeDislike.setVisibility(View.VISIBLE);
                    String movieId = snapshot.getKey();
                    String movieTitle = Objects.requireNonNull(snapshot.child("MovieTitle").getValue()).toString();
                    String movieGenre = snapshot.child("Genre").getValue(String.class);
                    String moviePoster = (String) snapshot.child("MoviePoster").getValue();
                    cards movie = new cards(movieId, movieTitle, moviePoster, movieGenre);//, movieGenre
                    rowItems.add(movie);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}