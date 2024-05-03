package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

public class FenceGateBlock extends HorizontalFacingBlock {
	public static final MapCodec<FenceGateBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(WoodType.CODEC.fieldOf("wood_type").forGetter(block -> block.type), createSettingsCodec()).apply(instance, FenceGateBlock::new)
	);
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty IN_WALL = Properties.IN_WALL;
	protected static final VoxelShape Z_AXIS_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape X_AXIS_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
	protected static final VoxelShape IN_WALL_Z_AXIS_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
	protected static final VoxelShape IN_WALL_X_AXIS_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
	protected static final VoxelShape Z_AXIS_COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
	protected static final VoxelShape X_AXIS_COLLISION_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
	protected static final VoxelShape Z_AXIS_SIDES_SHAPE = Block.createCuboidShape(0.0, 5.0, 6.0, 16.0, 24.0, 10.0);
	protected static final VoxelShape X_AXIS_SIDES_SHAPE = Block.createCuboidShape(6.0, 5.0, 0.0, 10.0, 24.0, 16.0);
	protected static final VoxelShape Z_AXIS_CULL_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.createCuboidShape(14.0, 5.0, 7.0, 16.0, 16.0, 9.0)
	);
	protected static final VoxelShape X_AXIS_CULL_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), Block.createCuboidShape(7.0, 5.0, 14.0, 9.0, 16.0, 16.0)
	);
	protected static final VoxelShape IN_WALL_Z_AXIS_CULL_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), Block.createCuboidShape(14.0, 2.0, 7.0, 16.0, 13.0, 9.0)
	);
	protected static final VoxelShape IN_WALL_X_AXIS_CULL_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), Block.createCuboidShape(7.0, 2.0, 14.0, 9.0, 13.0, 16.0)
	);
	private final WoodType type;

	@Override
	public MapCodec<FenceGateBlock> getCodec() {
		return CODEC;
	}

	public FenceGateBlock(WoodType type, AbstractBlock.Settings settings) {
		super(settings.sounds(type.soundType()));
		this.type = type;
		this.setDefaultState(
			this.stateManager.getDefaultState().with(OPEN, Boolean.valueOf(false)).with(POWERED, Boolean.valueOf(false)).with(IN_WALL, Boolean.valueOf(false))
		);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if ((Boolean)state.get(IN_WALL)) {
			return ((Direction)state.get(FACING)).getAxis() == Direction.Axis.X ? IN_WALL_X_AXIS_SHAPE : IN_WALL_Z_AXIS_SHAPE;
		} else {
			return ((Direction)state.get(FACING)).getAxis() == Direction.Axis.X ? X_AXIS_SHAPE : Z_AXIS_SHAPE;
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		Direction.Axis axis = direction.getAxis();
		if (((Direction)state.get(FACING)).rotateYClockwise().getAxis() != axis) {
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			boolean bl = this.isWall(neighborState) || this.isWall(world.getBlockState(pos.offset(direction.getOpposite())));
			return state.with(IN_WALL, Boolean.valueOf(bl));
		}
	}

	@Override
	protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		if ((Boolean)state.get(OPEN)) {
			return VoxelShapes.empty();
		} else {
			return ((Direction)state.get(FACING)).getAxis() == Direction.Axis.Z ? Z_AXIS_SIDES_SHAPE : X_AXIS_SIDES_SHAPE;
		}
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if ((Boolean)state.get(OPEN)) {
			return VoxelShapes.empty();
		} else {
			return ((Direction)state.get(FACING)).getAxis() == Direction.Axis.Z ? Z_AXIS_COLLISION_SHAPE : X_AXIS_COLLISION_SHAPE;
		}
	}

	@Override
	protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		if ((Boolean)state.get(IN_WALL)) {
			return ((Direction)state.get(FACING)).getAxis() == Direction.Axis.X ? IN_WALL_X_AXIS_CULL_SHAPE : IN_WALL_Z_AXIS_CULL_SHAPE;
		} else {
			return ((Direction)state.get(FACING)).getAxis() == Direction.Axis.X ? X_AXIS_CULL_SHAPE : Z_AXIS_CULL_SHAPE;
		}
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		switch (type) {
			case LAND:
				return (Boolean)state.get(OPEN);
			case WATER:
				return false;
			case AIR:
				return (Boolean)state.get(OPEN);
			default:
				return false;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		World world = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		boolean bl = world.isReceivingRedstonePower(blockPos);
		Direction direction = ctx.getHorizontalPlayerFacing();
		Direction.Axis axis = direction.getAxis();
		boolean bl2 = axis == Direction.Axis.Z && (this.isWall(world.getBlockState(blockPos.west())) || this.isWall(world.getBlockState(blockPos.east())))
			|| axis == Direction.Axis.X && (this.isWall(world.getBlockState(blockPos.north())) || this.isWall(world.getBlockState(blockPos.south())));
		return this.getDefaultState().with(FACING, direction).with(OPEN, Boolean.valueOf(bl)).with(POWERED, Boolean.valueOf(bl)).with(IN_WALL, Boolean.valueOf(bl2));
	}

	private boolean isWall(BlockState state) {
		return state.isIn(BlockTags.WALLS);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if ((Boolean)state.get(OPEN)) {
			state = state.with(OPEN, Boolean.valueOf(false));
			world.setBlockState(pos, state, Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD);
		} else {
			Direction direction = player.getHorizontalFacing();
			if (state.get(FACING) == direction.getOpposite()) {
				state = state.with(FACING, direction);
			}

			state = state.with(OPEN, Boolean.valueOf(true));
			world.setBlockState(pos, state, Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD);
		}

		boolean bl = (Boolean)state.get(OPEN);
		world.playSound(
			player, pos, bl ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F
		);
		world.emitGameEvent(player, bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		return ActionResult.success(world.isClient);
	}

	@Override
	protected void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
		if (explosion.canTriggerBlocks() && !(Boolean)state.get(POWERED)) {
			boolean bl = (Boolean)state.get(OPEN);
			world.setBlockState(pos, state.with(OPEN, Boolean.valueOf(!bl)));
			world.playSound(
				null, pos, bl ? this.type.fenceGateClose() : this.type.fenceGateOpen(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F
			);
			world.emitGameEvent(bl ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN, pos, GameEvent.Emitter.of(state));
		}

		super.onExploded(state, world, pos, explosion, stackMerger);
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (!world.isClient) {
			boolean bl = world.isReceivingRedstonePower(pos);
			if ((Boolean)state.get(POWERED) != bl) {
				world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl)).with(OPEN, Boolean.valueOf(bl)), Block.NOTIFY_LISTENERS);
				if ((Boolean)state.get(OPEN) != bl) {
					world.playSound(
						null, pos, bl ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F
					);
					world.emitGameEvent(null, bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
				}
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN, POWERED, IN_WALL);
	}

	public static boolean canWallConnect(BlockState state, Direction side) {
		return ((Direction)state.get(FACING)).getAxis() == side.rotateYClockwise().getAxis();
	}
}
