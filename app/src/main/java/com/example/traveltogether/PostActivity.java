package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {
    TextView title, date, name, description;
    String titleStr, dateStr, userStr, descriptionStr;
    CircleImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        title = findViewById(R.id.titlu);
        date = findViewById(R.id.data);
        name = findViewById(R.id.name);
        description = findViewById(R.id.descriere);

        titleStr = getIntent().getStringExtra("title");
        dateStr = getIntent().getStringExtra("data");
        userStr = getIntent().getStringExtra("userId");
        descriptionStr = getIntent().getStringExtra("description");

        title.setText(titleStr);
        date.setText(dateStr);
        description.setText(descriptionStr);
        profile = findViewById(R.id.profile_image);

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

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("user", userStr);
                startActivity(intent);
            }
        });
    }
}