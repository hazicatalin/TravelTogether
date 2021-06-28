package com.trav.traveltogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.trav.traveltogether.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {
    String name, phone, description, userId;
    TextView name_tw, phone_tw, description_tw;
    ImageButton edit_name, edit_description, edit_phone;
    Button logout;
    CircleImageView profile_image;

    private FirebaseUser user;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        name_tw = findViewById(R.id.name);
        phone_tw = findViewById(R.id.phone);
        description_tw = findViewById(R.id.description);
        edit_name = findViewById(R.id.edit_name);
        edit_phone = findViewById(R.id.edit_phone);
        edit_description = findViewById(R.id.edit_description);
        logout = findViewById(R.id.log_out);
        profile_image = findViewById(R.id.profile_image);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getImage();

        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile!=null){
                    name = userProfile.name;
                    name_tw.setText(name);
                    phone = userProfile.get_phoneNumber();
                    description = userProfile.get_description();
                    phone_tw.setText(phone);
                    description_tw.setText(description);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfileActivity.this, "Something wrong happened", Toast.LENGTH_SHORT).show();

            }
        });

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

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                builder.setTitle("Edit Image");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Choose from gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadPhoto();
                    }
                });
                builder.setNeutralButton("Take a photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (ContextCompat.checkSelfPermission(MyProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MyProfileActivity.this, new String[] {Manifest.permission.CAMERA}, 1);
                        }
                        else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 100);
                        }

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                builder.setTitle("Edit your name");
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
                        reference.child(userId).child("name").setValue(name);
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
                builder.setTitle("Edit phone number");
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
                        reference.child(userId).child("phoneNumber").setValue(phone);
                        Toast.makeText(MyProfileActivity.this, "Edit", Toast.LENGTH_SHORT).show();}
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        edit_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                builder.setTitle("Edit your description");
                final EditText et = new EditText(MyProfileActivity.this);
                et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
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
                        reference.child(userId).child("description").setValue(description);
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

    private void uploadPhoto(){
        if (ContextCompat.checkSelfPermission(MyProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MyProfileActivity.this, new String[] {Manifest.permission.CAMERA}, 1);
        }
        else {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 101);
        }
    }

    private void getImage(){
        try {
            final File file = File.createTempFile(userId, "jpg");
            storageReference.child("images/"+userId).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    profile_image.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MyProfileActivity.this, "Failed uploading photo!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (IOException ex){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                uploadPhoto();
            }
            else{
                Toast.makeText(MyProfileActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            captureImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            byte bb[] = bytes.toByteArray();
            String file = Base64.encodeToString(bb, Base64.DEFAULT);
            profile_image.setImageBitmap(captureImage);
            StorageReference ref = storageReference.child("images/"+userId);
            ref.putBytes(bb)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MyProfileActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MyProfileActivity.this, "Image failed uploading!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if (resultCode == RESULT_OK && requestCode == 101 && data.getData()!=null) {
            Uri imageUri = data.getData();
            profile_image.setImageURI(imageUri);
            StorageReference ref = storageReference.child("images/"+userId);
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MyProfileActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MyProfileActivity.this, "Image failed uploading!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("add", MODE_PRIVATE);
        preferences.edit().clear().commit();
        Log.i("logx", "onDestroy");
    }
}