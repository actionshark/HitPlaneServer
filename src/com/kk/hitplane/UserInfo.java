package com.kk.hitplane;

import org.java_websocket.WebSocket;

public class UserInfo {
	public final WebSocket webSocket;

	public String username;
	public int id = 0;

	public String nickname;

	public int money;

	public UserInfo(WebSocket webSocket) {
		this.webSocket = webSocket;
	}
}
