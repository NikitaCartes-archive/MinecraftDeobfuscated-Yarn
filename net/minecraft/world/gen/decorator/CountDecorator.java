/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class CountDecorator
extends SimpleDecorator<CountConfig> {
    public CountDecorator(Codec<CountConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, CountConfig countConfig, BlockPos blockPos) {
        return IntStream.range(0, countConfig.method_30396().getValue(random)).mapToObj(i -> blockPos);
    }
}

