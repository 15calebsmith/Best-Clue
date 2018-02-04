package com.games.csmith.bestclue;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by csmith on 2/4/18.
 */

class Card implements Parcelable {
    protected static final String SUSPECT = "SUSPECT";
    protected static final String WEAPON = "WEAPON";
    protected static final String ROOM = "ROOM";
    protected static final String NONE = "NONE";

    private String cardType;
    private int id;

    Card(String cardType, int id) {
        this.cardType = cardType;
        this.id = id;
    }

    Card(Parcel in) {
        cardType = in.readString();
        id = in.readInt();
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardType);
        dest.writeInt(id);
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}
