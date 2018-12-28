package com.games.csmith.bestclue;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by csmith on 11/25/18.
 */

public abstract class BestClueFragment extends Fragment {
    private static final String GAME_STATE_KEY = "GAME_STATE_KEY";
    private static final String FRAGMENT_TITLE_KEY = "FRAGMENT_TITLE_KEY";
    private int gameState;
    private String fragmentTitle;

    static void handleNewInstanceGameState(Bundle args, int gameState) {
        args.putInt(GAME_STATE_KEY, gameState);
    }

    static void handleNewInstanceFragmentTitle(Bundle args, String fragmentTitle) {
        args.putString(FRAGMENT_TITLE_KEY, fragmentTitle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(GAME_STATE_KEY))) {
            setGameState(savedInstanceState.getInt(GAME_STATE_KEY));
        } else if ((getArguments() != null) && (getArguments().containsKey(GAME_STATE_KEY))) {
            setGameState(getArguments().getInt(GAME_STATE_KEY));
        }
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(FRAGMENT_TITLE_KEY))) {
            setFragmentTitle(savedInstanceState.getString(FRAGMENT_TITLE_KEY));
        } else if ((getArguments() != null) && (getArguments().containsKey(FRAGMENT_TITLE_KEY))) {
            setFragmentTitle(getArguments().getString(FRAGMENT_TITLE_KEY));
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(GAME_STATE_KEY, getGameState());
        outState.putString(FRAGMENT_TITLE_KEY, getFragmentTitle());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(GAME_STATE_KEY))) {
            setGameState(savedInstanceState.getInt(GAME_STATE_KEY));
        }
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(FRAGMENT_TITLE_KEY))) {
            setFragmentTitle(savedInstanceState.getString(FRAGMENT_TITLE_KEY));
        }
    }

    public int getGameState() {
        return gameState;
    }

    void setGameState(int newGameState) {
        gameState = newGameState;
        handleGameStateChange(getGameState());
    }

    public String getFragmentTitle() {
        return fragmentTitle;
    }

    void setFragmentTitle(String newFragmentTitle) {
        fragmentTitle = newFragmentTitle;
    }

    abstract void handleGameStateChange(int gameState);

    abstract void updateKnowledge(Game game);
}
