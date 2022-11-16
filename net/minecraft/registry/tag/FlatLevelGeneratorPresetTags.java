/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;

public class FlatLevelGeneratorPresetTags {
    public static final TagKey<FlatLevelGeneratorPreset> VISIBLE = FlatLevelGeneratorPresetTags.of("visible");

    private FlatLevelGeneratorPresetTags() {
    }

    private static TagKey<FlatLevelGeneratorPreset> of(String id) {
        return TagKey.of(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, new Identifier(id));
    }
}

