/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.structure.StructureType;

public interface StructureTypeKeys {
    public static final RegistryKey<StructureType> PILLAGER_OUTPOST = StructureTypeKeys.of("pillager_outpost");
    public static final RegistryKey<StructureType> MINESHAFT = StructureTypeKeys.of("mineshaft");
    public static final RegistryKey<StructureType> MINESHAFT_MESA = StructureTypeKeys.of("mineshaft_mesa");
    public static final RegistryKey<StructureType> MANSION = StructureTypeKeys.of("mansion");
    public static final RegistryKey<StructureType> JUNGLE_PYRAMID = StructureTypeKeys.of("jungle_pyramid");
    public static final RegistryKey<StructureType> DESERT_PYRAMID = StructureTypeKeys.of("desert_pyramid");
    public static final RegistryKey<StructureType> IGLOO = StructureTypeKeys.of("igloo");
    public static final RegistryKey<StructureType> SHIPWRECK = StructureTypeKeys.of("shipwreck");
    public static final RegistryKey<StructureType> SHIPWRECK_BEACHED = StructureTypeKeys.of("shipwreck_beached");
    public static final RegistryKey<StructureType> SWAMP_HUT = StructureTypeKeys.of("swamp_hut");
    public static final RegistryKey<StructureType> STRONGHOLD = StructureTypeKeys.of("stronghold");
    public static final RegistryKey<StructureType> MONUMENT = StructureTypeKeys.of("monument");
    public static final RegistryKey<StructureType> OCEAN_RUIN_COLD = StructureTypeKeys.of("ocean_ruin_cold");
    public static final RegistryKey<StructureType> OCEAN_RUIN_WARM = StructureTypeKeys.of("ocean_ruin_warm");
    public static final RegistryKey<StructureType> FORTRESS = StructureTypeKeys.of("fortress");
    public static final RegistryKey<StructureType> NETHER_FOSSIL = StructureTypeKeys.of("nether_fossil");
    public static final RegistryKey<StructureType> END_CITY = StructureTypeKeys.of("end_city");
    public static final RegistryKey<StructureType> BURIED_TREASURE = StructureTypeKeys.of("buried_treasure");
    public static final RegistryKey<StructureType> BASTION_REMNANT = StructureTypeKeys.of("bastion_remnant");
    public static final RegistryKey<StructureType> VILLAGE_PLAINS = StructureTypeKeys.of("village_plains");
    public static final RegistryKey<StructureType> VILLAGE_DESERT = StructureTypeKeys.of("village_desert");
    public static final RegistryKey<StructureType> VILLAGE_SAVANNA = StructureTypeKeys.of("village_savanna");
    public static final RegistryKey<StructureType> VILLAGE_SNOWY = StructureTypeKeys.of("village_snowy");
    public static final RegistryKey<StructureType> VILLAGE_TAIGA = StructureTypeKeys.of("village_taiga");
    public static final RegistryKey<StructureType> RUINED_PORTAL = StructureTypeKeys.of("ruined_portal");
    public static final RegistryKey<StructureType> RUINED_PORTAL_DESERT = StructureTypeKeys.of("ruined_portal_desert");
    public static final RegistryKey<StructureType> RUINED_PORTAL_JUNGLE = StructureTypeKeys.of("ruined_portal_jungle");
    public static final RegistryKey<StructureType> RUINED_PORTAL_SWAMP = StructureTypeKeys.of("ruined_portal_swamp");
    public static final RegistryKey<StructureType> RUINED_PORTAL_MOUNTAIN = StructureTypeKeys.of("ruined_portal_mountain");
    public static final RegistryKey<StructureType> RUINED_PORTAL_OCEAN = StructureTypeKeys.of("ruined_portal_ocean");
    public static final RegistryKey<StructureType> RUINED_PORTAL_NETHER = StructureTypeKeys.of("ruined_portal_nether");
    public static final RegistryKey<StructureType> ANCIENT_CITY = StructureTypeKeys.of("ancient_city");

    private static RegistryKey<StructureType> of(String id) {
        return RegistryKey.of(Registry.STRUCTURE_KEY, new Identifier(id));
    }
}

