package net.minecraft.server.network;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Consumer;
import net.minecraft.util.math.ChunkPos;

public interface ChunkFilter {
	ChunkFilter IGNORE_ALL = new ChunkFilter() {
		@Override
		public boolean isWithinDistance(int x, int z, boolean includeEdge) {
			return false;
		}

		@Override
		public void forEach(Consumer<ChunkPos> consumer) {
		}
	};

	static ChunkFilter cylindrical(ChunkPos center, int viewDistance) {
		return new ChunkFilter.Cylindrical(center, viewDistance);
	}

	static void forEachChangedChunk(ChunkFilter oldFilter, ChunkFilter newFilter, Consumer<ChunkPos> newlyIncluded, Consumer<ChunkPos> justRemoved) {
		if (!oldFilter.equals(newFilter)) {
			if (oldFilter instanceof ChunkFilter.Cylindrical cylindrical
				&& newFilter instanceof ChunkFilter.Cylindrical cylindrical2
				&& cylindrical.overlaps(cylindrical2)) {
				int i = Math.min(cylindrical.getLeft(), cylindrical2.getLeft());
				int j = Math.min(cylindrical.getBottom(), cylindrical2.getBottom());
				int k = Math.max(cylindrical.getRight(), cylindrical2.getRight());
				int l = Math.max(cylindrical.getTop(), cylindrical2.getTop());

				for (int m = i; m <= k; m++) {
					for (int n = j; n <= l; n++) {
						boolean bl = cylindrical.isWithinDistance(m, n);
						boolean bl2 = cylindrical2.isWithinDistance(m, n);
						if (bl != bl2) {
							if (bl2) {
								newlyIncluded.accept(new ChunkPos(m, n));
							} else {
								justRemoved.accept(new ChunkPos(m, n));
							}
						}
					}
				}

				return;
			}

			oldFilter.forEach(justRemoved);
			newFilter.forEach(newlyIncluded);
		}
	}

	default boolean isWithinDistance(ChunkPos pos) {
		return this.isWithinDistance(pos.x, pos.z);
	}

	default boolean isWithinDistance(int x, int z) {
		return this.isWithinDistance(x, z, true);
	}

	boolean isWithinDistance(int x, int z, boolean includeEdge);

	void forEach(Consumer<ChunkPos> consumer);

	default boolean isWithinDistanceExcludingEdge(int x, int z) {
		return this.isWithinDistance(x, z, false);
	}

	static boolean isWithinDistanceExcludingEdge(int centerX, int centerZ, int viewDistance, int x, int z) {
		return isWithinDistance(centerX, centerZ, viewDistance, x, z, false);
	}

	static boolean isWithinDistance(int centerX, int centerZ, int viewDistance, int x, int z, boolean includeEdge) {
		int i = Math.max(0, Math.abs(x - centerX) - 1);
		int j = Math.max(0, Math.abs(z - centerZ) - 1);
		long l = (long)Math.max(0, Math.max(i, j) - (includeEdge ? 1 : 0));
		long m = (long)Math.min(i, j);
		long n = m * m + l * l;
		int k = viewDistance * viewDistance;
		return n < (long)k;
	}

	public static record Cylindrical(ChunkPos center, int viewDistance) implements ChunkFilter {
		int getLeft() {
			return this.center.x - this.viewDistance - 1;
		}

		int getBottom() {
			return this.center.z - this.viewDistance - 1;
		}

		int getRight() {
			return this.center.x + this.viewDistance + 1;
		}

		int getTop() {
			return this.center.z + this.viewDistance + 1;
		}

		@VisibleForTesting
		protected boolean overlaps(ChunkFilter.Cylindrical o) {
			return this.getLeft() <= o.getRight() && this.getRight() >= o.getLeft() && this.getBottom() <= o.getTop() && this.getTop() >= o.getBottom();
		}

		@Override
		public boolean isWithinDistance(int x, int z, boolean includeEdge) {
			return ChunkFilter.isWithinDistance(this.center.x, this.center.z, this.viewDistance, x, z, includeEdge);
		}

		@Override
		public void forEach(Consumer<ChunkPos> consumer) {
			for (int i = this.getLeft(); i <= this.getRight(); i++) {
				for (int j = this.getBottom(); j <= this.getTop(); j++) {
					if (this.isWithinDistance(i, j)) {
						consumer.accept(new ChunkPos(i, j));
					}
				}
			}
		}
	}
}
