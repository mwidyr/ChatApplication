package com.duxina.chatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseAuth firebaseAuth;

    private Button groupChat;
    private Button personalChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser userLogin = FirebaseAuth.getInstance().getCurrentUser();
        Log.v("isiUserLogin", "user = " + userLogin);
        if (null == userLogin) {
            // Start sign in/sign up activity
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(MainActivity.this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser().getEmail(),
                    Toast.LENGTH_SHORT)
                    .show();
            initView();
            setListener();
        }

    }

    private void initView() {
        groupChat = (Button) findViewById(R.id.main_group_chat);
        personalChat = (Button) findViewById(R.id.main_personal_chat);
    }

    private void setListener() {
        groupChat.setOnClickListener(this);
        personalChat.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_group_chat:
                Intent groupChat = new Intent(MainActivity.this, GroupChatAcitivity.class);
                startActivity(groupChat);
                break;
            case R.id.main_personal_chat:
                Intent personalChat = new Intent(MainActivity.this, ListNameActivity.class);
                startActivity(personalChat);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        }
        return true;
    }
}