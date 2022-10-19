/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.tag;

import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.AbstractTagProvider;
import net.minecraft.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;

public class FlatLevelGeneratorPresetTagProvider
extends AbstractTagProvider<FlatLevelGeneratorPreset> {
    public FlatLevelGeneratorPresetTagProvider(DataOutput dataGenerator) {
        super(dataGenerator, BuiltinRegistries.FLAT_LEVEL_GENERATOR_PRESET);
    }

    @Override
    protected void configure() {
        this.getOrCreateTagBuilder(FlatLevelGeneratorPresetTags.VISIBLE).add(FlatLevelGeneratorPresets.CLASSIC_FLAT).add(FlatLevelGeneratorPresets.TUNNELERS_DREAM).add(FlatLevelGeneratorPresets.WATER_WORLD).add(FlatLevelGeneratorPresets.OVERWORLD).add(FlatLevelGeneratorPresets.SNOWY_KINGDOM).add(FlatLevelGeneratorPresets.BOTTOMLESS_PIT).add(FlatLevelGeneratorPresets.DESERT).add(FlatLevelGeneratorPresets.REDSTONE_READY).add(FlatLevelGeneratorPresets.THE_VOID);
    }
}

