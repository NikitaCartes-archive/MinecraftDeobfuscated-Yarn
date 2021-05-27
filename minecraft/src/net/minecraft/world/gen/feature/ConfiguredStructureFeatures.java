package net.minecraft.world.gen.feature;

import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.DesertVillageData;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.SavannaVillageData;
import net.minecraft.structure.SnowyVillageData;
import net.minecraft.structure.TaigaVillageData;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredStructureFeatures {
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> PILLAGER_OUTPOST = register(
		"pillager_outpost", StructureFeature.PILLAGER_OUTPOST.configure(new StructurePoolFeatureConfig(() -> PillagerOutpostGenerator.STRUCTURE_POOLS, 7))
	);
	public static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> MINESHAFT = register(
		"mineshaft", StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.NORMAL))
	);
	public static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> MINESHAFT_MESA = register(
		"mineshaft_mesa", StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.MESA))
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> MANSION = register(
		"mansion", StructureFeature.MANSION.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> JUNGLE_PYRAMID = register(
		"jungle_pyramid", StructureFeature.JUNGLE_PYRAMID.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> DESERT_PYRAMID = register(
		"desert_pyramid", StructureFeature.DESERT_PYRAMID.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> IGLOO = register(
		"igloo", StructureFeature.IGLOO.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> SHIPWRECK = register(
		"shipwreck", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false))
	);
	public static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> SHIPWRECK_BEACHED = register(
		"shipwreck_beached", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(true))
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> SWAMP_HUT = register(
		"swamp_hut", StructureFeature.SWAMP_HUT.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> STRONGHOLD = register(
		"stronghold", StructureFeature.STRONGHOLD.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> MONUMENT = register(
		"monument", StructureFeature.MONUMENT.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> OCEAN_RUIN_COLD = register(
		"ocean_ruin_cold", StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.9F))
	);
	public static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> OCEAN_RUIN_WARM = register(
		"ocean_ruin_warm", StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.WARM, 0.3F, 0.9F))
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> FORTRESS = register(
		"fortress", StructureFeature.FORTRESS.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<RangeDecoratorConfig, ? extends StructureFeature<RangeDecoratorConfig>> NETHER_FOSSIL = register(
		"nether_fossil", StructureFeature.NETHER_FOSSIL.configure(new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.fixed(32), YOffset.belowTop(2))))
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> END_CITY = register(
		"end_city", StructureFeature.END_CITY.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<ProbabilityConfig, ? extends StructureFeature<ProbabilityConfig>> BURIED_TREASURE = register(
		"buried_treasure", StructureFeature.BURIED_TREASURE.configure(new ProbabilityConfig(0.01F))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> BASTION_REMNANT = register(
		"bastion_remnant", StructureFeature.BASTION_REMNANT.configure(new StructurePoolFeatureConfig(() -> BastionRemnantGenerator.STRUCTURE_POOLS, 6))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_PLAINS = register(
		"village_plains", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> PlainsVillageData.STRUCTURE_POOLS, 6))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_DESERT = register(
		"village_desert", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> DesertVillageData.STRUCTURE_POOLS, 6))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_SAVANNA = register(
		"village_savanna", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> SavannaVillageData.STRUCTURE_POOLS, 6))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_SNOWY = register(
		"village_snowy", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> SnowyVillageData.STRUCTURE_POOLS, 6))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_TAIGA = register(
		"village_taiga", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> TaigaVillageData.STRUCTURE_POOLS, 6))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL = register(
		"ruined_portal", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.STANDARD))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_DESERT = register(
		"ruined_portal_desert", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.DESERT))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_JUNGLE = register(
		"ruined_portal_jungle", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.JUNGLE))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_SWAMP = register(
		"ruined_portal_swamp", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.SWAMP))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_MOUNTAIN = register(
		"ruined_portal_mountain", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.MOUNTAIN))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_OCEAN = register(
		"ruined_portal_ocean", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.OCEAN))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_NETHER = register(
		"ruined_portal_nether", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.NETHER))
	);

	private static <FC extends FeatureConfig, F extends StructureFeature<FC>> ConfiguredStructureFeature<FC, F> register(
		String id, ConfiguredStructureFeature<FC, F> configuredStructureFeature
	) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, id, configuredStructureFeature);
	}
}
