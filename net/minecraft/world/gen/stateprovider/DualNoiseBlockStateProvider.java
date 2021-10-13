/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.stateprovider.NoiseBlockStateProvider;

public class DualNoiseBlockStateProvider
extends NoiseBlockStateProvider {
    public static final Codec<DualNoiseBlockStateProvider> DUAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Range.createRangedCodec(Codec.INT, 1, 64).fieldOf("variety")).forGetter(dualNoiseBlockStateProvider -> dualNoiseBlockStateProvider.variety), ((MapCodec)DoublePerlinNoiseSampler.NoiseParameters.CODEC.fieldOf("slow_noise")).forGetter(dualNoiseBlockStateProvider -> dualNoiseBlockStateProvider.slowNoiseParameters), ((MapCodec)Codecs.POSITIVE_FLOAT.fieldOf("slow_scale")).forGetter(dualNoiseBlockStateProvider -> Float.valueOf(dualNoiseBlockStateProvider.slowScale))).and(DualNoiseBlockStateProvider.fillNoiseCodecFields(instance)).apply(instance, DualNoiseBlockStateProvider::new));
    private final Range<Integer> variety;
    private final DoublePerlinNoiseSampler.NoiseParameters slowNoiseParameters;
    private final float slowScale;
    private final DoublePerlinNoiseSampler slowNoiseSampler;

    public DualNoiseBlockStateProvider(Range<Integer> variety, DoublePerlinNoiseSampler.NoiseParameters slowNoiseParameters, float slowScale, long seed, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float scale, List<BlockState> states) {
        super(seed, noiseParameters, scale, states);
        this.variety = variety;
        this.slowNoiseParameters = slowNoiseParameters;
        this.slowScale = slowScale;
        this.slowNoiseSampler = DoublePerlinNoiseSampler.method_39123(new ChunkRandom(new AtomicSimpleRandom(seed)), slowNoiseParameters);
    }

    @Override
    protected BlockStateProviderType<?> getType() {
        return BlockStateProviderType.DUAL_NOISE_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos pos) {
        double d = this.getSlowNoiseValue(pos);
        int i = (int)MathHelper.clampedLerpFromProgress(d, -1.0, 1.0, (double)this.variety.minInclusive().intValue(), (double)(this.variety.maxInclusive() + 1));
        ArrayList<BlockState> list = Lists.newArrayListWithCapacity(i);
        for (int j = 0; j < i; ++j) {
            list.add(this.getStateAtValue(this.states, this.getSlowNoiseValue(pos.add(j * 54545, 0, j * 34234))));
        }
        return this.getStateFromList(list, pos, this.scale);
    }

    protected double getSlowNoiseValue(BlockPos pos) {
        return this.slowNoiseSampler.sample((float)pos.getX() * this.slowScale, (float)pos.getY() * this.slowScale, (float)pos.getZ() * this.slowScale);
    }
}

