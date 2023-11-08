package net.minecraft.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record PngMetadata(int width, int height) {
	private static final long PNG_SIGNATURE = -8552249625308161526L;
	private static final int IHDR_CHUNK_TYPE = 1229472850;
	private static final int IHDR_CHUNK_LENGTH = 13;

	public static PngMetadata fromStream(InputStream stream) throws IOException {
		DataInputStream dataInputStream = new DataInputStream(stream);
		if (dataInputStream.readLong() != -8552249625308161526L) {
			throw new IOException("Bad PNG Signature");
		} else if (dataInputStream.readInt() != 13) {
			throw new IOException("Bad length for IHDR chunk!");
		} else if (dataInputStream.readInt() != 1229472850) {
			throw new IOException("Bad type for IHDR chunk!");
		} else {
			int i = dataInputStream.readInt();
			int j = dataInputStream.readInt();
			return new PngMetadata(i, j);
		}
	}

	public static PngMetadata fromBytes(byte[] bytes) throws IOException {
		return fromStream(new ByteArrayInputStream(bytes));
	}

	public static void validate(ByteBuffer buf) throws IOException {
		ByteOrder byteOrder = buf.order();
		buf.order(ByteOrder.BIG_ENDIAN);
		if (buf.getLong(0) != -8552249625308161526L) {
			throw new IOException("Bad PNG Signature");
		} else if (buf.getInt(8) != 13) {
			throw new IOException("Bad length for IHDR chunk!");
		} else if (buf.getInt(12) != 1229472850) {
			throw new IOException("Bad type for IHDR chunk!");
		} else {
			buf.order(byteOrder);
		}
	}
}
