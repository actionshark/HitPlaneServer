package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.Server;
import com.kk.hitplane.reponse.ShowToast;
import com.kk.hitplane.reponse.UserInfo;

public class Login extends Request {
	public int id = 0;
	
	@Override
	public boolean exe() {
		Server server = Server.getInstance();
		com.kk.hitplane.UserInfo old = null;
		
		if (id != 0) {
			old = server.getUserInfo(id);
		}
		
		server.onLogin(mUserInfo, old);
		
		UserInfo res = new UserInfo();
		res.id = mUserInfo.id;
		res.nickname = mUserInfo.nickname;
		res.status = old == null ? mUserInfo.status : old.status;
		
		res.send(mUserInfo);
		
		ShowToast toast = new ShowToast();
		toast.text = "登录成功";
		toast.send(mUserInfo);
		
		return true;
	}
}
