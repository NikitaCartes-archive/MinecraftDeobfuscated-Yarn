/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;

public class MineshaftFeatureConfig
implements FeatureConfig {
    public final double probability;
    public final MineshaftFeature.Type type;

    public MineshaftFeatureConfig(double d, MineshaftFeature.Type type) {
        this.probability = d;
        this.type = type;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createDouble(this.probability), dynamicOps.createString("type"), dynamicOps.createString(this.type.getName()))));
    }

    public static <T> MineshaftFeatureConfig deserialize(Dynamic<T> dynamic) {
        float f = dynamic.get("probability").asFloat(0.0f);
        MineshaftFeature.Type type = MineshaftFeature.Type.byName(dynamic.get("type").asString(""));
        return new MineshaftFeatureConfig(f, type);
    }
}

