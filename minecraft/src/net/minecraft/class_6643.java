package net.minecraft;

import java.util.BitSet;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class class_6643 {
	private final int field_35049;
	private final BitSet field_35050;

	public class_6643(int i, int j) {
		this.field_35049 = j;
		this.field_35050 = new BitSet(256 * i);
	}

	public class_6643(long[] ls, int i) {
		this.field_35049 = i;
		this.field_35050 = BitSet.valueOf(ls);
	}

	private int method_38869(int i, int j, int k) {
		return i & 15 | (k & 15) << 4 | j - this.field_35049 << 8;
	}

	public void method_38865(int i, int j, int k) {
		this.field_35050.set(this.method_38869(i, j, k));
	}

	public boolean method_38868(int i, int j, int k) {
		return this.field_35050.get(this.method_38869(i, j, k));
	}

	public Stream<BlockPos> method_38866(ChunkPos chunkPos) {
		return this.field_35050.stream().mapToObj(i -> {
			int j = i & 15;
			int k = i >> 4 & 15;
			int l = i >> 8;
			return chunkPos.getBlockPos(j, l + this.field_35049, k);
		});
	}

	public long[] method_38864() {
		return this.field_35050.toLongArray();
	}
}
