package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.Server;
import com.kk.hitplane.UserInfo;
import com.kk.hitplane.battle.BattleMgr;
import com.kk.hitplane.reponse.RequestBegin;
import com.kk.hitplane.reponse.ShowToast;

public class SendRequest extends Request {
	public int enemy;

	@Override
	public boolean exe() {
		UserInfo b = Server.getInstance().getUserInfo(enemy);
		if (b == null) {
			ShowToast toast = new ShowToast();
			toast.text = "对方不在线";
			toast.send(mUserInfo);
			return false;
		}
		
		ShowToast toast = new ShowToast();
		toast.text = BattleMgr.getInstance().addReq(mUserInfo, b);

		if (toast.text == null) {
			RequestBegin res = new RequestBegin();

			res.id = b.id;
			res.nickname = b.nickname;
			res.active = true;
			res.send(mUserInfo);

			res.id = mUserInfo.id;
			res.nickname = mUserInfo.nickname;
			res.active = false;
			res.send(b);

			return true;
		} else {
			toast.send(mUserInfo);
			
			return false;
		}
	}
}
