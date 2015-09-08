package com.nerderylabs.android.nerdalert.activity;

import com.nerderylabs.android.nerdalert.Constants;
import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.fragments.MainFragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String MAIN_FRAGMENT_TAG = TAG + "_main_fragment_tag";

    private boolean resolvingErrorFlag;

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the navigation bar color on Lollipop+ devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_primary_dark));
        }

        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        mainFragment = (MainFragment) fm.findFragmentByTag(MAIN_FRAGMENT_TAG);

        if(mainFragment == null) {
            mainFragment = new MainFragment();
            fm.beginTransaction().add(R.id.container, mainFragment, MAIN_FRAGMENT_TAG).commit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.REQUEST_RESOLVE_ERROR) {
            resolvingErrorFlag = false;
            if(resultCode == RESULT_OK) {
                if(mainFragment != null) {
                    mainFragment.publishAndSubscribe();
                }
            } else {
                Log.e(TAG, "Failed to resolve error with code=" + resultCode);
            }
        }

    }
}
