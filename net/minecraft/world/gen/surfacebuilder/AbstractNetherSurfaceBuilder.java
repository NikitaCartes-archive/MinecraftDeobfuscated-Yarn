/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.class_6557;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public abstract class AbstractNetherSurfaceBuilder
extends SurfaceBuilder<TernarySurfaceConfig> {
    private long seed;
    private ImmutableMap<BlockState, OctavePerlinNoiseSampler> surfaceNoises = ImmutableMap.of();
    private ImmutableMap<BlockState, OctavePerlinNoiseSampler> underLavaNoises = ImmutableMap.of();
    private OctavePerlinNoiseSampler shoreNoise;

    public AbstractNetherSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, class_6557 arg, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, int m, long n, TernarySurfaceConfig ternarySurfaceConfig) {
        int o = l + 1;
        int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        double e = 0.03125;
        boolean bl = this.shoreNoise.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) * 75.0 + random.nextDouble() > 0.0;
        BlockState blockState3 = (BlockState)this.underLavaNoises.entrySet().stream().max(Comparator.comparing(entry -> ((OctavePerlinNoiseSampler)entry.getValue()).sample(i, l, j))).get().getKey();
        BlockState blockState4 = (BlockState)this.surfaceNoises.entrySet().stream().max(Comparator.comparing(entry -> ((OctavePerlinNoiseSampler)entry.getValue()).sample(i, l, j))).get().getKey();
        BlockState blockState5 = arg.getState(128);
        for (int r = 127; r >= m; --r) {
            int s;
            BlockState blockState6 = arg.getState(r);
            if (blockState5.isOf(blockState.getBlock()) && (blockState6.isAir() || blockState6 == blockState2)) {
                for (s = 0; s < p && arg.getState(r + s).isOf(blockState.getBlock()); ++s) {
                    arg.method_38092(r + s, blockState3);
                }
            }
            if ((blockState5.isAir() || blockState5 == blockState2) && blockState6.isOf(blockState.getBlock())) {
                for (s = 0; s < q && arg.getState(r - s).isOf(blockState.getBlock()); ++s) {
                    if (bl && r >= o - 4 && r <= o + 1) {
                        arg.method_38092(r - s, this.getLavaShoreState());
                        continue;
                    }
                    arg.method_38092(r - s, blockState4);
                }
            }
            blockState5 = blockState6;
        }
    }

    @Override
    public void initSeed(long seed) {
        if (this.seed != seed || this.shoreNoise == null || this.surfaceNoises.isEmpty() || this.underLavaNoises.isEmpty()) {
            this.surfaceNoises = AbstractNetherSurfaceBuilder.createNoisesForStates(this.getSurfaceStates(), seed);
            this.underLavaNoises = AbstractNetherSurfaceBuilder.createNoisesForStates(this.getUnderLavaStates(), seed + (long)this.surfaceNoises.size());
            this.shoreNoise = new OctavePerlinNoiseSampler((AbstractRandom)new ChunkRandom(seed + (long)this.surfaceNoises.size() + (long)this.underLavaNoises.size()), ImmutableList.of(Integer.valueOf(0)));
        }
        this.seed = seed;
    }

    private static ImmutableMap<BlockState, OctavePerlinNoiseSampler> createNoisesForStates(ImmutableList<BlockState> states, long seed) {
        ImmutableMap.Builder<BlockState, OctavePerlinNoiseSampler> builder = new ImmutableMap.Builder<BlockState, OctavePerlinNoiseSampler>();
        for (BlockState blockState : states) {
            builder.put(blockState, new OctavePerlinNoiseSampler((AbstractRandom)new ChunkRandom(seed), ImmutableList.of(Integer.valueOf(-4))));
            ++seed;
        }
        return builder.build();
    }

    protected abstract ImmutableList<BlockState> getSurfaceStates();

    protected abstract ImmutableList<BlockState> getUnderLavaStates();

    /**
     * Returns the state that will make up the boundary between the land and the lava ocean.
     */
    protected abstract BlockState getLavaShoreState();
}

