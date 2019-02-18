package com.games.csmith.bestclue;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by csmith on 2/3/18.
 */

public class Player implements Parcelable {
    private static final String TAG = "Player";
    private final int DEBUG = 0;
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
        cardPlayerKnowledge = readCardPlayerKnowledge(in);
        printDebugKnowledge();
    }

    private static SparseArray<HashMap<Player, Integer>> readCardPlayerKnowledge(Parcel in) {
        int sparseSize = in.readInt();
        SparseArray<HashMap<Player, Integer>> cardPlayerKnowledge = new SparseArray<>(sparseSize);
        for (int i = 0; i < sparseSize; i++) {
            int sparseKey = in.readInt();
            int hashMapSize = in.readInt();
            HashMap<Player, Integer> playerKnowledge = new HashMap<>(hashMapSize);
            for (int j = 0; j < hashMapSize; j++) {
                Player player = in.readTypedObject(Player.CREATOR);
                int knowledge = in.readInt();
                playerKnowledge.put(player, knowledge);
            }
            cardPlayerKnowledge.put(sparseKey, playerKnowledge);
        }

        return cardPlayerKnowledge;
    }

    void setNumberOfCards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    int getNumberOfCards() {
        return numberOfCards;
    }

    void setCards(boolean[] hasCard) {
        cards = new Card[Card.getCardCount()];
        int numberOfCards = 0;
        for (int i = 0; i < hasCard.length; i++) {
            if (hasCard[i]) {
                addCard(new Card(i));
                numberOfCards++;
            }
        }

        setNumberOfCards(numberOfCards);
    }

    Card[] getCards() {
        Card[] cards = new Card[numberOfCards];
        Card unknownCard = new Card(Card.UNKNOWN_ID);
        Arrays.fill(cards, unknownCard);
        int index = 0;
        for (Card card : this.cards) {
            if (card != null) {
                cards[index] = card;
                ++index;
            }
        }
        return cards;
    }

    String getName() {
        return name;
    }

    void addCard(Card card) {
        if (Card.isGameCard(card.getId())) {
            cards[card.getId()] = card;
            addCardKnowledge(this, card, Card.Knowledge.NOT_GUILTY);
        }
    }

    void initializeKnowledge(ArrayList<Player> players) {
        for (int i = 0; i < Card.getCardCount(); i++) {
            Card card = new Card(i);
            for (Player player : players) {
                HashMap<Player, Integer> playerIntegerHashMap = new HashMap<>();
                playerIntegerHashMap.put(player, Card.Knowledge.UNKNOWN);
                cardPlayerKnowledge.put(card.getId(), playerIntegerHashMap);
            }
        }
    }

    void handleAnswer(Player answerPlayer, Card suspect, Card weapon, Card room, Card answer) {
        if (answer.getCardType().equals(Card.NONE)) {
            addCardKnowledge(answerPlayer, suspect, Card.Knowledge.POSSIBLY_GUILTY);
            addCardKnowledge(answerPlayer, weapon, Card.Knowledge.POSSIBLY_GUILTY);
            addCardKnowledge(answerPlayer, room, Card.Knowledge.POSSIBLY_GUILTY);

            checkIfGuilty(suspect);
            checkIfGuilty(weapon);
            checkIfGuilty(room);
        } else {
            addCardKnowledge(answerPlayer, suspect, Card.Knowledge.AVOIDED);
            addCardKnowledge(answerPlayer, weapon, Card.Knowledge.AVOIDED);
            addCardKnowledge(answerPlayer, room, Card.Knowledge.AVOIDED);
            addCardKnowledge(answerPlayer, answer, Card.Knowledge.NOT_GUILTY);
        }
    }

    private void addCardKnowledge(Player player, Card card, int cardKnowledge) {
        if (cardPlayerKnowledge.get(card.getId()) == null) {
            HashMap<Player, Integer> playerIntegerHashMap = new HashMap<>();
            playerIntegerHashMap.put(player, cardKnowledge);
            cardPlayerKnowledge.put(card.getId(), playerIntegerHashMap);
        } else if (cardPlayerKnowledge.get(card.getId()).get(player) == null) {
            cardPlayerKnowledge.get(card.getId()).put(player, cardKnowledge);
        } else {
            int currentAnswerKnowledge = cardPlayerKnowledge.get(card.getId()).get(player);
            cardPlayerKnowledge.get(card.getId()).put(player, Card.getGreatestKnowledge(currentAnswerKnowledge, cardKnowledge));
        }
    }

    private void checkIfGuilty(Card card) {
        boolean guilty = true;
        HashMap<Player, Integer> playerKnowledge = cardPlayerKnowledge.get(card.getId());
        for (Player player : playerKnowledge.keySet()) {
            if (!(playerKnowledge.get(player).equals(Card.Knowledge.POSSIBLY_GUILTY) || (playerKnowledge.get(player).equals(Card.Knowledge.GUILTY)))) {
                guilty = false;
            }
        }

        if (guilty) {
            addCardKnowledge(this, card, Card.Knowledge.GUILTY);
        }
    }

    void printDebugKnowledge() {
        if (DEBUG > 0) {
            Log.d(TAG, "printDebugKnowledge: ------------------------------");
            Log.d(TAG, "printDebugKnowledge: " + getName());
            Log.d(TAG, "printDebugKnowledge: ");
            for (int i = 0; i < Card.getCardCount(); i++) {
                int best = Card.Knowledge.UNKNOWN;
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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(numberOfCards);
        dest.writeTypedArray(cards, 0);
        writeCardPlayerKnowledge(dest, cardPlayerKnowledge);
    }

    private static void writeCardPlayerKnowledge(Parcel dest, SparseArray<HashMap<Player, Integer>> cardPlayerKnowledge) {
        if (cardPlayerKnowledge == null) {
            dest.writeInt(-1);
        } else {
            int size = cardPlayerKnowledge.size();
            dest.writeInt(size);
            for (int i = 0; i < size; i++) {
                dest.writeInt(cardPlayerKnowledge.keyAt(i));
                HashMap<Player, Integer> playerKnowledge = cardPlayerKnowledge.get(i);
                if (playerKnowledge == null) {
                    playerKnowledge = new HashMap<>();
                }
                dest.writeInt(playerKnowledge.size());
                for (HashMap.Entry<Player, Integer> entry : playerKnowledge.entrySet()) {
                    dest.writeTypedObject(entry.getKey(), 0);
                    dest.writeInt(entry.getValue());
                }
            }
        }
    }

    Card.Knowledge[] getCardKnowledge() {
        Card.Knowledge[] knowledge = new Card.Knowledge[Card.getCardCount()];
        for (int i = 0; i < knowledge.length; i++) {
            HashMap<Player, Integer> playerKnowledgeMap = cardPlayerKnowledge.get(i);
            if (playerKnowledgeMap == null) {
                playerKnowledgeMap = new HashMap<>();
            }
            int bestKnowledge = Card.Knowledge.UNKNOWN;
            for (int playerKnowledge : playerKnowledgeMap.values()) {
                bestKnowledge = Card.getGreatestKnowledge(bestKnowledge, playerKnowledge);
            }
            knowledge[i] = new Card.Knowledge(new Card(i), bestKnowledge);
        }

        return knowledge;
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
