package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class DoorBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final EnumProperty<DoorHinge> HINGE = Properties.DOOR_HINGE;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);

	protected DoorBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(OPEN, Boolean.valueOf(false))
				.with(HINGE, DoorHinge.LEFT)
				.with(POWERED, Boolean.valueOf(false))
				.with(HALF, DoubleBlockHalf.LOWER)
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		Direction direction = blockState.get(FACING);
		boolean bl = !(Boolean)blockState.get(OPEN);
		boolean bl2 = blockState.get(HINGE) == DoorHinge.RIGHT;
		switch (direction) {
			case EAST:
			default:
				return bl ? WEST_SHAPE : (bl2 ? SOUTH_SHAPE : NORTH_SHAPE);
			case SOUTH:
				return bl ? NORTH_SHAPE : (bl2 ? WEST_SHAPE : EAST_SHAPE);
			case WEST:
				return bl ? EAST_SHAPE : (bl2 ? NORTH_SHAPE : SOUTH_SHAPE);
			case NORTH:
				return bl ? SOUTH_SHAPE : (bl2 ? EAST_SHAPE : WEST_SHAPE);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		DoubleBlockHalf doubleBlockHalf = blockState.get(HALF);
		if (direction.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.LOWER != (direction == Direction.UP)) {
			return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)
				? Blocks.AIR.getDefaultState()
				: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			return blockState2.getBlock() == this && blockState2.get(HALF) != doubleBlockHalf
				? blockState.with(FACING, blockState2.get(FACING))
					.with(OPEN, blockState2.get(OPEN))
					.with(HINGE, blockState2.get(HINGE))
					.with(POWERED, blockState2.get(POWERED))
				: Blocks.AIR.getDefaultState();
		}
	}

	@Override
	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.afterBreak(world, playerEntity, blockPos, Blocks.AIR.getDefaultState(), blockEntity, itemStack);
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		DoubleBlockHalf doubleBlockHalf = blockState.get(HALF);
		BlockPos blockPos2 = doubleBlockHalf == DoubleBlockHalf.LOWER ? blockPos.up() : blockPos.method_10074();
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (blockState2.getBlock() == this && blockState2.get(HALF) != doubleBlockHalf) {
			world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 35);
			world.playLevelEvent(playerEntity, 2001, blockPos2, Block.getRawIdFromState(blockState2));
			ItemStack itemStack = playerEntity.getMainHandStack();
			if (!world.isClient && !playerEntity.isCreative() && playerEntity.isUsingEffectiveTool(blockState2)) {
				Block.dropStacks(blockState, world, blockPos, null, playerEntity, itemStack);
				Block.dropStacks(blockState2, world, blockPos2, null, playerEntity, itemStack);
			}
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case LAND:
				return (Boolean)blockState.get(OPEN);
			case WATER:
				return false;
			case AIR:
				return (Boolean)blockState.get(OPEN);
			default:
				return false;
		}
	}

	private int getOpenSoundEventId() {
		return this.material == Material.METAL ? 1011 : 1012;
	}

	private int getCloseSoundEventId() {
		return this.material == Material.METAL ? 1005 : 1006;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		if (blockPos.getY() < 255 && itemPlacementContext.getWorld().getBlockState(blockPos.up()).canReplace(itemPlacementContext)) {
			World world = itemPlacementContext.getWorld();
			boolean bl = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
			return this.getDefaultState()
				.with(FACING, itemPlacementContext.getPlayerFacing())
				.with(HINGE, this.getHinge(itemPlacementContext))
				.with(POWERED, Boolean.valueOf(bl))
				.with(OPEN, Boolean.valueOf(bl))
				.with(HALF, DoubleBlockHalf.LOWER);
		} else {
			return null;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		world.setBlockState(blockPos.up(), blockState.with(HALF, DoubleBlockHalf.UPPER), 3);
	}

	private DoorHinge getHinge(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		Direction direction = itemPlacementContext.getPlayerFacing();
		BlockPos blockPos2 = blockPos.up();
		Direction direction2 = direction.rotateYCounterclockwise();
		BlockPos blockPos3 = blockPos.offset(direction2);
		BlockState blockState = blockView.getBlockState(blockPos3);
		BlockPos blockPos4 = blockPos2.offset(direction2);
		BlockState blockState2 = blockView.getBlockState(blockPos4);
		Direction direction3 = direction.rotateYClockwise();
		BlockPos blockPos5 = blockPos.offset(direction3);
		BlockState blockState3 = blockView.getBlockState(blockPos5);
		BlockPos blockPos6 = blockPos2.offset(direction3);
		BlockState blockState4 = blockView.getBlockState(blockPos6);
		int i = (blockState.method_21743(blockView, blockPos3) ? -1 : 0)
			+ (blockState2.method_21743(blockView, blockPos4) ? -1 : 0)
			+ (blockState3.method_21743(blockView, blockPos5) ? 1 : 0)
			+ (blockState4.method_21743(blockView, blockPos6) ? 1 : 0);
		boolean bl = blockState.getBlock() == this && blockState.get(HALF) == DoubleBlockHalf.LOWER;
		boolean bl2 = blockState3.getBlock() == this && blockState3.get(HALF) == DoubleBlockHalf.LOWER;
		if ((!bl || bl2) && i <= 0) {
			if ((!bl2 || bl) && i >= 0) {
				int j = direction.getOffsetX();
				int k = direction.getOffsetZ();
				Vec3d vec3d = itemPlacementContext.getHitPos();
				double d = vec3d.x - (double)blockPos.getX();
				double e = vec3d.z - (double)blockPos.getZ();
				return (j >= 0 || !(e < 0.5)) && (j <= 0 || !(e > 0.5)) && (k >= 0 || !(d > 0.5)) && (k <= 0 || !(d < 0.5)) ? DoorHinge.LEFT : DoorHinge.RIGHT;
			} else {
				return DoorHinge.LEFT;
			}
		} else {
			return DoorHinge.RIGHT;
		}
	}

	@Override
	public boolean onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (this.material == Material.METAL) {
			return false;
		} else {
			blockState = blockState.cycle(OPEN);
			world.setBlockState(blockPos, blockState, 10);
			world.playLevelEvent(playerEntity, blockState.get(OPEN) ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), blockPos, 0);
			return true;
		}
	}

	public void setOpen(World world, BlockPos blockPos, boolean bl) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == this && (Boolean)blockState.get(OPEN) != bl) {
			world.setBlockState(blockPos, blockState.with(OPEN, Boolean.valueOf(bl)), 10);
			this.playOpenCloseSound(world, blockPos, bl);
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		boolean bl2 = world.isReceivingRedstonePower(blockPos)
			|| world.isReceivingRedstonePower(blockPos.offset(blockState.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (block != this && bl2 != (Boolean)blockState.get(POWERED)) {
			if (bl2 != (Boolean)blockState.get(OPEN)) {
				this.playOpenCloseSound(world, blockPos, bl2);
			}

			world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(bl2)).with(OPEN, Boolean.valueOf(bl2)), 2);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.method_10074();
		BlockState blockState2 = worldView.getBlockState(blockPos2);
		return blockState.get(HALF) == DoubleBlockHalf.LOWER ? blockState2.isSideSolidFullSquare(worldView, blockPos2, Direction.UP) : blockState2.getBlock() == this;
	}

	private void playOpenCloseSound(World world, BlockPos blockPos, boolean bl) {
		world.playLevelEvent(null, bl ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), blockPos, 0);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.DESTROY;
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockMirror == BlockMirror.NONE ? blockState : blockState.rotate(blockMirror.getRotation(blockState.get(FACING))).cycle(HINGE);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getRenderingSeed(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(blockPos.getX(), blockPos.down(blockState.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), blockPos.getZ());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING, OPEN, HINGE, POWERED);
	}
}
