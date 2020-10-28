package com.example.netflinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ConnectionActivity extends AppCompatActivity {

    private DatabaseReference usersDb;
    private String mConNameStr, mConCodeStr;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private String currentUId;
    private Boolean hasMatch = false;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        setTitle("Get together");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUId = mAuth.getCurrentUser().getUid();
        final EditText mConName = (EditText) findViewById(R.id.cname);
        final EditText mConCode = (EditText) findViewById(R.id.cid);

        Button mConnectUser = (Button) findViewById(R.id.connectuser);
        Button mCancel = (Button) findViewById(R.id.backtomain);
        
        TextView textView = (TextView) findViewById(R.id.ownid);
        textView.setText("Your Connection Code:\n" + currentUId.substring(0,4));

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mConnectUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 0;
                mConNameStr = (String) mConName.getText().toString().toLowerCase();
                mConCodeStr = (String) mConCode.getText().toString().toLowerCase();
                usersDb.child(currentUId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.getKey().equals("Matched user")){
                            hasMatch = true;
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

                usersDb.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        DatabaseReference myUserDb = usersDb.child(currentUId).child("Matched user");
                        if (snapshot.exists()
                                && mConCodeStr.equals(Objects.requireNonNull(snapshot.getKey()).substring(0, 4).toLowerCase())
                                && mConNameStr.equals(((String) Objects.requireNonNull(snapshot.child("Name").getValue())).toLowerCase())){
                            if (snapshot.child("Matched user").getValue() != null
                                    || hasMatch) {
                                Toast.makeText(ConnectionActivity.this, "One of you already has a connection", Toast.LENGTH_SHORT).show();
                            }
                            if (snapshot.child("Matched user").getValue() == null
                                    && !hasMatch) {
                                DatabaseReference otherUserDb = usersDb.child(snapshot.getKey()).child("Matched user");
                                otherUserDb.setValue(currentUId);
                                myUserDb.setValue(snapshot.getKey());
                                i = 1;
                            }
                        }
//                        if (snapshot.exists()
//                                && !mConCodeStr.equals(Objects.requireNonNull(snapshot.getKey()).substring(0, 4).toLowerCase())
//                                && !mConNameStr.equals(((String) Objects.requireNonNull(snapshot.child("Name").getValue())).toLowerCase())){
//                            Toast.makeText(ConnectionActivity.this, "User not found", Toast.LENGTH_SHORT).show();
//                        }
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
                if(i == 0) {
                    Toast.makeText(ConnectionActivity.this, "Sorry, haven't found your soulmate :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}