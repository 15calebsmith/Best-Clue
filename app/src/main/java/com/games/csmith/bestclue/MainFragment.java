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

/**
 * Created by csmith on 2/3/18.
 */

public class MainFragment extends BestClueFragment {
    private static final String TAG = "MainFragment";
    private static final String PREDICTIONS_ARRAY_LIST_KEY = "PREDICTIONS_ARRAY_LIST_KEY";
    private ArrayAdapter predictionsAdapter;
    private ArrayList<Prediction> predictions = new ArrayList<>();


    public static MainFragment newInstance(int gameState, String fragmentTitle, ArrayList<Prediction> predictions) {
        MainFragment fragment = new MainFragment();
        fragment.setFragmentTitle(fragmentTitle);
        Bundle args = new Bundle();
        handleNewInstanceGameState(args, gameState);
        handleNewInstanceFragmentTitle(args, fragmentTitle);
        handleNewInstancePredictionsList(args, predictions);
        fragment.setArguments(args);
        return fragment;
    }

    static void handleNewInstancePredictionsList(Bundle args, ArrayList<Prediction> predictions) {
        args.putParcelableArrayList(PREDICTIONS_ARRAY_LIST_KEY, predictions);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(PREDICTIONS_ARRAY_LIST_KEY))) {
            ArrayList<Prediction> argPredictions = savedInstanceState.getParcelableArrayList(PREDICTIONS_ARRAY_LIST_KEY);
            predictions.clear();
            predictions.addAll(argPredictions);
            updatePredictions();
        } else if ((getArguments() != null) && (getArguments().containsKey(PREDICTIONS_ARRAY_LIST_KEY))) {
            ArrayList<Prediction> argPredictions = getArguments().getParcelableArrayList(PREDICTIONS_ARRAY_LIST_KEY);
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
            ArrayList<Prediction> argPredictions = savedInstanceState.getParcelableArrayList(PREDICTIONS_ARRAY_LIST_KEY);
            predictions.clear();
            predictions.addAll(argPredictions);
            updatePredictions();
        }
    }

    public void updateKnowledge(Game game) {
        Card.Knowledge[] generatedPredictions = game.generatePredictions();
        predictions.clear();
        predictions.addAll(Prediction.Utils.generatePredictions(generatedPredictions));
        updatePredictions();
    }

    private void updatePredictions() {
        if (getView() != null) {
            if (predictionsAdapter == null) {
                predictionsAdapter = new ArrayAdapter<>(getView().getContext().getApplicationContext(), R.layout.item_prediction, R.id.prediction_text_view, predictions);
            }

            ListView predictionsView = getView().findViewById(R.id.prediction_list_view);
            if ((predictionsView != null) && (predictionsView.getAdapter() == null)) {
                predictionsView.setAdapter(predictionsAdapter);
            }

            predictionsAdapter.notifyDataSetChanged();
        }
    }

    void handleGameStateChange(int gameState) {
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

    private void showPredictionsList() {
        View predictionsListConstraintLayout = getView() == null ? null : getView().findViewById(R.id.predictions_list_constraint_layout);
        if (predictionsListConstraintLayout != null) {
            predictionsListConstraintLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hidePredictionsList() {
        View predictionsListConstraintLayout = getView() == null ? null : getView().findViewById(R.id.predictions_list_constraint_layout);
        if (predictionsListConstraintLayout != null) {
            predictionsListConstraintLayout.setVisibility(View.GONE);
        }
    }

    private void showSetupButtons() {
        View addPlayerButton = getView() == null ? null : getView().findViewById(R.id.add_player_button);
        if (addPlayerButton != null) {
            addPlayerButton.setVisibility(View.VISIBLE);
        }
        View startGameButton = getView() == null ? null : getView().findViewById(R.id.start_game_button);
        if (startGameButton != null) {
            startGameButton.setVisibility(View.VISIBLE);
        }
    }

    private void hideSetupButtons() {
        View addPlayerButton = getView() == null ? null : getView().findViewById(R.id.add_player_button);
        if (addPlayerButton != null) {
            addPlayerButton.setVisibility(View.GONE);
        }
        View startGameButton = getView() == null ? null : getView().findViewById(R.id.start_game_button);
        if (startGameButton != null) {
            startGameButton.setVisibility(View.GONE);
        }
    }

    private void enableStartGameButton() {
        View startGameButton = getView() == null ? null : getView().findViewById(R.id.start_game_button);
        if (startGameButton != null) {
            startGameButton.setEnabled(true);
        }
    }

    private void disableStartGameButton() {
        View startGameButton = getView() == null ? null : getView().findViewById(R.id.start_game_button);
        if (startGameButton != null) {
            startGameButton.setEnabled(false);
        }
    }

    private void showPlayingButtons() {
        View endGameButton = getView() == null ? null : getView().findViewById(R.id.end_game_button);
        if (endGameButton != null) {
            endGameButton.setVisibility(View.VISIBLE);
        }
        View newTurnButton = getView() == null ? null : getView().findViewById(R.id.new_turn_button);
        if (newTurnButton != null) {
            newTurnButton.setVisibility(View.VISIBLE);
        }
    }

    private void hidePlayingButtons() {
        View endGameButton = getView() == null ? null : getView().findViewById(R.id.end_game_button);
        if (endGameButton != null) {
            endGameButton.setVisibility(View.GONE);
        }
        View newTurnButton = getView() == null ? null : getView().findViewById(R.id.new_turn_button);
        if (newTurnButton != null) {
            newTurnButton.setVisibility(View.GONE);
        }
    }
}
