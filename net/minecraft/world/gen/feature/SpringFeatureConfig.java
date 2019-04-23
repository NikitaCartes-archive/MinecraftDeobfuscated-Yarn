/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SpringFeatureConfig
implements FeatureConfig {
    public final FluidState state;

    public SpringFeatureConfig(FluidState fluidState) {
        this.state = fluidState;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), FluidState.serialize(dynamicOps, this.state).getValue())));
    }

    public static <T> SpringFeatureConfig deserialize(Dynamic<T> dynamic) {
        FluidState fluidState = dynamic.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState());
        return new SpringFeatureConfig(fluidState);
    }
}

