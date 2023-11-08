package net.minecraft.util;

import java.io.DataOutput;
import java.io.IOException;

public class DelegatingDataOutput implements DataOutput {
	private final DataOutput delegate;

	public DelegatingDataOutput(DataOutput delegate) {
		this.delegate = delegate;
	}

	public void write(int v) throws IOException {
		this.delegate.write(v);
	}

	public void write(byte[] b) throws IOException {
		this.delegate.write(b);
	}

	public void write(byte[] bs, int off, int len) throws IOException {
		this.delegate.write(bs, off, len);
	}

	public void writeBoolean(boolean v) throws IOException {
		this.delegate.writeBoolean(v);
	}

	public void writeByte(int v) throws IOException {
		this.delegate.writeByte(v);
	}

	public void writeShort(int v) throws IOException {
		this.delegate.writeShort(v);
	}

	public void writeChar(int v) throws IOException {
		this.delegate.writeChar(v);
	}

	public void writeInt(int v) throws IOException {
		this.delegate.writeInt(v);
	}

	public void writeLong(long v) throws IOException {
		this.delegate.writeLong(v);
	}

	public void writeFloat(float v) throws IOException {
		this.delegate.writeFloat(v);
	}

	public void writeDouble(double v) throws IOException {
		this.delegate.writeDouble(v);
	}

	public void writeBytes(String s) throws IOException {
		this.delegate.writeBytes(s);
	}

	public void writeChars(String s) throws IOException {
		this.delegate.writeChars(s);
	}

	public void writeUTF(String s) throws IOException {
		this.delegate.writeUTF(s);
	}
}
