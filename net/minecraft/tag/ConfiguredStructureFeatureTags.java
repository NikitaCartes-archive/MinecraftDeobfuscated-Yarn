/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public interface ConfiguredStructureFeatureTags {
    public static final TagKey<ConfiguredStructureFeature<?, ?>> EYE_OF_ENDER_LOCATED = ConfiguredStructureFeatureTags.of("eye_of_ender_located");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> DOLPHIN_LOCATED = ConfiguredStructureFeatureTags.of("dolphin_located");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> ON_WOODLAND_EXPLORER_MAPS = ConfiguredStructureFeatureTags.of("on_woodland_explorer_maps");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> ON_OCEAN_EXPLORER_MAPS = ConfiguredStructureFeatureTags.of("on_ocean_explorer_maps");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> ON_TREASURE_MAPS = ConfiguredStructureFeatureTags.of("on_treasure_maps");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> VILLAGE = ConfiguredStructureFeatureTags.of("village");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> MINESHAFT = ConfiguredStructureFeatureTags.of("mineshaft");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK = ConfiguredStructureFeatureTags.of("shipwreck");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL = ConfiguredStructureFeatureTags.of("ruined_portal");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN = ConfiguredStructureFeatureTags.of("ocean_ruin");

    private static TagKey<ConfiguredStructureFeature<?, ?>> of(String id) {
        return TagKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(id));
    }
}

