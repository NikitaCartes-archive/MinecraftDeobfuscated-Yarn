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

public class EmeraldOreFeatureConfig
implements FeatureConfig {
    public final BlockState target;
    public final BlockState state;

    public EmeraldOreFeatureConfig(BlockState target, BlockState state) {
        this.target = target;
        this.state = state;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<T>(ops, ops.createMap(ImmutableMap.of(ops.createString("target"), BlockState.serialize(ops, this.target).getValue(), ops.createString("state"), BlockState.serialize(ops, this.state).getValue())));
    }

    public static <T> EmeraldOreFeatureConfig deserialize(Dynamic<T> dynamic) {
        BlockState blockState = dynamic.get("target").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        BlockState blockState2 = dynamic.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new EmeraldOreFeatureConfig(blockState, blockState2);
    }
}

