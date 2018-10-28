package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.battle.Battle;
import com.kk.hitplane.battle.BattleMgr;
import com.kk.hitplane.reponse.ShowToast;

public class PlayBattle extends Request {
	public int row;
	public int col;

	@Override
	public boolean exe() {
		Battle battle = BattleMgr.getInstance().getByUserId(mUserInfo.id);

		if (battle != null) {
			String error = battle.play(mUserInfo, row, col);

			if (error == null) {
				return true;
			} else {
				ShowToast toast = new ShowToast();
				toast.text = error;
				toast.send(mUserInfo);
				return false;
			}
		}

		return false;
	}
}
