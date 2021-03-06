package com.kk.hitplane;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.kk.hitplane.log.Level;
import com.kk.hitplane.log.Logger;
import com.kk.hitplane.request.Login;
import com.kk.hitplane.response.NeedLogin;

import net.sf.json.JSONObject;

public abstract class Request {
	public static final String PKG = "com.kk.hitplane.request";

	public static final String KEY_NAME = "name";

	public static boolean dispatch(UserInfo userInfo, String str) {
		try {
			JSONObject jo = JSONObject.fromString(str);
			String name = jo.getString(KEY_NAME);

			Class<?> clazz = Class.forName(PKG + "." + name);
			if (userInfo.id == 0 && clazz != Login.class) {
				NeedLogin nl = new NeedLogin();
				nl.send(userInfo);
				return false;
			}

			Request instance = (Request) clazz.newInstance();
			instance.mUserInfo = userInfo;
			instance.mJson = jo;

			for (Field field : clazz.getFields()) {
				int modifiers = field.getModifiers();
				if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & Modifier.STATIC) == 0) {
					String fieldName = field.getName();
					if (jo.has(fieldName)) {
						field.set(instance, jo.get(fieldName));
					}
				}
			}

			return instance.exe();
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}

		return false;
	}

	//////////////////////////////////////////////////////

	protected UserInfo mUserInfo;

	protected JSONObject mJson;

	public abstract boolean exe();
}
