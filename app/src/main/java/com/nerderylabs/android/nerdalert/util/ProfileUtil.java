/*
 * Copyright (C) 2016 The Nerdery, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nerderylabs.android.nerdalert.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
                } else if (columnName.equals(ContactsContract.Profile.PHOTO_URI)) {
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
