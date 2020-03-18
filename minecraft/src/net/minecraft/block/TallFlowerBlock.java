package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TallFlowerBlock extends TallPlantBlock implements Fertilizable {
	public TallFlowerBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return false;
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		dropStack(world, pos, new ItemStack(this));
	}
}
