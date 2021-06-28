package com.trav.traveltogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.trav.traveltogether.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsActivity extends AppCompatActivity {

    ArrayList<User> users = new ArrayList<User>();
    String myId;
    ListView listView;
    ArrayList <String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        readChats();
        myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        listView = findViewById(R.id.chats_list);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.chats);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.my_trips:
                        startActivity(new Intent(getApplicationContext(), MyTripsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.add_trip:
                        startActivity(new Intent(getApplicationContext(), AddTripActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.chats:
                        return true;
                    case R.id.my_profile:
                        startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("userId", list.get(position));
                startActivity(intent);
            }
        });
    }

    private void readChats(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if(message.getReceiver().equals(myId) || message.getSender().equals(myId)){
                        String id;
                        if(message.getSender().equals(myId)) {
                            id = message.getReceiver();
                        }
                        else {
                            id = message.getSender();
                        }
                        if(list.contains(id)) {
                            list.remove(id);
                        }
                        list.add(id);
                    }
                }
                Collections.reverse(list);
                MyAdapter adapter = new MyAdapter(ChatsActivity.this, list);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        overridePendingTransition(0,0);
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        List <String> rList;

        MyAdapter (Context c, ArrayList<String> list1){
            super(c, R.layout.chats_item, list1);
            this.context=c;
            this.rList=list1;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View chatsItem = layoutInflater.inflate(R.layout.chats_item, parent, false);
            TextView personName = chatsItem.findViewById(R.id.name);
            CircleImageView profile_image = chatsItem.findViewById(R.id.profile_image);
            String id = rList.get(position);
            Log.d("idasd", id);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    personName.setText(userProfile.get_name());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            try {
                final File file = File.createTempFile(id, "jpg");
                storageReference.child("images/"+id).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        profile_image.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatsActivity.this, "Failed uploading photo!", Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (IOException ex){
                Toast.makeText(ChatsActivity.this, "Exception: "+ex.toString(), Toast.LENGTH_SHORT).show();
            }
            return chatsItem;
        }
    }
}