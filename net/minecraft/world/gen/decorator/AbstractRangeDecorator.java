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

public abstract class AbstractRangeDecorator<DC extends DecoratorConfig>
extends Decorator<DC> {
    public AbstractRangeDecorator(Codec<DC> codec) {
        super(codec);
    }

    protected abstract int getY(DecoratorContext var1, Random var2, DC var3, int var4);

    @Override
    public final Stream<BlockPos> getPositions(DecoratorContext context, Random random, DC config, BlockPos pos) {
        return Stream.of(new BlockPos(pos.getX(), this.getY(context, random, config, pos.getY()), pos.getZ()));
    }
}

