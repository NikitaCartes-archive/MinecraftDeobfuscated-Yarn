package net.minecraft.world.gen.chunk;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.chunk.Chunk;

public class Blender {
	private static final double field_35514 = 1.0;
	private static final HeightLimitView OLD_HEIGHT_LIMIT = new HeightLimitView() {
		@Override
		public int getHeight() {
			return 256;
		}

		@Override
		public int getBottomY() {
			return 0;
		}
	};
	public static final int field_35511 = 8;
	private static final int field_35516 = 2;
	private static final int field_35517 = BiomeCoords.fromBlock(16) - 1;
	private static final int field_35518 = 2 * field_35517 + 1;
	private static final List<Block> SURFACE_BLOCKS = List.of(
		Blocks.PODZOL,
		Blocks.GRAVEL,
		Blocks.GRASS_BLOCK,
		Blocks.STONE,
		Blocks.COARSE_DIRT,
		Blocks.SAND,
		Blocks.RED_SAND,
		Blocks.MYCELIUM,
		Blocks.SNOW_BLOCK,
		Blocks.TERRACOTTA,
		Blocks.DIRT
	);
	public static final Blender NULL_BLENDER = new Blender(null);
	public static final double field_35513 = Double.POSITIVE_INFINITY;
	private final ChunkPos chunkPos;
	private final double[] surfaceHeights;
	private final double[][] field_35522;

	public static Blender create(ChunkRegion chunkRegion, int chunkX, int chunkZ) {
		Chunk chunk = chunkRegion.getChunk(chunkX, chunkZ);
		Blender blender = chunk.getBlender();
		if (blender != null) {
			return blender;
		} else {
			Chunk chunk2 = chunkRegion.getChunk(chunkX - 1, chunkZ);
			Chunk chunk3 = chunkRegion.getChunk(chunkX, chunkZ - 1);
			Chunk chunk4 = chunkRegion.getChunk(chunkX - 1, chunkZ - 1);
			boolean bl = chunk.usesOldNoise();
			Blender blender2;
			if (bl == chunk2.usesOldNoise() && bl == chunk3.usesOldNoise() && bl == chunk4.usesOldNoise()) {
				blender2 = NULL_BLENDER;
			} else {
				blender2 = new Blender(chunk, chunk2, chunk3, chunk4);
			}

			chunk.setBlender(blender2);
			return blender2;
		}
	}

	private Blender(ChunkPos chunkPos) {
		this.chunkPos = chunkPos;
		this.surfaceHeights = new double[field_35518];
		Arrays.fill(this.surfaceHeights, Double.POSITIVE_INFINITY);
		this.field_35522 = new double[field_35518][];
	}

	private Blender(Chunk chunk1, Chunk chunk2, Chunk chunk3, Chunk chunk4) {
		this(chunk1.getPos());
		if (chunk1.usesOldNoise()) {
			this.method_39347(method_39353(0, 0), chunk1, 0, 0);
			if (!chunk2.usesOldNoise()) {
				this.method_39347(method_39353(0, 1), chunk1, 0, 4);
				this.method_39347(method_39353(0, 2), chunk1, 0, 8);
				this.method_39347(method_39353(0, 3), chunk1, 0, 12);
			}

			if (!chunk3.usesOldNoise()) {
				this.method_39347(method_39353(1, 0), chunk1, 4, 0);
				this.method_39347(method_39353(2, 0), chunk1, 8, 0);
				this.method_39347(method_39353(3, 0), chunk1, 12, 0);
			}
		} else {
			if (chunk2.usesOldNoise()) {
				this.method_39347(method_39353(0, 0), chunk2, 15, 0);
				this.method_39347(method_39353(0, 1), chunk2, 15, 4);
				this.method_39347(method_39353(0, 2), chunk2, 15, 8);
				this.method_39347(method_39353(0, 3), chunk2, 15, 12);
			}

			if (chunk3.usesOldNoise()) {
				if (!chunk2.usesOldNoise()) {
					this.method_39347(method_39353(0, 0), chunk3, 0, 15);
				}

				this.method_39347(method_39353(1, 0), chunk3, 4, 15);
				this.method_39347(method_39353(2, 0), chunk3, 8, 15);
				this.method_39347(method_39353(3, 0), chunk3, 12, 15);
			}

			if (chunk4.usesOldNoise() && !chunk2.usesOldNoise() && !chunk3.usesOldNoise()) {
				this.method_39347(method_39353(0, 0), chunk4, 15, 15);
			}
		}
	}

	private void method_39347(int index, Chunk chunk, int x, int z) {
		this.surfaceHeights[index] = (double)getSurfaceHeight(chunk, x, z);
		this.field_35522[index] = method_39354(chunk, x, z);
	}

	private static int getSurfaceHeight(Chunk chunk, int x, int z) {
		int i;
		if (chunk.hasHeightmap(Heightmap.Type.WORLD_SURFACE_WG)) {
			i = Math.min(chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, x, z), OLD_HEIGHT_LIMIT.getTopY());
		} else {
			i = OLD_HEIGHT_LIMIT.getTopY();
		}

		int j = OLD_HEIGHT_LIMIT.getBottomY();
		BlockPos.Mutable mutable = new BlockPos.Mutable(x, i, z);

		while (mutable.getY() > j) {
			mutable.move(Direction.DOWN);
			if (SURFACE_BLOCKS.contains(chunk.getBlockState(mutable).getBlock())) {
				return mutable.getY();
			}
		}

		return j;
	}

	private static double[] method_39354(Chunk chunk, int x, int z) {
		int i = OLD_HEIGHT_LIMIT.countVerticalSections() * 2 + 1;
		int j = OLD_HEIGHT_LIMIT.getBottomSectionCoord() * 2;
		double[] ds = new double[i];
		double d = 3.0;
		double e = 0.0;
		double f = 0.0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		double g = 15.0;

		for (int k = OLD_HEIGHT_LIMIT.getTopY() - 1; k >= OLD_HEIGHT_LIMIT.getBottomY(); k--) {
			double h = method_39350(chunk, mutable.set(x, k, z)) ? 1.0 : -1.0;
			int l = k % 8;
			if (l == 0) {
				double m = e / 15.0;
				int n = k / 8 + 1;
				ds[n - j] = m * d;
				e = f;
				f = 0.0;
				if (m > 0.0) {
					d = 0.1;
				}
			} else {
				f += h;
			}

			e += h;
		}

		ds[0] = e / 8.0 * d;
		return ds;
	}

	private static boolean method_39350(Chunk chunk, BlockPos pos) {
		BlockState blockState = chunk.getBlockState(pos);
		if (blockState.isAir()) {
			return false;
		} else if (blockState.isIn(BlockTags.LEAVES)) {
			return false;
		} else if (blockState.isIn(BlockTags.LOGS)) {
			return false;
		} else {
			return blockState.isOf(Blocks.BROWN_MUSHROOM_BLOCK) || blockState.isOf(Blocks.RED_MUSHROOM_BLOCK)
				? false
				: !blockState.getCollisionShape(chunk, pos).isEmpty();
		}
	}

	public double method_39344(int i, int j) {
		int k = i & 3;
		int l = j & 3;
		return k != 0 && l != 0 ? Double.POSITIVE_INFINITY : this.surfaceHeights[method_39353(k, l)];
	}

	public double method_39345(int i, int j, int k) {
		int l = i & 3;
		int m = k & 3;
		if (l != 0 && m != 0) {
			return Double.POSITIVE_INFINITY;
		} else {
			double[] ds = this.field_35522[method_39353(l, m)];
			if (ds == null) {
				return Double.POSITIVE_INFINITY;
			} else {
				int n = j - OLD_HEIGHT_LIMIT.getBottomSectionCoord() * 2;
				return n >= 0 && n < ds.length ? ds[n] * 1.0 : Double.POSITIVE_INFINITY;
			}
		}
	}

	public void method_39351(Blender.class_6751 arg) {
		for (int i = 0; i < this.surfaceHeights.length; i++) {
			double d = this.surfaceHeights[i];
			if (d != Double.POSITIVE_INFINITY) {
				arg.consume(method_39343(i) + BiomeCoords.fromChunk(this.chunkPos.x), method_39352(i) + BiomeCoords.fromChunk(this.chunkPos.z), d);
			}
		}
	}

	public void method_39346(int i, int j, Blender.class_6750 arg) {
		int k = Math.max(0, i - OLD_HEIGHT_LIMIT.getBottomSectionCoord() * 2);
		int l = Math.min(OLD_HEIGHT_LIMIT.countVerticalSections() * 2 + 1, j - OLD_HEIGHT_LIMIT.getBottomSectionCoord() * 2);

		for (int m = 0; m < this.field_35522.length; m++) {
			double[] ds = this.field_35522[m];
			if (ds != null) {
				for (int n = k; n < l; n++) {
					arg.consume(
						method_39343(m) + BiomeCoords.fromChunk(this.chunkPos.x),
						n + OLD_HEIGHT_LIMIT.getBottomSectionCoord() * 2,
						method_39352(m) + BiomeCoords.fromChunk(this.chunkPos.z),
						ds[n] * 1.0
					);
				}
			}
		}
	}

	private static int method_39353(int i, int j) {
		return field_35517 - i + j;
	}

	private static int method_39343(int i) {
		return method_39355(field_35517 - i);
	}

	private static int method_39352(int i) {
		return method_39355(i - field_35517);
	}

	private static int method_39355(int i) {
		return i & ~(i >> 31);
	}

	protected interface class_6750 {
		void consume(int i, int j, int k, double d);
	}

	protected interface class_6751 {
		void consume(int i, int j, double d);
	}
}
