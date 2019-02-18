package com.games.csmith.bestclue;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
    private String name;
    private int numberOfCards;
    private Card[] cards;
    private HashMap<Player, Card.Knowledge[]> playerCardKnowledge;

    Player(String name) {
        this.name = name;
        this.numberOfCards = 0;
        this.cards = new Card[Card.getCardCount()];
        this.playerCardKnowledge = new HashMap<>();
    }

    private Player(Parcel in) {
        name = in.readString();
        numberOfCards = in.readInt();
        cards = in.createTypedArray(Card.CREATOR);
        playerCardKnowledge = readPlayerCardKnowledge(in);
    }

    private static HashMap<Player, Card.Knowledge[]> readPlayerCardKnowledge(Parcel in) {
        int sparseSize = in.readInt();
        HashMap<Player, Card.Knowledge[]> playerCardKnowledge = new HashMap<>(sparseSize);
        for (int i = 0; i < sparseSize; i++) {
            Player player = in.readTypedObject(Player.CREATOR);
            int arraySize = in.readInt();
            Card.Knowledge[] knowledge = new Card.Knowledge[arraySize];
            for (int j = 0; j < arraySize; j++) {
                knowledge[j] = in.readTypedObject(Card.Knowledge.CREATOR);
            }
            playerCardKnowledge.put(player, knowledge);
        }

        return playerCardKnowledge;
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
            addCardKnowledge(this, new Card.Knowledge(card, Card.Knowledge.NOT_GUILTY));
        }
    }

    void initializeKnowledge(ArrayList<Player> players) {
        for (Player player : players) {
            Card.Knowledge[] knowledge = new Card.Knowledge[Card.getCardCount()];
            for (int i = 0; i < knowledge.length; i++) {
                knowledge[i] = new Card.Knowledge(new Card(i), Card.Knowledge.UNKNOWN);
            }
            playerCardKnowledge.put(player, knowledge);
        }
    }

    void handleAnswer(Player answerPlayer, Card suspect, Card weapon, Card room, Card answer) {
        if (answer.getCardType().equals(Card.NONE)) {
            addCardKnowledge(answerPlayer, new Card.Knowledge(suspect, Card.Knowledge.POSSIBLY_GUILTY));
            addCardKnowledge(answerPlayer, new Card.Knowledge(weapon, Card.Knowledge.POSSIBLY_GUILTY));
            addCardKnowledge(answerPlayer, new Card.Knowledge(room, Card.Knowledge.POSSIBLY_GUILTY));

            checkIfGuilty(suspect);
            checkIfGuilty(weapon);
            checkIfGuilty(room);
        } else {
            addCardKnowledge(answerPlayer, new Card.Knowledge(suspect, Card.Knowledge.AVOIDED));
            addCardKnowledge(answerPlayer, new Card.Knowledge(weapon, Card.Knowledge.AVOIDED));
            addCardKnowledge(answerPlayer, new Card.Knowledge(room, Card.Knowledge.AVOIDED));
            addCardKnowledge(answerPlayer, new Card.Knowledge(answer, Card.Knowledge.NOT_GUILTY));
        }
    }

    private void addCardKnowledge(Player player, @NonNull Card.Knowledge newKnowledge) {
        int knowledgeCardId = newKnowledge.getCard().getId();
        Card.Knowledge[] playersKnowledgeArray = playerCardKnowledge.get(player);
        Card.Knowledge previousKnowledge = playersKnowledgeArray[knowledgeCardId];
        if (newKnowledge.getCard().equals(previousKnowledge.getCard())) {
            playersKnowledgeArray[knowledgeCardId] = previousKnowledge.getKnowledgeLevel() > newKnowledge.getKnowledgeLevel() ? previousKnowledge : newKnowledge;
        } else {
            Log.e(TAG, "addCardKnowledge: Cards do not match. previous knowledge card = " + previousKnowledge.getCard() + ", new knowledge card = " + newKnowledge.getCard());
        }
    }

    private void checkIfGuilty(Card card) {
        boolean guilty = true;
        for (Player player : playerCardKnowledge.keySet()) {
            Card.Knowledge[] cardKnowledge = playerCardKnowledge.get(player);
            int knowledgeLevel = cardKnowledge[card.getId()].getKnowledgeLevel();
            if (!(knowledgeLevel == Card.Knowledge.POSSIBLY_GUILTY || (knowledgeLevel == Card.Knowledge.GUILTY))) {
                guilty = false;
            }
        }

        if (guilty) {
            addCardKnowledge(this, new Card.Knowledge(card, Card.Knowledge.GUILTY));
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(numberOfCards);
        dest.writeTypedArray(cards, 0);
        writeCardPlayerKnowledge(dest, playerCardKnowledge);
    }

    private static void writeCardPlayerKnowledge(Parcel dest, HashMap<Player, Card.Knowledge[]> playerCardKnowledge) {
        if (playerCardKnowledge == null) {
            playerCardKnowledge = new HashMap<>();
        }
        dest.writeInt(playerCardKnowledge.size());
        for (Player player : playerCardKnowledge.keySet()) {
            dest.writeTypedObject(player, 0);
            Card.Knowledge[] knowledges = playerCardKnowledge.get(player);
            dest.writeInt(knowledges.length);
            for (Card.Knowledge knowledge : knowledges) {
                dest.writeTypedObject(knowledge, 0);
            }
        }
    }

    Card.Knowledge[] getCardKnowledge() {
        Card.Knowledge[] bestKnowledge = new Card.Knowledge[Card.getCardCount()];
        for (Player player : playerCardKnowledge.keySet()) {
            Card.Knowledge[] knowledge = playerCardKnowledge.get(player);
            for (int i = 0; i < knowledge.length; i++) {
                bestKnowledge[i] = bestKnowledge[i] == null || knowledge[i].getKnowledgeLevel() > bestKnowledge[i].getKnowledgeLevel() ? knowledge[i] : bestKnowledge[i];
            }
        }

        return bestKnowledge;
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
