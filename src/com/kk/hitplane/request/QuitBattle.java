package com.kk.hitplane.request;

import com.kk.hitplane.Request;
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

			BattleEnd be = new BattleEnd();
			be.encode(mUserInfo.id, null);

			be.send(battle.a);
			be.send(battle.b);

			return true;
		}

		return false;
	}
}
