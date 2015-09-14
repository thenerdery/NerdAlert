package com.nerderylabs.android.nerdalert.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileUtil {

    private static final String TAG = ProfileUtil.class.getSimpleName();

    private ProfileUtil() {
        // static class
    }

    public static Pair<String, Bitmap> getUserProfile(Context context) {
        String name = "";
        String photo = "";

        Cursor c = context.getContentResolver()
                .query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        if(c != null && c.moveToFirst()) {
            String[] columnNames = c.getColumnNames();
            for (String columnName : columnNames) {
                String columnValue = c.getString(c.getColumnIndex(columnName));
                if (columnName.equals(ContactsContract.Profile.DISPLAY_NAME)) {
                    if(columnValue != null) {
                        name = columnValue;
                    }
                } else if (columnName.equals(ContactsContract.Profile.PHOTO_THUMBNAIL_URI)) {
                    if(columnValue != null) {
                        photo = columnValue;
                    }
                }
            }
            c.close();
        }

        Log.d(TAG, "name: " + name + " | photo: " + photo);

        Bitmap bitmap = loadBitmapFromUriString(context, photo);

        return new Pair<>(name, bitmap);
    }

    private static Bitmap loadBitmapFromUriString(Context context, String uri) {

        Uri photoUri = Uri.parse(uri);
        try {
            InputStream is = context.getContentResolver().openInputStream(photoUri);
            return BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Unable to decode profile photo bitmap");
        }

        return null;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        return manufacturer + " " + model;
    }

}
