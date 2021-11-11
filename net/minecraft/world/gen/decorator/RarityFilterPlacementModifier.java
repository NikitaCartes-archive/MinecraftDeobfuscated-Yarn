/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.PlacementModifierType;

public class RarityFilterPlacementModifier
extends AbstractConditionalPlacementModifier {
    public static final Codec<RarityFilterPlacementModifier> MODIFIER_CODEC = ((MapCodec)Codecs.POSITIVE_INT.fieldOf("chance")).xmap(RarityFilterPlacementModifier::new, rarityFilterPlacementModifier -> rarityFilterPlacementModifier.chance).codec();
    private final int chance;

    private RarityFilterPlacementModifier(int chance) {
        this.chance = chance;
    }

    public static RarityFilterPlacementModifier of(int chance) {
        return new RarityFilterPlacementModifier(chance);
    }

    @Override
    protected boolean shouldPlace(DecoratorContext context, Random random, BlockPos pos) {
        return random.nextFloat() < 1.0f / (float)this.chance;
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.RARITY_FILTER;
    }
}

