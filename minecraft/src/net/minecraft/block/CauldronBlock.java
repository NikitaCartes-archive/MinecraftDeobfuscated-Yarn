package net.minecraft.block;

import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class CauldronBlock extends AbstractCauldronBlock {
	public CauldronBlock(AbstractBlock.Settings settings) {
		super(settings, CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR);
	}

	protected static boolean canFillWithPrecipitation(World world) {
		return world.random.nextInt(20) == 1;
	}

	@Override
	public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
		if (canFillWithPrecipitation(world)) {
			if (precipitation == Biome.Precipitation.RAIN) {
				world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState());
			} else if (precipitation == Biome.Precipitation.SNOW) {
				world.setBlockState(pos, Blocks.POWDER_SNOW_CAULDRON.getDefaultState());
			}
		}
	}
}
