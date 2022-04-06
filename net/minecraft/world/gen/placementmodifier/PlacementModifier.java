/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

/**
 * A placement modifier is a one-to-many position transformation, which
 * takes a position with some context and returns zero or more positions.
 * It's used to determine where to generate configured features.
 * 
 * @see net.minecraft.world.gen.feature.ConfiguredFeature
 */
public abstract class PlacementModifier {
    public static final Codec<PlacementModifier> CODEC = Registry.PLACEMENT_MODIFIER_TYPE.getCodec().dispatch(PlacementModifier::getType, PlacementModifierType::codec);

    /**
     * Applies this placement modifier to the given position.
     */
    public abstract Stream<BlockPos> getPositions(FeaturePlacementContext var1, AbstractRandom var2, BlockPos var3);

    public abstract PlacementModifierType<?> getType();
}

