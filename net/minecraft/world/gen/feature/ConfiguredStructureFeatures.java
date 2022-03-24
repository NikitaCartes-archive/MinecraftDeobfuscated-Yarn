/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.Map;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
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
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.BuriedTreasureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatureKeys;
import net.minecraft.world.gen.feature.DesertPyramidFeature;
import net.minecraft.world.gen.feature.EndCityFeature;
import net.minecraft.world.gen.feature.IglooFeature;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.JungleTempleFeature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.NetherFossilFeature;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.ShipwreckFeature;
import net.minecraft.world.gen.feature.StrongholdFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.SwampHutFeature;
import net.minecraft.world.gen.feature.WoodlandMansionFeature;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredStructureFeatures {
    public static final RegistryEntry<StructureFeature> PILLAGER_OUTPOST = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.PILLAGER_OUTPOST, new JigsawFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.PILLAGER_OUTPOST_HAS_STRUCTURE), Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 1)}))), GenerationStep.Feature.SURFACE_STRUCTURES, true, PillagerOutpostGenerator.STRUCTURE_POOLS, 7, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<StructureFeature> MINESHAFT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.MINESHAFT, new MineshaftFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.MINESHAFT_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.UNDERGROUND_STRUCTURES, false, MineshaftFeature.Type.NORMAL));
    public static final RegistryEntry<StructureFeature> MINESHAFT_MESA = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.MINESHAFT_MESA, new MineshaftFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.MINESHAFT_MESA_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.UNDERGROUND_STRUCTURES, false, MineshaftFeature.Type.MESA));
    public static final RegistryEntry<StructureFeature> MANSION = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.MANSION, new WoodlandMansionFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.WOODLAND_MANSION_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false));
    public static final RegistryEntry<StructureFeature> JUNGLE_PYRAMID = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.JUNGLE_PYRAMID, new JungleTempleFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.JUNGLE_TEMPLE_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false));
    public static final RegistryEntry<StructureFeature> DESERT_PYRAMID = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.DESERT_PYRAMID, new DesertPyramidFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false));
    public static final RegistryEntry<StructureFeature> IGLOO = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.IGLOO, new IglooFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.IGLOO_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false));
    public static final RegistryEntry<StructureFeature> SHIPWRECK = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.SHIPWRECK, new ShipwreckFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.SHIPWRECK_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, false));
    public static final RegistryEntry<StructureFeature> SHIPWRECK_BEACHED = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.SHIPWRECK_BEACHED, new ShipwreckFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.SHIPWRECK_BEACHED_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, true));
    public static final RegistryEntry<StructureFeature> SWAMP_HUT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.SWAMP_HUT, new SwampHutFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.SWAMP_HUT_HAS_STRUCTURE), Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.WITCH, 1, 1, 1)})), SpawnGroup.CREATURE, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.CAT, 1, 1, 1)}))), GenerationStep.Feature.SURFACE_STRUCTURES, false));
    public static final RegistryEntry<StructureFeature> STRONGHOLD = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.STRONGHOLD, new StrongholdFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.STRONGHOLD_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.STRONGHOLDS, true));
    public static final RegistryEntry<StructureFeature> MONUMENT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.MONUMENT, new OceanMonumentFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.OCEAN_MONUMENT_HAS_STRUCTURE), Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, Pool.of((Weighted[])new SpawnSettings.SpawnEntry[]{new SpawnSettings.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4)})), SpawnGroup.UNDERGROUND_WATER_CREATURE, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, SpawnSettings.EMPTY_ENTRY_POOL), SpawnGroup.AXOLOTLS, new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE, SpawnSettings.EMPTY_ENTRY_POOL)), GenerationStep.Feature.SURFACE_STRUCTURES, false));
    public static final RegistryEntry<StructureFeature> OCEAN_RUIN_COLD = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.OCEAN_RUIN_COLD, new OceanRuinFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.OCEAN_RUIN_COLD_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, OceanRuinFeature.BiomeType.COLD, 0.3f, 0.9f));
    public static final RegistryEntry<StructureFeature> OCEAN_RUIN_WARM = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.OCEAN_RUIN_WARM, new OceanRuinFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.OCEAN_RUIN_WARM_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, OceanRuinFeature.BiomeType.WARM, 0.3f, 0.9f));
    public static final RegistryEntry<StructureFeature> FORTRESS = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.FORTRESS, new NetherFortressFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.NETHER_FORTRESS_HAS_STRUCTURE), Map.of(SpawnGroup.MONSTER, new StructureSpawns(StructureSpawns.BoundingBox.PIECE, NetherFortressFeature.MONSTER_SPAWNS)), GenerationStep.Feature.UNDERGROUND_DECORATION, false));
    public static final RegistryEntry<StructureFeature> NETHER_FOSSIL = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.NETHER_FOSSIL, new NetherFossilFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.NETHER_FOSSIL_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.UNDERGROUND_DECORATION, true, UniformHeightProvider.create(YOffset.fixed(32), YOffset.belowTop(2))));
    public static final RegistryEntry<StructureFeature> END_CITY = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.END_CITY, new EndCityFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.END_CITY_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false));
    public static final RegistryEntry<StructureFeature> BURIED_TREASURE = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.BURIED_TREASURE, new BuriedTreasureFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.BURIED_TREASURE_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.UNDERGROUND_STRUCTURES, false));
    public static final RegistryEntry<StructureFeature> BASTION_REMNANT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.BASTION_REMNANT, new JigsawFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.BASTION_REMNANT_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, BastionRemnantGenerator.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(33)), false));
    public static final RegistryEntry<StructureFeature> VILLAGE_PLAINS = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.VILLAGE_PLAINS, new JigsawFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, true, PlainsVillageData.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<StructureFeature> VILLAGE_DESERT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.VILLAGE_DESERT, new JigsawFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.VILLAGE_DESERT_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, true, DesertVillageData.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<StructureFeature> VILLAGE_SAVANNA = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.VILLAGE_SAVANNA, new JigsawFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.VILLAGE_SAVANNA_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, true, SavannaVillageData.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<StructureFeature> VILLAGE_SNOWY = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.VILLAGE_SNOWY, new JigsawFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.VILLAGE_SNOWY_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, true, SnowyVillageData.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<StructureFeature> VILLAGE_TAIGA = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.VILLAGE_TAIGA, new JigsawFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.VILLAGE_TAIGA_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, true, TaigaVillageData.STRUCTURE_POOLS, 6, ConstantHeightProvider.create(YOffset.fixed(0)), true, Heightmap.Type.WORLD_SURFACE_WG));
    public static final RegistryEntry<StructureFeature> RUINED_PORTAL = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL, new RuinedPortalFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.RUINED_PORTAL_STANDARD_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, List.of(new RuinedPortalFeature.class_7155(RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND, 1.0f, 0.2f, false, false, true, false, 0.5f), new RuinedPortalFeature.class_7155(RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE, 0.5f, 0.2f, false, false, true, false, 0.5f))));
    public static final RegistryEntry<StructureFeature> RUINED_PORTAL_DESERT = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_DESERT, new RuinedPortalFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.RUINED_PORTAL_DESERT_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, new RuinedPortalFeature.class_7155(RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED, 0.0f, 0.0f, false, false, false, false, 1.0f)));
    public static final RegistryEntry<StructureFeature> RUINED_PORTAL_JUNGLE = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_JUNGLE, new RuinedPortalFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.RUINED_PORTAL_JUNGLE_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, new RuinedPortalFeature.class_7155(RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE, 0.5f, 0.8f, true, true, false, false, 1.0f)));
    public static final RegistryEntry<StructureFeature> RUINED_PORTAL_SWAMP = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_SWAMP, new RuinedPortalFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.RUINED_PORTAL_SWAMP_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, new RuinedPortalFeature.class_7155(RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0f, 0.5f, false, true, false, false, 1.0f)));
    public static final RegistryEntry<StructureFeature> RUINED_PORTAL_MOUNTAIN = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_MOUNTAIN, new RuinedPortalFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.RUINED_PORTAL_MOUNTAIN_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, List.of(new RuinedPortalFeature.class_7155(RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN, 1.0f, 0.2f, false, false, true, false, 0.5f), new RuinedPortalFeature.class_7155(RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE, 0.5f, 0.2f, false, false, true, false, 0.5f))));
    public static final RegistryEntry<StructureFeature> RUINED_PORTAL_OCEAN = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_OCEAN, new RuinedPortalFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.RUINED_PORTAL_OCEAN_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, new RuinedPortalFeature.class_7155(RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0f, 0.8f, false, false, true, false, 1.0f)));
    public static final RegistryEntry<StructureFeature> RUINED_PORTAL_NETHER = ConfiguredStructureFeatures.register(ConfiguredStructureFeatureKeys.RUINED_PORTAL_NETHER, new RuinedPortalFeature(ConfiguredStructureFeatures.method_42044(BiomeTags.RUINED_PORTAL_NETHER_HAS_STRUCTURE), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, false, new RuinedPortalFeature.class_7155(RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER, 0.5f, 0.0f, false, false, false, true, 1.0f)));

    public static RegistryEntry<? extends StructureFeature> getDefault() {
        return MINESHAFT;
    }

    private static RegistryEntry<StructureFeature> register(RegistryKey<StructureFeature> key, StructureFeature configuredStructureFeature) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, key, configuredStructureFeature);
    }

    private static RegistryEntryList<Biome> method_42044(TagKey<Biome> tagKey) {
        return BuiltinRegistries.BIOME.getOrCreateEntryList(tagKey);
    }
}

