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
import com.nerderylabs.android.nerdalert.ui.adapter.RecyclerViewAdapter;
import com.nerderylabs.android.nerdalert.ui.widget.EmptyRecyclerView;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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

        EmptyRecyclerView nerdRecyclerView = (EmptyRecyclerView) v.findViewById(R.id.nerd_recycler);
        nerdRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        nerdRecyclerView.setItemAnimator(new DefaultItemAnimator());
        nerdRecyclerView.setEmptyView(v.findViewById(android.R.id.empty));
        nerdAdapter = new RecyclerViewAdapter(getContext(), nerdList, R.layout.neighbor_card);
        nerdRecyclerView.setAdapter(nerdAdapter);

        return v;
    }

    public void addNeighbor(final Neighbor neighbor) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // don't add duplicates
                if(!nerdList.contains(neighbor)) {
                    nerdList.add(neighbor);
                    nerdAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void removeNeighbor(final Neighbor neighbor) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nerdList.remove(neighbor);
                nerdAdapter.notifyDataSetChanged();
            }
        });
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
