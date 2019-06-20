package net.minecraft;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;

public class class_3513<K> implements class_2359<K> {
	private static final Object field_15650 = null;
	private K[] field_15651;
	private int[] field_15646;
	private K[] field_15647;
	private int field_15649;
	private int field_15648;

	public class_3513(int i) {
		i = (int)((float)i / 0.8F);
		this.field_15651 = (K[])(new Object[i]);
		this.field_15646 = new int[i];
		this.field_15647 = (K[])(new Object[i]);
	}

	public int method_15231(@Nullable K object) {
		return this.method_15222(this.method_15232(object, this.method_15228(object)));
	}

	@Nullable
	@Override
	public K method_10200(int i) {
		return i >= 0 && i < this.field_15647.length ? this.field_15647[i] : null;
	}

	private int method_15222(int i) {
		return i == -1 ? -1 : this.field_15646[i];
	}

	public int method_15225(K object) {
		int i = this.method_15226();
		this.method_15230(object, i);
		return i;
	}

	private int method_15226() {
		while (this.field_15649 < this.field_15647.length && this.field_15647[this.field_15649] != null) {
			this.field_15649++;
		}

		return this.field_15649;
	}

	private void method_15224(int i) {
		K[] objects = this.field_15651;
		int[] is = this.field_15646;
		this.field_15651 = (K[])(new Object[i]);
		this.field_15646 = new int[i];
		this.field_15647 = (K[])(new Object[i]);
		this.field_15649 = 0;
		this.field_15648 = 0;

		for (int j = 0; j < objects.length; j++) {
			if (objects[j] != null) {
				this.method_15230(objects[j], is[j]);
			}
		}
	}

	public void method_15230(K object, int i) {
		int j = Math.max(i, this.field_15648 + 1);
		if ((float)j >= (float)this.field_15651.length * 0.8F) {
			int k = this.field_15651.length << 1;

			while (k < i) {
				k <<= 1;
			}

			this.method_15224(k);
		}

		int k = this.method_15223(this.method_15228(object));
		this.field_15651[k] = object;
		this.field_15646[k] = i;
		this.field_15647[i] = object;
		this.field_15648++;
		if (i == this.field_15649) {
			this.field_15649++;
		}
	}

	private int method_15228(@Nullable K object) {
		return (class_3532.method_15354(System.identityHashCode(object)) & 2147483647) % this.field_15651.length;
	}

	private int method_15232(@Nullable K object, int i) {
		for (int j = i; j < this.field_15651.length; j++) {
			if (this.field_15651[j] == object) {
				return j;
			}

			if (this.field_15651[j] == field_15650) {
				return -1;
			}
		}

		for (int j = 0; j < i; j++) {
			if (this.field_15651[j] == object) {
				return j;
			}

			if (this.field_15651[j] == field_15650) {
				return -1;
			}
		}

		return -1;
	}

	private int method_15223(int i) {
		for (int j = i; j < this.field_15651.length; j++) {
			if (this.field_15651[j] == field_15650) {
				return j;
			}
		}

		for (int jx = 0; jx < i; jx++) {
			if (this.field_15651[jx] == field_15650) {
				return jx;
			}
		}

		throw new RuntimeException("Overflowed :(");
	}

	public Iterator<K> iterator() {
		return Iterators.filter(Iterators.forArray(this.field_15647), Predicates.notNull());
	}

	public void method_15229() {
		Arrays.fill(this.field_15651, null);
		Arrays.fill(this.field_15647, null);
		this.field_15649 = 0;
		this.field_15648 = 0;
	}

	public int method_15227() {
		return this.field_15648;
	}
}
