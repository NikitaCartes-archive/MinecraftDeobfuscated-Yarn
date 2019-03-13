package net.minecraft.block;

import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class WallBlock extends HorizontalConnectedBlock {
	public static final BooleanProperty field_11717 = Properties.field_12519;
	private final VoxelShape[] field_11718;
	private final VoxelShape[] field_11716;

	public WallBlock(Block.Settings settings) {
		super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11717, Boolean.valueOf(true))
				.method_11657(field_10905, Boolean.valueOf(false))
				.method_11657(field_10907, Boolean.valueOf(false))
				.method_11657(field_10904, Boolean.valueOf(false))
				.method_11657(field_10903, Boolean.valueOf(false))
				.method_11657(field_10900, Boolean.valueOf(false))
		);
		this.field_11718 = this.method_9984(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
		this.field_11716 = this.method_9984(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return blockState.method_11654(field_11717)
			? this.field_11718[this.method_9987(blockState)]
			: super.method_9530(blockState, blockView, blockPos, verticalEntityPosition);
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return blockState.method_11654(field_11717)
			? this.field_11716[this.method_9987(blockState)]
			: super.method_9549(blockState, blockView, blockPos, verticalEntityPosition);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	private boolean method_16704(BlockState blockState, boolean bl, Direction direction) {
		Block block = blockState.getBlock();
		boolean bl2 = block.method_9525(BlockTags.field_15504) || block instanceof FenceGateBlock && FenceGateBlock.method_16703(blockState, direction);
		return !cannotConnectTo(block) && bl || bl2;
	}

	public static boolean cannotConnectTo(Block block) {
		return Block.method_9581(block)
			|| block == Blocks.field_10499
			|| block == Blocks.field_10545
			|| block == Blocks.field_10261
			|| block == Blocks.field_10147
			|| block == Blocks.field_10009
			|| block == Blocks.field_10110
			|| block == Blocks.field_10375;
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		ViewableWorld viewableWorld = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.method_8037();
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.method_8037());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.south();
		BlockPos blockPos5 = blockPos.west();
		BlockState blockState = viewableWorld.method_8320(blockPos2);
		BlockState blockState2 = viewableWorld.method_8320(blockPos3);
		BlockState blockState3 = viewableWorld.method_8320(blockPos4);
		BlockState blockState4 = viewableWorld.method_8320(blockPos5);
		boolean bl = this.method_16704(blockState, Block.method_9501(blockState.method_11628(viewableWorld, blockPos2), Direction.SOUTH), Direction.SOUTH);
		boolean bl2 = this.method_16704(blockState2, Block.method_9501(blockState2.method_11628(viewableWorld, blockPos3), Direction.WEST), Direction.WEST);
		boolean bl3 = this.method_16704(blockState3, Block.method_9501(blockState3.method_11628(viewableWorld, blockPos4), Direction.NORTH), Direction.NORTH);
		boolean bl4 = this.method_16704(blockState4, Block.method_9501(blockState4.method_11628(viewableWorld, blockPos5), Direction.EAST), Direction.EAST);
		boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
		return this.method_9564()
			.method_11657(field_11717, Boolean.valueOf(bl5 || !viewableWorld.method_8623(blockPos.up())))
			.method_11657(field_10905, Boolean.valueOf(bl))
			.method_11657(field_10907, Boolean.valueOf(bl2))
			.method_11657(field_10904, Boolean.valueOf(bl3))
			.method_11657(field_10903, Boolean.valueOf(bl4))
			.method_11657(field_10900, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_10900)) {
			iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		if (direction == Direction.DOWN) {
			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			Direction direction2 = direction.getOpposite();
			boolean bl = direction == Direction.NORTH
				? this.method_16704(blockState2, Block.method_9501(blockState2.method_11628(iWorld, blockPos2), direction2), direction2)
				: (Boolean)blockState.method_11654(field_10905);
			boolean bl2 = direction == Direction.EAST
				? this.method_16704(blockState2, Block.method_9501(blockState2.method_11628(iWorld, blockPos2), direction2), direction2)
				: (Boolean)blockState.method_11654(field_10907);
			boolean bl3 = direction == Direction.SOUTH
				? this.method_16704(blockState2, Block.method_9501(blockState2.method_11628(iWorld, blockPos2), direction2), direction2)
				: (Boolean)blockState.method_11654(field_10904);
			boolean bl4 = direction == Direction.WEST
				? this.method_16704(blockState2, Block.method_9501(blockState2.method_11628(iWorld, blockPos2), direction2), direction2)
				: (Boolean)blockState.method_11654(field_10903);
			boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
			return blockState.method_11657(field_11717, Boolean.valueOf(bl5 || !iWorld.method_8623(blockPos.up())))
				.method_11657(field_10905, Boolean.valueOf(bl))
				.method_11657(field_10907, Boolean.valueOf(bl2))
				.method_11657(field_10904, Boolean.valueOf(bl3))
				.method_11657(field_10903, Boolean.valueOf(bl4));
		}
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11717, field_10905, field_10907, field_10903, field_10904, field_10900);
	}
}
