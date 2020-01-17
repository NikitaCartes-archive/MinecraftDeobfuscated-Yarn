package net.minecraft.world.gen.stateprovider;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;

public abstract class BlockStateProvider implements DynamicSerializable {
	protected final BlockStateProviderType<?> stateProvider;

	protected BlockStateProvider(BlockStateProviderType<?> stateProvider) {
		this.stateProvider = stateProvider;
	}

	public abstract BlockState getBlockState(Random random, BlockPos pos);
}
