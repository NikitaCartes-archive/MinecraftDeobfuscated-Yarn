/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import java.util.List;
import java.util.Optional;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.StructureFeature;

public interface StructureSets {
    public static final RegistryEntry<StructureSet> VILLAGES = StructureSets.register(StructureSetKeys.VILLAGES, new StructureSet(List.of(StructureSet.createEntry(ConfiguredStructureFeatures.VILLAGE_PLAINS), StructureSet.createEntry(ConfiguredStructureFeatures.VILLAGE_DESERT), StructureSet.createEntry(ConfiguredStructureFeatures.VILLAGE_SAVANNA), StructureSet.createEntry(ConfiguredStructureFeatures.VILLAGE_SNOWY), StructureSet.createEntry(ConfiguredStructureFeatures.VILLAGE_TAIGA)), (StructurePlacement)new RandomSpreadStructurePlacement(34, 8, SpreadType.LINEAR, 10387312)));
    public static final RegistryEntry<StructureSet> DESERT_PYRAMIDS = StructureSets.register(StructureSetKeys.DESERT_PYRAMIDS, ConfiguredStructureFeatures.DESERT_PYRAMID, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357617));
    public static final RegistryEntry<StructureSet> IGLOOS = StructureSets.register(StructureSetKeys.IGLOOS, ConfiguredStructureFeatures.IGLOO, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357618));
    public static final RegistryEntry<StructureSet> JUNGLE_TEMPLES = StructureSets.register(StructureSetKeys.JUNGLE_TEMPLES, ConfiguredStructureFeatures.JUNGLE_PYRAMID, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357619));
    public static final RegistryEntry<StructureSet> SWAMP_HUTS = StructureSets.register(StructureSetKeys.SWAMP_HUTS, ConfiguredStructureFeatures.SWAMP_HUT, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357620));
    public static final RegistryEntry<StructureSet> PILLAGER_OUTPOSTS = StructureSets.register(StructureSetKeys.PILLAGER_OUTPOSTS, ConfiguredStructureFeatures.PILLAGER_OUTPOST, new RandomSpreadStructurePlacement(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_1, 0.2f, 165745296, Optional.of(new StructurePlacement.ExclusionZone(VILLAGES, 10)), 32, 8, SpreadType.LINEAR));
    public static final RegistryEntry<StructureSet> field_38475 = StructureSets.register(StructureSetKeys.ANCIENT_CITIES, ConfiguredStructureFeatures.field_38476, new RandomSpreadStructurePlacement(24, 8, SpreadType.LINEAR, 20083232));
    public static final RegistryEntry<StructureSet> OCEAN_MONUMENTS = StructureSets.register(StructureSetKeys.OCEAN_MONUMENTS, ConfiguredStructureFeatures.MONUMENT, new RandomSpreadStructurePlacement(32, 5, SpreadType.TRIANGULAR, 10387313));
    public static final RegistryEntry<StructureSet> WOODLAND_MANSIONS = StructureSets.register(StructureSetKeys.WOODLAND_MANSIONS, ConfiguredStructureFeatures.MANSION, new RandomSpreadStructurePlacement(80, 20, SpreadType.TRIANGULAR, 10387319));
    public static final RegistryEntry<StructureSet> BURIED_TREASURES = StructureSets.register(StructureSetKeys.BURIED_TREASURES, ConfiguredStructureFeatures.BURIED_TREASURE, new RandomSpreadStructurePlacement(new Vec3i(9, 0, 9), StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_2, 0.01f, 0, Optional.empty(), 1, 0, SpreadType.LINEAR));
    public static final RegistryEntry<StructureSet> MINESHAFTS = StructureSets.register(StructureSetKeys.MINESHAFTS, new StructureSet(List.of(StructureSet.createEntry(ConfiguredStructureFeatures.MINESHAFT), StructureSet.createEntry(ConfiguredStructureFeatures.MINESHAFT_MESA)), (StructurePlacement)new RandomSpreadStructurePlacement(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_3, 0.004f, 0, Optional.empty(), 1, 0, SpreadType.LINEAR)));
    public static final RegistryEntry<StructureSet> RUINED_PORTALS = StructureSets.register(StructureSetKeys.RUINED_PORTALS, new StructureSet(List.of(StructureSet.createEntry(ConfiguredStructureFeatures.RUINED_PORTAL), StructureSet.createEntry(ConfiguredStructureFeatures.RUINED_PORTAL_DESERT), StructureSet.createEntry(ConfiguredStructureFeatures.RUINED_PORTAL_JUNGLE), StructureSet.createEntry(ConfiguredStructureFeatures.RUINED_PORTAL_SWAMP), StructureSet.createEntry(ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN), StructureSet.createEntry(ConfiguredStructureFeatures.RUINED_PORTAL_OCEAN), StructureSet.createEntry(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER)), (StructurePlacement)new RandomSpreadStructurePlacement(40, 15, SpreadType.LINEAR, 34222645)));
    public static final RegistryEntry<StructureSet> SHIPWRECKS = StructureSets.register(StructureSetKeys.SHIPWRECKS, new StructureSet(List.of(StructureSet.createEntry(ConfiguredStructureFeatures.SHIPWRECK), StructureSet.createEntry(ConfiguredStructureFeatures.SHIPWRECK_BEACHED)), (StructurePlacement)new RandomSpreadStructurePlacement(24, 4, SpreadType.LINEAR, 165745295)));
    public static final RegistryEntry<StructureSet> OCEAN_RUINS = StructureSets.register(StructureSetKeys.OCEAN_RUINS, new StructureSet(List.of(StructureSet.createEntry(ConfiguredStructureFeatures.OCEAN_RUIN_COLD), StructureSet.createEntry(ConfiguredStructureFeatures.OCEAN_RUIN_WARM)), (StructurePlacement)new RandomSpreadStructurePlacement(20, 8, SpreadType.LINEAR, 14357621)));
    public static final RegistryEntry<StructureSet> NETHER_COMPLEXES = StructureSets.register(StructureSetKeys.NETHER_COMPLEXES, new StructureSet(List.of(StructureSet.createEntry(ConfiguredStructureFeatures.FORTRESS, 2), StructureSet.createEntry(ConfiguredStructureFeatures.BASTION_REMNANT, 3)), (StructurePlacement)new RandomSpreadStructurePlacement(27, 4, SpreadType.LINEAR, 30084232)));
    public static final RegistryEntry<StructureSet> NETHER_FOSSILS = StructureSets.register(StructureSetKeys.NETHER_FOSSILS, ConfiguredStructureFeatures.NETHER_FOSSIL, new RandomSpreadStructurePlacement(2, 1, SpreadType.LINEAR, 14357921));
    public static final RegistryEntry<StructureSet> END_CITIES = StructureSets.register(StructureSetKeys.END_CITIES, ConfiguredStructureFeatures.END_CITY, new RandomSpreadStructurePlacement(20, 11, SpreadType.TRIANGULAR, 10387313));
    public static final RegistryEntry<StructureSet> STRONGHOLDS = StructureSets.register(StructureSetKeys.STRONGHOLDS, ConfiguredStructureFeatures.STRONGHOLD, new ConcentricRingsStructurePlacement(32, 3, 128, BuiltinRegistries.BIOME.getOrCreateEntryList(BiomeTags.STRONGHOLD_BIASED_TO)));

    public static RegistryEntry<StructureSet> initAndGetDefault() {
        return (RegistryEntry)BuiltinRegistries.STRUCTURE_SET.streamEntries().iterator().next();
    }

    public static RegistryEntry<StructureSet> register(RegistryKey<StructureSet> key, StructureSet structureSet) {
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_SET, key, structureSet);
    }

    public static RegistryEntry<StructureSet> register(RegistryKey<StructureSet> key, RegistryEntry<StructureFeature> configuredStructureFigure, StructurePlacement placement) {
        return StructureSets.register(key, new StructureSet(configuredStructureFigure, placement));
    }
}

