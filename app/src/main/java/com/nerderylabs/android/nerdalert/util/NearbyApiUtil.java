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

import com.google.android.gms.iid.InstanceID;

import com.nerderylabs.android.nerdalert.model.Neighbor;

import android.content.Context;

public class NearbyApiUtil {

    private static final String TAG = NearbyApiUtil.class.getSimpleName();

    public static final String TYPE_NERD = "nerd";

    public static final String TYPE_BEACON = "beacon";

    private NearbyApiUtil() {
        // static class
    }

    /* WORKSHOP 007a
    // The Strategy for our Nearby messages.  The defaults are to allow messages to be exchanged
    // over any distance for 5 minutes. Other options are listed here:
    // https://developers.google.com/android/reference/com/google/android/gms/nearby/messages/Strategy
    public static final Strategy MESSAGE_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(Strategy.TTL_SECONDS_DEFAULT)
            .setDiscoveryMode(Strategy.DISCOVERY_MODE_DEFAULT)
            .build();
    */

    /* WORKSHOP 007b
    public static Message newNearbyMessage(Context context, Neighbor payload) {
        Gson gson = new Gson();
        Wrapper wrapper = new Wrapper(context, payload);
        byte[] bytes = gson.toJson(wrapper).getBytes(Charset.forName("UTF-8"));
        return new Message(bytes, TYPE_NERD);
    }
    */

    /* WORKSHOP 007c
    public static Neighbor parseNearbyMessage(Message nearbyMessage) {
        Gson gson = new Gson();
        String string = new String(nearbyMessage.getContent()).trim();
        Wrapper message = gson
                .fromJson(new String(string.getBytes(Charset.forName("UTF-8"))), Wrapper.class);
        if (message == null) {
            Log.w(TAG, "Unable to parse Nearby Message");
            return null;
        } else {
            return message.payload;
        }
    }
    */

    // NearbyApiUtil.Wrapper is a convenience class for wrapping a payload with a Google Play
    // Services instance identifier. This allows the Nearby API to distinguish identical payloads
    // that originate from different devices.
    private static class Wrapper {

        private final String id;

        public final Neighbor payload;

        public Wrapper(Context context, Neighbor payload) {
            this.id = InstanceID.getInstance(context.getApplicationContext()).getId();
            this.payload = payload;
        }
    }
}
