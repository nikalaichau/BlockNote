package com.example.blocknote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocknote.model.NoteModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.blocknote.adapter.NoteAdapter;

public class MainActivity extends AppCompatActivity{


   BottomNavigationView bottomNavigationView;
   NoteAdapter mAdapter;
   RecyclerView mRecycler;
   FirebaseFirestore mFirestore;
   FirebaseAuth mAuth;
   SearchView search_view;
   Query query;

   @SuppressLint("NotifyDataSetChanged")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mFirestore = FirebaseFirestore.getInstance();
      mAuth = FirebaseAuth.getInstance();
      search_view = findViewById(R.id.search_note);

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
               case R.id.menu_users:
                  startActivity(new Intent(getApplicationContext()
                          ,UsersActivity.class));

                  overridePendingTransition(0, 0);
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



      setUpRecyclerView();
      search_view();
   }



   @SuppressLint("NotifyDataSetChanged")
   private void setUpRecyclerView() {
      mRecycler = findViewById(R.id.recyclerViewSingleNote);
      mRecycler.setLayoutManager(new LinearLayoutManager(this));
      Query query = mFirestore.collection("note").whereEqualTo("id_user", mAuth.getCurrentUser().getUid());

      FirestoreRecyclerOptions<NoteModel> firestoreRecyclerOptions =
              new FirestoreRecyclerOptions.Builder<NoteModel>().setQuery(query, NoteModel.class).build();

      mAdapter = new NoteAdapter(firestoreRecyclerOptions, this, getSupportFragmentManager());
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
      mRecycler = findViewById(R.id.recyclerViewSingleNote);
      mRecycler.setLayoutManager(new LinearLayoutManager(this));
      Query query = mFirestore.collection("note").whereEqualTo("id_user", mAuth.getCurrentUser().getUid()).orderBy("title").startAt(s).endAt(s+"~");

      FirestoreRecyclerOptions<NoteModel> firestoreRecyclerOptions =
              new FirestoreRecyclerOptions.Builder<NoteModel>().setQuery(query, NoteModel.class).build();

      mAdapter = new NoteAdapter(firestoreRecyclerOptions, this, getSupportFragmentManager());
      mAdapter.startListening();
      mRecycler.setAdapter(mAdapter);



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