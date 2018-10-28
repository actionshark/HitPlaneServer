package com.kk.websocket.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {
	public String version;
	public int code;
	public String proto;
	
	public final Map<String, String> head = new HashMap<>();
	
	public String encode() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(version).append(' ')
			.append(code).append(' ')
			.append(proto).append("\r\n");
		
		for (Entry<String, String> entry : head.entrySet()) {
			sb.append(entry.getKey()).append(": ")
				.append(entry.getValue()).append("\r\n");
		}
		
		sb.append("\r\n");
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("version : ").append(version).append('\n');
		sb.append("code : ").append(code).append('\n');
		sb.append("proto : ").append(proto).append('\n');
		
		for (Entry<String, String> entry : head.entrySet()) {
			sb.append(entry.getKey()).append(" : ")
				.append(entry.getValue()).append('\n');
		}
		
		return sb.toString();
	}
}
