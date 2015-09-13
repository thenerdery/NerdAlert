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

import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.ui.fragment.MainFragment;
import com.nerderylabs.android.nerdalert.ui.fragment.NerdFragment;
import com.nerderylabs.android.nerdalert.model.Neighbor;
import com.nerderylabs.android.nerdalert.settings.Settings;
import com.nerderylabs.android.nerdalert.util.NearbyApiUtil;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
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

    GoogleApiClient googleApiClient;

    // Tracks if we are currently resolving an error related to Nearby permissions. Used to avoid
    // duplicate Nearby permission dialogs if the user initiates both subscription and publication
    // actions without having opted into Nearby.
    public boolean resolvingNearbyPermissionError = false;

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
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        String error = getString(R.string.google_api_connection_failed);
        Log.e(TAG, error);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NearbyApiUtil.REQUEST_RESOLVE_ERROR) {
            resolvingNearbyPermissionError = false;
            if(resultCode == RESULT_OK) {
                publish();
                subscribe();
            } else if(resultCode == RESULT_CANCELED) {
                Log.w(TAG, "User denied requested permissions.");
                Toast.makeText(this, getString(R.string.permissions_required), Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "Failed to resolve error with code=" + resultCode);
            }
        }
    }

    private void publish() {
        if(publishedInfo != null) {
            publish(publishedInfo);
        }
    }

    @Override
    public void publish(Neighbor myInfo) {
        Log.d(TAG, "publish( " + myInfo.toJson() + " )");

        publishedInfo = myInfo;

        Settings.setPublishing(this, true);

        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            // finally, the part that actually uses the API we're demoing...
            Message message = NearbyApiUtil.newNearbyMessage(this, myInfo.toJson());
            PendingResult<Status> result = Nearby.Messages.publish(googleApiClient, message,
                    NearbyApiUtil.MESSAGE_STRATEGY);
            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        // we're done publishing!
                        Log.i(TAG, "Nearby publish successful");
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
        Log.d(TAG, "unpublish( " + myInfo.toJson() + " )");

        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Message message = NearbyApiUtil.newNearbyMessage(this, myInfo.toJson());
            PendingResult<Status> result = Nearby.Messages.unpublish(googleApiClient, message);
            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
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
            Nearby.Messages.subscribe(googleApiClient, messageListener,
                    NearbyApiUtil.MESSAGE_STRATEGY)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "Nearby subscribe successful");
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
                        public void onResult(Status status) {
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
                    status.startResolutionForResult(this,
                            NearbyApiUtil.REQUEST_RESOLVE_ERROR);

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
                String parsedMessage = NearbyApiUtil.parseNearbyMessage(message);
                Log.d(TAG, "Message Found: " + parsedMessage);

                // magical way get the Nerds viewpager fragment
                NerdFragment nerdFragment = (NerdFragment) findViewPagerFragment(R.id.viewpager, 0);

                if(nerdFragment != null) {
                    Neighbor neighbor = Neighbor.fromJson(parsedMessage);
                    nerdFragment.addNeighbor(neighbor);
                }
            }

            @Override
            public void onLost(Message message) {
                // lost message
                String parsedMessage = NearbyApiUtil.parseNearbyMessage(message);
                Log.d(TAG, "Message Lost: " + parsedMessage);

                // magical way get the Nerds viewpager fragment
                NerdFragment nerdFragment = (NerdFragment) findViewPagerFragment(R.id.viewpager, 0);

                if (nerdFragment != null) {
                    Neighbor neighbor = Neighbor.fromJson(parsedMessage);
                    nerdFragment.removeNeighbor(neighbor);
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

