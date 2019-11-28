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

    public SpringFeatureConfig(FluidState fluidState, boolean bl, int i, int j, Set<Block> set) {
        this.state = fluidState;
        this.requiresBlockBelow = bl;
        this.rockCount = i;
        this.holeCount = j;
        this.validBlocks = set;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<Object>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), FluidState.serialize(dynamicOps, this.state).getValue(), dynamicOps.createString("requires_block_below"), dynamicOps.createBoolean(this.requiresBlockBelow), dynamicOps.createString("rock_count"), dynamicOps.createInt(this.rockCount), dynamicOps.createString("hole_count"), dynamicOps.createInt(this.holeCount), dynamicOps.createString("valid_blocks"), dynamicOps.createList(this.validBlocks.stream().map(Registry.BLOCK::getId).map(Identifier::toString).map(dynamicOps::createString)))));
    }

    public static <T> SpringFeatureConfig deserialize(Dynamic<T> dynamic2) {
        return new SpringFeatureConfig(dynamic2.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState()), dynamic2.get("requires_block_below").asBoolean(true), dynamic2.get("rock_count").asInt(4), dynamic2.get("hole_count").asInt(1), ImmutableSet.copyOf(dynamic2.get("valid_blocks").asList(dynamic -> Registry.BLOCK.get(new Identifier(dynamic.asString("minecraft:air"))))));
    }
}

