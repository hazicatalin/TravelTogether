package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ListView listView;
    ArrayList <Post> posts = new ArrayList <Post>();
    //String titles[] = {"transfagarasan", "mare", "Brasov", "Delta Dunarii", "Straja", "Bucuresti"};
    //String dates[] = {"15.iunie.2021", "22.august.2021", "1.mai.2021", "19.septembrie.2021", "5.ianuarie.2022", "15.mai.2021"};
    int image = R.drawable.im_travel;
    ArrayList <Post> posts2 = new ArrayList <Post>();
    Toolbar toolbar;
    private FirebaseDatabase database;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        readTrips();
        listView = findViewById(R.id.traels_list);
        toolbar = findViewById(R.id.top_bar);
        toolbar.setTitle("");
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
                    case R.id.favorite_trips:
                        startActivity(new Intent(getApplicationContext(), FavoriteTripsActivity.class));
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
                intent.putExtra("title",posts.get(position).get_destination());
                intent.putExtra("data", posts.get(position).get_date());
                intent.putExtra("userId", posts.get(position).get_creator_id());
                intent.putExtra("description", posts.get(position).get_description());
                startActivity(intent);
                Log.v("itemclick: ", posts.get(position).get_destination());
            }
        });
    }

    public void readTrips(){
        reference = FirebaseDatabase.getInstance().getReference().child("Trips");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    posts.add(post);
                    Log.d("array", post.get_destination());
                    Log.d("array", String.valueOf(posts.size()));
                }
                Log.d("size", String.valueOf(posts.size()));
                MyAdapter adapter = new MyAdapter(HomeActivity.this, posts);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                MyAdapter adapter2 = new MyAdapter(HomeActivity.this, posts2);
                listView.setAdapter(adapter2);
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
                if(!newText.isEmpty()) {
                    posts2.clear();
                    for (int i = 0; i < posts.size(); i++) {
                        if (posts.get(i).get_description().contains(newText) && !newText.isEmpty()) {
                            posts2.add(posts.get(i));
                            Log.v("nt", newText);
                        }
                    }
                    MyAdapter adapter2 = new MyAdapter(HomeActivity.this, posts2);
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



            mytitle.setText(rPost.get(position).get_destination());
            mydate.setText(rPost.get(position).get_date());
            myway.setText(rPost.get(position).get_travel_type());
            mystart.setText(rPost.get(position).get_start_location());
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