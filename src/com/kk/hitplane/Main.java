package com.kk.hitplane;

import com.kk.websocket.WebSocketServer;
import com.kk.websocket.log.FileLogger;
import com.kk.websocket.log.Logger;

public class Main {
	public static void main(String[] args) {
		FileLogger logger = new FileLogger();
		logger.setFiles("log1.txt", "log2.txt");
		Logger.setInstance(logger);

		Server server = Server.getInstance();
		server.start();

		WebSocketServer wss = new WebSocketServer();
		wss.putEndpoint("hitplane", server);
		wss.start(10001);
	}
}
