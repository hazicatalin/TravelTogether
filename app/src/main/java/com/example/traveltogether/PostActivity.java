package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    String titleStr, dateStr, userStr, descriptionStr, key, userId, phoneNumber;
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


        if (userStr.equals(userId)) {
            messageBtn.setVisibility(View.INVISIBLE);
            callBtn.setVisibility(View.INVISIBLE);
            joinBtn.setBackgroundColor(Color.RED);
            joinBtn.setTextColor(Color.WHITE);
            joinBtn.setText("Delete");
            reference.child("Trips").child(key).child("participants").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int counter = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        counter = counter + 1;
                    }
                    nr_participants.setText(String.valueOf(counter));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            joinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                    builder.setTitle("Delete Trip");
                    builder.setMessage("Are you sure you want to delete this trip?");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reference.child("Trips").child(key).removeValue();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        } else {
            reference.child("Trips").child(key).child("participants").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int counter = 0, ok = 0;
                    String idKey = new String();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String id = dataSnapshot.getValue(String.class);
                        if (id.equals(userId)) {
                            joinBtn.setBackgroundColor(Color.RED);
                            joinBtn.setTextColor(Color.WHITE);
                            idKey = dataSnapshot.getKey();
                            ok = 1;
                        }
                        counter = counter + 1;
                        nr_participants.setText(String.valueOf(counter));

                        int finalOk = ok;
                        String finalIdKey = idKey;
                        joinBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (finalOk == 0) {
                                    reference.child("Trips").child(key).child("participants").push().setValue(userId);
                                } else {
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
        } catch (IOException ex) {
            Log.e("error", ex.toString());
        }

        FirebaseDatabase.getInstance().getReference("Users").child(userStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    name.setText(userProfile.name);
                    phoneNumber = userProfile.get_phoneNumber();
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

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Log.d("posts", userStr);
                Log.d("posts", userId);
                if(userId.equals(userStr)) {
                    intent = new Intent(getApplicationContext(), MyProfileActivity.class);
                }
                else{
                    intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.putExtra("userId", userStr);
                }
                startActivity(intent);
            }
        });
    }

    void makeCall(){
        if (phoneNumber != null) {
            if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PostActivity.this, new String[] {Manifest.permission.CALL_PHONE}, 1);
            }
            else {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
            }
        }else{
            Toast.makeText(PostActivity.this, "this person doesn't have a number!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makeCall();
            }
            else{
                Toast.makeText(PostActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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