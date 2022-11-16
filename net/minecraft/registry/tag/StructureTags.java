/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public interface StructureTags {
    public static final TagKey<Structure> EYE_OF_ENDER_LOCATED = StructureTags.of("eye_of_ender_located");
    public static final TagKey<Structure> DOLPHIN_LOCATED = StructureTags.of("dolphin_located");
    public static final TagKey<Structure> ON_WOODLAND_EXPLORER_MAPS = StructureTags.of("on_woodland_explorer_maps");
    public static final TagKey<Structure> ON_OCEAN_EXPLORER_MAPS = StructureTags.of("on_ocean_explorer_maps");
    public static final TagKey<Structure> ON_TREASURE_MAPS = StructureTags.of("on_treasure_maps");
    public static final TagKey<Structure> CATS_SPAWN_IN = StructureTags.of("cats_spawn_in");
    public static final TagKey<Structure> CATS_SPAWN_AS_BLACK = StructureTags.of("cats_spawn_as_black");
    public static final TagKey<Structure> VILLAGE = StructureTags.of("village");
    public static final TagKey<Structure> MINESHAFT = StructureTags.of("mineshaft");
    public static final TagKey<Structure> SHIPWRECK = StructureTags.of("shipwreck");
    public static final TagKey<Structure> RUINED_PORTAL = StructureTags.of("ruined_portal");
    public static final TagKey<Structure> OCEAN_RUIN = StructureTags.of("ocean_ruin");

    private static TagKey<Structure> of(String id) {
        return TagKey.of(RegistryKeys.STRUCTURE, new Identifier(id));
    }
}

