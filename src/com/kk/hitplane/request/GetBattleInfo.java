package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.battle.Battle;
import com.kk.hitplane.battle.BattleMgr;
import com.kk.hitplane.reponse.BattleInfo;

public class GetBattleInfo extends Request {
	public int battleId = 0;

	@Override
	public boolean exe() {
		Battle battle = null;
		BattleMgr mgr = BattleMgr.getInstance();

		if (battleId == 0) {
			battle = mgr.getByUserId(mUserInfo.id);
		} else {
			battle = mgr.getByBattleId(battleId);
		}

		if (battle == null) {
			return false;
		}

		BattleInfo bi = new BattleInfo();
		bi.encode(battle);
		bi.send(mUserInfo);

		return true;
	}
}
