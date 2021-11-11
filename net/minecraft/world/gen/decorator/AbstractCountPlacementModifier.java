/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.PlacementModifier;

public abstract class AbstractCountPlacementModifier
extends PlacementModifier {
    protected abstract int getCount(Random var1, BlockPos var2);

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random random, BlockPos pos) {
        return IntStream.range(0, this.getCount(random, pos)).mapToObj(i -> pos);
    }
}

