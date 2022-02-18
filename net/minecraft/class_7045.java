/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public interface class_7045 {
    public static final TagKey<ConfiguredStructureFeature<?, ?>> EYE_OF_ENDER_LOCATED = class_7045.method_41006("eye_of_ender_located");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> DOLPHIN_LOCATED = class_7045.method_41006("dolphin_located");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> ON_WOODLAND_EXPLORER_MAPS = class_7045.method_41006("on_woodland_explorer_maps");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> ON_OCEAN_EXPLORER_MAPS = class_7045.method_41006("on_ocean_explorer_maps");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> ON_TREASURE_MAPS = class_7045.method_41006("on_treasure_maps");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> VILLAGE = class_7045.method_41006("village");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> MINESHAFT = class_7045.method_41006("mineshaft");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK = class_7045.method_41006("shipwreck");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL = class_7045.method_41006("ruined_portal");
    public static final TagKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN = class_7045.method_41006("ocean_ruin");

    private static TagKey<ConfiguredStructureFeature<?, ?>> method_41006(String string) {
        return TagKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(string));
    }
}

