/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class IcePatchFeatureConfig
implements FeatureConfig {
    public static final Codec<IcePatchFeatureConfig> field_24884 = ((MapCodec)Codec.INT.fieldOf("radius")).xmap(IcePatchFeatureConfig::new, icePatchFeatureConfig -> icePatchFeatureConfig.radius).codec();
    public final int radius;

    public IcePatchFeatureConfig(int radius) {
        this.radius = radius;
    }
}

