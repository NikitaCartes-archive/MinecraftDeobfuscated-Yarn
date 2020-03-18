package net.minecraft.block;

import net.minecraft.util.math.Direction;

public class WeepingVinesPlantBlock extends AbstractPlantBlock {
	public WeepingVinesPlantBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.DOWN, VineLogic.STEM_OUTLINE_SHAPE, false);
	}

	@Override
	protected AbstractPlantStemBlock getStem() {
		return (AbstractPlantStemBlock)Blocks.WEEPING_VINES;
	}
}
