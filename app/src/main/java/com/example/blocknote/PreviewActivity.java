package com.example.blocknote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class PreviewActivity extends AppCompatActivity {

    ImageView image_note;

    TextView title, content;
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;

    StorageReference storageReference;

    BottomNavigationView bottomNavigationView;



    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        String id = getIntent().getStringExtra("id_note");
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();


        content = findViewById(R.id.note_content);
        image_note = findViewById(R.id.note_image);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_add:
                        startActivity(new Intent(getApplicationContext()
                                , CreateNoteActivity.class));

                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menu_main:
                        return true;

                    case R.id.menu_shared:
                        startActivity(new Intent(getApplicationContext()
                                ,SharedActivity.class));

                        overridePendingTransition(0, 0);
                        return true;



                }
                return false;
            }
        });

        getNote(id);

    }

    private void getNote(String id){
        mfirestore.collection("note").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String notetitle = documentSnapshot.getString("title");

                String notecontent = documentSnapshot.getString("content");

                String imageNote = documentSnapshot.getString("image");


                content.setText(notecontent);
                setTitle(notetitle);
                try {
                    if(!imageNote.equals("")){
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.message_loading, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP,0,200);
                        toast.show();
                        Picasso.with(PreviewActivity.this)
                                .load(imageNote)

                                .into(image_note);
                    }
                }catch (Exception e){
                    Log.v("Error", "e: " + e);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), R.string.error_upload, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}