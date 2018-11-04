package com.kk.hitplane.request;

import com.kk.hitplane.Request;
import com.kk.hitplane.game.RoomMgr;
import com.kk.hitplane.response.ShowToast;

public class JoinRoom extends Request {
	public int id;

	@Override
	public boolean exe() {
		String error = RoomMgr.getInstance().join(mUserInfo.id, id);

		if (error == null) {
			new GetRoomInfo().exe();
			return true;
		} else {
			ShowToast toast = new ShowToast();
			toast.text = error;
			toast.send(mUserInfo);
			return false;
		}
	}
}
