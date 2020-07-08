/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.DecoratedDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class DecoratedDecorator
extends Decorator<DecoratedDecoratorConfig> {
    public DecoratedDecorator(Codec<DecoratedDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, DecoratedDecoratorConfig decoratedDecoratorConfig, BlockPos blockPos2) {
        return decoratedDecoratorConfig.method_30455().method_30444(decoratorContext, random, blockPos2).flatMap(blockPos -> decoratedDecoratorConfig.method_30457().method_30444(decoratorContext, random, (BlockPos)blockPos));
    }
}

