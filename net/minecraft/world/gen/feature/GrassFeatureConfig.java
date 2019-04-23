/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class GrassFeatureConfig
implements FeatureConfig {
    public final BlockState state;

    public GrassFeatureConfig(BlockState blockState) {
        this.state = blockState;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue())));
    }

    public static <T> GrassFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState blockState = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new GrassFeatureConfig(blockState);
    }
}

