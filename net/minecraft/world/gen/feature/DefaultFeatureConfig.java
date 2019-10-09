/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DefaultFeatureConfig
implements FeatureConfig {
    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.emptyMap());
    }

    public static <T> DefaultFeatureConfig deserialize(Dynamic<T> dynamic) {
        return DEFAULT;
    }
}

