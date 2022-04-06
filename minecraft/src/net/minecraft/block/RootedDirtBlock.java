package net.minecraft.block;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RootedDirtBlock extends Block implements Fertilizable {
	public RootedDirtBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return world.getBlockState(pos.down()).isAir();
	}

	@Override
	public boolean canGrow(World world, AbstractRandom random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, AbstractRandom random, BlockPos pos, BlockState state) {
		world.setBlockState(pos.down(), Blocks.HANGING_ROOTS.getDefaultState());
	}
}
