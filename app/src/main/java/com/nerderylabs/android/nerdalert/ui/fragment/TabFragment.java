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

package com.nerderylabs.android.nerdalert.ui.fragment;

import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.model.Neighbor;
import com.nerderylabs.android.nerdalert.model.Tabs;
import com.nerderylabs.android.nerdalert.ui.adapter.RecyclerViewAdapter;
import com.nerderylabs.android.nerdalert.ui.widget.EmptyRecyclerView;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TabFragment extends BaseFragment {

    private static final String TAG = TabFragment.class.getSimpleName();

    public static final String TAB_EXTRA = TAG + "_tab_extra";

    private RecyclerViewAdapter neighborAdapter;

    private final List<Neighbor> neighborList = new ArrayList<>();

    private Tabs tab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (getArguments() != null) {
            tab = (Tabs) getArguments().getSerializable(TAB_EXTRA);
        }

        View v = inflater.inflate(R.layout.fragment_tab, container, false);

        TextView emptyView = (TextView) v.findViewById(android.R.id.empty);
        emptyView.setText(tab.getEmptyViewPagerStringId());

        EmptyRecyclerView recyclerView = (EmptyRecyclerView) v.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setEmptyView(emptyView);
        neighborAdapter = new RecyclerViewAdapter(getContext(), neighborList, tab);
        recyclerView.setAdapter(neighborAdapter);

        return v;
    }

    public void addNeighbor(final Neighbor neighbor) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // don't add duplicates
                if (!neighborList.contains(neighbor)) {
                    neighborList.add(neighbor);
                    neighborAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void removeNeighbor(final Neighbor neighbor) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                neighborList.remove(neighbor);
                neighborAdapter.notifyDataSetChanged();
            }
        });
    }

    public void clearNeighborList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                neighborList.clear();
                neighborAdapter.notifyDataSetChanged();
            }
        });
    }
}
