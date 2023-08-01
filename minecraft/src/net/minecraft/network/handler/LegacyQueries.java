package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class LegacyQueries {
	public static final int HEADER = 250;
	public static final String PING_HOST = "MC|PingHost";
	public static final int QUERY_PACKET_ID = 254;
	public static final int field_44995 = 1;
	public static final int BUFFER_SIZE = 255;
	public static final int PROTOCOL_VERSION = 127;

	public static void write(ByteBuf buf, String string) {
		buf.writeShort(string.length());
		buf.writeCharSequence(string, StandardCharsets.UTF_16BE);
	}

	public static String read(ByteBuf buf) {
		int i = buf.readShort();
		int j = i * 2;
		String string = buf.toString(buf.readerIndex(), j, StandardCharsets.UTF_16BE);
		buf.skipBytes(j);
		return string;
	}
}
