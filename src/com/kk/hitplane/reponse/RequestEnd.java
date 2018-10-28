package com.kk.hitplane.reponse;

import com.kk.hitplane.Response;
import com.kk.hitplane.UserInfo;

public class RequestEnd extends Response {
	public int id;
	public String reason;
	
	public static void noticeAll(UserInfo a, UserInfo b, String reason) {
		RequestEnd res = new RequestEnd();
		res.reason = reason;
		
		res.id = b.id;
		res.send(a);
		
		res.id = a.id;
		res.send(b);
	}
}
