package com.kk.hitplane;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.kk.hitplane.reponse.Logout;
import com.kk.hitplane.util.ThreadUtil;

public class Server extends WebSocketServer {
	public static final long CLOSE_DURATION = 30000;

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
		ThreadUtil.run(() -> {
			List<WebSocket> removes = new ArrayList<>();

			synchronized (Server.this) {
				for (Entry<WebSocket, UserInfo> entry : mUsers.entrySet()) {
					WebSocket key = entry.getKey();
					UserInfo value = entry.getValue();

					if (System.currentTimeMillis() - value.lastRequestTime > CLOSE_DURATION) {
						removes.add(key);
					}
				}

				Logout logout = new Logout();
				logout.reason = "长时间未请求，自动踢出";

				for (WebSocket webSocket : removes) {
					UserInfo ui = mUsers.remove(webSocket);
					logout.send(ui);

					webSocket.close();
				}
			}
		}, 1000, 3000, -1);
	}

	public UserInfo getUserInfo(int id) {
		synchronized (this) {
			for (UserInfo ui : mUsers.values()) {
				if (ui.id == id) {
					return ui;
				}
			}
		}

		return null;
	}

	public List<UserInfo> getUserList() {
		List<UserInfo> list = new ArrayList<>();

		synchronized (this) {
			for (UserInfo ui : mUsers.values()) {
				if (ui.status != UserInfo.STATUS_OFFLINE) {
					list.add(ui);
				}
			}
		}

		list.sort(new Comparator<UserInfo>() {
			@Override
			public int compare(UserInfo a, UserInfo b) {
				return a.id - b.id;
			}
		});

		return list;
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
	public void onOpen(WebSocket ws, ClientHandshake hs) {
		UserInfo ui = new UserInfo(ws);
		ui.markRequest();

		synchronized (this) {
			mUsers.put(ws, ui);
		}
	}

	@Override
	public void onClose(WebSocket ws, int code, String reason, boolean remote) {
		synchronized (this) {
			mUsers.remove(ws);
		}
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

		ui.markRequest();
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
