package com.kk.websocket;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kk.websocket.log.Level;
import com.kk.websocket.log.Logger;
import com.kk.websocket.protocol.ProtocolUtil;
import com.kk.websocket.protocol.WebSocketPackage;
import com.kk.websocket.util.DataUtil;
import com.kk.websocket.util.ThreadUtil;

public class WebSocketServer {
	private ServerSocket mServer;

	private final Map<String, ServerEndpoint> mEndpoint = new HashMap<>();

	public synchronized void putEndpoint(String path, ServerEndpoint endpoint) {
		mEndpoint.put(path, endpoint);
	}

	public synchronized void removeEndpoint(String path) {
		mEndpoint.remove(path);
	}

	public synchronized boolean start(int port) {
		if (mServer != null) {
			return false;
		}

		try {
			mServer = new ServerSocket(port);

			ThreadUtil.run(() -> {
				try {
					while (true) {
						Socket socket = mServer.accept();

						ThreadUtil.run(() -> {
							dispatchSocket(socket);
						});
					}
				} catch (Exception e) {
					close();
					Logger.getInstance().print(null, Level.E, e);
				}
			});
		} catch (Exception e) {
			close();
			Logger.getInstance().print(null, Level.E, e);

			return false;
		}

		return true;
	}

	public synchronized void close() {
		try {
			if (mServer != null) {
				mServer.close();
			}
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}

		mServer = null;
	}

	private void dispatchSocket(Socket socket) {
		ServerEndpoint endpoint = null;
		Session session = null;

		try {
			String path = ProtocolUtil.handshake(socket);
			synchronized (this) {
				endpoint = mEndpoint.get(path);
			}

			if (endpoint == null) {
				throw new Exception("cannot find path " + path);
			}

			session = new Session(socket);
			endpoint.onOpen(session);

			InputStream is = socket.getInputStream();
			byte[] buf = new byte[1024 * 64];

			List<WebSocketPackage> packs = new ArrayList<>();

			while (true) {
				int len = is.read(buf);
				if (len <= 0) {
					ThreadUtil.sleep(500);
					continue;
				}

				boolean stop = false;

				byte[] left = DataUtil.cutBytes(buf, 0, len);

				while (true) {
					WebSocketPackage pack;
					int size = packs.size();

					if (size <= 0) {
						pack = new WebSocketPackage();
						packs.add(pack);
					} else {
						pack = packs.get(size - 1);
					}

					left = pack.decode(left);

					if (left == null) {
						break;
					}

					if (!pack.fin) {
						packs.add(new WebSocketPackage());
						continue;
					}

					int total = 0;
					for (WebSocketPackage p : packs) {
						total += p.payloadData.length;
					}

					byte[] data = new byte[total];
					int off = 0;
					for (WebSocketPackage p : packs) {
						for (int i = 0; i < p.payloadData.length; i++) {
							data[off + i] = p.payloadData[i];
						}

						off += p.payloadData.length;
					}

					pack = packs.get(0);

					switch (pack.opcode) {
					case WebSocketPackage.OP_ADDITION:
						break;

					case WebSocketPackage.OP_TEXT:
						String text = new String(data, DataUtil.CHARSET);
						endpoint.onMessage(session, null, text);
						break;

					case WebSocketPackage.OP_BINARY:
						endpoint.onMessage(session, data, null);
						break;

					case WebSocketPackage.OP_CLOSE:
						stop = true;
						break;

					case WebSocketPackage.OP_PING:
						break;

					case WebSocketPackage.OP_PONG:
						break;
					}

					packs.clear();
				}

				if (stop) {
					break;
				}
			}

			return;
		} catch (Exception error) {
			Logger.getInstance().print(null, Level.E, error);

			try {
				if (endpoint != null) {
					endpoint.onError(session, error);
				}
			} catch (Exception e) {
				Logger.getInstance().print(null, Level.E, e);
			}
		}

		try {
			if (endpoint != null && session != null) {
				endpoint.onClose(session);
			}
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}

		try {
			if (session != null) {
				session.close();
			}
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}
	}
}
