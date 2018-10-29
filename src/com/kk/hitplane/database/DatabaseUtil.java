package com.kk.hitplane.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.kk.websocket.log.Level;
import com.kk.websocket.log.Logger;

public class DatabaseUtil {
	private static DatabaseUtil sInstance = new DatabaseUtil();

	public static DatabaseUtil getInstance() {
		return sInstance;
	}

	private String mHost = "111.231.232.54";
	private int mPort = 3306;
	private String mDbName = "hitplane";
	private String mUserName = "root";
	private String mPassword = "sun,653.";

	private Connection mConn;

	private DatabaseUtil() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			String url = String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true&useUnicode=true&characterEncoding=utf8",
					mHost, mPort, mDbName);
			mConn = DriverManager.getConnection(url, mUserName, mPassword);
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}
	}

	public Statement getStatement() {
		try {
			return mConn.createStatement();
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}

		return null;
	}
}
