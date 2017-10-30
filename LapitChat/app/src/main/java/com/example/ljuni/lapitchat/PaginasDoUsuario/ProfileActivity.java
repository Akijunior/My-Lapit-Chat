package com.example.ljuni.lapitchat.PaginasDoUsuario;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ljuni.lapitchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mProfileSendSeqBtn;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;


    private FirebaseUser mCurrent_user;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");


        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);

        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);

        mProfileSendSeqBtn= (Button) findViewById(R.id.profile_send_sec_btn);
        mProfileSendSeqBtn.setVisibility(View.VISIBLE);
        mProfileSendSeqBtn.setEnabled(true);

        mCurrent_state = "not_friends";

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Carregando perfil do usuário");
        mProgressDialog.setMessage("Por favor, aguarde.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        if(user_id.equals(mCurrent_user.getUid())){
            mProfileSendReqBtn.setVisibility(View.INVISIBLE);
            mProfileSendSeqBtn.setVisibility(View.INVISIBLE);
        }

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.new_profile_image).into(mProfileImage);

                // ----------- FRIEND LIST / REQUEST FEATURE --------------

                mFriendRequestDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                mCurrent_state = "req_received";
                                mProfileSendReqBtn.setText("Aceitar Solicitação");
                                mProfileSendSeqBtn.setText("Rejeitar Solicitação");

                                mProfileSendSeqBtn.setVisibility(View.INVISIBLE);
                                mProfileSendSeqBtn.setEnabled(false);
                            }

                            else if (req_type.equals("sent")) {

                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancelar solicitação");

                                mProfileSendSeqBtn.setVisibility(View.INVISIBLE);
                                mProfileSendSeqBtn.setEnabled(false);
                            }

                            mProgressDialog.dismiss();
                        }

                        else {

                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)) {

                                        mCurrent_state = "friends";
                                        mProfileSendReqBtn.setText("Desfazer Amizade");

                                        mProfileSendSeqBtn.setVisibility(View.INVISIBLE);
                                        mProfileSendSeqBtn.setEnabled(false);
                                    }

                                    mProgressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendReqBtn.setEnabled(false);

                // ----------- Situação Não Amigos --------------

                if (mCurrent_state.equals("not_friends")) {

                    mFriendRequestDatabase.child(mCurrent_user.getUid()).child(user_id)
                            .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                mFriendRequestDatabase.child(user_id).child(mCurrent_user.getUid())
                                        .child("request_type").setValue("received")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                HashMap<String, String> notificationsData = new HashMap<>();
                                                notificationsData.put("from", mCurrent_user.getUid());
                                                notificationsData.put("type", "request");

                                                mNotificationDatabase.child(user_id).push().setValue(notificationsData)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                mCurrent_state = "req_sent";
                                                                mProfileSendReqBtn.setText("Cancelar solicitação de amizade");

                                                                mProfileSendSeqBtn.setVisibility(View.INVISIBLE);
                                                                mProfileSendSeqBtn.setEnabled(false);
                                                            }
                                                        });


                                                // Toast.makeText(ProfileActivity.this, "Solicitação enviada com sucesso.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {

                                Toast.makeText(ProfileActivity.this, "Falha ao enviar solicitação.", Toast.LENGTH_SHORT).show();
                            }


                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });
                }

                // ----------- Cancelar Solicitação --------------

                if (mCurrent_state.equals("req_sent")) {

                    mFriendRequestDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendRequestDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendSeqBtn.setVisibility(View.INVISIBLE);
                                    mProfileSendSeqBtn.setEnabled(false);

                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("Solicitar Amizade");

                                }
                            });
                        }
                    });

                }

                // ----------- Estado de Solicitação recebida --------------

                if (mCurrent_state.equals("req_received")) {

                    final String current_date = DateFormat.getDateInstance().format(new Date());

                    mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(user_id).child(mCurrent_user.getUid()).setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendRequestDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mFriendRequestDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mProfileSendReqBtn.setEnabled(true);
                                                    mCurrent_state = "friends";
                                                    mProfileSendReqBtn.setText("Desfazer Amizade");

                                                    mProfileSendSeqBtn.setVisibility(View.INVISIBLE);
                                                    mProfileSendSeqBtn.setEnabled(false);

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    });

                }


            }
        });
    }
}
