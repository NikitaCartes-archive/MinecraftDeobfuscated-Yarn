/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.carver.CaveCarver;
import net.minecraft.world.gen.carver.CaveCarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.NetherCaveCarver;
import net.minecraft.world.gen.carver.RavineCarver;
import net.minecraft.world.gen.carver.RavineCarverConfig;
import net.minecraft.world.gen.chunk.AquiferSampler;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

public abstract class Carver<C extends CarverConfig> {
    public static final Carver<CaveCarverConfig> CAVE = Carver.register("cave", new CaveCarver(CaveCarverConfig.CAVE_CODEC));
    public static final Carver<CaveCarverConfig> NETHER_CAVE = Carver.register("nether_cave", new NetherCaveCarver(CaveCarverConfig.CAVE_CODEC));
    public static final Carver<RavineCarverConfig> RAVINE = Carver.register("canyon", new RavineCarver(RavineCarverConfig.RAVINE_CODEC));
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    protected static final FluidState WATER = Fluids.WATER.getDefaultState();
    protected static final FluidState LAVA = Fluids.LAVA.getDefaultState();
    protected Set<Block> alwaysCarvableBlocks = ImmutableSet.of(Blocks.WATER, Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, new Block[]{Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE, Blocks.DEEPSLATE, Blocks.CALCITE, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.TUFF, Blocks.GRANITE, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.RAW_IRON_BLOCK, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_COPPER_BLOCK});
    protected Set<Fluid> carvableFluids = ImmutableSet.of(Fluids.WATER);
    private final Codec<ConfiguredCarver<C>> codec;

    private static <C extends CarverConfig, F extends Carver<C>> F register(String name, F carver) {
        return (F)Registry.register(Registry.CARVER, name, carver);
    }

    public Carver(Codec<C> configCodec) {
        this.codec = ((MapCodec)configCodec.fieldOf("config")).xmap(this::configure, ConfiguredCarver::config).codec();
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

    protected boolean carveRegion(CarverContext context, C config, Chunk chunk, Function<BlockPos, RegistryEntry<Biome>> posToBiome, AquiferSampler aquiferSampler, double d, double e, double f, double g, double h, CarvingMask mask, SkipPredicate skipPredicate) {
        ChunkPos chunkPos = chunk.getPos();
        double i = chunkPos.getCenterX();
        double j = chunkPos.getCenterZ();
        double k = 16.0 + g * 2.0;
        if (Math.abs(d - i) > k || Math.abs(f - j) > k) {
            return false;
        }
        int l = chunkPos.getStartX();
        int m = chunkPos.getStartZ();
        int n = Math.max(MathHelper.floor(d - g) - l - 1, 0);
        int o = Math.min(MathHelper.floor(d + g) - l, 15);
        int p = Math.max(MathHelper.floor(e - h) - 1, context.getMinY() + 1);
        int q = chunk.hasBelowZeroRetrogen() ? 0 : 7;
        int r = Math.min(MathHelper.floor(e + h) + 1, context.getMinY() + context.getHeight() - 1 - q);
        int s = Math.max(MathHelper.floor(f - g) - m - 1, 0);
        int t = Math.min(MathHelper.floor(f + g) - m, 15);
        boolean bl = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        for (int u = n; u <= o; ++u) {
            int v = chunkPos.getOffsetX(u);
            double w = ((double)v + 0.5 - d) / g;
            for (int x = s; x <= t; ++x) {
                int y = chunkPos.getOffsetZ(x);
                double z = ((double)y + 0.5 - f) / g;
                if (w * w + z * z >= 1.0) continue;
                MutableBoolean mutableBoolean = new MutableBoolean(false);
                for (int aa = r; aa > p; --aa) {
                    double ab = ((double)aa - 0.5 - e) / h;
                    if (skipPredicate.shouldSkip(context, w, ab, z, aa) || mask.get(u, aa, x) && !Carver.isDebug(config)) continue;
                    mask.set(u, aa, x);
                    mutable.set(v, aa, y);
                    bl |= this.carveAtPoint(context, config, chunk, posToBiome, mask, mutable, mutable2, aquiferSampler, mutableBoolean);
                }
            }
        }
        return bl;
    }

    protected boolean carveAtPoint(CarverContext context, C config, Chunk chunk, Function<BlockPos, RegistryEntry<Biome>> posToBiome, CarvingMask mask, BlockPos.Mutable mutable, BlockPos.Mutable mutable2, AquiferSampler aquiferSampler, MutableBoolean mutableBoolean) {
        BlockState blockState = chunk.getBlockState(mutable);
        if (blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.MYCELIUM)) {
            mutableBoolean.setTrue();
        }
        if (!this.canAlwaysCarveBlock(blockState) && !Carver.isDebug(config)) {
            return false;
        }
        BlockState blockState2 = this.getState(context, config, mutable, aquiferSampler);
        if (blockState2 == null) {
            return false;
        }
        chunk.setBlockState(mutable, blockState2, false);
        if (aquiferSampler.needsFluidTick() && !blockState2.getFluidState().isEmpty()) {
            chunk.markBlockForPostProcessing(mutable);
        }
        if (mutableBoolean.isTrue()) {
            mutable2.set((Vec3i)mutable, Direction.DOWN);
            if (chunk.getBlockState(mutable2).isOf(Blocks.DIRT)) {
                context.applyMaterialRule(posToBiome, chunk, mutable2, !blockState2.getFluidState().isEmpty()).ifPresent(state -> {
                    chunk.setBlockState(mutable2, (BlockState)state, false);
                    if (!state.getFluidState().isEmpty()) {
                        chunk.markBlockForPostProcessing(mutable2);
                    }
                });
            }
        }
        return true;
    }

    @Nullable
    private BlockState getState(CarverContext context, C config, BlockPos pos, AquiferSampler sampler) {
        if (pos.getY() <= ((CarverConfig)config).lavaLevel.getY(context)) {
            return LAVA.getBlockState();
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

    public abstract boolean carve(CarverContext var1, C var2, Chunk var3, Function<BlockPos, RegistryEntry<Biome>> var4, Random var5, AquiferSampler var6, ChunkPos var7, CarvingMask var8);

    public abstract boolean shouldCarve(C var1, Random var2);

    protected boolean canAlwaysCarveBlock(BlockState state) {
        return this.alwaysCarvableBlocks.contains(state.getBlock());
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

