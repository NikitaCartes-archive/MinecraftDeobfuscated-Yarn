package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class PaneBlock extends HorizontalConnectedBlock {
	protected PaneBlock(Block.Settings settings) {
		super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10905, Boolean.valueOf(false))
				.method_11657(field_10907, Boolean.valueOf(false))
				.method_11657(field_10904, Boolean.valueOf(false))
				.method_11657(field_10903, Boolean.valueOf(false))
				.method_11657(field_10900, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.getBlockPos());
		BlockPos blockPos2 = blockPos.north();
		BlockPos blockPos3 = blockPos.south();
		BlockPos blockPos4 = blockPos.west();
		BlockPos blockPos5 = blockPos.east();
		BlockState blockState = blockView.method_8320(blockPos2);
		BlockState blockState2 = blockView.method_8320(blockPos3);
		BlockState blockState3 = blockView.method_8320(blockPos4);
		BlockState blockState4 = blockView.method_8320(blockPos5);
		return this.method_9564()
			.method_11657(field_10905, Boolean.valueOf(this.method_10281(blockState, Block.method_20045(blockState, blockView, blockPos2, Direction.field_11035))))
			.method_11657(field_10904, Boolean.valueOf(this.method_10281(blockState2, Block.method_20045(blockState2, blockView, blockPos3, Direction.field_11043))))
			.method_11657(field_10903, Boolean.valueOf(this.method_10281(blockState3, Block.method_20045(blockState3, blockView, blockPos4, Direction.field_11034))))
			.method_11657(field_10907, Boolean.valueOf(this.method_10281(blockState4, Block.method_20045(blockState4, blockView, blockPos5, Direction.field_11039))))
			.method_11657(field_10900, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_10900)) {
			iWorld.method_8405().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return direction.getAxis().isHorizontal()
			? blockState.method_11657(
				(Property)FACING_PROPERTIES.get(direction),
				Boolean.valueOf(this.method_10281(blockState2, Block.method_20045(blockState2, iWorld, blockPos2, direction.getOpposite())))
			)
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9522(BlockState blockState, BlockState blockState2, Direction direction) {
		if (blockState2.getBlock() == this) {
			if (!direction.getAxis().isHorizontal()) {
				return true;
			}

			if ((Boolean)blockState.method_11654((Property)FACING_PROPERTIES.get(direction))
				&& (Boolean)blockState2.method_11654((Property)FACING_PROPERTIES.get(direction.getOpposite()))) {
				return true;
			}
		}

		return super.method_9522(blockState, blockState2, direction);
	}

	public final boolean method_10281(BlockState blockState, boolean bl) {
		Block block = blockState.getBlock();
		return !canConnect(block) && bl || block instanceof PaneBlock;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10905, field_10907, field_10903, field_10904, field_10900);
	}
}
