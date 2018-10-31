package com.kk.websocket.util;

import java.io.IOException;
import java.io.InputStream;

public class DataUtil {
	public static final String CHARSET = "UTF-8";

	public static byte[] readBytes(InputStream is) throws IOException {
		byte[] buf = new byte[1024 * 64];
		int len = is.read(buf);
		return cutBytes(buf, 0, len);
	}

	public static String readString(InputStream is) throws IOException {
		byte[] bs = readBytes(is);
		return new String(bs, CHARSET);
	}

	public static byte[] cutBytes(byte[] buf, int off, int len) {
		byte[] data = new byte[len];

		for (int i = 0; i < len; i++) {
			data[i] = buf[off + i];
		}

		return data;
	}

	public static long parseInt(byte[] buf, int off, int len) {
		long ret = 0;

		for (int i = 0; i < len; i++) {
			ret = (ret << 8) + (buf[off + i] & 0xff);
		}

		return ret;
	}

	public static void writeInt(long num, byte[] buf, int off, int len) {
		for (int i = len - 1; i >= 0; i--) {
			buf[off + i] = (byte) (num & 0xff);
		}
	}
}
