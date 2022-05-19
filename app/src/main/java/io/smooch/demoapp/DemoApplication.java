package io.smooch.demoapp;

import android.app.Application;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.smooch.core.InitializationStatus;
import io.smooch.core.Settings;
import io.smooch.core.Smooch;
import io.smooch.core.SmoochCallback;
import io.smooch.core.User;

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Below is where you would put your app's android-sdk integrationId to initialize the Smooch class;
        // Find it on the Sunshine Conversations dashboard, or via API: https://docs.smooch.io/rest/#get-sdk-ids
        Smooch.init(this, new Settings("61d5b97dd4fc4800ec8eb2ba"), new SmoochCallback<InitializationStatus>() {
            @Override
            public void run(Response response) {
                Log.d("Smooch-init-Response", response.toString());
                if(response.getData() == InitializationStatus.SUCCESS){
                    Log.d("Smooch-init-status", "SUCCESS");
                } else {
                    Log.d("Smooch-init-status", "ERROR");
                }
            }
        });

        String externalId = "73222178100";

        String jwtUser = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImtpZCI6ImFwcF82MDY0ODYxY2YzMjk5NjAwZDNjNzVlZTgifQ.eyJzY29wZSI6ImFwcFVzZXIiLCJ1c2VySWQiOiI3MzIyMjE3ODEwMCJ9.-1M48TnNdlhNJYjUhFtEVy3ad_ZPmUIT9iKvVUMApiE";

        Smooch.login(externalId, jwtUser, new SmoochCallback() {
            @Override
            public void run(Response response) {
                if (response.getError() == null) {
                    Log.d("Smooch-login-status", "SUCCESS");
                } else {
                    Log.d("Smooch-login-status", "ERROR");
                }
            }
        });
        addSomeProperties(User.getCurrentUser());
    }

    private void addSomeProperties(final User user) {
        final Map<String, Object> customProperties = new HashMap<>();

        // Identify user with default properties
        user.setFirstName("Usuário");
        user.setLastName("Teste Next");
        user.setEmail("gs.montanher@gmail.com");
        user.setSignedUpAt(new Date(1420070400000L));

        // Add your own custom properties
        customProperties.put("Last Seen", new Date());
        customProperties.put("Awesome", true);
        customProperties.put("Karma", 1337);
        user.addMetadata(customProperties);
    }
}
