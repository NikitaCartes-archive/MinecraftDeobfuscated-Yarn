/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public interface StructureKeys {
    public static final RegistryKey<Structure> PILLAGER_OUTPOST = StructureKeys.of("pillager_outpost");
    public static final RegistryKey<Structure> MINESHAFT = StructureKeys.of("mineshaft");
    public static final RegistryKey<Structure> MINESHAFT_MESA = StructureKeys.of("mineshaft_mesa");
    public static final RegistryKey<Structure> MANSION = StructureKeys.of("mansion");
    public static final RegistryKey<Structure> JUNGLE_PYRAMID = StructureKeys.of("jungle_pyramid");
    public static final RegistryKey<Structure> DESERT_PYRAMID = StructureKeys.of("desert_pyramid");
    public static final RegistryKey<Structure> IGLOO = StructureKeys.of("igloo");
    public static final RegistryKey<Structure> SHIPWRECK = StructureKeys.of("shipwreck");
    public static final RegistryKey<Structure> SHIPWRECK_BEACHED = StructureKeys.of("shipwreck_beached");
    public static final RegistryKey<Structure> SWAMP_HUT = StructureKeys.of("swamp_hut");
    public static final RegistryKey<Structure> STRONGHOLD = StructureKeys.of("stronghold");
    public static final RegistryKey<Structure> MONUMENT = StructureKeys.of("monument");
    public static final RegistryKey<Structure> OCEAN_RUIN_COLD = StructureKeys.of("ocean_ruin_cold");
    public static final RegistryKey<Structure> OCEAN_RUIN_WARM = StructureKeys.of("ocean_ruin_warm");
    public static final RegistryKey<Structure> FORTRESS = StructureKeys.of("fortress");
    public static final RegistryKey<Structure> NETHER_FOSSIL = StructureKeys.of("nether_fossil");
    public static final RegistryKey<Structure> END_CITY = StructureKeys.of("end_city");
    public static final RegistryKey<Structure> BURIED_TREASURE = StructureKeys.of("buried_treasure");
    public static final RegistryKey<Structure> BASTION_REMNANT = StructureKeys.of("bastion_remnant");
    public static final RegistryKey<Structure> VILLAGE_PLAINS = StructureKeys.of("village_plains");
    public static final RegistryKey<Structure> VILLAGE_DESERT = StructureKeys.of("village_desert");
    public static final RegistryKey<Structure> VILLAGE_SAVANNA = StructureKeys.of("village_savanna");
    public static final RegistryKey<Structure> VILLAGE_SNOWY = StructureKeys.of("village_snowy");
    public static final RegistryKey<Structure> VILLAGE_TAIGA = StructureKeys.of("village_taiga");
    public static final RegistryKey<Structure> RUINED_PORTAL = StructureKeys.of("ruined_portal");
    public static final RegistryKey<Structure> RUINED_PORTAL_DESERT = StructureKeys.of("ruined_portal_desert");
    public static final RegistryKey<Structure> RUINED_PORTAL_JUNGLE = StructureKeys.of("ruined_portal_jungle");
    public static final RegistryKey<Structure> RUINED_PORTAL_SWAMP = StructureKeys.of("ruined_portal_swamp");
    public static final RegistryKey<Structure> RUINED_PORTAL_MOUNTAIN = StructureKeys.of("ruined_portal_mountain");
    public static final RegistryKey<Structure> RUINED_PORTAL_OCEAN = StructureKeys.of("ruined_portal_ocean");
    public static final RegistryKey<Structure> RUINED_PORTAL_NETHER = StructureKeys.of("ruined_portal_nether");
    public static final RegistryKey<Structure> ANCIENT_CITY = StructureKeys.of("ancient_city");

    private static RegistryKey<Structure> of(String id) {
        return RegistryKey.of(RegistryKeys.STRUCTURE, new Identifier(id));
    }
}

