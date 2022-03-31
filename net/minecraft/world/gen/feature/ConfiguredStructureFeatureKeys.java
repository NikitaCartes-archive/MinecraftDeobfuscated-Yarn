/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.StructureFeature;

public interface ConfiguredStructureFeatureKeys {
    public static final RegistryKey<StructureFeature> PILLAGER_OUTPOST = ConfiguredStructureFeatureKeys.of("pillager_outpost");
    public static final RegistryKey<StructureFeature> MINESHAFT = ConfiguredStructureFeatureKeys.of("mineshaft");
    public static final RegistryKey<StructureFeature> MINESHAFT_MESA = ConfiguredStructureFeatureKeys.of("mineshaft_mesa");
    public static final RegistryKey<StructureFeature> MANSION = ConfiguredStructureFeatureKeys.of("mansion");
    public static final RegistryKey<StructureFeature> JUNGLE_PYRAMID = ConfiguredStructureFeatureKeys.of("jungle_pyramid");
    public static final RegistryKey<StructureFeature> DESERT_PYRAMID = ConfiguredStructureFeatureKeys.of("desert_pyramid");
    public static final RegistryKey<StructureFeature> IGLOO = ConfiguredStructureFeatureKeys.of("igloo");
    public static final RegistryKey<StructureFeature> SHIPWRECK = ConfiguredStructureFeatureKeys.of("shipwreck");
    public static final RegistryKey<StructureFeature> SHIPWRECK_BEACHED = ConfiguredStructureFeatureKeys.of("shipwreck_beached");
    public static final RegistryKey<StructureFeature> SWAMP_HUT = ConfiguredStructureFeatureKeys.of("swamp_hut");
    public static final RegistryKey<StructureFeature> STRONGHOLD = ConfiguredStructureFeatureKeys.of("stronghold");
    public static final RegistryKey<StructureFeature> MONUMENT = ConfiguredStructureFeatureKeys.of("monument");
    public static final RegistryKey<StructureFeature> OCEAN_RUIN_COLD = ConfiguredStructureFeatureKeys.of("ocean_ruin_cold");
    public static final RegistryKey<StructureFeature> OCEAN_RUIN_WARM = ConfiguredStructureFeatureKeys.of("ocean_ruin_warm");
    public static final RegistryKey<StructureFeature> FORTRESS = ConfiguredStructureFeatureKeys.of("fortress");
    public static final RegistryKey<StructureFeature> NETHER_FOSSIL = ConfiguredStructureFeatureKeys.of("nether_fossil");
    public static final RegistryKey<StructureFeature> END_CITY = ConfiguredStructureFeatureKeys.of("end_city");
    public static final RegistryKey<StructureFeature> BURIED_TREASURE = ConfiguredStructureFeatureKeys.of("buried_treasure");
    public static final RegistryKey<StructureFeature> BASTION_REMNANT = ConfiguredStructureFeatureKeys.of("bastion_remnant");
    public static final RegistryKey<StructureFeature> VILLAGE_PLAINS = ConfiguredStructureFeatureKeys.of("village_plains");
    public static final RegistryKey<StructureFeature> VILLAGE_DESERT = ConfiguredStructureFeatureKeys.of("village_desert");
    public static final RegistryKey<StructureFeature> VILLAGE_SAVANNA = ConfiguredStructureFeatureKeys.of("village_savanna");
    public static final RegistryKey<StructureFeature> VILLAGE_SNOWY = ConfiguredStructureFeatureKeys.of("village_snowy");
    public static final RegistryKey<StructureFeature> VILLAGE_TAIGA = ConfiguredStructureFeatureKeys.of("village_taiga");
    public static final RegistryKey<StructureFeature> RUINED_PORTAL = ConfiguredStructureFeatureKeys.of("ruined_portal");
    public static final RegistryKey<StructureFeature> RUINED_PORTAL_DESERT = ConfiguredStructureFeatureKeys.of("ruined_portal_desert");
    public static final RegistryKey<StructureFeature> RUINED_PORTAL_JUNGLE = ConfiguredStructureFeatureKeys.of("ruined_portal_jungle");
    public static final RegistryKey<StructureFeature> RUINED_PORTAL_SWAMP = ConfiguredStructureFeatureKeys.of("ruined_portal_swamp");
    public static final RegistryKey<StructureFeature> RUINED_PORTAL_MOUNTAIN = ConfiguredStructureFeatureKeys.of("ruined_portal_mountain");
    public static final RegistryKey<StructureFeature> RUINED_PORTAL_OCEAN = ConfiguredStructureFeatureKeys.of("ruined_portal_ocean");
    public static final RegistryKey<StructureFeature> RUINED_PORTAL_NETHER = ConfiguredStructureFeatureKeys.of("ruined_portal_nether");
    public static final RegistryKey<StructureFeature> ANCIENT_CITY = ConfiguredStructureFeatureKeys.of("ancient_city");

    private static RegistryKey<StructureFeature> of(String id) {
        return RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(id));
    }
}

