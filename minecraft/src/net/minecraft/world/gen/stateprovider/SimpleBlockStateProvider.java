package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class SimpleBlockStateProvider extends BlockStateProvider {
	public static final Codec<SimpleBlockStateProvider> field_24945 = BlockState.field_24734
		.fieldOf("state")
		.<SimpleBlockStateProvider>xmap(SimpleBlockStateProvider::new, simpleBlockStateProvider -> simpleBlockStateProvider.state)
		.codec();
	private final BlockState state;

	public SimpleBlockStateProvider(BlockState blockState) {
		this.state = blockState;
	}

	@Override
	protected BlockStateProviderType<?> method_28862() {
		return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		return this.state;
	}
}
