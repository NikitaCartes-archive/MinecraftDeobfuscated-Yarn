package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.tick.ScheduledTickView;

public class TrapdoorBlock extends HorizontalFacingBlock implements Waterloggable {
	public static final MapCodec<TrapdoorBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter(block -> block.blockSetType), createSettingsCodec())
				.apply(instance, TrapdoorBlock::new)
	);
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final int field_31266 = 3;
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape OPEN_BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
	protected static final VoxelShape OPEN_TOP_SHAPE = Block.createCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
	private final BlockSetType blockSetType;

	@Override
	public MapCodec<? extends TrapdoorBlock> getCodec() {
		return CODEC;
	}

	protected TrapdoorBlock(BlockSetType type, AbstractBlock.Settings settings) {
		super(settings.sounds(type.soundType()));
		this.blockSetType = type;
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(OPEN, Boolean.valueOf(false))
				.with(HALF, BlockHalf.BOTTOM)
				.with(POWERED, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (!(Boolean)state.get(OPEN)) {
			return state.get(HALF) == BlockHalf.TOP ? OPEN_TOP_SHAPE : OPEN_BOTTOM_SHAPE;
		} else {
			switch ((Direction)state.get(FACING)) {
				case NORTH:
				default:
					return NORTH_SHAPE;
				case SOUTH:
					return SOUTH_SHAPE;
				case WEST:
					return WEST_SHAPE;
				case EAST:
					return EAST_SHAPE;
			}
		}
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		switch (type) {
			case LAND:
				return (Boolean)state.get(OPEN);
			case WATER:
				return (Boolean)state.get(WATERLOGGED);
			case AIR:
				return (Boolean)state.get(OPEN);
			default:
				return false;
		}
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!this.blockSetType.canOpenByHand()) {
			return ActionResult.PASS;
		} else {
			this.flip(state, world, pos, player);
			return ActionResult.SUCCESS;
		}
	}

	@Override
	protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
		if (explosion.canTriggerBlocks() && this.blockSetType.canOpenByWindCharge() && !(Boolean)state.get(POWERED)) {
			this.flip(state, world, pos, null);
		}

		super.onExploded(state, world, pos, explosion, stackMerger);
	}

	private void flip(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
		BlockState blockState = state.cycle(OPEN);
		world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
		if ((Boolean)blockState.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		this.playToggleSound(player, world, pos, (Boolean)blockState.get(OPEN));
	}

	protected void playToggleSound(@Nullable PlayerEntity player, World world, BlockPos pos, boolean open) {
		world.playSound(
			player,
			pos,
			open ? this.blockSetType.trapdoorOpen() : this.blockSetType.trapdoorClose(),
			SoundCategory.BLOCKS,
			1.0F,
			world.getRandom().nextFloat() * 0.1F + 0.9F
		);
		world.emitGameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
		if (!world.isClient) {
			boolean bl = world.isReceivingRedstonePower(pos);
			if (bl != (Boolean)state.get(POWERED)) {
				if ((Boolean)state.get(OPEN) != bl) {
					state = state.with(OPEN, Boolean.valueOf(bl));
					this.playToggleSound(null, world, pos, bl);
				}

				world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl)), Block.NOTIFY_LISTENERS);
				if ((Boolean)state.get(WATERLOGGED)) {
					world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
				}
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = this.getDefaultState();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		Direction direction = ctx.getSide();
		if (!ctx.canReplaceExisting() && direction.getAxis().isHorizontal()) {
			blockState = blockState.with(FACING, direction).with(HALF, ctx.getHitPos().y - (double)ctx.getBlockPos().getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM);
		} else {
			blockState = blockState.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(HALF, direction == Direction.UP ? BlockHalf.BOTTOM : BlockHalf.TOP);
		}

		if (ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())) {
			blockState = blockState.with(OPEN, Boolean.valueOf(true)).with(POWERED, Boolean.valueOf(true));
		}

		return blockState.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
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
		if ((Boolean)state.get(WATERLOGGED)) {
			tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	protected BlockSetType getBlockSetType() {
		return this.blockSetType;
	}
}
