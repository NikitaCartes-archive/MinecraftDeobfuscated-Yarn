package net.minecraft.block;

import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceGateBlock extends HorizontalFacingBlock {
	public static final BooleanProperty field_11026 = Properties.OPEN;
	public static final BooleanProperty field_11021 = Properties.POWERED;
	public static final BooleanProperty field_11024 = Properties.IN_WALL;
	protected static final VoxelShape field_11022 = Block.createCubeShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape field_11017 = Block.createCubeShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
	protected static final VoxelShape field_11025 = Block.createCubeShape(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
	protected static final VoxelShape field_11016 = Block.createCubeShape(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
	protected static final VoxelShape field_11028 = Block.createCubeShape(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
	protected static final VoxelShape field_11019 = Block.createCubeShape(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
	protected static final VoxelShape field_11018 = VoxelShapes.method_1084(
		Block.createCubeShape(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.createCubeShape(14.0, 5.0, 7.0, 16.0, 16.0, 9.0)
	);
	protected static final VoxelShape field_11023 = VoxelShapes.method_1084(
		Block.createCubeShape(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), Block.createCubeShape(7.0, 5.0, 14.0, 9.0, 16.0, 16.0)
	);
	protected static final VoxelShape field_11020 = VoxelShapes.method_1084(
		Block.createCubeShape(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), Block.createCubeShape(14.0, 2.0, 7.0, 16.0, 13.0, 9.0)
	);
	protected static final VoxelShape field_11027 = VoxelShapes.method_1084(
		Block.createCubeShape(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), Block.createCubeShape(7.0, 2.0, 14.0, 9.0, 13.0, 16.0)
	);

	public FenceGateBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11026, Boolean.valueOf(false))
				.with(field_11021, Boolean.valueOf(false))
				.with(field_11024, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if ((Boolean)blockState.get(field_11024)) {
			return ((Direction)blockState.get(field_11177)).getAxis() == Direction.Axis.X ? field_11016 : field_11025;
		} else {
			return ((Direction)blockState.get(field_11177)).getAxis() == Direction.Axis.X ? field_11017 : field_11022;
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		Direction.Axis axis = direction.getAxis();
		if (((Direction)blockState.get(field_11177)).rotateYClockwise().getAxis() != axis) {
			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			boolean bl = this.method_10138(blockState2) || this.method_10138(iWorld.getBlockState(blockPos.method_10093(direction.getOpposite())));
			return blockState.with(field_11024, Boolean.valueOf(bl));
		}
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		if ((Boolean)blockState.get(field_11026)) {
			return VoxelShapes.empty();
		} else {
			return ((Direction)blockState.get(field_11177)).getAxis() == Direction.Axis.Z ? field_11028 : field_11019;
		}
	}

	@Override
	public VoxelShape method_9571(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if ((Boolean)blockState.get(field_11024)) {
			return ((Direction)blockState.get(field_11177)).getAxis() == Direction.Axis.X ? field_11027 : field_11020;
		} else {
			return ((Direction)blockState.get(field_11177)).getAxis() == Direction.Axis.X ? field_11023 : field_11018;
		}
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		switch (placementEnvironment) {
			case field_50:
				return (Boolean)blockState.get(field_11026);
			case field_48:
				return false;
			case field_51:
				return (Boolean)blockState.get(field_11026);
			default:
				return false;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		World world = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
		boolean bl = world.isReceivingRedstonePower(blockPos);
		Direction direction = itemPlacementContext.method_8042();
		Direction.Axis axis = direction.getAxis();
		boolean bl2 = axis == Direction.Axis.Z
				&& (this.method_10138(world.getBlockState(blockPos.west())) || this.method_10138(world.getBlockState(blockPos.east())))
			|| axis == Direction.Axis.X && (this.method_10138(world.getBlockState(blockPos.north())) || this.method_10138(world.getBlockState(blockPos.south())));
		return this.getDefaultState()
			.with(field_11177, direction)
			.with(field_11026, Boolean.valueOf(bl))
			.with(field_11021, Boolean.valueOf(bl))
			.with(field_11024, Boolean.valueOf(bl2));
	}

	private boolean method_10138(BlockState blockState) {
		return blockState.getBlock().matches(BlockTags.field_15504);
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if ((Boolean)blockState.get(field_11026)) {
			blockState = blockState.with(field_11026, Boolean.valueOf(false));
			world.setBlockState(blockPos, blockState, 10);
		} else {
			Direction direction2 = playerEntity.method_5735();
			if (blockState.get(field_11177) == direction2.getOpposite()) {
				blockState = blockState.with(field_11177, direction2);
			}

			blockState = blockState.with(field_11026, Boolean.valueOf(true));
			world.setBlockState(blockPos, blockState, 10);
		}

		world.fireWorldEvent(playerEntity, blockState.get(field_11026) ? 1008 : 1014, blockPos, 0);
		return true;
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isRemote) {
			boolean bl = world.isReceivingRedstonePower(blockPos);
			if ((Boolean)blockState.get(field_11021) != bl) {
				world.setBlockState(blockPos, blockState.with(field_11021, Boolean.valueOf(bl)).with(field_11026, Boolean.valueOf(bl)), 2);
				if ((Boolean)blockState.get(field_11026) != bl) {
					world.fireWorldEvent(null, bl ? 1008 : 1014, blockPos, 0);
				}
			}
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11177, field_11026, field_11021, field_11024);
	}

	public static boolean method_16703(BlockState blockState, Direction direction) {
		return ((Direction)blockState.get(field_11177)).getAxis() == direction.rotateYClockwise().getAxis();
	}
}
