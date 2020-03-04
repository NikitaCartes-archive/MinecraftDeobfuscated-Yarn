package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class WeepingVinesBlock extends AbstractPlantStemBlock {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 9.0, 4.0, 12.0, 16.0, 12.0);

	public WeepingVinesBlock(Block.Settings settings) {
		super(settings, Direction.DOWN, false, 0.1);
	}

	@Override
	protected boolean chooseStemState(BlockState state) {
		return state.isAir();
	}

	@Override
	protected Block getPlant() {
		return Blocks.WEEPING_VINES_PLANT;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		return SHAPE;
	}
}
