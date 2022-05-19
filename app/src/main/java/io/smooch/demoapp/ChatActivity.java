package io.smooch.demoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import io.smooch.core.CardSummary;
import io.smooch.core.Conversation;
import io.smooch.core.ConversationDelegate;
import io.smooch.core.ConversationEvent;
import io.smooch.core.DisplaySettings;
import io.smooch.core.InitializationStatus;
import io.smooch.core.LoginResult;
import io.smooch.core.LogoutResult;
import io.smooch.core.Message;
import io.smooch.core.MessageAction;
import io.smooch.core.MessageType;
import io.smooch.core.MessageUploadStatus;
import io.smooch.core.PaymentStatus;
import io.smooch.core.Settings;
import io.smooch.core.Smooch;
import io.smooch.core.SmoochConnectionStatus;
import io.smooch.core.model.MessageDto;
import io.smooch.demoapp.adapter.ChatMessageAdapter;
import io.smooch.demoapp.utils.FileUtils;

public class ChatActivity extends AppCompatActivity implements ConversationDelegate {

    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    RecyclerView rvChat;

    EditText edtText;

    ImageView ivSend;

    ImageView ivAttachment;

    ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Smooch.setConversationDelegate(this);

        rvChat = findViewById(R.id.rv_messages);
        edtText = findViewById(R.id.edt_text);
        ivSend = findViewById(R.id.iv_send);
        ivAttachment = findViewById(R.id.iv_attachment);

        ivSend.setOnClickListener(v -> {
            Conversation conversation = Smooch.getConversation();
            if(conversation != null){
                conversation.sendMessage(new Message(edtText.getText().toString()));
                edtText.setText("");
                refreshMessage();
            } else {
                Smooch.createConversation("Novo Chat", "...", null, null, null, null, response -> {
                    Log.d("Response", response.toString());
                });
            }
        });

        ivAttachment.setOnClickListener(v -> {
            showAlert();
        });

        edtText.addTextChangedListener(onTextChange());

        //setup List
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);
        rvChat.setItemAnimator(new DefaultItemAnimator());

        Conversation con = Smooch.getConversation();
        if(con != null){
            adapter = new ChatMessageAdapter(this, con.getMessages());
            rvChat.setAdapter(adapter);
        }
    }

    private void refreshMessage(){
        Conversation con = Smooch.getConversation();
        if(con != null){
            if(adapter != null){
                adapter.setMessageList(con.getMessages());
                adapter.notifyDataSetChanged();
                rvChat.smoothScrollToPosition(rvChat.getAdapter().getItemCount() - 1);
            }
        }
    }

    private TextWatcher onTextChange() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Conversation con = Smooch.getConversation();
                if (s.length() > 0) {
                    if(con != null){
                        con.startTyping();
                    }
                } else {
                    if(con != null){
                        con.stopTyping();
                    }
                }
            }
        };
    }

    @Override
    public void onMessagesReceived(@NonNull Conversation conversation, @NonNull List<Message> list) {
        refreshMessage();
    }

    @Override
    public void onMessagesReset(@NonNull Conversation conversation, @NonNull List<Message> list) {

    }

    @Override
    public void onUnreadCountChanged(@NonNull Conversation conversation, int i) {

    }

    @Override
    public void onMessageSent(@NonNull Message message, @NonNull MessageUploadStatus messageUploadStatus) {
        refreshMessage();
        if(messageUploadStatus == MessageUploadStatus.FAILED)
            Smooch.getConversation().retryMessage(message);
    }

    @Override
    public void onConversationEventReceived(@NonNull ConversationEvent conversationEvent) {
        Log.d("ConversationEvent", conversationEvent.toString());
    }

    @Override
    public void onInitializationStatusChanged(@NonNull InitializationStatus initializationStatus) {

    }

    @Override
    public void onLoginComplete(@NonNull LoginResult loginResult) {

    }

    @Override
    public void onLogoutComplete(@NonNull LogoutResult logoutResult) {

    }

    @Override
    public void onPaymentProcessed(@NonNull MessageAction messageAction, @NonNull PaymentStatus paymentStatus) {

    }

    @Override
    public boolean shouldTriggerAction(@NonNull MessageAction messageAction) {
        return false;
    }

    @Override
    public void onCardSummaryLoaded(@NonNull CardSummary cardSummary) {

    }

    @Override
    public void onSmoochConnectionStatusChanged(@NonNull SmoochConnectionStatus smoochConnectionStatus) {
        Log.d("SmoochConnectionStatus", smoochConnectionStatus.toString());
    }

    @Override
    public void onSmoochShown() {
        Log.d("onSmoochShown", "-");
    }

    @Override
    public void onSmoochHidden() {
        Log.d("onSmoochHidden", "-");
    }

    @Override
    public void onConversationsListUpdated(@NonNull List<Conversation> list) {
        Log.d("onConversationsList", "-");
    }

    private void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Escolha")
                .setCancelable(false)
                .setPositiveButton("Arquivos", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        askPermissions();
                    }
                })
                .setNegativeButton("Câmera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int permisson = ActivityCompat.checkSelfPermission(ChatActivity.this,
                                    Manifest.permission.CAMERA);
                            if (permisson != PackageManager.PERMISSION_GRANTED){
                                requestPermissions(
                                        new String[]{Manifest.permission.CAMERA},
                                        MY_CAMERA_PERMISSION_CODE);
                            }else{
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                            }
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void askPermissions()  {
        // With Android Level >= 23, you have to ask the user
        // for permission to access External Storage.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // Level 23

            // Check if we have Call permission
            int permisson = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permisson != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_PERMISSION
                );
                return;
            }
        }
        this.doBrowseFile();
    }

    private void doBrowseFile()  {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Escolha um arquivo");
        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                    this.doBrowseFile();
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            } case MY_CAMERA_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                else{
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MY_RESULT_CODE_FILECHOOSER:
                if (resultCode == Activity.RESULT_OK ) {
                    if(data != null)  {
                        try {
                            Uri fileUri = data.getData();
                            File f = new File(FileUtils.getPath(this, fileUri));
                            if(f.exists()) {

                                Log.d("FileSize", String.valueOf(f.length()));
                                Log.d("FileName", f.getName());

                                Message m = new Message(f);
                               // m.setMediaUrl(fileUri.getPath());

                                Smooch.getConversation().uploadFile(m, response -> {
                                    Log.d("RESPONSE_UPLOAD_FILE", response.toString());
                                    Smooch.getConversation().retryMessage(response.getData());
                                    refreshMessage();
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case CAMERA_REQUEST:
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Toast.makeText(this, "PHOTO_SIZE: " + photo.getByteCount(), Toast.LENGTH_SHORT).show();
                Smooch.getConversation().uploadImage(new Message(photo), response -> {
                    Log.d("RESPONSE_UPLOAD_IMAGE", response.toString());
                    refreshMessage();
                });
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}