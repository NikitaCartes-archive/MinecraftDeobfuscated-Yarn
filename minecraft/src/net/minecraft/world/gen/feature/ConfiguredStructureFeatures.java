package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.DesertVillageData;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.SavannaVillageData;
import net.minecraft.structure.SnowyVillageData;
import net.minecraft.structure.TaigaVillageData;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredStructureFeatures {
	private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> PILLAGER_OUTPOST = register(
		"pillager_outpost", StructureFeature.PILLAGER_OUTPOST.configure(new StructurePoolFeatureConfig(() -> PillagerOutpostGenerator.STRUCTURE_POOLS, 7))
	);
	private static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> MINESHAFT = register(
		"mineshaft", StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.NORMAL))
	);
	private static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> MINESHAFT_MESA = register(
		"mineshaft_mesa", StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.MESA))
	);
	private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> MANSION = register(
		"mansion", StructureFeature.MANSION.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> JUNGLE_PYRAMID = register(
		"jungle_pyramid", StructureFeature.JUNGLE_PYRAMID.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> DESERT_PYRAMID = register(
		"desert_pyramid", StructureFeature.DESERT_PYRAMID.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> IGLOO = register(
		"igloo", StructureFeature.IGLOO.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> SHIPWRECK = register(
		"shipwreck", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false))
	);
	private static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> SHIPWRECK_BEACHED = register(
		"shipwreck_beached", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(true))
	);
	private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> SWAMP_HUT = register(
		"swamp_hut", StructureFeature.SWAMP_HUT.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> STRONGHOLD = register(
		"stronghold", StructureFeature.STRONGHOLD.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> MONUMENT = register(
		"monument", StructureFeature.MONUMENT.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> OCEAN_RUIN_COLD = register(
		"ocean_ruin_cold", StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.9F))
	);
	private static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> OCEAN_RUIN_WARM = register(
		"ocean_ruin_warm", StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.WARM, 0.3F, 0.9F))
	);
	private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> FORTRESS = register(
		"fortress", StructureFeature.FORTRESS.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final ConfiguredStructureFeature<RangeDecoratorConfig, ? extends StructureFeature<RangeDecoratorConfig>> NETHER_FOSSIL = register(
		"nether_fossil", StructureFeature.NETHER_FOSSIL.configure(new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.fixed(32), YOffset.belowTop(2))))
	);
	private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> END_CITY = register(
		"end_city", StructureFeature.END_CITY.configure(DefaultFeatureConfig.INSTANCE)
	);
	private static final ConfiguredStructureFeature<ProbabilityConfig, ? extends StructureFeature<ProbabilityConfig>> BURIED_TREASURE = register(
		"buried_treasure", StructureFeature.BURIED_TREASURE.configure(new ProbabilityConfig(0.01F))
	);
	private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> BASTION_REMNANT = register(
		"bastion_remnant", StructureFeature.BASTION_REMNANT.configure(new StructurePoolFeatureConfig(() -> BastionRemnantGenerator.STRUCTURE_POOLS, 6))
	);
	private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_PLAINS = register(
		"village_plains", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> PlainsVillageData.STRUCTURE_POOLS, 6))
	);
	private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_DESERT = register(
		"village_desert", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> DesertVillageData.STRUCTURE_POOLS, 6))
	);
	private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_SAVANNA = register(
		"village_savanna", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> SavannaVillageData.STRUCTURE_POOLS, 6))
	);
	private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_SNOWY = register(
		"village_snowy", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> SnowyVillageData.STRUCTURE_POOLS, 6))
	);
	private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_TAIGA = register(
		"village_taiga", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> TaigaVillageData.STRUCTURE_POOLS, 6))
	);
	private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL = register(
		"ruined_portal", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.STANDARD))
	);
	private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_DESERT = register(
		"ruined_portal_desert", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.DESERT))
	);
	private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_JUNGLE = register(
		"ruined_portal_jungle", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.JUNGLE))
	);
	private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_SWAMP = register(
		"ruined_portal_swamp", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.SWAMP))
	);
	private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_MOUNTAIN = register(
		"ruined_portal_mountain", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.MOUNTAIN))
	);
	private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_OCEAN = register(
		"ruined_portal_ocean", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.OCEAN))
	);
	private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_NETHER = register(
		"ruined_portal_nether", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.NETHER))
	);

	public static ConfiguredStructureFeature<?, ?> getDefault() {
		return MINESHAFT;
	}

	private static <FC extends FeatureConfig, F extends StructureFeature<FC>> ConfiguredStructureFeature<FC, F> register(
		String id, ConfiguredStructureFeature<FC, F> configuredStructureFeature
	) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, id, configuredStructureFeature);
	}

	private static void register(
		BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar, ConfiguredStructureFeature<?, ?> feature, Set<RegistryKey<Biome>> biomes
	) {
		biomes.forEach(biome -> registrar.accept(feature, biome));
	}

	private static void register(
		BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar, ConfiguredStructureFeature<?, ?> feature, RegistryKey<Biome> biome
	) {
		registrar.accept(feature, biome);
	}

	public static void registerAll(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar) {
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
		register(registrar, BURIED_TREASURE, set3);
		register(registrar, DESERT_PYRAMID, BiomeKeys.DESERT);
		register(registrar, IGLOO, BiomeKeys.SNOWY_TAIGA);
		register(registrar, IGLOO, BiomeKeys.SNOWY_PLAINS);
		register(registrar, IGLOO, BiomeKeys.SNOWY_SLOPES);
		register(registrar, JUNGLE_PYRAMID, BiomeKeys.BAMBOO_JUNGLE);
		register(registrar, JUNGLE_PYRAMID, BiomeKeys.JUNGLE);
		register(registrar, MINESHAFT, set2);
		register(registrar, MINESHAFT, set4);
		register(registrar, MINESHAFT, set3);
		register(registrar, MINESHAFT, BiomeKeys.STONY_SHORE);
		register(registrar, MINESHAFT, set5);
		register(registrar, MINESHAFT, set7);
		register(registrar, MINESHAFT, set8);
		register(registrar, MINESHAFT, set9);
		register(registrar, MINESHAFT, set10);
		register(registrar, MINESHAFT, BiomeKeys.MUSHROOM_FIELDS);
		register(registrar, MINESHAFT, BiomeKeys.ICE_SPIKES);
		register(registrar, MINESHAFT, BiomeKeys.WINDSWEPT_SAVANNA);
		register(registrar, MINESHAFT, BiomeKeys.DESERT);
		register(registrar, MINESHAFT, BiomeKeys.SAVANNA);
		register(registrar, MINESHAFT, BiomeKeys.SNOWY_PLAINS);
		register(registrar, MINESHAFT, BiomeKeys.PLAINS);
		register(registrar, MINESHAFT, BiomeKeys.SUNFLOWER_PLAINS);
		register(registrar, MINESHAFT, BiomeKeys.SWAMP);
		register(registrar, MINESHAFT, BiomeKeys.SAVANNA_PLATEAU);
		register(registrar, MINESHAFT, BiomeKeys.DRIPSTONE_CAVES);
		register(registrar, MINESHAFT, BiomeKeys.LUSH_CAVES);
		register(registrar, MINESHAFT_MESA, set6);
		register(registrar, MONUMENT, set);
		register(registrar, OCEAN_RUIN_COLD, BiomeKeys.FROZEN_OCEAN);
		register(registrar, OCEAN_RUIN_COLD, BiomeKeys.COLD_OCEAN);
		register(registrar, OCEAN_RUIN_COLD, BiomeKeys.OCEAN);
		register(registrar, OCEAN_RUIN_COLD, BiomeKeys.DEEP_FROZEN_OCEAN);
		register(registrar, OCEAN_RUIN_COLD, BiomeKeys.DEEP_COLD_OCEAN);
		register(registrar, OCEAN_RUIN_COLD, BiomeKeys.DEEP_OCEAN);
		register(registrar, OCEAN_RUIN_WARM, BiomeKeys.LUKEWARM_OCEAN);
		register(registrar, OCEAN_RUIN_WARM, BiomeKeys.WARM_OCEAN);
		register(registrar, OCEAN_RUIN_WARM, BiomeKeys.DEEP_LUKEWARM_OCEAN);
		register(registrar, PILLAGER_OUTPOST, BiomeKeys.DESERT);
		register(registrar, PILLAGER_OUTPOST, BiomeKeys.PLAINS);
		register(registrar, PILLAGER_OUTPOST, BiomeKeys.SAVANNA);
		register(registrar, PILLAGER_OUTPOST, BiomeKeys.SNOWY_PLAINS);
		register(registrar, PILLAGER_OUTPOST, BiomeKeys.TAIGA);
		register(registrar, PILLAGER_OUTPOST, set5);
		register(registrar, PILLAGER_OUTPOST, BiomeKeys.GROVE);
		register(registrar, RUINED_PORTAL_DESERT, BiomeKeys.DESERT);
		register(registrar, RUINED_PORTAL_JUNGLE, set9);
		register(registrar, RUINED_PORTAL_OCEAN, set2);
		register(registrar, RUINED_PORTAL_SWAMP, BiomeKeys.SWAMP);
		register(registrar, RUINED_PORTAL_MOUNTAIN, set6);
		register(registrar, RUINED_PORTAL_MOUNTAIN, set7);
		register(registrar, RUINED_PORTAL_MOUNTAIN, BiomeKeys.SAVANNA_PLATEAU);
		register(registrar, RUINED_PORTAL_MOUNTAIN, BiomeKeys.WINDSWEPT_SAVANNA);
		register(registrar, RUINED_PORTAL_MOUNTAIN, BiomeKeys.STONY_SHORE);
		register(registrar, RUINED_PORTAL_MOUNTAIN, set5);
		register(registrar, RUINED_PORTAL, BiomeKeys.MUSHROOM_FIELDS);
		register(registrar, RUINED_PORTAL, BiomeKeys.ICE_SPIKES);
		register(registrar, RUINED_PORTAL, set3);
		register(registrar, RUINED_PORTAL, set4);
		register(registrar, RUINED_PORTAL, set8);
		register(registrar, RUINED_PORTAL, set10);
		register(registrar, RUINED_PORTAL, BiomeKeys.DRIPSTONE_CAVES);
		register(registrar, RUINED_PORTAL, BiomeKeys.LUSH_CAVES);
		register(registrar, RUINED_PORTAL, BiomeKeys.SAVANNA);
		register(registrar, RUINED_PORTAL, BiomeKeys.SNOWY_PLAINS);
		register(registrar, RUINED_PORTAL, BiomeKeys.PLAINS);
		register(registrar, RUINED_PORTAL, BiomeKeys.SUNFLOWER_PLAINS);
		register(registrar, SHIPWRECK_BEACHED, set3);
		register(registrar, SHIPWRECK, set2);
		register(registrar, SWAMP_HUT, BiomeKeys.SWAMP);
		register(registrar, VILLAGE_DESERT, BiomeKeys.DESERT);
		register(registrar, VILLAGE_PLAINS, BiomeKeys.PLAINS);
		register(registrar, VILLAGE_PLAINS, BiomeKeys.MEADOW);
		register(registrar, VILLAGE_SAVANNA, BiomeKeys.SAVANNA);
		register(registrar, VILLAGE_SNOWY, BiomeKeys.SNOWY_PLAINS);
		register(registrar, VILLAGE_TAIGA, BiomeKeys.TAIGA);
		register(registrar, MANSION, BiomeKeys.DARK_FOREST);
		register(registrar, FORTRESS, set11);
		register(registrar, NETHER_FOSSIL, BiomeKeys.SOUL_SAND_VALLEY);
		register(registrar, BASTION_REMNANT, BiomeKeys.CRIMSON_FOREST);
		register(registrar, BASTION_REMNANT, BiomeKeys.NETHER_WASTES);
		register(registrar, BASTION_REMNANT, BiomeKeys.SOUL_SAND_VALLEY);
		register(registrar, BASTION_REMNANT, BiomeKeys.WARPED_FOREST);
		register(registrar, RUINED_PORTAL_NETHER, set11);
		register(registrar, END_CITY, BiomeKeys.END_HIGHLANDS);
		register(registrar, END_CITY, BiomeKeys.END_MIDLANDS);
	}
}
