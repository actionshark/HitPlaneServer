package com.kk.hitplane;

import com.kk.websocket.Session;

public class UserInfo {
	public static final int STATUS_OFFLINE = 0;
	public static final int STATUS_IDLE = 1;
	public static final int STATUS_BATTLE = 2;
	public static final int STATUS_WATCH = 3;
	
	private static int sCount = 0;
	
	public Session session;
	
	public int id = 0;
	public String nickname;
	
	public int status = STATUS_OFFLINE;
	public long lastRequestTime = 0;
	
	public UserInfo(Session session) {
		this.session = session;
	}
	
	public void onLogin() {
		synchronized (UserInfo.class) {
			id = ++sCount;
		}
		
		nickname = "用户" + id;
		status = STATUS_IDLE;
	}
	
	public void markRequest() {
		lastRequestTime = System.currentTimeMillis();
	}
}
