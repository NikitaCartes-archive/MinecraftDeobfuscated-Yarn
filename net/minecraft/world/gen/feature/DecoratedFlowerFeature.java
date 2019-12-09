/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.feature.DecoratedFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;

public class DecoratedFlowerFeature
extends DecoratedFeature {
    public DecoratedFlowerFeature(Function<Dynamic<?>, ? extends DecoratedFeatureConfig> configFactory) {
        super(configFactory);
    }
}

