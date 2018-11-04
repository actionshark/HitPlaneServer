package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.response.RoomList;

public class GetRoomList extends Request {
	@Override
	public boolean exe() {
		RoomList rl = new RoomList();
		rl.encode();
		rl.send(mUserInfo);
		return true;
	}
}
