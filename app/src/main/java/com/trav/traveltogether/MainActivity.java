package com.trav.traveltogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.trav.traveltogether.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button button_log, button_reg, button_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        button_log = (Button) findViewById(R.id.log);
        button_reg = (Button) findViewById(R.id.reg);
        button_home = (Button) findViewById(R.id.home);

        button_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLog();
            }
        });
        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReg();
            }
        });
        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHome();
            }
        });*/
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            openLog();
        }
        else{
            openHome();
        }
    }

    public void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void openLog() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}