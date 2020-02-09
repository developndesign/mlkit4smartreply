package com.developndesign.mlkit4smartreply;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestion;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<FirebaseTextMessage> conversation;
    TextView replyText;
    EditText receiverMessage;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replyText = findViewById(R.id.reply_message);
        receiverMessage = findViewById(R.id.remote_message);
        conversation = new ArrayList<>();
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateReply();
            }
        });
    }

    private void generateReply() {
// when the user sends a message(local user) add the sender message and its timestamp to the list
//conversation.add(FirebaseTextMessage.createForLocalUser("heading out now", System.currentTimeMillis()));

// when user receives a message(remote user) add the message, its timestamp,
// and the sender's user ID to the list
        conversation.add(FirebaseTextMessage.createForRemoteUser(receiverMessage.getText().toString(),
                System.currentTimeMillis(), "user0"));
//To generate smart replies to a message, get an instance of FirebaseSmartReply and
        FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
// pass the conversation history to its suggestReplies() method:
        smartReply.suggestReplies(conversation)
                .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(SmartReplySuggestionResult result) {
                        if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                            replyText.setText("Language not supported");
                        } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
//If the operation succeeds, a SmartReplySuggestionResult object is passed to the success handler.
// This object contains a list of up to 3 suggested replies, which you can present to your user
                            replyText.setText("Suggestions: " + "\n\n");
                            for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                String replyMessage = suggestion.getText();
                                replyText.append("*" + replyMessage + ",\n\n");
                            }
                        }
                    }
                });
    }
}
