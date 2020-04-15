package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractPlantStemBlock extends AbstractPlantPartBlock implements Fertilizable {
	public static final IntProperty AGE = Properties.AGE_25;
	private final double growthChance;

	protected AbstractPlantStemBlock(AbstractBlock.Settings settings, Direction growthDirection, VoxelShape outlineShape, boolean tickWater, double growthChance) {
		super(settings, growthDirection, outlineShape, tickWater);
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
		}
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return (Integer)state.get(AGE) < 25;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Integer)state.get(AGE) < 25 && random.nextDouble() < this.growthChance) {
			BlockPos blockPos = pos.offset(this.growthDirection);
			if (this.chooseStemState(world.getBlockState(blockPos))) {
				world.setBlockState(blockPos, state.cycle(AGE));
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
		if (direction == this.growthDirection.getOpposite() && !state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		if (direction == this.growthDirection && newState.getBlock() == this) {
			return this.getPlant().getDefaultState();
		} else {
			if (this.tickWater) {
				world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}

			return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return this.chooseStemState(world.getBlockState(pos.offset(this.growthDirection)));
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.offset(this.growthDirection);
		int i = Math.min((Integer)state.get(AGE) + 1, 25);
		int j = this.method_26376(random);

		for (int k = 0; k < j && this.chooseStemState(world.getBlockState(blockPos)); k++) {
			world.setBlockState(blockPos, state.with(AGE, Integer.valueOf(i)));
			blockPos = blockPos.offset(this.growthDirection);
			i = Math.min(i + 1, 25);
		}
	}

	protected abstract int method_26376(Random random);

	protected abstract boolean chooseStemState(BlockState state);

	@Override
	protected AbstractPlantStemBlock getStem() {
		return this;
	}
}