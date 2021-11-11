/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.PlacementModifier;

public abstract class AbstractConditionalPlacementModifier
extends PlacementModifier {
    @Override
    public final Stream<BlockPos> getPositions(DecoratorContext context, Random random, BlockPos pos) {
        if (this.shouldPlace(context, random, pos)) {
            return Stream.of(pos);
        }
        return Stream.of(new BlockPos[0]);
    }

    protected abstract boolean shouldPlace(DecoratorContext var1, Random var2, BlockPos var3);
}

