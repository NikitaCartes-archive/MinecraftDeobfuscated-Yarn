package net.minecraft;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class class_3345 {
	private final ByteArrayOutputStream field_14395;
	private final DataOutputStream field_14396;

	public class_3345(int i) {
		this.field_14395 = new ByteArrayOutputStream(i);
		this.field_14396 = new DataOutputStream(this.field_14395);
	}

	public void method_14694(byte[] bs) throws IOException {
		this.field_14396.write(bs, 0, bs.length);
	}

	public void method_14690(String string) throws IOException {
		this.field_14396.writeBytes(string);
		this.field_14396.write(0);
	}

	public void method_14692(int i) throws IOException {
		this.field_14396.write(i);
	}

	public void method_14691(short s) throws IOException {
		this.field_14396.writeShort(Short.reverseBytes(s));
	}

	public byte[] method_14689() {
		return this.field_14395.toByteArray();
	}

	public void method_14693() {
		this.field_14395.reset();
	}
}
