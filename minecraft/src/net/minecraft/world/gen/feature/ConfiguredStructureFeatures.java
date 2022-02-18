package net.minecraft.world.gen.feature;

import java.util.Map;
import net.minecraft.class_7058;
import net.minecraft.class_7061;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.DesertVillageData;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.SavannaVillageData;
import net.minecraft.structure.SnowyVillageData;
import net.minecraft.structure.TaigaVillageData;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredStructureFeatures {
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26292 = register(
		class_7058.PILLAGER_OUTPOST,
		StructureFeature.PILLAGER_OUTPOST
			.method_41135(
				new StructurePoolFeatureConfig(PillagerOutpostGenerator.STRUCTURE_POOLS, 7),
				BiomeTags.PILLAGER_OUTPOST_HAS_STRUCTURE,
				true,
				Map.of(SpawnGroup.MONSTER, new class_7061(class_7061.class_7062.STRUCTURE, Pool.of(new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 1))))
			)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26293 = register(
		class_7058.MINESHAFT,
		StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.NORMAL), BiomeTags.MINESHAFT_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26294 = register(
		class_7058.MINESHAFT_MESA,
		StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.MESA), BiomeTags.MINESHAFT_MESA_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26295 = register(
		class_7058.MANSION, StructureFeature.MANSION.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.WOODLAND_MANSION_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26296 = register(
		class_7058.JUNGLE_PYRAMID, StructureFeature.JUNGLE_PYRAMID.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.JUNGLE_TEMPLE_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26297 = register(
		class_7058.DESERT_PYRAMID, StructureFeature.DESERT_PYRAMID.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26298 = register(
		class_7058.IGLOO, StructureFeature.IGLOO.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.IGLOO_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26299 = register(
		class_7058.SHIPWRECK, StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false), BiomeTags.SHIPWRECK_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26300 = register(
		class_7058.SHIPWRECK_BEACHED, StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(true), BiomeTags.SHIPWRECK_BEACHED_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26301 = register(
		class_7058.SWAMP_HUT,
		StructureFeature.SWAMP_HUT
			.method_41133(
				DefaultFeatureConfig.INSTANCE,
				BiomeTags.SWAMP_HUT_HAS_STRUCTURE,
				Map.of(
					SpawnGroup.MONSTER,
					new class_7061(class_7061.class_7062.PIECE, Pool.of(new SpawnSettings.SpawnEntry(EntityType.WITCH, 1, 1, 1))),
					SpawnGroup.CREATURE,
					new class_7061(class_7061.class_7062.PIECE, Pool.of(new SpawnSettings.SpawnEntry(EntityType.CAT, 1, 1, 1)))
				)
			)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26302 = register(
		class_7058.STRONGHOLD, StructureFeature.STRONGHOLD.method_41134(DefaultFeatureConfig.INSTANCE, BiomeTags.STRONGHOLD_HAS_STRUCTURE, true)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26303 = register(
		class_7058.MONUMENT,
		StructureFeature.MONUMENT
			.method_41133(
				DefaultFeatureConfig.INSTANCE,
				BiomeTags.OCEAN_MONUMENT_HAS_STRUCTURE,
				Map.of(
					SpawnGroup.MONSTER,
					new class_7061(class_7061.class_7062.STRUCTURE, Pool.of(new SpawnSettings.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4))),
					SpawnGroup.UNDERGROUND_WATER_CREATURE,
					new class_7061(class_7061.class_7062.STRUCTURE, SpawnSettings.EMPTY_ENTRY_POOL),
					SpawnGroup.AXOLOTLS,
					new class_7061(class_7061.class_7062.STRUCTURE, SpawnSettings.EMPTY_ENTRY_POOL)
				)
			)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26304 = register(
		class_7058.OCEAN_RUIN_COLD,
		StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.9F), BiomeTags.OCEAN_RUIN_COLD_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26305 = register(
		class_7058.OCEAN_RUIN_WARM,
		StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.WARM, 0.3F, 0.9F), BiomeTags.OCEAN_RUIN_WARM_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26306 = register(
		class_7058.FORTRESS,
		StructureFeature.FORTRESS
			.method_41133(
				DefaultFeatureConfig.INSTANCE,
				BiomeTags.NETHER_FORTRESS_HAS_STRUCTURE,
				Map.of(SpawnGroup.MONSTER, new class_7061(class_7061.class_7062.STRUCTURE, NetherFortressFeature.MONSTER_SPAWNS))
			)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26307 = register(
		class_7058.NETHER_FOSSIL,
		StructureFeature.NETHER_FOSSIL
			.method_41134(new RangeFeatureConfig(UniformHeightProvider.create(YOffset.fixed(32), YOffset.belowTop(2))), BiomeTags.NETHER_FOSSIL_HAS_STRUCTURE, true)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26308 = register(
		class_7058.END_CITY, StructureFeature.ENDCITY.configure(DefaultFeatureConfig.INSTANCE, BiomeTags.END_CITY_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26309 = register(
		class_7058.BURIED_TREASURE, StructureFeature.BURIED_TREASURE.configure(new ProbabilityConfig(0.01F), BiomeTags.BURIED_TREASURE_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26310 = register(
		class_7058.BASTION_REMNANT,
		StructureFeature.BASTION_REMNANT
			.configure(new StructurePoolFeatureConfig(BastionRemnantGenerator.STRUCTURE_POOLS, 6), BiomeTags.BASTION_REMNANT_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26311 = register(
		class_7058.VILLAGE_PLAINS,
		StructureFeature.VILLAGE.method_41134(new StructurePoolFeatureConfig(PlainsVillageData.STRUCTURE_POOLS, 6), BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE, true)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26312 = register(
		class_7058.VILLAGE_DESERT,
		StructureFeature.VILLAGE.method_41134(new StructurePoolFeatureConfig(DesertVillageData.STRUCTURE_POOLS, 6), BiomeTags.VILLAGE_DESERT_HAS_STRUCTURE, true)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26313 = register(
		class_7058.VILLAGE_SAVANNA,
		StructureFeature.VILLAGE.method_41134(new StructurePoolFeatureConfig(SavannaVillageData.STRUCTURE_POOLS, 6), BiomeTags.VILLAGE_SAVANNA_HAS_STRUCTURE, true)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26314 = register(
		class_7058.VILLAGE_SNOWY,
		StructureFeature.VILLAGE.method_41134(new StructurePoolFeatureConfig(SnowyVillageData.STRUCTURE_POOLS, 6), BiomeTags.VILLAGE_SNOWY_HAS_STRUCTURE, true)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26315 = register(
		class_7058.VILLAGE_TAIGA,
		StructureFeature.VILLAGE.method_41134(new StructurePoolFeatureConfig(TaigaVillageData.STRUCTURE_POOLS, 6), BiomeTags.VILLAGE_TAIGA_HAS_STRUCTURE, true)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26316 = register(
		class_7058.RUINED_PORTAL,
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.STANDARD), BiomeTags.RUINED_PORTAL_STANDARD_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26317 = register(
		class_7058.RUINED_PORTAL_DESERT,
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.DESERT), BiomeTags.RUINED_PORTAL_DESERT_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26287 = register(
		class_7058.RUINED_PORTAL_JUNGLE,
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.JUNGLE), BiomeTags.RUINED_PORTAL_JUNGLE_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26288 = register(
		class_7058.RUINED_PORTAL_SWAMP,
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.SWAMP), BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26289 = register(
		class_7058.RUINED_PORTAL_MOUNTAIN,
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.MOUNTAIN), BiomeTags.RUINED_PORTAL_MOUNTAIN_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26290 = register(
		class_7058.RUINED_PORTAL_OCEAN,
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.OCEAN), BiomeTags.RUINED_PORTAL_OCEAN_HAS_STRUCTURE)
	);
	public static final RegistryEntry<ConfiguredStructureFeature<?, ?>> field_26291 = register(
		class_7058.RUINED_PORTAL_NETHER,
		StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.NETHER), BiomeTags.RUINED_PORTAL_NETHER_HAS_STRUCTURE)
	);

	public static RegistryEntry<? extends ConfiguredStructureFeature<?, ?>> getDefault() {
		return field_26293;
	}

	private static <FC extends FeatureConfig, F extends StructureFeature<FC>> RegistryEntry<ConfiguredStructureFeature<?, ?>> register(
		RegistryKey<ConfiguredStructureFeature<?, ?>> registryKey, ConfiguredStructureFeature<FC, F> configuredStructureFeature
	) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, registryKey, configuredStructureFeature);
	}
}
