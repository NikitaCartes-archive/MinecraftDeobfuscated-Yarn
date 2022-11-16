package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public class OrePlacedFeatures {
	public static final RegistryKey<PlacedFeature> ORE_MAGMA = PlacedFeatures.of("ore_magma");
	public static final RegistryKey<PlacedFeature> ORE_SOUL_SAND = PlacedFeatures.of("ore_soul_sand");
	public static final RegistryKey<PlacedFeature> ORE_GOLD_DELTAS = PlacedFeatures.of("ore_gold_deltas");
	public static final RegistryKey<PlacedFeature> ORE_QUARTZ_DELTAS = PlacedFeatures.of("ore_quartz_deltas");
	public static final RegistryKey<PlacedFeature> ORE_GOLD_NETHER = PlacedFeatures.of("ore_gold_nether");
	public static final RegistryKey<PlacedFeature> ORE_QUARTZ_NETHER = PlacedFeatures.of("ore_quartz_nether");
	public static final RegistryKey<PlacedFeature> ORE_GRAVEL_NETHER = PlacedFeatures.of("ore_gravel_nether");
	public static final RegistryKey<PlacedFeature> ORE_BLACKSTONE = PlacedFeatures.of("ore_blackstone");
	public static final RegistryKey<PlacedFeature> ORE_DIRT = PlacedFeatures.of("ore_dirt");
	public static final RegistryKey<PlacedFeature> ORE_GRAVEL = PlacedFeatures.of("ore_gravel");
	public static final RegistryKey<PlacedFeature> ORE_GRANITE_UPPER = PlacedFeatures.of("ore_granite_upper");
	public static final RegistryKey<PlacedFeature> ORE_GRANITE_LOWER = PlacedFeatures.of("ore_granite_lower");
	public static final RegistryKey<PlacedFeature> ORE_DIORITE_UPPER = PlacedFeatures.of("ore_diorite_upper");
	public static final RegistryKey<PlacedFeature> ORE_DIORITE_LOWER = PlacedFeatures.of("ore_diorite_lower");
	public static final RegistryKey<PlacedFeature> ORE_ANDESITE_UPPER = PlacedFeatures.of("ore_andesite_upper");
	public static final RegistryKey<PlacedFeature> ORE_ANDESITE_LOWER = PlacedFeatures.of("ore_andesite_lower");
	public static final RegistryKey<PlacedFeature> ORE_TUFF = PlacedFeatures.of("ore_tuff");
	public static final RegistryKey<PlacedFeature> ORE_COAL_UPPER = PlacedFeatures.of("ore_coal_upper");
	public static final RegistryKey<PlacedFeature> ORE_COAL_LOWER = PlacedFeatures.of("ore_coal_lower");
	public static final RegistryKey<PlacedFeature> ORE_IRON_UPPER = PlacedFeatures.of("ore_iron_upper");
	public static final RegistryKey<PlacedFeature> ORE_IRON_MIDDLE = PlacedFeatures.of("ore_iron_middle");
	public static final RegistryKey<PlacedFeature> ORE_IRON_SMALL = PlacedFeatures.of("ore_iron_small");
	public static final RegistryKey<PlacedFeature> ORE_GOLD_EXTRA = PlacedFeatures.of("ore_gold_extra");
	public static final RegistryKey<PlacedFeature> ORE_GOLD = PlacedFeatures.of("ore_gold");
	public static final RegistryKey<PlacedFeature> ORE_GOLD_LOWER = PlacedFeatures.of("ore_gold_lower");
	public static final RegistryKey<PlacedFeature> ORE_REDSTONE = PlacedFeatures.of("ore_redstone");
	public static final RegistryKey<PlacedFeature> ORE_REDSTONE_LOWER = PlacedFeatures.of("ore_redstone_lower");
	public static final RegistryKey<PlacedFeature> ORE_DIAMOND = PlacedFeatures.of("ore_diamond");
	public static final RegistryKey<PlacedFeature> ORE_DIAMOND_LARGE = PlacedFeatures.of("ore_diamond_large");
	public static final RegistryKey<PlacedFeature> ORE_DIAMOND_BURIED = PlacedFeatures.of("ore_diamond_buried");
	public static final RegistryKey<PlacedFeature> ORE_LAPIS = PlacedFeatures.of("ore_lapis");
	public static final RegistryKey<PlacedFeature> ORE_LAPIS_BURIED = PlacedFeatures.of("ore_lapis_buried");
	public static final RegistryKey<PlacedFeature> ORE_INFESTED = PlacedFeatures.of("ore_infested");
	public static final RegistryKey<PlacedFeature> ORE_EMERALD = PlacedFeatures.of("ore_emerald");
	public static final RegistryKey<PlacedFeature> ORE_ANCIENT_DEBRIS_LARGE = PlacedFeatures.of("ore_ancient_debris_large");
	public static final RegistryKey<PlacedFeature> ORE_DEBRIS_SMALL = PlacedFeatures.of("ore_debris_small");
	public static final RegistryKey<PlacedFeature> ORE_COPPER = PlacedFeatures.of("ore_copper");
	public static final RegistryKey<PlacedFeature> ORE_COPPER_LARGE = PlacedFeatures.of("ore_copper_large");
	public static final RegistryKey<PlacedFeature> ORE_CLAY = PlacedFeatures.of("ore_clay");

	private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
		return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
	}

	private static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
		return modifiers(CountPlacementModifier.of(count), heightModifier);
	}

	private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
		return modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
	}

	public static void bootstrap(Registerable<PlacedFeature> featureRegisterable) {
		RegistryEntryLookup<ConfiguredFeature<?, ?>> registryEntryLookup = featureRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_MAGMA);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry2 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_SOUL_SAND);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry3 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_NETHER_GOLD);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry4 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_QUARTZ);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry5 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_GRAVEL_NETHER);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry6 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_BLACKSTONE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry7 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_DIRT);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry8 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_GRAVEL);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry9 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_GRANITE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry10 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_DIORITE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry11 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_ANDESITE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry12 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_TUFF);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry13 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_COAL);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry14 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_COAL_BURIED);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry15 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_IRON);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry16 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_IRON_SMALL);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry17 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_GOLD);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry18 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_GOLD_BURIED);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry19 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_REDSTONE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry20 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_DIAMOND_SMALL);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry21 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_DIAMOND_LARGE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry22 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_DIAMOND_BURIED);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry23 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_LAPIS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry24 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_LAPIS_BURIED);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry25 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_INFESTED);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry26 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_EMERALD);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry27 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_ANCIENT_DEBRIS_LARGE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry28 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_ANCIENT_DEBRIS_SMALL);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry29 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_COPPER_SMALL);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry30 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_COPPER_LARGE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry31 = registryEntryLookup.getOrThrow(OreConfiguredFeatures.ORE_CLAY);
		PlacedFeatures.register(
			featureRegisterable, ORE_MAGMA, registryEntry, modifiersWithCount(4, HeightRangePlacementModifier.uniform(YOffset.fixed(27), YOffset.fixed(36)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_SOUL_SAND, registryEntry2, modifiersWithCount(12, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(31)))
		);
		PlacedFeatures.register(featureRegisterable, ORE_GOLD_DELTAS, registryEntry3, modifiersWithCount(20, PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE));
		PlacedFeatures.register(featureRegisterable, ORE_QUARTZ_DELTAS, registryEntry4, modifiersWithCount(32, PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE));
		PlacedFeatures.register(featureRegisterable, ORE_GOLD_NETHER, registryEntry3, modifiersWithCount(10, PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE));
		PlacedFeatures.register(featureRegisterable, ORE_QUARTZ_NETHER, registryEntry4, modifiersWithCount(16, PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE));
		PlacedFeatures.register(
			featureRegisterable, ORE_GRAVEL_NETHER, registryEntry5, modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.fixed(5), YOffset.fixed(41)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_BLACKSTONE, registryEntry6, modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.fixed(5), YOffset.fixed(31)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_DIRT, registryEntry7, modifiersWithCount(7, HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(160)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_GRAVEL, registryEntry8, modifiersWithCount(14, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.getTop()))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_GRANITE_UPPER, registryEntry9, modifiersWithRarity(6, HeightRangePlacementModifier.uniform(YOffset.fixed(64), YOffset.fixed(128)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_GRANITE_LOWER, registryEntry9, modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(60)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_DIORITE_UPPER, registryEntry10, modifiersWithRarity(6, HeightRangePlacementModifier.uniform(YOffset.fixed(64), YOffset.fixed(128)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_DIORITE_LOWER, registryEntry10, modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(60)))
		);
		PlacedFeatures.register(
			featureRegisterable,
			ORE_ANDESITE_UPPER,
			registryEntry11,
			modifiersWithRarity(6, HeightRangePlacementModifier.uniform(YOffset.fixed(64), YOffset.fixed(128)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_ANDESITE_LOWER, registryEntry11, modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(60)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_TUFF, registryEntry12, modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_COAL_UPPER, registryEntry13, modifiersWithCount(30, HeightRangePlacementModifier.uniform(YOffset.fixed(136), YOffset.getTop()))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_COAL_LOWER, registryEntry14, modifiersWithCount(20, HeightRangePlacementModifier.trapezoid(YOffset.fixed(0), YOffset.fixed(192)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_IRON_UPPER, registryEntry15, modifiersWithCount(90, HeightRangePlacementModifier.trapezoid(YOffset.fixed(80), YOffset.fixed(384)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_IRON_MIDDLE, registryEntry15, modifiersWithCount(10, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-24), YOffset.fixed(56)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_IRON_SMALL, registryEntry16, modifiersWithCount(10, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(72)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_GOLD_EXTRA, registryEntry17, modifiersWithCount(50, HeightRangePlacementModifier.uniform(YOffset.fixed(32), YOffset.fixed(256)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_GOLD, registryEntry18, modifiersWithCount(4, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-64), YOffset.fixed(32)))
		);
		PlacedFeatures.register(
			featureRegisterable,
			ORE_GOLD_LOWER,
			registryEntry18,
			modifiers(CountPlacementModifier.of(UniformIntProvider.create(0, 1)), HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(-48)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_REDSTONE, registryEntry19, modifiersWithCount(4, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(15)))
		);
		PlacedFeatures.register(
			featureRegisterable,
			ORE_REDSTONE_LOWER,
			registryEntry19,
			modifiersWithCount(8, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-32), YOffset.aboveBottom(32)))
		);
		PlacedFeatures.register(
			featureRegisterable,
			ORE_DIAMOND,
			registryEntry20,
			modifiersWithCount(7, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80)))
		);
		PlacedFeatures.register(
			featureRegisterable,
			ORE_DIAMOND_LARGE,
			registryEntry21,
			modifiersWithRarity(9, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80)))
		);
		PlacedFeatures.register(
			featureRegisterable,
			ORE_DIAMOND_BURIED,
			registryEntry22,
			modifiersWithCount(4, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_LAPIS, registryEntry23, modifiersWithCount(2, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-32), YOffset.fixed(32)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_LAPIS_BURIED, registryEntry24, modifiersWithCount(4, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(64)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_INFESTED, registryEntry25, modifiersWithCount(14, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(63)))
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_EMERALD, registryEntry26, modifiersWithCount(100, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-16), YOffset.fixed(480)))
		);
		PlacedFeatures.register(
			featureRegisterable,
			ORE_ANCIENT_DEBRIS_LARGE,
			registryEntry27,
			SquarePlacementModifier.of(),
			HeightRangePlacementModifier.trapezoid(YOffset.fixed(8), YOffset.fixed(24)),
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable,
			ORE_DEBRIS_SMALL,
			registryEntry28,
			SquarePlacementModifier.of(),
			PlacedFeatures.EIGHT_ABOVE_AND_BELOW_RANGE,
			BiomePlacementModifier.of()
		);
		PlacedFeatures.register(
			featureRegisterable, ORE_COPPER, registryEntry29, modifiersWithCount(16, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-16), YOffset.fixed(112)))
		);
		PlacedFeatures.register(
			featureRegisterable,
			ORE_COPPER_LARGE,
			registryEntry30,
			modifiersWithCount(16, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-16), YOffset.fixed(112)))
		);
		PlacedFeatures.register(featureRegisterable, ORE_CLAY, registryEntry31, modifiersWithCount(46, PlacedFeatures.BOTTOM_TO_120_RANGE));
	}
}
