package net.minecraft.block;

import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

public class CauldronBlock extends AbstractCauldronBlock {
	private static final float FILL_WITH_RAIN_CHANCE = 0.05F;
	private static final float FILL_WITH_SNOW_CHANCE = 0.1F;

	public CauldronBlock(AbstractBlock.Settings settings) {
		super(settings, CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR);
	}

	@Override
	public boolean isFull(BlockState state) {
		return false;
	}

	protected static boolean canFillWithPrecipitation(World world, Biome.Precipitation precipitation) {
		if (precipitation == Biome.Precipitation.RAIN) {
			return world.getRandom().nextFloat() < 0.05F;
		} else {
			return precipitation == Biome.Precipitation.SNOW ? world.getRandom().nextFloat() < 0.1F : false;
		}
	}

	@Override
	public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
		if (canFillWithPrecipitation(world, precipitation)) {
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
