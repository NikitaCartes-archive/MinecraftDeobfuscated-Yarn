/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SpringFeatureConfig
implements FeatureConfig {
    public final FluidState state;
    public final boolean requiresBlockBelow;
    public final int rockCount;
    public final int holeCount;
    public final Set<Block> validBlocks;

    public SpringFeatureConfig(FluidState state, boolean requiresBlockBelow, int rockCount, int holeCount, Set<Block> validBlocks) {
        this.state = state;
        this.requiresBlockBelow = requiresBlockBelow;
        this.rockCount = rockCount;
        this.holeCount = holeCount;
        this.validBlocks = validBlocks;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<Object>(ops, ops.createMap(ImmutableMap.of(ops.createString("state"), FluidState.serialize(ops, this.state).getValue(), ops.createString("requires_block_below"), ops.createBoolean(this.requiresBlockBelow), ops.createString("rock_count"), ops.createInt(this.rockCount), ops.createString("hole_count"), ops.createInt(this.holeCount), ops.createString("valid_blocks"), ops.createList(this.validBlocks.stream().map(Registry.BLOCK::getId).map(Identifier::toString).map(ops::createString)))));
    }

    public static <T> SpringFeatureConfig deserialize(Dynamic<T> dynamic2) {
        return new SpringFeatureConfig(dynamic2.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState()), dynamic2.get("requires_block_below").asBoolean(true), dynamic2.get("rock_count").asInt(4), dynamic2.get("hole_count").asInt(1), ImmutableSet.copyOf(dynamic2.get("valid_blocks").asList(dynamic -> Registry.BLOCK.get(new Identifier(dynamic.asString("minecraft:air"))))));
    }
}

