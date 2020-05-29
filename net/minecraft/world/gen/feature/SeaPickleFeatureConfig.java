/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SeaPickleFeatureConfig
implements FeatureConfig {
    public static final Codec<SeaPickleFeatureConfig> CODEC = ((MapCodec)Codec.INT.fieldOf("count")).xmap(SeaPickleFeatureConfig::new, seaPickleFeatureConfig -> seaPickleFeatureConfig.count).codec();
    public final int count;

    public SeaPickleFeatureConfig(int count) {
        this.count = count;
    }
}

