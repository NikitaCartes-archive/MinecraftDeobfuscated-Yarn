package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.AncientCityGenerator;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.DesertVillageData;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.structure.SavannaVillageData;
import net.minecraft.structure.SnowyVillageData;
import net.minecraft.structure.TaigaVillageData;
import net.minecraft.tag.BiomeTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class StructureTypes {
	public static final RegistryEntry<StructureType> PILLAGER_OUTPOST = register(
		StructureTypeKeys.PILLAGER_OUTPOST,
		new JigsawStructure(
			createConfig(
				BiomeTags.PILLAGER_OUTPOST_HAS_STRUCTURE,
				Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, Pool.of(new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 1)))),
				GenerationStep.Feature.SURFACE_STRUCTURES,
				StructureTerrainAdaptation.BEARD_THIN
			),
			PillagerOutpostGenerator.STRUCTURE_POOLS,
			7,
			ConstantHeightProvider.create(YOffset.fixed(0)),
			true,
			Heightmap.Type.WORLD_SURFACE_WG
		)
	);
	public static final RegistryEntry<StructureType> MINESHAFT = register(
		StructureTypeKeys.MINESHAFT,
		new MineshaftStructure(
			createConfig(BiomeTags.MINESHAFT_HAS_STRUCTURE, GenerationStep.Feature.UNDERGROUND_STRUCTURES, StructureTerrainAdaptation.NONE),
			MineshaftStructure.Type.NORMAL
		)
	);
	public static final RegistryEntry<StructureType> MINESHAFT_MESA = register(
		StructureTypeKeys.MINESHAFT_MESA,
		new MineshaftStructure(
			createConfig(BiomeTags.MINESHAFT_MESA_HAS_STRUCTURE, GenerationStep.Feature.UNDERGROUND_STRUCTURES, StructureTerrainAdaptation.NONE),
			MineshaftStructure.Type.MESA
		)
	);
	public static final RegistryEntry<StructureType> MANSION = register(
		StructureTypeKeys.MANSION, new WoodlandMansionStructure(createConfig(BiomeTags.WOODLAND_MANSION_HAS_STRUCTURE, StructureTerrainAdaptation.NONE))
	);
	public static final RegistryEntry<StructureType> JUNGLE_PYRAMID = register(
		StructureTypeKeys.JUNGLE_PYRAMID, new JungleTempleStructure(createConfig(BiomeTags.JUNGLE_TEMPLE_HAS_STRUCTURE, StructureTerrainAdaptation.NONE))
	);
	public static final RegistryEntry<StructureType> DESERT_PYRAMID = register(
		StructureTypeKeys.DESERT_PYRAMID, new DesertPyramidStructure(createConfig(BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE, StructureTerrainAdaptation.NONE))
	);
	public static final RegistryEntry<StructureType> IGLOO = register(
		StructureTypeKeys.IGLOO, new IglooStructure(createConfig(BiomeTags.IGLOO_HAS_STRUCTURE, StructureTerrainAdaptation.NONE))
	);
	public static final RegistryEntry<StructureType> SHIPWRECK = register(
		StructureTypeKeys.SHIPWRECK, new ShipwreckStructure(createConfig(BiomeTags.SHIPWRECK_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), false)
	);
	public static final RegistryEntry<StructureType> SHIPWRECK_BEACHED = register(
		StructureTypeKeys.SHIPWRECK_BEACHED, new ShipwreckStructure(createConfig(BiomeTags.SHIPWRECK_BEACHED_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), true)
	);
	public static final RegistryEntry<StructureType> SWAMP_HUT = register(
		StructureTypeKeys.SWAMP_HUT,
		new SwampHutStructure(
			createConfig(
				BiomeTags.SWAMP_HUT_HAS_STRUCTURE,
				Map.of(
					SpawnGroup.MONSTER,
					new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.of(new SpawnSettings.SpawnEntry(EntityType.WITCH, 1, 1, 1))),
					SpawnGroup.CREATURE,
					new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.of(new SpawnSettings.SpawnEntry(EntityType.CAT, 1, 1, 1)))
				),
				GenerationStep.Feature.SURFACE_STRUCTURES,
				StructureTerrainAdaptation.NONE
			)
		)
	);
	public static final RegistryEntry<StructureType> STRONGHOLD = register(
		StructureTypeKeys.STRONGHOLD, new StrongholdStructure(createConfig(BiomeTags.STRONGHOLD_HAS_STRUCTURE, StructureTerrainAdaptation.BURY))
	);
	public static final RegistryEntry<StructureType> MONUMENT = register(
		StructureTypeKeys.MONUMENT,
		new OceanMonumentStructure(
			createConfig(
				BiomeTags.OCEAN_MONUMENT_HAS_STRUCTURE,
				Map.of(
					SpawnGroup.MONSTER,
					new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, Pool.of(new SpawnSettings.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4))),
					SpawnGroup.UNDERGROUND_WATER_CREATURE,
					new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, SpawnSettings.EMPTY_ENTRY_POOL),
					SpawnGroup.AXOLOTLS,
					new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, SpawnSettings.EMPTY_ENTRY_POOL)
				),
				GenerationStep.Feature.SURFACE_STRUCTURES,
				StructureTerrainAdaptation.NONE
			)
		)
	);
	public static final RegistryEntry<StructureType> OCEAN_RUIN_COLD = register(
		StructureTypeKeys.OCEAN_RUIN_COLD,
		new OceanRuinStructure(
			createConfig(BiomeTags.OCEAN_RUIN_COLD_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), OceanRuinStructure.BiomeTemperature.COLD, 0.3F, 0.9F
		)
	);
	public static final RegistryEntry<StructureType> OCEAN_RUIN_WARM = register(
		StructureTypeKeys.OCEAN_RUIN_WARM,
		new OceanRuinStructure(
			createConfig(BiomeTags.OCEAN_RUIN_WARM_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), OceanRuinStructure.BiomeTemperature.WARM, 0.3F, 0.9F
		)
	);
	public static final RegistryEntry<StructureType> FORTRESS = register(
		StructureTypeKeys.FORTRESS,
		new NetherFortressStructure(
			createConfig(
				BiomeTags.NETHER_FORTRESS_HAS_STRUCTURE,
				Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, NetherFortressStructure.MONSTER_SPAWNS)),
				GenerationStep.Feature.UNDERGROUND_DECORATION,
				StructureTerrainAdaptation.NONE
			)
		)
	);
	public static final RegistryEntry<StructureType> NETHER_FOSSIL = register(
		StructureTypeKeys.NETHER_FOSSIL,
		new NetherFossilStructure(
			createConfig(BiomeTags.NETHER_FOSSIL_HAS_STRUCTURE, GenerationStep.Feature.UNDERGROUND_DECORATION, StructureTerrainAdaptation.BEARD_THIN),
			UniformHeightProvider.create(YOffset.fixed(32), YOffset.belowTop(2))
		)
	);
	public static final RegistryEntry<StructureType> END_CITY = register(
		StructureTypeKeys.END_CITY, new EndCityStructure(createConfig(BiomeTags.END_CITY_HAS_STRUCTURE, StructureTerrainAdaptation.NONE))
	);
	public static final RegistryEntry<StructureType> BURIED_TREASURE = register(
		StructureTypeKeys.BURIED_TREASURE,
		new BuriedTreasureStructure(
			createConfig(BiomeTags.BURIED_TREASURE_HAS_STRUCTURE, GenerationStep.Feature.UNDERGROUND_STRUCTURES, StructureTerrainAdaptation.NONE)
		)
	);
	public static final RegistryEntry<StructureType> BASTION_REMNANT = register(
		StructureTypeKeys.BASTION_REMNANT,
		new JigsawStructure(
			createConfig(BiomeTags.BASTION_REMNANT_HAS_STRUCTURE, StructureTerrainAdaptation.NONE),
			BastionRemnantGenerator.STRUCTURE_POOLS,
			6,
			ConstantHeightProvider.create(YOffset.fixed(33)),
			false
		)
	);
	public static final RegistryEntry<StructureType> VILLAGE_PLAINS = register(
		StructureTypeKeys.VILLAGE_PLAINS,
		new JigsawStructure(
			createConfig(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN),
			PlainsVillageData.STRUCTURE_POOLS,
			6,
			ConstantHeightProvider.create(YOffset.fixed(0)),
			true,
			Heightmap.Type.WORLD_SURFACE_WG
		)
	);
	public static final RegistryEntry<StructureType> VILLAGE_DESERT = register(
		StructureTypeKeys.VILLAGE_DESERT,
		new JigsawStructure(
			createConfig(BiomeTags.VILLAGE_DESERT_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN),
			DesertVillageData.STRUCTURE_POOLS,
			6,
			ConstantHeightProvider.create(YOffset.fixed(0)),
			true,
			Heightmap.Type.WORLD_SURFACE_WG
		)
	);
	public static final RegistryEntry<StructureType> VILLAGE_SAVANNA = register(
		StructureTypeKeys.VILLAGE_SAVANNA,
		new JigsawStructure(
			createConfig(BiomeTags.VILLAGE_SAVANNA_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN),
			SavannaVillageData.STRUCTURE_POOLS,
			6,
			ConstantHeightProvider.create(YOffset.fixed(0)),
			true,
			Heightmap.Type.WORLD_SURFACE_WG
		)
	);
	public static final RegistryEntry<StructureType> VILLAGE_SNOWY = register(
		StructureTypeKeys.VILLAGE_SNOWY,
		new JigsawStructure(
			createConfig(BiomeTags.VILLAGE_SNOWY_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN),
			SnowyVillageData.STRUCTURE_POOLS,
			6,
			ConstantHeightProvider.create(YOffset.fixed(0)),
			true,
			Heightmap.Type.WORLD_SURFACE_WG
		)
	);
	public static final RegistryEntry<StructureType> VILLAGE_TAIGA = register(
		StructureTypeKeys.VILLAGE_TAIGA,
		new JigsawStructure(
			createConfig(BiomeTags.VILLAGE_TAIGA_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN),
			TaigaVillageData.STRUCTURE_POOLS,
			6,
			ConstantHeightProvider.create(YOffset.fixed(0)),
			true,
			Heightmap.Type.WORLD_SURFACE_WG
		)
	);
	public static final RegistryEntry<StructureType> RUINED_PORTAL = register(
		StructureTypeKeys.RUINED_PORTAL,
		new RuinedPortalStructure(
			createConfig(BiomeTags.RUINED_PORTAL_STANDARD_HAS_STRUCTURE, StructureTerrainAdaptation.NONE),
			List.of(
				new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND, 1.0F, 0.2F, false, false, true, false, 0.5F),
				new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE, 0.5F, 0.2F, false, false, true, false, 0.5F)
			)
		)
	);
	public static final RegistryEntry<StructureType> RUINED_PORTAL_DESERT = register(
		StructureTypeKeys.RUINED_PORTAL_DESERT,
		new RuinedPortalStructure(
			createConfig(BiomeTags.RUINED_PORTAL_DESERT_HAS_STRUCTURE, StructureTerrainAdaptation.NONE),
			new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED, 0.0F, 0.0F, false, false, false, false, 1.0F)
		)
	);
	public static final RegistryEntry<StructureType> RUINED_PORTAL_JUNGLE = register(
		StructureTypeKeys.RUINED_PORTAL_JUNGLE,
		new RuinedPortalStructure(
			createConfig(BiomeTags.RUINED_PORTAL_JUNGLE_HAS_STRUCTURE, StructureTerrainAdaptation.NONE),
			new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE, 0.5F, 0.8F, true, true, false, false, 1.0F)
		)
	);
	public static final RegistryEntry<StructureType> RUINED_PORTAL_SWAMP = register(
		StructureTypeKeys.RUINED_PORTAL_SWAMP,
		new RuinedPortalStructure(
			createConfig(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE, StructureTerrainAdaptation.NONE),
			new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0F, 0.5F, false, true, false, false, 1.0F)
		)
	);
	public static final RegistryEntry<StructureType> RUINED_PORTAL_MOUNTAIN = register(
		StructureTypeKeys.RUINED_PORTAL_MOUNTAIN,
		new RuinedPortalStructure(
			createConfig(BiomeTags.RUINED_PORTAL_MOUNTAIN_HAS_STRUCTURE, StructureTerrainAdaptation.NONE),
			List.of(
				new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN, 1.0F, 0.2F, false, false, true, false, 0.5F),
				new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE, 0.5F, 0.2F, false, false, true, false, 0.5F)
			)
		)
	);
	public static final RegistryEntry<StructureType> RUINED_PORTAL_OCEAN = register(
		StructureTypeKeys.RUINED_PORTAL_OCEAN,
		new RuinedPortalStructure(
			createConfig(BiomeTags.RUINED_PORTAL_OCEAN_HAS_STRUCTURE, StructureTerrainAdaptation.NONE),
			new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0F, 0.8F, false, false, true, false, 1.0F)
		)
	);
	public static final RegistryEntry<StructureType> RUINED_PORTAL_NETHER = register(
		StructureTypeKeys.RUINED_PORTAL_NETHER,
		new RuinedPortalStructure(
			createConfig(BiomeTags.RUINED_PORTAL_NETHER_HAS_STRUCTURE, StructureTerrainAdaptation.NONE),
			new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER, 0.5F, 0.0F, false, false, false, true, 1.0F)
		)
	);
	public static final RegistryEntry<StructureType> ANCIENT_CITY = register(
		StructureTypeKeys.ANCIENT_CITY,
		new JigsawStructure(
			createConfig(
				BiomeTags.ANCIENT_CITY_HAS_STRUCTURE,
				(Map<SpawnGroup, StructureSpawns>)Arrays.stream(SpawnGroup.values())
					.collect(Collectors.toMap(spawnGroup -> spawnGroup, spawnGroup -> new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, Pool.empty()))),
				GenerationStep.Feature.UNDERGROUND_DECORATION,
				StructureTerrainAdaptation.BEARD_BOX
			),
			AncientCityGenerator.CITY_CENTER,
			Optional.empty(),
			7,
			ConstantHeightProvider.create(YOffset.fixed(-51)),
			false,
			Optional.empty(),
			100
		)
	);

	public static RegistryEntry<? extends StructureType> getDefault() {
		return MINESHAFT;
	}

	private static StructureType.Config createConfig(
		TagKey<Biome> biomeTag, Map<SpawnGroup, StructureSpawns> spawns, GenerationStep.Feature featureStep, StructureTerrainAdaptation terrainAdaptation
	) {
		return new StructureType.Config(getOrCreateBiomeTag(biomeTag), spawns, featureStep, terrainAdaptation);
	}

	private static StructureType.Config createConfig(TagKey<Biome> biomeTag, GenerationStep.Feature featureStep, StructureTerrainAdaptation terrainAdaptation) {
		return createConfig(biomeTag, Map.of(), featureStep, terrainAdaptation);
	}

	private static StructureType.Config createConfig(TagKey<Biome> biomeTag, StructureTerrainAdaptation terrainAdaptation) {
		return createConfig(biomeTag, Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, terrainAdaptation);
	}

	private static RegistryEntry<StructureType> register(RegistryKey<StructureType> key, StructureType configuredStructureFeature) {
		return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE, key, configuredStructureFeature);
	}

	private static RegistryEntryList<Biome> getOrCreateBiomeTag(TagKey<Biome> key) {
		return BuiltinRegistries.BIOME.getOrCreateEntryList(key);
	}
}
