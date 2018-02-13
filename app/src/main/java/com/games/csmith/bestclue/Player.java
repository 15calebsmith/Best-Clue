package com.games.csmith.bestclue;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;

/**
 * Created by csmith on 2/3/18.
 */

public class Player implements Parcelable {
    private static final String TAG = "Player";
    private String name;
    private int numberOfCards;
    private Card[] cards;
    private SparseArray<HashMap<Player, Integer>> cardPlayerKnowledge;

    Player(String name) {
        this.name = name;
        this.numberOfCards = 0;
        this.cards = new Card[Card.getCardCount()];
        this.cardPlayerKnowledge = new SparseArray<>();
    }

    private Player(Parcel in) {
        name = in.readString();
        numberOfCards = in.readInt();
        cards = in.createTypedArray(Card.CREATOR);
        cardPlayerKnowledge = new SparseArray<>();
    }

    void setNumberOfCards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    void setCards(boolean[] hasCard) {
        clearCards();
        int numberOfCards = 0;
        for (int i = 0; i < hasCard.length; i++) {
            if (hasCard[i]) {
                addCard(new Card(i));
                numberOfCards++;
            }
        }

        setNumberOfCards(numberOfCards);
    }

    void clearCards() {
        setNumberOfCards(0);
        cards = new Card[Card.getCardCount()];
        cardPlayerKnowledge.clear();
    }

    String getName() {
        return name;
    }

    void addCard(Card card) {
        if (Card.isGameCard(card.getId())) {
            cards[card.getId()] = card;
            addCardKnowledge(this, card, Card.KNOWLEDGE_NOT_GUILTY);
        }
    }

    void handleAnswer(Player answerPlayer, Card suspect, Card weapon, Card room, Card answer) {
        if (answer.getCardType().equals(Card.NONE)) {
            addCardKnowledge(answerPlayer, suspect, Card.KNOWLEDGE_POSSIBLY_GUILTY);
            addCardKnowledge(answerPlayer, weapon, Card.KNOWLEDGE_POSSIBLY_GUILTY);
            addCardKnowledge(answerPlayer, room, Card.KNOWLEDGE_POSSIBLY_GUILTY);
        } else {
            addCardKnowledge(answerPlayer, suspect, Card.KNOWLEDGE_AVOIDED);
            addCardKnowledge(answerPlayer, weapon, Card.KNOWLEDGE_AVOIDED);
            addCardKnowledge(answerPlayer, room, Card.KNOWLEDGE_AVOIDED);
            addCardKnowledge(answerPlayer, answer, Card.KNOWLEDGE_NOT_GUILTY);
        }
    }

    private void addCardKnowledge(Player player, Card card, int cardKnowledge) {
        if ((cardPlayerKnowledge.get(card.getId()) == null) || (cardPlayerKnowledge.get(card.getId()).get(player) == null)) {
            HashMap<Player, Integer> playerIntegerHashMap = new HashMap<>();
            playerIntegerHashMap.put(player, cardKnowledge);
            cardPlayerKnowledge.put(card.getId(), playerIntegerHashMap);
        } else {
            int currentAnswerKnowledge = cardPlayerKnowledge.get(card.getId()).get(player);
            cardPlayerKnowledge.get(card.getId()).put(player, Card.getGreatestKnowledge(currentAnswerKnowledge, cardKnowledge));
        }
    }

    void printDebugKnowledge() {
        Log.d(TAG, "printDebugKnowledge: ------------------------------");
        Log.d(TAG, "printDebugKnowledge: " + getName());
        Log.d(TAG, "printDebugKnowledge: ");
        for (int i = 0; i < Card.getCardCount(); i++) {
            int best = Card.KNOWLEDGE_UNKNOWN;
            if (cardPlayerKnowledge.get(i) != null) {
                HashMap<Player, Integer> hashMap = cardPlayerKnowledge.get(i);
                for (Player player : hashMap.keySet()) {
                    best = Card.getGreatestKnowledge(best, hashMap.get(player));
                }
            }
            Log.d(TAG, "printDebugKnowledge: " + i + ":" + best);
        }
        Log.d(TAG, "printDebugKnowledge: ------------------------------");
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
