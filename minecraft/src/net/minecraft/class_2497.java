package net.minecraft;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class class_2497 extends class_2514 {
	private int field_11525;

	class_2497() {
	}

	public class_2497(int i) {
		this.field_11525 = i;
	}

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(this.field_11525);
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(96L);
		this.field_11525 = dataInput.readInt();
	}

	@Override
	public byte method_10711() {
		return 3;
	}

	@Override
	public String toString() {
		return String.valueOf(this.field_11525);
	}

	public class_2497 method_10592() {
		return new class_2497(this.field_11525);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2497 && this.field_11525 == ((class_2497)object).field_11525;
	}

	public int hashCode() {
		return this.field_11525;
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		return new class_2585(String.valueOf(this.field_11525)).method_10854(field_11593);
	}

	@Override
	public long method_10699() {
		return (long)this.field_11525;
	}

	@Override
	public int method_10701() {
		return this.field_11525;
	}

	@Override
	public short method_10696() {
		return (short)(this.field_11525 & 65535);
	}

	@Override
	public byte method_10698() {
		return (byte)(this.field_11525 & 0xFF);
	}

	@Override
	public double method_10697() {
		return (double)this.field_11525;
	}

	@Override
	public float method_10700() {
		return (float)this.field_11525;
	}

	@Override
	public Number method_10702() {
		return this.field_11525;
	}
}
