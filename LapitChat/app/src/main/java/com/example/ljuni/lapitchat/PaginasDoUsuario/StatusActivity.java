package com.example.ljuni.lapitchat.PaginasDoUsuario;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ljuni.lapitchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mStatus;
    private Button mSaveBtn;

    // Firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mUser;

    // Progress
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mToolbar = (Toolbar) findViewById(R.id.status_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Alterar Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_atual = getIntent().getStringExtra("status_atual");

        mStatus = (TextInputLayout) findViewById(R.id.status_input);
        mSaveBtn = (Button) findViewById(R.id.status_save_btn);

        mStatus.getEditText().setText(status_atual);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Progress
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Salvando alterações");
                mProgress.setMessage("Por favor, aguarde enquanto as alterações são salvas.");
                mProgress.show();

                String status = mStatus.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            mProgress.dismiss();
                            Intent settingsPage = new Intent(StatusActivity.this, SettingsActivity.class);
                            startActivity(settingsPage);
                        }

                        else {

                            Toast.makeText(getApplicationContext(), "Ocorreu um erro na hora de salvar as alterações."
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
