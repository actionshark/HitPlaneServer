package com.kk.websocket;

import java.net.Socket;
import java.util.List;

import com.kk.websocket.log.Level;
import com.kk.websocket.log.Logger;
import com.kk.websocket.protocol.WebSocketPackage;

public class Session {
	private final Socket mSocket;
	
	public Session(Socket socket) {
		mSocket = socket;
	}
	
	public void send(byte[] bs) {
		send(bs, 0, bs.length);
	}
	
	public void send(byte[] bs, int off, int len) {
		try {
			List<byte[]> list = WebSocketPackage.encode(bs, off, len);
			
			for (byte[] item : list) {
				mSocket.getOutputStream().write(item);
			}
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}
	}
	
	public void send(String text) {
		try {
			List<byte[]> list = WebSocketPackage.encode(text);
			
			for (byte[] item : list) {
				mSocket.getOutputStream().write(item);
			}
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e, text);
		}
	}
	
	public boolean close() {
		try {
			mSocket.close();
			return true;
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}
		
		return false;
	}
}
