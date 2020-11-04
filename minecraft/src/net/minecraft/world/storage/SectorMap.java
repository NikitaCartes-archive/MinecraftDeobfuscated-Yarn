package net.minecraft.world.storage;

import java.util.BitSet;

public class SectorMap {
	private final BitSet field_20433 = new BitSet();

	public void allocate(int i, int j) {
		this.field_20433.set(i, i + j);
	}

	public void free(int i, int j) {
		this.field_20433.clear(i, i + j);
	}

	public int allocate(int i) {
		int j = 0;

		while (true) {
			int k = this.field_20433.nextClearBit(j);
			int l = this.field_20433.nextSetBit(k);
			if (l == -1 || l - k >= i) {
				this.allocate(k, i);
				return k;
			}

			j = l;
		}
	}
}
