package com.example.blocknote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class PreviewActivity extends AppCompatActivity {

    ImageView image_note;

    TextView content;
    private FirebaseFirestore mfirestore;
    FirebaseAuth mAuth;

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