package com.kk.hitplane.request;

import com.kk.hitplane.DatabaseUtil;
import com.kk.hitplane.Request;
import com.kk.hitplane.reponse.ShowToast;

public class SetNickname extends Request {
	public String nickname;
	
	@Override
	public boolean exe() {
		if (nickname == null || nickname.length() < 1 || nickname.length() > 20) {
			return false;
		}
		
		boolean suc = DatabaseUtil.getInstance().setNickname(mUserInfo, nickname);
		
		if (suc) {
			ShowToast toast = new ShowToast();
			toast.text = "设置昵称成功";
			toast.send(mUserInfo);
			return true;
		}
		
		return false;
	}
}
