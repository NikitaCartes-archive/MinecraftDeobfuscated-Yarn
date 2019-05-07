/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.FeatureConfig;

public class PlantedFeatureConfig
implements FeatureConfig {
    public final boolean planted;

    public PlantedFeatureConfig(boolean bl) {
        this.planted = bl;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("planted"), dynamicOps.createBoolean(this.planted))));
    }

    public static <T> PlantedFeatureConfig deserialize(Dynamic<T> dynamic) {
        boolean bl = dynamic.get("planted").asBoolean(false);
        return new PlantedFeatureConfig(bl);
    }
}

