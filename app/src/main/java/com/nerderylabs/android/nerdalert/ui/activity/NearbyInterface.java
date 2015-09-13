package com.nerderylabs.android.nerdalert.ui.activity;

import com.nerderylabs.android.nerdalert.model.Neighbor;

public interface NearbyInterface {

    void publish(Neighbor myInfo);

    void unpublish(Neighbor myInfo);

    void subscribe();

    void unsubscribe();

}
