package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class LadderBlock extends Block implements Waterloggable {
	public static final DirectionProperty field_11253 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_11257 = Properties.WATERLOGGED;
	protected static final VoxelShape field_11255 = Block.createCubeShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	protected static final VoxelShape field_11252 = Block.createCubeShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_11254 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape field_11256 = Block.createCubeShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);

	protected LadderBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11253, Direction.NORTH).with(field_11257, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		switch ((Direction)blockState.get(field_11253)) {
			case NORTH:
				return field_11256;
			case SOUTH:
				return field_11254;
			case WEST:
				return field_11252;
			case EAST:
			default:
				return field_11255;
		}
	}

	private boolean method_10305(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState = blockView.getBlockState(blockPos);
		boolean bl = method_9581(blockState.getBlock());
		return !bl && Block.isFaceFullCube(blockState.getCollisionShape(blockView, blockPos), direction) && !blockState.emitsRedstonePower();
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = blockState.get(field_11253);
		return this.method_10305(viewableWorld, blockPos.offset(direction.getOpposite()), direction);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction.getOpposite() == blockState.get(field_11253) && !blockState.canPlaceAt(iWorld, blockPos)) {
			return Blocks.field_10124.getDefaultState();
		} else {
			if ((Boolean)blockState.get(field_11257)) {
				iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
			}

			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		if (!itemPlacementContext.method_7717()) {
			BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getPos().offset(itemPlacementContext.getFacing().getOpposite()));
			if (blockState.getBlock() == this && blockState.get(field_11253) == itemPlacementContext.getFacing()) {
				return null;
			}
		}

		BlockState blockState = this.getDefaultState();
		ViewableWorld viewableWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos());

		for (Direction direction : itemPlacementContext.getPlacementFacings()) {
			if (direction.getAxis().isHorizontal()) {
				blockState = blockState.with(field_11253, direction.getOpposite());
				if (blockState.canPlaceAt(viewableWorld, blockPos)) {
					return blockState.with(field_11257, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
				}
			}
		}

		return null;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_11253, rotation.method_10503(blockState.get(field_11253)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.getRotation(blockState.get(field_11253)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11253, field_11257);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(field_11257) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
	}
}
