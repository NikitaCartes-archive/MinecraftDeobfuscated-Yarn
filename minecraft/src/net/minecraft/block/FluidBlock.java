package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FluidBlock extends Block implements FluidDrainable {
	public static final IntProperty LEVEL = Properties.LEVEL_15;
	protected final BaseFluid fluid;
	private final List<FluidState> statesByLevel;

	protected FluidBlock(BaseFluid fluid, AbstractBlock.Settings settings) {
		super(settings);
		this.fluid = fluid;
		this.statesByLevel = Lists.<FluidState>newArrayList();
		this.statesByLevel.add(fluid.getStill(false));

		for (int i = 1; i < 8; i++) {
			this.statesByLevel.add(fluid.getFlowing(8 - i, false));
		}

		this.statesByLevel.add(fluid.getFlowing(8, true));
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, Integer.valueOf(0)));
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return state.getFluidState().hasRandomTicks();
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		state.getFluidState().onRandomTick(world, pos, random);
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return !this.fluid.isIn(FluidTags.LAVA);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		int i = (Integer)state.get(LEVEL);
		return (FluidState)this.statesByLevel.get(Math.min(i, 8));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.getFluidState().getFluid().matchesType(this.fluid);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		return Collections.emptyList();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (this.receiveNeighborFluids(world, pos, state)) {
			world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
		if (state.getFluidState().isStill() || newState.getFluidState().isStill()) {
			world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (this.receiveNeighborFluids(world, pos, state)) {
			world.getFluidTickScheduler().schedule(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
		}
	}

	public boolean receiveNeighborFluids(World world, BlockPos pos, BlockState state) {
		if (this.fluid.isIn(FluidTags.LAVA)) {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.DOWN && world.getFluidState(pos.offset(direction)).matches(FluidTags.WATER)) {
					bl = true;
					break;
				}
			}

			if (bl) {
				FluidState fluidState = world.getFluidState(pos);
				if (fluidState.isStill()) {
					world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
					this.playExtinguishSound(world, pos);
					return false;
				}

				if (fluidState.getHeight(world, pos) >= 0.44444445F) {
					world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
					this.playExtinguishSound(world, pos);
					return false;
				}
			}
		}

		return true;
	}

	private void playExtinguishSound(IWorld world, BlockPos pos) {
		world.playLevelEvent(1501, pos, 0);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
		if ((Integer)state.get(LEVEL) == 0) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
			return this.fluid;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (this.fluid.isIn(FluidTags.LAVA)) {
			entity.setInLava();
		}
	}
}
