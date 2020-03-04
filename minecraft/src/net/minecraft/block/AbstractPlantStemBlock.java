package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public abstract class AbstractPlantStemBlock extends AbstractPlantPartBlock {
	public static final IntProperty AGE = Properties.AGE_25;
	private final double growthChance;

	protected AbstractPlantStemBlock(Block.Settings settings, Direction growthDirection, boolean tickWater, double growthChance) {
		super(settings, growthDirection, tickWater);
		this.growthChance = growthChance;
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	public BlockState getRandomGrowthState(IWorld world) {
		return this.getDefaultState().with(AGE, Integer.valueOf(world.getRandom().nextInt(25)));
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.breakBlock(pos, true);
		} else {
			if ((Integer)state.get(AGE) < 25 && random.nextDouble() < this.growthChance) {
				BlockPos blockPos = pos.offset(this.growthDirection);
				if (this.chooseStemState(world.getBlockState(blockPos))) {
					world.setBlockState(blockPos, state.cycle(AGE));
				}
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (facing == this.growthDirection.getOpposite() && !state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		if (facing == this.growthDirection && neighborState.getBlock() == this) {
			return this.getPlant().getDefaultState();
		} else {
			if (this.tickWater) {
				world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}

			return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	protected abstract boolean chooseStemState(BlockState state);

	@Override
	protected AbstractPlantStemBlock getStem() {
		return this;
	}
}
