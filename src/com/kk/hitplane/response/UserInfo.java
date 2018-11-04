package com.kk.hitplane.response;

import com.kk.hitplane.Response;

public class UserInfo extends Response {
	public int id;
	public String nickname;
	public int money;

	public void encode(com.kk.hitplane.UserInfo ui) {
		id = ui.id;
		nickname = ui.nickname;
		money = ui.money;
	}
}
