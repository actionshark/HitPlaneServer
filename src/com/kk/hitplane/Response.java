package com.kk.hitplane;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kk.websocket.log.Level;
import com.kk.websocket.log.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class Response {
	protected static JSONObject packObject(Object obj) {
		try {
			JSONObject jo = new JSONObject();
			Field[] fields = obj.getClass().getFields();
			
			for (Field field : fields) {
				int modifiers = field.getModifiers();
				
				if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & Modifier.STATIC) == 0) {
					Object value = field.get(obj);
					
					if (value != null) {
						jo.put(field.getName(), packField(value));
					}
				}
			}
			
			return jo;
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}
		
		return null;
	}
	
	protected static Object packField(Object value) {
		if (value instanceof Boolean) {
			return value;
		}
		
		if (value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) {
			return value;
		}
		
		if (value instanceof Character || value instanceof String) {
			return value.toString();
		}
		
		if (value instanceof JSONObject || value instanceof JSONArray) {
			return value;
		}
		
		if (value instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) value;
			JSONObject jo = new JSONObject();
			
			for (Entry<?, ?> entry : map.entrySet()) {
				Object key = entry.getKey();
				Object val = entry.getValue();
				
				jo.put(key.toString(), packObject(val));
			}
			
			return jo;
		}
		
		if (value instanceof List) {
			List<?> list = (List<?>) value;
			JSONArray ja = new JSONArray();
			
			for (int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				
				ja.put(i, packObject(obj));
			}
			
			return ja;
		}
		
		return packObject(value);
	}
	
	public JSONObject pack() {
		JSONObject jo = packObject(this);
		jo.put(Request.KEY_NAME, this.getClass().getSimpleName());
		
		return jo;
	}
	
	public boolean send(UserInfo userInfo) {
		try {
			JSONObject jo = pack();
			userInfo.session.send(jo.toString());
			return true;
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}
		
		return false;
	}
}
