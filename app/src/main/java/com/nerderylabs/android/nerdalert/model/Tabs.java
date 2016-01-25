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

package com.nerderylabs.android.nerdalert.model;

import com.nerderylabs.android.nerdalert.R;

import java.io.Serializable;

public enum Tabs implements Serializable {
    NERDS(0, R.string.title_nerds, R.string.empty_text_nerds, R.drawable.ic_contact_photo),
    BEACONS(1, R.string.title_beacons, R.string.empty_text_beacons, R.drawable.ic_eddystone_logo);

    private final int tabIndex;

    private final int titleStringId;

    private final int emptyViewPagerStringId;

    private final int emptyPhotoDrawableId;

    Tabs(int tabIndex, int titleStringId, int emptyViewPagerStringId, int emptyPhotoDrawableId) {
        this.tabIndex = tabIndex;
        this.titleStringId = titleStringId;
        this.emptyViewPagerStringId = emptyViewPagerStringId;
        this.emptyPhotoDrawableId = emptyPhotoDrawableId;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public int getTitleStringId() {
        return titleStringId;
    }

    public int getEmptyViewPagerStringId() {
        return emptyViewPagerStringId;
    }

    public int getEmptyPhotoDrawableId() {
        return emptyPhotoDrawableId;
    }
}
