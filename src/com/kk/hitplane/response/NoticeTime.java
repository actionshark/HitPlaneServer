package com.kk.hitplane.response;

import com.kk.hitplane.Response;

public class NoticeTime extends Response {
	public long time;

	public void encode() {
		time = System.currentTimeMillis();
	}
}
