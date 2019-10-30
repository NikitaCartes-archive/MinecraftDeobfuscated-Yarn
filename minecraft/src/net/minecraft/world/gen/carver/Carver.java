package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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

public abstract class Carver<C extends CarverConfig> {
	public static final Carver<ProbabilityConfig> CAVE = register("cave", new CaveCarver(ProbabilityConfig::deserialize, 256));
	public static final Carver<ProbabilityConfig> HELL_CAVE = register("hell_cave", new NetherCaveCarver(ProbabilityConfig::deserialize));
	public static final Carver<ProbabilityConfig> CANYON = register("canyon", new RavineCarver(ProbabilityConfig::deserialize));
	public static final Carver<ProbabilityConfig> UNDERWATER_CANYON = register("underwater_canyon", new UnderwaterRavineCarver(ProbabilityConfig::deserialize));
	public static final Carver<ProbabilityConfig> UNDERWATER_CAVE = register("underwater_cave", new UnderwaterCaveCarver(ProbabilityConfig::deserialize));
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
	private final Function<Dynamic<?>, ? extends C> configDeserializer;
	protected final int heightLimit;

	private static <C extends CarverConfig, F extends Carver<C>> F register(String string, F carver) {
		return Registry.register(Registry.CARVER, string, carver);
	}

	public Carver(Function<Dynamic<?>, ? extends C> configDeserializer, int heightLimit) {
		this.configDeserializer = configDeserializer;
		this.heightLimit = heightLimit;
	}

	public int getBranchFactor() {
		return 4;
	}

	protected boolean carveRegion(
		Chunk chunk, Function<BlockPos, Biome> function, long l, int i, int j, int k, double d, double e, double f, double g, double h, BitSet bitSet
	) {
		Random random = new Random(l + (long)j + (long)k);
		double m = (double)(j * 16 + 8);
		double n = (double)(k * 16 + 8);
		if (!(d < m - 16.0 - g * 2.0) && !(f < n - 16.0 - g * 2.0) && !(d > m + 16.0 + g * 2.0) && !(f > n + 16.0 + g * 2.0)) {
			int o = Math.max(MathHelper.floor(d - g) - j * 16 - 1, 0);
			int p = Math.min(MathHelper.floor(d + g) - j * 16 + 1, 16);
			int q = Math.max(MathHelper.floor(e - h) - 1, 1);
			int r = Math.min(MathHelper.floor(e + h) + 1, this.heightLimit - 8);
			int s = Math.max(MathHelper.floor(f - g) - k * 16 - 1, 0);
			int t = Math.min(MathHelper.floor(f + g) - k * 16 + 1, 16);
			if (this.isRegionUncarvable(chunk, j, k, o, p, q, r, s, t)) {
				return false;
			} else {
				boolean bl = false;
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				BlockPos.Mutable mutable2 = new BlockPos.Mutable();
				BlockPos.Mutable mutable3 = new BlockPos.Mutable();

				for (int u = o; u < p; u++) {
					int v = u + j * 16;
					double w = ((double)v + 0.5 - d) / g;

					for (int x = s; x < t; x++) {
						int y = x + k * 16;
						double z = ((double)y + 0.5 - f) / g;
						if (!(w * w + z * z >= 1.0)) {
							AtomicBoolean atomicBoolean = new AtomicBoolean(false);

							for (int aa = r; aa > q; aa--) {
								double ab = ((double)aa - 0.5 - e) / h;
								if (!this.isPositionExcluded(w, ab, z, aa)) {
									bl |= this.carveAtPoint(chunk, function, bitSet, random, mutable, mutable2, mutable3, i, j, k, v, y, u, aa, x, atomicBoolean);
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
		Function<BlockPos, Biome> function,
		BitSet bitSet,
		Random random,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		BlockPos.Mutable mutable3,
		int mainChunkX,
		int mainChunkZ,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		AtomicBoolean atomicBoolean
	) {
		int o = l | n << 4 | m << 8;
		if (bitSet.get(o)) {
			return false;
		} else {
			bitSet.set(o);
			mutable.set(j, m, k);
			BlockState blockState = chunk.getBlockState(mutable);
			BlockState blockState2 = chunk.getBlockState(mutable2.set(mutable).setOffset(Direction.UP));
			if (blockState.getBlock() == Blocks.GRASS_BLOCK || blockState.getBlock() == Blocks.MYCELIUM) {
				atomicBoolean.set(true);
			}

			if (!this.canCarveBlock(blockState, blockState2)) {
				return false;
			} else {
				if (m < 11) {
					chunk.setBlockState(mutable, LAVA.getBlockState(), false);
				} else {
					chunk.setBlockState(mutable, CAVE_AIR, false);
					if (atomicBoolean.get()) {
						mutable3.set(mutable).setOffset(Direction.DOWN);
						if (chunk.getBlockState(mutable3).getBlock() == Blocks.DIRT) {
							chunk.setBlockState(mutable3, ((Biome)function.apply(mutable)).getSurfaceConfig().getTopMaterial(), false);
						}
					}
				}

				return true;
			}
		}
	}

	public abstract boolean carve(
		Chunk chunk, Function<BlockPos, Biome> function, Random random, int chunkX, int chunkZ, int mainChunkX, int mainChunkZ, int i, BitSet bitSet, C carverConfig
	);

	public abstract boolean shouldCarve(Random random, int chunkX, int chunkZ, C config);

	protected boolean canAlwaysCarveBlock(BlockState state) {
		return this.alwaysCarvableBlocks.contains(state.getBlock());
	}

	protected boolean canCarveBlock(BlockState state, BlockState stateAbove) {
		Block block = state.getBlock();
		return this.canAlwaysCarveBlock(state) || (block == Blocks.SAND || block == Blocks.GRAVEL) && !stateAbove.getFluidState().matches(FluidTags.WATER);
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

	protected boolean canCarveBranch(int mainChunkX, int mainChunkZ, double relativeX, double relativeZ, int branch, int branchCount, float baseWidth) {
		double d = (double)(mainChunkX * 16 + 8);
		double e = (double)(mainChunkZ * 16 + 8);
		double f = relativeX - d;
		double g = relativeZ - e;
		double h = (double)(branchCount - branch);
		double i = (double)(baseWidth + 2.0F + 16.0F);
		return f * f + g * g - h * h <= i * i;
	}

	protected abstract boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y);
}
