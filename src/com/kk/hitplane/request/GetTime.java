package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.reponse.NoticeTime;

public class GetTime extends Request {
	@Override
	public boolean exe() {
		NoticeTime nt = new NoticeTime();
		nt.time = System.currentTimeMillis();
		nt.send(mUserInfo);
		
		return true;
	}
}
