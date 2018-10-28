package com.kk.hitplane.reponse;

import com.kk.hitplane.Response;
import com.kk.hitplane.reponse.BattleInfo.Tile;

public class TurnChange extends Response {
	public int id;

	public int row;
	public int col;
	public Tile tile;
}
