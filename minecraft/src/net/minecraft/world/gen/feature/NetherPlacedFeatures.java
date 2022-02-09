package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountMultilayerPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public class NetherPlacedFeatures {
	public static final RegistryEntry<PlacedFeature> DELTA = PlacedFeatures.register(
		"delta", NetherConfiguredFeatures.DELTA, CountMultilayerPlacementModifier.of(40), BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> SMALL_BASALT_COLUMNS = PlacedFeatures.register(
		"small_basalt_columns", NetherConfiguredFeatures.SMALL_BASALT_COLUMNS, CountMultilayerPlacementModifier.of(4), BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> LARGE_BASALT_COLUMNS = PlacedFeatures.register(
		"large_basalt_columns", NetherConfiguredFeatures.SMALL_BASALT_COLUMNS_TEMP, CountMultilayerPlacementModifier.of(2), BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> BASALT_BLOBS = PlacedFeatures.register(
		"basalt_blobs",
		NetherConfiguredFeatures.BASALT_BLOBS,
		CountPlacementModifier.of(75),
		SquarePlacementModifier.of(),
		PlacedFeatures.BOTTOM_TO_TOP_RANGE,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> BLACKSTONE_BLOBS = PlacedFeatures.register(
		"blackstone_blobs",
		NetherConfiguredFeatures.BLACKSTONE_BLOBS,
		CountPlacementModifier.of(25),
		SquarePlacementModifier.of(),
		PlacedFeatures.BOTTOM_TO_TOP_RANGE,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> GLOWSTONE_EXTRA = PlacedFeatures.register(
		"glowstone_extra",
		NetherConfiguredFeatures.GLOWSTONE_EXTRA,
		CountPlacementModifier.of(BiasedToBottomIntProvider.create(0, 9)),
		SquarePlacementModifier.of(),
		PlacedFeatures.FOUR_ABOVE_AND_BELOW_RANGE,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> GLOWSTONE = PlacedFeatures.register(
		"glowstone",
		NetherConfiguredFeatures.GLOWSTONE_EXTRA,
		CountPlacementModifier.of(10),
		SquarePlacementModifier.of(),
		PlacedFeatures.BOTTOM_TO_TOP_RANGE,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> CRIMSON_FOREST_VEGETATION = PlacedFeatures.register(
		"crimson_forest_vegetation", NetherConfiguredFeatures.CRIMSON_FOREST_VEGETATION, CountMultilayerPlacementModifier.of(6), BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> WARPED_FOREST_VEGETATION = PlacedFeatures.register(
		"warped_forest_vegetation", NetherConfiguredFeatures.WARPED_FOREST_VEGETATION, CountMultilayerPlacementModifier.of(5), BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> NETHER_SPROUTS = PlacedFeatures.register(
		"nether_sprouts", NetherConfiguredFeatures.NETHER_SPROUTS, CountMultilayerPlacementModifier.of(4), BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> TWISTING_VINES = PlacedFeatures.register(
		"twisting_vines",
		NetherConfiguredFeatures.TWISTING_VINES,
		CountPlacementModifier.of(10),
		SquarePlacementModifier.of(),
		PlacedFeatures.BOTTOM_TO_TOP_RANGE,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> WEEPING_VINES = PlacedFeatures.register(
		"weeping_vines",
		NetherConfiguredFeatures.WEEPING_VINES,
		CountPlacementModifier.of(10),
		SquarePlacementModifier.of(),
		PlacedFeatures.BOTTOM_TO_TOP_RANGE,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> PATCH_CRIMSON_ROOTS = PlacedFeatures.register(
		"patch_crimson_roots", NetherConfiguredFeatures.PATCH_CRIMSON_ROOTS, PlacedFeatures.BOTTOM_TO_TOP_RANGE, BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> BASALT_PILLAR = PlacedFeatures.register(
		"basalt_pillar",
		NetherConfiguredFeatures.BASALT_PILLAR,
		CountPlacementModifier.of(10),
		SquarePlacementModifier.of(),
		PlacedFeatures.BOTTOM_TO_TOP_RANGE,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> SPRING_DELTA = PlacedFeatures.register(
		"spring_delta",
		NetherConfiguredFeatures.SPRING_LAVA_NETHER,
		CountPlacementModifier.of(16),
		SquarePlacementModifier.of(),
		PlacedFeatures.FOUR_ABOVE_AND_BELOW_RANGE,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> SPRING_CLOSED = PlacedFeatures.register(
		"spring_closed",
		NetherConfiguredFeatures.SPRING_NETHER_CLOSED,
		CountPlacementModifier.of(16),
		SquarePlacementModifier.of(),
		PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> SPRING_CLOSED_DOUBLE = PlacedFeatures.register(
		"spring_closed_double",
		NetherConfiguredFeatures.SPRING_NETHER_CLOSED,
		CountPlacementModifier.of(32),
		SquarePlacementModifier.of(),
		PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> SPRING_OPEN = PlacedFeatures.register(
		"spring_open",
		NetherConfiguredFeatures.SPRING_NETHER_OPEN,
		CountPlacementModifier.of(8),
		SquarePlacementModifier.of(),
		PlacedFeatures.FOUR_ABOVE_AND_BELOW_RANGE,
		BiomePlacementModifier.of()
	);
	public static final List<PlacementModifier> FIRE_MODIFIERS = List.of(
		CountPlacementModifier.of(UniformIntProvider.create(0, 5)),
		SquarePlacementModifier.of(),
		PlacedFeatures.FOUR_ABOVE_AND_BELOW_RANGE,
		BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> PATCH_SOUL_FIRE = PlacedFeatures.register(
		"patch_soul_fire", NetherConfiguredFeatures.PATCH_SOUL_FIRE, FIRE_MODIFIERS
	);
	public static final RegistryEntry<PlacedFeature> PATCH_FIRE = PlacedFeatures.register("patch_fire", NetherConfiguredFeatures.PATCH_FIRE, FIRE_MODIFIERS);
}
