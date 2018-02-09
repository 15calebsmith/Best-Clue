package com.games.csmith.bestclue;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by csmith on 2/3/18.
 */

public class Game implements Parcelable {
    private ArrayList<Player> players;

    Game() {
        players = new ArrayList<>();
    }

    private Game(Parcel in) {
        in.readTypedList(players, Player.CREATOR);
    }

    void addPlayer(Player player) {
        players.add(player);
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

    boolean hasEnoughPlayersToStart() {
        return players.size() >= 2;
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
    }
}
