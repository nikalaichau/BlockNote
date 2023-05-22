package com.example.blocknote.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocknote.PreviewActivity;
import com.example.blocknote.UsersActivity;
import com.example.blocknote.model.NoteModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.blocknote.CreateNoteActivity;
import com.example.blocknote.R;

public class NoteAdapter extends FirestoreRecyclerAdapter<NoteModel, NoteAdapter.ViewHolder> {

   private final Boolean shared;
   private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
   Activity activity;
   FragmentManager fm;

   public NoteAdapter(@NonNull FirestoreRecyclerOptions<NoteModel> options, Boolean shared, Activity activity, FragmentManager fm) {
      super(options);
      this.activity = activity;
      this.fm = fm;
      this.shared = shared;
   }

   @Override
   protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull NoteModel NoteModel) {

      DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
      final String id = documentSnapshot.getId();

      viewHolder.title.setText(NoteModel.getTitle());
      viewHolder.content.setText(NoteModel.getContent());
      viewHolder.btn_view.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent i = new Intent(activity, PreviewActivity.class);
            i.putExtra("id_note", id);
            activity.startActivity(i);
         }
      });

      viewHolder.btn_share.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

            Intent i = new Intent(activity, UsersActivity.class);
            i.putExtra("id_note", id);
            activity.startActivity(i);
         }
      });

      if (shared){
         viewHolder.btn_share.setVisibility(View.GONE);
      }else {
         viewHolder.btn_share.setVisibility(View.VISIBLE);
      }
      viewHolder.btn_edit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent i = new Intent(activity, CreateNoteActivity.class);
            i.putExtra("id_note", id);
            activity.startActivity(i);
         }
      });

      viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            deleteNote(id);
         }
      });
   }

   private void deleteNote(String id) {
      mFirestore.collection("note").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
         @Override
         public void onSuccess(DocumentSnapshot documentSnapshot) {
            if (shared){
               mFirestore.collection("note").document(id).update("shared_to", null).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                     Toast.makeText(activity, R.string.message_success, Toast.LENGTH_SHORT).show();
                  }
               }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                     Toast.makeText(activity, R.string.error_delete, Toast.LENGTH_SHORT).show();
                  }
               });
            }else {
               mFirestore.collection("note").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                     Toast.makeText(activity, R.string.message_success, Toast.LENGTH_SHORT).show();
                  }
               }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                     Toast.makeText(activity, R.string.error_delete, Toast.LENGTH_SHORT).show();
                  }
               });
            }
         }
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
         }
      });
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_note_single, parent, false);
      return new ViewHolder(v);
   }

   public class ViewHolder extends RecyclerView.ViewHolder {
      TextView title, content;
      ImageView btn_delete, btn_edit, btn_view, btn_share;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);

         title = itemView.findViewById(R.id.note_title);
         content = itemView.findViewById(R.id.note_content);

         btn_delete = itemView.findViewById(R.id.btn_delete);
         btn_edit = itemView.findViewById(R.id.btn_edit);
         btn_view = itemView.findViewById(R.id.btn_view);
         btn_share = itemView.findViewById(R.id.btn_share);
      }
   }
}
