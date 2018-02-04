package com.games.csmith.bestclue;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Set;

/**
 * Created by csmith on 2/3/18.
 */

public class Player implements Parcelable {
    private String name;
    private int numberOfCards;
    private Card[] cards;

    Player(String name) {
        this.name = name;
        this.numberOfCards = 0;
        this.cards = new Card[21];
    }

    private Player(Parcel in) {
        name = in.readString();
        numberOfCards = in.readInt();
        cards = in.createTypedArray(Card.CREATOR);
    }

    void setNumberOfCards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    void setCards(Set<Card> cards) {
        for (Card card : cards) {
            this.cards[card.getId()] = card;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(numberOfCards);
        dest.writeTypedArray(cards, 0);
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
