/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.FeatureConfig;

public class NetherSpringFeatureConfig
implements FeatureConfig {
    public final boolean insideRock;

    public NetherSpringFeatureConfig(boolean bl) {
        this.insideRock = bl;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("inside_rock"), dynamicOps.createBoolean(this.insideRock))));
    }

    public static <T> NetherSpringFeatureConfig deserialize(Dynamic<T> dynamic) {
        boolean bl = dynamic.get("inside_rock").asBoolean(false);
        return new NetherSpringFeatureConfig(bl);
    }
}

