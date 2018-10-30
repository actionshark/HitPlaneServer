package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.Server;
import com.kk.hitplane.UserInfo;
import com.kk.hitplane.battle.Battle;
import com.kk.hitplane.battle.BattleMgr;
import com.kk.hitplane.battle.BattleMgr.Req;
import com.kk.hitplane.reponse.BattleInfo;
import com.kk.hitplane.reponse.RequestEnd;

public class AcceptRequest extends Request {
	public int enemy;

	@Override
	public boolean exe() {
		UserInfo b = Server.getInstance().getUserInfo(enemy);
		if (b == null) {
			return false;
		}

		BattleMgr mgr = BattleMgr.getInstance();

		Req req = mgr.removeReq(b.id, mUserInfo.id);
		if (req == null) {
			return false;
		}
		RequestEnd.noticeAll(b.id, mUserInfo.id, null);

		Battle battle = mgr.add(b, mUserInfo);
		if (battle == null) {
			return false;
		}

		BattleInfo bi = new BattleInfo();
		bi.encode(battle);
		bi.send(mUserInfo);
		bi.send(b);

		return true;
	}
}
