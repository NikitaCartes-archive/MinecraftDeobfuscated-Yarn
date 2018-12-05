package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class BellBlock extends BlockWithEntity {
	public static final DirectionProperty field_16324 = HorizontalFacingBlock.field_11177;
	public static final EnumProperty<WallMountLocation> field_16326 = Properties.WALL_MOUNT_LOCAITON;
	public static final VoxelShape field_16325 = Block.createCubeShape(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
	public static final VoxelShape field_16322 = Block.createCubeShape(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	public static final VoxelShape field_16321 = Block.createCubeShape(0.0, 4.0, 4.0, 16.0, 16.0, 12.0);
	public static final VoxelShape field_16323 = Block.createCubeShape(4.0, 4.0, 0.0, 12.0, 16.0, 16.0);

	public BellBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_16324, Direction.NORTH).with(field_16326, WallMountLocation.field_12475));
	}

	private VoxelShape method_16116(BlockState blockState) {
		Direction direction = blockState.get(field_16324);
		WallMountLocation wallMountLocation = blockState.get(field_16326);
		if (wallMountLocation == WallMountLocation.field_12475) {
			return direction != Direction.NORTH && direction != Direction.SOUTH ? field_16322 : field_16325;
		} else {
			return direction != Direction.NORTH && direction != Direction.SOUTH ? field_16323 : field_16321;
		}
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.method_16116(blockState);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.method_16116(blockState);
	}

	@Override
	public RenderTypeBlock getRenderType(BlockState blockState) {
		return RenderTypeBlock.MODEL;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		for (Direction direction : itemPlacementContext.method_7718()) {
			if (direction.getAxis() == Direction.Axis.Y) {
				BlockState blockState = this.getDefaultState()
					.with(field_16326, direction == Direction.UP ? WallMountLocation.field_12473 : WallMountLocation.field_12475)
					.with(field_16324, itemPlacementContext.method_8042());
				if (blockState.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getPos())) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return method_16115(blockState).getOpposite() == direction && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = method_16115(blockState).getOpposite();
		BlockPos blockPos2 = blockPos.method_10093(direction);
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		Block block = blockState2.getBlock();
		if (method_9553(block)) {
			return false;
		} else {
			boolean bl = Block.method_9501(blockState2.method_11628(viewableWorld, blockPos2), direction.getOpposite());
			return direction == Direction.UP ? block == Blocks.field_10312 || bl : !method_9581(block) && bl;
		}
	}

	protected static Direction method_16115(BlockState blockState) {
		switch ((WallMountLocation)blockState.get(field_16326)) {
			case field_12473:
				return Direction.DOWN;
			case field_12475:
				return Direction.UP;
			default:
				return blockState.get(field_16324);
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_16324, field_16326);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new BellBlockEntity();
	}
}
