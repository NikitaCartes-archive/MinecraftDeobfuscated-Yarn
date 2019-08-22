package net.minecraft;

import java.util.BitSet;

public class class_4485 {
	private final BitSet field_20433 = new BitSet();

	public void method_21868(int i, int j) {
		this.field_20433.set(i, i + j);
	}

	public void method_21869(int i, int j) {
		this.field_20433.clear(i, i + j);
	}

	public int method_21867(int i) {
		int j = 0;

		while (true) {
			int k = this.field_20433.nextClearBit(j);
			int l = this.field_20433.nextSetBit(k);
			if (l == -1 || l - k >= i) {
				this.method_21868(k, i);
				return k;
			}

			j = l;
		}
	}
}
