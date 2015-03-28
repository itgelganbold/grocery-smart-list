package com.example.john.smartlist;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by mshehab on 3/28/15.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "hfqnp2VIY7P7fsF9wwDTrS2VhaHe7dzlIhnf1Pbm", "StNVZjceJslvQchjfdYNg3vEaIY6XW4YImcfDfSf");
    }
}
