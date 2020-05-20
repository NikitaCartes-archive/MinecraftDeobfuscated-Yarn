/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.SimpleDecorator;

public class ChancePassthroughDecorator
extends SimpleDecorator<ChanceDecoratorConfig> {
    public ChancePassthroughDecorator(Codec<ChanceDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos) {
        if (random.nextFloat() < 1.0f / (float)chanceDecoratorConfig.chance) {
            return Stream.of(blockPos);
        }
        return Stream.empty();
    }
}

