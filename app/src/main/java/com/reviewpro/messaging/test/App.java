package com.reviewpro.messaging.test;

import android.app.Application;

import io.smooch.core.Smooch;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Smooch.init(this);
    }
}
