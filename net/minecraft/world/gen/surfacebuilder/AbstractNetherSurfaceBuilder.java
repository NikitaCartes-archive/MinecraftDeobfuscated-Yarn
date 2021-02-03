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
import net.minecraft.class_5819;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
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
    public void generate(Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, TernarySurfaceConfig ternarySurfaceConfig) {
        int n = l + 1;
        int o = i & 0xF;
        int p = j & 0xF;
        int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int r = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        double e = 0.03125;
        boolean bl = this.shoreNoise.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) * 75.0 + random.nextDouble() > 0.0;
        BlockState blockState3 = (BlockState)this.underLavaNoises.entrySet().stream().max(Comparator.comparing(entry -> ((OctavePerlinNoiseSampler)entry.getValue()).sample(i, l, j))).get().getKey();
        BlockState blockState4 = (BlockState)this.surfaceNoises.entrySet().stream().max(Comparator.comparing(entry -> ((OctavePerlinNoiseSampler)entry.getValue()).sample(i, l, j))).get().getKey();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockState blockState5 = chunk.getBlockState(mutable.set(o, 128, p));
        for (int s = 127; s >= 0; --s) {
            int t;
            mutable.set(o, s, p);
            BlockState blockState6 = chunk.getBlockState(mutable);
            if (blockState5.isOf(blockState.getBlock()) && (blockState6.isAir() || blockState6 == blockState2)) {
                for (t = 0; t < q; ++t) {
                    mutable.move(Direction.UP);
                    if (!chunk.getBlockState(mutable).isOf(blockState.getBlock())) break;
                    chunk.setBlockState(mutable, blockState3, false);
                }
                mutable.set(o, s, p);
            }
            if ((blockState5.isAir() || blockState5 == blockState2) && blockState6.isOf(blockState.getBlock())) {
                for (t = 0; t < r && chunk.getBlockState(mutable).isOf(blockState.getBlock()); ++t) {
                    if (bl && s >= n - 4 && s <= n + 1) {
                        chunk.setBlockState(mutable, this.getLavaShoreState(), false);
                    } else {
                        chunk.setBlockState(mutable, blockState4, false);
                    }
                    mutable.move(Direction.DOWN);
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
            this.shoreNoise = new OctavePerlinNoiseSampler((class_5819)new ChunkRandom(seed + (long)this.surfaceNoises.size() + (long)this.underLavaNoises.size()), ImmutableList.of(Integer.valueOf(0)));
        }
        this.seed = seed;
    }

    private static ImmutableMap<BlockState, OctavePerlinNoiseSampler> createNoisesForStates(ImmutableList<BlockState> states, long seed) {
        ImmutableMap.Builder<BlockState, OctavePerlinNoiseSampler> builder = new ImmutableMap.Builder<BlockState, OctavePerlinNoiseSampler>();
        for (BlockState blockState : states) {
            builder.put(blockState, new OctavePerlinNoiseSampler((class_5819)new ChunkRandom(seed), ImmutableList.of(Integer.valueOf(-4))));
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

