package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TrapdoorBlock extends HorizontalFacingBlock implements Waterloggable {
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape OPEN_BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
	protected static final VoxelShape OPEN_TOP_SHAPE = Block.createCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);

	protected TrapdoorBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11177, Direction.NORTH)
				.with(OPEN, Boolean.valueOf(false))
				.with(HALF, BlockHalf.BOTTOM)
				.with(POWERED, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		if (!(Boolean)blockState.get(OPEN)) {
			return blockState.get(HALF) == BlockHalf.TOP ? OPEN_TOP_SHAPE : OPEN_BOTTOM_SHAPE;
		} else {
			switch ((Direction)blockState.get(field_11177)) {
				case NORTH:
				default:
					return NORTH_SHAPE;
				case SOUTH:
					return SOUTH_SHAPE;
				case WEST:
					return WEST_SHAPE;
				case EAST:
					return EAST_SHAPE;
			}
		}
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return (Boolean)blockState.get(OPEN);
			case field_48:
				return (Boolean)blockState.get(WATERLOGGED);
			case field_51:
				return (Boolean)blockState.get(OPEN);
			default:
				return false;
		}
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (this.material == Material.METAL) {
			return false;
		} else {
			blockState = blockState.cycle(OPEN);
			world.setBlockState(blockPos, blockState, 2);
			if ((Boolean)blockState.get(WATERLOGGED)) {
				world.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}

			this.method_10740(playerEntity, world, blockPos, (Boolean)blockState.get(OPEN));
			return true;
		}
	}

	protected void method_10740(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, boolean bl) {
		if (bl) {
			int i = this.material == Material.METAL ? 1037 : 1007;
			world.playLevelEvent(playerEntity, i, blockPos, 0);
		} else {
			int i = this.material == Material.METAL ? 1036 : 1013;
			world.playLevelEvent(playerEntity, i, blockPos, 0);
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!world.isClient) {
			boolean bl2 = world.isReceivingRedstonePower(blockPos);
			if (bl2 != (Boolean)blockState.get(POWERED)) {
				if ((Boolean)blockState.get(OPEN) != bl2) {
					blockState = blockState.with(OPEN, Boolean.valueOf(bl2));
					this.method_10740(null, world, blockPos, bl2);
				}

				world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(bl2)), 2);
				if ((Boolean)blockState.get(WATERLOGGED)) {
					world.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
				}
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getDefaultState();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
		Direction direction = itemPlacementContext.getFacing();
		if (!itemPlacementContext.method_7717() && direction.getAxis().isHorizontal()) {
			blockState = blockState.with(field_11177, direction)
				.with(HALF, itemPlacementContext.getPos().y - (double)itemPlacementContext.getBlockPos().getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM);
		} else {
			blockState = blockState.with(field_11177, itemPlacementContext.getPlayerHorizontalFacing().getOpposite())
				.with(HALF, direction == Direction.UP ? BlockHalf.BOTTOM : BlockHalf.TOP);
		}

		if (itemPlacementContext.getWorld().isReceivingRedstonePower(itemPlacementContext.getBlockPos())) {
			blockState = blockState.with(OPEN, Boolean.valueOf(true)).with(POWERED, Boolean.valueOf(true));
		}

		return blockState.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11177, OPEN, HALF, POWERED, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(WATERLOGGED) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}
}
