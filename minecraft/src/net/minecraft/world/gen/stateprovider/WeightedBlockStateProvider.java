package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.block.BlockState;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class WeightedBlockStateProvider extends BlockStateProvider {
	public static final Codec<WeightedBlockStateProvider> CODEC = DataPool.createCodec(BlockState.CODEC)
		.<WeightedBlockStateProvider>comapFlatMap(WeightedBlockStateProvider::wrap, weightedBlockStateProvider -> weightedBlockStateProvider.states)
		.fieldOf("entries")
		.codec();
	private final DataPool<BlockState> states;

	private static DataResult<WeightedBlockStateProvider> wrap(DataPool<BlockState> states) {
		return states.isEmpty() ? DataResult.error(() -> "WeightedStateProvider with no states") : DataResult.success(new WeightedBlockStateProvider(states));
	}

	public WeightedBlockStateProvider(DataPool<BlockState> states) {
		this.states = states;
	}

	public WeightedBlockStateProvider(DataPool.Builder<BlockState> states) {
		this(states.build());
	}

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.WEIGHTED_STATE_PROVIDER;
	}

	@Override
	public BlockState get(Random random, BlockPos pos) {
		return (BlockState)this.states.getDataOrEmpty(random).orElseThrow(IllegalStateException::new);
	}
}
