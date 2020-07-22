package net.minecraft.block;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class TwistingVinesPlantBlock extends AbstractPlantBlock {
	public static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

	public TwistingVinesPlantBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.UP, SHAPE, false);
	}

	@Override
	protected AbstractPlantStemBlock getStem() {
		return (AbstractPlantStemBlock)Blocks.TWISTING_VINES;
	}
}
