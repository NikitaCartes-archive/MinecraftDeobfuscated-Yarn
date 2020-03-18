package net.minecraft.block;

import net.minecraft.util.math.Direction;

public class TwistingVinesPlantBlock extends AbstractPlantBlock {
	public TwistingVinesPlantBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.UP, VineLogic.STEM_OUTLINE_SHAPE, false);
	}

	@Override
	protected AbstractPlantStemBlock getStem() {
		return (AbstractPlantStemBlock)Blocks.TWISTING_VINES;
	}
}
