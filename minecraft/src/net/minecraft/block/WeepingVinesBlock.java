package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;

public class WeepingVinesBlock extends AbstractPlantStemBlock {
	public static final MapCodec<WeepingVinesBlock> CODEC = createCodec(WeepingVinesBlock::new);
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 9.0, 4.0, 12.0, 16.0, 12.0);

	@Override
	public MapCodec<WeepingVinesBlock> getCodec() {
		return CODEC;
	}

	public WeepingVinesBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.DOWN, SHAPE, false, 0.1);
	}

	@Override
	protected int getGrowthLength(Random random) {
		return VineLogic.getGrowthLength(random);
	}

	@Override
	protected Block getPlant() {
		return Blocks.WEEPING_VINES_PLANT;
	}

	@Override
	protected boolean chooseStemState(BlockState state) {
		return VineLogic.isValidForWeepingStem(state);
	}
}
