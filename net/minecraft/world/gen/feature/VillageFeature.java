/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class VillageFeature
extends JigsawFeature {
    public VillageFeature(Codec<StructurePoolFeatureConfig> configCodec) {
        super(configCodec, 0, true, true, arg -> true);
    }
}

