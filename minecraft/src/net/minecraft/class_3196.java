package net.minecraft;

import net.minecraft.world.chunk.ChunkPos;

public abstract class class_3196 extends class_3554 {
	protected static final long field_13881 = ChunkPos.toLong(1875016, 1875016);

	protected class_3196(int i, int j, int k) {
		super(i, j, k);
	}

	@Override
	protected boolean method_15494(long l) {
		return l == field_13881;
	}

	@Override
	protected void method_15487(long l, int i, boolean bl) {
		ChunkPos chunkPos = new ChunkPos(l);
		int j = chunkPos.x;
		int k = chunkPos.z;

		for (int m = -1; m <= 1; m++) {
			for (int n = -1; n <= 1; n++) {
				long o = ChunkPos.toLong(j + m, k + n);
				if (o != l) {
					this.method_15484(l, o, i, bl);
				}
			}
		}
	}

	@Override
	protected int method_15486(long l, long m, int i) {
		int j = i;
		ChunkPos chunkPos = new ChunkPos(l);
		int k = chunkPos.x;
		int n = chunkPos.z;

		for (int o = -1; o <= 1; o++) {
			for (int p = -1; p <= 1; p++) {
				long q = ChunkPos.toLong(k + o, n + p);
				if (q == l) {
					q = field_13881;
				}

				if (q != m) {
					int r = this.method_15488(q, l, this.method_15480(q));
					if (j > r) {
						j = r;
					}

					if (j == 0) {
						return j;
					}
				}
			}
		}

		return j;
	}

	@Override
	protected int method_15488(long l, long m, int i) {
		return l == field_13881 ? this.method_14028(m) : i + 1;
	}

	protected abstract int method_14028(long l);

	public void method_14027(long l, int i, boolean bl) {
		this.method_15478(field_13881, l, i, bl);
	}
}
