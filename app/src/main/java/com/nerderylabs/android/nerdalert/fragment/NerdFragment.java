package com.nerderylabs.android.nerdalert.fragment;

import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.adapter.RecyclerViewAdapter;
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

    private RecyclerViewAdapter nerdAdapter;

    private List<Neighbor> nerdList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_nerds, container, false);

        RecyclerView nerdRecyclerView = (RecyclerView) v.findViewById(R.id.nerd_recycler);
        nerdRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        nerdRecyclerView.setItemAnimator(new DefaultItemAnimator());
        nerdAdapter = new RecyclerViewAdapter(getContext(), nerdList, R.layout.neighbor_card);
        nerdRecyclerView.setAdapter(nerdAdapter);

        return v;
    }

    public void addNeighbor(Neighbor neighbor) {

    }

    public void removeNeighbor(Neighbor neighbor) {

    }

    public void clearNeighborList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nerdList.clear();
                nerdAdapter.notifyDataSetChanged();
            }
        });
    }
}
