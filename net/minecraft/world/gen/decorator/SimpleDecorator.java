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

public abstract class SimpleDecorator<DC extends DecoratorConfig>
extends Decorator<DC> {
    public SimpleDecorator(Codec<DC> codec) {
        super(codec);
    }

    @Override
    public final Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos) {
        return this.getPositions(random, config, pos);
    }

    protected abstract Stream<BlockPos> getPositions(Random var1, DC var2, BlockPos var3);
}

