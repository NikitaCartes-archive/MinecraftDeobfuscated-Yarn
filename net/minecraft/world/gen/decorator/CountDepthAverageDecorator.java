/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class CountDepthAverageDecorator
extends SimpleDecorator<CountDepthDecoratorConfig> {
    public CountDepthAverageDecorator(Function<Dynamic<?>, ? extends CountDepthDecoratorConfig> function) {
        super(function);
    }

    public Stream<BlockPos> method_15907(Random random, CountDepthDecoratorConfig countDepthDecoratorConfig, BlockPos blockPos) {
        int i = countDepthDecoratorConfig.count;
        int j = countDepthDecoratorConfig.baseline;
        int k2 = countDepthDecoratorConfig.spread;
        return IntStream.range(0, i).mapToObj(k -> {
            int l = random.nextInt(16);
            int m = random.nextInt(k2) + random.nextInt(k2) - k2 + j;
            int n = random.nextInt(16);
            return blockPos.add(l, m, n);
        });
    }
}

