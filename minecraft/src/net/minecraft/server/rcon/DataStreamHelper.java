package net.minecraft.server.rcon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataStreamHelper {
	private final ByteArrayOutputStream byteArrayOutputStream;
	private final DataOutputStream dataOutputStream;

	public DataStreamHelper(int i) {
		this.byteArrayOutputStream = new ByteArrayOutputStream(i);
		this.dataOutputStream = new DataOutputStream(this.byteArrayOutputStream);
	}

	public void write(byte[] bs) throws IOException {
		this.dataOutputStream.write(bs, 0, bs.length);
	}

	public void writeBytes(String string) throws IOException {
		this.dataOutputStream.writeBytes(string);
		this.dataOutputStream.write(0);
	}

	public void write(int i) throws IOException {
		this.dataOutputStream.write(i);
	}

	public void writeShort(short s) throws IOException {
		this.dataOutputStream.writeShort(Short.reverseBytes(s));
	}

	public byte[] bytes() {
		return this.byteArrayOutputStream.toByteArray();
	}

	public void reset() {
		this.byteArrayOutputStream.reset();
	}
}
