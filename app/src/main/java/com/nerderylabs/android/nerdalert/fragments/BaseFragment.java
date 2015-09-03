package com.nerderylabs.android.nerdalert.fragments;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    public static <T extends android.support.v4.app.Fragment> T newInstance(Class<T> fragmentType) {
        try {
            return fragmentType.newInstance();
        } catch (Exception e) {
            //NOTE: Intentionally do nothing
        }

        return null;
    }
}
