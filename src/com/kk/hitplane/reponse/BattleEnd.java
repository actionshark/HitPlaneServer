package com.kk.hitplane.reponse;

import java.util.ArrayList;
import java.util.List;

import com.kk.hitplane.Response;

public class BattleEnd extends Response {
	public static class Tile {
		public int type;
		public int owner;
	}

	public int winnerId;

	public List<Tile> tiles = new ArrayList<>();

	public void encode(int winnerId, List<com.kk.hitplane.battle.Battle.Tile> tiles) {
		this.winnerId = winnerId;

		this.tiles.clear();

		for (com.kk.hitplane.battle.Battle.Tile server : tiles) {
			Tile client = new Tile();
			client.type = server.type;
			client.owner = server.owner;
			this.tiles.add(client);
		}
	}
}
