package com.kk.hitplane.request;

import com.kk.hitplane.Request;

public class Call extends Request {
	public static final int TYPE_GIVEUP = 1;
	public static final int TYPE_NORMAL = 2;
	public static final int TYPE_DOUBLE = 3;

	public int type;
	public int pk = 0;

	@Override
	public boolean exe() {
		return false;
	}

}
