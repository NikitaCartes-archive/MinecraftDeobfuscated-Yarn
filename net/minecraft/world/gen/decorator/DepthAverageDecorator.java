/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class DepthAverageDecorator
extends SimpleDecorator<CountDepthDecoratorConfig> {
    public DepthAverageDecorator(Codec<CountDepthDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, CountDepthDecoratorConfig countDepthDecoratorConfig, BlockPos blockPos) {
        int i = countDepthDecoratorConfig.count;
        int j = countDepthDecoratorConfig.spread;
        int k = blockPos.getX();
        int l = blockPos.getZ();
        int m = random.nextInt(j) + random.nextInt(j) - j + i;
        return Stream.of(new BlockPos(k, m, l));
    }
}

