package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    TextView title, date, name, description, nr_participants;
    String titleStr, dateStr, userStr, descriptionStr, key, userId;
    ArrayList<String> participantsList;
    Post post;
    CircleImageView profile;
    FloatingActionButton messageBtn, callBtn;
    Button joinBtn;

    private FirebaseUser user;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        title = findViewById(R.id.titlu);
        date = findViewById(R.id.data);
        name = findViewById(R.id.name);
        description = findViewById(R.id.descriere);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        userId = user.getUid();
        post = (Post) getIntent().getSerializableExtra("post");
        titleStr = post.get_destination();
        dateStr = post.get_date();
        userStr = post.get_creator_id();
        descriptionStr = post.get_description();
        messageBtn = findViewById(R.id.message);
        joinBtn = findViewById(R.id.join);
        callBtn = findViewById(R.id.call);
        nr_participants = findViewById(R.id.nr_participanti);

        key = getIntent().getStringExtra("postKey");

        title.setText(titleStr);
        date.setText(dateStr);
        description.setText(descriptionStr);
        profile = findViewById(R.id.profile_image);



        if(userStr.equals(userId)){
            joinBtn.setVisibility(View.INVISIBLE);
            messageBtn.setVisibility(View.INVISIBLE);
            callBtn.setVisibility(View.INVISIBLE);
            reference.child("Trips").child(key).child("participants").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int counter=0;
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        counter=counter+1;
                    }
                    nr_participants.setText(String.valueOf(counter));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            reference.child("Trips").child(key).child("participants").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int counter=0, ok=0;
                    String idKey = new String();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String id = dataSnapshot.getValue(String.class);
                        if(ok==0 && id==userId){
                            joinBtn.setBackgroundColor(Color.RED);
                            joinBtn.setTextColor(Color.WHITE);
                            idKey = dataSnapshot.getKey();
                            ok=1;
                        }
                        counter=counter+1;
                        nr_participants.setText(String.valueOf(counter));

                        int finalOk = ok;
                        String finalIdKey = idKey;
                        joinBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(finalOk == 0) {
                                    reference.child("Trips").child(key).child("participants").push().setValue(userId);
                                }
                                else {
                                    reference.child("Trips").child(key).child("participants").child(finalIdKey).removeValue();
                                    joinBtn.setBackgroundColor(Color.parseColor("#01DFD7"));
                                    joinBtn.setTextColor(Color.BLACK);
                                }
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        try {
            final File file = File.createTempFile(userStr, "jpg");
            storageReference.child("images/" + userStr).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    profile.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }catch (IOException ex){
            Log.e("error", ex.toString());
        }

        FirebaseDatabase.getInstance().getReference("Users").child(userStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile!=null){
                    name.setText(userProfile.name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("userId", userStr);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("userId", userStr);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("add", MODE_PRIVATE);
        preferences.edit().clear().commit();
        Log.i("logx", "onDestroy");
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}