package com.kk.hitplane.reponse;

import com.kk.hitplane.Response;

public class RequestEnd extends Response {
	public int id;
	public String reason;

	public static void noticeAll(int a, int b, String reason) {
		RequestEnd res = new RequestEnd();
		res.reason = reason;

		res.id = b;
		res.send(a);

		res.id = a;
		res.send(b);
	}
}
