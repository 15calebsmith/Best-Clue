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

    Card(int id) {
        this.cardType = getCardTypeFromId(id);
        this.id = id;
    }

    Card(Parcel in) {
        cardType = in.readString();
        id = in.readInt();
    }

    private static String getCardTypeFromId(int id) {
        if (id < SUSPECTS.length) {
            return SUSPECT;
        } else if (id < (SUSPECTS.length + WEAPONS.length)) {
            return WEAPON;
        } else if (id < (getCardCount())) {
            return ROOM;
        } else {
            return NONE;
        }
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

    static int getCardCount() {
        return SUSPECTS.length + WEAPONS.length + ROOMS.length;
    }
    static CharSequence[] getCards() {
        String[] cards = new String[getCardCount()];
        System.arraycopy(SUSPECTS, 0, cards, 0, SUSPECTS.length);
        System.arraycopy(WEAPONS, 0, cards, SUSPECTS.length, WEAPONS.length);
        System.arraycopy(ROOMS, 0, cards, SUSPECTS.length + WEAPONS.length, ROOMS.length);
        return cards;
    }

    private static final String[] SUSPECTS = {
            "Colonel Mustard",
            "Professor Plum",
            "Mr. Green",
            "Mrs. Peacock",
            "Miss Scarlet",
            "Mrs. White",
    };

    private static final String[] WEAPONS = {
            "Knife",
            "Candlestick",
            "Revolver",
            "Rope",
            "Lead Pipe",
            "Wrench",
    };

    private static final String[] ROOMS = {
            "Hall",
            "Lounge",
            "Dining Hall",
            "Kitchen",
            "Ballroom",
            "Conservatory",
            "Billiard Room",
            "Library",
            "Study",
    };
}
