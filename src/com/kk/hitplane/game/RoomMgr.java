package com.kk.hitplane.game;

import java.util.ArrayList;
import java.util.List;

import com.kk.hitplane.game.Room.User;

public class RoomMgr {
	private static final RoomMgr sInstance = new RoomMgr();

	public static RoomMgr getInstance() {
		return sInstance;
	}

	public static final int NUM = 6;

	/////////////////////////////////////////////////////////////////

	private final List<Room> mRooms = new ArrayList<>();

	private RoomMgr() {
		for (int i = 0; i < NUM; i++) {
			Room room = new Room();
			mRooms.add(room);
		}
	}

	public synchronized List<Room> getRooms() {
		return new ArrayList<>(mRooms);
	}

	public synchronized Room getRoom(int id) {
		for (Room room : mRooms) {
			if (room.id == id) {
				return room;
			}
		}

		return null;
	}

	public synchronized String join(int userId, int roomId) {
		for (Room room : mRooms) {
			List<User> users = room.getUsers();
			for (User user : users) {
				if (user.id == userId) {
					String error = room.quit(userId);
					if (error != null) {
						return error;
					}

					break;
				}
			}
		}

		for (Room room : mRooms) {
			if (room.id == roomId) {
				return room.join(roomId);
			}
		}

		return "没有找到该房间";
	}
}
