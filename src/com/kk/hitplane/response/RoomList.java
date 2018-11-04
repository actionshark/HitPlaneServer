package com.kk.hitplane.response;

import java.util.ArrayList;
import java.util.List;

import com.kk.hitplane.Response;
import com.kk.hitplane.database.UserInfoDB;
import com.kk.hitplane.game.Room;
import com.kk.hitplane.game.RoomMgr;
import com.kk.hitplane.game.Room.User;

public class RoomList extends Response {
	public static class UserInfo {
		public int id;
		public String nickname;
		public boolean status;
	}

	public static class RoomInfo {
		public int id;
		public boolean running;

		public List<UserInfo> users = new ArrayList<>();
	}

	public List<RoomInfo> rooms = new ArrayList<>();

	public void encode() {
		List<Room> rooms = RoomMgr.getInstance().getRooms();

		for (Room room : rooms) {
			RoomInfo ri = new RoomInfo();
			this.rooms.add(ri);

			ri.id = room.id;
			ri.running = room.isRunning();

			for (User user : room.getUsers()) {
				UserInfo ui = new UserInfo();
				ri.users.add(ui);

				ui.id = user.id;
				ui.nickname = UserInfoDB.getUserInfo(user.id).nickname;

				if (ri.running) {
					ui.status = user.alive;
				} else {
					ui.status = user.ready;
				}
			}
		}
	}
}
