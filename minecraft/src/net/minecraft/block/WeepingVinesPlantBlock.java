package net.minecraft.block;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class WeepingVinesPlantBlock extends AbstractPlantBlock {
	public static final VoxelShape field_23326 = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	public WeepingVinesPlantBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.DOWN, field_23326, false);
	}

	@Override
	protected AbstractPlantStemBlock getStem() {
		return (AbstractPlantStemBlock)Blocks.WEEPING_VINES;
	}
}
