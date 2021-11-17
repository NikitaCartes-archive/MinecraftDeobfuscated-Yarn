package net.minecraft.world.gen.chunk;

import com.google.common.primitives.Doubles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.EightWayDirection;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.chunk.Chunk;

public class BlendingData {
	private static final double field_35514 = 0.1;
	protected static final HeightLimitView OLD_HEIGHT_LIMIT = new HeightLimitView() {
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
	private static final int field_35683 = BiomeCoords.fromBlock(16);
	private static final int field_35684 = field_35683 - 1;
	private static final int field_35685 = field_35683;
	private static final int field_35686 = 2 * field_35684 + 1;
	private static final int field_35687 = 2 * field_35685 + 1;
	private static final int field_35518 = field_35686 + field_35687;
	private static final int field_35688 = field_35683 + 1;
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
	protected static final double field_35513 = Double.MAX_VALUE;
	private final boolean oldNoise;
	private boolean field_35690;
	private final boolean field_35691;
	private final double[] heights;
	private final transient double[][] field_35693;
	private final transient double[] field_35694;
	private static final Codec<double[]> field_35695 = Codec.DOUBLE.listOf().xmap(Doubles::toArray, Doubles::asList);
	public static final Codec<BlendingData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.BOOL.fieldOf("old_noise").forGetter(BlendingData::usesOldNoise),
						field_35695.optionalFieldOf("heights")
							.forGetter(
								blendingData -> DoubleStream.of(blendingData.heights).anyMatch(d -> d != Double.MAX_VALUE) ? Optional.of(blendingData.heights) : Optional.empty()
							)
					)
					.apply(instance, BlendingData::new)
		)
		.comapFlatMap(BlendingData::method_39573, Function.identity());

	private static DataResult<BlendingData> method_39573(BlendingData blendingData) {
		return blendingData.heights.length != field_35518 ? DataResult.error("heights has to be of length " + field_35518) : DataResult.success(blendingData);
	}

	private BlendingData(boolean oldNoise, Optional<double[]> optional) {
		this.oldNoise = oldNoise;
		this.heights = (double[])optional.orElse(Util.make(new double[field_35518], ds -> Arrays.fill(ds, Double.MAX_VALUE)));
		this.field_35691 = optional.isPresent();
		this.field_35693 = new double[field_35518][];
		this.field_35694 = new double[field_35688 * field_35688];
	}

	public boolean usesOldNoise() {
		return this.oldNoise;
	}

	@Nullable
	public static BlendingData getBlendingData(ChunkRegion chunkRegion, int chunkX, int chunkZ) {
		Chunk chunk = chunkRegion.getChunk(chunkX, chunkZ);
		BlendingData blendingData = chunk.getBlendingData();
		if (blendingData != null && blendingData.usesOldNoise()) {
			blendingData.method_39572(chunk, getAdjacentChunksWithNoise(chunkRegion, chunkX, chunkZ, false));
			return blendingData;
		} else {
			return null;
		}
	}

	public static Set<EightWayDirection> getAdjacentChunksWithNoise(StructureWorldAccess access, int chunkX, int chunkZ, boolean newNoise) {
		Set<EightWayDirection> set = EnumSet.noneOf(EightWayDirection.class);

		for (EightWayDirection eightWayDirection : EightWayDirection.values()) {
			int i = chunkX;
			int j = chunkZ;

			for (Direction direction : eightWayDirection.getDirections()) {
				i += direction.getOffsetX();
				j += direction.getOffsetZ();
			}

			if (access.getChunk(i, j).usesOldNoise() == newNoise) {
				set.add(eightWayDirection);
			}
		}

		return set;
	}

	private void method_39572(Chunk chunk, Set<EightWayDirection> set) {
		if (!this.field_35690) {
			BlockPos.Mutable mutable = new BlockPos.Mutable(0, OLD_HEIGHT_LIMIT.getBottomY(), 0);

			for (int i = 0; i < this.field_35694.length; i++) {
				mutable.setX(Math.max(BiomeCoords.toBlock(this.method_39568(i)), 15));
				mutable.setZ(Math.max(BiomeCoords.toBlock(this.method_39577(i)), 15));
				this.field_35694[i] = isCollidableAndNotTreeAt(chunk, mutable) ? 1.0 : -1.0;
			}

			if (set.contains(EightWayDirection.NORTH) || set.contains(EightWayDirection.WEST) || set.contains(EightWayDirection.NORTH_WEST)) {
				this.method_39347(method_39578(0, 0), chunk, 0, 0);
			}

			if (set.contains(EightWayDirection.NORTH)) {
				for (int i = 1; i < field_35683; i++) {
					this.method_39347(method_39578(i, 0), chunk, 4 * i, 0);
				}
			}

			if (set.contains(EightWayDirection.WEST)) {
				for (int i = 1; i < field_35683; i++) {
					this.method_39347(method_39578(0, i), chunk, 0, 4 * i);
				}
			}

			if (set.contains(EightWayDirection.EAST)) {
				for (int i = 1; i < field_35683; i++) {
					this.method_39347(method_39582(field_35685, i), chunk, 15, 4 * i);
				}
			}

			if (set.contains(EightWayDirection.SOUTH)) {
				for (int i = 0; i < field_35683; i++) {
					this.method_39347(method_39582(i, field_35685), chunk, 4 * i, 15);
				}
			}

			if (set.contains(EightWayDirection.EAST) && set.contains(EightWayDirection.NORTH_EAST)) {
				this.method_39347(method_39582(field_35685, 0), chunk, 15, 0);
			}

			if (set.contains(EightWayDirection.EAST) && set.contains(EightWayDirection.SOUTH) && set.contains(EightWayDirection.SOUTH_EAST)) {
				this.method_39347(method_39582(field_35685, field_35685), chunk, 15, 15);
			}

			this.field_35690 = true;
		}
	}

	private void method_39347(int index, Chunk chunk, int x, int z) {
		if (!this.field_35691) {
			this.heights[index] = (double)getSurfaceHeight(chunk, x, z);
		}

		this.field_35693[index] = method_39354(chunk, x, z);
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
		double[] ds = new double[method_39576()];
		int i = method_39581();
		double d = 30.0;
		double e = 0.0;
		double f = 0.0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		double g = 15.0;

		for (int j = OLD_HEIGHT_LIMIT.getTopY() - 1; j >= OLD_HEIGHT_LIMIT.getBottomY(); j--) {
			double h = isCollidableAndNotTreeAt(chunk, mutable.set(x, j, z)) ? 1.0 : -1.0;
			int k = j % 8;
			if (k == 0) {
				double l = e / 15.0;
				int m = j / 8 + 1;
				ds[m - i] = l * d;
				e = f;
				f = 0.0;
				if (l > 0.0) {
					d = 1.0;
				}
			} else {
				f += h;
			}

			e += h;
		}

		return ds;
	}

	private static boolean isCollidableAndNotTreeAt(Chunk chunk, BlockPos pos) {
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

	protected double method_39344(int i, int j, int k) {
		if (i == field_35685 || k == field_35685) {
			return this.heights[method_39582(i, k)];
		} else {
			return i != 0 && k != 0 ? Double.MAX_VALUE : this.heights[method_39578(i, k)];
		}
	}

	private static double method_39575(@Nullable double[] ds, int i) {
		if (ds == null) {
			return Double.MAX_VALUE;
		} else {
			int j = i - method_39581();
			return j >= 0 && j < ds.length ? ds[j] * 0.1 : Double.MAX_VALUE;
		}
	}

	protected double method_39345(int i, int j, int k) {
		if (j == method_39583()) {
			return this.field_35694[this.method_39569(i, k)] * 0.1;
		} else if (i == field_35685 || k == field_35685) {
			return method_39575(this.field_35693[method_39582(i, k)], j);
		} else {
			return i != 0 && k != 0 ? Double.MAX_VALUE : method_39575(this.field_35693[method_39578(i, k)], j);
		}
	}

	protected void method_39351(int i, int j, BlendingData.class_6751 arg) {
		for (int k = 0; k < this.field_35693.length; k++) {
			double d = this.heights[k];
			if (d != Double.MAX_VALUE) {
				arg.consume(i + method_39343(k), j + method_39352(k), d);
			}
		}
	}

	protected void method_39346(int i, int j, int k, int l, BlendingData.class_6750 arg) {
		int m = method_39581();
		int n = Math.max(0, k - m);
		int o = Math.min(method_39576(), l - m);

		for (int p = 0; p < this.field_35693.length; p++) {
			double[] ds = this.field_35693[p];
			if (ds != null) {
				int q = i + method_39343(p);
				int r = j + method_39352(p);

				for (int s = n; s < o; s++) {
					arg.consume(q, s + m, r, ds[s] * 0.1);
				}
			}
		}

		if (m >= k && m <= l) {
			for (int px = 0; px < this.field_35694.length; px++) {
				int t = this.method_39568(px);
				int q = this.method_39577(px);
				arg.consume(t, m, q, this.field_35694[px] * 0.1);
			}
		}
	}

	private int method_39569(int i, int j) {
		return i * field_35688 + j;
	}

	private int method_39568(int i) {
		return i / field_35688;
	}

	private int method_39577(int i) {
		return i % field_35688;
	}

	private static int method_39576() {
		return OLD_HEIGHT_LIMIT.countVerticalSections() * 2;
	}

	private static int method_39581() {
		return method_39583() + 1;
	}

	private static int method_39583() {
		return OLD_HEIGHT_LIMIT.getBottomSectionCoord() * 2;
	}

	private static int method_39578(int i, int j) {
		return field_35684 - i + j;
	}

	private static int method_39582(int i, int j) {
		return field_35686 + i + field_35685 - j;
	}

	private static int method_39343(int i) {
		if (i < field_35686) {
			return method_39355(field_35684 - i);
		} else {
			int j = i - field_35686;
			return field_35685 - method_39355(field_35685 - j);
		}
	}

	private static int method_39352(int i) {
		if (i < field_35686) {
			return method_39355(i - field_35684);
		} else {
			int j = i - field_35686;
			return field_35685 - method_39355(j - field_35685);
		}
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
