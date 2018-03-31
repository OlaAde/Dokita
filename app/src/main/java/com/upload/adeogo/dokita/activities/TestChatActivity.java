package com.upload.adeogo.dokita.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.upload.adeogo.dokita.R;
import com.upload.adeogo.dokita.adapters.QuestionAdapter;
import com.upload.adeogo.dokita.models.Message;
import com.upload.adeogo.dokita.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestChatActivity extends AppCompatActivity
        implements DateFormatter.Formatter  {

    MessageInput messageInput;
    MessagesList messagesList;
    MessagesListAdapter messagesListAdapter;
    ImageLoader imageLoader;
    Message messageToAdd;
    User user, meUser;
    List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_chat);

        messageInput = findViewById(R.id.input);
        messagesList = findViewById(R.id.messagesList);

        messages =  new ArrayList<>();
        user =  new User("ds", "Name", "https://cdn.pixabay.com/photo/2013/07/13/10/07/man-156584_1280.png", true);
        meUser =  new User("0", "Name", "https://cdn.pixabay.com/photo/2013/07/13/10/07/man-156584_1280.png", true);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.get().load(url).into(imageView);
            }
        };

        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                String string = input.toString().trim();
                messageToAdd = new Message("232", meUser, string);
                messages.clear();
                messages.add(messageToAdd);
                messagesListAdapter.addToEnd(messages, true);
                return true;
            }
        });

        messageInput.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                Toast.makeText(TestChatActivity.this, "New Attachment ", Toast.LENGTH_SHORT).show();

                String string = "New Message Guy";
                messageToAdd = new Message("345", user, string);
                messages.clear();
                messages.add(messageToAdd);
                messagesListAdapter.addToEnd(messages, true);
            }
        });

        messagesListAdapter = new MessagesListAdapter("0", imageLoader);
        messagesListAdapter.enableSelectionMode(new MessagesListAdapter.SelectionListener() {
            @Override
            public void onSelectionChanged(int count) {

            }
        });

        messagesList.setAdapter(messagesListAdapter, false);

        messageInput.getInputEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return true;
            }
        });
    }

    @Override
    public String format(Date date) {
        return null;
    }
}
