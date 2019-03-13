package net.minecraft.block.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ViewableWorld;

public class BlockPattern {
	private final Predicate<CachedBlockPosition>[][][] pattern;
	private final int depth;
	private final int height;
	private final int width;

	public BlockPattern(Predicate<CachedBlockPosition>[][][] predicates) {
		this.pattern = predicates;
		this.depth = predicates.length;
		if (this.depth > 0) {
			this.height = predicates[0].length;
			if (this.height > 0) {
				this.width = predicates[0][0].length;
			} else {
				this.width = 0;
			}
		} else {
			this.height = 0;
			this.width = 0;
		}
	}

	public int getDepth() {
		return this.depth;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	@Nullable
	private BlockPattern.Result method_11711(
		BlockPos blockPos, Direction direction, Direction direction2, LoadingCache<BlockPos, CachedBlockPosition> loadingCache
	) {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					if (!this.pattern[k][j][i].test(loadingCache.getUnchecked(method_11707(blockPos, direction, direction2, i, j, k)))) {
						return null;
					}
				}
			}
		}

		return new BlockPattern.Result(blockPos, direction, direction2, loadingCache, this.width, this.height, this.depth);
	}

	@Nullable
	public BlockPattern.Result method_11708(ViewableWorld viewableWorld, BlockPos blockPos) {
		LoadingCache<BlockPos, CachedBlockPosition> loadingCache = makeCache(viewableWorld, false);
		int i = Math.max(Math.max(this.width, this.height), this.depth);

		for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(blockPos, blockPos.add(i - 1, i - 1, i - 1))) {
			for (Direction direction : Direction.values()) {
				for (Direction direction2 : Direction.values()) {
					if (direction2 != direction && direction2 != direction.getOpposite()) {
						BlockPattern.Result result = this.method_11711(blockPos2, direction, direction2, loadingCache);
						if (result != null) {
							return result;
						}
					}
				}
			}
		}

		return null;
	}

	public static LoadingCache<BlockPos, CachedBlockPosition> makeCache(ViewableWorld viewableWorld, boolean bl) {
		return CacheBuilder.newBuilder().build(new BlockPattern.BlockStateCacheLoader(viewableWorld, bl));
	}

	protected static BlockPos method_11707(BlockPos blockPos, Direction direction, Direction direction2, int i, int j, int k) {
		if (direction != direction2 && direction != direction2.getOpposite()) {
			Vec3i vec3i = new Vec3i(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
			Vec3i vec3i2 = new Vec3i(direction2.getOffsetX(), direction2.getOffsetY(), direction2.getOffsetZ());
			Vec3i vec3i3 = vec3i.crossProduct(vec3i2);
			return blockPos.add(
				vec3i2.getX() * -j + vec3i3.getX() * i + vec3i.getX() * k,
				vec3i2.getY() * -j + vec3i3.getY() * i + vec3i.getY() * k,
				vec3i2.getZ() * -j + vec3i3.getZ() * i + vec3i.getZ() * k
			);
		} else {
			throw new IllegalArgumentException("Invalid forwards & up combination");
		}
	}

	static class BlockStateCacheLoader extends CacheLoader<BlockPos, CachedBlockPosition> {
		private final ViewableWorld world;
		private final boolean forceLoad;

		public BlockStateCacheLoader(ViewableWorld viewableWorld, boolean bl) {
			this.world = viewableWorld;
			this.forceLoad = bl;
		}

		public CachedBlockPosition method_11714(BlockPos blockPos) throws Exception {
			return new CachedBlockPosition(this.world, blockPos, this.forceLoad);
		}
	}

	public static class Result {
		private final BlockPos field_12367;
		private final Direction field_12365;
		private final Direction field_12364;
		private final LoadingCache<BlockPos, CachedBlockPosition> cache;
		private final int width;
		private final int height;
		private final int depth;

		public Result(BlockPos blockPos, Direction direction, Direction direction2, LoadingCache<BlockPos, CachedBlockPosition> loadingCache, int i, int j, int k) {
			this.field_12367 = blockPos;
			this.field_12365 = direction;
			this.field_12364 = direction2;
			this.cache = loadingCache;
			this.width = i;
			this.height = j;
			this.depth = k;
		}

		public BlockPos method_11715() {
			return this.field_12367;
		}

		public Direction method_11719() {
			return this.field_12365;
		}

		public Direction method_11716() {
			return this.field_12364;
		}

		public int getWidth() {
			return this.width;
		}

		public int getHeight() {
			return this.height;
		}

		public CachedBlockPosition translate(int i, int j, int k) {
			return this.cache.getUnchecked(BlockPattern.method_11707(this.field_12367, this.method_11719(), this.method_11716(), i, j, k));
		}

		public String toString() {
			return MoreObjects.toStringHelper(this).add("up", this.field_12364).add("forwards", this.field_12365).add("frontTopLeft", this.field_12367).toString();
		}

		public Pair<Vec3d, Pair<Vec3d, Integer>> method_18478(Direction direction, BlockPos blockPos, double d, Vec3d vec3d, double e) {
			Direction direction2 = this.method_11719();
			Direction direction3 = direction2.rotateYClockwise();
			double f = (double)(this.method_11715().getY() + 1) - d * (double)this.getHeight();
			double g;
			double h;
			if (direction3 == Direction.NORTH) {
				g = (double)blockPos.getX() + 0.5;
				h = (double)(this.method_11715().getZ() + 1) - (1.0 - e) * (double)this.getWidth();
			} else if (direction3 == Direction.SOUTH) {
				g = (double)blockPos.getX() + 0.5;
				h = (double)this.method_11715().getZ() + (1.0 - e) * (double)this.getWidth();
			} else if (direction3 == Direction.WEST) {
				g = (double)(this.method_11715().getX() + 1) - (1.0 - e) * (double)this.getWidth();
				h = (double)blockPos.getZ() + 0.5;
			} else {
				g = (double)this.method_11715().getX() + (1.0 - e) * (double)this.getWidth();
				h = (double)blockPos.getZ() + 0.5;
			}

			double i;
			double j;
			if (direction2.getOpposite() == direction) {
				i = vec3d.x;
				j = vec3d.z;
			} else if (direction2.getOpposite() == direction.getOpposite()) {
				i = -vec3d.x;
				j = -vec3d.z;
			} else if (direction2.getOpposite() == direction.rotateYClockwise()) {
				i = -vec3d.z;
				j = vec3d.x;
			} else {
				i = vec3d.z;
				j = -vec3d.x;
			}

			int k = (direction2.getHorizontal() - direction.getOpposite().getHorizontal()) * 90;
			return Pair.of(new Vec3d(g, f, h), Pair.of(new Vec3d(i, vec3d.y, j), k));
		}
	}
}
