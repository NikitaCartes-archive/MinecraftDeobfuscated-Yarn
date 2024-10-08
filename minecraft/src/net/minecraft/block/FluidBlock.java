package net.minecraft.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;

public class FluidBlock extends Block implements FluidDrainable {
	private static final Codec<FlowableFluid> FLUID_CODEC = Registries.FLUID
		.getCodec()
		.comapFlatMap(
			fluid -> fluid instanceof FlowableFluid flowableFluid ? DataResult.success(flowableFluid) : DataResult.error(() -> "Not a flowing fluid: " + fluid),
			fluid -> fluid
		);
	public static final MapCodec<FluidBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(FLUID_CODEC.fieldOf("fluid").forGetter(block -> block.fluid), createSettingsCodec()).apply(instance, FluidBlock::new)
	);
	public static final IntProperty LEVEL = Properties.LEVEL_15;
	protected final FlowableFluid fluid;
	private final List<FluidState> statesByLevel;
	public static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	public static final ImmutableList<Direction> FLOW_DIRECTIONS = ImmutableList.of(
		Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST
	);

	@Override
	public MapCodec<FluidBlock> getCodec() {
		return CODEC;
	}

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
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return context.isAbove(COLLISION_SHAPE, pos, true) && state.get(LEVEL) == 0 && context.canWalkOnFluid(world.getFluidState(pos.up()), state.getFluidState())
			? COLLISION_SHAPE
			: VoxelShapes.empty();
	}

	@Override
	protected boolean hasRandomTicks(BlockState state) {
		return state.getFluidState().hasRandomTicks();
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		state.getFluidState().onRandomTick(world, pos, random);
	}

	@Override
	protected boolean isTransparent(BlockState state) {
		return false;
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return !this.fluid.isIn(FluidTags.LAVA);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		int i = (Integer)state.get(LEVEL);
		return (FluidState)this.statesByLevel.get(Math.min(i, 8));
	}

	@Override
	protected boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.getFluidState().getFluid().matchesType(this.fluid);
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	protected List<ItemStack> getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
		return Collections.emptyList();
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (this.receiveNeighborFluids(world, pos, state)) {
			world.scheduleFluidTick(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		if (state.getFluidState().isStill() || neighborState.getFluidState().isStill()) {
			tickView.scheduleFluidTick(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
		if (this.receiveNeighborFluids(world, pos, state)) {
			world.scheduleFluidTick(pos, state.getFluidState().getFluid(), this.fluid.getTickRate(world));
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
	public ItemStack tryDrainFluid(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, BlockState state) {
		if ((Integer)state.get(LEVEL) == 0) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
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
