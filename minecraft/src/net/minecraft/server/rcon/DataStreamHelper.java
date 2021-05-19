package net.minecraft.server.rcon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataStreamHelper {
	private final ByteArrayOutputStream byteArrayOutputStream;
	private final DataOutputStream dataOutputStream;

	public DataStreamHelper(int size) {
		this.byteArrayOutputStream = new ByteArrayOutputStream(size);
		this.dataOutputStream = new DataOutputStream(this.byteArrayOutputStream);
	}

	public void write(byte[] values) throws IOException {
		this.dataOutputStream.write(values, 0, values.length);
	}

	public void writeBytes(String value) throws IOException {
		this.dataOutputStream.writeBytes(value);
		this.dataOutputStream.write(0);
	}

	public void write(int value) throws IOException {
		this.dataOutputStream.write(value);
	}

	public void writeShort(short value) throws IOException {
		this.dataOutputStream.writeShort(Short.reverseBytes(value));
	}

	public void writeInt(int value) throws IOException {
		this.dataOutputStream.writeInt(Integer.reverseBytes(value));
	}

	public void writeFloat(float value) throws IOException {
		this.dataOutputStream.writeInt(Integer.reverseBytes(Float.floatToIntBits(value)));
	}

	public byte[] bytes() {
		return this.byteArrayOutputStream.toByteArray();
	}

	public void reset() {
		this.byteArrayOutputStream.reset();
	}
}
