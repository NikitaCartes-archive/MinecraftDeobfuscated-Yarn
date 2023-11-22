package net.minecraft.network.encoding;

import io.netty.buffer.ByteBuf;

public class VarLongs {
	private static final int MAX_BYTES = 10;
	private static final int DATA_BITS_MASK = 127;
	private static final int MORE_BITS_MASK = 128;
	private static final int DATA_BITS_PER_BYTE = 7;

	public static int getSizeInBytes(long l) {
		for (int i = 1; i < MAX_BYTES; i++) {
			if ((l & -1L << i * 7) == 0L) {
				return i;
			}
		}

		return MAX_BYTES;
	}

	public static boolean shouldContinueRead(byte b) {
		return (b & 128) == 128;
	}

	public static long read(ByteBuf buf) {
		long l = 0L;
		int i = 0;

		byte b;
		do {
			b = buf.readByte();
			l |= (long)(b & 127) << i++ * 7;
			if (i > 10) {
				throw new RuntimeException("VarLong too big");
			}
		} while (shouldContinueRead(b));

		return l;
	}

	public static ByteBuf write(ByteBuf buf, long l) {
		while ((l & -128L) != 0L) {
			buf.writeByte((int)(l & 127L) | 128);
			l >>>= 7;
		}

		buf.writeByte((int)l);
		return buf;
	}
}
