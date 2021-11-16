/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.decorator.PlacementModifier;
import net.minecraft.world.gen.feature.PileConfiguredFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.feature.VegetationConfiguredFeatures;

public class class_6825 {
    public static final PlacedFeature field_36195 = PlacedFeatures.register("pile_hay", PileConfiguredFeatures.PILE_HAY.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature field_36196 = PlacedFeatures.register("pile_melon", PileConfiguredFeatures.PILE_MELON.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature field_36197 = PlacedFeatures.register("pile_snow", PileConfiguredFeatures.PILE_SNOW.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature field_36198 = PlacedFeatures.register("pile_ice", PileConfiguredFeatures.PILE_ICE.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature field_36199 = PlacedFeatures.register("pile_pumpkin", PileConfiguredFeatures.PILE_PUMPKIN.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature field_36200 = PlacedFeatures.register("oak", TreeConfiguredFeatures.OAK.withWouldSurviveFilter(Blocks.OAK_SAPLING));
    public static final PlacedFeature field_36201 = PlacedFeatures.register("acacia", TreeConfiguredFeatures.ACACIA.withWouldSurviveFilter(Blocks.ACACIA_SAPLING));
    public static final PlacedFeature field_36202 = PlacedFeatures.register("spruce", TreeConfiguredFeatures.SPRUCE.withWouldSurviveFilter(Blocks.SPRUCE_SAPLING));
    public static final PlacedFeature field_36203 = PlacedFeatures.register("pine", TreeConfiguredFeatures.PINE.withWouldSurviveFilter(Blocks.SPRUCE_SAPLING));
    public static final PlacedFeature field_36204 = PlacedFeatures.register("patch_cactus", VegetationConfiguredFeatures.PATCH_CACTUS.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature field_36205 = PlacedFeatures.register("flower_plain", VegetationConfiguredFeatures.FLOWER_PLAIN.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature field_36206 = PlacedFeatures.register("patch_taiga_grass", VegetationConfiguredFeatures.PATCH_TAIGA_GRASS.withPlacement(new PlacementModifier[0]));
    public static final PlacedFeature field_36207 = PlacedFeatures.register("patch_berry_bush", VegetationConfiguredFeatures.PATCH_BERRY_BUSH.withPlacement(new PlacementModifier[0]));
}

