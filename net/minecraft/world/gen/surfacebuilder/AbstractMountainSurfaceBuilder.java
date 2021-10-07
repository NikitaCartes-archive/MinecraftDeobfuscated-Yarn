/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.BlockColumn;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.DefaultSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMountainSurfaceBuilder
extends DefaultSurfaceBuilder {
    private long seed;
    protected DoublePerlinNoiseSampler noiseSampler;

    public AbstractMountainSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, BlockColumn blockColumn, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, int m, long n, TernarySurfaceConfig ternarySurfaceConfig) {
        BlockState blockState4;
        BlockState blockState3;
        if (this.getLayerBlockConfig() != null && this.shouldPlaceSteepSlopeBlock(blockColumn, i, j, this.getLayerBlockConfig())) {
            blockState3 = this.getLayerBlockConfig().getState();
            blockState4 = this.getLayerBlockConfig().getState();
        } else {
            blockState3 = this.getTopMaterial(ternarySurfaceConfig, i, j);
            blockState4 = this.getUnderMaterial(ternarySurfaceConfig, i, j);
        }
        this.generate(random, blockColumn, biome, i, j, k, d, blockState, blockState2, blockState3, blockState4, ternarySurfaceConfig.getUnderwaterMaterial(), l, m);
    }

    protected BlockState getBlockFromNoise(double scale, int x, int z, BlockState outsideRangeState, BlockState insideRangeState, double noiseMin, double noiseMax) {
        double d = this.noiseSampler.sample((double)x * scale, 0.0, (double)z * scale);
        BlockState blockState = d >= noiseMin && d <= noiseMax ? insideRangeState : outsideRangeState;
        return blockState;
    }

    @Override
    public void initSeed(long seed) {
        if (this.seed != seed) {
            ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(seed));
            this.noiseSampler = DoublePerlinNoiseSampler.create((AbstractRandom)chunkRandom, -3, 1.0, 1.0, 1.0, 1.0);
        }
        this.seed = seed;
    }

    public boolean shouldPlaceSteepSlopeBlock(BlockColumn blockColumn, int x, int z, SteepSlopeBlockConfig config) {
        return false;
    }

    @Nullable
    protected abstract SteepSlopeBlockConfig getLayerBlockConfig();

    protected abstract BlockState getTopMaterial(TernarySurfaceConfig var1, int var2, int var3);

    protected abstract BlockState getUnderMaterial(TernarySurfaceConfig var1, int var2, int var3);

    public static class SteepSlopeBlockConfig {
        private final BlockState state;
        private final boolean north;
        private final boolean south;
        private final boolean west;
        private final boolean east;

        public SteepSlopeBlockConfig(BlockState state, boolean north, boolean south, boolean west, boolean east) {
            this.state = state;
            this.north = north;
            this.south = south;
            this.west = west;
            this.east = east;
        }

        public BlockState getState() {
            return this.state;
        }

        public boolean isNorth() {
            return this.north;
        }

        public boolean isSouth() {
            return this.south;
        }

        public boolean isWest() {
            return this.west;
        }

        public boolean isEast() {
            return this.east;
        }
    }
}

