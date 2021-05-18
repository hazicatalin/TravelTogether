package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    TextView name, description;
    String userStr, phoneNumber;
    CircleImageView profile_image;
    ListView listView;
    ArrayList<Post> posts = new ArrayList <Post>();
    ArrayList<String> keys = new ArrayList<String>();
    FloatingActionButton messageBtn, callBtn;
    int image = R.drawable.im_travel;
    private StorageReference storageReference;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        reference = FirebaseDatabase.getInstance().getReference().child("Trips");
        readTrips();

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        userStr = getIntent().getStringExtra("userId");
        profile_image = findViewById(R.id.profile_image);
        listView = findViewById(R.id.travels_list);
        messageBtn = findViewById(R.id.message);
        callBtn = findViewById(R.id.call);

        storageReference = FirebaseStorage.getInstance().getReference();

        getImage();

        FirebaseDatabase.getInstance().getReference("Users").child(userStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile!=null){
                    name.setText(userProfile.get_name());
                    description.setText(userProfile.get_description());
                    phoneNumber = userProfile.get_phoneNumber();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ProfileActivity.this, posts.get(position).get_destination(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), PostActivity.class);
                intent.putExtra("post", (Serializable) posts.get(position));
                intent.putExtra("postKey", keys.get(position));
                startActivity(intent);
                Log.v("itemclick: ", posts.get(position).get_destination());
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
    }

    void makeCall(){
        if (phoneNumber != null) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[] {Manifest.permission.CALL_PHONE}, 1);
            }
            else {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
            }
        }else{
            Toast.makeText(ProfileActivity.this, "this person doesn't have a number!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makeCall();
            }
            else{
                Toast.makeText(ProfileActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void readTrips(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(userStr.equals(post.get_creator_id())) {
                        keys.add(dataSnapshot.getKey());
                        posts.add(post);
                    }
                }
                Collections.reverse(posts);
                Collections.reverse(keys);
                ProfileActivity.MyAdapter adapter = new MyAdapter(ProfileActivity.this, posts);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getImage(){
        try {
            final File file = File.createTempFile(userStr, "jpg");
            storageReference.child("images/" + userStr).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    profile_image.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Failed uploading photo!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (IOException ex){

        }
    }


    class MyAdapter extends ArrayAdapter<Post> {
        Context context;
        List<Post> rPost;
        int rImage;

        MyAdapter (Context c, ArrayList<Post> post){
            super(c, R.layout.travel, R.id.travel_title, post);
            this.context=c;
            this.rPost=post;
            this.rImage=image;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View travel = layoutInflater.inflate(R.layout.travel, parent, false);
            TextView mytitle = travel.findViewById(R.id.travel_title);
            TextView mydate = travel.findViewById(R.id.travel_date);
            TextView myway = travel.findViewById(R.id.travel_by);
            TextView mystart = travel.findViewById(R.id.travel_start);
            TextView myowner = travel.findViewById(R.id.travel_owner);
            ImageView myimage = travel.findViewById(R.id.image);

            reference = FirebaseDatabase.getInstance().getReference("Users");

            reference.child(rPost.get(position).get_creator_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if(userProfile!=null){
                        myowner.setText(userProfile.name);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            if(rPost.get(position).get_destination().length()>20){
                mytitle.setText(rPost.get(position).get_destination().substring(0,20)+"...");
            }
            else {
                mytitle.setText(rPost.get(position).get_destination());
            }
            mydate.setText(rPost.get(position).get_date());
            myway.setText(rPost.get(position).get_travel_type());
            if(rPost.get(position).get_start_location().length()>15){
                mystart.setText(rPost.get(position).get_start_location().substring(0,15)+"...");
            }
            else {
                mystart.setText(rPost.get(position).get_start_location());
            }
            myimage.setImageResource(rImage);
            return travel;
        }
    }
}