package com.nerderylabs.android.nerdalert.fragments;

import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.adapters.RecyclerViewAdapter;
import com.nerderylabs.android.nerdalert.model.Neighbor;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class NerdFragment extends BaseFragment {

    private static final String TAG = NerdFragment.class.getSimpleName();

    private RecyclerView nerdRecyclerView;

    private RecyclerViewAdapter nerdAdapter;

    private List<Neighbor> nerdList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_nerds, container, false);

        nerdRecyclerView = (RecyclerView) v.findViewById(R.id.nerd_recycler);
        nerdRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        nerdRecyclerView.setItemAnimator(new DefaultItemAnimator());
        nerdAdapter = new RecyclerViewAdapter(getContext(), nerdList, R.layout.neighbor_card);
        nerdRecyclerView.setAdapter(nerdAdapter);

        return v;
    }

    @Override
    public void onResume() {

        super.onResume();

        nerdList.clear();

        for (int i = 0; i < 100; i++) {
            Neighbor nerd = new Neighbor(i + " Richard Banasiak " + i, "i void warranties...", null);
            nerdList.add(nerd);
        }

        nerdAdapter.notifyDataSetChanged();
    }
}
