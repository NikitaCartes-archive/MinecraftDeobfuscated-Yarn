package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;

public final class class_3626 implements class_3625 {
	private final class_4 field_16035;
	private final Long2IntLinkedOpenHashMap field_16038;
	private final int field_16036;

	public class_3626(Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap, int i, class_4 arg) {
		this.field_16038 = long2IntLinkedOpenHashMap;
		this.field_16036 = i;
		this.field_16035 = arg;
	}

	@Override
	public int method_15825(int i, int j) {
		long l = class_1923.method_8331(i, j);
		synchronized (this.field_16038) {
			int k = this.field_16038.get(l);
			if (k != Integer.MIN_VALUE) {
				return k;
			} else {
				int m = this.field_16035.apply(i, j);
				this.field_16038.put(l, m);
				if (this.field_16038.size() > this.field_16036) {
					for (int n = 0; n < this.field_16036 / 16; n++) {
						this.field_16038.removeFirstInt();
					}
				}

				return m;
			}
		}
	}

	public int method_15827() {
		return this.field_16036;
	}
}
