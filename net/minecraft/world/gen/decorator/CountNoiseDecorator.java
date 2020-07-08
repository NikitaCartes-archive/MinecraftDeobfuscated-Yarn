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
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;

public class CountNoiseDecorator
extends Decorator<NoiseHeightmapDecoratorConfig> {
    public CountNoiseDecorator(Codec<NoiseHeightmapDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, NoiseHeightmapDecoratorConfig noiseHeightmapDecoratorConfig, BlockPos blockPos) {
        double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0, false);
        int i2 = d < noiseHeightmapDecoratorConfig.noiseLevel ? noiseHeightmapDecoratorConfig.belowNoise : noiseHeightmapDecoratorConfig.aboveNoise;
        return IntStream.range(0, i2).mapToObj(i -> blockPos);
    }
}

