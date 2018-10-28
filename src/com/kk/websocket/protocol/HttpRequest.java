package com.kk.websocket.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.kk.websocket.log.Level;
import com.kk.websocket.log.Logger;

import java.util.Map.Entry;

public class HttpRequest {
	public String mothed;
	public String path;
	public String version;
	
	public final Map<String, String> head = new HashMap<>();
	
	public boolean decode(String str) {
		try {
			Scanner scanner = new Scanner(str);
			
			mothed = scanner.next();
			
			path = scanner.next();
			if (path.charAt(0) == '/') {
				path = path.substring(1);
			}
			int idx = path.indexOf(':');
			if (idx != -1) {
				path = path.substring(0, idx);
			}
			
			version = scanner.next();
			
			head.clear();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				int index = line.indexOf(':');
				if (index == -1) {
					continue;
				}
				
				String key = line.substring(0, index);
				String value = line.substring(index + 1).trim();
				head.put(key, value);
			}
			
			scanner.close();
			
			return true;
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("mothed : ").append(mothed).append('\n');
		sb.append("path : ").append(path).append('\n');
		sb.append("version : ").append(version).append('\n');
		
		for (Entry<String, String> entry : head.entrySet()) {
			sb.append(entry.getKey()).append(" : ")
				.append(entry.getValue()).append('\n');
		}
		
		return sb.toString();
	}
}
