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
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.Structures;

public interface StructureSets {
    public static final RegistryEntry<StructureSet> VILLAGES = StructureSets.register(StructureSetKeys.VILLAGES, new StructureSet(List.of(StructureSet.createEntry(Structures.VILLAGE_PLAINS), StructureSet.createEntry(Structures.VILLAGE_DESERT), StructureSet.createEntry(Structures.VILLAGE_SAVANNA), StructureSet.createEntry(Structures.VILLAGE_SNOWY), StructureSet.createEntry(Structures.VILLAGE_TAIGA)), (StructurePlacement)new RandomSpreadStructurePlacement(34, 8, SpreadType.LINEAR, 10387312)));
    public static final RegistryEntry<StructureSet> DESERT_PYRAMIDS = StructureSets.register(StructureSetKeys.DESERT_PYRAMIDS, Structures.DESERT_PYRAMID, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357617));
    public static final RegistryEntry<StructureSet> IGLOOS = StructureSets.register(StructureSetKeys.IGLOOS, Structures.IGLOO, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357618));
    public static final RegistryEntry<StructureSet> JUNGLE_TEMPLES = StructureSets.register(StructureSetKeys.JUNGLE_TEMPLES, Structures.JUNGLE_PYRAMID, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357619));
    public static final RegistryEntry<StructureSet> SWAMP_HUTS = StructureSets.register(StructureSetKeys.SWAMP_HUTS, Structures.SWAMP_HUT, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 14357620));
    public static final RegistryEntry<StructureSet> PILLAGER_OUTPOSTS = StructureSets.register(StructureSetKeys.PILLAGER_OUTPOSTS, Structures.PILLAGER_OUTPOST, new RandomSpreadStructurePlacement(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_1, 0.2f, 165745296, Optional.of(new StructurePlacement.ExclusionZone(VILLAGES, 10)), 32, 8, SpreadType.LINEAR));
    public static final RegistryEntry<StructureSet> ANCIENT_CITIES = StructureSets.register(StructureSetKeys.ANCIENT_CITIES, Structures.ANCIENT_CITY, new RandomSpreadStructurePlacement(24, 8, SpreadType.LINEAR, 20083232));
    public static final RegistryEntry<StructureSet> OCEAN_MONUMENTS = StructureSets.register(StructureSetKeys.OCEAN_MONUMENTS, Structures.MONUMENT, new RandomSpreadStructurePlacement(32, 5, SpreadType.TRIANGULAR, 10387313));
    public static final RegistryEntry<StructureSet> WOODLAND_MANSIONS = StructureSets.register(StructureSetKeys.WOODLAND_MANSIONS, Structures.MANSION, new RandomSpreadStructurePlacement(80, 20, SpreadType.TRIANGULAR, 10387319));
    public static final RegistryEntry<StructureSet> BURIED_TREASURES = StructureSets.register(StructureSetKeys.BURIED_TREASURES, Structures.BURIED_TREASURE, new RandomSpreadStructurePlacement(new Vec3i(9, 0, 9), StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_2, 0.01f, 0, Optional.empty(), 1, 0, SpreadType.LINEAR));
    public static final RegistryEntry<StructureSet> MINESHAFTS = StructureSets.register(StructureSetKeys.MINESHAFTS, new StructureSet(List.of(StructureSet.createEntry(Structures.MINESHAFT), StructureSet.createEntry(Structures.MINESHAFT_MESA)), (StructurePlacement)new RandomSpreadStructurePlacement(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_3, 0.004f, 0, Optional.empty(), 1, 0, SpreadType.LINEAR)));
    public static final RegistryEntry<StructureSet> RUINED_PORTALS = StructureSets.register(StructureSetKeys.RUINED_PORTALS, new StructureSet(List.of(StructureSet.createEntry(Structures.RUINED_PORTAL), StructureSet.createEntry(Structures.RUINED_PORTAL_DESERT), StructureSet.createEntry(Structures.RUINED_PORTAL_JUNGLE), StructureSet.createEntry(Structures.RUINED_PORTAL_SWAMP), StructureSet.createEntry(Structures.RUINED_PORTAL_MOUNTAIN), StructureSet.createEntry(Structures.RUINED_PORTAL_OCEAN), StructureSet.createEntry(Structures.RUINED_PORTAL_NETHER)), (StructurePlacement)new RandomSpreadStructurePlacement(40, 15, SpreadType.LINEAR, 34222645)));
    public static final RegistryEntry<StructureSet> SHIPWRECKS = StructureSets.register(StructureSetKeys.SHIPWRECKS, new StructureSet(List.of(StructureSet.createEntry(Structures.SHIPWRECK), StructureSet.createEntry(Structures.SHIPWRECK_BEACHED)), (StructurePlacement)new RandomSpreadStructurePlacement(24, 4, SpreadType.LINEAR, 165745295)));
    public static final RegistryEntry<StructureSet> OCEAN_RUINS = StructureSets.register(StructureSetKeys.OCEAN_RUINS, new StructureSet(List.of(StructureSet.createEntry(Structures.OCEAN_RUIN_COLD), StructureSet.createEntry(Structures.OCEAN_RUIN_WARM)), (StructurePlacement)new RandomSpreadStructurePlacement(20, 8, SpreadType.LINEAR, 14357621)));
    public static final RegistryEntry<StructureSet> NETHER_COMPLEXES = StructureSets.register(StructureSetKeys.NETHER_COMPLEXES, new StructureSet(List.of(StructureSet.createEntry(Structures.FORTRESS, 2), StructureSet.createEntry(Structures.BASTION_REMNANT, 3)), (StructurePlacement)new RandomSpreadStructurePlacement(27, 4, SpreadType.LINEAR, 30084232)));
    public static final RegistryEntry<StructureSet> NETHER_FOSSILS = StructureSets.register(StructureSetKeys.NETHER_FOSSILS, Structures.NETHER_FOSSIL, new RandomSpreadStructurePlacement(2, 1, SpreadType.LINEAR, 14357921));
    public static final RegistryEntry<StructureSet> END_CITIES = StructureSets.register(StructureSetKeys.END_CITIES, Structures.END_CITY, new RandomSpreadStructurePlacement(20, 11, SpreadType.TRIANGULAR, 10387313));
    public static final RegistryEntry<StructureSet> STRONGHOLDS = StructureSets.register(StructureSetKeys.STRONGHOLDS, Structures.STRONGHOLD, new ConcentricRingsStructurePlacement(32, 3, 128, BuiltinRegistries.BIOME.getOrCreateEntryList(BiomeTags.STRONGHOLD_BIASED_TO)));

    public static RegistryEntry<StructureSet> initAndGetDefault(Registry<StructureSet> registry) {
        return (RegistryEntry)registry.streamEntries().iterator().next();
    }

    public static RegistryEntry<StructureSet> register(RegistryKey<StructureSet> key, StructureSet structureSet) {
        return BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_SET, key, structureSet);
    }

    public static RegistryEntry<StructureSet> register(RegistryKey<StructureSet> key, RegistryEntry<Structure> structure, StructurePlacement placement) {
        return StructureSets.register(key, new StructureSet(structure, placement));
    }
}

