package com.duxina.chatapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duxina.chatapplication.model.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonalChatActivity extends AppCompatActivity {

    private String sender;
    private String reciever;
    private String combinedName;

    private static final int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseListAdapter<ChatMessage> adapter;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getCombinedName() {
        return combinedName;
    }

    public void setCombinedName(String combinedName) {
        this.combinedName = combinedName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);
        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser userLogin = FirebaseAuth.getInstance().getCurrentUser();
        if (null == userLogin) {
            // Start sign in/sign up activity
            Intent login = new Intent(PersonalChatActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(PersonalChatActivity.this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser().getEmail(),
                    Toast.LENGTH_SHORT)
                    .show();
            String newString;
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                setSender(extras.getString("sender"));
                setReciever(extras.getString("reciever"));
                setCombinedName(extras.getString("combinedName"));
            } else {
                setSender((String) savedInstanceState.getSerializable("sender"));
                setReciever((String) savedInstanceState.getSerializable("reciever"));
                setCombinedName((String) savedInstanceState.getSerializable("combinedName"));
            }
            displayChatMessages();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = (EditText) findViewById(R.id.input);
                if (null == input || "".equalsIgnoreCase(input.getText().toString())) {
                    Toast.makeText(PersonalChatActivity.this, "Message tidak boleh kosong", Toast.LENGTH_LONG).show();
                } else {
                    // Read the input field and push a new instance
                    // of ChatMessage to the Firebase database
                    FirebaseDatabase.getInstance().getReference().child("USER_DATA").child(firebaseAuth.getUid()).child("fullName").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String fullName = dataSnapshot.getValue(String.class);
                            FirebaseDatabase.getInstance()
                                    .getReference().child("CHAT_PERSONAL").child(sender).child(combinedName)
                                    .push()
                                    .setValue(new ChatMessage(input.getText().toString(), fullName));
                            // Clear the input
                            input.setText("");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
        });


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
            Intent login = new Intent(PersonalChatActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        }
        return true;
    }

    private void displayChatMessages() {
        final ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child("CHAT_PERSONAL").child(sender).child(combinedName)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                DatabaseReference itemRef = getRef(position);
                String itemKey = itemRef.getKey();
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                model.setChatId(itemRef.getKey());
                FirebaseDatabase.getInstance()
                        .getReference().child("CHAT_PERSONAL").child(sender).child(combinedName)
                        .child(model.getChatId()).child("chatId")
                        .setValue(model.getChatId());
                FirebaseDatabase.getInstance()
                        .getReference().child("CHAT_PERSONAL").child(reciever).child(combinedName)
                        .child(model.getChatId()).setValue(model);
                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
        final ListView lv = (ListView) findViewById(R.id.list_of_messages);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View myView, int myItemInt, long mylng) {
                final ChatMessage selectedFromList = (ChatMessage) (lv.getItemAtPosition(myItemInt));
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                FirebaseDatabase.getInstance()
                                        .getReference().child("CHAT_PERSONAL").child(sender).child(combinedName)
                                        .child(selectedFromList.getChatId()).removeValue();
                                FirebaseDatabase.getInstance()
                                        .getReference().child("CHAT_PERSONAL").child(reciever).child(combinedName)
                                        .child(selectedFromList.getChatId()).removeValue();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //do nothing
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(lv.getContext());
                builder.setMessage("Delete Message?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }
}