/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;

public abstract class AbstractCountDecorator<DC extends DecoratorConfig>
extends Decorator<DC> {
    public AbstractCountDecorator(Codec<DC> codec) {
        super(codec);
    }

    protected abstract int getCount(Random var1, DC var2, BlockPos var3);

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos) {
        return IntStream.range(0, this.getCount(random, config, pos)).mapToObj(i -> pos);
    }
}

