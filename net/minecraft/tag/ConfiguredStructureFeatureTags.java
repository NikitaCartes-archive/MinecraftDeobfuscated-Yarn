/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.StructureType;

public interface ConfiguredStructureFeatureTags {
    public static final TagKey<StructureType> EYE_OF_ENDER_LOCATED = ConfiguredStructureFeatureTags.of("eye_of_ender_located");
    public static final TagKey<StructureType> DOLPHIN_LOCATED = ConfiguredStructureFeatureTags.of("dolphin_located");
    public static final TagKey<StructureType> ON_WOODLAND_EXPLORER_MAPS = ConfiguredStructureFeatureTags.of("on_woodland_explorer_maps");
    public static final TagKey<StructureType> ON_OCEAN_EXPLORER_MAPS = ConfiguredStructureFeatureTags.of("on_ocean_explorer_maps");
    public static final TagKey<StructureType> ON_TREASURE_MAPS = ConfiguredStructureFeatureTags.of("on_treasure_maps");
    public static final TagKey<StructureType> CATS_SPAWN_IN = ConfiguredStructureFeatureTags.of("cats_spawn_in");
    public static final TagKey<StructureType> CATS_SPAWN_AS_BLACK = ConfiguredStructureFeatureTags.of("cats_spawn_as_black");
    public static final TagKey<StructureType> VILLAGE = ConfiguredStructureFeatureTags.of("village");
    public static final TagKey<StructureType> MINESHAFT = ConfiguredStructureFeatureTags.of("mineshaft");
    public static final TagKey<StructureType> SHIPWRECK = ConfiguredStructureFeatureTags.of("shipwreck");
    public static final TagKey<StructureType> RUINED_PORTAL = ConfiguredStructureFeatureTags.of("ruined_portal");
    public static final TagKey<StructureType> OCEAN_RUIN = ConfiguredStructureFeatureTags.of("ocean_ruin");

    private static TagKey<StructureType> of(String id) {
        return TagKey.of(Registry.STRUCTURE_KEY, new Identifier(id));
    }
}

