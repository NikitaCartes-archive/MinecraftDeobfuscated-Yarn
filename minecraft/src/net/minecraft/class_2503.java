package net.minecraft;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class class_2503 extends class_2514 {
	private long field_11553;

	class_2503() {
	}

	public class_2503(long l) {
		this.field_11553 = l;
	}

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		dataOutput.writeLong(this.field_11553);
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(128L);
		this.field_11553 = dataInput.readLong();
	}

	@Override
	public byte method_10711() {
		return 4;
	}

	@Override
	public String toString() {
		return this.field_11553 + "L";
	}

	public class_2503 method_10621() {
		return new class_2503(this.field_11553);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2503 && this.field_11553 == ((class_2503)object).field_11553;
	}

	public int hashCode() {
		return (int)(this.field_11553 ^ this.field_11553 >>> 32);
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		class_2561 lv = new class_2585("L").method_10854(field_11595);
		return new class_2585(String.valueOf(this.field_11553)).method_10852(lv).method_10854(field_11593);
	}

	@Override
	public long method_10699() {
		return this.field_11553;
	}

	@Override
	public int method_10701() {
		return (int)(this.field_11553 & -1L);
	}

	@Override
	public short method_10696() {
		return (short)((int)(this.field_11553 & 65535L));
	}

	@Override
	public byte method_10698() {
		return (byte)((int)(this.field_11553 & 255L));
	}

	@Override
	public double method_10697() {
		return (double)this.field_11553;
	}

	@Override
	public float method_10700() {
		return (float)this.field_11553;
	}

	@Override
	public Number method_10702() {
		return this.field_11553;
	}
}
