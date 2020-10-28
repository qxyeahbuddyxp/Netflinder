package com.example.netflinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddMoviesActivity extends AppCompatActivity {

    private EditText mTitleField, mGenreField;
    private Button mBack, mAdd;
    private ImageView mMoviePoster;
    private FirebaseAuth mAuth;
    private DatabaseReference mMovieDb;
    private String movieTitle, movieGenre, posterUrl;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movies);

        mTitleField = (EditText) findViewById(R.id.fillMovieTitle);
        mGenreField = (EditText) findViewById(R.id.fillMovieGenre);
        mMoviePoster = (ImageView) findViewById(R.id.moviePoster);
        mBack = (Button) findViewById(R.id.backtomain);
        mAdd = (Button) findViewById(R.id.addMovie);

        mAuth = FirebaseAuth.getInstance();

        mMovieDb = FirebaseDatabase.getInstance().getReference().child("Movies");

        mMoviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieTitle = mTitleField.getText().toString();
                movieGenre = mGenreField.getText().toString();
                int integer = new Random().nextInt(1000);
                final String string = "" + integer + rndChar() + rndChar() + rndChar();

                mMovieDb.child(string).child("MovieTitle").setValue(movieTitle);
                mMovieDb.child(string).child("Genre").setValue(movieGenre);

                if(resultUri != null){
                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("moviePosters").child(string);
                    Bitmap bitmap = null;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = filepath.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            finish();
                        }
                    });
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mMovieDb.child(string).child("MoviePoster").setValue(uri.toString());
                                }
                            });
                        }
                    });
                }
                mTitleField.setText("");
                mGenreField.setText("");
                mMoviePoster.setImageResource(R.mipmap.ic_launcher);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddMoviesActivity.this, MainActivity.class);
                startActivity(intent);
                return;
            }
        });
    }

    private static char rndChar () {
        int rnd = (int) (Math.random() * 52); // or use Random or whatever
        char base = (rnd < 26) ? 'A' : 'a';
        return (char) (base + rnd % 26);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            resultUri = data.getData();
            mMoviePoster.setImageURI(resultUri);
        }
    }
}