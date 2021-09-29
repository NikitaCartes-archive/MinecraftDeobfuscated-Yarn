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
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;

public class DecoratedDecorator
extends Decorator<DecoratedDecoratorConfig> {
    public DecoratedDecorator(Codec<DecoratedDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, DecoratedDecoratorConfig decoratedDecoratorConfig, BlockPos blockPos2) {
        return decoratedDecoratorConfig.getOuter().getPositions(decoratorContext, random, blockPos2).flatMap(blockPos -> decoratedDecoratorConfig.getInner().getPositions(decoratorContext, random, (BlockPos)blockPos));
    }

    @Override
    public /* synthetic */ Stream getPositions(DecoratorContext context, Random random, DecoratorConfig config, BlockPos pos) {
        return this.getPositions(context, random, (DecoratedDecoratorConfig)config, pos);
    }
}

