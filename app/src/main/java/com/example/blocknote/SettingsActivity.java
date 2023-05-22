package com.example.blocknote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {


    Button change_language, log_out, about;
    TextView email, name, anon;
    LinearLayout profile;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth;

    FirebaseFirestore mFirestore;
    String idUser, emailUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        this.setTitle(R.string.settings);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        email = findViewById(R.id.user_email);
        name = findViewById(R.id.user_name);
        anon = findViewById(R.id.anon);
        profile = findViewById(R.id.profile);

        idUser = mAuth.getCurrentUser().getUid();
        emailUser = mAuth.getCurrentUser().getEmail();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_settings);
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
                        startActivity(new Intent(getApplicationContext()
                                , MainActivity.class));

                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.menu_shared:
                        startActivity(new Intent(getApplicationContext()
                                ,SharedActivity.class));

                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menu_users:
                        startActivity(new Intent(getApplicationContext()
                                ,UsersActivity.class));

                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menu_settings:
                        return true;


                }
                return false;
            }
        });
        about = findViewById(R.id.btn_about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
            }
        });

        log_out = findViewById(R.id.btn_logout);
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        change_language = findViewById(R.id.btn_language);
        change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });
        loadLocale();
        getUserData(idUser);
    }


    public void logOut() {
        if (emailUser == null){
            mAuth.getCurrentUser().delete();
            mAuth.signOut();
            finish();
            removeAllNotes(idUser);
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
        }else {
            mAuth.signOut();
            finish();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
        }
    }
    public void removeAllNotes (String idUser) {
        mFirestore.collection("note")
                .whereEqualTo("id_user", idUser)
                .get()
                .addOnSuccessListener((querySnapshot) -> {
                    WriteBatch batch = mFirestore.batch();
                    for (DocumentSnapshot doc : querySnapshot) {
                        batch.delete(doc.getReference());
                    }

                    batch.commit()
                            .addOnSuccessListener((result) -> {
                                Toast.makeText(getApplicationContext(), R.string.message_success, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener((error) -> {
                                Toast.makeText(getApplicationContext(), R.string.error_delete, Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener((error) -> {
                    Toast.makeText(getApplicationContext(), R.string.error_delete, Toast.LENGTH_SHORT).show();
                });
    }


    private void getUserData(String idUser) {
        if (emailUser == null){
            anon.setVisibility(View.VISIBLE);
            profile.setVisibility(View.GONE);
        }else {
            anon.setVisibility(View.GONE);
            profile.setVisibility(View.VISIBLE);
            mFirestore.collection("user").document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String username = documentSnapshot.getString("name");
                    String useremail = documentSnapshot.getString("email");
                    email.setText(useremail);
                    name.setText(username);
                }
            });
        }
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"English", "Русский"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
        mBuilder.setTitle("R.string.language");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    setLocale("en");
                    recreate();
                }else if (i == 1){
                    setLocale("ru");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
    private void setLocale(String language) {
        Locale locale = new Locale( language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", language);
        editor.apply();
    }
    public void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String myLanguage = preferences.getString("My_Lang","");
        setLocale(myLanguage);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}