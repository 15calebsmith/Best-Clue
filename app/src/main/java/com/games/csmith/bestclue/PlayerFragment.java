package com.games.csmith.bestclue;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by csmith on 2/3/18.
 */

public class PlayerFragment extends BestClueFragment {
    private static final String TAG = "PlayerFragment";
    private static final String PLAYER_KEY = "PLAYER_KEY";
    private Player player;


    public static PlayerFragment newInstance(int gameState, Player player) {
        PlayerFragment fragment = new PlayerFragment();
        fragment.setFragmentTitle(player.getName());
        Bundle args = new Bundle();
        handleNewInstanceGameState(args, gameState);
        handleNewInstanceFragmentTitle(args, player.getName());
        args.putParcelable(PLAYER_KEY, player);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(PLAYER_KEY))) {
            Log.d(TAG, "onCreateView: 1");
            player = savedInstanceState.getParcelable(PLAYER_KEY);
        } else if ((getArguments() != null) && (getArguments().containsKey(PLAYER_KEY))) {
            Log.d(TAG, "onCreateView: 2");
            player = getArguments().getParcelable(PLAYER_KEY);
        }
        super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(PLAYER_KEY, player);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(PLAYER_KEY))) {
            player = savedInstanceState.getParcelable(PLAYER_KEY);
        }
    }

    @Override
    void handleGameStateChange(int gameState) {
        Log.d(TAG, "handleGameStateChange: " + getFragmentTitle() + ": " + player);
    }

    public void updateKnowledge(Game game) {
        Log.d(TAG, "updateKnowledge: " + getFragmentTitle() + ": " + player);
    }
}
