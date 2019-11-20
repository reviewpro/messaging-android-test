package com.reviewpro.messaging.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.smooch.core.Settings;
import io.smooch.core.Smooch;
import io.smooch.core.SmoochCallback;
import io.smooch.core.User;
import io.smooch.ui.ConversationActivity;

import static android.content.ContentValues.TAG;
import static io.jsonwebtoken.JwsHeader.KEY_ID;

public class MainActivity extends AppCompatActivity {
    public String conversationText = "";

    public Boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText appIdText = findViewById(R.id.appId);
        final EditText userIdText = findViewById(R.id.userId);
        final EditText idText = findViewById(R.id.id);
        final EditText secretText = findViewById(R.id.secret);
        final EditText properties = findViewById(R.id.properties);
        final Button connectButton = findViewById(R.id.connectButton);
        final Button goCharButton = findViewById(R.id.goCharButton);

        appIdText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    connectButton.setEnabled(false);
                } else {
                    connectButton.setEnabled(true);
                }
            }
        });

        if (!appIdText.getText().toString().isEmpty()) {
            connectButton.setEnabled(true);
        }

        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!connected) {
                    Smooch.init(
                        getApplication(),
                        new Settings(appIdText.getText().toString()),
                        new SmoochCallback() {
                            @Override
                            public void run(Response response) {
                                if (response.getError() == null) {
                                    String message = "Init succeed";
                                    Log.d(TAG, message);
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                    if (
                                        !userIdText.getText().toString().isEmpty()
                                        && !idText.getText().toString().isEmpty()
                                        && !secretText.getText().toString().isEmpty()
                                    ) {
                                        String jwt = Jwts.builder()
                                            .claim("scope", "appUser")
                                            .claim("userId", userIdText.getText().toString())
                                            .setHeaderParam(KEY_ID, idText.getText().toString())
                                            .signWith(
                                                Keys.hmacShaKeyFor(secretText.getText().toString().getBytes()),
                                                SignatureAlgorithm.HS256
                                            )
                                            .compact();

                                        Smooch.login(userIdText.getText().toString(), jwt, new SmoochCallback() {
                                            @Override
                                            public void run(Response response) {
                                                if (response.getError() == null) {
                                                    String message = "Login " + userIdText.getText().toString() + " succeed";
                                                    Log.d(TAG, message);
                                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                                    appIdText.setEnabled(false);
                                                    userIdText.setEnabled(false);
                                                    idText.setEnabled(false);
                                                    secretText.setEnabled(false);
                                                    properties.setEnabled(false);

                                                    goCharButton.setVisibility(View.VISIBLE);

                                                    connectButton.setText(getString(R.string.disconnect));

                                                    connected = true;

                                                    if (!properties.getText().toString().isEmpty()) {
                                                        addProperties(properties.getText().toString());
                                                    }

                                                    ConversationActivity.show(getApplicationContext());
                                                } else {
                                                    String message = "Login " + userIdText.getText().toString() + " fails: " + response.getError();
                                                    Log.d(TAG, message);
                                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        if (!properties.getText().toString().isEmpty()) {
                                            addProperties(properties.getText().toString());
                                        }
                                    }
                                } else {
                                    String message = "Init fails: " + response.getError();
                                    Log.d(TAG, message);
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                } else {
                    Smooch.logout(new SmoochCallback() {
                        @Override
                        public void run(Response response) {
                            if (response.getError() == null) {
                                String message  = "Logout " + userIdText.getText().toString() + " succeed";
                                Log.d(TAG, message);
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                appIdText.setEnabled(true);
                                userIdText.setEnabled(true);
                                idText.setEnabled(true);
                                secretText.setEnabled(true);
                                properties.setEnabled(true);

                                goCharButton.setVisibility(View.GONE);

                                connectButton.setText(getString(R.string.connect));

                                connected = false;
                                conversationText = "";
                            } else {
                                String message = "Logout " + userIdText.getText().toString() + " fails: " + response.getError();
                                Log.d(TAG, message);
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        goCharButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConversationActivity.show(getApplicationContext());
            }
        });
    }

    public void addProperties(String properties) {
        User user = User.getCurrentUser();

        String[] props = properties.split(",");
        final Map<String, Object> customProperties = new HashMap<>();

        for (String prop : props) {
            String key = prop.split(":")[0];
            String value = prop.split(":")[1];

            customProperties.put(key, value);
        }

        user.addProperties(customProperties);

        // Identify user with default properties
        user.setSignedUpAt(new Date());
    }
}
