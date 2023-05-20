package com.example.blocknote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocknote.adapter.UserAdapter;
import com.example.blocknote.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UsersActivity extends AppCompatActivity{


    BottomNavigationView bottomNavigationView;
    UserAdapter mAdapter;
    RecyclerView mRecycler;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    SearchView search_view;

    String id_note;
    Query query;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        search_view = findViewById(R.id.search_user);
        id_note = getIntent().getStringExtra("id_note");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_users);
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
                        return true;
                    case R.id.menu_settings:
                        startActivity(new Intent(getApplicationContext()
                                ,SettingsActivity.class));

                        overridePendingTransition(0, 0);
                        return true;


                }
                return false;
            }
        });



        if (id_note == null || id_note == ""){
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else {
            bottomNavigationView.setVisibility(View.GONE);
            this.setTitle(R.string.share_to);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setUpRecyclerView(id_note);
        search_view();
    }



    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView(String id_note) {

        mRecycler = findViewById(R.id.recyclerViewSingleUser);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        Query query = mFirestore.collection("user");

        FirestoreRecyclerOptions<UserModel> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<UserModel>().setQuery(query, UserModel.class).build();

        mAdapter = new UserAdapter(firestoreRecyclerOptions, this, getSupportFragmentManager(), id_note);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }

    private void search_view() {
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                textSearch(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textSearch(s);


                return false;
            }
        });
    }
    public void textSearch(String s){
        mRecycler = findViewById(R.id.recyclerViewSingleUser);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        Query query = mFirestore.collection("user").orderBy("email").startAt(s).endAt(s+"~");

        FirestoreRecyclerOptions<UserModel> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<UserModel>().setQuery(query, UserModel.class).build();
        String id_note = getIntent().getStringExtra("id_note");
        mAdapter = new UserAdapter(firestoreRecyclerOptions, this, getSupportFragmentManager(), id_note);
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);



    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}