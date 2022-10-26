/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.AbstractNoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class NoiseBlockStateProvider
extends AbstractNoiseBlockStateProvider {
    public static final Codec<NoiseBlockStateProvider> CODEC = RecordCodecBuilder.create(instance -> NoiseBlockStateProvider.fillNoiseCodecFields(instance).apply((Applicative)instance, NoiseBlockStateProvider::new));
    protected final List<BlockState> states;

    protected static <P extends NoiseBlockStateProvider> Products.P4<RecordCodecBuilder.Mu<P>, Long, DoublePerlinNoiseSampler.NoiseParameters, Float, List<BlockState>> fillNoiseCodecFields(RecordCodecBuilder.Instance<P> instance) {
        return NoiseBlockStateProvider.fillCodecFields(instance).and(((MapCodec)Codec.list(BlockState.CODEC).fieldOf("states")).forGetter(noiseBlockStateProvider -> noiseBlockStateProvider.states));
    }

    public NoiseBlockStateProvider(long seed, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float scale, List<BlockState> states) {
        super(seed, noiseParameters, scale);
        this.states = states;
    }

    @Override
    protected BlockStateProviderType<?> getType() {
        return BlockStateProviderType.NOISE_PROVIDER;
    }

    @Override
    public BlockState get(Random random, BlockPos pos) {
        return this.getStateFromList(this.states, pos, this.scale);
    }

    protected BlockState getStateFromList(List<BlockState> states, BlockPos pos, double scale) {
        double d = this.getNoiseValue(pos, scale);
        return this.getStateAtValue(states, d);
    }

    protected BlockState getStateAtValue(List<BlockState> states, double value) {
        double d = MathHelper.clamp((1.0 + value) / 2.0, 0.0, 0.9999);
        return states.get((int)(d * (double)states.size()));
    }
}

