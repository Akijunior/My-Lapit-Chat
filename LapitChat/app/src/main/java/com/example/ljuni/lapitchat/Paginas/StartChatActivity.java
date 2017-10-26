package com.example.ljuni.lapitchat.Paginas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ljuni.lapitchat.R;


public class StartChatActivity extends AppCompatActivity {

    private Button botaoRegistrar;
    private Button botaoLogar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);

        botaoRegistrar = (Button) findViewById(R.id.start_reg_btn);
        botaoRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentRegistrar = new Intent(StartChatActivity.this, RegisterActivity.class);
                startActivity(intentRegistrar);
            }
        });

        botaoLogar = (Button) findViewById(R.id.btn_ir_a_pagina_de_login);
        botaoLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentLogar = new Intent(StartChatActivity.this, LoginActivity.class);
                startActivity(intentLogar);
            }
        });
    }
}