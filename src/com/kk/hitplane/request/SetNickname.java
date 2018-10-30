package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.database.UserInfoDB;
import com.kk.hitplane.reponse.ShowToast;
import com.kk.hitplane.reponse.UserInfo;

public class SetNickname extends Request {
	public String nickname;

	@Override
	public boolean exe() {
		if (nickname == null || nickname.length() < 1 || nickname.length() > 20) {
			return false;
		}

		String error = UserInfoDB.setNickname(mUserInfo, nickname);

		if (error == null) {
			ShowToast toast = new ShowToast();
			toast.text = "设置昵称成功";
			toast.send(mUserInfo);

			UserInfo info = new UserInfo();
			info.encode(mUserInfo);
			info.send(mUserInfo);

			return true;
		} else {
			ShowToast toast = new ShowToast();
			toast.text = error;
			toast.send(mUserInfo);
			return false;
		}
	}
}
