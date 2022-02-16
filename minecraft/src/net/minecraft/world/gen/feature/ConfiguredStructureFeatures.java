package net.minecraft.world.gen.feature;

import net.minecraft.class_6908;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.DesertVillageData;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.SavannaVillageData;
import net.minecraft.structure.SnowyVillageData;
import net.minecraft.structure.TaigaVillageData;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredStructureFeatures {
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> PILLAGER_OUTPOST = register(
		"pillager_outpost",
		StructureFeature.PILLAGER_OUTPOST
			.configure(new StructurePoolFeatureConfig(PillagerOutpostGenerator.STRUCTURE_POOLS, 7), class_6908.PILLAGER_OUTPOST_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<MineshaftFeatureConfig, ?>> MINESHAFT = register(
		"mineshaft", StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.NORMAL), class_6908.MINESHAFT_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<MineshaftFeatureConfig, ?>> MINESHAFT_MESA = register(
		"mineshaft_mesa",
		StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.MESA), class_6908.MINESHAFT_MESA_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> MANSION = register(
		"mansion", StructureFeature.MANSION.configure(DefaultFeatureConfig.INSTANCE, class_6908.WOODLAND_MANSION_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> JUNGLE_PYRAMID = register(
		"jungle_pyramid", StructureFeature.JUNGLE_PYRAMID.configure(DefaultFeatureConfig.INSTANCE, class_6908.JUNGLE_TEMPLE_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> DESERT_PYRAMID = register(
		"desert_pyramid", StructureFeature.DESERT_PYRAMID.configure(DefaultFeatureConfig.INSTANCE, class_6908.DESERT_PYRAMID_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> IGLOO = register(
		"igloo", StructureFeature.IGLOO.configure(DefaultFeatureConfig.INSTANCE, class_6908.IGLOO_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<ShipwreckFeatureConfig, ?>> SHIPWRECK = register(
		"shipwreck", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false), class_6908.SHIPWRECK_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<ShipwreckFeatureConfig, ?>> SHIPWRECK_BEACHED = register(
		"shipwreck_beached", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(true), class_6908.SHIPWRECK_BEACHED_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> SWAMP_HUT = register(
		"swamp_hut", StructureFeature.SWAMP_HUT.configure(DefaultFeatureConfig.INSTANCE, class_6908.SWAMP_HUT_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> STRONGHOLD = register(
		"stronghold", StructureFeature.STRONGHOLD.configure(DefaultFeatureConfig.INSTANCE, class_6908.STRONGHOLD_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> MONUMENT = register(
		"monument", StructureFeature.MONUMENT.configure(DefaultFeatureConfig.INSTANCE, class_6908.OCEAN_MONUMENT_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<OceanRuinFeatureConfig, ?>> OCEAN_RUIN_COLD = register(
		"ocean_ruin_cold",
		StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.9F), class_6908.OCEAN_RUIN_COLD_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<OceanRuinFeatureConfig, ?>> OCEAN_RUIN_WARM = register(
		"ocean_ruin_warm",
		StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.WARM, 0.3F, 0.9F), class_6908.OCEAN_RUIN_WARM_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> FORTRESS = register(
		"fortress", StructureFeature.FORTRESS.configure(DefaultFeatureConfig.INSTANCE, class_6908.NETHER_FORTRESS_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RangeFeatureConfig, ?>> NETHER_FOSSIL = register(
		"nether_fossil",
		StructureFeature.NETHER_FOSSIL
			.configure(new RangeFeatureConfig(UniformHeightProvider.create(YOffset.fixed(32), YOffset.belowTop(2))), class_6908.NETHER_FOSSIL_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<DefaultFeatureConfig, ?>> END_CITY = register(
		"end_city", StructureFeature.END_CITY.configure(DefaultFeatureConfig.INSTANCE, class_6908.END_CITY_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<ProbabilityConfig, ?>> BURIED_TREASURE = register(
		"buried_treasure", StructureFeature.BURIED_TREASURE.configure(new ProbabilityConfig(0.01F), class_6908.BURIED_TREASURE_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> BASTION_REMNANT = register(
		"bastion_remnant",
		StructureFeature.BASTION_REMNANT
			.configure(new StructurePoolFeatureConfig(BastionRemnantGenerator.STRUCTURE_POOLS, 6), class_6908.BASTION_REMNANT_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> VILLAGE_PLAINS = register(
		"village_plains",
		StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(PlainsVillageData.STRUCTURE_POOLS, 6), class_6908.VILLAGE_PLAINS_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> VILLAGE_DESERT = register(
		"village_desert",
		StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(DesertVillageData.STRUCTURE_POOLS, 6), class_6908.VILLAGE_DESERT_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> VILLAGE_SAVANNA = register(
		"village_savanna",
		StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(SavannaVillageData.STRUCTURE_POOLS, 6), class_6908.VILLAGE_SAVANNA_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> VILLAGE_SNOWY = register(
		"village_snowy",
		StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(SnowyVillageData.STRUCTURE_POOLS, 6), class_6908.VILLAGE_SNOWY_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<StructurePoolFeatureConfig, ?>> VILLAGE_TAIGA = register(
		"village_taiga",
		StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(TaigaVillageData.STRUCTURE_POOLS, 6), class_6908.VILLAGE_TAIGA_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL = register(
		"ruined_portal",
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.STANDARD), class_6908.RUINED_PORTAL_STANDARD_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_DESERT = register(
		"ruined_portal_desert",
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.DESERT), class_6908.RUINED_PORTAL_DESERT_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_JUNGLE = register(
		"ruined_portal_jungle",
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.JUNGLE), class_6908.RUINED_PORTAL_JUNGLE_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_SWAMP = register(
		"ruined_portal_swamp",
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.SWAMP), class_6908.RUINED_PORTAL_SWAMP_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_MOUNTAIN = register(
		"ruined_portal_mountain",
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.MOUNTAIN), class_6908.RUINED_PORTAL_MOUNTAIN_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_OCEAN = register(
		"ruined_portal_ocean",
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.OCEAN), class_6908.RUINED_PORTAL_OCEAN_HAS_STRUCTURE)
	);
	private static final RegistryEntry<ConfiguredStructureFeature<RuinedPortalFeatureConfig, ?>> RUINED_PORTAL_NETHER = register(
		"ruined_portal_nether",
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.NETHER), class_6908.RUINED_PORTAL_NETHER_HAS_STRUCTURE)
	);

	public static RegistryEntry<? extends ConfiguredStructureFeature<?, ?>> getDefault() {
		return MINESHAFT;
	}

	private static <FC extends FeatureConfig, F extends StructureFeature<FC>> RegistryEntry<ConfiguredStructureFeature<FC, ?>> register(
		String id, ConfiguredStructureFeature<FC, F> configuredStructureFeature
	) {
		return BuiltinRegistries.method_40360(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, id, configuredStructureFeature);
	}
}
