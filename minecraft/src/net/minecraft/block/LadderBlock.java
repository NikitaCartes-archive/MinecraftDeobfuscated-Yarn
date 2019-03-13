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
	public static final BooleanProperty field_11257 = Properties.field_12508;
	protected static final VoxelShape field_11255 = Block.method_9541(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	protected static final VoxelShape field_11252 = Block.method_9541(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_11254 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape field_11256 = Block.method_9541(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);

	protected LadderBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11253, Direction.NORTH).method_11657(field_11257, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		switch ((Direction)blockState.method_11654(field_11253)) {
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
		BlockState blockState = blockView.method_8320(blockPos);
		boolean bl = method_9581(blockState.getBlock());
		return !bl && Block.method_9501(blockState.method_11628(blockView, blockPos), direction) && !blockState.emitsRedstonePower();
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = blockState.method_11654(field_11253);
		return this.method_10305(viewableWorld, blockPos.method_10093(direction.getOpposite()), direction);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction.getOpposite() == blockState.method_11654(field_11253) && !blockState.method_11591(iWorld, blockPos)) {
			return Blocks.field_10124.method_9564();
		} else {
			if ((Boolean)blockState.method_11654(field_11257)) {
				iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
			}

			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		if (!itemPlacementContext.method_7717()) {
			BlockState blockState = itemPlacementContext.method_8045()
				.method_8320(itemPlacementContext.method_8037().method_10093(itemPlacementContext.method_8038().getOpposite()));
			if (blockState.getBlock() == this && blockState.method_11654(field_11253) == itemPlacementContext.method_8038()) {
				return null;
			}
		}

		BlockState blockState = this.method_9564();
		ViewableWorld viewableWorld = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.method_8037();
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.method_8037());

		for (Direction direction : itemPlacementContext.method_7718()) {
			if (direction.getAxis().isHorizontal()) {
				blockState = blockState.method_11657(field_11253, direction.getOpposite());
				if (blockState.method_11591(viewableWorld, blockPos)) {
					return blockState.method_11657(field_11257, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
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
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_11253, rotation.method_10503(blockState.method_11654(field_11253)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.method_10345(blockState.method_11654(field_11253)));
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11253, field_11257);
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_11257) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}
}
