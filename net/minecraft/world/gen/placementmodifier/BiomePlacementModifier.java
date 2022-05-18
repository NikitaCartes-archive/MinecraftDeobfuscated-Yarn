/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class BiomePlacementModifier
extends AbstractConditionalPlacementModifier {
    private static final BiomePlacementModifier INSTANCE = new BiomePlacementModifier();
    public static Codec<BiomePlacementModifier> MODIFIER_CODEC = Codec.unit(() -> INSTANCE);

    private BiomePlacementModifier() {
    }

    public static BiomePlacementModifier of() {
        return INSTANCE;
    }

    @Override
    protected boolean shouldPlace(FeaturePlacementContext context, Random random, BlockPos pos) {
        PlacedFeature placedFeature = context.getPlacedFeature().orElseThrow(() -> new IllegalStateException("Tried to biome check an unregistered feature, or a feature that should not restrict the biome"));
        RegistryEntry<Biome> registryEntry = context.getWorld().getBiome(pos);
        return context.getChunkGenerator().method_44216(registryEntry).isFeatureAllowed(placedFeature);
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.BIOME;
    }
}

