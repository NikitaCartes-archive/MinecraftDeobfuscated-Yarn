package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class WallBannerBlock extends AbstractBannerBlock {
	public static final DirectionProperty field_11722 = HorizontalFacingBlock.field_11177;
	private static final Map<Direction, VoxelShape> field_11723 = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			Block.createCubeShape(0.0, 0.0, 14.0, 16.0, 12.5, 16.0),
			Direction.SOUTH,
			Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 12.5, 2.0),
			Direction.WEST,
			Block.createCubeShape(14.0, 0.0, 0.0, 16.0, 12.5, 16.0),
			Direction.EAST,
			Block.createCubeShape(0.0, 0.0, 0.0, 2.0, 12.5, 16.0)
		)
	);

	public WallBannerBlock(DyeColor dyeColor, Block.Settings settings) {
		super(dyeColor, settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11722, Direction.NORTH));
	}

	@Override
	public String getTranslationKey() {
		return this.getItem().getTranslationKey();
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.getBlockState(blockPos.offset(((Direction)blockState.get(field_11722)).getOpposite())).getMaterial().method_15799();
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction == ((Direction)blockState.get(field_11722)).getOpposite() && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return (VoxelShape)field_11723.get(blockState.get(field_11722));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getDefaultState();
		ViewableWorld viewableWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
		Direction[] directions = itemPlacementContext.getPlacementFacings();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.with(field_11722, direction2);
				if (blockState.canPlaceAt(viewableWorld, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_11722, rotation.method_10503(blockState.get(field_11722)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.getRotation(blockState.get(field_11722)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11722);
	}
}
