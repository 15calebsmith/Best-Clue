package com.games.csmith.bestclue;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by csmith on 2/16/18.
 */

class Predictions {
    private static final double UNKNOWN_WEIGHT = 1.0;
    private static final double AVOIDED_WEIGHT = 2.0;
    private static final double POSSIBLY_GUILTY_WEIGHT = 3.0;

    static ArrayList<Prediction> generatePredictions(int[] knowledge) {
        Integer[] allSuspects = new Integer[Card.getSuspects().length];
        Integer[] allWeapons = new Integer[Card.getWeapons().length];
        Integer[] allRooms = new Integer[Card.getRooms().length];

        for (int i = 0; i < knowledge.length; i++) {
            String cardType = Card.getCardTypeFromId(i);
            int j = Card.getCardPosFromTypeAndId(cardType, i);
            switch (cardType) {
                case Card.SUSPECT:
                    allSuspects[j] = knowledge[i];
                    break;
                case Card.WEAPON:
                    allWeapons[j] = knowledge[i];
                    break;
                case Card.ROOM:
                    allRooms[j] = knowledge[i];
                    break;
            }
        }

        HashMap<Integer, Double> suspiciousSuspects = getSuspicious(allSuspects);
        HashMap<Integer, Double> suspiciousWeapons = getSuspicious(allWeapons);
        HashMap<Integer, Double> suspiciousRooms = getSuspicious(allRooms);

        return generatePredictionCombinations(suspiciousSuspects, suspiciousWeapons, suspiciousRooms);
    }

    private static HashMap<Integer, Double> getSuspicious(Integer[] cards) {
        HashMap<Integer, Double> suspicious = new HashMap<>();
        double denominator = 0;
        for (int i = 0; i < cards.length; i++) {
            int knowledge = cards[i];
            if (knowledge == Card.KNOWLEDGE_GUILTY) {
                suspicious.clear();
                suspicious.put(i, 1.0);
                return suspicious;
            } else if (knowledge == Card.KNOWLEDGE_UNKNOWN) {
                denominator = denominator + UNKNOWN_WEIGHT;
            } else if (knowledge == Card.KNOWLEDGE_AVOIDED) {
                denominator = denominator + AVOIDED_WEIGHT;
            } else if (knowledge == Card.KNOWLEDGE_POSSIBLY_GUILTY) {
                denominator = denominator + POSSIBLY_GUILTY_WEIGHT;
            }
        }

        for (int i = 0; i < cards.length; i++) {
            int knowledge = cards[i];
            if (knowledge == Card.KNOWLEDGE_UNKNOWN) {
                double prediction = UNKNOWN_WEIGHT / denominator;
                suspicious.put(i, prediction);
            } else if (knowledge == Card.KNOWLEDGE_POSSIBLY_GUILTY) {
                double prediction = POSSIBLY_GUILTY_WEIGHT / denominator;
                suspicious.put(i, prediction);
            } else if (knowledge == Card.KNOWLEDGE_AVOIDED) {
                double prediction = AVOIDED_WEIGHT / denominator;
                suspicious.put(i, prediction);
            }
        }

        return suspicious;
    }

    private static ArrayList<Prediction> generatePredictionCombinations(HashMap<Integer, Double> suspects, HashMap<Integer, Double> weapons, HashMap<Integer, Double> rooms) {
        ArrayList<Prediction> predictions = new ArrayList<>();

        for (Integer s : suspects.keySet()) {
            for (Integer w : weapons.keySet()) {
                for (Integer r : rooms.keySet()) {
                    Pair<Card, Double> suspect = new Pair<>(new Card(Card.SUSPECT, s), suspects.get(s));
                    Pair<Card, Double> weapon = new Pair<>(new Card(Card.WEAPON, w), weapons.get(w));
                    Pair<Card, Double> room = new Pair<>(new Card (Card.ROOM, r), rooms.get(r));
                    predictions.add(new Prediction(suspect, weapon, room));
                }
            }
        }

        Collections.sort(predictions);
        return predictions;
    }

    static class Prediction implements Comparable<Prediction>, Parcelable {
        Pair<Card, Double> suspect;
        Pair<Card, Double> weapon;
        Pair<Card, Double> room;
        double probability;
        Prediction(Pair<Card, Double> suspect, Pair<Card, Double> weapon, Pair<Card, Double> room) {
            this.suspect = suspect;
            this.weapon = weapon;
            this.room = room;
            findProbability();
        }

        private Prediction(Parcel in) {
            suspect = new Pair<>(in.readTypedObject(Card.CREATOR), in.readDouble());
            weapon = new Pair<>(in.readTypedObject(Card.CREATOR), in.readDouble());
            room = new Pair<>(in.readTypedObject(Card.CREATOR), in.readDouble());
            probability = in.readDouble();

        }

        public static final Creator<Prediction> CREATOR = new Creator<Prediction>() {
            @Override
            public Prediction createFromParcel(Parcel in) {
                return new Prediction(in);
            }

            @Override
            public Prediction[] newArray(int size) {
                return new Prediction[size];
            }
        };

        private void findProbability() {
            probability = suspect.second * weapon.second * room.second;
        }

        @Override
        public String toString() {
            String suspectString = suspect.first.getCardName();
            String weaponString = weapon.first.getCardName();
            String roomString = room.first.getCardName();
            return "(" + suspectString + ", " + weaponString + ", " + roomString + ") --> " + String.format(Locale.US, "%.2f", probability * 100) + "%";
        }

        @Override
        public int compareTo(@NonNull Prediction o) {
            if (this.probability > o.probability) {
                return -1;
            } else if (this.probability < o.probability) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedObject(suspect.first, 0);
            dest.writeDouble(suspect.second);
            dest.writeTypedObject(weapon.first, 0);
            dest.writeDouble(weapon.second);
            dest.writeTypedObject(room.first, 0);
            dest.writeDouble(room.second);
            dest.writeDouble(probability);
        }
    }
}
