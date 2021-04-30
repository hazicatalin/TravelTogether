package com.example.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfileActivity extends AppCompatActivity {
    String name, surname, phone="0765166085", description = "ala bala portocala", userId;
    TextView name_tw, surname_tw, phone_tw, description_tw;
    ImageButton edit_name, edit_surname, edit_descriprion, edit_phone;
    Button logout;

    private FirebaseUser user;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        name_tw = findViewById(R.id.name);
        surname_tw = findViewById(R.id.surname);
        phone_tw = findViewById(R.id.phone);
        description_tw = findViewById(R.id.description);
        edit_name = findViewById(R.id.edit_name);
        edit_surname = findViewById(R.id.edit_surname);
        edit_phone = findViewById(R.id.edit_phone);
        edit_descriprion = findViewById(R.id.edit_description);
        logout = findViewById(R.id.log_out);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId = user.getUid();

        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile!=null){
                    name = userProfile.name;
                    surname = userProfile.email;
                    name_tw.setText(name);
                    surname_tw.setText(surname);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfileActivity.this, "Something wrong happened", Toast.LENGTH_SHORT).show();

            }
        });

        phone_tw.setText(phone);
        description_tw.setText(description);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.my_profile);

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
                        return true;
                    case R.id.my_trips:
                        startActivity(new Intent(getApplicationContext(), MyTripsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                builder.setTitle("Edit Name");
                final EditText et = new EditText(MyProfileActivity.this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(et);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = et.getText().toString();
                        name_tw.setText(name);
                        Toast.makeText(MyProfileActivity.this, "Edit", Toast.LENGTH_SHORT).show();}
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        edit_surname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                builder.setTitle("Edit Name");
                final EditText et = new EditText(MyProfileActivity.this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(et);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        surname = et.getText().toString();
                        surname_tw.setText(surname);
                        Toast.makeText(MyProfileActivity.this, "Edit", Toast.LENGTH_SHORT).show();}
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        edit_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                builder.setTitle("Edit Name");
                final EditText et = new EditText(MyProfileActivity.this);
                et.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(et);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        phone = et.getText().toString();
                        phone_tw.setText(phone);
                        Toast.makeText(MyProfileActivity.this, "Edit", Toast.LENGTH_SHORT).show();}
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        edit_descriprion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                builder.setTitle("Edit Name");
                final EditText et = new EditText(MyProfileActivity.this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(et);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        description = et.getText().toString();
                        description_tw.setText(description);
                        Toast.makeText(MyProfileActivity.this, "Edit", Toast.LENGTH_SHORT).show();}
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
}