package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;

public class VillagePlacedFeatures {
	public static final PlacedFeature PILE_HAY = PlacedFeatures.register("pile_hay", PileConfiguredFeatures.PILE_HAY.withPlacement());
	public static final PlacedFeature PILE_MELON = PlacedFeatures.register("pile_melon", PileConfiguredFeatures.PILE_MELON.withPlacement());
	public static final PlacedFeature PILE_SNOW = PlacedFeatures.register("pile_snow", PileConfiguredFeatures.PILE_SNOW.withPlacement());
	public static final PlacedFeature PILE_ICE = PlacedFeatures.register("pile_ice", PileConfiguredFeatures.PILE_ICE.withPlacement());
	public static final PlacedFeature PILE_PUMPKIN = PlacedFeatures.register("pile_pumpkin", PileConfiguredFeatures.PILE_PUMPKIN.withPlacement());
	public static final PlacedFeature OAK = PlacedFeatures.register("oak", TreeConfiguredFeatures.OAK.withWouldSurviveFilter(Blocks.OAK_SAPLING));
	public static final PlacedFeature ACACIA = PlacedFeatures.register("acacia", TreeConfiguredFeatures.ACACIA.withWouldSurviveFilter(Blocks.ACACIA_SAPLING));
	public static final PlacedFeature SPRUCE = PlacedFeatures.register("spruce", TreeConfiguredFeatures.SPRUCE.withWouldSurviveFilter(Blocks.SPRUCE_SAPLING));
	public static final PlacedFeature PINE = PlacedFeatures.register("pine", TreeConfiguredFeatures.PINE.withWouldSurviveFilter(Blocks.SPRUCE_SAPLING));
	public static final PlacedFeature PATCH_CACTUS = PlacedFeatures.register("patch_cactus", VegetationConfiguredFeatures.PATCH_CACTUS.withPlacement());
	public static final PlacedFeature FLOWER_PLAIN = PlacedFeatures.register("flower_plain", VegetationConfiguredFeatures.FLOWER_PLAIN.withPlacement());
	public static final PlacedFeature PATCH_TAIGA_GRASS = PlacedFeatures.register(
		"patch_taiga_grass", VegetationConfiguredFeatures.PATCH_TAIGA_GRASS.withPlacement()
	);
	public static final PlacedFeature PATCH_BERRY_BUSH = PlacedFeatures.register("patch_berry_bush", VegetationConfiguredFeatures.PATCH_BERRY_BUSH.withPlacement());
}
