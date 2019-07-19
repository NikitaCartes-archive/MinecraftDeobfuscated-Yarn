package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BellBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private static final EnumProperty<Attachment> ATTACHMENT = Properties.ATTACHMENT;
	private static final VoxelShape NORTH_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
	private static final VoxelShape EAST_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	private static final VoxelShape BELL_WAIST_SHAPE = Block.createCuboidShape(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
	private static final VoxelShape BELL_LIP_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
	private static final VoxelShape BELL_SHAPE = VoxelShapes.union(BELL_LIP_SHAPE, BELL_WAIST_SHAPE);
	private static final VoxelShape NORTH_SOUTH_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
	private static final VoxelShape EAST_WEST_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final VoxelShape WEST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
	private static final VoxelShape EAST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final VoxelShape NORTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
	private static final VoxelShape SOUTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
	private static final VoxelShape HANGING_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));

	public BellBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(ATTACHMENT, Attachment.FLOOR));
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hitResult, Entity entity) {
		if (entity instanceof ProjectileEntity) {
			Entity entity2 = ((ProjectileEntity)entity).getOwner();
			PlayerEntity playerEntity = entity2 instanceof PlayerEntity ? (PlayerEntity)entity2 : null;
			this.ring(world, state, world.getBlockEntity(hitResult.getBlockPos()), hitResult, playerEntity, true);
		}
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return this.ring(world, state, world.getBlockEntity(pos), hit, player, true);
	}

	public boolean ring(
		World world, BlockState state, @Nullable BlockEntity blockEntity, BlockHitResult hitPosition, @Nullable PlayerEntity player, boolean checkHitPosition
	) {
		Direction direction = hitPosition.getSide();
		BlockPos blockPos = hitPosition.getBlockPos();
		boolean bl = !checkHitPosition || this.isPointOnBell(state, direction, hitPosition.getPos().y - (double)blockPos.getY());
		if (!world.isClient && blockEntity instanceof BellBlockEntity && bl) {
			((BellBlockEntity)blockEntity).activate(direction);
			this.ring(world, blockPos);
			if (player != null) {
				player.incrementStat(Stats.BELL_RING);
			}

			return true;
		} else {
			return true;
		}
	}

	private boolean isPointOnBell(BlockState state, Direction side, double y) {
		if (side.getAxis() != Direction.Axis.Y && !(y > 0.8124F)) {
			Direction direction = state.get(FACING);
			Attachment attachment = state.get(ATTACHMENT);
			switch (attachment) {
				case FLOOR:
					return direction.getAxis() == side.getAxis();
				case SINGLE_WALL:
				case DOUBLE_WALL:
					return direction.getAxis() != side.getAxis();
				case CEILING:
					return true;
				default:
					return false;
			}
		} else {
			return false;
		}
	}

	private void ring(World world, BlockPos pos) {
		world.playSound(null, pos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0F, 1.0F);
	}

	private VoxelShape getShape(BlockState state) {
		Direction direction = state.get(FACING);
		Attachment attachment = state.get(ATTACHMENT);
		if (attachment == Attachment.FLOOR) {
			return direction != Direction.NORTH && direction != Direction.SOUTH ? EAST_WEST_SHAPE : NORTH_SOUTH_SHAPE;
		} else if (attachment == Attachment.CEILING) {
			return HANGING_SHAPE;
		} else if (attachment == Attachment.DOUBLE_WALL) {
			return direction != Direction.NORTH && direction != Direction.SOUTH ? EAST_WEST_WALLS_SHAPE : NORTH_SOUTH_WALLS_SHAPE;
		} else if (direction == Direction.NORTH) {
			return NORTH_WALL_SHAPE;
		} else if (direction == Direction.SOUTH) {
			return SOUTH_WALL_SHAPE;
		} else {
			return direction == Direction.EAST ? EAST_WALL_SHAPE : WEST_WALL_SHAPE;
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return this.getShape(state);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return this.getShape(state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide();
		BlockPos blockPos = ctx.getBlockPos();
		World world = ctx.getWorld();
		Direction.Axis axis = direction.getAxis();
		if (axis == Direction.Axis.Y) {
			BlockState blockState = this.getDefaultState()
				.with(ATTACHMENT, direction == Direction.DOWN ? Attachment.CEILING : Attachment.FLOOR)
				.with(FACING, ctx.getPlayerFacing());
			if (blockState.canPlaceAt(ctx.getWorld(), blockPos)) {
				return blockState;
			}
		} else {
			boolean bl = axis == Direction.Axis.X
					&& world.getBlockState(blockPos.west()).isSideSolidFullSquare(world, blockPos.west(), Direction.EAST)
					&& world.getBlockState(blockPos.east()).isSideSolidFullSquare(world, blockPos.east(), Direction.WEST)
				|| axis == Direction.Axis.Z
					&& world.getBlockState(blockPos.north()).isSideSolidFullSquare(world, blockPos.north(), Direction.SOUTH)
					&& world.getBlockState(blockPos.south()).isSideSolidFullSquare(world, blockPos.south(), Direction.NORTH);
			BlockState blockState = this.getDefaultState().with(FACING, direction.getOpposite()).with(ATTACHMENT, bl ? Attachment.DOUBLE_WALL : Attachment.SINGLE_WALL);
			if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
				return blockState;
			}

			boolean bl2 = world.getBlockState(blockPos.down()).isSideSolidFullSquare(world, blockPos.down(), Direction.UP);
			blockState = blockState.with(ATTACHMENT, bl2 ? Attachment.FLOOR : Attachment.CEILING);
			if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		Attachment attachment = state.get(ATTACHMENT);
		Direction direction = getPlacementSide(state).getOpposite();
		if (direction == facing && !state.canPlaceAt(world, pos) && attachment != Attachment.DOUBLE_WALL) {
			return Blocks.AIR.getDefaultState();
		} else {
			if (facing.getAxis() == ((Direction)state.get(FACING)).getAxis()) {
				if (attachment == Attachment.DOUBLE_WALL && !neighborState.isSideSolidFullSquare(world, neighborPos, facing)) {
					return state.with(ATTACHMENT, Attachment.SINGLE_WALL).with(FACING, facing.getOpposite());
				}

				if (attachment == Attachment.SINGLE_WALL && direction.getOpposite() == facing && neighborState.isSideSolidFullSquare(world, neighborPos, state.get(FACING))
					)
				 {
					return state.with(ATTACHMENT, Attachment.DOUBLE_WALL);
				}
			}

			return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, CollisionView world, BlockPos pos) {
		return WallMountedBlock.canPlaceAt(world, pos, getPlacementSide(state).getOpposite());
	}

	private static Direction getPlacementSide(BlockState state) {
		switch ((Attachment)state.get(ATTACHMENT)) {
			case FLOOR:
				return Direction.UP;
			case CEILING:
				return Direction.DOWN;
			default:
				return ((Direction)state.get(FACING)).getOpposite();
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, ATTACHMENT);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new BellBlockEntity();
	}

	@Override
	public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
		return false;
	}
}
