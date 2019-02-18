package com.games.csmith.bestclue;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by csmith on 2/3/18.
 */

public class MainFragment extends BestClueFragment {
    private static final String TAG = "MainFragment";
    private static final String PREDICTIONS_ARRAY_LIST_KEY = "PREDICTIONS_ARRAY_LIST_KEY";
    private View rootView;
    private ArrayAdapter predictionsAdapter;
    private ArrayList<Predictions.Prediction> predictions = new ArrayList<>();


    public static MainFragment newInstance(int gameState, String fragmentTitle, ArrayList<Predictions.Prediction> predictions) {
        MainFragment fragment = new MainFragment();
        fragment.setFragmentTitle(fragmentTitle);
        Bundle args = new Bundle();
        handleNewInstanceGameState(args, gameState);
        handleNewInstanceFragmentTitle(args, fragmentTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(PREDICTIONS_ARRAY_LIST_KEY))) {
            ArrayList<Predictions.Prediction> argPredictions = savedInstanceState.getParcelableArrayList(PREDICTIONS_ARRAY_LIST_KEY);
            predictions.clear();
            predictions.addAll(argPredictions);
            updatePredictions();
        } else if ((getArguments() != null) && (getArguments().containsKey(PREDICTIONS_ARRAY_LIST_KEY))) {
            ArrayList<Predictions.Prediction> argPredictions = getArguments().getParcelableArrayList(PREDICTIONS_ARRAY_LIST_KEY);
            predictions.clear();
            predictions.addAll(argPredictions);
            updatePredictions();
        }
        super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PREDICTIONS_ARRAY_LIST_KEY, predictions);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ((savedInstanceState) != null && (savedInstanceState.containsKey(PREDICTIONS_ARRAY_LIST_KEY))) {
            ArrayList<Predictions.Prediction> argPredictions = savedInstanceState.getParcelableArrayList(PREDICTIONS_ARRAY_LIST_KEY);
            predictions.clear();
            predictions.addAll(argPredictions);
            updatePredictions();
        }
    }

    public void updateKnowledge(Game game) {
        Card.Knowledge[] generatedPredictions = game.generatePredictions();
        predictions.clear();
        predictions.addAll(Predictions.generatePredictions(generatedPredictions));
        updatePredictions();
    }

    private void updatePredictions() {
        if (rootView != null) {
            if (predictionsAdapter == null) {
                predictionsAdapter = new ArrayAdapter<>(rootView.getContext().getApplicationContext(), R.layout.item_prediction, R.id.prediction_text_view, predictions);
            }

            ListView predictionsView = rootView.findViewById(R.id.prediction_list_view);
            if ((predictionsView != null) && (predictionsView.getAdapter() == null)) {
                predictionsView.setAdapter(predictionsAdapter);
            }

            predictionsAdapter.notifyDataSetChanged();
        }
    }

    void handleGameStateChange(int gameState) {
        if (rootView != null) {
            switch (gameState) {
                case Game.GAME_STATE_ADD_PLAYERS:
                    hidePlayingButtons();
                    hidePredictionsList();
                    showSetupButtons();
                    disableStartGameButton();
                    break;
                case Game.GAME_STATE_READY_TO_START:
                    hidePlayingButtons();
                    hidePredictionsList();
                    showSetupButtons();
                    enableStartGameButton();
                    break;
                case Game.GAME_STATE_PLAYING:
                    hideSetupButtons();
                    showPlayingButtons();
                    showPredictionsList();
                    break;
                default:
                    Log.e(TAG, "handleGameStateChange: Unknown game state: " + gameState);
                    break;
            }
        }
    }

    private void showPredictionsList() {
        View predictionsListConstraintLayout = rootView.findViewById(R.id.predictions_list_constraint_layout);
        if (predictionsListConstraintLayout != null) {
            predictionsListConstraintLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hidePredictionsList() {
        View predictionsListConstraintLayout = rootView.findViewById(R.id.predictions_list_constraint_layout);
        if (predictionsListConstraintLayout != null) {
            predictionsListConstraintLayout.setVisibility(View.GONE);
        }
    }

    private void showSetupButtons() {
        Button addPlayerButton = rootView.findViewById(R.id.add_player_button);
        if (addPlayerButton != null) {
            addPlayerButton.setVisibility(View.VISIBLE);
        }
        Button startGameButton = rootView.findViewById(R.id.start_game_button);
        if (startGameButton != null) {
            startGameButton.setVisibility(View.VISIBLE);
        }
    }

    private void hideSetupButtons() {
        Button addPlayerButton = rootView.findViewById(R.id.add_player_button);
        if (addPlayerButton != null) {
            addPlayerButton.setVisibility(View.GONE);
        }
        Button startGameButton = rootView.findViewById(R.id.start_game_button);
        if (startGameButton != null) {
            startGameButton.setVisibility(View.GONE);
        }
    }

    private void enableStartGameButton() {
        Button startGameButton = rootView.findViewById(R.id.start_game_button);
        if (startGameButton != null) {
            startGameButton.setEnabled(true);
        }
    }

    private void disableStartGameButton() {
        Button startGameButton = rootView.findViewById(R.id.start_game_button);
        if (startGameButton != null) {
            startGameButton.setEnabled(false);
        }
    }

    private void showPlayingButtons() {
        Button endGameButton = rootView.findViewById(R.id.end_game_button);
        if (endGameButton != null) {
            endGameButton.setVisibility(View.VISIBLE);
        }
        Button newTurnButton = rootView.findViewById(R.id.new_turn_button);
        if (newTurnButton != null) {
            newTurnButton.setVisibility(View.VISIBLE);
        }
    }

    private void hidePlayingButtons() {
        Button endGameButton = rootView.findViewById(R.id.end_game_button);
        if (endGameButton != null) {
            endGameButton.setVisibility(View.GONE);
        }
        Button newTurnButton = rootView.findViewById(R.id.new_turn_button);
        if (newTurnButton != null) {
            newTurnButton.setVisibility(View.GONE);
        }
    }
}
