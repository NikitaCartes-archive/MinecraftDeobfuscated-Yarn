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
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
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
	public static final DirectionProperty field_10938 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_10945 = Properties.OPEN;
	public static final EnumProperty<DoorHinge> field_10941 = Properties.DOOR_HINGE;
	public static final BooleanProperty field_10940 = Properties.POWERED;
	public static final EnumProperty<DoubleBlockHalf> field_10946 = Properties.DOUBLE_BLOCK_HALF;
	protected static final VoxelShape field_10942 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape field_10939 = Block.createCubeShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_10944 = Block.createCubeShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_10943 = Block.createCubeShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);

	protected DoorBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_10938, Direction.NORTH)
				.with(field_10945, Boolean.valueOf(false))
				.with(field_10941, DoorHinge.field_12588)
				.with(field_10940, Boolean.valueOf(false))
				.with(field_10946, DoubleBlockHalf.field_12607)
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		Direction direction = blockState.get(field_10938);
		boolean bl = !(Boolean)blockState.get(field_10945);
		boolean bl2 = blockState.get(field_10941) == DoorHinge.field_12586;
		switch (direction) {
			case EAST:
			default:
				return bl ? field_10943 : (bl2 ? field_10939 : field_10942);
			case SOUTH:
				return bl ? field_10942 : (bl2 ? field_10943 : field_10944);
			case WEST:
				return bl ? field_10944 : (bl2 ? field_10942 : field_10939);
			case NORTH:
				return bl ? field_10939 : (bl2 ? field_10944 : field_10943);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		DoubleBlockHalf doubleBlockHalf = blockState.get(field_10946);
		if (direction.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.field_12607 != (direction == Direction.UP)) {
			return doubleBlockHalf == DoubleBlockHalf.field_12607 && direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)
				? Blocks.field_10124.getDefaultState()
				: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			return blockState2.getBlock() == this && blockState2.get(field_10946) != doubleBlockHalf
				? blockState.with(field_10938, blockState2.get(field_10938))
					.with(field_10945, blockState2.get(field_10945))
					.with(field_10941, blockState2.get(field_10941))
					.with(field_10940, blockState2.get(field_10940))
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
		DoubleBlockHalf doubleBlockHalf = blockState.get(field_10946);
		BlockPos blockPos2 = doubleBlockHalf == DoubleBlockHalf.field_12607 ? blockPos.up() : blockPos.down();
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (blockState2.getBlock() == this && blockState2.get(field_10946) != doubleBlockHalf) {
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
				return (Boolean)blockState.get(field_10945);
			case field_48:
				return false;
			case field_51:
				return (Boolean)blockState.get(field_10945);
			default:
				return false;
		}
	}

	private int method_10034() {
		return this.material == Material.METAL ? 1011 : 1012;
	}

	private int method_10032() {
		return this.material == Material.METAL ? 1005 : 1006;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getPos();
		if (blockPos.getY() < 255 && itemPlacementContext.getWorld().getBlockState(blockPos.up()).method_11587(itemPlacementContext)) {
			World world = itemPlacementContext.getWorld();
			boolean bl = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
			return this.getDefaultState()
				.with(field_10938, itemPlacementContext.getPlayerHorizontalFacing())
				.with(field_10941, this.method_10035(itemPlacementContext))
				.with(field_10940, Boolean.valueOf(bl))
				.with(field_10945, Boolean.valueOf(bl))
				.with(field_10946, DoubleBlockHalf.field_12607);
		} else {
			return null;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		world.setBlockState(blockPos.up(), blockState.with(field_10946, DoubleBlockHalf.field_12609), 3);
	}

	private DoorHinge method_10035(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
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
		boolean bl = blockState.getBlock() == this && blockState.get(field_10946) == DoubleBlockHalf.field_12607;
		boolean bl2 = blockState3.getBlock() == this && blockState3.get(field_10946) == DoubleBlockHalf.field_12607;
		if ((!bl || bl2) && i <= 0) {
			if ((!bl2 || bl) && i >= 0) {
				int j = direction.getOffsetX();
				int k = direction.getOffsetZ();
				Vec3d vec3d = itemPlacementContext.method_17698();
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
			blockState = blockState.method_11572(field_10945);
			world.setBlockState(blockPos, blockState, 10);
			world.fireWorldEvent(playerEntity, blockState.get(field_10945) ? this.method_10032() : this.method_10034(), blockPos, 0);
			return true;
		}
	}

	public void method_10033(World world, BlockPos blockPos, boolean bl) {
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == this && (Boolean)blockState.get(field_10945) != bl) {
			world.setBlockState(blockPos, blockState.with(field_10945, Boolean.valueOf(bl)), 10);
			this.method_10036(world, blockPos, bl);
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		boolean bl = world.isReceivingRedstonePower(blockPos)
			|| world.isReceivingRedstonePower(blockPos.offset(blockState.get(field_10946) == DoubleBlockHalf.field_12607 ? Direction.UP : Direction.DOWN));
		if (block != this && bl != (Boolean)blockState.get(field_10940)) {
			if (bl != (Boolean)blockState.get(field_10945)) {
				this.method_10036(world, blockPos, bl);
			}

			world.setBlockState(blockPos, blockState.with(field_10940, Boolean.valueOf(bl)).with(field_10945, Boolean.valueOf(bl)), 2);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		return blockState.get(field_10946) == DoubleBlockHalf.field_12607 ? blockState2.hasSolidTopSurface(viewableWorld, blockPos2) : blockState2.getBlock() == this;
	}

	private void method_10036(World world, BlockPos blockPos, boolean bl) {
		world.fireWorldEvent(null, bl ? this.method_10032() : this.method_10034(), blockPos, 0);
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
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_10938, rotation.method_10503(blockState.get(field_10938)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return mirror == Mirror.NONE ? blockState : blockState.applyRotation(mirror.getRotation(blockState.get(field_10938))).method_11572(field_10941);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getRenderingSeed(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(blockPos.getX(), blockPos.down(blockState.get(field_10946) == DoubleBlockHalf.field_12607 ? 0 : 1).getY(), blockPos.getZ());
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_10946, field_10938, field_10945, field_10941, field_10940);
	}
}
