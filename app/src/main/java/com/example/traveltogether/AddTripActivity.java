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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    int ok_start = 1, ok_dest = 1;
    private TextView dateText;
    EditText start, destination, description;
    String str_start, str_dest, str_desc, str_date;
    RadioGroup radioGroup;
    RadioButton radioButton;
    int radioButtonID;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        Log.i("logx", "onCreate");
        dateText = findViewById(R.id.data_text);
        button = findViewById(R.id.data_button);
        start = findViewById(R.id.start);
        destination = findViewById(R.id.destinatie);
        description = findViewById(R.id.descriere);
        radioGroup = findViewById(R.id.radio_group);

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

        button.setOnClickListener(new View.OnClickListener() {
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
        SharedPreferences preferences = getSharedPreferences("add", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        str_start = start.getText().toString();
        str_dest = destination.getText().toString();
        str_desc = description.getText().toString();
        radioButtonID = radioGroup.getCheckedRadioButtonId();
        editor.putString("start", str_start);
        editor.putString("dest", str_dest);
        editor.putString("desc", str_desc);
        editor.putString("date", str_date);
        editor.putInt("radioButton", radioButtonID);
        editor.apply();
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