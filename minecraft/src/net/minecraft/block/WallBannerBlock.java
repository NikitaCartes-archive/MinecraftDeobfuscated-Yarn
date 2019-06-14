package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class WallBannerBlock extends AbstractBannerBlock {
	public static final DirectionProperty field_11722 = HorizontalFacingBlock.field_11177;
	private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.field_11043,
			Block.method_9541(0.0, 0.0, 14.0, 16.0, 12.5, 16.0),
			Direction.field_11035,
			Block.method_9541(0.0, 0.0, 0.0, 16.0, 12.5, 2.0),
			Direction.field_11039,
			Block.method_9541(14.0, 0.0, 0.0, 16.0, 12.5, 16.0),
			Direction.field_11034,
			Block.method_9541(0.0, 0.0, 0.0, 2.0, 12.5, 16.0)
		)
	);

	public WallBannerBlock(DyeColor dyeColor, Block.Settings settings) {
		super(dyeColor, settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11722, Direction.field_11043));
	}

	@Override
	public String getTranslationKey() {
		return this.asItem().getTranslationKey();
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.method_8320(blockPos.offset(((Direction)blockState.method_11654(field_11722)).getOpposite())).method_11620().isSolid();
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction == ((Direction)blockState.method_11654(field_11722)).getOpposite() && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return (VoxelShape)FACING_TO_SHAPE.get(blockState.method_11654(field_11722));
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.method_9564();
		ViewableWorld viewableWorld = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		Direction[] directions = itemPlacementContext.getPlacementDirections();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.method_11657(field_11722, direction2);
				if (blockState.canPlaceAt(viewableWorld, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_11722, blockRotation.rotate(blockState.method_11654(field_11722)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_11722)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11722);
	}
}
