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

public class BoulderFeatureConfig
implements FeatureConfig {
    public final BlockState state;
    public final int startRadius;

    public BoulderFeatureConfig(BlockState blockState, int i) {
        this.state = blockState;
        this.startRadius = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), BlockState.serialize(dynamicOps, this.state).getValue(), dynamicOps.createString("start_radius"), dynamicOps.createInt(this.startRadius))));
    }

    public static <T> BoulderFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState blockState = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        int i = dynamic.get("start_radius").asInt(0);
        return new BoulderFeatureConfig(blockState, i);
    }
}

