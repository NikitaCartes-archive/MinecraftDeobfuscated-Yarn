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
	private static final int MAX_ENTRY_SIZE = 256;
	private final ThreadLocal<BiomeColorCache.Last> last = ThreadLocal.withInitial(BiomeColorCache.Last::new);
	private final Long2ObjectLinkedOpenHashMap<BiomeColorCache.Colors> colors = new Long2ObjectLinkedOpenHashMap<>(256, 0.25F);
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ToIntFunction<BlockPos> colorFactory;

	public BiomeColorCache(ToIntFunction<BlockPos> colorFactory) {
		this.colorFactory = colorFactory;
	}

	public int getBiomeColor(BlockPos pos) {
		int i = ChunkSectionPos.getSectionCoord(pos.getX());
		int j = ChunkSectionPos.getSectionCoord(pos.getZ());
		BiomeColorCache.Last last = (BiomeColorCache.Last)this.last.get();
		if (last.x != i || last.z != j || last.colors == null || last.colors.needsCacheRefresh()) {
			last.x = i;
			last.z = j;
			last.colors = this.getColorArray(i, j);
		}

		int[] is = last.colors.get(pos.getY());
		int k = pos.getX() & 15;
		int l = pos.getZ() & 15;
		int m = l << 4 | k;
		int n = is[m];
		if (n != -1) {
			return n;
		} else {
			int o = this.colorFactory.applyAsInt(pos);
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
					BiomeColorCache.Colors colors = this.colors.remove(l);
					if (colors != null) {
						colors.setNeedsCacheRefresh();
					}
				}
			}
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	public void reset() {
		try {
			this.lock.writeLock().lock();
			this.colors.values().forEach(BiomeColorCache.Colors::setNeedsCacheRefresh);
			this.colors.clear();
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	private BiomeColorCache.Colors getColorArray(int chunkX, int chunkZ) {
		long l = ChunkPos.toLong(chunkX, chunkZ);
		this.lock.readLock().lock();

		try {
			BiomeColorCache.Colors colors = this.colors.get(l);
			if (colors != null) {
				return colors;
			}
		} finally {
			this.lock.readLock().unlock();
		}

		this.lock.writeLock().lock();

		BiomeColorCache.Colors colors2;
		try {
			BiomeColorCache.Colors colors = this.colors.get(l);
			if (colors == null) {
				colors2 = new BiomeColorCache.Colors();
				if (this.colors.size() >= 256) {
					this.colors.removeFirst();
				}

				this.colors.put(l, colors2);
				return colors2;
			}

			colors2 = colors;
		} finally {
			this.lock.writeLock().unlock();
		}

		return colors2;
	}

	@Environment(EnvType.CLIENT)
	static class Colors {
		private final Int2ObjectArrayMap<int[]> colors = new Int2ObjectArrayMap<>(16);
		private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		private static final int XZ_COLORS_SIZE = MathHelper.square(16);
		private volatile boolean needsCacheRefresh;

		public int[] get(int y) {
			this.lock.readLock().lock();

			try {
				int[] is = this.colors.get(y);
				if (is != null) {
					return is;
				}
			} finally {
				this.lock.readLock().unlock();
			}

			this.lock.writeLock().lock();

			int[] var12;
			try {
				var12 = this.colors.computeIfAbsent(y, yx -> this.createDefault());
			} finally {
				this.lock.writeLock().unlock();
			}

			return var12;
		}

		private int[] createDefault() {
			int[] is = new int[XZ_COLORS_SIZE];
			Arrays.fill(is, -1);
			return is;
		}

		public boolean needsCacheRefresh() {
			return this.needsCacheRefresh;
		}

		public void setNeedsCacheRefresh() {
			this.needsCacheRefresh = true;
		}
	}

	@Environment(EnvType.CLIENT)
	static class Last {
		public int x = Integer.MIN_VALUE;
		public int z = Integer.MIN_VALUE;
		@Nullable
		BiomeColorCache.Colors colors;

		private Last() {
		}
	}
}
