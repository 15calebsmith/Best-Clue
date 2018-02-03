package com.games.csmith.bestclue;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by csmith on 2/3/18.
 */

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    public MainFragment() {
    }

    public static MainFragment newInstance(int sectionNumber) {
        Log.d(TAG, "newInstance: ");
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }
}
