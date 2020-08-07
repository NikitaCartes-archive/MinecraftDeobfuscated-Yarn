package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class TwistingVinesBlock extends AbstractPlantStemBlock {
	public static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 15.0, 12.0);

	public TwistingVinesBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.field_11036, SHAPE, false, 0.1);
	}

	@Override
	protected int method_26376(Random random) {
		return VineLogic.method_26381(random);
	}

	@Override
	protected Block getPlant() {
		return Blocks.field_23079;
	}

	@Override
	protected boolean chooseStemState(BlockState state) {
		return VineLogic.isValidForWeepingStem(state);
	}
}
