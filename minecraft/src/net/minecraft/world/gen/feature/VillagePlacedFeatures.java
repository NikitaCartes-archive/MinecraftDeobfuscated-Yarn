package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.util.registry.RegistryEntry;

public class VillagePlacedFeatures {
	public static final RegistryEntry<PlacedFeature> PILE_HAY = PlacedFeatures.register("pile_hay", PileConfiguredFeatures.PILE_HAY);
	public static final RegistryEntry<PlacedFeature> PILE_MELON = PlacedFeatures.register("pile_melon", PileConfiguredFeatures.PILE_MELON);
	public static final RegistryEntry<PlacedFeature> PILE_SNOW = PlacedFeatures.register("pile_snow", PileConfiguredFeatures.PILE_SNOW);
	public static final RegistryEntry<PlacedFeature> PILE_ICE = PlacedFeatures.register("pile_ice", PileConfiguredFeatures.PILE_ICE);
	public static final RegistryEntry<PlacedFeature> PILE_PUMPKIN = PlacedFeatures.register("pile_pumpkin", PileConfiguredFeatures.PILE_PUMPKIN);
	public static final RegistryEntry<PlacedFeature> OAK = PlacedFeatures.register(
		"oak", TreeConfiguredFeatures.OAK, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> ACACIA = PlacedFeatures.register(
		"acacia", TreeConfiguredFeatures.ACACIA, PlacedFeatures.wouldSurvive(Blocks.ACACIA_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> SPRUCE = PlacedFeatures.register(
		"spruce", TreeConfiguredFeatures.SPRUCE, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> PINE = PlacedFeatures.register(
		"pine", TreeConfiguredFeatures.PINE, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> PATCH_CACTUS = PlacedFeatures.register("patch_cactus", VegetationConfiguredFeatures.PATCH_CACTUS);
	public static final RegistryEntry<PlacedFeature> FLOWER_PLAIN = PlacedFeatures.register("flower_plain", VegetationConfiguredFeatures.FLOWER_PLAIN);
	public static final RegistryEntry<PlacedFeature> PATCH_TAIGA_GRASS = PlacedFeatures.register(
		"patch_taiga_grass", VegetationConfiguredFeatures.PATCH_TAIGA_GRASS
	);
	public static final RegistryEntry<PlacedFeature> PATCH_BERRY_BUSH = PlacedFeatures.register("patch_berry_bush", VegetationConfiguredFeatures.PATCH_BERRY_BUSH);
}
