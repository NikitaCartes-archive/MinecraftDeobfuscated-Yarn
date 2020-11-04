package net.minecraft.block;

import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CauldronBlock extends AbstractCauldronBlock {
	public CauldronBlock(AbstractBlock.Settings settings) {
		super(settings, CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR);
	}

	protected static boolean canFillWithRain(World world, BlockPos pos) {
		return world.random.nextInt(20) != 1 ? false : world.getBiome(pos).getTemperature(pos) >= 0.15F;
	}

	@Override
	public void rainTick(BlockState state, World world, BlockPos pos) {
		if (canFillWithRain(world, pos)) {
			world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState());
		}
	}
}
