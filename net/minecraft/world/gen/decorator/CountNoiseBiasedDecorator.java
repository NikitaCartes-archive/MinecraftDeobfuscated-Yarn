/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.decorator.CountNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class CountNoiseBiasedDecorator
extends SimpleDecorator<CountNoiseBiasedDecoratorConfig> {
    public CountNoiseBiasedDecorator(Codec<CountNoiseBiasedDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, CountNoiseBiasedDecoratorConfig countNoiseBiasedDecoratorConfig, BlockPos blockPos) {
        double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / countNoiseBiasedDecoratorConfig.noiseFactor, (double)blockPos.getZ() / countNoiseBiasedDecoratorConfig.noiseFactor, false);
        int i2 = (int)Math.ceil((d + countNoiseBiasedDecoratorConfig.noiseOffset) * (double)countNoiseBiasedDecoratorConfig.noiseToCountRatio);
        return IntStream.range(0, i2).mapToObj(i -> blockPos);
    }
}

