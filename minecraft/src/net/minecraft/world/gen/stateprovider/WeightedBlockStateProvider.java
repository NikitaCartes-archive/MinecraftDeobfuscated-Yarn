package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.math.BlockPos;

public class WeightedBlockStateProvider extends BlockStateProvider {
	public static final Codec<WeightedBlockStateProvider> field_24946 = WeightedList.method_28338(BlockState.field_24734)
		.<WeightedBlockStateProvider>comapFlatMap(WeightedBlockStateProvider::method_28868, weightedBlockStateProvider -> weightedBlockStateProvider.states)
		.fieldOf("entries")
		.codec();
	private final WeightedList<BlockState> states;

	private static DataResult<WeightedBlockStateProvider> method_28868(WeightedList<BlockState> weightedList) {
		return weightedList.method_28339()
			? DataResult.error("WeightedStateProvider with no states")
			: DataResult.success(new WeightedBlockStateProvider(weightedList));
	}

	private WeightedBlockStateProvider(WeightedList<BlockState> states) {
		this.states = states;
	}

	@Override
	protected BlockStateProviderType<?> method_28862() {
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
