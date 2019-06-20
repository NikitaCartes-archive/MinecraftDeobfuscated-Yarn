package net.minecraft;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class class_2489 extends class_2514 {
	private double field_11520;

	class_2489() {
	}

	public class_2489(double d) {
		this.field_11520 = d;
	}

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		dataOutput.writeDouble(this.field_11520);
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(128L);
		this.field_11520 = dataInput.readDouble();
	}

	@Override
	public byte method_10711() {
		return 6;
	}

	@Override
	public String toString() {
		return this.field_11520 + "d";
	}

	public class_2489 method_10585() {
		return new class_2489(this.field_11520);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2489 && this.field_11520 == ((class_2489)object).field_11520;
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.field_11520);
		return (int)(l ^ l >>> 32);
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		class_2561 lv = new class_2585("d").method_10854(field_11595);
		return new class_2585(String.valueOf(this.field_11520)).method_10852(lv).method_10854(field_11593);
	}

	@Override
	public long method_10699() {
		return (long)Math.floor(this.field_11520);
	}

	@Override
	public int method_10701() {
		return class_3532.method_15357(this.field_11520);
	}

	@Override
	public short method_10696() {
		return (short)(class_3532.method_15357(this.field_11520) & 65535);
	}

	@Override
	public byte method_10698() {
		return (byte)(class_3532.method_15357(this.field_11520) & 0xFF);
	}

	@Override
	public double method_10697() {
		return this.field_11520;
	}

	@Override
	public float method_10700() {
		return (float)this.field_11520;
	}

	@Override
	public Number method_10702() {
		return this.field_11520;
	}
}
