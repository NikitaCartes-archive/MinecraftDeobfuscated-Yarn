package net.minecraft.block;

import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DeadBushBlock extends PlantBlock {
	protected static final float field_31080 = 6.0F;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

	protected DeadBushBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(Blocks.SAND)
			|| floor.isOf(Blocks.RED_SAND)
			|| floor.isOf(Blocks.TERRACOTTA)
			|| floor.isOf(Blocks.WHITE_TERRACOTTA)
			|| floor.isOf(Blocks.ORANGE_TERRACOTTA)
			|| floor.isOf(Blocks.MAGENTA_TERRACOTTA)
			|| floor.isOf(Blocks.LIGHT_BLUE_TERRACOTTA)
			|| floor.isOf(Blocks.YELLOW_TERRACOTTA)
			|| floor.isOf(Blocks.LIME_TERRACOTTA)
			|| floor.isOf(Blocks.PINK_TERRACOTTA)
			|| floor.isOf(Blocks.GRAY_TERRACOTTA)
			|| floor.isOf(Blocks.LIGHT_GRAY_TERRACOTTA)
			|| floor.isOf(Blocks.CYAN_TERRACOTTA)
			|| floor.isOf(Blocks.PURPLE_TERRACOTTA)
			|| floor.isOf(Blocks.BLUE_TERRACOTTA)
			|| floor.isOf(Blocks.BROWN_TERRACOTTA)
			|| floor.isOf(Blocks.GREEN_TERRACOTTA)
			|| floor.isOf(Blocks.RED_TERRACOTTA)
			|| floor.isOf(Blocks.BLACK_TERRACOTTA)
			|| floor.isIn(BlockTags.DIRT);
	}
}
