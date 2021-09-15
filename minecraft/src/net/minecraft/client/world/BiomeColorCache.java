package net.minecraft.client.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BiomeColorCache {
	private static final int field_32164 = 256;
	private final ThreadLocal<BiomeColorCache.Last> last = ThreadLocal.withInitial(BiomeColorCache.Last::new);
	private final Long2ObjectLinkedOpenHashMap<BiomeColorCache.class_6598> colors = new Long2ObjectLinkedOpenHashMap<>(256, 0.25F);
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ToIntFunction<BlockPos> field_34795;

	public BiomeColorCache(ToIntFunction<BlockPos> toIntFunction) {
		this.field_34795 = toIntFunction;
	}

	public int getBiomeColor(BlockPos pos) {
		int i = ChunkSectionPos.getSectionCoord(pos.getX());
		int j = ChunkSectionPos.getSectionCoord(pos.getZ());
		BiomeColorCache.Last last = (BiomeColorCache.Last)this.last.get();
		if (last.x != i || last.z != j || last.colors == null) {
			last.x = i;
			last.z = j;
			last.colors = this.getColorArray(i, j);
		}

		int[] is = last.colors.method_38528(pos.getY());
		int k = pos.getX() & 15;
		int l = pos.getZ() & 15;
		int m = l << 4 | k;
		int n = is[m];
		if (n != -1) {
			return n;
		} else {
			int o = this.field_34795.applyAsInt(pos);
			is[m] = o;
			return o;
		}
	}

	public void reset(int chunkX, int chunkZ) {
		try {
			this.lock.writeLock().lock();

			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					long l = ChunkPos.toLong(chunkX + i, chunkZ + j);
					this.colors.remove(l);
				}
			}
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	public void reset() {
		try {
			this.lock.writeLock().lock();
			this.colors.clear();
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	private BiomeColorCache.class_6598 getColorArray(int chunkX, int chunkZ) {
		long l = ChunkPos.toLong(chunkX, chunkZ);
		this.lock.readLock().lock();

		try {
			BiomeColorCache.class_6598 lv = this.colors.get(l);
			if (lv != null) {
				return lv;
			}
		} finally {
			this.lock.readLock().unlock();
		}

		this.lock.writeLock().lock();

		BiomeColorCache.class_6598 lv2;
		try {
			BiomeColorCache.class_6598 lv = this.colors.get(l);
			if (lv == null) {
				lv2 = new BiomeColorCache.class_6598();
				if (this.colors.size() >= 256) {
					this.colors.removeFirst();
				}

				this.colors.put(l, lv2);
				return lv2;
			}

			lv2 = lv;
		} finally {
			this.lock.writeLock().unlock();
		}

		return lv2;
	}

	@Environment(EnvType.CLIENT)
	static class Last {
		public int x = Integer.MIN_VALUE;
		public int z = Integer.MIN_VALUE;
		@Nullable
		BiomeColorCache.class_6598 colors;

		private Last() {
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_6598 {
		private Int2ObjectArrayMap<int[]> field_34796 = new Int2ObjectArrayMap<>(16);
		private final ReentrantReadWriteLock field_34797 = new ReentrantReadWriteLock();
		private static final int field_34798 = MathHelper.square(16);

		public int[] method_38528(int i) {
			this.field_34797.readLock().lock();

			try {
				int[] is = this.field_34796.get(i);
				if (is != null) {
					return is;
				}
			} finally {
				this.field_34797.readLock().unlock();
			}

			this.field_34797.writeLock().lock();

			int[] var12;
			try {
				var12 = this.field_34796.computeIfAbsent(i, ix -> this.method_38527());
			} finally {
				this.field_34797.writeLock().unlock();
			}

			return var12;
		}

		private int[] method_38527() {
			int[] is = new int[field_34798];
			Arrays.fill(is, -1);
			return is;
		}
	}
}
