package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_6643;
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
import net.minecraft.world.gen.chunk.AquiferSampler;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class Carver<C extends CarverConfig> {
	public static final Carver<CaveCarverConfig> CAVE = register("cave", new CaveCarver(CaveCarverConfig.CAVE_CODEC));
	public static final Carver<CaveCarverConfig> NETHER_CAVE = register("nether_cave", new NetherCaveCarver(CaveCarverConfig.CAVE_CODEC));
	public static final Carver<RavineCarverConfig> RAVINE = register("canyon", new RavineCarver(RavineCarverConfig.RAVINE_CODEC));
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
		AquiferSampler aquiferSampler,
		double d,
		double e,
		double f,
		double g,
		double h,
		class_6643 arg,
		Carver.SkipPredicate skipPredicate
	) {
		ChunkPos chunkPos = chunk.getPos();
		double i = (double)chunkPos.getCenterX();
		double j = (double)chunkPos.getCenterZ();
		double k = 16.0 + g * 2.0;
		if (!(Math.abs(d - i) > k) && !(Math.abs(f - j) > k)) {
			int l = chunkPos.getStartX();
			int m = chunkPos.getStartZ();
			int n = Math.max(MathHelper.floor(d - g) - l - 1, 0);
			int o = Math.min(MathHelper.floor(d + g) - l, 15);
			int p = Math.max(MathHelper.floor(e - h) - 1, context.getMinY() + 1);
			int q = Math.min(MathHelper.floor(e + h) + 1, context.getMinY() + context.getHeight() - 8);
			int r = Math.max(MathHelper.floor(f - g) - m - 1, 0);
			int s = Math.min(MathHelper.floor(f + g) - m, 15);
			boolean bl = false;
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			BlockPos.Mutable mutable2 = new BlockPos.Mutable();

			for (int t = n; t <= o; t++) {
				int u = chunkPos.getOffsetX(t);
				double v = ((double)u + 0.5 - d) / g;

				for (int w = r; w <= s; w++) {
					int x = chunkPos.getOffsetZ(w);
					double y = ((double)x + 0.5 - f) / g;
					if (!(v * v + y * y >= 1.0)) {
						MutableBoolean mutableBoolean = new MutableBoolean(false);

						for (int z = q; z > p; z--) {
							double aa = ((double)z - 0.5 - e) / h;
							if (!skipPredicate.shouldSkip(context, v, aa, y, z) && (!arg.method_38868(t, z, w) || isDebug(config))) {
								arg.method_38865(t, z, w);
								mutable.set(u, z, x);
								bl |= this.carveAtPoint(context, config, chunk, posToBiome, arg, mutable, mutable2, aquiferSampler, mutableBoolean);
							}
						}
					}
				}
			}

			return bl;
		} else {
			return false;
		}
	}

	protected boolean carveAtPoint(
		CarverContext context,
		C config,
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		class_6643 arg,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		AquiferSampler aquiferSampler,
		MutableBoolean mutableBoolean
	) {
		BlockState blockState = chunk.getBlockState(mutable);
		if (blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.MYCELIUM)) {
			mutableBoolean.setTrue();
		}

		if (!this.canAlwaysCarveBlock(blockState) && !isDebug(config)) {
			return false;
		} else {
			BlockState blockState2 = this.getState(context, config, mutable, aquiferSampler);
			if (blockState2 == null) {
				return false;
			} else {
				chunk.setBlockState(mutable, blockState2, false);
				if (aquiferSampler.needsFluidTick() && !blockState2.getFluidState().isEmpty()) {
					chunk.getFluidTickScheduler().schedule(mutable, blockState2.getFluidState().getFluid(), 0);
				}

				if (mutableBoolean.isTrue()) {
					mutable2.set(mutable, Direction.DOWN);
					if (chunk.getBlockState(mutable2).isOf(Blocks.DIRT)) {
						chunk.setBlockState(mutable2, ((Biome)posToBiome.apply(mutable)).getGenerationSettings().getSurfaceConfig().getTopMaterial(), false);
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
		} else {
			BlockState blockState = sampler.apply(pos.getX(), pos.getY(), pos.getZ(), 0.0, 0.0);
			if (blockState == null) {
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
		class_6643 arg
	);

	public abstract boolean shouldCarve(C config, Random random);

	protected boolean canAlwaysCarveBlock(BlockState state) {
		return this.alwaysCarvableBlocks.contains(state.getBlock());
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
