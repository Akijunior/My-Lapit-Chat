package com.example.ljuni.lapitchat.Paginas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ljuni.lapitchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayNome;
    private TextInputLayout mDisplayEmail;
    private TextInputLayout mSenha;
    private TextInputLayout mConfirmarSenha;
    private Button btnCriarConta;

    // ProgressDialog
    private ProgressDialog mRegProgress;

    private Toolbar mToolbar;
    private DatabaseReference mDatabase;
    private String TAG;

    // Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Progress Dialog
        mRegProgress = new ProgressDialog(this);

        // Toolbar Set
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Criar nova conta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Campos de Cadastro
        mDisplayNome = (TextInputLayout) findViewById(R.id.req_nome);
        mDisplayEmail = (TextInputLayout) findViewById(R.id.req_email);
        mSenha = (TextInputLayout) findViewById(R.id.req_senha);
        mConfirmarSenha = (TextInputLayout) findViewById(R.id.req_conf_senha);
        btnCriarConta = (Button) findViewById(R.id.btn_req_criar_conta);

        TAG = "Hello World!";

        btnCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String display_nome = mDisplayNome.getEditText().getText().toString();
                String display_email = mDisplayEmail.getEditText().getText().toString();
                String display_senha = mSenha.getEditText().getText().toString();
                String display_conf_senha = mConfirmarSenha.getEditText().getText().toString();

                if (!TextUtils.isEmpty(display_nome) || !TextUtils.isEmpty(display_email)
                        || !TextUtils.isEmpty(display_senha) || !TextUtils.isEmpty(display_conf_senha)) {

                    mRegProgress.setTitle("Registrando Usuário");
                    mRegProgress.setMessage("Por favor, aguarde enquanto criamos sua conta!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    Registrar_Usuario(display_nome, display_email, display_senha, display_conf_senha);
                }
            }
        });

    }

    private void Registrar_Usuario
            (final String nome, String email, String senha, String conf_senha) {

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        String error = "";

                        try {
                            throw task.getException();
                        }

                        catch (FirebaseAuthWeakPasswordException e) {
                            error = "Senha fraca!";
                        }

                        catch (FirebaseAuthInvalidCredentialsException e) {
                            error = "Email invalido!";
                        }
                        catch (FirebaseAuthUserCollisionException e) {
                            error = "Este email já está sendo usado!";
                        }
                        catch (Exception e) {
                            error = "Erro desconhecido!";
                            e.printStackTrace();
                        }

                        if (task.isSuccessful()) {

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            String device_token = FirebaseInstanceId.getInstance().getToken();

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", nome);
                            userMap.put("status", "Olá, eu estou usando o Lapit Chat.");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");
                            userMap.put("device_token", device_token);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        mRegProgress.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                        else {

                            mRegProgress.hide();
                            Toast.makeText(RegisterActivity.this, "Houve um erro em seu cadastro. " +
                                            error,
                                    Toast.LENGTH_LONG).show();
                        }
                        // ...
                    }
                });
    }
}
