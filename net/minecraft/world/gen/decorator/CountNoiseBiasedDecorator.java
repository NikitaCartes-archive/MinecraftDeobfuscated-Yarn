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
import net.minecraft.world.gen.decorator.SimpleDecorator;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;

public class CountNoiseBiasedDecorator
extends SimpleDecorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> {
    public CountNoiseBiasedDecorator(Codec<TopSolidHeightmapNoiseBiasedDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, TopSolidHeightmapNoiseBiasedDecoratorConfig topSolidHeightmapNoiseBiasedDecoratorConfig, BlockPos blockPos) {
        double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / topSolidHeightmapNoiseBiasedDecoratorConfig.noiseFactor, (double)blockPos.getZ() / topSolidHeightmapNoiseBiasedDecoratorConfig.noiseFactor, false);
        int i2 = (int)Math.ceil((d + topSolidHeightmapNoiseBiasedDecoratorConfig.noiseOffset) * (double)topSolidHeightmapNoiseBiasedDecoratorConfig.noiseToCountRatio);
        return IntStream.range(0, i2).mapToObj(i -> blockPos);
    }
}

