package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

@Environment(EnvType.CLIENT)
public class class_4700 {
	private final ThreadLocal<class_4700.class_4701> field_21519 = ThreadLocal.withInitial(() -> new class_4700.class_4701());
	private final Long2ObjectLinkedOpenHashMap<int[]> field_21520 = new Long2ObjectLinkedOpenHashMap<>(256, 0.25F);
	private final ReentrantReadWriteLock field_21521 = new ReentrantReadWriteLock();

	public int method_23770(BlockPos blockPos, IntSupplier intSupplier) {
		int i = blockPos.getX() >> 4;
		int j = blockPos.getZ() >> 4;
		class_4700.class_4701 lv = (class_4700.class_4701)this.field_21519.get();
		if (lv.field_21522 != i || lv.field_21523 != j) {
			lv.field_21522 = i;
			lv.field_21523 = j;
			lv.field_21524 = this.method_23772(i, j);
		}

		int k = blockPos.getX() & 15;
		int l = blockPos.getZ() & 15;
		int m = l << 4 | k;
		int n = lv.field_21524[m];
		if (n != -1) {
			return n;
		} else {
			int o = intSupplier.getAsInt();
			lv.field_21524[m] = o;
			return o;
		}
	}

	public void method_23769(int i, int j) {
		try {
			this.field_21521.writeLock().lock();

			for (int k = -1; k <= 1; k++) {
				for (int l = -1; l <= 1; l++) {
					long m = ChunkPos.toLong(i + k, j + l);
					this.field_21520.remove(m);
				}
			}
		} finally {
			this.field_21521.writeLock().unlock();
		}
	}

	public void method_23768() {
		try {
			this.field_21521.writeLock().lock();
			this.field_21520.clear();
		} finally {
			this.field_21521.writeLock().unlock();
		}
	}

	private int[] method_23772(int i, int j) {
		long l = ChunkPos.toLong(i, j);
		this.field_21521.readLock().lock();

		int[] is;
		try {
			is = this.field_21520.get(l);
		} finally {
			this.field_21521.readLock().unlock();
		}

		if (is != null) {
			return is;
		} else {
			int[] js = new int[256];
			Arrays.fill(js, -1);

			try {
				this.field_21521.writeLock().lock();
				if (this.field_21520.size() >= 256) {
					this.field_21520.removeFirst();
				}

				this.field_21520.put(l, js);
			} finally {
				this.field_21521.writeLock().unlock();
			}

			return js;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4701 {
		public int field_21522;
		public int field_21523;
		public int[] field_21524;

		private class_4701() {
		}
	}
}
