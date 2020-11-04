package net.minecraft.block;

import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WaterCauldronBlock extends AbstractCauldronBlock {
	public static final IntProperty LEVEL = Properties.LEVEL_3;

	public WaterCauldronBlock(AbstractBlock.Settings settings) {
		super(settings, CauldronBehavior.WATER_CAULDRON_BEHAVIOR);
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, Integer.valueOf(1)));
	}

	@Override
	protected double getFluidHeight(BlockState state) {
		return (double)(6 + (Integer)state.get(LEVEL) * 3) / 16.0;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && entity.isOnFire() && this.isEntityTouchingFluid(state, pos, entity)) {
			entity.extinguish();
			subtractWaterLevel(state, world, pos);
		}
	}

	public static void subtractWaterLevel(BlockState state, World world, BlockPos pos) {
		int i = (Integer)state.get(LEVEL) - 1;
		world.setBlockState(pos, i == 0 ? Blocks.CAULDRON.getDefaultState() : state.with(LEVEL, Integer.valueOf(i)));
	}

	@Override
	public void rainTick(BlockState state, World world, BlockPos pos) {
		if (CauldronBlock.canFillWithRain(world, pos) && (Integer)state.get(LEVEL) != 3) {
			world.setBlockState(pos, state.cycle(LEVEL));
		}
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return (Integer)state.get(LEVEL);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}
}
