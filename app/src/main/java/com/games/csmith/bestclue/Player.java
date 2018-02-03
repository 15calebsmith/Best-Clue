package com.games.csmith.bestclue;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by csmith on 2/3/18.
 */

public class Player implements Parcelable {
    private String name;
    
    protected Player(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
}
