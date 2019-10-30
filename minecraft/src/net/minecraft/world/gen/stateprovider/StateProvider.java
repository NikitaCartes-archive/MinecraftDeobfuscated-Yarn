package net.minecraft.world.gen.stateprovider;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;

public abstract class StateProvider implements DynamicSerializable {
	protected final StateProviderType<?> stateProvider;

	protected StateProvider(StateProviderType<?> stateProvider) {
		this.stateProvider = stateProvider;
	}

	public abstract BlockState getBlockState(Random random, BlockPos pos);
}
