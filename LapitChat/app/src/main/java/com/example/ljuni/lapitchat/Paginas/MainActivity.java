package com.example.ljuni.lapitchat.Paginas;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ljuni.lapitchat.Adapters.SectionsPagerAdapter;
import com.example.ljuni.lapitchat.PaginasDoUsuario.SettingsActivity;
import com.example.ljuni.lapitchat.PaginasDoUsuario.UsersActivity;
import com.example.ljuni.lapitchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolBar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mUserRef;

    private TabLayout mTabLayout;

    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolBar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Vongola Chat");

        if (mAuth.getCurrentUser() != null) {

            String user_id = mAuth.getCurrentUser().getUid();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        }

        // Tabs
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Checar se usuário está ou não logado.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {
            Ver_se_usuario_esta_logado();
        }

        else {

            mUserRef.child("online").setValue("true");
        }
    }

    private void Ver_se_usuario_esta_logado() {
        Intent StartIntent = new Intent(MainActivity.this, StartChatActivity.class);
        startActivity(StartIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_chat_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_btn) {

            FirebaseAuth.getInstance().signOut();
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            Ver_se_usuario_esta_logado();
        }

        if (item.getItemId() == R.id.main_config_btn) {

            Intent configIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(configIntent);
        }
        if (item.getItemId() == R.id.main_todos_btn) {

            Intent usersIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(usersIntent);
        }

        return true;
    }
}
