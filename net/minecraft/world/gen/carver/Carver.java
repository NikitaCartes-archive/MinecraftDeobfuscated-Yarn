/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CaveCarver;
import net.minecraft.world.gen.carver.CaveCarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.NetherCaveCarver;
import net.minecraft.world.gen.carver.RavineCarver;
import net.minecraft.world.gen.carver.RavineCarverConfig;
import net.minecraft.world.gen.carver.UnderwaterCanyonCarver;
import net.minecraft.world.gen.carver.UnderwaterCaveCarver;
import net.minecraft.world.gen.chunk.AquiferSampler;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

public abstract class Carver<C extends CarverConfig> {
    public static final Carver<CaveCarverConfig> CAVE = Carver.register("cave", new CaveCarver(CaveCarverConfig.CAVE_CODEC));
    public static final Carver<CaveCarverConfig> NETHER_CAVE = Carver.register("nether_cave", new NetherCaveCarver(CaveCarverConfig.CAVE_CODEC));
    public static final Carver<RavineCarverConfig> RAVINE = Carver.register("canyon", new RavineCarver(RavineCarverConfig.RAVINE_CODEC));
    public static final Carver<RavineCarverConfig> UNDERWATER_CANYON = Carver.register("underwater_canyon", new UnderwaterCanyonCarver(RavineCarverConfig.RAVINE_CODEC));
    public static final Carver<CaveCarverConfig> UNDERWATER_CAVE = Carver.register("underwater_cave", new UnderwaterCaveCarver(CaveCarverConfig.CAVE_CODEC));
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    protected static final FluidState WATER = Fluids.WATER.getDefaultState();
    protected static final FluidState LAVA = Fluids.LAVA.getDefaultState();
    protected Set<Block> alwaysCarvableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE, Blocks.DEEPSLATE, Blocks.CALCITE, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.TUFF, Blocks.GRANITE, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.RAW_IRON_BLOCK, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_COPPER_BLOCK});
    protected Set<Fluid> carvableFluids = ImmutableSet.of(Fluids.WATER);
    private final Codec<ConfiguredCarver<C>> codec;

    private static <C extends CarverConfig, F extends Carver<C>> F register(String name, F carver) {
        return (F)Registry.register(Registry.CARVER, name, carver);
    }

    public Carver(Codec<C> configCodec) {
        this.codec = ((MapCodec)configCodec.fieldOf("config")).xmap(this::configure, ConfiguredCarver::getConfig).codec();
    }

    public ConfiguredCarver<C> configure(C config) {
        return new ConfiguredCarver<C>(this, config);
    }

    public Codec<ConfiguredCarver<C>> getCodec() {
        return this.codec;
    }

    public int getBranchFactor() {
        return 4;
    }

    protected boolean carveRegion(CarverContext context, C config, Chunk chunk, Function<BlockPos, Biome> posToBiome, long seed, AquiferSampler sampler, double x, double y, double z, double horizontalScale, double verticalScale, BitSet carvingMask, SkipPredicate skipPredicate) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.x;
        int j = chunkPos.z;
        Random random = new Random(seed + (long)i + (long)j);
        double d = chunkPos.getCenterX();
        double e = chunkPos.getCenterZ();
        double f = 16.0 + horizontalScale * 2.0;
        if (Math.abs(x - d) > f || Math.abs(z - e) > f) {
            return false;
        }
        int k = chunkPos.getStartX();
        int l = chunkPos.getStartZ();
        int m = Math.max(MathHelper.floor(x - horizontalScale) - k - 1, 0);
        int n = Math.min(MathHelper.floor(x + horizontalScale) - k, 15);
        int o = Math.max(MathHelper.floor(y - verticalScale) - 1, context.getMinY() + 1);
        int p = Math.min(MathHelper.floor(y + verticalScale) + 1, context.getMinY() + context.getHeight() - 8);
        int q = Math.max(MathHelper.floor(z - horizontalScale) - l - 1, 0);
        int r = Math.min(MathHelper.floor(z + horizontalScale) - l, 15);
        if (!((CarverConfig)config).aquifers && this.isRegionUncarvable(chunk, m, n, o, p, q, r)) {
            return false;
        }
        boolean bl = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        for (int s = m; s <= n; ++s) {
            int t = chunkPos.getOffsetX(s);
            double g = ((double)t + 0.5 - x) / horizontalScale;
            for (int u = q; u <= r; ++u) {
                int v = chunkPos.getOffsetZ(u);
                double h = ((double)v + 0.5 - z) / horizontalScale;
                if (g * g + h * h >= 1.0) continue;
                MutableBoolean mutableBoolean = new MutableBoolean(false);
                for (int w = p; w > o; --w) {
                    int ab;
                    int ac;
                    double aa = ((double)w - 0.5 - y) / verticalScale;
                    if (skipPredicate.shouldSkip(context, g, aa, h, w) || carvingMask.get(ac = s | u << 4 | (ab = w - context.getMinY()) << 8) && !Carver.isDebug(config)) continue;
                    carvingMask.set(ac);
                    mutable.set(t, w, v);
                    bl |= this.carveAtPoint(context, config, chunk, posToBiome, carvingMask, random, mutable, mutable2, sampler, mutableBoolean);
                }
            }
        }
        return bl;
    }

    protected boolean carveAtPoint(CarverContext context, C config, Chunk chunk, Function<BlockPos, Biome> posToBiome, BitSet carvingMask, Random random, BlockPos.Mutable pos, BlockPos.Mutable downPos, AquiferSampler sampler, MutableBoolean foundSurface) {
        BlockState blockState = chunk.getBlockState(pos);
        if (blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.MYCELIUM)) {
            foundSurface.setTrue();
        }
        if (!this.canAlwaysCarveBlock(blockState) && !Carver.isDebug(config)) {
            return false;
        }
        BlockState blockState2 = this.getState(context, config, pos, sampler);
        if (blockState2 == null) {
            return false;
        }
        chunk.setBlockState(pos, blockState2, false);
        if (sampler.needsFluidTick() && !blockState2.getFluidState().isEmpty()) {
            chunk.getFluidTickScheduler().schedule(pos, blockState2.getFluidState().getFluid(), 0);
        }
        if (foundSurface.isTrue()) {
            downPos.set((Vec3i)pos, Direction.DOWN);
            if (chunk.getBlockState(downPos).isOf(Blocks.DIRT)) {
                chunk.setBlockState(downPos, posToBiome.apply(pos).getGenerationSettings().getSurfaceConfig().getTopMaterial(), false);
            }
        }
        return true;
    }

    @Nullable
    private BlockState getState(CarverContext context, C config, BlockPos pos, AquiferSampler sampler) {
        if (pos.getY() <= ((CarverConfig)config).lavaLevel.getY(context)) {
            return LAVA.getBlockState();
        }
        if (!((CarverConfig)config).aquifers) {
            return Carver.isDebug(config) ? Carver.getDebugState(config, AIR) : AIR;
        }
        BlockState blockState = sampler.apply(pos.getX(), pos.getY(), pos.getZ(), 0.0, 0.0);
        if (blockState == null) {
            return Carver.isDebug(config) ? ((CarverConfig)config).debugConfig.getBarrierState() : null;
        }
        return Carver.isDebug(config) ? Carver.getDebugState(config, blockState) : blockState;
    }

    private static BlockState getDebugState(CarverConfig config, BlockState state) {
        if (state.isOf(Blocks.AIR)) {
            return config.debugConfig.getAirState();
        }
        if (state.isOf(Blocks.WATER)) {
            BlockState blockState = config.debugConfig.getWaterState();
            if (blockState.contains(Properties.WATERLOGGED)) {
                return (BlockState)blockState.with(Properties.WATERLOGGED, true);
            }
            return blockState;
        }
        if (state.isOf(Blocks.LAVA)) {
            return config.debugConfig.getLavaState();
        }
        return state;
    }

    public abstract boolean carve(CarverContext var1, C var2, Chunk var3, Function<BlockPos, Biome> var4, Random var5, AquiferSampler var6, ChunkPos var7, BitSet var8);

    public abstract boolean shouldCarve(C var1, Random var2);

    protected boolean canAlwaysCarveBlock(BlockState state) {
        return this.alwaysCarvableBlocks.contains(state.getBlock());
    }

    protected boolean isRegionUncarvable(Chunk chunk, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int k = minX; k <= maxX; ++k) {
            for (int l = minZ; l <= maxZ; ++l) {
                for (int m = minY - 1; m <= maxY + 1; ++m) {
                    mutable.set(i + k, m, j + l);
                    if (this.carvableFluids.contains(chunk.getFluidState(mutable).getFluid())) {
                        return true;
                    }
                    if (m == maxY + 1 || Carver.isOnBoundary(k, l, minX, maxX, minZ, maxZ)) continue;
                    m = maxY;
                }
            }
        }
        return false;
    }

    private static boolean isOnBoundary(int x, int z, int minX, int maxX, int minZ, int maxZ) {
        return x == minX || x == maxX || z == minZ || z == maxZ;
    }

    protected static boolean canCarveBranch(ChunkPos pos, double x, double z, int branchIndex, int branchCount, float baseWidth) {
        double i;
        double h;
        double e;
        double g;
        double d = pos.getCenterX();
        double f = x - d;
        return f * f + (g = z - (e = (double)pos.getCenterZ())) * g - (h = (double)(branchCount - branchIndex)) * h <= (i = (double)(baseWidth + 2.0f + 16.0f)) * i;
    }

    private static boolean isDebug(CarverConfig config) {
        return config.debugConfig.isDebugMode();
    }

    public static interface SkipPredicate {
        public boolean shouldSkip(CarverContext var1, double var2, double var4, double var6, int var8);
    }
}

