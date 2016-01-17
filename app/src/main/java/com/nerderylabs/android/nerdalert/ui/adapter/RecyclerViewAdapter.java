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

package com.nerderylabs.android.nerdalert.ui.adapter;

import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.model.Neighbor;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();

    private Context context;

    private List<Neighbor> neighbors;

    private int layout;

    public RecyclerViewAdapter(Context context, List<Neighbor> neighbors, int layout) {
        this.context = context;
        this.neighbors = neighbors;
        this.layout = layout;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

        Neighbor neighbor = neighbors.get(position);
        if(neighbor.getName() != null) {
            holder.name.setText(neighbor.getName());
        }
        if(neighbor.getTagline() != null) {
            holder.tagline.setText(neighbor.getTagline());
        }
        if(neighbor.getBitmap() != null) {
            holder.photo.setImageDrawable(new BitmapDrawable(context.getResources(), neighbor.getBitmap()));
        } else {
            holder.photo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_contact_picture));
        }

    }

    @Override
    public int getItemCount() {
        return neighbors == null ? 0 : neighbors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public TextView tagline;

        public ImageView photo;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.neighbor_name);
            tagline = (TextView) itemView.findViewById(R.id.neighbor_tagline);
            photo = (ImageView) itemView.findViewById(R.id.neighbor_photo);
        }
    }
}
