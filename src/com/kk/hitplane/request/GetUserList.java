package com.kk.hitplane.request;

import java.util.List;

import com.kk.hitplane.Request;
import com.kk.hitplane.Server;
import com.kk.hitplane.reponse.UserInfo;
import com.kk.hitplane.reponse.UserList;

public class GetUserList extends Request {
	@Override
	public boolean exe() {
		UserList ul = new UserList();
		
		List<com.kk.hitplane.UserInfo> list = Server.getInstance().getUserList();
		
		for (com.kk.hitplane.UserInfo ui : list) {
			UserInfo info = new UserInfo();
			ul.list.add(info);
			
			info.id = ui.id;
			info.nickname = ui.nickname;
			info.status = ui.status;
		}
		
		ul.send(mUserInfo);
		
		return true;
	}
}
