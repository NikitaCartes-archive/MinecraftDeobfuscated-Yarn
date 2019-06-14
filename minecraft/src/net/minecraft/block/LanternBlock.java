package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class LanternBlock extends Block {
	public static final BooleanProperty field_16545 = Properties.field_16561;
	protected static final VoxelShape field_16546 = VoxelShapes.method_1084(
		Block.method_9541(5.0, 0.0, 5.0, 11.0, 7.0, 11.0), Block.method_9541(6.0, 7.0, 6.0, 10.0, 9.0, 10.0)
	);
	protected static final VoxelShape field_16544 = VoxelShapes.method_1084(
		Block.method_9541(5.0, 1.0, 5.0, 11.0, 8.0, 11.0), Block.method_9541(6.0, 8.0, 6.0, 10.0, 10.0, 10.0)
	);

	public LanternBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_16545, Boolean.valueOf(false)));
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		for (Direction direction : itemPlacementContext.getPlacementDirections()) {
			if (direction.getAxis() == Direction.Axis.Y) {
				BlockState blockState = this.method_9564().method_11657(field_16545, Boolean.valueOf(direction == Direction.field_11036));
				if (blockState.canPlaceAt(itemPlacementContext.method_8045(), itemPlacementContext.getBlockPos())) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return blockState.method_11654(field_16545) ? field_16544 : field_16546;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_16545);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = method_16370(blockState).getOpposite();
		return Block.isSolidSmallSquare(viewableWorld, blockPos.offset(direction), direction.getOpposite());
	}

	protected static Direction method_16370(BlockState blockState) {
		return blockState.method_11654(field_16545) ? Direction.field_11033 : Direction.field_11036;
	}

	@Override
	public PistonBehavior method_9527(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return method_16370(blockState).getOpposite() == direction && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
