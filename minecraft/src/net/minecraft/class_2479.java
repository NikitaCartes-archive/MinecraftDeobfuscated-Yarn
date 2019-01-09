package net.minecraft;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class class_2479 extends class_2483<class_2481> {
	private byte[] field_11493;

	class_2479() {
	}

	public class_2479(byte[] bs) {
		this.field_11493 = bs;
	}

	public class_2479(List<Byte> list) {
		this(method_10522(list));
	}

	private static byte[] method_10522(List<Byte> list) {
		byte[] bs = new byte[list.size()];

		for (int i = 0; i < list.size(); i++) {
			Byte byte_ = (Byte)list.get(i);
			bs[i] = byte_ == null ? 0 : byte_;
		}

		return bs;
	}

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(this.field_11493.length);
		dataOutput.write(this.field_11493);
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(192L);
		int j = dataInput.readInt();
		arg.method_10623((long)(8 * j));
		this.field_11493 = new byte[j];
		dataInput.readFully(this.field_11493);
	}

	@Override
	public byte method_10711() {
		return 7;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[B;");

		for (int i = 0; i < this.field_11493.length; i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.field_11493[i]).append('B');
		}

		return stringBuilder.append(']').toString();
	}

	@Override
	public class_2520 method_10707() {
		byte[] bs = new byte[this.field_11493.length];
		System.arraycopy(this.field_11493, 0, bs, 0, this.field_11493.length);
		return new class_2479(bs);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2479 && Arrays.equals(this.field_11493, ((class_2479)object).field_11493);
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_11493);
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		class_2561 lv = new class_2585("B").method_10854(field_11595);
		class_2561 lv2 = new class_2585("[").method_10852(lv).method_10864(";");

		for (int j = 0; j < this.field_11493.length; j++) {
			class_2561 lv3 = new class_2585(String.valueOf(this.field_11493[j])).method_10854(field_11593);
			lv2.method_10864(" ").method_10852(lv3).method_10852(lv);
			if (j != this.field_11493.length - 1) {
				lv2.method_10864(",");
			}
		}

		lv2.method_10864("]");
		return lv2;
	}

	public byte[] method_10521() {
		return this.field_11493;
	}

	@Override
	public int size() {
		return this.field_11493.length;
	}

	public class_2481 method_10523(int i) {
		return new class_2481(this.field_11493[i]);
	}

	@Override
	public void method_10535(int i, class_2520 arg) {
		this.field_11493[i] = ((class_2514)arg).method_10698();
	}

	@Override
	public void method_10533(int i, class_2520 arg) {
		this.field_11493 = ArrayUtils.add(this.field_11493, i, ((class_2514)arg).method_10698());
	}

	@Override
	public void method_10532(int i) {
		this.field_11493 = ArrayUtils.remove(this.field_11493, i);
	}

	public void clear() {
		this.field_11493 = new byte[0];
	}
}
