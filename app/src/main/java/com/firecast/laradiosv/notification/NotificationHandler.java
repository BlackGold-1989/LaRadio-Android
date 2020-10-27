package com.firecast.laradiosv.notification;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

public class NotificationHandler extends Application {

    private static FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }

}