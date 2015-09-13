package com.nerderylabs.android.nerdalert.ui.fragment;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    public static <T extends android.support.v4.app.Fragment> T newInstance(Class<T> fragmentType) {
        try {
            return fragmentType.newInstance();
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }
}
