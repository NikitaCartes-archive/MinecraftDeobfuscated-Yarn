/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.PlacementModifierType;

/**
 * A placement modifier is a one-to-many position transformation, which
 * takes a position with some context and returns zero or more positions.
 * It's used to determine where to generate configured features.
 * 
 * @see net.minecraft.world.gen.feature.ConfiguredFeature#withPlacement
 */
public abstract class PlacementModifier {
    public static final Codec<PlacementModifier> CODEC = Registry.PLACEMENT_MODIFIER_TYPE.getCodec().dispatch(PlacementModifier::getType, PlacementModifierType::codec);

    /**
     * Applies this placement modifier to the given position.
     */
    public abstract Stream<BlockPos> getPositions(DecoratorContext var1, Random var2, BlockPos var3);

    public abstract PlacementModifierType<?> getType();
}

