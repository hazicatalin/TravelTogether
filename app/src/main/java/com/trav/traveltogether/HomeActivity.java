package com.trav.traveltogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.trav.traveltogether.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity{

    ListView listView;
    ArrayList <Post> posts = new ArrayList <Post>();
    ArrayList<String> keys = new ArrayList<String>();
    int image = R.drawable.im_travel;
    ArrayList <Post> posts2 = new ArrayList <Post>();
    ArrayList <Post> posts1 = new ArrayList <Post>();
    ArrayList<String> keys2 = new ArrayList<String>();
    Toolbar toolbar;
    private DatabaseReference reference;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        reference = FirebaseDatabase.getInstance().getReference().child("Trips");
        readTrips();

        listView = findViewById(R.id.travels_list);
        toolbar = findViewById(R.id.top_bar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(Color.parseColor("#01DFD7"));
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.add_trip:
                        startActivity(new Intent(getApplicationContext(), AddTripActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.chats:
                        startActivity(new Intent(getApplicationContext(), ChatsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.my_profile:
                        startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.my_trips:
                        startActivity(new Intent(getApplicationContext(), MyTripsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(HomeActivity.this, posts.get(position).get_destination(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), PostActivity.class);
                intent.putExtra("post", (Serializable) posts.get(position));
                intent.putExtra("postKey", keys.get(position));
                startActivity(intent);
            }
        });
    }
    public void readTrips(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                keys.clear();
                posts.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    SimpleDateFormat sdformat = new SimpleDateFormat("yyyy/MM/dd");
                    Date d1 = null;
                    Date d2=null;
                    try {
                        d1 = sdformat.parse(post.get_date());
                        d2 = sdformat.parse(sdformat.format(new Date()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.v("dataxx", "The date 2 is: " + sdformat.format(d2));
                    if(d1.compareTo(d2) >= 0) {
                        keys.add(dataSnapshot.getKey());
                        posts.add(post);
                    }
                }
                Collections.reverse(posts);
                Collections.reverse(keys);
                posts2.addAll(posts);
                keys2.addAll(keys);
                showTrips();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showTrips(){
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(HomeActivity.this, R.array.travelTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                if(text.equals("All travel types")){
                    HomeActivity.MyAdapter adapter2 = new MyAdapter(HomeActivity.this, posts);
                    listView.setAdapter(adapter2);
                }
                else{
                    posts2.clear();
                    for (int i = 0; i < posts.size(); i++) {
                        if (posts.get(i).get_travel_type().toLowerCase().equals(text)) {
                            posts2.add(posts.get(i));
                        }
                    }
                    HomeActivity.MyAdapter adapter2 = new MyAdapter(HomeActivity.this, posts2);
                    listView.setAdapter(adapter2);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_navigation_menu, menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Toast.makeText(HomeActivity.this, "Search is expanded", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Toast.makeText(HomeActivity.this, "Search is collapse", Toast.LENGTH_SHORT).show();
                showTrips();
                return true;
            }
        };

        menu.findItem(R.id.search).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView=(SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search by destination...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    posts1.clear();
                    for (int i = 0; i < posts2.size(); i++) {
                        posts1.add(posts2.get(i));
                    }
                    HomeActivity.MyAdapter adapter2 = new MyAdapter(HomeActivity.this, posts2);
                    listView.setAdapter(adapter2);
                }
                else{
                    posts1.clear();
                    for (int i = 0; i < posts2.size(); i++) {
                        if (posts2.get(i).get_destination().toLowerCase().contains(newText.toLowerCase())) {
                            posts1.add(posts2.get(i));
                            Log.v("nt", newText);
                        }
                    }
                    HomeActivity.MyAdapter adapter2 = new MyAdapter(HomeActivity.this, posts1);
                    listView.setAdapter(adapter2);
                }
                return false;
            }
        });
        return true;
    }

    class MyAdapter extends ArrayAdapter<Post>{
        Context context;
        List <Post> rPost;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("add", MODE_PRIVATE);
        preferences.edit().clear().commit();
        Log.i("logx", "onDestroy");
    }

    @Override
    public void onBackPressed() {
        HomeActivity.this.finishAffinity();
        System.exit(0);
    }

}