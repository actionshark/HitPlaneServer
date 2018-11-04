package com.kk.hitplane.game;

public class Card {
	public static final int SUIT_SPADE = 4;
	public static final int SUIT_HEART = 3;
	public static final int SUIT_CLUB = 2;
	public static final int SUIT_DIAMOND = 1;

	public final int suit;
	public final int num;

	public Card(int suit, int num) {
		this.suit = suit;
		this.num = num;
	}
}
