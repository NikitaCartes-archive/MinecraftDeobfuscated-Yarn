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

public class ConfiguredStructureFeatures {
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_26292 = register(
		"pillager_outpost", StructureFeature.field_24843.configure(new StructurePoolFeatureConfig(() -> PillagerOutpostGenerator.field_26252, 7))
	);
	public static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> field_26293 = register(
		"mineshaft", StructureFeature.field_24844.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.field_13692))
	);
	public static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> field_26294 = register(
		"mineshaft_mesa", StructureFeature.field_24844.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.field_13691))
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_26295 = register(
		"mansion", StructureFeature.field_24845.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_26296 = register(
		"jungle_pyramid", StructureFeature.field_24846.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_26297 = register(
		"desert_pyramid", StructureFeature.field_24847.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_26298 = register(
		"igloo", StructureFeature.field_24848.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> field_26299 = register(
		"shipwreck", StructureFeature.field_24850.configure(new ShipwreckFeatureConfig(false))
	);
	public static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> field_26300 = register(
		"shipwreck_beached", StructureFeature.field_24850.configure(new ShipwreckFeatureConfig(true))
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_26301 = register(
		"swamp_hut", StructureFeature.SWAMP_HUT.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_26302 = register(
		"stronghold", StructureFeature.field_24852.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_26303 = register(
		"monument", StructureFeature.field_24853.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> field_26304 = register(
		"ocean_ruin_cold", StructureFeature.field_24854.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.field_14528, 0.3F, 0.9F))
	);
	public static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> field_26305 = register(
		"ocean_ruin_warm", StructureFeature.field_24854.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.field_14532, 0.3F, 0.9F))
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_26306 = register(
		"fortress", StructureFeature.field_24855.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_26307 = register(
		"nether_fossil", StructureFeature.field_24859.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> field_26308 = register(
		"end_city", StructureFeature.field_24856.configure(DefaultFeatureConfig.INSTANCE)
	);
	public static final ConfiguredStructureFeature<ProbabilityConfig, ? extends StructureFeature<ProbabilityConfig>> field_26309 = register(
		"buried_treasure", StructureFeature.field_24857.configure(new ProbabilityConfig(0.01F))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_26310 = register(
		"bastion_remnant", StructureFeature.field_24860.configure(new StructurePoolFeatureConfig(() -> BastionRemnantGenerator.field_25941, 6))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_26311 = register(
		"village_plains", StructureFeature.field_24858.configure(new StructurePoolFeatureConfig(() -> PlainsVillageData.field_26253, 6))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_26312 = register(
		"village_desert", StructureFeature.field_24858.configure(new StructurePoolFeatureConfig(() -> DesertVillageData.field_25948, 6))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_26313 = register(
		"village_savanna", StructureFeature.field_24858.configure(new StructurePoolFeatureConfig(() -> SavannaVillageData.field_26285, 6))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_26314 = register(
		"village_snowy", StructureFeature.field_24858.configure(new StructurePoolFeatureConfig(() -> SnowyVillageData.field_26286, 6))
	);
	public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> field_26315 = register(
		"village_taiga", StructureFeature.field_24858.configure(new StructurePoolFeatureConfig(() -> TaigaVillageData.field_26341, 6))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> field_26316 = register(
		"ruined_portal", StructureFeature.field_24849.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.field_24000))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> field_26317 = register(
		"ruined_portal_desert", StructureFeature.field_24849.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.field_24001))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> field_26287 = register(
		"ruined_portal_jungle", StructureFeature.field_24849.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.field_24002))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> field_26288 = register(
		"ruined_portal_swamp", StructureFeature.field_24849.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.field_24003))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> field_26289 = register(
		"ruined_portal_mountain", StructureFeature.field_24849.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.field_24004))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> field_26290 = register(
		"ruined_portal_ocean", StructureFeature.field_24849.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.field_24005))
	);
	public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> field_26291 = register(
		"ruined_portal_nether", StructureFeature.field_24849.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.field_24006))
	);

	private static <FC extends FeatureConfig, F extends StructureFeature<FC>> ConfiguredStructureFeature<FC, F> register(
		String id, ConfiguredStructureFeature<FC, F> configuredStructureFeature
	) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, id, configuredStructureFeature);
	}
}
