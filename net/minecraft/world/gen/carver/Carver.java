/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CaveCarver;
import net.minecraft.world.gen.carver.NetherCaveCarver;
import net.minecraft.world.gen.carver.RavineCarver;
import net.minecraft.world.gen.carver.UnderwaterCaveCarver;
import net.minecraft.world.gen.carver.UnderwaterRavineCarver;

public abstract class Carver<C extends CarverConfig> {
    public static final Carver<ProbabilityConfig> CAVE = Carver.register("cave", new CaveCarver((Function<Dynamic<?>, ? extends ProbabilityConfig>)((Function<Dynamic<?>, ProbabilityConfig>)ProbabilityConfig::deserialize), 256));
    public static final Carver<ProbabilityConfig> HELL_CAVE = Carver.register("hell_cave", new NetherCaveCarver(ProbabilityConfig::deserialize));
    public static final Carver<ProbabilityConfig> CANYON = Carver.register("canyon", new RavineCarver(ProbabilityConfig::deserialize));
    public static final Carver<ProbabilityConfig> UNDERWATER_CANYON = Carver.register("underwater_canyon", new UnderwaterRavineCarver(ProbabilityConfig::deserialize));
    public static final Carver<ProbabilityConfig> UNDERWATER_CAVE = Carver.register("underwater_cave", new UnderwaterCaveCarver(ProbabilityConfig::deserialize));
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    protected static final FluidState WATER = Fluids.WATER.getDefaultState();
    protected static final FluidState LAVA = Fluids.LAVA.getDefaultState();
    protected Set<Block> alwaysCarvableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE});
    protected Set<Fluid> carvableFluids = ImmutableSet.of(Fluids.WATER);
    private final Function<Dynamic<?>, ? extends C> configDeserializer;
    protected final int heightLimit;

    private static <C extends CarverConfig, F extends Carver<C>> F register(String string, F carver) {
        return (F)Registry.register(Registry.CARVER, string, carver);
    }

    public Carver(Function<Dynamic<?>, ? extends C> function, int i) {
        this.configDeserializer = function;
        this.heightLimit = i;
    }

    public int getBranchFactor() {
        return 4;
    }

    protected boolean carveRegion(Chunk chunk, long l, int i, int j, int k, double d, double e, double f, double g, double h, BitSet bitSet) {
        int t;
        int s;
        int r;
        int q;
        int p;
        Random random = new Random(l + (long)j + (long)k);
        double m = j * 16 + 8;
        double n = k * 16 + 8;
        if (d < m - 16.0 - g * 2.0 || f < n - 16.0 - g * 2.0 || d > m + 16.0 + g * 2.0 || f > n + 16.0 + g * 2.0) {
            return false;
        }
        int o = Math.max(MathHelper.floor(d - g) - j * 16 - 1, 0);
        if (this.isRegionUncarvable(chunk, j, k, o, p = Math.min(MathHelper.floor(d + g) - j * 16 + 1, 16), q = Math.max(MathHelper.floor(e - h) - 1, 1), r = Math.min(MathHelper.floor(e + h) + 1, this.heightLimit - 8), s = Math.max(MathHelper.floor(f - g) - k * 16 - 1, 0), t = Math.min(MathHelper.floor(f + g) - k * 16 + 1, 16))) {
            return false;
        }
        boolean bl = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        BlockPos.Mutable mutable3 = new BlockPos.Mutable();
        for (int u = o; u < p; ++u) {
            int v = u + j * 16;
            double w = ((double)v + 0.5 - d) / g;
            for (int x = s; x < t; ++x) {
                int y = x + k * 16;
                double z = ((double)y + 0.5 - f) / g;
                if (w * w + z * z >= 1.0) continue;
                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                for (int aa = r; aa > q; --aa) {
                    double ab = ((double)aa - 0.5 - e) / h;
                    if (this.isPositionExcluded(w, ab, z, aa)) continue;
                    bl |= this.carveAtPoint(chunk, bitSet, random, mutable, mutable2, mutable3, i, j, k, v, y, u, aa, x, atomicBoolean);
                }
            }
        }
        return bl;
    }

    protected boolean carveAtPoint(Chunk chunk, BitSet bitSet, Random random, BlockPos.Mutable mutable, BlockPos.Mutable mutable2, BlockPos.Mutable mutable3, int i, int j, int k, int l, int m, int n, int o, int p, AtomicBoolean atomicBoolean) {
        int q = n | p << 4 | o << 8;
        if (bitSet.get(q)) {
            return false;
        }
        bitSet.set(q);
        mutable.set(l, o, m);
        BlockState blockState = chunk.getBlockState(mutable);
        BlockState blockState2 = chunk.getBlockState(mutable2.set(mutable).setOffset(Direction.UP));
        if (blockState.getBlock() == Blocks.GRASS_BLOCK || blockState.getBlock() == Blocks.MYCELIUM) {
            atomicBoolean.set(true);
        }
        if (!this.canCarveBlock(blockState, blockState2)) {
            return false;
        }
        if (o < 11) {
            chunk.setBlockState(mutable, LAVA.getBlockState(), false);
        } else {
            chunk.setBlockState(mutable, CAVE_AIR, false);
            if (atomicBoolean.get()) {
                mutable3.set(mutable).setOffset(Direction.DOWN);
                if (chunk.getBlockState(mutable3).getBlock() == Blocks.DIRT) {
                    chunk.setBlockState(mutable3, chunk.getBiome(mutable).getSurfaceConfig().getTopMaterial(), false);
                }
            }
        }
        return true;
    }

    public abstract boolean carve(Chunk var1, Random var2, int var3, int var4, int var5, int var6, int var7, BitSet var8, C var9);

    public abstract boolean shouldCarve(Random var1, int var2, int var3, C var4);

    protected boolean canAlwaysCarveBlock(BlockState blockState) {
        return this.alwaysCarvableBlocks.contains(blockState.getBlock());
    }

    protected boolean canCarveBlock(BlockState blockState, BlockState blockState2) {
        Block block = blockState.getBlock();
        return this.canAlwaysCarveBlock(blockState) || (block == Blocks.SAND || block == Blocks.GRAVEL) && !blockState2.getFluidState().matches(FluidTags.WATER);
    }

    protected boolean isRegionUncarvable(Chunk chunk, int i, int j, int k, int l, int m, int n, int o, int p) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int q = k; q < l; ++q) {
            for (int r = o; r < p; ++r) {
                for (int s = m - 1; s <= n + 1; ++s) {
                    if (this.carvableFluids.contains(chunk.getFluidState(mutable.set(q + i * 16, s, r + j * 16)).getFluid())) {
                        return true;
                    }
                    if (s == n + 1 || this.isOnBoundary(k, l, o, p, q, r)) continue;
                    s = n;
                }
            }
        }
        return false;
    }

    private boolean isOnBoundary(int i, int j, int k, int l, int m, int n) {
        return m == i || m == j - 1 || n == k || n == l - 1;
    }

    protected boolean canCarveBranch(int i, int j, double d, double e, int k, int l, float f) {
        double g = i * 16 + 8;
        double m = d - g;
        double h = j * 16 + 8;
        double n = e - h;
        double o = l - k;
        double p = f + 2.0f + 16.0f;
        return m * m + n * n - o * o <= p * p;
    }

    protected abstract boolean isPositionExcluded(double var1, double var3, double var5, int var7);
}

