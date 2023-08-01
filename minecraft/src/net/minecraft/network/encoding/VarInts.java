package net.minecraft.network.encoding;

import io.netty.buffer.ByteBuf;

public class VarInts {
	private static final int field_45682 = 5;
	private static final int field_45683 = 127;
	private static final int field_45684 = 128;
	private static final int field_45685 = 7;

	public static int getSizeInBytes(int i) {
		for (int j = 1; j < 5; j++) {
			if ((i & -1 << j * 7) == 0) {
				return j;
			}
		}

		return 5;
	}

	public static boolean shouldContinueRead(byte b) {
		return (b & 128) == 128;
	}

	public static int read(ByteBuf buf) {
		int i = 0;
		int j = 0;

		byte b;
		do {
			b = buf.readByte();
			i |= (b & 127) << j++ * 7;
			if (j > 5) {
				throw new RuntimeException("VarInt too big");
			}
		} while (shouldContinueRead(b));

		return i;
	}

	public static ByteBuf write(ByteBuf buf, int i) {
		while ((i & -128) != 0) {
			buf.writeByte(i & 127 | 128);
			i >>>= 7;
		}

		buf.writeByte(i);
		return buf;
	}
}
