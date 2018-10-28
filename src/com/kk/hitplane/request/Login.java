package com.kk.hitplane.request;

import com.kk.hitplane.DatabaseUtil;
import com.kk.hitplane.Request;
import com.kk.hitplane.Server;
import com.kk.hitplane.reponse.ShowToast;
import com.kk.hitplane.reponse.UserInfo;

public class Login extends Request {
	public String username;

	@Override
	public boolean exe() {
		if (username == null || username.length() < 1 || username.length() > 30) {
			return false;
		}
		
		mUserInfo.username = username;
		
		boolean suc = DatabaseUtil.getInstance().login(mUserInfo);
		if (!suc) {
			return false;
		}
		
		UserInfo info = new UserInfo();
		info.id = mUserInfo.id;
		info.nickname = mUserInfo.nickname;
		info.status = com.kk.hitplane.UserInfo.STATUS_IDLE;
		info.send(mUserInfo);

		ShowToast toast = new ShowToast();
		toast.text = "登录成功";
		toast.send(mUserInfo);
		
		Server.getInstance().onLogin(mUserInfo);

		return true;
	}
}
