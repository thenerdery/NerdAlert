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

package com.nerderylabs.android.nerdalert.ui.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import com.nerderylabs.android.nerdalert.Constants;
import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.model.Neighbor;
import com.nerderylabs.android.nerdalert.settings.Settings;
import com.nerderylabs.android.nerdalert.ui.fragment.MainFragment;
import com.nerderylabs.android.nerdalert.ui.fragment.NerdFragment;
import com.nerderylabs.android.nerdalert.util.NearbyApiUtil;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NearbyInterface, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String MAIN_FRAGMENT_TAG = TAG + "_main_fragment_tag";

    private GoogleApiClient googleApiClient;

    // Tracks if we are currently resolving an error related to Nearby permissions. Used to avoid
    // duplicate Nearby permission dialogs if the user initiates both subscription and publication
    // actions without having opted into Nearby.
    private boolean resolvingNearbyPermissionError = false;

    // The listener that receives new Nearby messages when subscribing
    private MessageListener messageListener;

    private Neighbor publishedInfo = null;

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

        // setup the Google API Client, requesting access to the Nearby Messages API
        // DO NOT use the application context here, otherwise the Nearby API will fail with the
        // following error when publishing/subscribing:
        //    Attempting to perform a high-power operation from a non-Activity Context
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        initializeMessageListener();

        FragmentManager fm = getSupportFragmentManager();
        MainFragment mainFragment = (MainFragment) fm.findFragmentByTag(MAIN_FRAGMENT_TAG);

        if(mainFragment == null) {
            mainFragment = new MainFragment();
            fm.beginTransaction().add(R.id.container, mainFragment, MAIN_FRAGMENT_TAG).commit();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        if(!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        unpublish();
        unsubscribe();
        googleApiClient.disconnect();

        // sometimes the app dies before the callbacks complete, so let's force the
        // unpublish/unsubscribe state so the FAB isn't spinning when the app starts back up.
        Settings.setPublishing(this, false);
        Settings.setSubscribing(this, false);

        super.onStop();
    }

    // If the user has requested a subscription or publication task that requires
    // GoogleApiClient to be connected, we keep track of that task and execute it here, since
    // we now have a connected GoogleApiClient.
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Google API Client connected");

        if(Settings.isPublishing(this)) {
            publish();
        } else {
            unpublish();
        }

        if(Settings.isSubscribing(this)) {
            subscribe();
        } else {
            unsubscribe();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        String text1 = getString(R.string.google_api_connection_suspended);
        String text2;
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
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String error = getString(R.string.google_api_connection_failed);
        Log.e(TAG, error);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.REQUEST_GOOGLE_PLAY_ERROR) {
            resolvingNearbyPermissionError = false;
            if(resultCode == RESULT_OK) {
                publish();
                subscribe();
            } else if(resultCode == RESULT_CANCELED) {
                Log.w(TAG, "User denied requested permissions.");
                Toast.makeText(this, getString(R.string.permission_denied_nearby), Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "Failed to resolve error with code=" + resultCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constants.REQUEST_ASK_PERMISSIONS) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission Granted!");
                MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                fragment.restoreUserInformation();
            } else {
                Log.w(TAG, "Permission Denied!");
                Toast.makeText(this, getString(R.string.permission_denied_contacts), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void publish() {
        if(publishedInfo != null) {
            publish(publishedInfo);
        }
    }

    @Override
    public void publish(Neighbor myInfo) {
        Log.d(TAG, "publish( " + myInfo + " )");

        publishedInfo = myInfo;

        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            // finally, the part that actually uses the API we're demoing...
            Message message = NearbyApiUtil.newNearbyMessage(this, myInfo);

            PublishOptions.Builder builder = new PublishOptions.Builder();
            builder.setStrategy(NearbyApiUtil.MESSAGE_STRATEGY);
            builder.setCallback(new PublishCallback() {
                @Override
                public void onExpired() {
                    Log.i(TAG, "PublishCallback.onExpired(): No longer publishing");
                    Settings.setPublishing(MainActivity.this, false);
                }
            });
            PublishOptions options = builder.build();

            PendingResult<Status> result = Nearby.Messages
                    .publish(googleApiClient, message, options);

            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        // we're done publishing!
                        Log.i(TAG, "Nearby publish successful");
                        Settings.setPublishing(MainActivity.this, true);
                    } else {
                        Log.w(TAG, "Nearby publish unsuccessful");
                        Settings.setPublishing(MainActivity.this, false);
                        handleUnsuccessfulNearbyResult(status);
                    }
                }
            });
        }
    }

    private void unpublish() {
        if(publishedInfo != null) {
            unpublish(publishedInfo);
        }
    }

    @Override
    public void unpublish(Neighbor myInfo) {
        Log.d(TAG, "unpublish( " + myInfo + " )");

        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Message message = NearbyApiUtil.newNearbyMessage(this, myInfo);
            PendingResult<Status> result = Nearby.Messages.unpublish(googleApiClient, message);
            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Log.i(TAG, "Nearby unpublish successful");
                        Settings.setPublishing(MainActivity.this, false);
                    } else {
                        Log.w(TAG, "Nearby unpublish unsuccessful");
                        Settings.setPublishing(MainActivity.this, true);
                        handleUnsuccessfulNearbyResult(status);
                    }
                }
            });
        }
    }

    @Override
    public void subscribe() {
        Log.d(TAG, "subscribe()");

        Settings.setSubscribing(this, true);

        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            SubscribeOptions.Builder builder = new SubscribeOptions.Builder();
            builder.setStrategy(NearbyApiUtil.MESSAGE_STRATEGY);
            builder.setCallback(new SubscribeCallback() {
                @Override
                public void onExpired() {
                    Log.i(TAG, "SubscribeCallback.onExpired(): No longer subscribing");
                    Settings.setSubscribing(MainActivity.this, false);
                }
            });
            SubscribeOptions options = builder.build();

            Nearby.Messages.subscribe(googleApiClient, messageListener, options).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "Nearby subscribe successful");
                                Settings.setSubscribing(MainActivity.this, true);
                            } else {
                                Log.w(TAG, "Nearby subscribe unsuccessful");
                                Settings.setSubscribing(MainActivity.this, false);
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    @Override
    public void unsubscribe() {
        Log.d(TAG, "unsubscribe()");

        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Nearby.Messages.unsubscribe(googleApiClient, messageListener)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "Nearby unsubscribe successful");
                                Settings.setSubscribing(MainActivity.this, false);
                                // clear the list of Neighbors since we're not subscribing anymore
                                NerdFragment nerdFragment = (NerdFragment) findViewPagerFragment(R.id.viewpager, 0);
                                nerdFragment.clearNeighborList();
                            } else {
                                Log.w(TAG, "Nearby unsubscribe unsuccessful");
                                Settings.setSubscribing(MainActivity.this, true);
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    // Handles errors generated when performing a subscription or publication action. Uses
    // Status#startResolutionForResult to display an opt-in dialog to handle the case
    // where a device is not opted into using Nearby.
    private void handleUnsuccessfulNearbyResult(Status status) {
        Log.e(TAG, "processing error, status = " + status);
        if (status.getStatusCode() == NearbyMessagesStatusCodes.APP_NOT_OPTED_IN) {
            if (!resolvingNearbyPermissionError) {
                try {
                    resolvingNearbyPermissionError = true;
                    status.startResolutionForResult(this, Constants.REQUEST_GOOGLE_PLAY_ERROR);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (status.getStatusCode() == ConnectionResult.NETWORK_ERROR) {
                Toast.makeText(this, getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
            } else {
                // To keep things simple, pop a toast for all other error messages.
                Toast.makeText(this.getApplicationContext(), "Unsuccessful: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initializeMessageListener() {
        messageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                // found message
                Log.d(TAG, "Message Found: " + message.getContent().length + " bytes");

                Neighbor neighbor = NearbyApiUtil.parseNearbyMessage(message);
                if(neighbor != null) {

                    // magical way get the Nerds viewpager fragment
                    NerdFragment nerdFragment = (NerdFragment) findViewPagerFragment(R.id.viewpager, 0);

                    if(nerdFragment != null) {
                        Log.d(TAG, "Adding neighbor: " + neighbor);
                        nerdFragment.addNeighbor(neighbor);
                    }
                }

            }

            @Override
            public void onLost(Message message) {
                // lost message
                Log.d(TAG, "Message Lost: " + message.getContent().length + " bytes");

                Neighbor neighbor = NearbyApiUtil.parseNearbyMessage(message);
                if(neighbor != null) {

                    // magical way get the Nerds viewpager fragment
                    NerdFragment nerdFragment = (NerdFragment) findViewPagerFragment(R.id.viewpager, 0);

                    if (nerdFragment != null) {
                        Log.d(TAG, "Removing neighbor: " + neighbor);
                        nerdFragment.removeNeighbor(neighbor);
                    }
                }
            }
        };
    }

    // Fragments added to a ViewPager via the FragmentPagerManager are auto-tagged when
    // instantiated using the private static method FragmentPagerAdapter.makeFragmentName().
    // This method reverses the tag format generated in order to retrieve the fragment.
    private Fragment findViewPagerFragment(int viewPagerId, int index) {
        return getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + viewPagerId + ":" + index);
    }

}

