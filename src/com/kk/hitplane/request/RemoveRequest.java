package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.Server;
import com.kk.hitplane.UserInfo;
import com.kk.hitplane.battle.BattleMgr;
import com.kk.hitplane.reponse.RequestEnd;

public class RemoveRequest extends Request {
	public int enemy;
	
	@Override
	public boolean exe() {
		UserInfo ui = Server.getInstance().getUserInfo(enemy);
		if (ui == null) {
			return false;
		}
				
		BattleMgr mgr = BattleMgr.getInstance();
		
		if (mgr.removeReq(mUserInfo.id, ui.id) != null || mgr.removeReq(ui.id, mUserInfo.id) != null) {
			RequestEnd.noticeAll(mUserInfo, ui, "挑战已取消");
			return true;
		}
		
		return false;
	}
}
