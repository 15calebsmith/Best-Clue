package com.games.csmith.bestclue;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by csmith on 11/25/18.
 */

public abstract class BestClueFragment extends Fragment {
    private static final String GAME_STATE_KEY = "GAME_STATE_KEY";
    private static final String FRAGMENT_TITLE_KEY = "FRAGMENT_TITLE_KEY";
    private String fragmentTitle;

    static void handleNewInstanceGameState(Bundle args, int gameState) {
        args.putInt(GAME_STATE_KEY, gameState);
    }

    static void handleNewInstanceFragmentTitle(Bundle args, String fragmentTitle) {
        args.putString(FRAGMENT_TITLE_KEY, fragmentTitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(FRAGMENT_TITLE_KEY, getFragmentTitle());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(FRAGMENT_TITLE_KEY))) {
            fragmentTitle = savedInstanceState.getString(FRAGMENT_TITLE_KEY);
        }
    }

    void setFragmentTitle(String newFragmentTitle) {
        fragmentTitle = newFragmentTitle;
    }

    public String getFragmentTitle() {
        return fragmentTitle;
    }

    abstract void handleGameStateChange(int gameState);

    abstract void updateKnowledge(Game game);
}
