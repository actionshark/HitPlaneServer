package com.kk.hitplane.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.kk.hitplane.log.Level;
import com.kk.hitplane.log.Logger;

public class StringUtil {
	public static byte[] sha1(byte[] data) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(data);
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			Logger.getInstance().print(null, Level.E, e);
		}

		return null;
	}
}
