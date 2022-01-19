/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.PileConfiguredFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public class VillagePlacedFeatures {
    public static final PlacedFeature PILE_HAY = PlacedFeatures.register("pile_hay", PileConfiguredFeatures.PILE_HAY.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature PILE_MELON = PlacedFeatures.register("pile_melon", PileConfiguredFeatures.PILE_MELON.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature PILE_SNOW = PlacedFeatures.register("pile_snow", PileConfiguredFeatures.PILE_SNOW.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature PILE_ICE = PlacedFeatures.register("pile_ice", PileConfiguredFeatures.PILE_ICE.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature PILE_PUMPKIN = PlacedFeatures.register("pile_pumpkin", PileConfiguredFeatures.PILE_PUMPKIN.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature OAK = PlacedFeatures.register("oak", TreeConfiguredFeatures.OAK.withWouldSurviveFilter(Blocks.OAK_SAPLING));
    public static final PlacedFeature ACACIA = PlacedFeatures.register("acacia", TreeConfiguredFeatures.ACACIA.withWouldSurviveFilter(Blocks.ACACIA_SAPLING));
    public static final PlacedFeature SPRUCE = PlacedFeatures.register("spruce", TreeConfiguredFeatures.SPRUCE.withWouldSurviveFilter(Blocks.SPRUCE_SAPLING));
    public static final PlacedFeature PINE = PlacedFeatures.register("pine", TreeConfiguredFeatures.PINE.withWouldSurviveFilter(Blocks.SPRUCE_SAPLING));
    public static final PlacedFeature PATCH_CACTUS = PlacedFeatures.register("patch_cactus", VegetationConfiguredFeatures.PATCH_CACTUS.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature FLOWER_PLAIN = PlacedFeatures.register("flower_plain", VegetationConfiguredFeatures.FLOWER_PLAIN.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature PATCH_TAIGA_GRASS = PlacedFeatures.register("patch_taiga_grass", VegetationConfiguredFeatures.PATCH_TAIGA_GRASS.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature PATCH_BERRY_BUSH = PlacedFeatures.register("patch_berry_bush", VegetationConfiguredFeatures.PATCH_BERRY_BUSH.withPlacement(new PlacementModifier[0]));
}

