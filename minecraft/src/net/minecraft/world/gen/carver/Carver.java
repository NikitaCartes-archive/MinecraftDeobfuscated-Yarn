package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class Carver<C extends CarverConfig> {
	public static final Carver<ProbabilityConfig> CAVE = register("cave", new CaveCarver(ProbabilityConfig.CODEC, 256));
	public static final Carver<ProbabilityConfig> NETHER_CAVE = register("nether_cave", new NetherCaveCarver(ProbabilityConfig.CODEC));
	public static final Carver<ProbabilityConfig> CANYON = register("canyon", new RavineCarver(ProbabilityConfig.CODEC));
	public static final Carver<ProbabilityConfig> UNDERWATER_CANYON = register("underwater_canyon", new UnderwaterRavineCarver(ProbabilityConfig.CODEC));
	public static final Carver<ProbabilityConfig> UNDERWATER_CAVE = register("underwater_cave", new UnderwaterCaveCarver(ProbabilityConfig.CODEC));
	protected static final BlockState AIR = Blocks.AIR.getDefaultState();
	protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
	protected static final FluidState WATER = Fluids.WATER.getDefaultState();
	protected static final FluidState LAVA = Fluids.LAVA.getDefaultState();
	protected Set<Block> alwaysCarvableBlocks = ImmutableSet.of(
		Blocks.STONE,
		Blocks.GRANITE,
		Blocks.DIORITE,
		Blocks.ANDESITE,
		Blocks.DIRT,
		Blocks.COARSE_DIRT,
		Blocks.PODZOL,
		Blocks.GRASS_BLOCK,
		Blocks.TERRACOTTA,
		Blocks.WHITE_TERRACOTTA,
		Blocks.ORANGE_TERRACOTTA,
		Blocks.MAGENTA_TERRACOTTA,
		Blocks.LIGHT_BLUE_TERRACOTTA,
		Blocks.YELLOW_TERRACOTTA,
		Blocks.LIME_TERRACOTTA,
		Blocks.PINK_TERRACOTTA,
		Blocks.GRAY_TERRACOTTA,
		Blocks.LIGHT_GRAY_TERRACOTTA,
		Blocks.CYAN_TERRACOTTA,
		Blocks.PURPLE_TERRACOTTA,
		Blocks.BLUE_TERRACOTTA,
		Blocks.BROWN_TERRACOTTA,
		Blocks.GREEN_TERRACOTTA,
		Blocks.RED_TERRACOTTA,
		Blocks.BLACK_TERRACOTTA,
		Blocks.SANDSTONE,
		Blocks.RED_SANDSTONE,
		Blocks.MYCELIUM,
		Blocks.SNOW,
		Blocks.PACKED_ICE
	);
	protected Set<Fluid> carvableFluids = ImmutableSet.of(Fluids.WATER);
	private final Codec<ConfiguredCarver<C>> codec;
	protected final int heightLimit;

	private static <C extends CarverConfig, F extends Carver<C>> F register(String string, F carver) {
		return Registry.register(Registry.CARVER, string, carver);
	}

	public Carver(Codec<C> configCodec, int heightLimit) {
		this.heightLimit = heightLimit;
		this.codec = configCodec.fieldOf("config")
			.<ConfiguredCarver<C>>xmap(carverConfig -> new ConfiguredCarver<>(this, (C)carverConfig), configuredCarver -> configuredCarver.config)
			.codec();
	}

	public Codec<ConfiguredCarver<C>> getCodec() {
		return this.codec;
	}

	public int getBranchFactor() {
		return 4;
	}

	protected boolean carveRegion(
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		long seed,
		int seaLevel,
		int chunkX,
		int chunkZ,
		double x,
		double y,
		double z,
		double yaw,
		double pitch,
		BitSet carvingMask
	) {
		Random random = new Random(seed + (long)chunkX + (long)chunkZ);
		double d = (double)(chunkX * 16 + 8);
		double e = (double)(chunkZ * 16 + 8);
		if (!(x < d - 16.0 - yaw * 2.0) && !(z < e - 16.0 - yaw * 2.0) && !(x > d + 16.0 + yaw * 2.0) && !(z > e + 16.0 + yaw * 2.0)) {
			int i = Math.max(MathHelper.floor(x - yaw) - chunkX * 16 - 1, 0);
			int j = Math.min(MathHelper.floor(x + yaw) - chunkX * 16 + 1, 16);
			int k = Math.max(MathHelper.floor(y - pitch) - 1, 1);
			int l = Math.min(MathHelper.floor(y + pitch) + 1, this.heightLimit - 8);
			int m = Math.max(MathHelper.floor(z - yaw) - chunkZ * 16 - 1, 0);
			int n = Math.min(MathHelper.floor(z + yaw) - chunkZ * 16 + 1, 16);
			if (this.isRegionUncarvable(chunk, chunkX, chunkZ, i, j, k, l, m, n)) {
				return false;
			} else {
				boolean bl = false;
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				BlockPos.Mutable mutable2 = new BlockPos.Mutable();
				BlockPos.Mutable mutable3 = new BlockPos.Mutable();

				for (int o = i; o < j; o++) {
					int p = o + chunkX * 16;
					double f = ((double)p + 0.5 - x) / yaw;

					for (int q = m; q < n; q++) {
						int r = q + chunkZ * 16;
						double g = ((double)r + 0.5 - z) / yaw;
						if (!(f * f + g * g >= 1.0)) {
							MutableBoolean mutableBoolean = new MutableBoolean(false);

							for (int s = l; s > k; s--) {
								double h = ((double)s - 0.5 - y) / pitch;
								if (!this.isPositionExcluded(f, h, g, s)) {
									bl |= this.carveAtPoint(chunk, posToBiome, carvingMask, random, mutable, mutable2, mutable3, seaLevel, chunkX, chunkZ, p, r, o, s, q, mutableBoolean);
								}
							}
						}
					}
				}

				return bl;
			}
		} else {
			return false;
		}
	}

	protected boolean carveAtPoint(
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		BitSet carvingMask,
		Random random,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		BlockPos.Mutable mutable3,
		int seaLevel,
		int mainChunkX,
		int mainChunkZ,
		int x,
		int z,
		int relativeX,
		int y,
		int relativeZ,
		MutableBoolean mutableBoolean
	) {
		int i = relativeX | relativeZ << 4 | y << 8;
		if (carvingMask.get(i)) {
			return false;
		} else {
			carvingMask.set(i);
			mutable.set(x, y, z);
			BlockState blockState = chunk.getBlockState(mutable);
			BlockState blockState2 = chunk.getBlockState(mutable2.set(mutable, Direction.UP));
			if (blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.MYCELIUM)) {
				mutableBoolean.setTrue();
			}

			if (!this.canCarveBlock(blockState, blockState2)) {
				return false;
			} else {
				if (y < 11) {
					chunk.setBlockState(mutable, LAVA.getBlockState(), false);
				} else {
					chunk.setBlockState(mutable, CAVE_AIR, false);
					if (mutableBoolean.isTrue()) {
						mutable3.set(mutable, Direction.DOWN);
						if (chunk.getBlockState(mutable3).isOf(Blocks.DIRT)) {
							chunk.setBlockState(mutable3, ((Biome)posToBiome.apply(mutable)).getSurfaceConfig().getTopMaterial(), false);
						}
					}
				}

				return true;
			}
		}
	}

	public abstract boolean carve(
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		Random random,
		int seaLevel,
		int chunkX,
		int chunkZ,
		int mainChunkX,
		int mainChunkZ,
		BitSet carvingMask,
		C carverConfig
	);

	public abstract boolean shouldCarve(Random random, int chunkX, int chunkZ, C config);

	protected boolean canAlwaysCarveBlock(BlockState state) {
		return this.alwaysCarvableBlocks.contains(state.getBlock());
	}

	protected boolean canCarveBlock(BlockState state, BlockState stateAbove) {
		return this.canAlwaysCarveBlock(state) || (state.isOf(Blocks.SAND) || state.isOf(Blocks.GRAVEL)) && !stateAbove.getFluidState().isIn(FluidTags.WATER);
	}

	protected boolean isRegionUncarvable(Chunk chunk, int mainChunkX, int mainChunkZ, int relMinX, int relMaxX, int minY, int maxY, int relMinZ, int relMaxZ) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = relMinX; i < relMaxX; i++) {
			for (int j = relMinZ; j < relMaxZ; j++) {
				for (int k = minY - 1; k <= maxY + 1; k++) {
					if (this.carvableFluids.contains(chunk.getFluidState(mutable.set(i + mainChunkX * 16, k, j + mainChunkZ * 16)).getFluid())) {
						return true;
					}

					if (k != maxY + 1 && !this.isOnBoundary(relMinX, relMaxX, relMinZ, relMaxZ, i, j)) {
						k = maxY;
					}
				}
			}
		}

		return false;
	}

	private boolean isOnBoundary(int minX, int maxX, int minZ, int maxZ, int x, int z) {
		return x == minX || x == maxX - 1 || z == minZ || z == maxZ - 1;
	}

	protected boolean canCarveBranch(int mainChunkX, int mainChunkZ, double x, double z, int branch, int branchCount, float baseWidth) {
		double d = (double)(mainChunkX * 16 + 8);
		double e = (double)(mainChunkZ * 16 + 8);
		double f = x - d;
		double g = z - e;
		double h = (double)(branchCount - branch);
		double i = (double)(baseWidth + 2.0F + 16.0F);
		return f * f + g * g - h * h <= i * i;
	}

	protected abstract boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y);
}
