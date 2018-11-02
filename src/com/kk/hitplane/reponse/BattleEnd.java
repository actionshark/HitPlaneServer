package com.kk.hitplane.reponse;

import java.util.ArrayList;
import java.util.List;

import com.kk.hitplane.Response;

public class BattleEnd extends Response {
	public static class Tile {
		public int type;
		public int owner;
	}

	public int uid;

	public List<Tile> tiles;

	public void encode(int uid, List<com.kk.hitplane.battle.Battle.Tile> tiles) {
		this.uid = uid;

		if (tiles == null) {
			this.tiles = null;
		} else {
			this.tiles = new ArrayList<>();

			for (com.kk.hitplane.battle.Battle.Tile server : tiles) {
				Tile client = new Tile();
				client.type = server.type;
				client.owner = server.owner;
				this.tiles.add(client);
			}
		}
	}
}
