package com.kk.hitplane;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kk.hitplane.reponse.Logout;
import com.kk.websocket.ServerEndpoint;
import com.kk.websocket.Session;
import com.kk.websocket.util.ThreadUtil;

public class Server implements ServerEndpoint {
	public static final long CLOSE_DURATION = 30000;

	private static final Server sServer = new Server();

	public static Server getInstance() {
		return sServer;
	}

	private final Map<Session, UserInfo> mUsers = new HashMap<>();

	public void start() {
		ThreadUtil.run(() -> {
			List<Session> removes = new ArrayList<>();

			synchronized (Server.this) {
				for (Entry<Session, UserInfo> entry : mUsers.entrySet()) {
					Session key = entry.getKey();
					UserInfo value = entry.getValue();

					if (System.currentTimeMillis() - value.lastRequestTime > CLOSE_DURATION) {
						removes.add(key);
					}
				}

				Logout logout = new Logout();
				logout.reason = "长时间未请求，自动踢出";

				for (Session session : removes) {
					UserInfo ui = mUsers.remove(session);
					logout.send(ui);
				}
			}
		}, 1000, 3000, -1);
	}

	@Override
	public void onOpen(Session session) {
		UserInfo ui = new UserInfo(session);
		ui.markRequest();

		synchronized (this) {
			mUsers.put(session, ui);
		}
	}

	@Override
	public void onClose(Session session) {
		synchronized (this) {
			mUsers.remove(session);
		}
	}

	@Override
	public void onMessage(Session session, byte[] data, String text) {
		UserInfo ui = null;

		synchronized (this) {
			ui = mUsers.get(session);
		}

		if (ui == null) {
			onOpen(session);

			synchronized (this) {
				ui = mUsers.get(session);
			}
		}

		ui.markRequest();
		Request.dispatch(ui, text);
	}

	@Override
	public void onError(Session session, Throwable error) {
		UserInfo ui = null;

		synchronized (this) {
			ui = mUsers.remove(session);
		}

		if (ui != null) {
			Logout logout = new Logout();
			logout.reason = "出错服务器主动断开";
			logout.send(ui);
		}
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
		List<Session> removes = new ArrayList<>();

		for (Entry<Session, UserInfo> entry : mUsers.entrySet()) {
			Session key = entry.getKey();
			UserInfo value = entry.getValue();

			if (value.id == ui.id && value != ui) {
				removes.add(key);
			}
		}

		Logout logout = new Logout();
		logout.reason = "重登录";

		for (Session session : removes) {
			UserInfo userInfo = mUsers.remove(session);
			logout.send(userInfo);
		}
	}
}
