/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
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
import net.minecraft.world.gen.structure.BuriedTreasureStructure;
import net.minecraft.world.gen.structure.DesertPyramidStructure;
import net.minecraft.world.gen.structure.EndCityStructure;
import net.minecraft.world.gen.structure.IglooStructure;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.JungleTempleStructure;
import net.minecraft.world.gen.structure.MineshaftStructure;
import net.minecraft.world.gen.structure.NetherFortressStructure;
import net.minecraft.world.gen.structure.NetherFossilStructure;
import net.minecraft.world.gen.structure.OceanMonumentStructure;
import net.minecraft.world.gen.structure.OceanRuinStructure;
import net.minecraft.world.gen.structure.RuinedPortalStructure;
import net.minecraft.world.gen.structure.ShipwreckStructure;
import net.minecraft.world.gen.structure.StrongholdStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import net.minecraft.world.gen.structure.SwampHutStructure;
import net.minecraft.world.gen.structure.WoodlandMansionStructure;

public class Structures {
    public static final RegistryEntry<Structure> PILLAGER_OUTPOST = Structures.register(StructureKeys.PILLAGER_OUTPOST, new JigsawStructure(Structures.createConfig(BiomeTags.PILLAGER_OUTPOST_HAS_STRUCTURE, Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 1)}))), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_THIN), PillagerOutpostGenerator.STRUCTURE_POOLS, 7, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<Structure> MINESHAFT = Structures.register(StructureKeys.MINESHAFT, new MineshaftStructure(Structures.createConfig(BiomeTags.MINESHAFT_HAS_STRUCTURE, GenerationStep.Feature.UNDERGROUND_STRUCTURES, StructureTerrainAdaptation.NONE), MineshaftStructure.Type.NORMAL));
    public static final RegistryEntry<Structure> MINESHAFT_MESA = Structures.register(StructureKeys.MINESHAFT_MESA, new MineshaftStructure(Structures.createConfig(BiomeTags.MINESHAFT_MESA_HAS_STRUCTURE, GenerationStep.Feature.UNDERGROUND_STRUCTURES, StructureTerrainAdaptation.NONE), MineshaftStructure.Type.MESA));
    public static final RegistryEntry<Structure> MANSION = Structures.register(StructureKeys.MANSION, new WoodlandMansionStructure(Structures.createConfig(BiomeTags.WOODLAND_MANSION_HAS_STRUCTURE, StructureTerrainAdaptation.NONE)));
    public static final RegistryEntry<Structure> JUNGLE_PYRAMID = Structures.register(StructureKeys.JUNGLE_PYRAMID, new JungleTempleStructure(Structures.createConfig(BiomeTags.JUNGLE_TEMPLE_HAS_STRUCTURE, StructureTerrainAdaptation.NONE)));
    public static final RegistryEntry<Structure> DESERT_PYRAMID = Structures.register(StructureKeys.DESERT_PYRAMID, new DesertPyramidStructure(Structures.createConfig(BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE, StructureTerrainAdaptation.NONE)));
    public static final RegistryEntry<Structure> IGLOO = Structures.register(StructureKeys.IGLOO, new IglooStructure(Structures.createConfig(BiomeTags.IGLOO_HAS_STRUCTURE, StructureTerrainAdaptation.NONE)));
    public static final RegistryEntry<Structure> SHIPWRECK = Structures.register(StructureKeys.SHIPWRECK, new ShipwreckStructure(Structures.createConfig(BiomeTags.SHIPWRECK_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), false));
    public static final RegistryEntry<Structure> SHIPWRECK_BEACHED = Structures.register(StructureKeys.SHIPWRECK_BEACHED, new ShipwreckStructure(Structures.createConfig(BiomeTags.SHIPWRECK_BEACHED_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), true));
    public static final RegistryEntry<Structure> SWAMP_HUT = Structures.register(StructureKeys.SWAMP_HUT, new SwampHutStructure(Structures.createConfig(BiomeTags.SWAMP_HUT_HAS_STRUCTURE, Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.WITCH, 1, 1, 1)})), SpawnGroup.CREATURE, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.CAT, 1, 1, 1)}))), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.NONE)));
    public static final RegistryEntry<Structure> STRONGHOLD = Structures.register(StructureKeys.STRONGHOLD, new StrongholdStructure(Structures.createConfig(BiomeTags.STRONGHOLD_HAS_STRUCTURE, StructureTerrainAdaptation.BURY)));
    public static final RegistryEntry<Structure> MONUMENT = Structures.register(StructureKeys.MONUMENT, new OceanMonumentStructure(Structures.createConfig(BiomeTags.OCEAN_MONUMENT_HAS_STRUCTURE, Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4)})), SpawnGroup.UNDERGROUND_WATER_CREATURE, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, SpawnSettings.EMPTY_ENTRY_POOL), SpawnGroup.AXOLOTLS, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, SpawnSettings.EMPTY_ENTRY_POOL)), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.NONE)));
    public static final RegistryEntry<Structure> OCEAN_RUIN_COLD = Structures.register(StructureKeys.OCEAN_RUIN_COLD, new OceanRuinStructure(Structures.createConfig(BiomeTags.OCEAN_RUIN_COLD_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), OceanRuinStructure.BiomeTemperature.COLD, 0.3f, 0.9f));
    public static final RegistryEntry<Structure> OCEAN_RUIN_WARM = Structures.register(StructureKeys.OCEAN_RUIN_WARM, new OceanRuinStructure(Structures.createConfig(BiomeTags.OCEAN_RUIN_WARM_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), OceanRuinStructure.BiomeTemperature.WARM, 0.3f, 0.9f));
    public static final RegistryEntry<Structure> FORTRESS = Structures.register(StructureKeys.FORTRESS, new NetherFortressStructure(Structures.createConfig(BiomeTags.NETHER_FORTRESS_HAS_STRUCTURE, Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, NetherFortressStructure.MONSTER_SPAWNS)), GenerationStep.Feature.UNDERGROUND_DECORATION, StructureTerrainAdaptation.NONE)));
    public static final RegistryEntry<Structure> NETHER_FOSSIL = Structures.register(StructureKeys.NETHER_FOSSIL, new NetherFossilStructure(Structures.createConfig(BiomeTags.NETHER_FOSSIL_HAS_STRUCTURE, GenerationStep.Feature.UNDERGROUND_DECORATION, StructureTerrainAdaptation.BEARD_THIN), UniformHeightProvider.create(YOffset.fixed(32), YOffset.belowTop(2))));
    public static final RegistryEntry<Structure> END_CITY = Structures.register(StructureKeys.END_CITY, new EndCityStructure(Structures.createConfig(BiomeTags.END_CITY_HAS_STRUCTURE, StructureTerrainAdaptation.NONE)));
    public static final RegistryEntry<Structure> BURIED_TREASURE = Structures.register(StructureKeys.BURIED_TREASURE, new BuriedTreasureStructure(Structures.createConfig(BiomeTags.BURIED_TREASURE_HAS_STRUCTURE, GenerationStep.Feature.UNDERGROUND_STRUCTURES, StructureTerrainAdaptation.NONE)));
    public static final RegistryEntry<Structure> BASTION_REMNANT = Structures.register(StructureKeys.BASTION_REMNANT, new JigsawStructure(Structures.createConfig(BiomeTags.BASTION_REMNANT_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), BastionRemnantGenerator.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(33)), false));
    public static final RegistryEntry<Structure> VILLAGE_PLAINS = Structures.register(StructureKeys.VILLAGE_PLAINS, new JigsawStructure(Structures.createConfig(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN), PlainsVillageData.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<Structure> VILLAGE_DESERT = Structures.register(StructureKeys.VILLAGE_DESERT, new JigsawStructure(Structures.createConfig(BiomeTags.VILLAGE_DESERT_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN), DesertVillageData.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<Structure> VILLAGE_SAVANNA = Structures.register(StructureKeys.VILLAGE_SAVANNA, new JigsawStructure(Structures.createConfig(BiomeTags.VILLAGE_SAVANNA_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN), SavannaVillageData.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<Structure> VILLAGE_SNOWY = Structures.register(StructureKeys.VILLAGE_SNOWY, new JigsawStructure(Structures.createConfig(BiomeTags.VILLAGE_SNOWY_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN), SnowyVillageData.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<Structure> VILLAGE_TAIGA = Structures.register(StructureKeys.VILLAGE_TAIGA, new JigsawStructure(Structures.createConfig(BiomeTags.VILLAGE_TAIGA_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN), TaigaVillageData.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<Structure> RUINED_PORTAL = Structures.register(StructureKeys.RUINED_PORTAL, new RuinedPortalStructure(Structures.createConfig(BiomeTags.RUINED_PORTAL_STANDARD_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), List.of(new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND, 1.0f, 0.2f, false, false, true, false, 0.5f), new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE, 0.5f, 0.2f, false, false, true, false, 0.5f))));
    public static final RegistryEntry<Structure> RUINED_PORTAL_DESERT = Structures.register(StructureKeys.RUINED_PORTAL_DESERT, new RuinedPortalStructure(Structures.createConfig(BiomeTags.RUINED_PORTAL_DESERT_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED, 0.0f, 0.0f, false, false, false, false, 1.0f)));
    public static final RegistryEntry<Structure> RUINED_PORTAL_JUNGLE = Structures.register(StructureKeys.RUINED_PORTAL_JUNGLE, new RuinedPortalStructure(Structures.createConfig(BiomeTags.RUINED_PORTAL_JUNGLE_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE, 0.5f, 0.8f, true, true, false, false, 1.0f)));
    public static final RegistryEntry<Structure> RUINED_PORTAL_SWAMP = Structures.register(StructureKeys.RUINED_PORTAL_SWAMP, new RuinedPortalStructure(Structures.createConfig(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0f, 0.5f, false, true, false, false, 1.0f)));
    public static final RegistryEntry<Structure> RUINED_PORTAL_MOUNTAIN = Structures.register(StructureKeys.RUINED_PORTAL_MOUNTAIN, new RuinedPortalStructure(Structures.createConfig(BiomeTags.RUINED_PORTAL_MOUNTAIN_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), List.of(new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN, 1.0f, 0.2f, false, false, true, false, 0.5f), new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE, 0.5f, 0.2f, false, false, true, false, 0.5f))));
    public static final RegistryEntry<Structure> RUINED_PORTAL_OCEAN = Structures.register(StructureKeys.RUINED_PORTAL_OCEAN, new RuinedPortalStructure(Structures.createConfig(BiomeTags.RUINED_PORTAL_OCEAN_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0f, 0.8f, false, false, true, false, 1.0f)));
    public static final RegistryEntry<Structure> RUINED_PORTAL_NETHER = Structures.register(StructureKeys.RUINED_PORTAL_NETHER, new RuinedPortalStructure(Structures.createConfig(BiomeTags.RUINED_PORTAL_NETHER_HAS_STRUCTURE, StructureTerrainAdaptation.NONE), new RuinedPortalStructure.Setup(RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER, 0.5f, 0.0f, false, false, false, true, 1.0f)));
    public static final RegistryEntry<Structure> ANCIENT_CITY = Structures.register(StructureKeys.ANCIENT_CITY, new JigsawStructure(Structures.createConfig(BiomeTags.ANCIENT_CITY_HAS_STRUCTURE, Arrays.stream(SpawnGroup.values()).collect(Collectors.toMap(spawnGroup -> spawnGroup, spawnGroup -> new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, Pool.empty()))), GenerationStep.Feature.UNDERGROUND_DECORATION, StructureTerrainAdaptation.BEARD_BOX), AncientCityGenerator.CITY_CENTER, Optional.of(new Identifier("city_anchor")), 7, ConstantHeightProvider.create(YOffset.fixed(-27)), false, Optional.empty(), 116));

    public static RegistryEntry<? extends Structure> getDefault(Registry<Structure> registry) {
        return MINESHAFT;
    }

    private static Structure.Config createConfig(TagKey<Biome> biomeTag, Map<SpawnGroup, StructureSpawns> spawns, GenerationStep.Feature featureStep, StructureTerrainAdaptation terrainAdaptation) {
        return new Structure.Config(Structures.getOrCreateBiomeTag(biomeTag), spawns, featureStep, terrainAdaptation);
    }

    private static Structure.Config createConfig(TagKey<Biome> biomeTag, GenerationStep.Feature featureStep, StructureTerrainAdaptation terrainAdaptation) {
        return Structures.createConfig(biomeTag, Map.of(), featureStep, terrainAdaptation);
    }

    private static Structure.Config createConfig(TagKey<Biome> biomeTag, StructureTerrainAdaptation terrainAdaptation) {
        return Structures.createConfig(biomeTag, Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, terrainAdaptation);
    }

    private static RegistryEntry<Structure> register(RegistryKey<Structure> key, Structure structure) {
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE, key, structure);
    }

    private static RegistryEntryList<Biome> getOrCreateBiomeTag(TagKey<Biome> key) {
        return BuiltinRegistries.BIOME.getOrCreateEntryList(key);
    }
}

