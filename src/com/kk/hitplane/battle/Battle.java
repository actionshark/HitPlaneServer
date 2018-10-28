package com.kk.hitplane.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.kk.hitplane.UserInfo;
import com.kk.hitplane.reponse.BattleEnd;
import com.kk.hitplane.reponse.ShowToast;
import com.kk.hitplane.reponse.TurnChange;

public class Battle {
	public static final int ROW_NUM = 8;
	public static final int COL_NUM = 8;
	
	public static final int TILE_EMPTY = 0;
	public static final int TILE_BODY = 1;
	public static final int TILE_HEAD = 2;
	
	public static class Tile {
		public int type;
		public UserInfo owner;
	}
	
	public static final int[][] PLANE = new int[][] {
		{TILE_EMPTY, TILE_EMPTY, TILE_HEAD, TILE_EMPTY, TILE_EMPTY,},
		{TILE_BODY, TILE_BODY, TILE_BODY, TILE_BODY, TILE_BODY,},
		{TILE_EMPTY, TILE_EMPTY, TILE_BODY, TILE_EMPTY, TILE_EMPTY,},
		{TILE_EMPTY, TILE_BODY, TILE_BODY, TILE_BODY, TILE_EMPTY,},
	};
	
	private static int sCount = 0;
	
	//////////////////////////////////////////////////////////
	
	public final int id;
	
	public final UserInfo a;
	public final UserInfo b;
	
	private UserInfo mTurn;
	
	private final Tile[][] mTiles = new Tile[ROW_NUM][COL_NUM];
	
	private final Random mRandom = new Random();
	
	public Battle(UserInfo a, UserInfo b) {
		synchronized (Battle.class) {
			id = ++sCount;
		}
		
		this.a = a;
		this.b = b;
	}
	
	public void start() {
		initTiles();
		
		mTurn = mRandom.nextBoolean() ? a : b;
		
		next(0, 0, null);
	}
	
	private void initTiles() {
		int[][] plane;
		int rowNum;
		int colNum;
		
		if (mRandom.nextBoolean()) {
			rowNum = PLANE.length;
			colNum = PLANE[0].length;
			
			plane = new int[rowNum][colNum];
			
			for (int row = 0; row < rowNum; row++) {
				for (int col = 0; col < colNum; col++) {
					plane[row][col] = PLANE[row][col];
				}
			}
		} else {
			rowNum = PLANE[0].length;
			colNum = PLANE.length;
			
			plane = new int[rowNum][colNum];
			
			for (int row = 0; row < rowNum; row++) {
				for (int col = 0; col < colNum; col++) {
					plane[row][col] = PLANE[col][row];
				}
			}
		}
		
		if (mRandom.nextBoolean()) {
			for (int row = 0; row < rowNum; row++) {
				for (int col = 0; col < colNum / 2; col++) {
					int tmp = plane[row][col];
					plane[row][col] = plane[row][colNum - 1 - col];
					plane[row][colNum - 1 - col] = tmp;
				}
			}
		}
		
		if (mRandom.nextBoolean()) {
			for (int col = 0; col < colNum; col++) {
				for (int row = 0; row < rowNum / 2; row++) {
					int tmp = plane[row][col];
					plane[row][col] = plane[rowNum - 1 - row][col];
					plane[rowNum - 1 - row][col] = tmp;
				}
			}
		}
		
		int x = mRandom.nextInt(COL_NUM - colNum);
		int y = mRandom.nextInt(ROW_NUM - rowNum);
		
		for (int row = 0; row < ROW_NUM; row++) {
			for (int col = 0; col < COL_NUM; col++) {
				Tile tile = new Tile();
				tile.type = TILE_EMPTY;
				mTiles[row][col] = tile;
			}
		}
		
		for (int row = 0; row < rowNum; row++) {
			for (int col = 0; col < colNum; col++) {
				mTiles[y + row][x + col].type = plane[row][col];
			}
		}
	}
	
	private void next(int row, int col, Tile last) {
		TurnChange tc = new TurnChange();
		
		if (mTurn == null) {
			tc.id = 0;
			
			BattleMgr.getInstance().remove(id);
		} else {
			mTurn = mTurn == a ? b : a;
			tc.id = mTurn.id;
		}
		
		if (last != null) {
			tc.row = row;
			tc.col = col;
			tc.tile = new com.kk.hitplane.reponse.BattleInfo.Tile();
			tc.tile.encode(last);
		}
		
		tc.send(a);
		tc.send(b);
		
		if (mTurn == null) {
			BattleEnd be = new BattleEnd();
			be.encode(last.owner.id, getTiles());
			
			be.send(a);
			be.send(b);
			
			ShowToast st = new ShowToast();
			st.text = last.owner.nickname + "胜利";
			st.send(a);
			st.send(b);
		}
	}
	
	public synchronized String play(UserInfo ui, int row, int col) {
		if (mTurn.id != ui.id) {
			return "不是你的回合";
		}
		
		if (row < 0 || row >= ROW_NUM || col < 0 || col >= COL_NUM) {
			return "出界了";
		}
		
		Tile tile = mTiles[row][col];
		
		if (tile.owner != null) {
			return "这里点过了";
		}
		
		tile.owner = mTurn;
		
		if (tile.type == TILE_HEAD) {
			mTurn = null;
		}
		
		next(row, col, tile);
		
		return null;
	}
	
	public UserInfo getTurn() {
		return mTurn;
	}
	
	public List<Tile> getTiles() {
		List<Tile> list = new ArrayList<>();
		
		for (int row = 0; row < ROW_NUM; row++) {
			for (int col = 0; col < COL_NUM; col++) {
				list.add(mTiles[row][col]);
			}
		}
		
		return list;
	}
}
