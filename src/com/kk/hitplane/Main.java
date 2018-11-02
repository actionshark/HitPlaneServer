package com.kk.hitplane;

import com.kk.hitplane.log.FileLogger;
import com.kk.hitplane.log.Logger;

public class Main {
	public static void main(String[] args) {
		int port = 10001;
		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			if ("-port".equals(arg)) {
				port = Integer.parseInt(args[++i]);
			}
		}
		
		FileLogger logger = new FileLogger();
		logger.setFiles("log1.txt", "log2.txt");
		Logger.setInstance(logger);

		Server.init(port);
		Server server = Server.getInstance();
		server.start();
	}
}
