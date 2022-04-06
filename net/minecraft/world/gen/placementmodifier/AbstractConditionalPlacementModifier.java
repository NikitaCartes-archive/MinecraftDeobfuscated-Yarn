/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placementmodifier;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public abstract class AbstractConditionalPlacementModifier
extends PlacementModifier {
    @Override
    public final Stream<BlockPos> getPositions(FeaturePlacementContext context, AbstractRandom abstractRandom, BlockPos pos) {
        if (this.shouldPlace(context, abstractRandom, pos)) {
            return Stream.of(pos);
        }
        return Stream.of(new BlockPos[0]);
    }

    protected abstract boolean shouldPlace(FeaturePlacementContext var1, AbstractRandom var2, BlockPos var3);
}

