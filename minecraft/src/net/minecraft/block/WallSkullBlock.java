package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class WallSkullBlock extends AbstractSkullBlock {
	public static final DirectionProperty field_11724 = HorizontalFacingBlock.field_11177;
	private static final Map<Direction, VoxelShape> field_11725 = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			Block.createCubeShape(4.0, 4.0, 8.0, 12.0, 12.0, 16.0),
			Direction.SOUTH,
			Block.createCubeShape(4.0, 4.0, 0.0, 12.0, 12.0, 8.0),
			Direction.EAST,
			Block.createCubeShape(0.0, 4.0, 4.0, 8.0, 12.0, 12.0),
			Direction.WEST,
			Block.createCubeShape(8.0, 4.0, 4.0, 16.0, 12.0, 12.0)
		)
	);

	protected WallSkullBlock(SkullBlock.SkullType skullType, Block.Settings settings) {
		super(skullType, settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11724, Direction.NORTH));
	}

	@Override
	public String getTranslationKey() {
		return this.getItem().getTranslationKey();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return (VoxelShape)field_11725.get(blockState.get(field_11724));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getDefaultState();
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
		Direction[] directions = itemPlacementContext.getPlacementFacings();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.with(field_11724, direction2);
				if (!blockView.getBlockState(blockPos.offset(direction)).method_11587(itemPlacementContext)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_11724, rotation.method_10503(blockState.get(field_11724)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.getRotation(blockState.get(field_11724)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11724);
	}
}
