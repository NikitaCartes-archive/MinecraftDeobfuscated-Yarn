package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.DefaultBlockSource;
import net.minecraft.world.gen.chunk.AquiferSampler;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class Carver<C extends CarverConfig> {
	public static final Carver<CaveCarverConfig> CAVE = register("cave", new CaveCarver(CaveCarverConfig.CAVE_CODEC));
	public static final Carver<CaveCarverConfig> NETHER_CAVE = register("nether_cave", new NetherCaveCarver(CaveCarverConfig.CAVE_CODEC));
	public static final Carver<RavineCarverConfig> RAVINE = register("canyon", new RavineCarver(RavineCarverConfig.RAVINE_CODEC));
	public static final Carver<RavineCarverConfig> UNDERWATER_CANYON = register("underwater_canyon", new UnderwaterCanyonCarver(RavineCarverConfig.RAVINE_CODEC));
	public static final Carver<CaveCarverConfig> UNDERWATER_CAVE = register("underwater_cave", new UnderwaterCaveCarver(CaveCarverConfig.CAVE_CODEC));
	protected static final BlockSource STONE_SOURCE = new DefaultBlockSource(Blocks.STONE.getDefaultState());
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
		Blocks.PACKED_ICE,
		Blocks.DEEPSLATE,
		Blocks.CALCITE,
		Blocks.SAND,
		Blocks.RED_SAND,
		Blocks.GRAVEL,
		Blocks.TUFF,
		Blocks.GRANITE,
		Blocks.IRON_ORE,
		Blocks.DEEPSLATE_IRON_ORE,
		Blocks.RAW_IRON_BLOCK,
		Blocks.COPPER_ORE,
		Blocks.DEEPSLATE_COPPER_ORE,
		Blocks.RAW_COPPER_BLOCK
	);
	protected Set<Fluid> carvableFluids = ImmutableSet.of(Fluids.WATER);
	private final Codec<ConfiguredCarver<C>> codec;

	private static <C extends CarverConfig, F extends Carver<C>> F register(String name, F carver) {
		return Registry.register(Registry.CARVER, name, carver);
	}

	public Carver(Codec<C> configCodec) {
		this.codec = configCodec.fieldOf("config").<ConfiguredCarver<C>>xmap(this::configure, ConfiguredCarver::getConfig).codec();
	}

	public ConfiguredCarver<C> configure(C config) {
		return new ConfiguredCarver<>(this, config);
	}

	public Codec<ConfiguredCarver<C>> getCodec() {
		return this.codec;
	}

	public int getBranchFactor() {
		return 4;
	}

	protected boolean carveRegion(
		CarverContext context,
		C config,
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		long seed,
		AquiferSampler sampler,
		double x,
		double y,
		double z,
		double horizontalScale,
		double verticalScale,
		BitSet carvingMask,
		Carver.SkipPredicate skipPredicate
	) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		Random random = new Random(seed + (long)i + (long)j);
		double d = (double)chunkPos.getCenterX();
		double e = (double)chunkPos.getCenterZ();
		double f = 16.0 + horizontalScale * 2.0;
		if (!(Math.abs(x - d) > f) && !(Math.abs(z - e) > f)) {
			int k = chunkPos.getStartX();
			int l = chunkPos.getStartZ();
			int m = Math.max(MathHelper.floor(x - horizontalScale) - k - 1, 0);
			int n = Math.min(MathHelper.floor(x + horizontalScale) - k, 15);
			int o = Math.max(MathHelper.floor(y - verticalScale) - 1, context.getMinY() + 1);
			int p = Math.min(MathHelper.floor(y + verticalScale) + 1, context.getMinY() + context.getHeight() - 8);
			int q = Math.max(MathHelper.floor(z - horizontalScale) - l - 1, 0);
			int r = Math.min(MathHelper.floor(z + horizontalScale) - l, 15);
			if (!config.aquifers && this.isRegionUncarvable(chunk, m, n, o, p, q, r)) {
				return false;
			} else {
				boolean bl = false;
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				BlockPos.Mutable mutable2 = new BlockPos.Mutable();

				for (int s = m; s <= n; s++) {
					int t = chunkPos.getOffsetX(s);
					double g = ((double)t + 0.5 - x) / horizontalScale;

					for (int u = q; u <= r; u++) {
						int v = chunkPos.getOffsetZ(u);
						double h = ((double)v + 0.5 - z) / horizontalScale;
						if (!(g * g + h * h >= 1.0)) {
							MutableBoolean mutableBoolean = new MutableBoolean(false);

							for (int w = p; w > o; w--) {
								double aa = ((double)w - 0.5 - y) / verticalScale;
								if (!skipPredicate.shouldSkip(context, g, aa, h, w)) {
									int ab = w - context.getMinY();
									int ac = s | u << 4 | ab << 8;
									if (!carvingMask.get(ac) || isDebug(config)) {
										carvingMask.set(ac);
										mutable.set(t, w, v);
										bl |= this.carveAtPoint(context, config, chunk, posToBiome, carvingMask, random, mutable, mutable2, sampler, mutableBoolean);
									}
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
		CarverContext context,
		C config,
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		BitSet carvingMask,
		Random random,
		BlockPos.Mutable pos,
		BlockPos.Mutable downPos,
		AquiferSampler sampler,
		MutableBoolean foundSurface
	) {
		BlockState blockState = chunk.getBlockState(pos);
		BlockState blockState2 = chunk.getBlockState(downPos.set(pos, Direction.UP));
		if (blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.MYCELIUM)) {
			foundSurface.setTrue();
		}

		if (!this.canAlwaysCarveBlock(blockState) && !isDebug(config)) {
			return false;
		} else {
			BlockState blockState3 = this.getState(context, config, pos, sampler);
			if (blockState3 == null) {
				return false;
			} else {
				chunk.setBlockState(pos, blockState3, false);
				if (sampler.needsFluidTick() && !blockState3.getFluidState().isEmpty()) {
					chunk.getFluidTickScheduler().schedule(pos, blockState3.getFluidState().getFluid(), 0);
				}

				if (foundSurface.isTrue()) {
					downPos.set(pos, Direction.DOWN);
					if (chunk.getBlockState(downPos).isOf(Blocks.DIRT)) {
						chunk.setBlockState(downPos, ((Biome)posToBiome.apply(pos)).getGenerationSettings().getSurfaceConfig().getTopMaterial(), false);
					}
				}

				return true;
			}
		}
	}

	@Nullable
	private BlockState getState(CarverContext context, C config, BlockPos pos, AquiferSampler sampler) {
		if (pos.getY() <= config.lavaLevel.getY(context)) {
			return LAVA.getBlockState();
		} else if (!config.aquifers) {
			return isDebug(config) ? getDebugState(config, AIR) : AIR;
		} else {
			BlockState blockState = sampler.apply(STONE_SOURCE, pos.getX(), pos.getY(), pos.getZ(), 0.0);
			if (blockState == Blocks.STONE.getDefaultState()) {
				return isDebug(config) ? config.debugConfig.getBarrierState() : null;
			} else {
				return isDebug(config) ? getDebugState(config, blockState) : blockState;
			}
		}
	}

	private static BlockState getDebugState(CarverConfig config, BlockState state) {
		if (state.isOf(Blocks.AIR)) {
			return config.debugConfig.getAirState();
		} else if (state.isOf(Blocks.WATER)) {
			BlockState blockState = config.debugConfig.getWaterState();
			return blockState.contains(Properties.WATERLOGGED) ? blockState.with(Properties.WATERLOGGED, Boolean.valueOf(true)) : blockState;
		} else {
			return state.isOf(Blocks.LAVA) ? config.debugConfig.getLavaState() : state;
		}
	}

	public abstract boolean carve(
		CarverContext context,
		C config,
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		Random random,
		AquiferSampler aquiferSampler,
		ChunkPos pos,
		BitSet carvingMask
	);

	public abstract boolean shouldCarve(C config, Random random);

	protected boolean canAlwaysCarveBlock(BlockState state) {
		return this.alwaysCarvableBlocks.contains(state.getBlock());
	}

	protected boolean isRegionUncarvable(Chunk chunk, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = minX; k <= maxX; k++) {
			for (int l = minZ; l <= maxZ; l++) {
				for (int m = minY - 1; m <= maxY + 1; m++) {
					mutable.set(i + k, m, j + l);
					if (this.carvableFluids.contains(chunk.getFluidState(mutable).getFluid())) {
						return true;
					}

					if (m != maxY + 1 && !isOnBoundary(k, l, minX, maxX, minZ, maxZ)) {
						m = maxY;
					}
				}
			}
		}

		return false;
	}

	private static boolean isOnBoundary(int x, int z, int minX, int maxX, int minZ, int maxZ) {
		return x == minX || x == maxX || z == minZ || z == maxZ;
	}

	protected static boolean canCarveBranch(ChunkPos pos, double x, double z, int branchIndex, int branchCount, float baseWidth) {
		double d = (double)pos.getCenterX();
		double e = (double)pos.getCenterZ();
		double f = x - d;
		double g = z - e;
		double h = (double)(branchCount - branchIndex);
		double i = (double)(baseWidth + 2.0F + 16.0F);
		return f * f + g * g - h * h <= i * i;
	}

	private static boolean isDebug(CarverConfig config) {
		return config.debugConfig.isDebugMode();
	}

	public interface SkipPredicate {
		boolean shouldSkip(CarverContext context, double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y);
	}
}
