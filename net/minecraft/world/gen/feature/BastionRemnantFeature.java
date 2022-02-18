/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class BastionRemnantFeature
extends JigsawFeature {
    private static final int STRUCTURE_START_Y = 33;

    public BastionRemnantFeature(Codec<StructurePoolFeatureConfig> configCodec) {
        super(configCodec, 33, false, false, context -> true);
    }
}

