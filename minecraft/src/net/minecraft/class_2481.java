package net.minecraft;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class class_2481 extends class_2514 {
	private byte field_11498;

	class_2481() {
	}

	public class_2481(byte b) {
		this.field_11498 = b;
	}

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		dataOutput.writeByte(this.field_11498);
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(72L);
		this.field_11498 = dataInput.readByte();
	}

	@Override
	public byte method_10711() {
		return 1;
	}

	@Override
	public String toString() {
		return this.field_11498 + "b";
	}

	public class_2481 method_10530() {
		return new class_2481(this.field_11498);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2481 && this.field_11498 == ((class_2481)object).field_11498;
	}

	public int hashCode() {
		return this.field_11498;
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		class_2561 lv = new class_2585("b").method_10854(field_11595);
		return new class_2585(String.valueOf(this.field_11498)).method_10852(lv).method_10854(field_11593);
	}

	@Override
	public long method_10699() {
		return (long)this.field_11498;
	}

	@Override
	public int method_10701() {
		return this.field_11498;
	}

	@Override
	public short method_10696() {
		return (short)this.field_11498;
	}

	@Override
	public byte method_10698() {
		return this.field_11498;
	}

	@Override
	public double method_10697() {
		return (double)this.field_11498;
	}

	@Override
	public float method_10700() {
		return (float)this.field_11498;
	}

	@Override
	public Number method_10702() {
		return this.field_11498;
	}
}
