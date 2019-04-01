package net.minecraft;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class class_2494 extends class_2514 {
	private float field_11523;

	class_2494() {
	}

	public class_2494(float f) {
		this.field_11523 = f;
	}

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		dataOutput.writeFloat(this.field_11523);
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(96L);
		this.field_11523 = dataInput.readFloat();
	}

	@Override
	public byte method_10711() {
		return 5;
	}

	@Override
	public String toString() {
		return this.field_11523 + "f";
	}

	public class_2494 method_10587() {
		return new class_2494(this.field_11523);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2494 && this.field_11523 == ((class_2494)object).field_11523;
	}

	public int hashCode() {
		return Float.floatToIntBits(this.field_11523);
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		class_2561 lv = new class_2585("f").method_10854(field_11595);
		return new class_2585(String.valueOf(this.field_11523)).method_10852(lv).method_10854(field_11593);
	}

	@Override
	public long method_10699() {
		return (long)this.field_11523;
	}

	@Override
	public int method_10701() {
		return class_3532.method_15375(this.field_11523);
	}

	@Override
	public short method_10696() {
		return (short)(class_3532.method_15375(this.field_11523) & 65535);
	}

	@Override
	public byte method_10698() {
		return (byte)(class_3532.method_15375(this.field_11523) & 0xFF);
	}

	@Override
	public double method_10697() {
		return (double)this.field_11523;
	}

	@Override
	public float method_10700() {
		return this.field_11523;
	}

	@Override
	public Number method_10702() {
		return this.field_11523;
	}
}
