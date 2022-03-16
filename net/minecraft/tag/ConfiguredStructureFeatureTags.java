/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

public interface ConfiguredStructureFeatureTags {
    public static final TagKey<StructureFeature> EYE_OF_ENDER_LOCATED = ConfiguredStructureFeatureTags.of("eye_of_ender_located");
    public static final TagKey<StructureFeature> DOLPHIN_LOCATED = ConfiguredStructureFeatureTags.of("dolphin_located");
    public static final TagKey<StructureFeature> ON_WOODLAND_EXPLORER_MAPS = ConfiguredStructureFeatureTags.of("on_woodland_explorer_maps");
    public static final TagKey<StructureFeature> ON_OCEAN_EXPLORER_MAPS = ConfiguredStructureFeatureTags.of("on_ocean_explorer_maps");
    public static final TagKey<StructureFeature> ON_TREASURE_MAPS = ConfiguredStructureFeatureTags.of("on_treasure_maps");
    public static final TagKey<StructureFeature> CATS_SPAWN_IN = ConfiguredStructureFeatureTags.of("cats_spawn_in");
    public static final TagKey<StructureFeature> CATS_SPAWN_AS_BLACK = ConfiguredStructureFeatureTags.of("cats_spawn_as_black");
    public static final TagKey<StructureFeature> VILLAGE = ConfiguredStructureFeatureTags.of("village");
    public static final TagKey<StructureFeature> MINESHAFT = ConfiguredStructureFeatureTags.of("mineshaft");
    public static final TagKey<StructureFeature> SHIPWRECK = ConfiguredStructureFeatureTags.of("shipwreck");
    public static final TagKey<StructureFeature> RUINED_PORTAL = ConfiguredStructureFeatureTags.of("ruined_portal");
    public static final TagKey<StructureFeature> OCEAN_RUIN = ConfiguredStructureFeatureTags.of("ocean_ruin");

    private static TagKey<StructureFeature> of(String id) {
        return TagKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(id));
    }
}

