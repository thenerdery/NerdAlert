package com.nerderylabs.android.nerdalert.util;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.gson.Gson;

import com.nerderylabs.android.nerdalert.model.Neighbor;

import android.content.Context;
import android.util.Log;

import java.nio.charset.Charset;

public class NearbyApiUtil {

    private static final String TAG = NearbyApiUtil.class.getSimpleName();

    private NearbyApiUtil(){
        // static class
    }

    // The Strategy for our Nearby messages.  The defaults are to allow messages to be exchanged
    // over any distance for 5 minutes. Other options are listed here:
    // https://developers.google.com/android/reference/com/google/android/gms/nearby/messages/Strategy
    public static final Strategy MESSAGE_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(Strategy.TTL_SECONDS_DEFAULT)
            .setDiscoveryMode(Strategy.DISCOVERY_MODE_DEFAULT)
            .build();

    public static Message newNearbyMessage(Context context, Neighbor payload) {
        Gson gson = new Gson();
        String id = InstanceID.getInstance(context.getApplicationContext()).getId();
        NearbyMessage message = new NearbyMessage(id, payload);
        return new Message(gson.toJson(message).getBytes(Charset.forName("UTF-8")));
    }

    public static Neighbor parseNearbyMessage(Message nearbyMessage) {
        Gson gson = new Gson();
        String string = new String(nearbyMessage.getContent()).trim();
        System.out.println(string);
        NearbyMessage message = gson.fromJson(new String(string.getBytes(Charset.forName("UTF-8"))), NearbyMessage.class);
        if(message == null) {
            Log.w(TAG, "Unable to parse Nearby Message");
            return null;
        } else {
            return message.payload;
        }
    }

    // The NearbyMessage is a convenience class for wrapping a payload with a Google Play Services
    // instance identifier. This allows the Nearby API to distinguish identical payloads that
    // originate from different devices.
    private static class NearbyMessage {
        private String id;
        public Neighbor payload;

        public NearbyMessage(String id, Neighbor payload) {
            this.id = id;
            this.payload = payload;
        }
    }
}
