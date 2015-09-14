package com.nerderylabs.android.nerdalert.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings {

    private static final String TAG = Settings.class.getSimpleName();

    private static final String NAME_KEY = TAG + "_name";

    private static final String TAGLINE_KEY = TAG + "_tagline";

    private static final String PUBLISHING_KEY = TAG + "_publishing";

    private static final String SUBSCRIBING_KEY = TAG + "_subscribing";

    private Settings() {
        // static class, no constructor
    }

    public static void setName(Context context, String name) {
        Log.d(TAG, "setName(" + name + ")");
        persistString(context, NAME_KEY, name);
    }

    public static void setTagline(Context context, String tagline) {
        Log.d(TAG, "setTagline(" + tagline + ")");
        persistString(context, TAGLINE_KEY, tagline);
    }

    public static void setPublishing(Context context, boolean publishing) {
        Log.d(TAG, "setPublishing(" + publishing + ")");
        persistBoolean(context, PUBLISHING_KEY, publishing);
    }

    public static void setSubscribing(Context context, boolean subscribing) {
        Log.d(TAG, "setSubscribing(" + subscribing + ")");
        persistBoolean(context, SUBSCRIBING_KEY, subscribing);
    }

    public static String getName(Context context) {
        return retrieveString(context, NAME_KEY);
    }

    public static String getTagline(Context context) {
        return retrieveString(context, TAGLINE_KEY);
    }

    public static boolean isPublishing(Context context) {
        return retrieveBoolean(context, PUBLISHING_KEY);
    }

    public static boolean isSubscribing(Context context) {
        return retrieveBoolean(context, SUBSCRIBING_KEY);
    }

    private static void persistString(Context context, String key, String value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static void persistBoolean(Context context, String key, boolean value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static String retrieveString(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    private static boolean retrieveBoolean(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }
}
