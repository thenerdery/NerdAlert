package com.nerderylabs.android.nerdalert.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings {

    private static final String TAG = Settings.class.getSimpleName();

    private static final String NAME_KEY = TAG + "_name";

    private static final String TAGLINE_KEY = TAG + "_tagline";

    private static final String PHOTOURL_KEY = TAG + "_photoUrl";

    private Settings() {

    }

    public static void setName(Context context, String name) {
        Log.d(TAG, "setName(" + name + ")");
        persistString(context, NAME_KEY, name);
    }

    public static void setTagline(Context context, String tagline) {
        Log.d(TAG, "setTagline(" + tagline + ")");
        persistString(context, TAGLINE_KEY, tagline);
    }

    public static void setPhotoUrl(Context context, String photoUrl) {
        Log.d(TAG, "setPhotoUrl(" + photoUrl + ")");
        persistString(context, PHOTOURL_KEY, photoUrl);
    }

    public static String getName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(NAME_KEY, "");
    }

    public static String getTagline(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(TAGLINE_KEY, "");
    }

    public static String getPhotoUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PHOTOURL_KEY, "");
    }

    private static void persistString(Context context, String key, String value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
