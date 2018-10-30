package com.kk.hitplane.reponse;

import java.util.ArrayList;
import java.util.List;

import com.kk.hitplane.Response;
import com.kk.hitplane.UserInfo;
import com.kk.hitplane.battle.Battle;
import com.kk.hitplane.database.UserInfoDB;

public class BattleInfo extends Response {
	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_MISS = 1;
	public static final int STATUS_INJURY = 2;
	public static final int STATUS_GOAL = 3;

	public static class Tile {
		public int status;
		public int owner;

		public void encode(com.kk.hitplane.battle.Battle.Tile tile) {
			owner = tile.owner;

			if (tile.owner == 0) {
				status = STATUS_NORMAL;
			} else if (tile.type == Battle.TILE_EMPTY) {
				status = STATUS_MISS;
			} else if (tile.type == Battle.TILE_BODY) {
				status = STATUS_INJURY;
			} else if (tile.type == Battle.TILE_HEAD) {
				status = STATUS_GOAL;
			}
		}
	}

	public int idA;
	public String nicknameA;

	public int idB;
	public String nicknameB;

	public int turn;

	public List<Tile> tiles = new ArrayList<>();

	public void encode(Battle battle) {
		idA = battle.a;
		UserInfo aui = UserInfoDB.getUserInfo(idA);
		nicknameA = aui == null ? "" : aui.nickname;

		idB = battle.b;
		UserInfo bui = UserInfoDB.getUserInfo(idB);
		nicknameB = bui == null ? "" : bui.nickname;

		turn = battle.getTurn();

		List<com.kk.hitplane.battle.Battle.Tile> list = battle.getTiles();
		for (com.kk.hitplane.battle.Battle.Tile tile : list) {
			Tile t = new Tile();
			t.encode(tile);
			tiles.add(t);
		}
	}
}
