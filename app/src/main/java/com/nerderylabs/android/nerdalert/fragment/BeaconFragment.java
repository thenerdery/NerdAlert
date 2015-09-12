package com.nerderylabs.android.nerdalert.fragment;

import com.nerderylabs.android.nerdalert.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BeaconFragment extends BaseFragment {

    private static final String TAG = BeaconFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_beacons, container, false);

        TextView textView = (TextView) v.findViewById(R.id.beacon_text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(getString(R.string.exercise_url));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        return v;
    }

}
