package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountMultilayerPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public class NetherPlacedFeatures {
	public static final RegistryKey<PlacedFeature> DELTA = PlacedFeatures.of("delta");
	public static final RegistryKey<PlacedFeature> POISON_POOL = PlacedFeatures.of("poison_pool");
	public static final RegistryKey<PlacedFeature> SMALL_BASALT_COLUMNS = PlacedFeatures.of("small_basalt_columns");
	public static final RegistryKey<PlacedFeature> SMALL_DEBRIS_COLUMNS = PlacedFeatures.of("small_debris_columns");
	public static final RegistryKey<PlacedFeature> LARGE_BASALT_COLUMNS = PlacedFeatures.of("large_basalt_columns");
	public static final RegistryKey<PlacedFeature> LARGE_POTATO_COLUMNS = PlacedFeatures.of("large_potato_columns");
	public static final RegistryKey<PlacedFeature> BASALT_BLOBS = PlacedFeatures.of("basalt_blobs");
	public static final RegistryKey<PlacedFeature> BLACKSTONE_BLOBS = PlacedFeatures.of("blackstone_blobs");
	public static final RegistryKey<PlacedFeature> GLOWSTONE_EXTRA = PlacedFeatures.of("glowstone_extra");
	public static final RegistryKey<PlacedFeature> GLOWSTONE = PlacedFeatures.of("glowstone");
	public static final RegistryKey<PlacedFeature> CRIMSON_FOREST_VEGETATION = PlacedFeatures.of("crimson_forest_vegetation");
	public static final RegistryKey<PlacedFeature> WARPED_FOREST_VEGETATION = PlacedFeatures.of("warped_forest_vegetation");
	public static final RegistryKey<PlacedFeature> NETHER_SPROUTS = PlacedFeatures.of("nether_sprouts");
	public static final RegistryKey<PlacedFeature> TWISTING_VINES = PlacedFeatures.of("twisting_vines");
	public static final RegistryKey<PlacedFeature> CORRUPTED_BUDS = PlacedFeatures.of("corrupted_buds");
	public static final RegistryKey<PlacedFeature> POTATO_SPROUTS = PlacedFeatures.of("potato_sprouts");
	public static final RegistryKey<PlacedFeature> WEEPING_VINES = PlacedFeatures.of("weeping_vines");
	public static final RegistryKey<PlacedFeature> PATCH_CRIMSON_ROOTS = PlacedFeatures.of("patch_crimson_roots");
	public static final RegistryKey<PlacedFeature> BASALT_PILLAR = PlacedFeatures.of("basalt_pillar");
	public static final RegistryKey<PlacedFeature> SPRING_DELTA = PlacedFeatures.of("spring_delta");
	public static final RegistryKey<PlacedFeature> SPRING_CLOSED = PlacedFeatures.of("spring_closed");
	public static final RegistryKey<PlacedFeature> SPRING_CLOSED_DOUBLE = PlacedFeatures.of("spring_closed_double");
	public static final RegistryKey<PlacedFeature> SPRING_OPEN = PlacedFeatures.of("spring_open");
	public static final RegistryKey<PlacedFeature> PATCH_SOUL_FIRE = PlacedFeatures.of("patch_soul_fire");
	public static final RegistryKey<PlacedFeature> PATCH_FIRE = PlacedFeatures.of("patch_fire");

	public static void bootstrap(Registerable<PlacedFeature> featureRegisterable) {
		RegistryEntryLookup<ConfiguredFeature<?, ?>> registryEntryLookup = featureRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.DELTA);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry2 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.POISON);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry3 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.SMALL_BASALT_COLUMNS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry4 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.SMALL_DEBRIS_COLUMNS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry5 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.SMALL_BASALT_COLUMNS_TEMP);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry6 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.LARGE_POTATO_COLUMNS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry7 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.BASALT_BLOBS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry8 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.BLACKSTONE_BLOBS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry9 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.GLOWSTONE_EXTRA);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry10 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.CRIMSON_FOREST_VEGETATION);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry11 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.WARPED_FOREST_VEGETATION);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry12 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.NETHER_SPROUTS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry13 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.TWISTING_VINES);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry14 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.CORRUPTED_BUDS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry15 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.POTATO_SPROUTS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry16 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.WEEPING_VINES);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry17 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.PATCH_CRIMSON_ROOTS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry18 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.BASALT_PILLAR);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry19 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.SPRING_LAVA_NETHER);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry20 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.SPRING_NETHER_CLOSED);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry21 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.SPRING_NETHER_OPEN);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry22 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.PATCH_SOUL_FIRE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry23 = registryEntryLookup.getOrThrow(NetherConfiguredFeatures.PATCH_FIRE);
		PlacedFeatures.register(featureRegisterable, DELTA, registryEntry, CountMultilayerPlacementModifier.of(40), BiomePlacementModifier.of());
		PlacedFeatures.register(featureRegisterable, POISON_POOL, registryEntry2, CountMultilayerPlacementModifier.of(40), BiomePlacementModifier.of());
		PlacedFeatures.register(featureRegisterable, SMALL_BASALT_COLUMNS, registryEntry3, CountMultilayerPlacementModifier.of(4), BiomePlacementModifier.of());
		PlacedFeatures.register(
			featureRegisterable,
			SMALL_DEBRIS_COLUMNS,
			registryEntry4,
			CountMultilayerPlacementModifier.of(1),
			RarityFilterPlacementModifier.of(3),
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(featureRegisterable, LARGE_BASALT_COLUMNS, registryEntry5, CountMultilayerPlacementModifier.of(2), BiomePlacementModifier.of());
		PlacedFeatures.register(
			featureRegisterable,
			LARGE_POTATO_COLUMNS,
			registryEntry6,
			CountMultilayerPlacementModifier.of(1),
			RarityFilterPlacementModifier.of(3),
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			BASALT_BLOBS,
			registryEntry7,
			CountPlacementModifier.of(75),
			SquarePlacementModifier.of(),
			PlacedFeatures.BOTTOM_TO_TOP_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			BLACKSTONE_BLOBS,
			registryEntry8,
			CountPlacementModifier.of(25),
			SquarePlacementModifier.of(),
			PlacedFeatures.BOTTOM_TO_TOP_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			GLOWSTONE_EXTRA,
			registryEntry9,
			CountPlacementModifier.of(BiasedToBottomIntProvider.create(0, 9)),
			SquarePlacementModifier.of(),
			PlacedFeatures.FOUR_ABOVE_AND_BELOW_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			GLOWSTONE,
			registryEntry9,
			CountPlacementModifier.of(10),
			SquarePlacementModifier.of(),
			PlacedFeatures.BOTTOM_TO_TOP_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(featureRegisterable, CRIMSON_FOREST_VEGETATION, registryEntry10, CountMultilayerPlacementModifier.of(6), BiomePlacementModifier.of());
		PlacedFeatures.register(featureRegisterable, WARPED_FOREST_VEGETATION, registryEntry11, CountMultilayerPlacementModifier.of(5), BiomePlacementModifier.of());
		PlacedFeatures.register(featureRegisterable, NETHER_SPROUTS, registryEntry12, CountMultilayerPlacementModifier.of(4), BiomePlacementModifier.of());
		PlacedFeatures.register(featureRegisterable, POTATO_SPROUTS, registryEntry15, CountMultilayerPlacementModifier.of(8), BiomePlacementModifier.of());
		PlacedFeatures.register(
			featureRegisterable,
			TWISTING_VINES,
			registryEntry13,
			CountPlacementModifier.of(10),
			SquarePlacementModifier.of(),
			PlacedFeatures.BOTTOM_TO_TOP_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			CORRUPTED_BUDS,
			registryEntry14,
			CountPlacementModifier.of(10),
			SquarePlacementModifier.of(),
			PlacedFeatures.BOTTOM_TO_TOP_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			WEEPING_VINES,
			registryEntry16,
			CountPlacementModifier.of(10),
			SquarePlacementModifier.of(),
			PlacedFeatures.BOTTOM_TO_TOP_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(featureRegisterable, PATCH_CRIMSON_ROOTS, registryEntry17, PlacedFeatures.BOTTOM_TO_TOP_RANGE, BiomePlacementModifier.of());
		PlacedFeatures.register(
			featureRegisterable,
			BASALT_PILLAR,
			registryEntry18,
			CountPlacementModifier.of(10),
			SquarePlacementModifier.of(),
			PlacedFeatures.BOTTOM_TO_TOP_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			SPRING_DELTA,
			registryEntry19,
			CountPlacementModifier.of(16),
			SquarePlacementModifier.of(),
			PlacedFeatures.FOUR_ABOVE_AND_BELOW_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			SPRING_CLOSED,
			registryEntry20,
			CountPlacementModifier.of(16),
			SquarePlacementModifier.of(),
			PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			SPRING_CLOSED_DOUBLE,
			registryEntry20,
			CountPlacementModifier.of(32),
			SquarePlacementModifier.of(),
			PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			SPRING_OPEN,
			registryEntry21,
			CountPlacementModifier.of(8),
			SquarePlacementModifier.of(),
			PlacedFeatures.FOUR_ABOVE_AND_BELOW_RANGE,
			BiomePlacementModifier.of()
		);
		List<PlacementModifier> list = List.of(
			CountPlacementModifier.of(UniformIntProvider.create(0, 5)),
			SquarePlacementModifier.of(),
			PlacedFeatures.FOUR_ABOVE_AND_BELOW_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(featureRegisterable, PATCH_SOUL_FIRE, registryEntry22, list);
		PlacedFeatures.register(featureRegisterable, PATCH_FIRE, registryEntry23, list);
	}
}
