package com.kk.hitplane.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
	public static final int HAND_NUM = 3;

	public static final Card[] sCards = new Card[] { new Card(Card.SUIT_SPADE, 2), new Card(Card.SUIT_HEART, 2),
			new Card(Card.SUIT_CLUB, 2), new Card(Card.SUIT_DIAMOND, 2), new Card(Card.SUIT_SPADE, 3),
			new Card(Card.SUIT_HEART, 3), new Card(Card.SUIT_CLUB, 3), new Card(Card.SUIT_DIAMOND, 3),
			new Card(Card.SUIT_SPADE, 4), new Card(Card.SUIT_HEART, 4), new Card(Card.SUIT_CLUB, 4),
			new Card(Card.SUIT_DIAMOND, 4), new Card(Card.SUIT_SPADE, 5), new Card(Card.SUIT_HEART, 5),
			new Card(Card.SUIT_CLUB, 5), new Card(Card.SUIT_DIAMOND, 5), new Card(Card.SUIT_SPADE, 6),
			new Card(Card.SUIT_HEART, 6), new Card(Card.SUIT_CLUB, 6), new Card(Card.SUIT_DIAMOND, 6),
			new Card(Card.SUIT_SPADE, 7), new Card(Card.SUIT_HEART, 7), new Card(Card.SUIT_CLUB, 7),
			new Card(Card.SUIT_DIAMOND, 7), new Card(Card.SUIT_SPADE, 8), new Card(Card.SUIT_HEART, 8),
			new Card(Card.SUIT_CLUB, 8), new Card(Card.SUIT_DIAMOND, 8), new Card(Card.SUIT_SPADE, 9),
			new Card(Card.SUIT_HEART, 9), new Card(Card.SUIT_CLUB, 9), new Card(Card.SUIT_DIAMOND, 9),
			new Card(Card.SUIT_SPADE, 10), new Card(Card.SUIT_HEART, 10), new Card(Card.SUIT_CLUB, 10),
			new Card(Card.SUIT_DIAMOND, 10), new Card(Card.SUIT_SPADE, 11), new Card(Card.SUIT_HEART, 11),
			new Card(Card.SUIT_CLUB, 11), new Card(Card.SUIT_DIAMOND, 11), new Card(Card.SUIT_SPADE, 12),
			new Card(Card.SUIT_HEART, 12), new Card(Card.SUIT_CLUB, 12), new Card(Card.SUIT_DIAMOND, 12),
			new Card(Card.SUIT_SPADE, 13), new Card(Card.SUIT_HEART, 13), new Card(Card.SUIT_CLUB, 13),
			new Card(Card.SUIT_DIAMOND, 13), new Card(Card.SUIT_SPADE, 14), new Card(Card.SUIT_HEART, 14),
			new Card(Card.SUIT_CLUB, 14), new Card(Card.SUIT_DIAMOND, 14), };

	public static int calcScore(List<Card> hand) {
		return 0;
	}

	//////////////////////////////////////////////

	private final List<Card> mCards = new ArrayList<>();

	private Random mRandom = new Random();

	public Deck() {
	}

	public void reset() {
		mCards.clear();

		for (Card card : sCards) {
			mCards.add(card);
		}
	}

	public void shuffle() {
		for (int i = mCards.size() - 1; i > 0; i--) {
			int j = mRandom.nextInt(i);

			Card temp = mCards.get(i);
			mCards.set(i, mCards.get(j));
			mCards.set(j, temp);
		}
	}

	public Card deal() {
		return mCards.remove(0);
	}
}
