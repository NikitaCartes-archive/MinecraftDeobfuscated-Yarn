package net.minecraft;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkView;
import net.minecraft.world.chunk.light.LightStorage;

public final class class_3552 extends LightStorage<class_3547.class_3548, class_3547> {
	private static final Direction[] field_15778 = Direction.values();
	private final BlockPos.Mutable field_16511 = new BlockPos.Mutable();

	public class_3552(ChunkView chunkView) {
		super(chunkView, LightType.field_9282, new class_3547(chunkView));
	}

	private int method_15474(long l) {
		int i = BlockPos.unpackLongX(l);
		int j = BlockPos.unpackLongY(l);
		int k = BlockPos.unpackLongZ(l);
		BlockView blockView = this.view.get(i >> 4, k >> 4);
		return blockView != null ? blockView.getLuminance(this.field_16511.set(i, j, k)) : 0;
	}

	@Override
	protected int method_15488(long l, long m, int i) {
		if (m == -1L) {
			return 15;
		} else if (l == -1L) {
			return i + 15 - this.method_15474(m);
		} else {
			return i >= 15 ? i : i + Math.max(1, this.method_16340(l, m));
		}
	}

	@Override
	protected void method_15487(long l, int i, boolean bl) {
		long m = BlockPos.method_10090(l);

		for (Direction direction : field_15778) {
			long n = BlockPos.method_10060(l, direction);
			long o = BlockPos.method_10090(n);
			if (m == o || this.field_15793.method_15524(o)) {
				this.method_15484(l, n, i, bl);
			}
		}
	}

	@Override
	protected int method_15486(long l, long m, int i) {
		int j = i;
		if (-1L != m) {
			int k = this.method_15488(-1L, l, 0);
			if (i > k) {
				j = k;
			}

			if (j == 0) {
				return j;
			}
		}

		long n = BlockPos.method_10090(l);
		class_2804 lv = this.field_15793.method_15522(n, true);

		for (Direction direction : field_15778) {
			long o = BlockPos.method_10060(l, direction);
			if (o != m) {
				long p = BlockPos.method_10090(o);
				class_2804 lv2;
				if (n == p) {
					lv2 = lv;
				} else {
					lv2 = this.field_15793.method_15522(p, true);
				}

				if (lv2 != null) {
					int q = this.method_15488(o, l, this.method_15517(lv2, o));
					if (j > q) {
						j = q;
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
	public void method_15514(BlockPos blockPos, int i) {
		this.field_15793.method_15539();
		this.method_15478(-1L, blockPos.asLong(), 15 - i, true);
	}
}
