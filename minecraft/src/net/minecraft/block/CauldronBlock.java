package net.minecraft.block;

import net.fabricmc.yarn.constants.WorldEvents;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

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
				world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
			} else if (precipitation == Biome.Precipitation.SNOW) {
				world.setBlockState(pos, Blocks.POWDER_SNOW_CAULDRON.getDefaultState());
				world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
			}
		}
	}

	@Override
	protected boolean canBeFilledByDripstone(Fluid fluid) {
		return true;
	}

	@Override
	protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
		if (fluid == Fluids.WATER) {
			world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState());
			world.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS_WATER_INTO_CAULDRON, pos, 0);
			world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
		} else if (fluid == Fluids.LAVA) {
			world.setBlockState(pos, Blocks.LAVA_CAULDRON.getDefaultState());
			world.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS_LAVA_INTO_CAULDRON, pos, 0);
			world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
		}
	}
}
