package com.kk.hitplane.reponse;

import com.kk.hitplane.Response;
import com.kk.hitplane.Server;

public class RequestEnd extends Response {
	public int id;
	public String reason;

	public static void noticeAll(int a, int b, String reason) {
		Server server = Server.getInstance();
		
		RequestEnd res = new RequestEnd();
		res.reason = reason;

		res.id = b;
		res.send(server.getUserInfo(a));

		res.id = a;
		res.send(server.getUserInfo(b));
	}
}
