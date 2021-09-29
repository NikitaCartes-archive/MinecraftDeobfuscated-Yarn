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

public abstract class ConditionalDecorator<DC extends DecoratorConfig>
extends Decorator<DC> {
    public ConditionalDecorator(Codec<DC> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos) {
        if (this.shouldPlace(context, random, config, pos)) {
            return Stream.of(pos);
        }
        return Stream.of(new BlockPos[0]);
    }

    protected abstract boolean shouldPlace(DecoratorContext var1, Random var2, DC var3, BlockPos var4);
}

