package com.kk.hitplane.reponse;

import com.kk.hitplane.Response;

public class UserInfo extends Response {
	public int id;
	public String nickname;
	public int status;

	public void encode(com.kk.hitplane.UserInfo ui) {
		id = ui.id;
		nickname = ui.nickname;
		status = ui.status;
	}
}
