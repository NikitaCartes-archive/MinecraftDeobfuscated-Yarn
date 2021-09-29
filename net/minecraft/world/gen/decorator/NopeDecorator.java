/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class NopeDecorator
extends Decorator<NopeDecoratorConfig> {
    public NopeDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos) {
        return Stream.of(blockPos);
    }

    @Override
    public /* synthetic */ Stream getPositions(DecoratorContext context, Random random, DecoratorConfig config, BlockPos pos) {
        return this.getPositions(context, random, (NopeDecoratorConfig)config, pos);
    }
}

