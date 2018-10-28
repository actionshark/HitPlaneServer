package com.kk.hitplane.reponse;

import java.util.ArrayList;
import java.util.List;

import com.kk.hitplane.Response;

public class BattleEnd extends Response {
	public static class Tile {
		public int type;
		public int owner;
	}
	
	public int winner;
	
	public List<Tile> tiles = new ArrayList<>();
	
	public void encode(int winner, List<com.kk.hitplane.battle.Battle.Tile> tiles) {
		this.winner = winner;
		
		this.tiles.clear();
		
		for (com.kk.hitplane.battle.Battle.Tile server : tiles) {
			Tile client = new Tile();
			client.type = server.type;
			client.owner = server.owner == null ? 0 : server.owner.id;
			this.tiles.add(client);
		}
	}
}
