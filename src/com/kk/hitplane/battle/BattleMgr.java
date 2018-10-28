package com.kk.hitplane.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kk.hitplane.UserInfo;
import com.kk.hitplane.reponse.RequestEnd;
import com.kk.websocket.util.ThreadUtil;

public class BattleMgr {
	private static BattleMgr sInstance = new BattleMgr();

	public static BattleMgr getInstance() {
		return sInstance;
	}

	public static class Req {
		public UserInfo a;
		public UserInfo b;
		public long time;
	}

	public static final long REQ_TIMEOUT = 60000;

	//////////////////////////////////////////////////////////////////

	private final List<Req> mReqs = new ArrayList<>();

	private final Map<Integer, Battle> mBattles = new HashMap<>();

	private BattleMgr() {
		ThreadUtil.run(() -> {
			synchronized (BattleMgr.this) {
				for (int i = mReqs.size() - 1; i >= 0; i--) {
					Req req = mReqs.get(i);

					if (System.currentTimeMillis() - req.time > REQ_TIMEOUT) {
						mReqs.remove(i);
						RequestEnd.noticeAll(req.a, req.b, "挑战长时间未回应");
					}
				}
			}
		}, 1000, 1000, -1);
	}

	public synchronized Battle getByBattleId(int id) {
		return mBattles.get(id);
	}

	public synchronized Battle getByUserId(int id) {
		for (Battle battle : mBattles.values()) {
			if (id == battle.a.id || id == battle.b.id) {
				return battle;
			}
		}

		return null;
	}

	public synchronized boolean addReq(UserInfo a, UserInfo b) {
		if (a.status != UserInfo.STATUS_IDLE || b.status != UserInfo.STATUS_IDLE) {
			return false;
		}

		for (Req req : mReqs) {
			if (a.id == req.a.id || a.id == req.b.id || b.id == req.a.id || b.id == req.b.id) {
				return false;
			}
		}

		for (Battle bt : mBattles.values()) {
			if (a.id == bt.a.id || a.id == bt.b.id || b.id == bt.a.id || b.id == bt.b.id) {

				return false;
			}
		}

		Req req = new Req();
		req.a = a;
		req.b = b;
		req.time = System.currentTimeMillis();

		mReqs.add(req);

		return true;
	}

	public synchronized Req removeReq(int a, int b) {
		for (int i = mReqs.size() - 1; i >= 0; i--) {
			Req req = mReqs.get(i);

			if (a == req.a.id && b == req.b.id) {
				mReqs.remove(i);
				return req;
			}
		}

		return null;
	}

	public synchronized Battle add(UserInfo a, UserInfo b) {
		if (a.status != UserInfo.STATUS_IDLE || b.status != UserInfo.STATUS_IDLE) {
			return null;
		}

		for (Battle bt : mBattles.values()) {
			if (a.id == bt.a.id || a.id == bt.b.id || b.id == bt.a.id || b.id == bt.b.id) {

				return null;
			}
		}

		Battle battle = new Battle(a, b);
		mBattles.put(battle.id, battle);
		battle.start();

		return battle;
	}

	public synchronized void remove(int id) {
		Battle battle = mBattles.remove(id);

		if (battle != null) {
			battle.a.status = battle.b.status = UserInfo.STATUS_IDLE;
		}
	}
}
