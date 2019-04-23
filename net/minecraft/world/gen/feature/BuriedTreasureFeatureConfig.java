/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.FeatureConfig;

public class BuriedTreasureFeatureConfig
implements FeatureConfig {
    public final float probability;

    public BuriedTreasureFeatureConfig(float f) {
        this.probability = f;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createFloat(this.probability))));
    }

    public static <T> BuriedTreasureFeatureConfig deserialize(Dynamic<T> dynamic) {
        float f = dynamic.get("probability").asFloat(0.0f);
        return new BuriedTreasureFeatureConfig(f);
    }
}

