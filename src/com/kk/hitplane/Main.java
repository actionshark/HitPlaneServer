package com.kk.hitplane;

import com.kk.hitplane.log.FileLogger;
import com.kk.hitplane.log.Logger;

public class Main {
	public static void main(String[] args) {
		FileLogger logger = new FileLogger();
		logger.setFiles("log1.txt", "log2.txt");
		Logger.setInstance(logger);

		Server.init(10001);
		Server server = Server.getInstance();
		server.start();
	}
}
