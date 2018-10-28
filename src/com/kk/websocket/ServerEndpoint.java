package com.kk.websocket;

public interface ServerEndpoint {
	public void onOpen(Session session);
	public void onClose(Session session);
	public void onMessage(Session session, byte[] data, String text);
	public void onError(Session session, Throwable error);
}
