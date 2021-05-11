package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    TextView title, date, name, description;
    String titleStr, dateStr, userStr, descriptionStr;
    ArrayList<String> participantsList;
    Post post;
    CircleImageView profile;
    FloatingActionButton messageBtn, callBtn;
    Button joinBtn;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        title = findViewById(R.id.titlu);
        date = findViewById(R.id.data);
        name = findViewById(R.id.name);
        description = findViewById(R.id.descriere);
        user = FirebaseAuth.getInstance().getCurrentUser();

        post = (Post) getIntent().getSerializableExtra("post");
        titleStr = post.get_destination();
        dateStr = post.get_date();
        userStr = post.get_creator_id();
        descriptionStr = post.get_description();
        participantsList = post.get_participants();
        messageBtn = findViewById(R.id.message);
        joinBtn = findViewById(R.id.join);

        title.setText(titleStr);
        date.setText(dateStr);
        description.setText(descriptionStr);
        profile = findViewById(R.id.profile_image);

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

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinBtn.setBackgroundColor(Color.RED);
                joinBtn.setTextColor(Color.WHITE);
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
}