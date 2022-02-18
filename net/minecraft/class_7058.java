/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public interface class_7058 {
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> PILLAGER_OUTPOST = class_7058.method_41142("pillager_outpost");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> MINESHAFT = class_7058.method_41142("mineshaft");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> MINESHAFT_MESA = class_7058.method_41142("mineshaft_mesa");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> MANSION = class_7058.method_41142("mansion");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> JUNGLE_PYRAMID = class_7058.method_41142("jungle_pyramid");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> DESERT_PYRAMID = class_7058.method_41142("desert_pyramid");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> IGLOO = class_7058.method_41142("igloo");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK = class_7058.method_41142("shipwreck");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK_BEACHED = class_7058.method_41142("shipwreck_beached");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> SWAMP_HUT = class_7058.method_41142("swamp_hut");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> STRONGHOLD = class_7058.method_41142("stronghold");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> MONUMENT = class_7058.method_41142("monument");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_COLD = class_7058.method_41142("ocean_ruin_cold");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_WARM = class_7058.method_41142("ocean_ruin_warm");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> FORTRESS = class_7058.method_41142("fortress");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> NETHER_FOSSIL = class_7058.method_41142("nether_fossil");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> END_CITY = class_7058.method_41142("end_city");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> BURIED_TREASURE = class_7058.method_41142("buried_treasure");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> BASTION_REMNANT = class_7058.method_41142("bastion_remnant");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_PLAINS = class_7058.method_41142("village_plains");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_DESERT = class_7058.method_41142("village_desert");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_SAVANNA = class_7058.method_41142("village_savanna");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_SNOWY = class_7058.method_41142("village_snowy");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> VILLAGE_TAIGA = class_7058.method_41142("village_taiga");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL = class_7058.method_41142("ruined_portal");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_DESERT = class_7058.method_41142("ruined_portal_desert");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_JUNGLE = class_7058.method_41142("ruined_portal_jungle");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_SWAMP = class_7058.method_41142("ruined_portal_swamp");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_MOUNTAIN = class_7058.method_41142("ruined_portal_mountain");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_OCEAN = class_7058.method_41142("ruined_portal_ocean");
    public static final RegistryKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_NETHER = class_7058.method_41142("ruined_portal_nether");

    private static RegistryKey<ConfiguredStructureFeature<?, ?>> method_41142(String string) {
        return RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(string));
    }
}

