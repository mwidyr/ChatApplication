package com.duxina.chatapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duxina.chatapplication.utils.Utils;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListNameActivity extends Utils{
    private static final int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseListAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_name);
        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser userLogin = FirebaseAuth.getInstance().getCurrentUser();
        if (null == userLogin) {
            // Start sign in/sign up activity
            Intent login = new Intent(ListNameActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(ListNameActivity.this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser().getEmail(),
                    Toast.LENGTH_SHORT)
                    .show();
            displayChatMessages();
        }
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

                displayChatMessages();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent login = new Intent(ListNameActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        }
        return true;
    }

    private void displayChatMessages() {
        final ListView listOfFriends = (ListView) findViewById(R.id.list_of_friends);
        adapter = new FirebaseListAdapter<String>(this, String.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child("LIST_USER")) {
            @Override
            protected void populateView(View v, String model, int position) {
                DatabaseReference itemRef = getRef(position);
                String itemKey = itemRef.getKey();
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                // Set their text
                messageText.setText(itemKey);
                messageUser.setText("");
                messageTime.setText("");
            }
        };
        listOfFriends.setAdapter(adapter);
        final ListView lv = (ListView) findViewById(R.id.list_of_friends);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View myView, int myItemInt, long mylng) {
                final String selectedFriend = (String) (lv.getItemAtPosition(myItemInt));
                FirebaseDatabase.getInstance().getReference().child("USER_DATA").child(selectedFriend).child("username").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String selectedFriendName = dataSnapshot.getValue(String.class);
                        FirebaseDatabase.getInstance().getReference().child("USER_DATA").child(firebaseAuth.getUid()).child("username").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String usernameLogin = dataSnapshot.getValue(String.class);
                                if(usernameLogin.equalsIgnoreCase(selectedFriendName)){
                                    Toast.makeText(ListNameActivity.this,"It is You!", Toast.LENGTH_SHORT).show();
                                }else{
                                    String combineNameChat = combineName(usernameLogin, selectedFriendName);
                                    Toast.makeText(ListNameActivity.this,combineNameChat, Toast.LENGTH_SHORT).show();
                                    Intent personalChatActivity = new Intent(ListNameActivity.this, PersonalChatActivity.class);
                                    Bundle extra = new Bundle();
                                    personalChatActivity.putExtra("combinedName",combineNameChat);
                                    personalChatActivity.putExtra("sender", usernameLogin);
                                    personalChatActivity.putExtra("reciever", selectedFriendName);
                                    startActivity(personalChatActivity);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
