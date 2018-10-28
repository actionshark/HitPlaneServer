package com.kk.hitplane;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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

	private final String TB_USERINFO = "userinfo";

	private DatabaseUtil() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			String url = String.format("jdbc:mysql://%s:%d/%s", mHost, mPort, mDbName);
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

	public synchronized boolean login(UserInfo ui) {
		Statement stmt = null;

		try {
			stmt = getStatement();
			
			long now = System.currentTimeMillis();

			String sql = String.format("select * from %s where username='%s'",
					TB_USERINFO, ui.username);
			ResultSet rs = stmt.executeQuery(sql);

			if (!rs.next()) {
				rs.close();
				
				sql = String.format("insert into %s (username, create_time) values('%s', %d)",
						TB_USERINFO, ui.username, now);
				int ct = stmt.executeUpdate(sql);
				
				if (ct != 1) {
					return false;
				}
				
				return login(ui);
			}
			
			ui.id = rs.getInt("id");
			ui.nickname = rs.getString("nickname");

			ui.winCount = rs.getInt("win_count");
			ui.loseCount = rs.getInt("lose_count");

			rs.close();
			
			sql = String.format("update %s set login_time=%d where username='%s'",
					TB_USERINFO, now, ui.username);
			int ct = stmt.executeUpdate(sql);
			
			if (ct != 1) {
				return false;
			}

			return true;
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return false;
	}
	
	public synchronized boolean setNickname(UserInfo ui, String nickname) {
		Statement stmt = null;

		try {
			stmt = getStatement();

			String sql = String.format("select id from %s where nickname='%s'",
					TB_USERINFO, nickname);
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {
				rs.close();
				return false;
			}
			rs.close();
			
			sql = String.format("update %s set nickname='%s' where id=%d",
					TB_USERINFO, nickname, ui.id);
			int ct = stmt.executeUpdate(sql);
			
			if (ct != 1) {
				return false;
			}
			
			ui.nickname = nickname;

			return true;
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return false;
	}
	
	public synchronized boolean updateBattleCount(UserInfo ui, int winDelta, int loseDelta) {
		Statement stmt = null;

		try {
			stmt = getStatement();
			
			String sql = String.format("update %s set win_count=win_count+%d, lose_count=lose_count+%d where id=%d",
					TB_USERINFO, winDelta, loseDelta, ui.id);
			int ct = stmt.executeUpdate(sql);
			
			if (ct != 1) {
				return false;
			}

			return true;
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return false;
	}
}
