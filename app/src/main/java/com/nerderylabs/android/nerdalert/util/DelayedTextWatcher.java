package com.nerderylabs.android.nerdalert.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class DelayedTextWatcher implements TextWatcher{
    private static final String TAG = DelayedTextWatcher.class.getSimpleName();

    private static final int DELAY_IN_MS = 500;

    long lastTimeTextChanged = 0;

    private Callback callback;

    public DelayedTextWatcher(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //nothing
    }

    @Override
    public void afterTextChanged(Editable s) {

        long currentTime = System.currentTimeMillis();
        long delta = currentTime - lastTimeTextChanged;

        // Perform the callback after the delay, as opposed to after every text change
        if(delta > DELAY_IN_MS) {
            Log.d(TAG, "time delta: " + delta);
            lastTimeTextChanged = currentTime;
            callback.afterTextChanged(s);
        }
    }

    public interface Callback {
        void afterTextChanged(Editable editableText);
    }
}
