package io.smooch.demoapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import io.smooch.core.Config;
import io.smooch.core.Conversation;
import io.smooch.core.InitializationStatus;
import io.smooch.core.Settings;
import io.smooch.core.Smooch;
import io.smooch.core.SmoochCallback;
import io.smooch.core.SmoochConnectionStatus;

public class ZendeskConversation {

    private final String ZENDESK_INTEGRATION_ID = "61cc66fd3e685a00eb2f6fd0";
    private final String TAG = getClass().getName();

    public void init(Application applicationContext) {
        Smooch.init(applicationContext, new Settings(ZENDESK_INTEGRATION_ID), response -> {
            if (response.getData() == InitializationStatus.SUCCESS) {
                Log.d(TAG, "SUCCESS");
            } else {
                Log.d(TAG, "ERROR");
            }
        });
    }

    public Conversation getConversation() {
        return Smooch.getConversation();
    }

    public Config getConfig() {
        return Smooch.getConfig();
    }

    public Settings getSettings() {
        return Smooch.getSettings();
    }

    public SmoochConnectionStatus getConnectionStatus() {
        return Smooch.getSmoochConnectionStatus();
    }
}
