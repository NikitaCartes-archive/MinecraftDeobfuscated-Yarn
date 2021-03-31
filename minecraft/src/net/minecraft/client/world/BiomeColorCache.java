package net.minecraft.client.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

@Environment(EnvType.CLIENT)
public class BiomeColorCache {
	private static final int field_32164 = 256;
	private final ThreadLocal<BiomeColorCache.Last> last = ThreadLocal.withInitial(() -> new BiomeColorCache.Last());
	private final Long2ObjectLinkedOpenHashMap<int[]> colors = new Long2ObjectLinkedOpenHashMap<>(256, 0.25F);
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public int getBiomeColor(BlockPos pos, IntSupplier colorFactory) {
		int i = ChunkSectionPos.getSectionCoord(pos.getX());
		int j = ChunkSectionPos.getSectionCoord(pos.getZ());
		BiomeColorCache.Last last = (BiomeColorCache.Last)this.last.get();
		if (last.x != i || last.z != j) {
			last.x = i;
			last.z = j;
			last.colors = this.getColorArray(i, j);
		}

		int k = pos.getX() & 15;
		int l = pos.getZ() & 15;
		int m = l << 4 | k;
		int n = last.colors[m];
		if (n != -1) {
			return n;
		} else {
			int o = colorFactory.getAsInt();
			last.colors[m] = o;
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

	private int[] getColorArray(int chunkX, int chunkZ) {
		long l = ChunkPos.toLong(chunkX, chunkZ);
		this.lock.readLock().lock();

		int[] is;
		try {
			is = this.colors.get(l);
		} finally {
			this.lock.readLock().unlock();
		}

		if (is != null) {
			return is;
		} else {
			int[] js = new int[256];
			Arrays.fill(js, -1);

			try {
				this.lock.writeLock().lock();
				if (this.colors.size() >= 256) {
					this.colors.removeFirst();
				}

				this.colors.put(l, js);
			} finally {
				this.lock.writeLock().unlock();
			}

			return js;
		}
	}

	@Environment(EnvType.CLIENT)
	static class Last {
		public int x = Integer.MIN_VALUE;
		public int z = Integer.MIN_VALUE;
		public int[] colors;

		private Last() {
		}
	}
}
