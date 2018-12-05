package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class LanternBlock extends Block {
	public static final BooleanProperty field_16545 = Properties.HANGING;
	protected static final VoxelShape field_16546 = VoxelShapes.method_1084(
		Block.createCubeShape(5.0, 0.0, 5.0, 11.0, 7.0, 11.0), Block.createCubeShape(6.0, 7.0, 6.0, 10.0, 9.0, 10.0)
	);
	protected static final VoxelShape field_16544 = VoxelShapes.method_1084(
		Block.createCubeShape(5.0, 1.0, 5.0, 11.0, 8.0, 11.0), Block.createCubeShape(6.0, 8.0, 6.0, 10.0, 10.0, 10.0)
	);

	public LanternBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_16545, Boolean.valueOf(false)));
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		for (Direction direction : itemPlacementContext.method_7718()) {
			if (direction.getAxis() == Direction.Axis.Y) {
				BlockState blockState = this.getDefaultState().with(field_16545, Boolean.valueOf(direction == Direction.UP));
				if (blockState.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getPos())) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(field_16545) ? field_16544 : field_16546;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_16545);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = method_16370(blockState).getOpposite();
		BlockPos blockPos2 = blockPos.method_10093(direction);
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		Block block = blockState2.getBlock();
		if (method_9553(block)) {
			return false;
		} else {
			boolean bl = Block.method_9501(blockState2.method_11628(viewableWorld, blockPos2), direction.getOpposite())
				|| block.matches(BlockTags.field_16584)
				|| block.matches(BlockTags.field_15504);
			return direction == Direction.UP ? block == Blocks.field_10312 || bl : !method_9581(block) && bl;
		}
	}

	protected static Direction method_16370(BlockState blockState) {
		return blockState.get(field_16545) ? Direction.DOWN : Direction.UP;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return method_16370(blockState).getOpposite() == direction && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}
}
