package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class TripwireBlock extends Block {
	public static final MapCodec<TripwireBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Registries.BLOCK.getCodec().fieldOf("hook").forGetter(block -> block.hookBlock), createSettingsCodec())
				.apply(instance, TripwireBlock::new)
	);
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty ATTACHED = Properties.ATTACHED;
	public static final BooleanProperty DISARMED = Properties.DISARMED;
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = HorizontalConnectingBlock.FACING_PROPERTIES;
	protected static final VoxelShape ATTACHED_SHAPE = Block.createCuboidShape(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
	protected static final VoxelShape DETACHED_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private static final int SCHEDULED_TICK_DELAY = 10;
	private final Block hookBlock;

	@Override
	public MapCodec<TripwireBlock> getCodec() {
		return CODEC;
	}

	public TripwireBlock(Block hookBlock, AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(POWERED, Boolean.valueOf(false))
				.with(ATTACHED, Boolean.valueOf(false))
				.with(DISARMED, Boolean.valueOf(false))
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
		);
		this.hookBlock = hookBlock;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(ATTACHED) ? ATTACHED_SHAPE : DETACHED_SHAPE;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		return this.getDefaultState()
			.with(NORTH, Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.north()), Direction.NORTH)))
			.with(EAST, Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.east()), Direction.EAST)))
			.with(SOUTH, Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.south()), Direction.SOUTH)))
			.with(WEST, Boolean.valueOf(this.shouldConnectTo(blockView.getBlockState(blockPos.west()), Direction.WEST)));
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return direction.getAxis().isHorizontal()
			? state.with((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(this.shouldConnectTo(neighborState, direction)))
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock())) {
			this.update(world, pos, state);
		}
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.isOf(newState.getBlock())) {
			this.update(world, pos, state.with(POWERED, Boolean.valueOf(true)));
		}
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient && !player.getMainHandStack().isEmpty() && player.getMainHandStack().isOf(Items.SHEARS)) {
			world.setBlockState(pos, state.with(DISARMED, Boolean.valueOf(true)), Block.NO_REDRAW);
			world.emitGameEvent(player, GameEvent.SHEAR, pos);
		}

		return super.onBreak(world, pos, state, player);
	}

	private void update(World world, BlockPos pos, BlockState state) {
		for (Direction direction : new Direction[]{Direction.SOUTH, Direction.WEST}) {
			for (int i = 1; i < 42; i++) {
				BlockPos blockPos = pos.offset(direction, i);
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.isOf(this.hookBlock)) {
					if (blockState.get(TripwireHookBlock.FACING) == direction.getOpposite()) {
						TripwireHookBlock.update(world, blockPos, blockState, false, true, i, state);
					}
					break;
				}

				if (!blockState.isOf(this)) {
					break;
				}
			}
		}
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient) {
			if (!(Boolean)state.get(POWERED)) {
				this.updatePowered(world, pos);
			}
		}
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Boolean)world.getBlockState(pos).get(POWERED)) {
			this.updatePowered(world, pos);
		}
	}

	private void updatePowered(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		boolean bl = (Boolean)blockState.get(POWERED);
		boolean bl2 = false;
		List<? extends Entity> list = world.getOtherEntities(null, blockState.getOutlineShape(world, pos).getBoundingBox().offset(pos));
		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.canAvoidTraps()) {
					bl2 = true;
					break;
				}
			}
		}

		if (bl2 != bl) {
			blockState = blockState.with(POWERED, Boolean.valueOf(bl2));
			world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
			this.update(world, pos, blockState);
		}

		if (bl2) {
			world.scheduleBlockTick(new BlockPos(pos), this, 10);
		}
	}

	public boolean shouldConnectTo(BlockState state, Direction facing) {
		return state.isOf(this.hookBlock) ? state.get(TripwireHookBlock.FACING) == facing.getOpposite() : state.isOf(this);
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.with(NORTH, (Boolean)state.get(SOUTH))
					.with(EAST, (Boolean)state.get(WEST))
					.with(SOUTH, (Boolean)state.get(NORTH))
					.with(WEST, (Boolean)state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, (Boolean)state.get(EAST))
					.with(EAST, (Boolean)state.get(SOUTH))
					.with(SOUTH, (Boolean)state.get(WEST))
					.with(WEST, (Boolean)state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, (Boolean)state.get(WEST))
					.with(EAST, (Boolean)state.get(NORTH))
					.with(SOUTH, (Boolean)state.get(EAST))
					.with(WEST, (Boolean)state.get(SOUTH));
			default:
				return state;
		}
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.with(NORTH, (Boolean)state.get(SOUTH)).with(SOUTH, (Boolean)state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, (Boolean)state.get(WEST)).with(WEST, (Boolean)state.get(EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
	}
}
