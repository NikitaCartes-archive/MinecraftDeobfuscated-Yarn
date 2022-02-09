package net.minecraft.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

public class FluidBlock extends Block implements FluidDrainable {
	public static final IntProperty LEVEL = Properties.LEVEL_15;
	protected final FlowableFluid fluid;
	private final List<FluidState> statesByLevel;
	public static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	public static final ImmutableList<Direction> FLOW_DIRECTIONS = ImmutableList.of(
		Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST
	);

	protected FluidBlock(FlowableFluid fluid, AbstractBlock.Settings settings) {
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
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return context.isAbove(COLLISION_SHAPE, pos, true) && state.get(LEVEL) == 0 && context.canWalkOnFluid(world.getFluidState(pos.up()), state.getFluidState())
			? COLLISION_SHAPE
			: VoxelShapes.empty();
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
			world.createAndScheduleFluidTick(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (state.getFluidState().isStill() || neighborState.getFluidState().isStill()) {
			world.createAndScheduleFluidTick(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (this.receiveNeighborFluids(world, pos, state)) {
			world.createAndScheduleFluidTick(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
		}
	}

	private boolean receiveNeighborFluids(World world, BlockPos pos, BlockState state) {
		if (this.fluid.isIn(FluidTags.LAVA)) {
			boolean bl = world.getBlockState(pos.down()).isOf(Blocks.SOUL_SOIL);

			for (Direction direction : FLOW_DIRECTIONS) {
				BlockPos blockPos = pos.offset(direction.getOpposite());
				if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
					Block block = world.getFluidState(pos).isStill() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
					world.setBlockState(pos, block.getDefaultState());
					this.playExtinguishSound(world, pos);
					return false;
				}

				if (bl && world.getBlockState(blockPos).isOf(Blocks.BLUE_ICE)) {
					world.setBlockState(pos, Blocks.BASALT.getDefaultState());
					this.playExtinguishSound(world, pos);
					return false;
				}
			}
		}

		return true;
	}

	private void playExtinguishSound(WorldAccess world, BlockPos pos) {
		world.syncWorldEvent(WorldEvents.LAVA_EXTINGUISHED, pos, 0);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		if ((Integer)state.get(LEVEL) == 0) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
			return new ItemStack(this.fluid.getBucketItem());
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return this.fluid.getBucketFillSound();
	}
}
