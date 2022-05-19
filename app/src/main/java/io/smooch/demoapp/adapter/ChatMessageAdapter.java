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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;

import io.smooch.core.Message;
import io.smooch.core.MessageType;
import io.smooch.core.MessageUploadStatus;
import io.smooch.demoapp.R;
import io.smooch.demoapp.utils.FileUtils;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MyViewHolder> {
    private Context mContext;
    private List<Message> messageList;

    private boolean isTyping;

    public static final int HEADER = 10;
    public static final int ERROR_OR_FINISH = 20;
    public static final int SELF = 30;
    public static final int OTHER = 40;
    public static final int DOTS = 50;

    public ChatMessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        int baseType = (viewType / 10) * 10;
        int vType = viewType % 10;

        if (baseType == DOTS) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_dots, parent, false);
        } else if (baseType == SELF) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_adapter, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_adapter_other, parent, false);
        }

        return new MyViewHolder(itemView, baseType);
    }

    @Override
    public int getItemViewType(int position) {
        /*
        if (isTyping && position == lastPosition) {
            return DOTS;
        }
         */

        Message message = messageList.get(position);

        return message.isFromCurrentUser() ? SELF : OTHER;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            Message message = messageList.get(position);

            if(message.getText() == null || message.getText().isEmpty())
                holder.tvMessage.setVisibility(View.GONE);
            else
                holder.tvMessage.setVisibility(View.VISIBLE);

            holder.tvMessage.setText(message.getText());
            holder.tvTime.setText(new SimpleDateFormat("HH:mm").format(message.getDate()));
            if(message.isFromCurrentUser()) {
                if (message.getUploadStatus() == MessageUploadStatus.SENT) {
                    holder.ivStatus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_check_chat));
                    holder.ivStatus.setColorFilter(ContextCompat.getColor(mContext, R.color.brand_white));
                } else {
                    holder.ivStatus.setImageDrawable(ContextCompat.getDrawable(mContext, android.R.drawable.ic_menu_recent_history));
                }

                if (message.isRead()) {
                    holder.ivStatus.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_check_chat));
                    holder.ivStatus.setColorFilter(ContextCompat.getColor(mContext, R.color.brand_green));
                }
            } else {
                holder.tvUsername.setText(message.getName());
            }

            if(message.getAvatarUrl() != null && !message.getAvatarUrl().isEmpty()) {
                holder.ivAvatar.setVisibility(View.VISIBLE);
                Glide
                        .with(mContext)
                        .load(message.getAvatarUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .into(holder.ivAvatar);
            } else {
                holder.ivAvatar.setVisibility(View.GONE);
            }

            holder.viewImage.setVisibility(View.GONE);
            holder.viewFile.setVisibility(View.GONE);
            if(message.getMediaUrl() != null){
                if(message.getType().equals(MessageType.IMAGE.getValue())){
                    Glide
                            .with(mContext)
                            .load(message.getAvatarUrl())
                            .centerCrop()
                            .load(message.getMediaUrl())
                            .into(holder.ivChat);

                    holder.viewImage.setVisibility(View.VISIBLE);
                } else if(message.getType().equals(MessageType.FILE.getValue())){
                    holder.viewFile.setVisibility(View.VISIBLE);
                    holder.tvFilename.setText(converFileName(message));
                    holder.tvFileSize.setText(FileUtils.getFormattedSize(String.valueOf(message.getMediaSize())));
                    holder.viewFile.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMediaUrl()));
                        mContext.startActivity(browserIntent);
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private TextView tvTime;
        private ImageView ivStatus;
        private ImageView ivAvatar;
        private View viewImage;
        private LinearLayout viewFile;
        private TextView tvFilename;
        private TextView tvFileSize;
        private ImageView ivChat;
        private TextView tvUsername;

        public MyViewHolder(View itemView, int baseType) {
            super(itemView);

            if (baseType == SELF || baseType == OTHER) {
                tvMessage = itemView.findViewById(R.id.tv_message);
                tvTime = itemView.findViewById(R.id.tv_time);
                ivStatus = itemView.findViewById(R.id.iv_status);
                ivAvatar = itemView.findViewById(R.id.iv_avatar);
                viewImage = itemView.findViewById(R.id.view_image);
                viewFile = itemView.findViewById(R.id.view_file);
                tvFilename = itemView.findViewById(R.id.tv_filename);
                tvFileSize = itemView.findViewById(R.id.tv_file_size);
                ivChat = itemView.findViewById(R.id.iv_chat);
                tvUsername = itemView.findViewById(R.id.tv_username);
            }
        }
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    private String converFileName(Message message){
        String fileName = message.getMediaUrl();

        String[] splitted = fileName.split("/");
        if(splitted.length > 0)
            fileName = splitted[splitted.length - 1];

        if(fileName.length() > 13){
            return fileName.substring(0, 9) + "..." + fileName.substring(fileName.length() - 4);
        } else {
            return fileName;
        }
    }
}
