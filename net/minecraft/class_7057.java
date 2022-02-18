/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.class_7059;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface class_7057 {
    public static final RegistryKey<class_7059> VILLAGES = class_7057.method_41141("villages");
    public static final RegistryKey<class_7059> DESERT_PYRAMIDS = class_7057.method_41141("desert_pyramids");
    public static final RegistryKey<class_7059> IGLOOS = class_7057.method_41141("igloos");
    public static final RegistryKey<class_7059> JUNGLE_TEMPLES = class_7057.method_41141("jungle_temples");
    public static final RegistryKey<class_7059> SWAMP_HUTS = class_7057.method_41141("swamp_huts");
    public static final RegistryKey<class_7059> PILLAGER_OUTPOSTS = class_7057.method_41141("pillager_outposts");
    public static final RegistryKey<class_7059> OCEAN_MONUMENTS = class_7057.method_41141("ocean_monuments");
    public static final RegistryKey<class_7059> WOODLAND_MANSIONS = class_7057.method_41141("woodland_mansions");
    public static final RegistryKey<class_7059> BURIED_TREASURES = class_7057.method_41141("buried_treasures");
    public static final RegistryKey<class_7059> MINESHAFTS = class_7057.method_41141("mineshafts");
    public static final RegistryKey<class_7059> RUINED_PORTALS = class_7057.method_41141("ruined_portals");
    public static final RegistryKey<class_7059> SHIPWRECKS = class_7057.method_41141("shipwrecks");
    public static final RegistryKey<class_7059> OCEAN_RUINS = class_7057.method_41141("ocean_ruins");
    public static final RegistryKey<class_7059> NETHER_COMPLEXES = class_7057.method_41141("nether_complexes");
    public static final RegistryKey<class_7059> NETHER_FOSSILS = class_7057.method_41141("nether_fossils");
    public static final RegistryKey<class_7059> END_CITIES = class_7057.method_41141("end_cities");
    public static final RegistryKey<class_7059> STRONGHOLDS = class_7057.method_41141("strongholds");

    private static RegistryKey<class_7059> method_41141(String string) {
        return RegistryKey.of(Registry.STRUCTURE_SET_WORLDGEN, new Identifier(string));
    }
}

