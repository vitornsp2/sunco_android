package io.smooch.demoapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;

import io.smooch.core.Conversation;
import io.smooch.core.Message;
import io.smooch.demoapp.R;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {
    private Context mContext;
    private List<Conversation> conversations;

    public ConversationAdapter(Context context, List<Conversation> conversations) {
        mContext = context;
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public int getItemCount() {
        return conversations.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            Conversation conversation = conversations.get(position);

            Glide
                    .with(mContext)
                    .load(conversation.getIconUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .into(holder.ivLogo);

            holder.tvTitle.setText(conversation.getDisplayName());

            List<Message> messages = conversation.getMessages();
            if (messages.size() > 0){
                Message message = messages.get(messages.size() - 1);
                holder.tvSubtitle.setText(message.getName() + " - " + message.getText());
            }

            holder.tvTime.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(conversation.getLastUpdatedAt()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvSubtitle;
        private TextView tvTime;
        private ImageView ivLogo;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivLogo = itemView.findViewById(R.id.iv_logo);
        }
    }


    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }
}
