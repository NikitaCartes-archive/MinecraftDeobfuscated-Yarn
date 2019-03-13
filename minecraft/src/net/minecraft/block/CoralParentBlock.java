package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class CoralParentBlock extends Block implements Waterloggable {
	public static final BooleanProperty field_9940 = Properties.field_12508;
	private static final VoxelShape field_9939 = Block.method_9541(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

	protected CoralParentBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_9940, Boolean.valueOf(true)));
	}

	protected void method_9430(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		if (!method_9431(blockState, iWorld, blockPos)) {
			iWorld.method_8397().method_8676(blockPos, this, 60 + iWorld.getRandom().nextInt(40));
		}
	}

	protected static boolean method_9431(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if ((Boolean)blockState.method_11654(field_9940)) {
			return true;
		} else {
			for (Direction direction : Direction.values()) {
				if (blockView.method_8316(blockPos.method_10093(direction)).method_15767(FluidTags.field_15517)) {
					return true;
				}
			}

			return false;
		}
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.method_8037());
		return this.method_9564().method_11657(field_9940, Boolean.valueOf(fluidState.method_15767(FluidTags.field_15517) && fluidState.getLevel() == 8));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_9939;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_9940)) {
			iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return direction == Direction.DOWN && !this.method_9558(blockState, iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return viewableWorld.method_8320(blockPos2).method_11631(viewableWorld, blockPos2);
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_9940);
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_9940) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}
}
