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
    private static final String UNKNOWN = "UNKNOWN";
    private static final String ERROR = "ERROR";

    static final int NONE_ID = -1;
    static final int UNKNOWN_ID = -2;
    private static final int ERROR_ID = -3;

    private int id;

    Card(int id) {
        this.id = id;
    }

    Card(String cardType, int position) {
        this.id = getIdFromTypeAndPos(cardType, position);
    }

    private Card(Parcel in) {
        id = in.readInt();
    }

    static String getCardTypeFromId(int id) {
        if ((id >= 0) && (id < SUSPECTS.length)) {
            return SUSPECT;
        } else if ((id >= SUSPECTS.length) && (id < (SUSPECTS.length + WEAPONS.length))) {
            return WEAPON;
        } else if ((id >= SUSPECTS.length + WEAPONS.length) && (id < getCardCount())) {
            return ROOM;
        } else if (id == NONE_ID) {
            return NONE;
        } else if (id == UNKNOWN_ID) {
            return UNKNOWN;
        } else {
            Log.e(TAG, "getCardTypeFromId: Unknown card id: " + id);
            return ERROR;
        }
    }

    static int getCardPosFromTypeAndId(String cardType, int id) {
        switch (cardType) {
            case SUSPECT:
                return id;
            case WEAPON:
                return id - SUSPECTS.length;
            case ROOM:
                return id - SUSPECTS.length - WEAPONS.length;
            case NONE:
                return NONE_ID;
            case UNKNOWN:
                return UNKNOWN_ID;
            default:
                Log.e(TAG, "getCardPosFromTypeAndId: Unknown card type:" + cardType);
                return ERROR_ID;
        }
    }

    private int getIdFromTypeAndPos(String cardType, int position) {
        switch (cardType) {
            case SUSPECT:
                return position;
            case WEAPON:
                return position + SUSPECTS.length;
            case ROOM:
                return position + SUSPECTS.length + WEAPONS.length;
            case NONE:
                return NONE_ID;
            case UNKNOWN:
                return UNKNOWN_ID;
            default:
                Log.e(TAG, "getIdFromTypeAndPos: Unknown card type:" + cardType);
                return ERROR_ID;
        }
    }

    String getCardName() {
        switch (getCardType()) {
            case SUSPECT:
                return SUSPECTS[id];
            case WEAPON:
                return WEAPONS[id - SUSPECTS.length];
            case ROOM:
                return ROOMS[id - SUSPECTS.length - WEAPONS.length];
            case NONE:
                return "None";
            case UNKNOWN:
                return "Unknown";
            default:
                Log.e(TAG, "getCardName: Unknown card type:" + getCardType());
                return "Error";
        }
    }

    String getCardType() {
        return getCardTypeFromId(getId());
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Card && ((Card) obj).getId() == id;
    }

    @Override
    public String toString() {
        return getCardName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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

    private static int getLongestCharSequenceLength(CharSequence[] charSequences) {
        int longest = 0;
        for (CharSequence charSequence : charSequences) {
            if (charSequence.length() > longest) {
                longest = charSequence.length();
            }
        }

        return longest;
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

    static class Knowledge implements Parcelable{
        static final int UNKNOWN = 0;
        static final int AVOIDED = 1;
        static final int POSSIBLY_GUILTY = 2;
        static final int GUILTY = 3;
        static final int NOT_GUILTY = 4;
        static final Creator<Knowledge> CREATOR = new Creator<Knowledge>() {
            @Override
            public Knowledge createFromParcel(Parcel in) {
                return new Knowledge(in);
            }

            @Override
            public Knowledge[] newArray(int size) {
                return new Knowledge[size];
            }
        };
        private Card card;
        private int knowledgeLevel;

        Knowledge(Card card, int knowledgeLevel) {
            this.card = card;
            setKnowledgeLevel(knowledgeLevel);
        }

        private Knowledge(Parcel in) {
            card = in.readTypedObject(Card.CREATOR);
            knowledgeLevel = in.readInt();
        }

        int getKnowledgeLevel() {
            return knowledgeLevel;
        }

        void setKnowledgeLevel(int knowledgeLevel) {
            this.knowledgeLevel = knowledgeLevel;
        }

        Card getCard() {
            return card;
        }

        @Override
        public String toString() {
            StringBuilder divider = new StringBuilder();
            divider.append("   ");
            for (int i = card.getCardName().length(); i < getLongestCharSequenceLength(getCards()); i++) {
                divider.append(" ");
            }
            return card.getCardName() + divider.toString() + knowledgeLevelToString();
        }

        String knowledgeLevelToString() {
            switch (knowledgeLevel) {
                case UNKNOWN:
                    return "Unknown";
                case AVOIDED:
                    return "Avoided";
                case POSSIBLY_GUILTY:
                    return "Possibly guilty";
                case GUILTY:
                    return "Guilty";
                case NOT_GUILTY:
                    return "Not guilty";
                default:
                    Log.e(TAG, "knowledgeLevelToString: Unknown knowledge level:" + knowledgeLevel);
                    return "Error";
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedObject(card, flags);
            dest.writeInt(knowledgeLevel);
        }
    }
}
