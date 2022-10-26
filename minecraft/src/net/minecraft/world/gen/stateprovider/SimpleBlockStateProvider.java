package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class SimpleBlockStateProvider extends BlockStateProvider {
	public static final Codec<SimpleBlockStateProvider> CODEC = BlockState.CODEC
		.fieldOf("state")
		.<SimpleBlockStateProvider>xmap(SimpleBlockStateProvider::new, simpleBlockStateProvider -> simpleBlockStateProvider.state)
		.codec();
	private final BlockState state;

	protected SimpleBlockStateProvider(BlockState state) {
		this.state = state;
	}

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
	}

	@Override
	public BlockState get(Random random, BlockPos pos) {
		return this.state;
	}
}
