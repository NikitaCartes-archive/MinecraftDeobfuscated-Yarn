package net.minecraft.block;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class TwistingVinesBlock extends AbstractPlantStemBlock {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 15.0, 12.0);

	public TwistingVinesBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.UP, SHAPE, false, 0.1);
	}

	@Override
	protected Block getPlant() {
		return Blocks.TWISTING_VINES_PLANT;
	}

	@Override
	protected boolean chooseStemState(BlockState state) {
		return VineLogic.isValidForWeepingStem(state);
	}
}
