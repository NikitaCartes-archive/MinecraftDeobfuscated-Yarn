/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BuriedTreasureFeatureConfig
implements FeatureConfig {
    public static final Codec<BuriedTreasureFeatureConfig> field_24875 = Codec.FLOAT.xmap(BuriedTreasureFeatureConfig::new, buriedTreasureFeatureConfig -> Float.valueOf(buriedTreasureFeatureConfig.probability));
    public final float probability;

    public BuriedTreasureFeatureConfig(float probability) {
        this.probability = probability;
    }
}

