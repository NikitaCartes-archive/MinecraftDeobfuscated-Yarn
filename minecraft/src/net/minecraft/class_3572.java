package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkView;
import net.minecraft.world.chunk.light.LightStorage;

public final class class_3572 extends LightStorage<class_3569.class_3570, class_3569> {
	private static final Direction[] field_15826 = Direction.values();
	private static final Direction[] field_15825 = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

	public class_3572(ChunkView chunkView) {
		super(chunkView, LightType.field_9284, new class_3569(chunkView));
	}

	@Override
	protected int method_15488(long l, long m, int i) {
		if (m == -1L) {
			return 15;
		} else {
			if (l == -1L) {
				if (!this.field_15793.method_15565(m)) {
					return 15;
				}

				i = 0;
			}

			if (i >= 15) {
				return i;
			} else {
				int j = this.method_16340(l, m);
				boolean bl = l == -1L
					|| BlockPos.unpackLongX(l) == BlockPos.unpackLongX(m)
						&& BlockPos.unpackLongZ(l) == BlockPos.unpackLongZ(m)
						&& BlockPos.unpackLongY(l) > BlockPos.unpackLongY(m);
				return bl && i == 0 && j == 0 ? 0 : i + Math.max(1, j);
			}
		}
	}

	@Override
	protected void method_15487(long l, int i, boolean bl) {
		long m = BlockPos.method_10090(l);
		int j = BlockPos.unpackLongY(l);
		int k = j & 15;
		int n = j & -16;
		int o;
		if (k != 0) {
			o = 0;
		} else {
			int p = 0;

			while (!this.field_15793.method_15524(BlockPos.add(m, 0, -p - 16, 0)) && this.field_15793.method_15567(n - p - 16)) {
				p += 16;
			}

			o = p;
		}

		long q = BlockPos.add(l, 0, -1 - o, 0);
		long r = BlockPos.method_10090(q);
		if (m == r || this.field_15793.method_15524(r)) {
			this.method_15484(l, q, i, bl);
		}

		long s = BlockPos.method_10060(l, Direction.UP);
		long t = BlockPos.method_10090(s);
		if (m == t || this.field_15793.method_15524(t)) {
			this.method_15484(l, s, i, bl);
		}

		for (Direction direction : field_15825) {
			int u = 0;

			do {
				long v = BlockPos.add(l, direction.getOffsetX(), -u, direction.getOffsetZ());
				long w = BlockPos.method_10090(v);
				if (m == w) {
					this.method_15484(l, v, i, bl);
					break;
				}

				if (this.field_15793.method_15524(w)) {
					this.method_15484(l, v, i, bl);
				}
			} while (++u >= o);
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

		for (Direction direction : field_15826) {
			long o = BlockPos.method_10060(l, direction);
			long p = BlockPos.method_10090(o);
			class_2804 lv2;
			if (n == p) {
				lv2 = lv;
			} else {
				lv2 = this.field_15793.method_15522(p, true);
			}

			if (lv2 != null) {
				if (o != m) {
					int q = this.method_15488(o, l, this.method_15517(lv2, o));
					if (j > q) {
						j = q;
					}

					if (j == 0) {
						return j;
					}
				}
			} else if (direction != Direction.DOWN) {
				for (o = BlockPos.method_10091(o); !this.field_15793.method_15524(p) && !this.field_15793.method_15568(p); o = BlockPos.add(o, 0, 16, 0)) {
					p = BlockPos.add(p, 0, 16, 0);
				}

				class_2804 lv3 = this.field_15793.method_15522(p, true);
				if (o != m) {
					int r;
					if (lv3 != null) {
						r = this.method_15488(o, l, this.method_15517(lv3, o));
					} else {
						r = this.field_15793.method_15566(p) ? 15 : 0;
					}

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
	protected void queueLightCheck(long l) {
		this.field_15793.method_15539();
		long m = BlockPos.method_10090(l);
		if (this.field_15793.method_15524(m)) {
			super.queueLightCheck(l);
		} else {
			for (l = BlockPos.method_10091(l); !this.field_15793.method_15524(m) && !this.field_15793.method_15568(m); l = BlockPos.add(l, 0, 16, 0)) {
				m = BlockPos.add(m, 0, 16, 0);
			}

			if (this.field_15793.method_15524(m)) {
				super.queueLightCheck(l);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_15520(long l) {
		return super.method_15520(l) + (this.field_15793.method_15568(l) ? "*" : "");
	}
}
