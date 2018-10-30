package com.kk.hitplane.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kk.hitplane.Server;
import com.kk.hitplane.UserInfo;
import com.kk.hitplane.reponse.RequestEnd;
import com.kk.websocket.util.ThreadUtil;

public class BattleMgr {
	private static BattleMgr sInstance = new BattleMgr();

	public static BattleMgr getInstance() {
		return sInstance;
	}

	public static class Req {
		public int a;
		public int b;
		public long time;
	}

	public static final long REQ_TIMEOUT = 60000;

	//////////////////////////////////////////////////////////////////

	private final List<Req> mReqs = new ArrayList<>();

	private final Map<Integer, Battle> mBattles = new HashMap<>();

	private BattleMgr() {
		ThreadUtil.run(() -> {
			Server server = Server.getInstance();
			
			synchronized (BattleMgr.this) {
				for (int i = mReqs.size() - 1; i >= 0; i--) {
					Req req = mReqs.get(i);

					if (System.currentTimeMillis() - req.time > REQ_TIMEOUT) {
						mReqs.remove(i);
						RequestEnd.noticeAll(req.a, req.b, "挑战长时间未回应");
					} if (server.getUserInfo(req.a) == null || server.getUserInfo(req.b) == null) {
						mReqs.remove(i);
						RequestEnd.noticeAll(req.a, req.b, "已下线");
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
			if (id == battle.a || id == battle.b) {
				return battle;
			}
		}

		return null;
	}

	public synchronized String addReq(UserInfo a, UserInfo b) {
		if (a.status != UserInfo.STATUS_IDLE || b.status != UserInfo.STATUS_IDLE) {
			return "用户不是空闲状态";
		}

		for (Req req : mReqs) {
			if (a.id == req.a || a.id == req.b || b.id == req.a || b.id == req.b) {
				return "已经发起了挑战";
			}
		}

		for (Battle bt : mBattles.values()) {
			if (a.id == bt.a || a.id == bt.b || b.id == bt.a || b.id == bt.b) {

				return "已经在战斗中";
			}
		}

		Req req = new Req();
		req.a = a.id;
		req.b = b.id;
		req.time = System.currentTimeMillis();

		mReqs.add(req);

		return null;
	}

	public synchronized Req removeReq(int a, int b) {
		for (int i = mReqs.size() - 1; i >= 0; i--) {
			Req req = mReqs.get(i);

			if (a == req.a && b == req.b) {
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
			if (a.id == bt.a || a.id == bt.b || b.id == bt.a || b.id == bt.b) {

				return null;
			}
		}

		Battle battle = new Battle(a.id, b.id);
		mBattles.put(battle.id, battle);
		battle.start();

		a.status = b.status = UserInfo.STATUS_BATTLE;

		return battle;
	}

	public synchronized void remove(int id) {
		Battle battle = mBattles.remove(id);
		if (battle == null) {
			return;
		}

		Server server = Server.getInstance();

		UserInfo a = server.getUserInfo(battle.a);
		if (a != null) {
			a.status = UserInfo.STATUS_IDLE;
		}

		UserInfo b = server.getUserInfo(battle.b);
		if (b != null) {
			b.status = UserInfo.STATUS_IDLE;
		}
	}
}
