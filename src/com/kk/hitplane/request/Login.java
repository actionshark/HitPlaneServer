package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.Server;
import com.kk.hitplane.database.UserInfoDB;
import com.kk.hitplane.reponse.LoginResult;
import com.kk.hitplane.reponse.UserInfo;

public class Login extends Request {
	public String username;

	@Override
	public boolean exe() {
		LoginResult lr = new LoginResult();
		lr.error = login();
		lr.send(mUserInfo);

		if (lr.error == null) {
			UserInfo info = new UserInfo();
			info.encode(mUserInfo);
			info.send(mUserInfo);

			return true;
		} else {
			return false;
		}
	}

	private String login() {
		if (username == null || username.length() < 1 || username.length() > 50) {
			return "账号长度不对";
		}

		mUserInfo.username = username;

		boolean suc = UserInfoDB.login(mUserInfo);
		if (!suc) {
			return "登录数据出错";
		}

		if (mUserInfo.status == com.kk.hitplane.UserInfo.STATUS_OFFLINE) {
			mUserInfo.status = com.kk.hitplane.UserInfo.STATUS_IDLE;
		}

		Server.getInstance().onLogin(mUserInfo);

		return null;
	}
}
