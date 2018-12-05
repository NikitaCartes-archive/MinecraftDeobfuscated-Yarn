package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkView;
import net.minecraft.world.chunk.light.LightStorage;

public abstract class class_3560<M extends class_3556<M>> extends class_3554 {
	protected static final class_2804 field_15801 = new class_2804();
	private static final Direction[] field_15799 = Direction.values();
	private final LightType field_15805;
	private final ChunkView field_15803;
	protected final LongSet field_15808 = new LongOpenHashSet();
	protected final LongSet field_15797 = new LongOpenHashSet();
	protected final LongSet field_15804 = new LongOpenHashSet();
	protected volatile M field_15806;
	protected final M field_15796;
	protected LongSet field_15802 = new LongOpenHashSet();
	protected LongSet field_16448 = new LongOpenHashSet();
	protected final Long2ObjectMap<class_2804> field_15807 = new Long2ObjectOpenHashMap<>();
	private final LongSet field_15798 = new LongOpenHashSet();
	protected volatile boolean field_15800;

	protected class_3560(LightType lightType, ChunkView chunkView, M arg) {
		super(3, 16, 256);
		this.field_15805 = lightType;
		this.field_15803 = chunkView;
		this.field_15796 = arg;
		this.field_15806 = arg.method_15504();
		this.field_15806.method_16188();
	}

	public boolean method_15524(long l) {
		return this.method_15522(l, true) != null;
	}

	@Nullable
	public class_2804 method_15522(long l, boolean bl) {
		return this.method_15533(bl ? this.field_15796 : this.field_15806, l);
	}

	@Nullable
	protected class_2804 method_15533(M arg, long l) {
		return arg.method_15501(l);
	}

	public abstract int method_15538(long l);

	public int method_15537(long l) {
		long m = BlockPos.method_10090(l);
		class_2804 lv = this.method_15522(m, true);
		return lv.method_12139(BlockPos.unpackLongX(l) & 15, BlockPos.unpackLongY(l) & 15, BlockPos.unpackLongZ(l) & 15);
	}

	public void method_15525(long l, int i) {
		long m = BlockPos.method_10090(l);
		if (this.field_15802.add(m)) {
			this.field_15796.method_15502(m);
		}

		class_2804 lv = this.method_15522(m, true);
		lv.method_12145(BlockPos.unpackLongX(l) & 15, BlockPos.unpackLongY(l) & 15, BlockPos.unpackLongZ(l) & 15, i);

		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				for (int n = -1; n <= 1; n++) {
					this.field_16448.add(BlockPos.method_10090(BlockPos.add(l, k, n, j)));
				}
			}
		}
	}

	@Override
	protected boolean method_15494(long l) {
		return l == -1L;
	}

	@Override
	protected void method_15487(long l, int i, boolean bl) {
		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				for (int m = -1; m <= 1; m++) {
					long n = BlockPos.add(l, j * 16, k * 16, m * 16);
					if (n != l) {
						this.method_15484(l, n, i, bl);
					}
				}
			}
		}
	}

	@Override
	protected int method_15486(long l, long m, int i) {
		int j = i;

		for (int k = -1; k <= 1; k++) {
			for (int n = -1; n <= 1; n++) {
				for (int o = -1; o <= 1; o++) {
					long p = BlockPos.add(l, k * 16, n * 16, o * 16);
					if (p == l) {
						p = -1L;
					}

					if (p != m) {
						int q = this.method_15488(p, l, this.method_15480(p));
						if (j > q) {
							j = q;
						}

						if (j == 0) {
							return j;
						}
					}
				}
			}
		}

		return j;
	}

	@Override
	public int method_15480(long l) {
		if (l == -1L) {
			return 2;
		} else if (this.field_15808.contains(l)) {
			return 0;
		} else {
			return !this.field_15798.contains(l) && this.field_15796.method_15503(l) ? 1 : 2;
		}
	}

	@Override
	protected int method_15488(long l, long m, int i) {
		if (l == -1L) {
			if (this.field_15797.contains(m)) {
				return 2;
			} else {
				return !this.field_15808.contains(m) && !this.field_15804.contains(m) ? 2 : 0;
			}
		} else {
			return i + 1;
		}
	}

	@Override
	protected void method_15485(long l, int i) {
		int j = this.method_15480(l);
		if (j != 0 && i == 0) {
			this.field_15808.add(l);
			this.field_15804.remove(l);
		}

		if (j == 0 && i != 0) {
			this.field_15808.remove(l);
			this.field_15797.remove(l);
		}

		if (j >= 2 && i != 2) {
			if (this.field_15798.contains(l)) {
				this.field_15798.remove(l);
			} else {
				this.field_15796.method_15499(l, this.method_15529(l));
				this.field_15802.add(l);
				this.method_15523(l);

				for (int k = -1; k <= 1; k++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 1; n++) {
							this.field_16448.add(BlockPos.method_10090(BlockPos.add(l, m * 16, n * 16, k * 16)));
						}
					}
				}
			}
		}

		if (j != 2 && i >= 2) {
			this.field_15798.add(l);
		}

		this.field_15800 = !this.field_15798.isEmpty();
	}

	private class_2804 method_15529(long l) {
		class_2804 lv = this.field_15807.get(l);
		return lv != null ? lv : new class_2804();
	}

	protected void method_15536(LightStorage<?, ?> lightStorage, long l) {
		int i = BlockPos.unpackLongX(l);
		int j = BlockPos.unpackLongY(l);
		int k = BlockPos.unpackLongZ(l);

		for (int m = 0; m < 16; m++) {
			for (int n = 0; n < 16; n++) {
				for (int o = 0; o < 16; o++) {
					long p = BlockPos.asLong(i + m, j + n, k + o);
					lightStorage.method_15483(p);
				}
			}
		}
	}

	public boolean method_15528() {
		return this.field_15800;
	}

	public void method_15527(LightStorage<M, ?> lightStorage, boolean bl, boolean bl2) {
		if (this.method_15528() || !this.field_15807.isEmpty()) {
			LongIterator objectIterator = this.field_15798.iterator();

			while (objectIterator.hasNext()) {
				long l = (Long)objectIterator.next();
				this.field_15807.remove(l);
				this.method_15536(lightStorage, l);
				this.field_15796.method_15500(l);
			}

			this.field_15796.method_15505();
			objectIterator = this.field_15798.iterator();

			while (objectIterator.hasNext()) {
				long l = (Long)objectIterator.next();
				this.method_15534(l);
			}

			this.field_15798.clear();
			this.field_15800 = false;

			for (Entry<class_2804> entry : this.field_15807.long2ObjectEntrySet()) {
				long m = entry.getLongKey();
				if (this.method_15524(m)) {
					class_2804 lv = (class_2804)entry.getValue();
					if (this.field_15796.method_15501(m) != lv) {
						this.method_15536(lightStorage, m);
						this.field_15796.method_15499(m, lv);
						this.field_15802.add(m);
					}
				}
			}

			this.field_15796.method_15505();
			if (!bl2) {
				objectIterator = this.field_15807.keySet().iterator();

				while (objectIterator.hasNext()) {
					long l = (Long)objectIterator.next();
					if (this.method_15524(l)) {
						int i = BlockPos.unpackLongX(l);
						int j = BlockPos.unpackLongY(l);
						int k = BlockPos.unpackLongZ(l);

						for (Direction direction : field_15799) {
							long n = BlockPos.add(l, direction.getOffsetX() * 16, direction.getOffsetY() * 16, direction.getOffsetZ() * 16);
							if (!this.field_15807.containsKey(n) && this.method_15524(n)) {
								for (int o = 0; o < 16; o++) {
									for (int p = 0; p < 16; p++) {
										long q;
										long r;
										switch (direction) {
											case DOWN:
												q = BlockPos.asLong(i + p, j, k + o);
												r = BlockPos.asLong(i + p, j - 1, k + o);
												break;
											case UP:
												q = BlockPos.asLong(i + p, j + 16 - 1, k + o);
												r = BlockPos.asLong(i + p, j + 16, k + o);
												break;
											case NORTH:
												q = BlockPos.asLong(i + o, j + p, k);
												r = BlockPos.asLong(i + o, j + p, k - 1);
												break;
											case SOUTH:
												q = BlockPos.asLong(i + o, j + p, k + 16 - 1);
												r = BlockPos.asLong(i + o, j + p, k + 16);
												break;
											case WEST:
												q = BlockPos.asLong(i, j + o, k + p);
												r = BlockPos.asLong(i - 1, j + o, k + p);
												break;
											default:
												q = BlockPos.asLong(i + 16 - 1, j + o, k + p);
												r = BlockPos.asLong(i + 16, j + o, k + p);
										}

										lightStorage.method_15478(q, r, lightStorage.method_15488(q, r, lightStorage.method_15480(q)), false);
										lightStorage.method_15478(r, q, lightStorage.method_15488(r, q, lightStorage.method_15480(r)), false);
									}
								}
							}
						}
					}
				}
			}

			ObjectIterator<Entry<class_2804>> objectIteratorx = this.field_15807.long2ObjectEntrySet().iterator();

			while (objectIteratorx.hasNext()) {
				Entry<class_2804> entryx = (Entry<class_2804>)objectIteratorx.next();
				long m = entryx.getLongKey();
				if (this.method_15524(m)) {
					objectIteratorx.remove();
				}
			}
		}
	}

	protected void method_15523(long l) {
	}

	protected void method_15534(long l) {
	}

	public void method_15535(long l, boolean bl) {
	}

	public void method_15532(long l, class_2804 arg) {
		this.field_15807.put(l, arg);
	}

	public void method_15526(long l, boolean bl) {
		boolean bl2 = this.field_15808.contains(l);
		if (!bl2 && !bl) {
			this.field_15804.add(l);
			this.method_15478(-1L, l, 0, true);
		}

		if (bl2 && bl) {
			this.field_15797.add(l);
			this.method_15478(-1L, l, 2, false);
		}
	}

	public void method_15539() {
		if (this.method_15489()) {
			this.method_15492(Integer.MAX_VALUE);
		}
	}

	public void method_15530() {
		if (!this.field_15802.isEmpty()) {
			M lv = this.field_15796.method_15504();
			lv.method_16188();
			this.field_15806 = lv;
			this.field_15802.clear();
		}

		if (!this.field_16448.isEmpty()) {
			LongIterator longIterator = this.field_16448.iterator();

			while (longIterator.hasNext()) {
				long l = longIterator.nextLong();
				int i = BlockPos.unpackLongX(l);
				int j = BlockPos.unpackLongY(l);
				int k = BlockPos.unpackLongZ(l);
				this.field_15803.method_12247(this.field_15805, i >> 4, j >> 4, k >> 4);
			}

			this.field_16448.clear();
		}
	}
}
