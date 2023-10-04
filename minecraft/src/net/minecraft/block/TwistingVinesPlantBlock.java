package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class TwistingVinesPlantBlock extends AbstractPlantBlock {
	public static final MapCodec<TwistingVinesPlantBlock> CODEC = createCodec(TwistingVinesPlantBlock::new);
	public static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

	@Override
	public MapCodec<TwistingVinesPlantBlock> getCodec() {
		return CODEC;
	}

	public TwistingVinesPlantBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.UP, SHAPE, false);
	}

	@Override
	protected AbstractPlantStemBlock getStem() {
		return (AbstractPlantStemBlock)Blocks.TWISTING_VINES;
	}
}
