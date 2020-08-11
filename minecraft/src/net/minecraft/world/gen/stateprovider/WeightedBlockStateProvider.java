package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.BlockPos;

public class WeightedBlockStateProvider extends BlockStateProvider {
	public static final Codec<WeightedBlockStateProvider> CODEC = WeightedList.method_28338(BlockState.CODEC)
		.<WeightedBlockStateProvider>comapFlatMap(WeightedBlockStateProvider::wrap, weightedBlockStateProvider -> weightedBlockStateProvider.states)
		.fieldOf("entries")
		.codec();
	private final WeightedList<BlockState> states;

	private static DataResult<WeightedBlockStateProvider> wrap(WeightedList<BlockState> states) {
		return states.isEmpty() ? DataResult.error("WeightedStateProvider with no states") : DataResult.success(new WeightedBlockStateProvider(states));
	}

	private WeightedBlockStateProvider(WeightedList<BlockState> states) {
		this.states = states;
	}

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.WEIGHTED_STATE_PROVIDER;
	}

	public WeightedBlockStateProvider() {
		this(new WeightedList<>());
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
