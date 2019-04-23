/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public interface FeatureConfig {
    public static final DefaultFeatureConfig DEFAULT = new DefaultFeatureConfig();

    public <T> Dynamic<T> serialize(DynamicOps<T> var1);
}

