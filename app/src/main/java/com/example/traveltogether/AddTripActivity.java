package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    int ok_start = 1, ok_dest = 1, ok_save = 1;
    private TextView dateText, startTw, destinationTw;
    EditText start, destination, description;
    String str_start, str_dest, str_desc, str_date, userId;
    RadioGroup radioGroup;
    RadioButton radioButton;
    int radioButtonID;
    private Button addDate, create;

    private FirebaseUser user;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        dateText = findViewById(R.id.data_text);
        addDate = findViewById(R.id.data_button);
        start = findViewById(R.id.start);
        destination = findViewById(R.id.destinatie);
        description = findViewById(R.id.descriere);
        radioGroup = findViewById(R.id.radio_group);
        create = findViewById(R.id.create_button);
        startTw = findViewById(R.id.start_tw);
        destinationTw = findViewById(R.id.destinatie_tw);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.add_trip);

        Places.initialize(getApplicationContext(), "AIzaSyCqGZKpMWZ_lFV2jLKSngjdt1bJL92U4tc");

        start.setFocusable(false);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(AddTripActivity.this);
                startActivityForResult(intent, 100);
            }
        });

        destination.setFocusable(false);
        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(AddTripActivity.this);
                startActivityForResult(intent, 101);
            }
        });

        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.add_trip:
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

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTrip();
            }
        });

    }

    private void createTrip(){
        String type = " ";
        str_start = start.getText().toString().trim();
        str_dest = destination.getText().toString().trim();
        str_desc = description.getText().toString().trim();
        reff = FirebaseDatabase.getInstance().getReference("Trips");
        if (radioGroup.getCheckedRadioButtonId() == -1)
        {
            radioButton = findViewById(R.id.hike);
            radioButton.setError("Select a travel type!");
            radioGroup.requestFocus();
            return;
        }
        if(str_start.isEmpty()){
            startTw.setError("Start location is required!");
            startTw.requestFocus();
            return;
        }
        if(str_dest.isEmpty()){
            destinationTw.setError("Destination is required!");
            destinationTw.requestFocus();
            return;
        }
        if(str_date==null || str_date.isEmpty()){
            dateText.setError("Date is required!");
            dateText.requestFocus();
            return;
        }
        if(str_desc.isEmpty()){
            description.setError("Description is required!");
            description.requestFocus();
            return;
        }
        if(radioButtonID == findViewById(R.id.car).getId()) {
            type = "car";
        }
        if(radioButtonID == findViewById(R.id.motorbike).getId()) {
            type = "motorbike";
        }
        if(radioButtonID == findViewById(R.id.bike).getId()) {
            type = "bike";
        }
        if(radioButtonID == findViewById(R.id.electric_scooter).getId()) {
            type = "electric scooter";
        }
        if(radioButtonID == findViewById(R.id.hike).getId()) {
            type = "hike";
        }

        Post post = new Post(str_dest,str_start, str_desc, userId, type, str_date);
        radioGroup.clearCheck();
        description.setText("");
        destination.setText("");
        start.setText("");
        str_date = "";

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("destination", post.get_destination());
        hashMap.put("startLocation", post.get_start_location());
        hashMap.put("description", post.get_description());
        hashMap.put("creatorId", post.get_creator_id());
        hashMap.put("travelType", post.get_travel_type());
        hashMap.put("date", post.get_date());


        reff.push().setValue(hashMap);
        Toast.makeText(AddTripActivity.this, "Trip created successfully!", Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = getSharedPreferences("add", MODE_PRIVATE);
        preferences.edit().clear().commit();
        startActivity(new Intent(getApplicationContext(), AddTripActivity.class));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            start.setText(place.getAddress());
            ok_start = 0;
        }
        else if(requestCode==101 && resultCode==RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            destination.setText(place.getAddress());
            ok_dest = 0;
        }
        else if(resultCode== AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("logx", "onResume");
        SharedPreferences preferences = getSharedPreferences("add", MODE_PRIVATE);
        str_start = preferences.getString("start", null);
        str_dest = preferences.getString("dest", null);
        str_desc = preferences.getString("desc", null);
        str_date = preferences.getString("date", null);
        radioButtonID = preferences.getInt("radioButton", -1);
        if(ok_start==1) {
            start.setText(str_start);
        }else ok_start = 1;
        description.setText(str_desc);
        if(ok_dest==1) {
            destination.setText(str_dest);
        }else ok_dest = 1;
        dateText.setText(str_date);
        if(radioButtonID !=-1) {
            radioButton = (RadioButton) findViewById(radioButtonID);
            radioButton.setChecked(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("logx", "onPause");
        if(ok_save==1) {
            SharedPreferences preferences = getSharedPreferences("add", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            str_start = start.getText().toString().trim();
            str_dest = destination.getText().toString().trim();
            str_desc = description.getText().toString().trim();
            radioButtonID = radioGroup.getCheckedRadioButtonId();
            editor.putString("start", str_start);
            editor.putString("dest", str_dest);
            editor.putString("desc", str_desc);
            editor.putString("date", str_date);
            editor.putInt("radioButton", radioButtonID);
            editor.apply();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("logx", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("add", MODE_PRIVATE);
        preferences.edit().clear().commit();
        Log.i("logx", "onDestroy");
    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog=new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        str_date = "date: "+year+"/"+month+"/"+dayOfMonth;
        dateText.setText(str_date);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        overridePendingTransition(0,0);
    }
}