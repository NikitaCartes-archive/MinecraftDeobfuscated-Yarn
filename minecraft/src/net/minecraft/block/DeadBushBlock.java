package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DeadBushBlock extends PlantBlock {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

	protected DeadBushBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Block block = blockState.getBlock();
		return block == Blocks.SAND
			|| block == Blocks.RED_SAND
			|| block == Blocks.TERRACOTTA
			|| block == Blocks.WHITE_TERRACOTTA
			|| block == Blocks.ORANGE_TERRACOTTA
			|| block == Blocks.MAGENTA_TERRACOTTA
			|| block == Blocks.LIGHT_BLUE_TERRACOTTA
			|| block == Blocks.YELLOW_TERRACOTTA
			|| block == Blocks.LIME_TERRACOTTA
			|| block == Blocks.PINK_TERRACOTTA
			|| block == Blocks.GRAY_TERRACOTTA
			|| block == Blocks.LIGHT_GRAY_TERRACOTTA
			|| block == Blocks.CYAN_TERRACOTTA
			|| block == Blocks.PURPLE_TERRACOTTA
			|| block == Blocks.BLUE_TERRACOTTA
			|| block == Blocks.BROWN_TERRACOTTA
			|| block == Blocks.GREEN_TERRACOTTA
			|| block == Blocks.RED_TERRACOTTA
			|| block == Blocks.BLACK_TERRACOTTA
			|| block == Blocks.DIRT
			|| block == Blocks.COARSE_DIRT
			|| block == Blocks.PODZOL;
	}
}
