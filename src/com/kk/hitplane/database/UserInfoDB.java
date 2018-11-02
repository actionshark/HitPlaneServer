package com.kk.hitplane.database;

import java.sql.ResultSet;
import java.sql.Statement;

import com.kk.hitplane.UserInfo;
import com.kk.hitplane.log.Level;
import com.kk.hitplane.log.Logger;

public class UserInfoDB {
	public static final String TB_NAME = "userinfo";

	public static final String COL_USERNAME = "username";
	public static final String COL_ID = "id";
	public static final String COL_NICKNAME = "nickname";

	public static final String COL_CREATE_TIME = "create_time";
	public static final String COL_LOGIN_TIME = "login_time";

	public static final String COL_WIN_COUNT = "win_count";
	public static final String COL_LOSE_COUNT = "lose_count";

	private static Statement getStatement() {
		return DatabaseUtil.getInstance().getStatement();
	}

	public static synchronized boolean login(UserInfo ui) {
		Statement stmt = null;

		try {
			stmt = getStatement();

			long now = System.currentTimeMillis();

			String sql = String.format("select * from %s where %s='%s'", TB_NAME, COL_USERNAME, ui.username);
			ResultSet rs = stmt.executeQuery(sql);

			if (!rs.next()) {
				rs.close();

				sql = String.format("insert into %s (%s, %s) values('%s', %d)", TB_NAME, COL_USERNAME, COL_CREATE_TIME,
						ui.username, now);
				int ct = stmt.executeUpdate(sql);

				if (ct != 1) {
					return false;
				}

				return login(ui);
			}

			ui.id = rs.getInt(COL_ID);
			ui.nickname = rs.getString(COL_NICKNAME);

			ui.winCount = rs.getInt(COL_WIN_COUNT);
			ui.loseCount = rs.getInt(COL_LOSE_COUNT);

			rs.close();

			handleUserInfo(ui);

			sql = String.format("update %s set %s=%d where %s='%s'", TB_NAME, COL_LOGIN_TIME, now, COL_USERNAME,
					ui.username);
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

	public static synchronized String setNickname(UserInfo ui, String nickname) {
		Statement stmt = null;

		try {
			stmt = getStatement();

			String sql = String.format("select * from %s where %s='%s'", TB_NAME, COL_NICKNAME, nickname);
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next()) {
				rs.close();
				return "昵称已被占用";
			}
			rs.close();

			sql = String.format("update %s set %s='%s' where %s=%d", TB_NAME, COL_NICKNAME, nickname, COL_ID, ui.id);
			int ct = stmt.executeUpdate(sql);

			if (ct != 1) {
				return "修改昵称数据失败";
			}

			ui.nickname = nickname;

			return null;
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return "修改昵称失败";
	}

	public static synchronized boolean updateBattleCount(int uid, int winDelta, int loseDelta) {
		Statement stmt = null;

		try {
			stmt = getStatement();

			String sql = String.format("update %s set %s=%s+%d, %s=%s+%d where %s=%d", TB_NAME, COL_WIN_COUNT,
					COL_WIN_COUNT, winDelta, COL_LOSE_COUNT, COL_LOSE_COUNT, loseDelta, COL_ID, uid);
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

	public static synchronized UserInfo getUserInfo(int id) {
		Statement stmt = null;

		try {
			stmt = getStatement();
			UserInfo ui = new UserInfo(null);

			String sql = String.format("select * from %s where %s=%d", TB_NAME, COL_ID, id);
			ResultSet rs = stmt.executeQuery(sql);

			if (!rs.next()) {
				rs.close();
				return null;
			}

			ui.username = rs.getString(COL_USERNAME);
			ui.id = id;
			ui.nickname = rs.getString(COL_NICKNAME);

			ui.winCount = rs.getInt(COL_WIN_COUNT);
			ui.loseCount = rs.getInt(COL_LOSE_COUNT);

			rs.close();

			handleUserInfo(ui);

			return ui;
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {
			}
		}

		return null;
	}

	private static void handleUserInfo(UserInfo ui) {
		if (ui.nickname == null || ui.nickname.equals("")) {
			ui.nickname = "无名" + ui.id;
		}
	}
}
