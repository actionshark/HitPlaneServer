package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.Server;
import com.kk.hitplane.UserInfo;
import com.kk.hitplane.battle.Battle;
import com.kk.hitplane.battle.BattleMgr;
import com.kk.hitplane.reponse.BattleEnd;

public class QuitBattle extends Request {
	@Override
	public boolean exe() {
		BattleMgr mgr = BattleMgr.getInstance();
		Battle battle = mgr.getByUserId(mUserInfo.id);

		if (battle != null) {
			mgr.remove(battle.id);

			Server server = Server.getInstance();
			UserInfo a = server.getUserInfo(battle.a);
			UserInfo b = server.getUserInfo(battle.b);

			BattleEnd be = new BattleEnd();
			be.encode(mUserInfo.id, null);

			be.send(a);
			be.send(b);

			return true;
		}

		return false;
	}
}
