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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class WallSkullBlock extends AbstractSkullBlock {
	public static final DirectionProperty field_11724 = HorizontalFacingBlock.field_11177;
	private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.field_11043,
			Block.method_9541(4.0, 4.0, 8.0, 12.0, 12.0, 16.0),
			Direction.field_11035,
			Block.method_9541(4.0, 4.0, 0.0, 12.0, 12.0, 8.0),
			Direction.field_11034,
			Block.method_9541(0.0, 4.0, 4.0, 8.0, 12.0, 12.0),
			Direction.field_11039,
			Block.method_9541(8.0, 4.0, 4.0, 16.0, 12.0, 12.0)
		)
	);

	protected WallSkullBlock(SkullBlock.SkullType skullType, Block.Settings settings) {
		super(skullType, settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11724, Direction.field_11043));
	}

	@Override
	public String getTranslationKey() {
		return this.asItem().getTranslationKey();
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return (VoxelShape)FACING_TO_SHAPE.get(blockState.method_11654(field_11724));
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.method_9564();
		BlockView blockView = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		Direction[] directions = itemPlacementContext.getPlacementDirections();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.method_11657(field_11724, direction2);
				if (!blockView.method_8320(blockPos.offset(direction)).canReplace(itemPlacementContext)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_11724, blockRotation.rotate(blockState.method_11654(field_11724)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_11724)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11724);
	}
}
