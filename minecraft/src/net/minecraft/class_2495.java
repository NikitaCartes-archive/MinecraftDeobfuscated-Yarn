package net.minecraft;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class class_2495 extends class_2483<class_2497> {
	private int[] field_11524;

	class_2495() {
	}

	public class_2495(int[] is) {
		this.field_11524 = is;
	}

	public class_2495(List<Integer> list) {
		this(method_10590(list));
	}

	private static int[] method_10590(List<Integer> list) {
		int[] is = new int[list.size()];

		for (int i = 0; i < list.size(); i++) {
			Integer integer = (Integer)list.get(i);
			is[i] = integer == null ? 0 : integer;
		}

		return is;
	}

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(this.field_11524.length);

		for (int i : this.field_11524) {
			dataOutput.writeInt(i);
		}
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(192L);
		int j = dataInput.readInt();
		arg.method_10623((long)(32 * j));
		this.field_11524 = new int[j];

		for (int k = 0; k < j; k++) {
			this.field_11524[k] = dataInput.readInt();
		}
	}

	@Override
	public byte method_10711() {
		return 11;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[I;");

		for (int i = 0; i < this.field_11524.length; i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.field_11524[i]);
		}

		return stringBuilder.append(']').toString();
	}

	public class_2495 method_10591() {
		int[] is = new int[this.field_11524.length];
		System.arraycopy(this.field_11524, 0, is, 0, this.field_11524.length);
		return new class_2495(is);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2495 && Arrays.equals(this.field_11524, ((class_2495)object).field_11524);
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_11524);
	}

	public int[] method_10588() {
		return this.field_11524;
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		class_2561 lv = new class_2585("I").method_10854(field_11595);
		class_2561 lv2 = new class_2585("[").method_10852(lv).method_10864(";");

		for (int j = 0; j < this.field_11524.length; j++) {
			lv2.method_10864(" ").method_10852(new class_2585(String.valueOf(this.field_11524[j])).method_10854(field_11593));
			if (j != this.field_11524.length - 1) {
				lv2.method_10864(",");
			}
		}

		lv2.method_10864("]");
		return lv2;
	}

	@Override
	public int size() {
		return this.field_11524.length;
	}

	public class_2497 method_10589(int i) {
		return new class_2497(this.field_11524[i]);
	}

	@Override
	public void method_10533(int i, class_2520 arg) {
		this.field_11524 = ArrayUtils.add(this.field_11524, i, ((class_2514)arg).method_10701());
	}

	@Override
	public void method_10535(int i, class_2520 arg) {
		this.field_11524[i] = ((class_2514)arg).method_10701();
	}

	@Override
	public void method_10532(int i) {
		this.field_11524 = ArrayUtils.remove(this.field_11524, i);
	}

	public void clear() {
		this.field_11524 = new int[0];
	}
}
