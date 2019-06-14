package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceGateBlock extends HorizontalFacingBlock {
	public static final BooleanProperty field_11026 = Properties.field_12537;
	public static final BooleanProperty field_11021 = Properties.field_12484;
	public static final BooleanProperty field_11024 = Properties.field_12491;
	protected static final VoxelShape field_11022 = Block.method_9541(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape field_11017 = Block.method_9541(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
	protected static final VoxelShape field_11025 = Block.method_9541(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
	protected static final VoxelShape field_11016 = Block.method_9541(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
	protected static final VoxelShape field_11028 = Block.method_9541(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
	protected static final VoxelShape field_11019 = Block.method_9541(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
	protected static final VoxelShape field_11018 = VoxelShapes.method_1084(
		Block.method_9541(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.method_9541(14.0, 5.0, 7.0, 16.0, 16.0, 9.0)
	);
	protected static final VoxelShape field_11023 = VoxelShapes.method_1084(
		Block.method_9541(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), Block.method_9541(7.0, 5.0, 14.0, 9.0, 16.0, 16.0)
	);
	protected static final VoxelShape field_11020 = VoxelShapes.method_1084(
		Block.method_9541(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), Block.method_9541(14.0, 2.0, 7.0, 16.0, 13.0, 9.0)
	);
	protected static final VoxelShape field_11027 = VoxelShapes.method_1084(
		Block.method_9541(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), Block.method_9541(7.0, 2.0, 14.0, 9.0, 13.0, 16.0)
	);

	public FenceGateBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11026, Boolean.valueOf(false))
				.method_11657(field_11021, Boolean.valueOf(false))
				.method_11657(field_11024, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		if ((Boolean)blockState.method_11654(field_11024)) {
			return ((Direction)blockState.method_11654(field_11177)).getAxis() == Direction.Axis.X ? field_11016 : field_11025;
		} else {
			return ((Direction)blockState.method_11654(field_11177)).getAxis() == Direction.Axis.X ? field_11017 : field_11022;
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		Direction.Axis axis = direction.getAxis();
		if (((Direction)blockState.method_11654(field_11177)).rotateYClockwise().getAxis() != axis) {
			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			boolean bl = this.method_10138(blockState2) || this.method_10138(iWorld.method_8320(blockPos.offset(direction.getOpposite())));
			return blockState.method_11657(field_11024, Boolean.valueOf(bl));
		}
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		if ((Boolean)blockState.method_11654(field_11026)) {
			return VoxelShapes.method_1073();
		} else {
			return ((Direction)blockState.method_11654(field_11177)).getAxis() == Direction.Axis.Z ? field_11028 : field_11019;
		}
	}

	@Override
	public VoxelShape method_9571(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if ((Boolean)blockState.method_11654(field_11024)) {
			return ((Direction)blockState.method_11654(field_11177)).getAxis() == Direction.Axis.X ? field_11027 : field_11020;
		} else {
			return ((Direction)blockState.method_11654(field_11177)).getAxis() == Direction.Axis.X ? field_11023 : field_11018;
		}
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return (Boolean)blockState.method_11654(field_11026);
			case field_48:
				return false;
			case field_51:
				return (Boolean)blockState.method_11654(field_11026);
			default:
				return false;
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		World world = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		boolean bl = world.isReceivingRedstonePower(blockPos);
		Direction direction = itemPlacementContext.getPlayerFacing();
		Direction.Axis axis = direction.getAxis();
		boolean bl2 = axis == Direction.Axis.Z && (this.method_10138(world.method_8320(blockPos.west())) || this.method_10138(world.method_8320(blockPos.east())))
			|| axis == Direction.Axis.X && (this.method_10138(world.method_8320(blockPos.north())) || this.method_10138(world.method_8320(blockPos.south())));
		return this.method_9564()
			.method_11657(field_11177, direction)
			.method_11657(field_11026, Boolean.valueOf(bl))
			.method_11657(field_11021, Boolean.valueOf(bl))
			.method_11657(field_11024, Boolean.valueOf(bl2));
	}

	private boolean method_10138(BlockState blockState) {
		return blockState.getBlock().matches(BlockTags.field_15504);
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if ((Boolean)blockState.method_11654(field_11026)) {
			blockState = blockState.method_11657(field_11026, Boolean.valueOf(false));
			world.method_8652(blockPos, blockState, 10);
		} else {
			Direction direction = playerEntity.getHorizontalFacing();
			if (blockState.method_11654(field_11177) == direction.getOpposite()) {
				blockState = blockState.method_11657(field_11177, direction);
			}

			blockState = blockState.method_11657(field_11026, Boolean.valueOf(true));
			world.method_8652(blockPos, blockState, 10);
		}

		world.playLevelEvent(playerEntity, blockState.method_11654(field_11026) ? 1008 : 1014, blockPos, 0);
		return true;
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!world.isClient) {
			boolean bl2 = world.isReceivingRedstonePower(blockPos);
			if ((Boolean)blockState.method_11654(field_11021) != bl2) {
				world.method_8652(blockPos, blockState.method_11657(field_11021, Boolean.valueOf(bl2)).method_11657(field_11026, Boolean.valueOf(bl2)), 2);
				if ((Boolean)blockState.method_11654(field_11026) != bl2) {
					world.playLevelEvent(null, bl2 ? 1008 : 1014, blockPos, 0);
				}
			}
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11177, field_11026, field_11021, field_11024);
	}

	public static boolean method_16703(BlockState blockState, Direction direction) {
		return ((Direction)blockState.method_11654(field_11177)).getAxis() == direction.rotateYClockwise().getAxis();
	}
}
