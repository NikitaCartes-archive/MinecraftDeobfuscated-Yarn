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
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class CountBiasedRangeDecorator
extends SimpleDecorator<RangeDecoratorConfig> {
    public CountBiasedRangeDecorator(Function<Dynamic<?>, ? extends RangeDecoratorConfig> function) {
        super(function);
    }

    public Stream<BlockPos> method_15897(Random random, RangeDecoratorConfig rangeDecoratorConfig, BlockPos blockPos) {
        return IntStream.range(0, rangeDecoratorConfig.count).mapToObj(i -> {
            int j = random.nextInt(16) + blockPos.getX();
            int k = random.nextInt(16) + blockPos.getZ();
            int l = random.nextInt(random.nextInt(rangeDecoratorConfig.maximum - rangeDecoratorConfig.topOffset) + rangeDecoratorConfig.bottomOffset);
            return new BlockPos(j, l, k);
        });
    }
}

