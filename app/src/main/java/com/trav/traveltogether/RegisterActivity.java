package com.trav.traveltogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.trav.traveltogether.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etName, etEmail, etPassword, etConfPassword;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.name_register);
        etEmail = findViewById(R.id.email_register);
        etPassword= findViewById(R.id.password_register);
        etConfPassword = findViewById(R.id.password_confirm_register);
        register = findViewById(R.id.register);

        mAuth = FirebaseAuth.getInstance();

        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register(){
        String name = etName.getText().toString().trim(),
                email = etEmail.getText().toString().trim(),
                password = etPassword.getText().toString().trim(),
                confPassword = etConfPassword.getText().toString().trim();
        if(name.isEmpty()){
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please provide a valid email");
            etEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            etPassword.setError("Password is required!");
            etPassword.requestFocus();
            return;
        }
        if(confPassword.isEmpty()){
            etConfPassword.setError("Password confirmation is required!");
            etConfPassword.requestFocus();
            return;
        }
        if(password.length()<6){
            etPassword.setError("Password length should be min 6 characters!");
            etPassword.requestFocus();
            return;
        }
        if(!password.equals(confPassword)) {
            etConfPassword.setError("Passwords do not match!");
            etConfPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(email, name);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "Check your email to verify your account!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Register failed! Try again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterActivity.this, "Register failed! Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }
}