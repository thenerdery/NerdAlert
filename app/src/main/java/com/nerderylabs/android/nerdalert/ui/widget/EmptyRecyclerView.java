package com.nerderylabs.android.nerdalert.ui.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;


// https://github.com/googlesamples/android-XYZTouristAttractions/blob/master/Application/src/main/java/com/example/android/xyztouristattractions/ui/AttractionsRecyclerView.java
public class EmptyRecyclerView extends RecyclerView{

    private View emptyView;

    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateEmptyView();
        }
    };

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


     // Designate a view as the empty view. When the backing adapter has no
     // data this view will be made visible and the recycler view hidden.
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(mDataObserver);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mDataObserver);
        }
        super.setAdapter(adapter);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (emptyView != null && getAdapter() != null) {
            boolean showEmptyView = getAdapter().getItemCount() == 0;
            emptyView.setVisibility(showEmptyView ? VISIBLE : GONE);
            setVisibility(showEmptyView ? GONE : VISIBLE);
        }
    }

}
