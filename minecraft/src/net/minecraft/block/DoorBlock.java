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
import net.minecraft.util.ActionResult;
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
			this.stateManager
				.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(OPEN, Boolean.valueOf(false))
				.with(HINGE, DoorHinge.LEFT)
				.with(POWERED, Boolean.valueOf(false))
				.with(HALF, DoubleBlockHalf.LOWER)
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		Direction direction = state.get(FACING);
		boolean bl = !(Boolean)state.get(OPEN);
		boolean bl2 = state.get(HINGE) == DoorHinge.RIGHT;
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
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		DoubleBlockHalf doubleBlockHalf = state.get(HALF);
		if (facing.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.LOWER != (facing == Direction.UP)) {
			return doubleBlockHalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canPlaceAt(world, pos)
				? Blocks.AIR.getDefaultState()
				: super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
		} else {
			return neighborState.getBlock() == this && neighborState.get(HALF) != doubleBlockHalf
				? state.with(FACING, neighborState.get(FACING))
					.with(OPEN, neighborState.get(OPEN))
					.with(HINGE, neighborState.get(HINGE))
					.with(POWERED, neighborState.get(POWERED))
				: Blocks.AIR.getDefaultState();
		}
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf doubleBlockHalf = state.get(HALF);
		BlockPos blockPos = doubleBlockHalf == DoubleBlockHalf.LOWER ? pos.up() : pos.method_10074();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == this && blockState.get(HALF) != doubleBlockHalf) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
			world.playLevelEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
			ItemStack itemStack = player.getMainHandStack();
			if (!world.isClient && !player.isCreative() && player.isUsingEffectiveTool(blockState)) {
				Block.dropStacks(state, world, pos, null, player, itemStack);
				Block.dropStacks(blockState, world, blockPos, null, player, itemStack);
			}
		}

		super.onBreak(world, pos, state, player);
	}

	@Override
	public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
		switch (env) {
			case LAND:
				return (Boolean)world.get(OPEN);
			case WATER:
				return false;
			case AIR:
				return (Boolean)world.get(OPEN);
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
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		if (blockPos.getY() < 255 && ctx.getWorld().getBlockState(blockPos.up()).canReplace(ctx)) {
			World world = ctx.getWorld();
			boolean bl = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
			return this.getDefaultState()
				.with(FACING, ctx.getPlayerFacing())
				.with(HINGE, this.getHinge(ctx))
				.with(POWERED, Boolean.valueOf(bl))
				.with(OPEN, Boolean.valueOf(bl))
				.with(HALF, DoubleBlockHalf.LOWER);
		} else {
			return null;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
	}

	private DoorHinge getHinge(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		Direction direction = ctx.getPlayerFacing();
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
		int i = (blockState.isFullCube(blockView, blockPos3) ? -1 : 0)
			+ (blockState2.isFullCube(blockView, blockPos4) ? -1 : 0)
			+ (blockState3.isFullCube(blockView, blockPos5) ? 1 : 0)
			+ (blockState4.isFullCube(blockView, blockPos6) ? 1 : 0);
		boolean bl = blockState.getBlock() == this && blockState.get(HALF) == DoubleBlockHalf.LOWER;
		boolean bl2 = blockState3.getBlock() == this && blockState3.get(HALF) == DoubleBlockHalf.LOWER;
		if ((!bl || bl2) && i <= 0) {
			if ((!bl2 || bl) && i >= 0) {
				int j = direction.getOffsetX();
				int k = direction.getOffsetZ();
				Vec3d vec3d = ctx.getHitPos();
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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (this.material == Material.METAL) {
			return ActionResult.PASS;
		} else {
			state = state.cycle(OPEN);
			world.setBlockState(pos, state, 10);
			world.playLevelEvent(player, state.get(OPEN) ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), pos, 0);
			return ActionResult.SUCCESS;
		}
	}

	public void setOpen(World world, BlockPos pos, boolean open) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == this && (Boolean)blockState.get(OPEN) != open) {
			world.setBlockState(pos, blockState.with(OPEN, Boolean.valueOf(open)), 10);
			this.playOpenCloseSound(world, pos, open);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		boolean bl = world.isReceivingRedstonePower(pos)
			|| world.isReceivingRedstonePower(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (block != this && bl != (Boolean)state.get(POWERED)) {
			if (bl != (Boolean)state.get(OPEN)) {
				this.playOpenCloseSound(world, pos, bl);
			}

			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl)).with(OPEN, Boolean.valueOf(bl)), 2);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.method_10074();
		BlockState blockState = world.getBlockState(blockPos);
		return state.get(HALF) == DoubleBlockHalf.LOWER ? blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) : blockState.getBlock() == this;
	}

	private void playOpenCloseSound(World world, BlockPos pos, boolean open) {
		world.playLevelEvent(null, open ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), pos, 0);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return mirror == BlockMirror.NONE ? state : state.rotate(mirror.getRotation(state.get(FACING))).cycle(HINGE);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getRenderingSeed(BlockState state, BlockPos pos) {
		return MathHelper.hashCode(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING, OPEN, HINGE, POWERED);
	}
}
