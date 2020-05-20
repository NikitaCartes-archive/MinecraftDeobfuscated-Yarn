package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public abstract class BlockStateProvider {
	public static final Codec<BlockStateProvider> field_24937 = Registry.BLOCK_STATE_PROVIDER_TYPE
		.dispatch(BlockStateProvider::method_28862, BlockStateProviderType::method_28863);

	protected abstract BlockStateProviderType<?> method_28862();

	public abstract BlockState getBlockState(Random random, BlockPos pos);
}
