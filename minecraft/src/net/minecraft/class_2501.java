package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class class_2501 extends class_2483<class_2503> {
	private long[] field_11552;

	class_2501() {
	}

	public class_2501(long[] ls) {
		this.field_11552 = ls;
	}

	public class_2501(LongSet longSet) {
		this.field_11552 = longSet.toLongArray();
	}

	public class_2501(List<Long> list) {
		this(method_10617(list));
	}

	private static long[] method_10617(List<Long> list) {
		long[] ls = new long[list.size()];

		for (int i = 0; i < list.size(); i++) {
			Long long_ = (Long)list.get(i);
			ls[i] = long_ == null ? 0L : long_;
		}

		return ls;
	}

	@Override
	public void method_10713(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(this.field_11552.length);

		for (long l : this.field_11552) {
			dataOutput.writeLong(l);
		}
	}

	@Override
	public void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException {
		arg.method_10623(192L);
		int j = dataInput.readInt();
		arg.method_10623((long)(64 * j));
		this.field_11552 = new long[j];

		for (int k = 0; k < j; k++) {
			this.field_11552[k] = dataInput.readLong();
		}
	}

	@Override
	public byte method_10711() {
		return 12;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[L;");

		for (int i = 0; i < this.field_11552.length; i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.field_11552[i]).append('L');
		}

		return stringBuilder.append(']').toString();
	}

	public class_2501 method_10618() {
		long[] ls = new long[this.field_11552.length];
		System.arraycopy(this.field_11552, 0, ls, 0, this.field_11552.length);
		return new class_2501(ls);
	}

	public boolean equals(Object object) {
		return this == object ? true : object instanceof class_2501 && Arrays.equals(this.field_11552, ((class_2501)object).field_11552);
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_11552);
	}

	@Override
	public class_2561 method_10710(String string, int i) {
		class_2561 lv = new class_2585("L").method_10854(field_11595);
		class_2561 lv2 = new class_2585("[").method_10852(lv).method_10864(";");

		for (int j = 0; j < this.field_11552.length; j++) {
			class_2561 lv3 = new class_2585(String.valueOf(this.field_11552[j])).method_10854(field_11593);
			lv2.method_10864(" ").method_10852(lv3).method_10852(lv);
			if (j != this.field_11552.length - 1) {
				lv2.method_10864(",");
			}
		}

		lv2.method_10864("]");
		return lv2;
	}

	public long[] method_10615() {
		return this.field_11552;
	}

	public int size() {
		return this.field_11552.length;
	}

	public class_2503 method_10616(int i) {
		return new class_2503(this.field_11552[i]);
	}

	public class_2503 method_17810(int i, class_2503 arg) {
		long l = this.field_11552[i];
		this.field_11552[i] = arg.method_10699();
		return new class_2503(l);
	}

	public void method_17812(int i, class_2503 arg) {
		this.field_11552 = ArrayUtils.add(this.field_11552, i, arg.method_10699());
	}

	@Override
	public boolean method_10535(int i, class_2520 arg) {
		if (arg instanceof class_2514) {
			this.field_11552[i] = ((class_2514)arg).method_10699();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_10533(int i, class_2520 arg) {
		if (arg instanceof class_2514) {
			this.field_11552 = ArrayUtils.add(this.field_11552, i, ((class_2514)arg).method_10699());
			return true;
		} else {
			return false;
		}
	}

	public class_2503 method_17811(int i) {
		long l = this.field_11552[i];
		this.field_11552 = ArrayUtils.remove(this.field_11552, i);
		return new class_2503(l);
	}

	public void clear() {
		this.field_11552 = new long[0];
	}
}
