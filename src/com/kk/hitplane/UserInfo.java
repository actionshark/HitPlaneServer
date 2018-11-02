package com.kk.hitplane;

import org.java_websocket.WebSocket;

public class UserInfo {
	public static final int STATUS_OFFLINE = 0;
	public static final int STATUS_IDLE = 1;
	public static final int STATUS_BATTLE = 2;
	public static final int STATUS_WATCH = 3;

	public WebSocket webSocket;

	public String username;
	public int id = 0;
	public String nickname;

	public int winCount = 0;
	public int loseCount = 0;

	public int status = STATUS_OFFLINE;
	public long lastRequestTime = 0;

	public UserInfo(WebSocket webSocket) {
		this.webSocket = webSocket;
	}

	public void markRequest() {
		lastRequestTime = System.currentTimeMillis();
	}
}
