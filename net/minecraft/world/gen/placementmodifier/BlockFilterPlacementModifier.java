/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class BlockFilterPlacementModifier
extends AbstractConditionalPlacementModifier {
    public static final Codec<BlockFilterPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockPredicate.BASE_CODEC.fieldOf("predicate")).forGetter(blockFilterPlacementModifier -> blockFilterPlacementModifier.predicate)).apply((Applicative<BlockFilterPlacementModifier, ?>)instance, BlockFilterPlacementModifier::new));
    private final BlockPredicate predicate;

    private BlockFilterPlacementModifier(BlockPredicate predicate) {
        this.predicate = predicate;
    }

    public static BlockFilterPlacementModifier of(BlockPredicate predicate) {
        return new BlockFilterPlacementModifier(predicate);
    }

    @Override
    protected boolean shouldPlace(FeaturePlacementContext context, Random random, BlockPos pos) {
        return this.predicate.test(context.getWorld(), pos);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.BLOCK_PREDICATE_FILTER;
    }
}

