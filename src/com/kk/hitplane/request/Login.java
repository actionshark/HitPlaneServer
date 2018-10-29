package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.Server;
import com.kk.hitplane.database.UserInfoDB;
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

		boolean suc = UserInfoDB.login(mUserInfo);
		if (!suc) {
			return false;
		}
		
		if (mUserInfo.status == com.kk.hitplane.UserInfo.STATUS_OFFLINE) {
			mUserInfo.status = com.kk.hitplane.UserInfo.STATUS_IDLE;
		}

		Server.getInstance().onLogin(mUserInfo);

		ShowToast toast = new ShowToast();
		toast.text = "登录成功";
		toast.send(mUserInfo);

		UserInfo info = new UserInfo();
		info.encode(mUserInfo);
		info.send(mUserInfo);

		return true;
	}
}
