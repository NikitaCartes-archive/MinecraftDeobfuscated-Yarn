package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailBlock extends AbstractRailBlock {
	public static final EnumProperty<RailShape> SHAPE = Properties.RAIL_SHAPE;

	protected RailBlock(Block.Settings settings) {
		super(false, settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH));
	}

	@Override
	protected void updateBlockState(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (block.getDefaultState().emitsRedstonePower() && new RailPlacementHelper(world, blockPos, blockState).method_10460() == 3) {
			this.updateBlockState(world, blockPos, blockState, false);
		}
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case CLOCKWISE_180:
				switch ((RailShape)blockState.get(SHAPE)) {
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
				}
			case COUNTERCLOCKWISE_90:
				switch ((RailShape)blockState.get(SHAPE)) {
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_SOUTH:
						return blockState.with(SHAPE, RailShape.EAST_WEST);
					case EAST_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_SOUTH);
				}
			case CLOCKWISE_90:
				switch ((RailShape)blockState.get(SHAPE)) {
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_SOUTH:
						return blockState.with(SHAPE, RailShape.EAST_WEST);
					case EAST_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_SOUTH);
				}
			default:
				return blockState;
		}
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		RailShape railShape = blockState.get(SHAPE);
		switch (blockMirror) {
			case LEFT_RIGHT:
				switch (railShape) {
					case ASCENDING_NORTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return blockState.with(SHAPE, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					default:
						return super.mirror(blockState, blockMirror);
				}
			case FRONT_BACK:
				switch (railShape) {
					case ASCENDING_EAST:
						return blockState.with(SHAPE, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return blockState.with(SHAPE, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
					case ASCENDING_SOUTH:
					default:
						break;
					case SOUTH_EAST:
						return blockState.with(SHAPE, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return blockState.with(SHAPE, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return blockState.with(SHAPE, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return blockState.with(SHAPE, RailShape.NORTH_WEST);
				}
		}

		return super.mirror(blockState, blockMirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(SHAPE);
	}
}
