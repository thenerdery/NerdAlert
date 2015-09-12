package com.nerderylabs.android.nerdalert.fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Strategy;

import com.nerderylabs.android.nerdalert.Constants;
import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.adapter.TabsPagerAdapter;
import com.nerderylabs.android.nerdalert.model.Neighbor;
import com.nerderylabs.android.nerdalert.settings.Settings;
import com.nerderylabs.android.nerdalert.widget.DelayedTextWatcher;
import com.nerderylabs.android.nerdalert.widget.NoSwipeViewPager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    public static final Strategy MESSAGE_STRATEGY = new Strategy.Builder().setTtlSeconds(Constants.TTL_IN_SECONDS).build();

    View view;

    FloatingActionButton fab;

    GoogleApiClient googleApiClient;

    Neighbor myInfo = new Neighbor();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use a retained fragment to avoid re-publishing or re-subscribing upon orientation changes
        setRetainInstance(true);

        // setup the Google API Client, requesting access to the Nearby Messages API
        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getContext(),
                getActivity().getSupportFragmentManager());
        NoSwipeViewPager viewPager = (NoSwipeViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(tabsPagerAdapter);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        initializeTextInputs();

        initializeFab();

        return view;
    }

    private void initializeTextInputs() {
        final EditText nameEditText = (EditText) view.findViewById(R.id.my_name);
        final EditText taglineEditText = (EditText) view.findViewById(R.id.my_tagline);

        nameEditText.setText(Settings.getName(getContext()));
        taglineEditText.setText(Settings.getTagline(getContext()));

        // submit buttons are for lamers...
        DelayedTextWatcher watcher = new DelayedTextWatcher(new DelayedTextWatcher.Callback() {
            @Override
            public void afterTextChanged(Editable editableText) {
                if(nameEditText.getEditableText() == editableText) {
                    myInfo.name = editableText.toString();
                    Settings.setName(getContext(), myInfo.name);
                } else if(taglineEditText.getEditableText() == editableText) {
                    myInfo.tagline = editableText.toString();
                    Settings.setTagline(getContext(), myInfo.tagline);
                }
                Log.d(TAG, "myInfo: " + myInfo.toJson());
            }
        });

        nameEditText.addTextChangedListener(watcher);
        taglineEditText.addTextChangedListener(watcher);
    }

    private void initializeFab() {
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            boolean state = false;

            @Override
            public void onClick(View v) {
                state = !state;
                if (state) {
                    startSpinner();
                    publish();
                    subscribe();
                } else {
                    unsubscribe();
                    unpublish();
                    stopSpinner();
                }
            }
        });
    }

    private void startSpinner() {
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_nearby_spinner));
    }

    private void stopSpinner() {
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_nearby));
    }


    public void publishAndSubscribe() {
        publish();
        subscribe();
    }


    private void publish() {

    }

    private void unpublish() {

    }

    private void subscribe() {

    }

    private void unsubscribe() {

    }

    @Override
    public void onStart() {
        super.onStart();
        if(!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        unsubscribe();
        unpublish();
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Google API Client connected");
        Nearby.Messages.getPermissionStatus(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });


    }

    @Override
    public void onConnectionSuspended(int i) {
        String text1 = getString(R.string.google_api_connection_suspended);
        String text2 = "";
        switch(i) {
            case CAUSE_NETWORK_LOST:
                text2 = getString(R.string.google_api_network_lost);
                break;
            case CAUSE_SERVICE_DISCONNECTED:
                text2 = getString(R.string.google_api_service_disconnected);
                break;
            default:
                text2 = getString(R.string.google_api_unknown);
                break;
        }
        String error = text1 + ": " + text2;
        Log.w(TAG, error);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        String error = getString(R.string.google_api_connection_failed);
        Log.e(TAG, error);
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }


}

