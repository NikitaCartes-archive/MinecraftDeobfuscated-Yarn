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
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class WeightedBlockStateProvider
extends BlockStateProvider {
    private final WeightedList<BlockState> states;

    private WeightedBlockStateProvider(WeightedList<BlockState> states) {
        super(BlockStateProviderType.WEIGHTED_STATE_PROVIDER);
        this.states = states;
    }

    public WeightedBlockStateProvider() {
        this(new WeightedList<BlockState>());
    }

    public <T> WeightedBlockStateProvider(Dynamic<T> configDeserializer) {
        this(new WeightedList<BlockState>(configDeserializer.get("entries").orElseEmptyList(), BlockState::deserialize));
    }

    public WeightedBlockStateProvider addState(BlockState state, int weight) {
        this.states.add(state, weight);
        return this;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos pos) {
        return this.states.pickRandom(random);
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(ops.createString("type"), ops.createString(Registry.BLOCK_STATE_PROVIDER_TYPE.getId(this.stateProvider).toString())).put(ops.createString("entries"), this.states.serialize(ops, blockState -> BlockState.serialize(ops, blockState)));
        return new Dynamic<T>(ops, ops.createMap(builder.build())).getValue();
    }
}

