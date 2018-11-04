package com.kk.hitplane.request;

import java.util.List;

import com.kk.hitplane.Request;
import com.kk.hitplane.game.Room;
import com.kk.hitplane.game.Room.User;
import com.kk.hitplane.game.RoomMgr;
import com.kk.hitplane.response.RoomInfo;

public class GetRoomInfo extends Request {
	public int id = 0;

	@Override
	public boolean exe() {
		Room room = null;
		RoomMgr mgr = RoomMgr.getInstance();
		
		if (id == 0) {
			List<Room> rooms = mgr.getRooms();
			
			for (Room rm : rooms) {
				List<User> users = rm.getUsers();
				
				for (User user : users) {
					if (user.id == mUserInfo.id) {
						room = rm;
						break;
					}
				}
				
				if (room != null) {
					break;
				}
			}
		} else {
			room = mgr.getRoom(id);
		}
		
		if (room == null) {
			return false;
		}
		
		RoomInfo ri = new RoomInfo();
		ri.encode(room, mUserInfo.id);
		ri.send(mUserInfo);
		
		return true;
	}
}
