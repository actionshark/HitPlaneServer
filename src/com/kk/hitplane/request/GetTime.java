package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.response.NoticeTime;

public class GetTime extends Request {
	@Override
	public boolean exe() {
		NoticeTime nt = new NoticeTime();
		nt.encode();
		nt.send(mUserInfo);

		return true;
	}
}
