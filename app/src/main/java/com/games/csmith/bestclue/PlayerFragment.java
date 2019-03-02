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
    private ArrayAdapter cardsAdapter;
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayAdapter knowledgeAdapter;
    private ArrayList<Card.Knowledge> knowledge = new ArrayList<>();


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
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(PLAYER_KEY))) {
            player = savedInstanceState.getParcelable(PLAYER_KEY);
        } else if ((getArguments() != null) && (getArguments().containsKey(PLAYER_KEY))) {
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
        super.onActivityCreated(savedInstanceState);
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(PLAYER_KEY))) {
            player = savedInstanceState.getParcelable(PLAYER_KEY);
        }
    }

    @Override
    void handleGameStateChange(int gameState) {
        switch (gameState) {
            case Game.GAME_STATE_ADD_PLAYERS:
                hidePlayerCardsTitle();
                hidePlayerKnowledgeTitle();
                break;
            case Game.GAME_STATE_READY_TO_START:
                hidePlayerCardsTitle();
                hidePlayerKnowledgeTitle();
                break;
            case Game.GAME_STATE_PLAYING:
                showPlayerCardsTitle();
                showPlayerKnowledgeTitle();
                break;
            default:
                Log.e(TAG, "handleGameStateChange: Unknown game state: " + gameState);
                break;
        }
    }

    public void handleOnPlayerCardsTitleClick(View view) {
        View cardsTitleView = getView() == null ? null :getView().findViewById(R.id.player_cards_title);
        if (view != null && view.equals(cardsTitleView)) {
            togglePlayerCardsList();
        }
    }

    private void togglePlayerCardsList() {
        View playerCardsListConstraintLayout = getView() == null ? null : getView().findViewById(R.id.player_cards_list_constraint_layout);
        if (playerCardsListConstraintLayout != null) {
            playerCardsListConstraintLayout.setVisibility(playerCardsListConstraintLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        }
    }

    private void hidePlayerCardsTitle() {
        View playerCardsTitle = getView() == null ? null : getView().findViewById(R.id.player_cards_title);
        if (playerCardsTitle != null) {
            playerCardsTitle.setVisibility(View.GONE);
        }
    }

    private void showPlayerCardsTitle() {
        View playerCardsTitle = getView() == null ? null : getView().findViewById(R.id.player_cards_title);
        if (playerCardsTitle != null) {
            playerCardsTitle.setVisibility(View.VISIBLE);
        }
    }

    public void handleOnPlayerKnowledgeTitleClick(View view) {
        View cardsTitleView = getView() == null ? null : getView().findViewById(R.id.player_knowledge_title);
        if (view != null && view.equals(cardsTitleView)) {
            togglePlayerKnowledgeList();
        }
    }

    private void togglePlayerKnowledgeList() {
        View playerKnowledgeListConstraintLayout = getView() == null ? null : getView().findViewById(R.id.player_knowledge_list_constraint_layout);
        if (playerKnowledgeListConstraintLayout != null) {
            playerKnowledgeListConstraintLayout.setVisibility(playerKnowledgeListConstraintLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        }
    }

    private void hidePlayerKnowledgeTitle() {
        View playerKnowledgeTitle = getView() == null ? null : getView().findViewById(R.id.player_knowledge_title);
        if (playerKnowledgeTitle != null) {
            playerKnowledgeTitle.setVisibility(View.GONE);
        }
    }

    private void showPlayerKnowledgeTitle() {
        View playerKnowledgeTitle = getView() == null ? null : getView().findViewById(R.id.player_knowledge_title);
        if (playerKnowledgeTitle != null) {
            playerKnowledgeTitle.setVisibility(View.VISIBLE);
        }
    }

    public void updateKnowledge(Game game) {
        updateCards();
        updateKnowledge();
    }

    private void updateCards() {
        if (getView() != null) {
            cards.clear();
            cards.addAll(Arrays.asList(player.getCards()));

            if (cardsAdapter == null) {
                cardsAdapter = new ArrayAdapter<>(getView().getContext().getApplicationContext(), R.layout.item_prediction, R.id.prediction_text_view, cards);
            }

            ListView cardsView = getView() == null ? null : (ListView) getView().findViewById(R.id.player_cards_list_view);
            if ((cardsView != null) && (cardsView.getAdapter() == null)) {
                cardsView.setAdapter(cardsAdapter);
            }

            cardsAdapter.notifyDataSetChanged();
        }
    }

    private void updateKnowledge() {
        if (getView() != null) {
            knowledge.clear();
            knowledge.addAll(Arrays.asList(player.getCardKnowledge()));

            if (knowledgeAdapter == null) {
                knowledgeAdapter = new ArrayAdapter<>(getView().getContext().getApplicationContext(), R.layout.item_prediction, R.id.prediction_text_view, knowledge);
            }

            ListView knowledgeListView = getView().findViewById(R.id.player_knowledge_list_view);
            if ((knowledgeListView != null) && (knowledgeListView.getAdapter() == null)) {
                knowledgeListView.setAdapter(knowledgeAdapter);
            }

            knowledgeAdapter.notifyDataSetChanged();
        }
    }
}
