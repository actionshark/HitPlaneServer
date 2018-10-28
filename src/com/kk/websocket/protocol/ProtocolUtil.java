package com.kk.websocket.protocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;

import com.kk.websocket.log.Level;
import com.kk.websocket.log.Logger;
import com.kk.websocket.util.DataUtil;
import com.kk.websocket.util.StringUtil;

public class ProtocolUtil {
	public static final String MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
	
	public static String handshake(Socket socket) {
		try {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			HttpRequest req = new HttpRequest();
			req.decode(DataUtil.readString(is));
			
			HttpResponse res = new HttpResponse();
			
			res.version = "HTTP/1.1";
			res.code = 101;
			res.proto = "Switching Protocols";
			
			res.head.put("Upgrade", "websocket");
			res.head.put("Connection", "Upgrade");
			
			String magic = req.head.get("Sec-WebSocket-Key") + MAGIC;
			byte[] sha1 = StringUtil.sha1(magic.getBytes());
			byte[] base64 = Base64.getEncoder().encode(sha1);
			res.head.put("Sec-WebSocket-Accept", new String(base64));
			
			String str = res.encode();
			
			os.write(str.getBytes());
			os.flush();
			
			return req.path;
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}
		
		return null;
	}
}
