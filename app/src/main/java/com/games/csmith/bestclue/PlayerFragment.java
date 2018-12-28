package com.games.csmith.bestclue;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by csmith on 2/3/18.
 */

public class PlayerFragment extends BestClueFragment {
    private static final String TAG = "PlayerFragment";
    private static final String PLAYER_KEY = "PLAYER_KEY";
    private Player player;
    private View rootView;
    private ArrayAdapter cardsAdapter;
    private ArrayList<Card> cards = new ArrayList<>();


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
        rootView = inflater.inflate(R.layout.fragment_player, container, false);
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
        switch (gameState) {
            case Game.GAME_STATE_ADD_PLAYERS:
                hidePlayerCardsTitle();
                hidePlayerCardsList();
                break;
            case Game.GAME_STATE_READY_TO_START:
                hidePlayerCardsTitle();
                hidePlayerCardsList();
                break;
            case Game.GAME_STATE_PLAYING:
                showPlayerCardsTitle();
                showPlayerCardsList();
                break;
            default:
                Log.e(TAG, "handleGameStateChange: Unknown game state: " + gameState);
                break;
        }
    }

    private void hidePlayerCardsList() {
        View playerCardsList = rootView == null ? null : rootView.findViewById(R.id.player_cards_list_view);
        if (playerCardsList != null) {
            playerCardsList.setVisibility(View.GONE);
        }
    }

    private void showPlayerCardsList() {
        View playerCardsList = rootView == null ? null : rootView.findViewById(R.id.player_cards_list_view);
        if (playerCardsList != null) {
            playerCardsList.setVisibility(View.VISIBLE);
        }
    }

    private void hidePlayerCardsTitle() {
        View playerCardsList = rootView == null ? null : rootView.findViewById(R.id.player_cards_title);
        if (playerCardsList != null) {
            playerCardsList.setVisibility(View.GONE);
        }
    }

    private void showPlayerCardsTitle() {
        View playerCardsList = rootView == null ? null : rootView.findViewById(R.id.player_cards_title);
        if (playerCardsList != null) {
            playerCardsList.setVisibility(View.VISIBLE);
        }
    }

    public void updateKnowledge(Game game) {
        Log.d(TAG, "updateKnowledge: " + getFragmentTitle() + ": " + player);
        updateCards();
    }

    private void updateCards() {
        Log.d(TAG, "updateCards: " + getFragmentTitle() + ": " + player);
        if (rootView != null) {
            cards.clear();
            cards.addAll(Arrays.asList(player.getCards()));

            if (cardsAdapter == null) {
                cardsAdapter = new ArrayAdapter<>(rootView.getContext().getApplicationContext(), R.layout.item_prediction, R.id.prediction_text_view, cards);
            }

            ListView cardsView = rootView == null ? null : (ListView) rootView.findViewById(R.id.player_cards_list_view);
            if ((cardsView != null) && (cardsView.getAdapter() == null)) {
                cardsView.setAdapter(cardsAdapter);
            }

            cardsAdapter.notifyDataSetChanged();
        }
    }
}
