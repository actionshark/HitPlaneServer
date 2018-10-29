package com.kk.websocket.protocol;

import java.util.ArrayList;
import java.util.List;

import com.kk.websocket.log.Level;
import com.kk.websocket.log.Logger;
import com.kk.websocket.util.DataUtil;

public class WebSocketPackage {
	public static final int OP_ADDITION = 0;
	public static final int OP_TEXT = 1;
	public static final int OP_BINARY = 2;
	public static final int OP_CLOSE = 8;
	public static final int OP_PING = 9;
	public static final int OP_PONG = 10;

	public static final int FRAME_SIZE = 125;

	public static List<byte[]> encode(byte[] bs) {
		return encode(bs, 0, bs.length);
	}

	public static List<byte[]> encode(byte[] bs, int off, int len) {
		return encode(OP_BINARY, bs, 0, bs.length);
	}

	public static List<byte[]> encode(String text) {
		try {
			byte[] bs = text.getBytes(DataUtil.CHARSET);
			return encode(OP_TEXT, bs, 0, bs.length);
		} catch (Exception e) {
			Logger.getInstance().print(null, Level.E, e);
		}

		return null;
	}

	private static List<byte[]> encode(int op, byte[] bs, int off, int len) {
		List<byte[]> list = new ArrayList<>();
		int count = (len + FRAME_SIZE - 1) / FRAME_SIZE;

		for (int i = 0; i < count; i++) {
			boolean fin = i == count - 1;

			int length = fin ? len % FRAME_SIZE : FRAME_SIZE;
			byte[] data = new byte[length + 2];
			list.add(data);

			int offset = 0;
			byte bt = 0;

			// fin
			bt |= fin ? 0b10000000 : 0;

			// rsv
			//

			// opcode
			bt |= i == 0 ? op : 0;

			data[offset++] = bt;

			bt = 0;

			// mask
			//

			bt |= length;
			data[offset++] = bt;

			while (offset < data.length) {
				data[offset++] = bs[off++];
			}
		}

		return list;
	}

	///////////////////////////////////////////////////////////////////////

	private byte[] mData;

	public boolean fin;

	public int opcode;

	public byte[] payloadData;

	public void reset() {
		mData = null;
		payloadData = null;
	}

	public byte[] decode(byte[] buf) {
		return decode(buf, 0, buf.length);
	}

	public byte[] decode(byte[] buf, int off, int len) {
		if (len <= 0) {
			return null;
		}

		int len0 = mData == null ? 0 : mData.length;
		byte[] data = new byte[len0 + len];

		for (int i = 0; i < len0; i++) {
			data[i] = mData[i];
		}

		for (int i = 0; i < len; i++) {
			data[len0 + i] = buf[off + i];
		}

		mData = data;

		try {
			off = decode();
			return DataUtil.cutBytes(mData, off, mData.length - off);
		} catch (Exception e) {
		}

		return null;
	}

	private int decode() throws Exception {
		int off = 0;

		byte bt = mData[off++];

		fin = (bt & 0b10000000) != 0;

		// if ((bt & 0b01110000) != 0) {
		// throw new Exception("");
		// }

		opcode = bt & 0b1111;

		bt = mData[off++];

		boolean mask = (bt & 0b10000000) != 0;

		long payloadLen = bt & 0b01111111;

		if (payloadLen < 126) {

		} else if (payloadLen == 126) {
			payloadLen = DataUtil.parseInt(mData, 2, 2);
			off += 2;
		} else {
			payloadLen = DataUtil.parseInt(mData, 2, 8);
			off += 8;
		}

		byte[] maskKey = null;
		if (mask) {
			maskKey = new byte[4];

			for (int i = 0; i < maskKey.length; i++) {
				maskKey[i] = mData[off++];
			}
		}
		int maskIdx = 0;

		payloadData = new byte[(int) payloadLen];
		for (int i = 0; i < payloadLen; i++) {
			payloadData[i] = mData[off++];

			if (mask) {
				payloadData[i] ^= maskKey[maskIdx];

				if (++maskIdx >= maskKey.length) {
					maskIdx = 0;
				}
			}
		}

		return off;
	}
}
