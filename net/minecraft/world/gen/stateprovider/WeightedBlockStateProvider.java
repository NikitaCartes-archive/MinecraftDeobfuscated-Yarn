/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class WeightedBlockStateProvider
extends BlockStateProvider {
    public static final Codec<WeightedBlockStateProvider> CODEC = ((MapCodec)WeightedList.method_28338(BlockState.CODEC).comapFlatMap(WeightedBlockStateProvider::wrap, weightedBlockStateProvider -> weightedBlockStateProvider.states).fieldOf("entries")).codec();
    private final WeightedList<BlockState> states;

    private static DataResult<WeightedBlockStateProvider> wrap(WeightedList<BlockState> states) {
        if (states.isEmpty()) {
            return DataResult.error("WeightedStateProvider with no states");
        }
        return DataResult.success(new WeightedBlockStateProvider(states));
    }

    private WeightedBlockStateProvider(WeightedList<BlockState> states) {
        this.states = states;
    }

    @Override
    protected BlockStateProviderType<?> getType() {
        return BlockStateProviderType.WEIGHTED_STATE_PROVIDER;
    }

    public WeightedBlockStateProvider() {
        this(new WeightedList<BlockState>());
    }

    public WeightedBlockStateProvider addState(BlockState state, int weight) {
        this.states.add(state, weight);
        return this;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos pos) {
        return this.states.pickRandom(random);
    }
}

