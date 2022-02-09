package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.DesertVillageData;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.SavannaVillageData;
import net.minecraft.structure.SnowyVillageData;
import net.minecraft.structure.TaigaVillageData;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredStructureFeatures {
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> PILLAGER_OUTPOST = register(
		"pillager_outpost", StructureFeature.PILLAGER_OUTPOST.configure(new StructurePoolFeatureConfig(PillagerOutpostGenerator.STRUCTURE_POOLS, 7))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<MineshaftFeatureConfig, ?>> MINESHAFT = register(
		"mineshaft", StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.NORMAL))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<MineshaftFeatureConfig, ?>> MINESHAFT_MESA = register(
		"mineshaft_mesa", StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.MESA))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> MANSION = register(
		"mansion", StructureFeature.MANSION.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> JUNGLE_PYRAMID = register(
		"jungle_pyramid", StructureFeature.JUNGLE_PYRAMID.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> DESERT_PYRAMID = register(
		"desert_pyramid", StructureFeature.DESERT_PYRAMID.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> IGLOO = register(
		"igloo", StructureFeature.IGLOO.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<ShipwreckFeatureConfig, ?>> SHIPWRECK = register(
		"shipwreck", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<ShipwreckFeatureConfig, ?>> SHIPWRECK_BEACHED = register(
		"shipwreck_beached", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(true))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> SWAMP_HUT = register(
		"swamp_hut", StructureFeature.SWAMP_HUT.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> STRONGHOLD = register(
		"stronghold", StructureFeature.STRONGHOLD.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> MONUMENT = register(
		"monument", StructureFeature.MONUMENT.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<OceanRuinFeatureConfig, ?>> OCEAN_RUIN_COLD = register(
		"ocean_ruin_cold", StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.9F))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<OceanRuinFeatureConfig, ?>> OCEAN_RUIN_WARM = register(
		"ocean_ruin_warm", StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.WARM, 0.3F, 0.9F))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> FORTRESS = register(
		"fortress", StructureFeature.FORTRESS.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RangeFeatureConfig, ?>> NETHER_FOSSIL = register(
		"nether_fossil", StructureFeature.NETHER_FOSSIL.configure(new RangeFeatureConfig(UniformHeightProvider.create(YOffset.fixed(32), YOffset.belowTop(2))))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> END_CITY = register(
		"end_city", StructureFeature.END_CITY.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<ProbabilityConfig, ?>> BURIED_TREASURE = register(
		"buried_treasure", StructureFeature.BURIED_TREASURE.configure(new ProbabilityConfig(0.01F))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> BASTION_REMNANT = register(
		"bastion_remnant", StructureFeature.BASTION_REMNANT.configure(new StructurePoolFeatureConfig(BastionRemnantGenerator.STRUCTURE_POOLS, 6))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> VILLAGE_PLAINS = register(
		"village_plains", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(PlainsVillageData.STRUCTURE_POOLS, 6))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> VILLAGE_DESERT = register(
		"village_desert", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(DesertVillageData.STRUCTURE_POOLS, 6))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> VILLAGE_SAVANNA = register(
		"village_savanna", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(SavannaVillageData.STRUCTURE_POOLS, 6))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> VILLAGE_SNOWY = register(
		"village_snowy", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(SnowyVillageData.STRUCTURE_POOLS, 6))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> VILLAGE_TAIGA = register(
		"village_taiga", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(TaigaVillageData.STRUCTURE_POOLS, 6))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL = register(
		"ruined_portal", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.STANDARD))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_DESERT = register(
		"ruined_portal_desert", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.DESERT))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_JUNGLE = register(
		"ruined_portal_jungle", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.JUNGLE))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_SWAMP = register(
		"ruined_portal_swamp", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.SWAMP))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_MOUNTAIN = register(
		"ruined_portal_mountain", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.MOUNTAIN))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_OCEAN = register(
		"ruined_portal_ocean", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.OCEAN))
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_NETHER = register(
		"ruined_portal_nether", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.NETHER))
	);

	public static RegistryEntry<? extends ConfiguredStructureFeature<?, ?>> getDefault() {
		return MINESHAFT;
	}

	private static <FC extends FeatureConfig, F extends StructureFeature<FC>> RegistryEntry<ConfiguredStructureFeature<FC, ?>> register(
		String id, ConfiguredStructureFeature<FC, F> configuredStructureFeature
	) {
		return BuiltinRegistries.method_40360(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, id, configuredStructureFeature);
	}

	private static void register(
		ConfiguredStructureFeatures.class_6896 arg, RegistryEntry<? extends ConfiguredStructureFeature<?, ?>> registryEntry, Set<RegistryKey<Biome>> biomes
	) {
		biomes.forEach(biome -> register(arg, registryEntry, biome));
	}

	private static void register(
		ConfiguredStructureFeatures.class_6896 arg, RegistryEntry<? extends ConfiguredStructureFeature<?, ?>> registryEntry, RegistryKey<Biome> biome
	) {
		RegistryEntry<ConfiguredStructureFeature<?, ?>> registryEntry2 = RegistryEntry.upcast(registryEntry);
		arg.accept(registryEntry2.value().feature, (RegistryKey<ConfiguredStructureFeature<?, ?>>)registryEntry2.getKey().orElseThrow(), biome);
	}

	public static void registerAll(ConfiguredStructureFeatures.class_6896 arg) {
		Set<RegistryKey<Biome>> set = ImmutableSet.<RegistryKey<Biome>>builder()
			.add(BiomeKeys.DEEP_FROZEN_OCEAN)
			.add(BiomeKeys.DEEP_COLD_OCEAN)
			.add(BiomeKeys.DEEP_OCEAN)
			.add(BiomeKeys.DEEP_LUKEWARM_OCEAN)
			.build();
		Set<RegistryKey<Biome>> set2 = ImmutableSet.<RegistryKey<Biome>>builder()
			.add(BiomeKeys.FROZEN_OCEAN)
			.add(BiomeKeys.OCEAN)
			.add(BiomeKeys.COLD_OCEAN)
			.add(BiomeKeys.LUKEWARM_OCEAN)
			.add(BiomeKeys.WARM_OCEAN)
			.addAll(set)
			.build();
		Set<RegistryKey<Biome>> set3 = ImmutableSet.<RegistryKey<Biome>>builder().add(BiomeKeys.BEACH).add(BiomeKeys.SNOWY_BEACH).build();
		Set<RegistryKey<Biome>> set4 = ImmutableSet.<RegistryKey<Biome>>builder().add(BiomeKeys.RIVER).add(BiomeKeys.FROZEN_RIVER).build();
		Set<RegistryKey<Biome>> set5 = ImmutableSet.<RegistryKey<Biome>>builder()
			.add(BiomeKeys.MEADOW)
			.add(BiomeKeys.FROZEN_PEAKS)
			.add(BiomeKeys.JAGGED_PEAKS)
			.add(BiomeKeys.STONY_PEAKS)
			.add(BiomeKeys.SNOWY_SLOPES)
			.build();
		Set<RegistryKey<Biome>> set6 = ImmutableSet.<RegistryKey<Biome>>builder()
			.add(BiomeKeys.BADLANDS)
			.add(BiomeKeys.ERODED_BADLANDS)
			.add(BiomeKeys.WOODED_BADLANDS)
			.build();
		Set<RegistryKey<Biome>> set7 = ImmutableSet.<RegistryKey<Biome>>builder()
			.add(BiomeKeys.WINDSWEPT_HILLS)
			.add(BiomeKeys.WINDSWEPT_FOREST)
			.add(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS)
			.build();
		Set<RegistryKey<Biome>> set8 = ImmutableSet.<RegistryKey<Biome>>builder()
			.add(BiomeKeys.TAIGA)
			.add(BiomeKeys.SNOWY_TAIGA)
			.add(BiomeKeys.OLD_GROWTH_PINE_TAIGA)
			.add(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA)
			.build();
		Set<RegistryKey<Biome>> set9 = ImmutableSet.<RegistryKey<Biome>>builder()
			.add(BiomeKeys.BAMBOO_JUNGLE)
			.add(BiomeKeys.JUNGLE)
			.add(BiomeKeys.SPARSE_JUNGLE)
			.build();
		Set<RegistryKey<Biome>> set10 = ImmutableSet.<RegistryKey<Biome>>builder()
			.add(BiomeKeys.FOREST)
			.add(BiomeKeys.FLOWER_FOREST)
			.add(BiomeKeys.BIRCH_FOREST)
			.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST)
			.add(BiomeKeys.DARK_FOREST)
			.add(BiomeKeys.GROVE)
			.build();
		Set<RegistryKey<Biome>> set11 = ImmutableSet.<RegistryKey<Biome>>builder()
			.add(BiomeKeys.NETHER_WASTES)
			.add(BiomeKeys.BASALT_DELTAS)
			.add(BiomeKeys.SOUL_SAND_VALLEY)
			.add(BiomeKeys.CRIMSON_FOREST)
			.add(BiomeKeys.WARPED_FOREST)
			.build();
		Set<RegistryKey<Biome>> set12 = ImmutableSet.<RegistryKey<Biome>>builder()
			.add(BiomeKeys.THE_VOID)
			.add(BiomeKeys.PLAINS)
			.add(BiomeKeys.SUNFLOWER_PLAINS)
			.add(BiomeKeys.SNOWY_PLAINS)
			.add(BiomeKeys.ICE_SPIKES)
			.add(BiomeKeys.DESERT)
			.add(BiomeKeys.FOREST)
			.add(BiomeKeys.FLOWER_FOREST)
			.add(BiomeKeys.BIRCH_FOREST)
			.add(BiomeKeys.DARK_FOREST)
			.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST)
			.add(BiomeKeys.OLD_GROWTH_PINE_TAIGA)
			.add(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA)
			.add(BiomeKeys.TAIGA)
			.add(BiomeKeys.SNOWY_TAIGA)
			.add(BiomeKeys.SAVANNA)
			.add(BiomeKeys.SAVANNA_PLATEAU)
			.add(BiomeKeys.WINDSWEPT_HILLS)
			.add(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS)
			.add(BiomeKeys.WINDSWEPT_FOREST)
			.add(BiomeKeys.WINDSWEPT_SAVANNA)
			.add(BiomeKeys.JUNGLE)
			.add(BiomeKeys.SPARSE_JUNGLE)
			.add(BiomeKeys.BAMBOO_JUNGLE)
			.add(BiomeKeys.BADLANDS)
			.add(BiomeKeys.ERODED_BADLANDS)
			.add(BiomeKeys.WOODED_BADLANDS)
			.add(BiomeKeys.MEADOW)
			.add(BiomeKeys.GROVE)
			.add(BiomeKeys.SNOWY_SLOPES)
			.add(BiomeKeys.FROZEN_PEAKS)
			.add(BiomeKeys.JAGGED_PEAKS)
			.add(BiomeKeys.STONY_PEAKS)
			.add(BiomeKeys.MUSHROOM_FIELDS)
			.add(BiomeKeys.DRIPSTONE_CAVES)
			.add(BiomeKeys.LUSH_CAVES)
			.build();
		register(arg, BURIED_TREASURE, set3);
		register(arg, DESERT_PYRAMID, BiomeKeys.DESERT);
		register(arg, IGLOO, BiomeKeys.SNOWY_TAIGA);
		register(arg, IGLOO, BiomeKeys.SNOWY_PLAINS);
		register(arg, IGLOO, BiomeKeys.SNOWY_SLOPES);
		register(arg, JUNGLE_PYRAMID, BiomeKeys.BAMBOO_JUNGLE);
		register(arg, JUNGLE_PYRAMID, BiomeKeys.JUNGLE);
		register(arg, MINESHAFT, set2);
		register(arg, MINESHAFT, set4);
		register(arg, MINESHAFT, set3);
		register(arg, MINESHAFT, BiomeKeys.STONY_SHORE);
		register(arg, MINESHAFT, set5);
		register(arg, MINESHAFT, set7);
		register(arg, MINESHAFT, set8);
		register(arg, MINESHAFT, set9);
		register(arg, MINESHAFT, set10);
		register(arg, MINESHAFT, BiomeKeys.MUSHROOM_FIELDS);
		register(arg, MINESHAFT, BiomeKeys.ICE_SPIKES);
		register(arg, MINESHAFT, BiomeKeys.WINDSWEPT_SAVANNA);
		register(arg, MINESHAFT, BiomeKeys.DESERT);
		register(arg, MINESHAFT, BiomeKeys.SAVANNA);
		register(arg, MINESHAFT, BiomeKeys.SNOWY_PLAINS);
		register(arg, MINESHAFT, BiomeKeys.PLAINS);
		register(arg, MINESHAFT, BiomeKeys.SUNFLOWER_PLAINS);
		register(arg, MINESHAFT, BiomeKeys.SWAMP);
		register(arg, MINESHAFT, BiomeKeys.SAVANNA_PLATEAU);
		register(arg, MINESHAFT, BiomeKeys.DRIPSTONE_CAVES);
		register(arg, MINESHAFT, BiomeKeys.LUSH_CAVES);
		register(arg, MINESHAFT_MESA, set6);
		register(arg, MONUMENT, set);
		register(arg, OCEAN_RUIN_COLD, BiomeKeys.FROZEN_OCEAN);
		register(arg, OCEAN_RUIN_COLD, BiomeKeys.COLD_OCEAN);
		register(arg, OCEAN_RUIN_COLD, BiomeKeys.OCEAN);
		register(arg, OCEAN_RUIN_COLD, BiomeKeys.DEEP_FROZEN_OCEAN);
		register(arg, OCEAN_RUIN_COLD, BiomeKeys.DEEP_COLD_OCEAN);
		register(arg, OCEAN_RUIN_COLD, BiomeKeys.DEEP_OCEAN);
		register(arg, OCEAN_RUIN_WARM, BiomeKeys.LUKEWARM_OCEAN);
		register(arg, OCEAN_RUIN_WARM, BiomeKeys.WARM_OCEAN);
		register(arg, OCEAN_RUIN_WARM, BiomeKeys.DEEP_LUKEWARM_OCEAN);
		register(arg, PILLAGER_OUTPOST, BiomeKeys.DESERT);
		register(arg, PILLAGER_OUTPOST, BiomeKeys.PLAINS);
		register(arg, PILLAGER_OUTPOST, BiomeKeys.SAVANNA);
		register(arg, PILLAGER_OUTPOST, BiomeKeys.SNOWY_PLAINS);
		register(arg, PILLAGER_OUTPOST, BiomeKeys.TAIGA);
		register(arg, PILLAGER_OUTPOST, set5);
		register(arg, PILLAGER_OUTPOST, BiomeKeys.GROVE);
		register(arg, RUINED_PORTAL_DESERT, BiomeKeys.DESERT);
		register(arg, RUINED_PORTAL_JUNGLE, set9);
		register(arg, RUINED_PORTAL_OCEAN, set2);
		register(arg, RUINED_PORTAL_SWAMP, BiomeKeys.SWAMP);
		register(arg, RUINED_PORTAL_MOUNTAIN, set6);
		register(arg, RUINED_PORTAL_MOUNTAIN, set7);
		register(arg, RUINED_PORTAL_MOUNTAIN, BiomeKeys.SAVANNA_PLATEAU);
		register(arg, RUINED_PORTAL_MOUNTAIN, BiomeKeys.WINDSWEPT_SAVANNA);
		register(arg, RUINED_PORTAL_MOUNTAIN, BiomeKeys.STONY_SHORE);
		register(arg, RUINED_PORTAL_MOUNTAIN, set5);
		register(arg, RUINED_PORTAL, BiomeKeys.MUSHROOM_FIELDS);
		register(arg, RUINED_PORTAL, BiomeKeys.ICE_SPIKES);
		register(arg, RUINED_PORTAL, set3);
		register(arg, RUINED_PORTAL, set4);
		register(arg, RUINED_PORTAL, set8);
		register(arg, RUINED_PORTAL, set10);
		register(arg, RUINED_PORTAL, BiomeKeys.DRIPSTONE_CAVES);
		register(arg, RUINED_PORTAL, BiomeKeys.LUSH_CAVES);
		register(arg, RUINED_PORTAL, BiomeKeys.SAVANNA);
		register(arg, RUINED_PORTAL, BiomeKeys.SNOWY_PLAINS);
		register(arg, RUINED_PORTAL, BiomeKeys.PLAINS);
		register(arg, RUINED_PORTAL, BiomeKeys.SUNFLOWER_PLAINS);
		register(arg, SHIPWRECK_BEACHED, set3);
		register(arg, SHIPWRECK, set2);
		register(arg, SWAMP_HUT, BiomeKeys.SWAMP);
		register(arg, VILLAGE_DESERT, BiomeKeys.DESERT);
		register(arg, VILLAGE_PLAINS, BiomeKeys.PLAINS);
		register(arg, VILLAGE_PLAINS, BiomeKeys.MEADOW);
		register(arg, VILLAGE_SAVANNA, BiomeKeys.SAVANNA);
		register(arg, VILLAGE_SNOWY, BiomeKeys.SNOWY_PLAINS);
		register(arg, VILLAGE_TAIGA, BiomeKeys.TAIGA);
		register(arg, MANSION, BiomeKeys.DARK_FOREST);
		register(arg, STRONGHOLD, set12);
		register(arg, FORTRESS, set11);
		register(arg, NETHER_FOSSIL, BiomeKeys.SOUL_SAND_VALLEY);
		register(arg, BASTION_REMNANT, BiomeKeys.CRIMSON_FOREST);
		register(arg, BASTION_REMNANT, BiomeKeys.NETHER_WASTES);
		register(arg, BASTION_REMNANT, BiomeKeys.SOUL_SAND_VALLEY);
		register(arg, BASTION_REMNANT, BiomeKeys.WARPED_FOREST);
		register(arg, RUINED_PORTAL_NETHER, set11);
		register(arg, END_CITY, BiomeKeys.END_HIGHLANDS);
		register(arg, END_CITY, BiomeKeys.END_MIDLANDS);
	}

	@FunctionalInterface
	public interface class_6896 {
		void accept(StructureFeature<?> feature, RegistryKey<ConfiguredStructureFeature<?, ?>> configuredFeatureKey, RegistryKey<Biome> biomeKey);
	}
}
