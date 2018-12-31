package com.games.csmith.bestclue;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by csmith on 2/3/18.
 */

public class Game implements Parcelable {
    private Context context;
    private ArrayList<Player> players;
    private int gameState;

    static final String ACTION_GAME_STATE_CHANGED = "com.games.csmith.bestclue.ACTION_GAME_STATE_CHANGED";
    static final String ACTION_UPDATE_PREDICTIONS = "com.games.csmith.bestclue.ACTION_UPDATE_PREDICTIONS";
    static final int GAME_STATE_ADD_PLAYERS = 1;
    static final int GAME_STATE_READY_TO_START = 2;
    static final int GAME_STATE_PLAYING = 3;


    Game(Context context) {
        this.context = context.getApplicationContext();
        this.players = new ArrayList<>();
        setGameState(GAME_STATE_ADD_PLAYERS);
    }

    private Game(Parcel in) {
        in.readTypedList(players, Player.CREATOR);
        gameState = in.readInt();
    }

    synchronized private void setGameState(int newState) {
        if (gameState != newState) {
            gameState = newState;
            context.sendBroadcast(new Intent(ACTION_GAME_STATE_CHANGED));
            context.sendBroadcast(new Intent(ACTION_UPDATE_PREDICTIONS));
        }
    }

    synchronized int getGameState() {
        return gameState;
    }

    void addPlayer(Player player) {
        players.add(player);

        if (players.size() >= 2) {
            setGameState(GAME_STATE_READY_TO_START);
        }
    }

    ArrayList<Player> getPlayers() {
        return players;
    }

    boolean containsPlayer(String name) {
        boolean ret = false;
        for (Player player : players) {
            if (player.getName().equals(name)) {
                ret = true;
                break;
            }
        }

        return ret;
    }

    void setPlayersCards(Player player, boolean[] checkedCards) {
        player.setCards(checkedCards);
        if (isGameReadyToStart()) {
            setGameState(GAME_STATE_PLAYING);
        }
    }

    void setPlayersNumberOfCards(Player player, int numberOfCards) {
        player.setNumberOfCards(numberOfCards);
        if (isGameReadyToStart()) {
            setGameState(GAME_STATE_PLAYING);
        }
    }

    private boolean isGameReadyToStart() {
        return allPlayersCardsSet();
    }

    private boolean allPlayersCardsSet() {
        for (Player player : players) {
            if (player.getNumberOfCards() == 0) {
                return false;
            }
        }
        return true;
    }

    void reset() {
        players.clear();
        setGameState(GAME_STATE_ADD_PLAYERS);
    }

    void initializePlayerPredictions() {
        for (Player player : getPlayers()) {
            player.initializeKnowledge(getPlayers());
        }
    }

    void handleQuestion(Player askPlayer, Player answerPlayer, Card suspect, Card weapon, Card room, Card answer) {
        answerPlayer.addCard(answer);
        askPlayer.handleAnswer(answerPlayer, suspect, weapon, room, answer);
        context.sendBroadcast(new Intent(ACTION_UPDATE_PREDICTIONS));

        //TODO: remove once knowledge debugging is finished
        for (Player player : players) {
            player.printDebugKnowledge();
        }
    }

    int[] generatePredictions() {
        int[] totalKnowledge = new int[Card.getCardCount()];
        for (Player player : players) {
            Integer[] playerCardKnowledge = player.getCardKnowledge();
            for (int i = 0; i < playerCardKnowledge.length; i++) {
                int cardKnowledge = playerCardKnowledge[i];
                int currentKnowledge = totalKnowledge[i];
                totalKnowledge[i] = Card.getGreatestKnowledge(currentKnowledge, cardKnowledge);
            }
        }

        return totalKnowledge;
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(players);
        dest.writeInt(gameState);
    }
}
