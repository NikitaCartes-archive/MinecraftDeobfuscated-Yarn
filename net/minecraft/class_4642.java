/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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

public class class_4642
implements FeatureConfig {
    public final FluidState field_21283;
    public final boolean field_21284;
    public final int field_21285;
    public final int field_21286;
    public final Set<Block> field_21287;

    public class_4642(FluidState fluidState, boolean bl, int i, int j, Set<Block> set) {
        this.field_21283 = fluidState;
        this.field_21284 = bl;
        this.field_21285 = i;
        this.field_21286 = j;
        this.field_21287 = set;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        return new Dynamic<Object>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), FluidState.serialize(dynamicOps, this.field_21283).getValue(), dynamicOps.createString("requires_block_below"), dynamicOps.createBoolean(this.field_21284), dynamicOps.createString("rock_count"), dynamicOps.createInt(this.field_21285), dynamicOps.createString("hole_count"), dynamicOps.createInt(this.field_21286), dynamicOps.createString("valid_blocks"), dynamicOps.createList(this.field_21287.stream().map(Registry.BLOCK::getId).map(Identifier::toString).map(dynamicOps::createString)))));
    }

    public static <T> class_4642 method_23440(Dynamic<T> dynamic2) {
        return new class_4642(dynamic2.get("state").map(FluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState()), dynamic2.get("requires_block_below").asBoolean(true), dynamic2.get("rock_count").asInt(4), dynamic2.get("hole_count").asInt(1), ImmutableSet.copyOf(dynamic2.get("valid_blocks").asList(dynamic -> Registry.BLOCK.get(new Identifier(dynamic.asString("minecraft:air"))))));
    }
}

