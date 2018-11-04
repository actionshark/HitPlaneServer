package com.kk.hitplane.response;

import java.util.ArrayList;
import java.util.List;

import com.kk.hitplane.Response;
import com.kk.hitplane.database.UserInfoDB;
import com.kk.hitplane.game.Card;
import com.kk.hitplane.game.Room;
import com.kk.hitplane.game.Room.User;

public class RoomInfo extends Response {
	public static class UserInfo {
		public int id;
		public String nickname;

		public int wager;
		public int money;

		public boolean alive;
		public List<Card> cards;
	}

	public int id;
	public int current;

	public int wagerCurr;
	public int wagerTotal;

	public List<UserInfo> users = new ArrayList<>();

	public void encode(Room room, int ob) {
		id = room.id;
		current = room.getCurrent();

		wagerCurr = room.getWagerCurr();
		wagerTotal = room.getWagerTotal();

		users.clear();

		for (User user : room.getUsers()) {
			UserInfo ui = new UserInfo();
			users.add(ui);

			ui.id = user.id;
			ui.nickname = UserInfoDB.getUserInfo(user.id).nickname;

			ui.wager = user.wager;
			ui.money = user.money;

			ui.alive = user.alive;

			if (room.watchHand(ob, ui.id) == null) {
				ui.cards = user.cards;
			} else {
				ui.cards = null;
			}
		}
	}
}
