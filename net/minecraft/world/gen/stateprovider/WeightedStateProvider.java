/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.stateprovider;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.WeightedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.StateProvider;
import net.minecraft.world.gen.stateprovider.StateProviderType;

public class WeightedStateProvider
extends StateProvider {
    private final WeightedList<BlockState> states;

    private WeightedStateProvider(WeightedList<BlockState> weightedList) {
        super(StateProviderType.WEIGHTED_STATE_PROVIDER);
        this.states = weightedList;
    }

    public WeightedStateProvider() {
        this(new WeightedList<BlockState>());
    }

    public <T> WeightedStateProvider(Dynamic<T> dynamic) {
        this(new WeightedList<BlockState>(dynamic.get("entries").orElseEmptyList(), BlockState::deserialize));
    }

    public WeightedStateProvider addState(BlockState blockState, int i) {
        this.states.add(blockState, i);
        return this;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos blockPos) {
        return this.states.pickRandom(random);
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.stateProvider).toString())).put(dynamicOps.createString("entries"), this.states.method_23330(dynamicOps, blockState -> BlockState.serialize(dynamicOps, blockState)));
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
    }
}

