package com.kk.websocket.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kk.websocket.log.Level;
import com.kk.websocket.log.Logger;

public class ThreadUtil {
	private static final ExecutorService sService = Executors.newCachedThreadPool();

	public static void run(Runnable runnable) {
		run(runnable, 0);
	}

	public static void run(Runnable runnable, long delay) {
		run(runnable, delay, 0, 1);
	}

	public static void run(Runnable runnable, long firstDelay, long repeatDelay, int times) {
		sService.submit(() -> {
			if (firstDelay > 0) {
				sleep(firstDelay);
			}

			for (int i = 0; i != times; i++) {
				try {
					runnable.run();
				} catch (Exception e) {
					Logger.getInstance().print(null, Level.E, e);
				}

				sleep(repeatDelay);
			}
		});
	}

	public static void sleep(long duration) {
		try {
			Thread.sleep(duration);
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}
	}
}
