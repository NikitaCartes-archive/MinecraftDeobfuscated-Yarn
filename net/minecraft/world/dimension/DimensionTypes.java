/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public class DimensionTypes {
    public static final RegistryKey<DimensionType> OVERWORLD = DimensionTypes.of("overworld");
    public static final RegistryKey<DimensionType> THE_NETHER = DimensionTypes.of("the_nether");
    public static final RegistryKey<DimensionType> THE_END = DimensionTypes.of("the_end");
    public static final RegistryKey<DimensionType> OVERWORLD_CAVES = DimensionTypes.of("overworld_caves");
    public static final Identifier OVERWORLD_ID = new Identifier("overworld");
    public static final Identifier THE_NETHER_ID = new Identifier("the_nether");
    public static final Identifier THE_END_ID = new Identifier("the_end");

    private static RegistryKey<DimensionType> of(String id) {
        return RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(id));
    }
}

