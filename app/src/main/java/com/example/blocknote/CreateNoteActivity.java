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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class CreateNoteActivity extends AppCompatActivity {

   ImageView image_note;
   Button btn_add;
   Button btn_image_add, btn_image_remove;
   LinearLayout linearLayout_image_btn;
   EditText title, content;
   private FirebaseFirestore mfirestore;
   private FirebaseAuth mAuth;

   StorageReference storageReference;

   private static final int COD_SEL_IMAGE = 300;

   String image = "image";
   String idd;

   ProgressDialog progressDialog;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_create_note);
      this.setTitle(R.string.create);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      progressDialog = new ProgressDialog(this);
      String id = getIntent().getStringExtra("id_note");
      mfirestore = FirebaseFirestore.getInstance();
      mAuth = FirebaseAuth.getInstance();
      storageReference = FirebaseStorage.getInstance().getReference();

      linearLayout_image_btn = findViewById(R.id.images_btn);
      title = findViewById(R.id.note_title);

      content = findViewById(R.id.note_content);

      image_note = findViewById(R.id.note_image);
      btn_image_add = findViewById(R.id.btn_image);
      btn_image_remove = findViewById(R.id.btn_remove_image);

      btn_add = findViewById(R.id.add);

      btn_image_add.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            uploadImage();
         }
      });
      btn_image_remove.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("image", "");
            mfirestore.collection("note").document(idd).update(map);
            Toast.makeText(CreateNoteActivity.this, R.string.message_success, Toast.LENGTH_SHORT).show();
            recreate();
         }
      });


      if (id == null || id == ""){
         linearLayout_image_btn.setVisibility(View.GONE);
         btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String notetitle = title.getText().toString().trim();
               String notecontent = content.getText().toString().trim();
               if(notetitle.isEmpty() || notecontent.isEmpty()){
                  Toast.makeText(getApplicationContext(), R.string.error_no_data, Toast.LENGTH_SHORT).show();
               }else{
                  postNote(notetitle, notecontent);
               }
            }
         });
      }else{
         idd = id;
         btn_add.setText(R.string.update);
         getNote(id);
         btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String notetitle = title.getText().toString().trim();
               String notecontent = content.getText().toString().trim();
               if(notetitle.isEmpty() || notecontent.isEmpty()){
                  Toast.makeText(getApplicationContext(), R.string.error_no_data, Toast.LENGTH_SHORT).show();
               }else{
                  updateNote(notetitle, notecontent, id);
               }
            }
         });
      }
   }

   private void uploadImage() {
      Intent i = new Intent(Intent.ACTION_PICK);
      i.setType("image/*");

      startActivityForResult(i, COD_SEL_IMAGE);

   }


   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      if(resultCode == RESULT_OK){
         if (requestCode == COD_SEL_IMAGE){
            Uri image_url = data.getData();
            uploadImage(image_url);
         }
      }
      super.onActivityResult(requestCode, resultCode, data);
   }

   private void uploadImage(Uri image_url) {
      progressDialog.setMessage("Loading");
      progressDialog.show();
      String rute_storage_image =  "|" + image + "|" + mAuth.getUid() +"|"+ idd;
      StorageReference reference = storageReference.child(rute_storage_image);
      reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
         @Override
         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) {
            }
               if (uriTask.isSuccessful()){
                  uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                     @Override
                     public void onSuccess(Uri uri) {
                        String download_uri = uri.toString();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", download_uri);
                        mfirestore.collection("note").document(idd).update(map);
                        Toast.makeText(CreateNoteActivity.this, R.string.message_success, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        recreate();
                     }
                  });
               }
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(CreateNoteActivity.this, R.string.error_upload, Toast.LENGTH_SHORT).show();
         }
      });
   }

   private void updateNote(String notetitle, String notecontent, String id) {
      Map<String, Object> map = new HashMap<>();
      map.put("title", notetitle);
      map.put("content", notecontent);
      mfirestore.collection("note").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void unused) {
            Toast.makeText(getApplicationContext(), R.string.message_success, Toast.LENGTH_SHORT).show();
            finish();
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), R.string.error_upload, Toast.LENGTH_SHORT).show();
         }
      });

   }

   private void postNote(String notetitle, String notecontent) {
      String idUser = mAuth.getCurrentUser().getUid();
      DocumentReference id = mfirestore.collection("note").document();
      Map<String, Object> map = new HashMap<>();
      map.put("id_user", idUser);
      map.put("id", id.getId());
      map.put("title", notetitle);
      map.put("shared_to", null);
      map.put("image", "");
      map.put("content", notecontent);
      mfirestore.collection("note").document(id.getId()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void unused) {
            Toast.makeText(getApplicationContext(), R.string.message_success, Toast.LENGTH_SHORT).show();
            finish();
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), R.string.error_upload, Toast.LENGTH_SHORT).show();
         }
      });
      recreate();
   }

   private void getNote(String id){
      mfirestore.collection("note").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
         @Override
         public void onSuccess(DocumentSnapshot documentSnapshot) {
            String notetitle = documentSnapshot.getString("title");
            String notecontent = documentSnapshot.getString("content");
            String imageNote = documentSnapshot.getString("image");
            title.setText(notetitle);
            content.setText(notecontent);
            try {
               if(!imageNote.equals("")){
                  btn_image_remove.setVisibility(View.VISIBLE);
                  btn_image_add.setVisibility(View.GONE);
                  Toast toast = Toast.makeText(getApplicationContext(), R.string.message_loading, Toast.LENGTH_SHORT);
                  toast.setGravity(Gravity.TOP,0,200);
                  toast.show();
                  Picasso.with(CreateNoteActivity.this)
                          .load(imageNote)
                          .resize(400, 400)
                          .into(image_note);
               }else{
                  btn_image_remove.setVisibility(View.GONE);
                  btn_image_add.setVisibility(View.VISIBLE);
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