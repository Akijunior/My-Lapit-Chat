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
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mProfileSendSeqBtn;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;


    private FirebaseUser mCurrent_user;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();

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

        mCurrent_state = "not_friends";

        mProfileSendSeqBtn.setVisibility(View.INVISIBLE);
        mProfileSendSeqBtn.setEnabled(false);

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

                                mProfileSendSeqBtn.setVisibility(View.VISIBLE);
                                mProfileSendSeqBtn.setEnabled(true);
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

                    DatabaseReference newNotificationRef = mRootRef.child("notifications");
                    String newNotificationID = newNotificationRef.getKey();

                    HashMap<String, String> notificationsData = new HashMap<>();
                    notificationsData.put("from", mCurrent_user.getUid());
                    notificationsData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "receive");
                    requestMap.put("notifications/" + user_id + "/" +newNotificationID, notificationsData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Toast.makeText(ProfileActivity.this,
                                        "There was some error in sending request.", Toast.LENGTH_SHORT);
                            }

                            mProfileSendReqBtn.setEnabled(true);

                            mCurrent_state = "req_sent";
                            mProfileSendReqBtn.setText("Cancel Friend Request");
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

                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("Solicitar Amizade");

                                    mProfileSendSeqBtn.setVisibility(View.INVISIBLE);
                                    mProfileSendSeqBtn.setEnabled(false);

                                }
                            });
                        }
                    });

                }

                // ----------- Estado de Solicitação recebida --------------

                if (mCurrent_state.equals("req_received")) {

                    final String current_date = DateFormat.getDateInstance().format(new Date());

                    Map friendMap = new HashMap();
                    friendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", current_date);
                    friendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid() + "/date", current_date);

                    friendMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
                    friendMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state = "friends";
                                mProfileSendReqBtn.setText("Unfriend this Person.");

                                mProfileSendSeqBtn.setVisibility(View.INVISIBLE);
                                mProfileSendSeqBtn.setEnabled(false);
                            }

                            else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }

                // --------------- UNFRIENDS ------------

                if (mCurrent_state.equals("friends")) {

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                mCurrent_state = "not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");

                                mProfileSendSeqBtn.setVisibility(View.INVISIBLE);
                                mProfileSendSeqBtn.setEnabled(false);
                            }

                            else {

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });
                }
            }
        });
    }

}