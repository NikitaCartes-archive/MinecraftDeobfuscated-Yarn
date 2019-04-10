package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class BellBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = HorizontalFacingBlock.field_11177;
	private static final EnumProperty<Attachment> ATTACHMENT = Properties.ATTACHMENT;
	private static final VoxelShape NORTH_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
	private static final VoxelShape EAST_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	private static final VoxelShape field_17087 = Block.createCuboidShape(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
	private static final VoxelShape field_17088 = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
	private static final VoxelShape BELL_SHAPE = VoxelShapes.union(field_17088, field_17087);
	private static final VoxelShape NORTH_SOUTH_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
	private static final VoxelShape EAST_WEST_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final VoxelShape WEST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
	private static final VoxelShape EAST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final VoxelShape NORTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
	private static final VoxelShape SOUTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
	private static final VoxelShape HANGING_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));

	public BellBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH).with(ATTACHMENT, Attachment.field_17098));
	}

	@Override
	public void method_19286(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
		if (entity instanceof ProjectileEntity) {
			this.ring(world, blockState, world.getBlockEntity(blockHitResult.getBlockPos()), blockHitResult);
		}
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return this.ring(world, blockState, world.getBlockEntity(blockPos), blockHitResult);
	}

	public boolean ring(World world, BlockState blockState, @Nullable BlockEntity blockEntity, BlockHitResult blockHitResult) {
		Direction direction = blockHitResult.getSide();
		BlockPos blockPos = blockHitResult.getBlockPos();
		if (!world.isClient
			&& blockEntity instanceof BellBlockEntity
			&& this.isPointOnBell(blockState, direction, blockHitResult.getPos().y - (double)blockPos.getY())) {
			((BellBlockEntity)blockEntity).activate(direction);
			this.ring(world, blockPos);
			return true;
		} else {
			return true;
		}
	}

	private boolean isPointOnBell(BlockState blockState, Direction direction, double d) {
		if (direction.getAxis() != Direction.Axis.Y && !(d > 0.8124F)) {
			Direction direction2 = blockState.get(FACING);
			Attachment attachment = blockState.get(ATTACHMENT);
			switch (attachment) {
				case field_17098:
					return direction2.getAxis() == direction.getAxis();
				case field_17100:
				case field_17101:
					return direction2.getAxis() != direction.getAxis();
				case field_17099:
					return true;
				default:
					return false;
			}
		} else {
			return false;
		}
	}

	private void ring(World world, BlockPos blockPos) {
		world.playSound(null, blockPos, SoundEvents.field_17265, SoundCategory.field_15245, 2.0F, 1.0F);
	}

	private VoxelShape getShape(BlockState blockState) {
		Direction direction = blockState.get(FACING);
		Attachment attachment = blockState.get(ATTACHMENT);
		if (attachment == Attachment.field_17098) {
			return direction != Direction.NORTH && direction != Direction.SOUTH ? EAST_WEST_SHAPE : NORTH_SOUTH_SHAPE;
		} else if (attachment == Attachment.field_17099) {
			return HANGING_SHAPE;
		} else if (attachment == Attachment.field_17101) {
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
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.getShape(blockState);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.getShape(blockState);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.getFacing();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		World world = itemPlacementContext.getWorld();
		Direction.Axis axis = direction.getAxis();
		if (axis == Direction.Axis.Y) {
			BlockState blockState = this.getDefaultState()
				.with(ATTACHMENT, direction == Direction.DOWN ? Attachment.field_17099 : Attachment.field_17098)
				.with(FACING, itemPlacementContext.getPlayerHorizontalFacing());
			if (blockState.canPlaceAt(itemPlacementContext.getWorld(), blockPos)) {
				return blockState;
			}
		} else {
			boolean bl = axis == Direction.Axis.X
					&& isSolidFullSquare(world.getBlockState(blockPos.west()), world, blockPos.west(), Direction.EAST)
					&& isSolidFullSquare(world.getBlockState(blockPos.east()), world, blockPos.east(), Direction.WEST)
				|| axis == Direction.Axis.Z
					&& isSolidFullSquare(world.getBlockState(blockPos.north()), world, blockPos.north(), Direction.SOUTH)
					&& isSolidFullSquare(world.getBlockState(blockPos.south()), world, blockPos.south(), Direction.NORTH);
			BlockState blockState = this.getDefaultState().with(FACING, direction.getOpposite()).with(ATTACHMENT, bl ? Attachment.field_17101 : Attachment.field_17100);
			if (blockState.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())) {
				return blockState;
			}

			boolean bl2 = isSolidFullSquare(world.getBlockState(blockPos.down()), world, blockPos.down(), Direction.UP);
			blockState = blockState.with(ATTACHMENT, bl2 ? Attachment.field_17098 : Attachment.field_17099);
			if (blockState.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())) {
				return blockState;
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		Attachment attachment = blockState.get(ATTACHMENT);
		Direction direction2 = method_16115(blockState).getOpposite();
		if (direction2 == direction && !blockState.canPlaceAt(iWorld, blockPos) && attachment != Attachment.field_17101) {
			return Blocks.field_10124.getDefaultState();
		} else {
			if (direction.getAxis() == ((Direction)blockState.get(FACING)).getAxis()) {
				if (attachment == Attachment.field_17101 && !isSolidFullSquare(blockState2, iWorld, blockPos2, direction)) {
					return blockState.with(ATTACHMENT, Attachment.field_17100).with(FACING, direction.getOpposite());
				}

				if (attachment == Attachment.field_17100
					&& direction2.getOpposite() == direction
					&& isSolidFullSquare(blockState2, iWorld, blockPos2, blockState.get(FACING))) {
					return blockState.with(ATTACHMENT, Attachment.field_17101);
				}
			}

			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return WallMountedBlock.method_20046(viewableWorld, blockPos, method_16115(blockState).getOpposite());
	}

	private static Direction method_16115(BlockState blockState) {
		switch ((Attachment)blockState.get(ATTACHMENT)) {
			case field_17098:
				return Direction.UP;
			case field_17099:
				return Direction.DOWN;
			default:
				return ((Direction)blockState.get(FACING)).getOpposite();
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, ATTACHMENT);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new BellBlockEntity();
	}
}
