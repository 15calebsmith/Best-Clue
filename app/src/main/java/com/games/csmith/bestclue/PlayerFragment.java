package com.games.csmith.bestclue;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by csmith on 2/3/18.
 */

public class PlayerFragment extends Fragment{
    private static final String TAG = "PlayerFragment";

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance(int sectionNumber) {
        Log.d(TAG, "newInstance: ");
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        return rootView;
    }

}
