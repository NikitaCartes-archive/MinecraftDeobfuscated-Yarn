package net.minecraft.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

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
				.with(HINGE, DoorHinge.field_12588)
				.with(POWERED, Boolean.valueOf(false))
				.with(HALF, DoubleBlockHalf.field_12607)
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Direction direction = blockState.get(FACING);
		boolean bl = !(Boolean)blockState.get(OPEN);
		boolean bl2 = blockState.get(HINGE) == DoorHinge.field_12586;
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
		if (direction.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.field_12607 != (direction == Direction.UP)) {
			return doubleBlockHalf == DoubleBlockHalf.field_12607 && direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)
				? Blocks.field_10124.getDefaultState()
				: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			return blockState2.getBlock() == this && blockState2.get(HALF) != doubleBlockHalf
				? blockState.with(FACING, blockState2.get(FACING))
					.with(OPEN, blockState2.get(OPEN))
					.with(HINGE, blockState2.get(HINGE))
					.with(POWERED, blockState2.get(POWERED))
				: Blocks.field_10124.getDefaultState();
		}
	}

	@Override
	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.afterBreak(world, playerEntity, blockPos, Blocks.field_10124.getDefaultState(), blockEntity, itemStack);
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		DoubleBlockHalf doubleBlockHalf = blockState.get(HALF);
		BlockPos blockPos2 = doubleBlockHalf == DoubleBlockHalf.field_12607 ? blockPos.up() : blockPos.down();
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (blockState2.getBlock() == this && blockState2.get(HALF) != doubleBlockHalf) {
			world.setBlockState(blockPos2, Blocks.field_10124.getDefaultState(), 35);
			world.fireWorldEvent(playerEntity, 2001, blockPos2, Block.getRawIdFromState(blockState2));
			ItemStack itemStack = playerEntity.getMainHandStack();
			if (!world.isClient && !playerEntity.isCreative()) {
				Block.dropStacks(blockState, world, blockPos, null, playerEntity, itemStack);
				Block.dropStacks(blockState2, world, blockPos2, null, playerEntity, itemStack);
			}
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return (Boolean)blockState.get(OPEN);
			case field_48:
				return false;
			case field_51:
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
		if (blockPos.getY() < 255 && itemPlacementContext.getWorld().getBlockState(blockPos.up()).method_11587(itemPlacementContext)) {
			World world = itemPlacementContext.getWorld();
			boolean bl = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
			return this.getDefaultState()
				.with(FACING, itemPlacementContext.getPlayerHorizontalFacing())
				.with(HINGE, this.getHinge(itemPlacementContext))
				.with(POWERED, Boolean.valueOf(bl))
				.with(OPEN, Boolean.valueOf(bl))
				.with(HALF, DoubleBlockHalf.field_12607);
		} else {
			return null;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		world.setBlockState(blockPos.up(), blockState.with(HALF, DoubleBlockHalf.field_12609), 3);
	}

	private DoorHinge getHinge(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		Direction direction = itemPlacementContext.getPlayerHorizontalFacing();
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
		int i = (blockState.method_11603(blockView, blockPos3) ? -1 : 0)
			+ (blockState2.method_11603(blockView, blockPos4) ? -1 : 0)
			+ (blockState3.method_11603(blockView, blockPos5) ? 1 : 0)
			+ (blockState4.method_11603(blockView, blockPos6) ? 1 : 0);
		boolean bl = blockState.getBlock() == this && blockState.get(HALF) == DoubleBlockHalf.field_12607;
		boolean bl2 = blockState3.getBlock() == this && blockState3.get(HALF) == DoubleBlockHalf.field_12607;
		if ((!bl || bl2) && i <= 0) {
			if ((!bl2 || bl) && i >= 0) {
				int j = direction.getOffsetX();
				int k = direction.getOffsetZ();
				Vec3d vec3d = itemPlacementContext.getPos();
				double d = vec3d.x - (double)blockPos.getX();
				double e = vec3d.z - (double)blockPos.getZ();
				return (j >= 0 || !(e < 0.5)) && (j <= 0 || !(e > 0.5)) && (k >= 0 || !(d > 0.5)) && (k <= 0 || !(d < 0.5)) ? DoorHinge.field_12588 : DoorHinge.field_12586;
			} else {
				return DoorHinge.field_12588;
			}
		} else {
			return DoorHinge.field_12586;
		}
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (this.material == Material.METAL) {
			return false;
		} else {
			blockState = blockState.method_11572(OPEN);
			world.setBlockState(blockPos, blockState, 10);
			world.fireWorldEvent(playerEntity, blockState.get(OPEN) ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), blockPos, 0);
			return true;
		}
	}

	public void onMobOpenedOrClosed(World world, BlockPos blockPos, boolean bl) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == this && (Boolean)blockState.get(OPEN) != bl) {
			world.setBlockState(blockPos, blockState.with(OPEN, Boolean.valueOf(bl)), 10);
			this.playOpenCloseSound(world, blockPos, bl);
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		boolean bl = world.isReceivingRedstonePower(blockPos)
			|| world.isReceivingRedstonePower(blockPos.offset(blockState.get(HALF) == DoubleBlockHalf.field_12607 ? Direction.UP : Direction.DOWN));
		if (block != this && bl != (Boolean)blockState.get(POWERED)) {
			if (bl != (Boolean)blockState.get(OPEN)) {
				this.playOpenCloseSound(world, blockPos, bl);
			}

			world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(bl)).with(OPEN, Boolean.valueOf(bl)), 2);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		return blockState.get(HALF) == DoubleBlockHalf.field_12607 ? blockState2.hasSolidTopSurface(viewableWorld, blockPos2) : blockState2.getBlock() == this;
	}

	private void playOpenCloseSound(World world, BlockPos blockPos, boolean bl) {
		world.fireWorldEvent(null, bl ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), blockPos, 0);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.with(FACING, rotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		return mirror == Mirror.NONE ? blockState : blockState.rotate(mirror.getRotation(blockState.get(FACING))).method_11572(HINGE);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getRenderingSeed(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(blockPos.getX(), blockPos.down(blockState.get(HALF) == DoubleBlockHalf.field_12607 ? 0 : 1).getY(), blockPos.getZ());
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(HALF, FACING, OPEN, HINGE, POWERED);
	}
}
