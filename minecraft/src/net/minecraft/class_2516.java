package net.minecraft;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class class_2516 extends class_2514 {
	private short field_11588;

	public class_2516() {
	}

	public class_2516(short s) {
		this.field_11588 = s;
	}

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		dataOutput.writeShort(this.field_11588);
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(80L);
		this.field_11588 = dataInput.readShort();
	}

	@Override
	public byte method_10711() {
		return 2;
	}

	@Override
	public String toString() {
		return this.field_11588 + "s";
	}

	public class_2516 method_10704() {
		return new class_2516(this.field_11588);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2516 && this.field_11588 == ((class_2516)object).field_11588;
	}

	public int hashCode() {
		return this.field_11588;
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		class_2561 lv = new class_2585("s").method_10854(field_11595);
		return new class_2585(String.valueOf(this.field_11588)).method_10852(lv).method_10854(field_11593);
	}

	@Override
	public long method_10699() {
		return (long)this.field_11588;
	}

	@Override
	public int method_10701() {
		return this.field_11588;
	}

	@Override
	public short method_10696() {
		return this.field_11588;
	}

	@Override
	public byte method_10698() {
		return (byte)(this.field_11588 & 255);
	}

	@Override
	public double method_10697() {
		return (double)this.field_11588;
	}

	@Override
	public float method_10700() {
		return (float)this.field_11588;
	}

	@Override
	public Number method_10702() {
		return this.field_11588;
	}
}
