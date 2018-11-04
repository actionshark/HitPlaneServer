package com.kk.hitplane;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.kk.hitplane.response.Logout;

public class Server extends WebSocketServer {
	private static Server sServer;

	public static void init(int port) {
		sServer = new Server(port);
	}

	public static Server getInstance() {
		return sServer;
	}

	/////////////////////////////////////////////////////////////////////////////

	public Server(int port) {
		super(new InetSocketAddress(port));
	}

	private final Map<WebSocket, UserInfo> mUsers = new HashMap<>();

	@Override
	public void onStart() {
	}

	public synchronized UserInfo getUserInfo(int id) {
		for (UserInfo ui : mUsers.values()) {
			if (ui.id == id) {
				return ui;
			}
		}

		return null;
	}

	public synchronized void onLogin(UserInfo ui) {
		List<WebSocket> removes = new ArrayList<>();

		for (Entry<WebSocket, UserInfo> entry : mUsers.entrySet()) {
			WebSocket key = entry.getKey();
			UserInfo value = entry.getValue();

			if (value.id == ui.id && value != ui) {
				removes.add(key);
			}
		}

		Logout logout = new Logout();
		logout.reason = "重登录";

		for (WebSocket webSocket : removes) {
			UserInfo userInfo = mUsers.remove(webSocket);
			logout.send(userInfo);

			webSocket.close();
		}
	}

	@Override
	public synchronized void onOpen(WebSocket ws, ClientHandshake hs) {
		UserInfo ui = new UserInfo(ws);
		mUsers.put(ws, ui);
	}

	@Override
	public synchronized void onClose(WebSocket ws, int code, String reason, boolean remote) {
		mUsers.remove(ws);
	}

	@Override
	public void onMessage(WebSocket ws, String message) {
		UserInfo ui = null;

		synchronized (this) {
			ui = mUsers.get(ws);
		}

		if (ui == null) {
			onOpen(ws, null);

			synchronized (this) {
				ui = mUsers.get(ws);
			}
		}

		Request.dispatch(ui, message);
	}

	@Override
	public void onError(WebSocket ws, Exception ex) {
		UserInfo ui = null;

		synchronized (this) {
			ui = mUsers.remove(ws);
		}

		if (ui != null) {
			Logout logout = new Logout();
			logout.reason = "出错服务器主动断开";
			logout.send(ui);
		}
	}
}
