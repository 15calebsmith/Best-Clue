package com.games.csmith.bestclue;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by csmith on 2/4/18.
 */

class Card implements Parcelable {
    private static final String TAG = "Card";
    static final String SUSPECT = "SUSPECT";
    static final String WEAPON = "WEAPON";
    static final String ROOM = "ROOM";
    static final String NONE = "NONE";
    static final String ERROR = "ERROR";

    static final int NONE_ID = -1;
    static final int ERROR_ID = -2;

    static final int KNOWLEDGE_UNKNOWN = 0;
    static final int KNOWLEDGE_AVOIDED = 1;
    static final int KNOWLEDGE_POSSIBLY_GUILTY = 2;
    static final int KNOWLEDGE_GUILTY = 3;
    static final int KNOWLEDGE_NOT_GUILTY = 4;

    private String cardType;
    private int id;

    Card(int id) {
        this.cardType = getCardTypeFromId(id);
        this.id = id;
    }

    Card(String cardType, int position) {
        this.cardType = cardType;
        this.id = getIdFromTypeAndPos(cardType, position);
    }

    Card(Parcel in) {
        cardType = in.readString();
        id = in.readInt();
    }

    private static String getCardTypeFromId(int id) {
        if ((id >= 0) && (id < SUSPECTS.length)) {
            return SUSPECT;
        } else if ((id >= SUSPECTS.length) && (id < (SUSPECTS.length + WEAPONS.length))) {
            return WEAPON;
        } else if ((id >= SUSPECTS.length + WEAPONS.length) && (id < getCardCount())) {
            return ROOM;
        } else if (id == NONE_ID) {
            return NONE;
        } else {
            Log.e(TAG, "getCardTypeFromId: Unknown card id: " + id);
            return ERROR;
        }
    }

    private int getIdFromTypeAndPos(String cardType, int position) {
        switch (cardType) {
            case SUSPECT:
                return  position;
            case WEAPON:
                return position + SUSPECTS.length;
            case ROOM:
                return position + SUSPECTS.length + WEAPONS.length;
            case NONE:
                return NONE_ID;
            default:
                Log.e(TAG, "getIdFromTypeAndPos: Unknown card type:" + cardType);
                return ERROR_ID;
        }
    }

    String getCardName() {
        switch (cardType) {
            case SUSPECT:
                return SUSPECTS[id];
            case WEAPON:
                return WEAPONS[id - SUSPECTS.length];
            case ROOM:
                return ROOMS[id - SUSPECTS.length - WEAPONS.length];
            case NONE:
                return "None";
            default:
                Log.e(TAG, "getCardName: Unknown card type:" + cardType);
                return "Error";
        }
    }

    public String getCardType() {
        return cardType;
    }

    static boolean isGameCard(int id) {
        switch (getCardTypeFromId(id)) {
            case SUSPECT:
            case WEAPON:
            case ROOM:
                return true;
            default:
                return false;
        }
    }

    static int getGreatestKnowledge(int k1, int k2) {
        return k1 >= k2 ? k1 : k2;
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

    static CharSequence[] getSuspects() {
        return SUSPECTS;
    }

    static CharSequence[] getWeapons() {
        return WEAPONS;
    }

    static CharSequence[] getRooms() {
        return ROOMS;
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
