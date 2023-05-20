package com.example.blocknote.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocknote.MainActivity;
import com.example.blocknote.UsersActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.blocknote.R;
import com.example.blocknote.model.UserModel;

import java.util.HashMap;
import java.util.Map;

public class UserAdapter extends FirestoreRecyclerAdapter<UserModel, UserAdapter.ViewHolder> {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    FragmentManager fm;
    String id_note;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     * @param id_note
     */
    public UserAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Activity activity, FragmentManager fm, String id_note) {
        super(options);
        this.activity = activity;
        this.fm = fm;
        this.id_note = id_note;


    }

    @Override
    protected void onBindViewHolder(@NonNull UserAdapter.ViewHolder viewHolder, int i,  @NonNull UserModel UserModel) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id_user = documentSnapshot.getId();



        viewHolder.name.setText(UserModel.getName());
        viewHolder.email.setText(UserModel.getEmail());



        viewHolder.btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareNote(id_user, id_note);
            }
        });


    }

    private void shareNote(String id_user, String id_note) {

        Map<String, Object> map = new HashMap<>();
        map.put("shared_to", id_user);


        mFirestore.collection("note").document(id_note).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(activity, R.string.message_success, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(activity, R.string.error_update, Toast.LENGTH_SHORT).show();
            }
        });
        activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));

    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_single, parent, false);
        return new UserAdapter.ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        ImageView btn_share;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_name);
            email = itemView.findViewById(R.id.user_email);
            btn_share = itemView.findViewById(R.id.btn_confirm_share);

            if (id_note == null || id_note ==""){
                btn_share.setVisibility(View.GONE);

            }else {
                btn_share.setVisibility(View.VISIBLE);
            }
        }
    }
}
