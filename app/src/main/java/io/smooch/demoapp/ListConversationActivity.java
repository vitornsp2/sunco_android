package io.smooch.demoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import io.smooch.core.Conversation;
import io.smooch.core.Smooch;
import io.smooch.demoapp.adapter.ChatMessageAdapter;
import io.smooch.demoapp.adapter.ConversationAdapter;

public class ListConversationActivity extends AppCompatActivity {


    private RecyclerView rvConversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_conversation);

        rvConversations = findViewById(R.id.rv_conversations);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvConversations.setLayoutManager(linearLayoutManager);
        rvConversations.setItemAnimator(new DefaultItemAnimator());

        Conversation conversation = Smooch.getConversation();

        Smooch.getConversationsList(response -> {
            ConversationAdapter adapter = new ConversationAdapter(this, response.getData());
            rvConversations.setAdapter(adapter);
        });

    }
}