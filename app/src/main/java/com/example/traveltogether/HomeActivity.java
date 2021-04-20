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

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ListView listView;
    String titles[] = {"transfagarasan", "mare", "Brasov", "Delta Dunarii", "Straja", "Bucuresti"};
    String dates[] = {"15.iunie.2021", "22.august.2021", "1.mai.2021", "19.septembrie.2021", "5.ianuarie.2022", "15.mai.2021"};
    int image = R.drawable.im_travel;
    List <String> titles2 = new ArrayList <String>();
    List <String> dates2 = new ArrayList <String>();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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

        MyAdapter adapter = new MyAdapter(this, titles, dates);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(HomeActivity.this, titles[position], Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), PostActivity.class);
                intent.putExtra("title", titles[position]);
                intent.putExtra("data", dates[position]);
                startActivity(intent);
                Log.v("itemclick: ", titles[position]);
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
                MyAdapter adapter2 = new MyAdapter(HomeActivity.this, titles2.toArray(new String[0]), dates2.toArray(new String[0]));
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
                    titles2.clear();
                    for (int i = 0; i < titles.length; i++) {
                        if (titles[i].contains(newText) && !newText.isEmpty()) {
                            titles2.add(titles[i]);
                            dates2.add(dates[i]);
                            Log.v("nt", newText);
                        }
                    }
                    MyAdapter adapter2 = new MyAdapter(HomeActivity.this, titles2.toArray(new String[0]), dates2.toArray(new String[0]));
                    listView.setAdapter(adapter2);
                }
                return false;
            }
        });
        return true;
    }

    class MyAdapter extends ArrayAdapter<String>{
        Context context;
        String rTitle[];
        String rDate[];
        int rImage;

        MyAdapter (Context c, String title[], String date[]){
            super(c, R.layout.travel, R.id.travel_title, title);
            this.context=c;
            this.rTitle=title;
            this.rDate = date;
            this.rImage=image;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View travel = layoutInflater.inflate(R.layout.travel, parent, false);
            TextView mytitle = travel.findViewById(R.id.travel_title);
            TextView mydate = travel.findViewById(R.id.travel_date);
            ImageView myimage = travel.findViewById(R.id.image);

            mytitle.setText(rTitle[position]);
            mydate.setText(rDate[position]);
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