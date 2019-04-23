/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.FeatureConfig;

public class IcePatchFeatureConfig
implements FeatureConfig {
    public final int radius;

    public IcePatchFeatureConfig(int i) {
        this.radius = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("radius"), dynamicOps.createInt(this.radius))));
    }

    public static <T> IcePatchFeatureConfig deserialize(Dynamic<T> dynamic) {
        int i = dynamic.get("radius").asInt(0);
        return new IcePatchFeatureConfig(i);
    }
}

