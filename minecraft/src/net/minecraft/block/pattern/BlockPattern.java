package net.minecraft.block.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ViewableWorld;

public class BlockPattern {
	private final Predicate<BlockProxy>[][][] pattern;
	private final int depth;
	private final int height;
	private final int width;

	public BlockPattern(Predicate<BlockProxy>[][][] predicates) {
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
	private BlockPattern.Result testTransform(BlockPos blockPos, Direction direction, Direction direction2, LoadingCache<BlockPos, BlockProxy> loadingCache) {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					if (!this.pattern[k][j][i].test(loadingCache.getUnchecked(translate(blockPos, direction, direction2, i, j, k)))) {
						return null;
					}
				}
			}
		}

		return new BlockPattern.Result(blockPos, direction, direction2, loadingCache, this.width, this.height, this.depth);
	}

	@Nullable
	public BlockPattern.Result searchAround(ViewableWorld viewableWorld, BlockPos blockPos) {
		LoadingCache<BlockPos, BlockProxy> loadingCache = makeCache(viewableWorld, false);
		int i = Math.max(Math.max(this.width, this.height), this.depth);

		for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(blockPos, blockPos.add(i - 1, i - 1, i - 1))) {
			for (Direction direction : Direction.values()) {
				for (Direction direction2 : Direction.values()) {
					if (direction2 != direction && direction2 != direction.getOpposite()) {
						BlockPattern.Result result = this.testTransform(blockPos2, direction, direction2, loadingCache);
						if (result != null) {
							return result;
						}
					}
				}
			}
		}

		return null;
	}

	public static LoadingCache<BlockPos, BlockProxy> makeCache(ViewableWorld viewableWorld, boolean bl) {
		return CacheBuilder.newBuilder().build(new BlockPattern.BlockStateCacheLoader(viewableWorld, bl));
	}

	protected static BlockPos translate(BlockPos blockPos, Direction direction, Direction direction2, int i, int j, int k) {
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

	static class BlockStateCacheLoader extends CacheLoader<BlockPos, BlockProxy> {
		private final ViewableWorld world;
		private final boolean forceLoad;

		public BlockStateCacheLoader(ViewableWorld viewableWorld, boolean bl) {
			this.world = viewableWorld;
			this.forceLoad = bl;
		}

		public BlockProxy load(BlockPos blockPos) throws Exception {
			return new BlockProxy(this.world, blockPos, this.forceLoad);
		}
	}

	public static class Result {
		private final BlockPos frontTopLeft;
		private final Direction forwards;
		private final Direction up;
		private final LoadingCache<BlockPos, BlockProxy> cache;
		private final int width;
		private final int height;
		private final int depth;

		public Result(BlockPos blockPos, Direction direction, Direction direction2, LoadingCache<BlockPos, BlockProxy> loadingCache, int i, int j, int k) {
			this.frontTopLeft = blockPos;
			this.forwards = direction;
			this.up = direction2;
			this.cache = loadingCache;
			this.width = i;
			this.height = j;
			this.depth = k;
		}

		public BlockPos getFrontTopLeft() {
			return this.frontTopLeft;
		}

		public Direction getForwards() {
			return this.forwards;
		}

		public Direction getUp() {
			return this.up;
		}

		public int getWidth() {
			return this.width;
		}

		public int getHeight() {
			return this.height;
		}

		public BlockProxy translate(int i, int j, int k) {
			return this.cache.getUnchecked(BlockPattern.translate(this.frontTopLeft, this.getForwards(), this.getUp(), i, j, k));
		}

		public String toString() {
			return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
		}
	}
}
