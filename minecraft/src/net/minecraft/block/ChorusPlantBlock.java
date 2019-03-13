package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class ChorusPlantBlock extends ConnectedPlantBlock {
	protected ChorusPlantBlock(Block.Settings settings) {
		super(0.3125F, settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11332, Boolean.valueOf(false))
				.method_11657(field_11335, Boolean.valueOf(false))
				.method_11657(field_11331, Boolean.valueOf(false))
				.method_11657(field_11328, Boolean.valueOf(false))
				.method_11657(field_11327, Boolean.valueOf(false))
				.method_11657(field_11330, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9759(itemPlacementContext.method_8045(), itemPlacementContext.method_8037());
	}

	public BlockState method_9759(BlockView blockView, BlockPos blockPos) {
		Block block = blockView.method_8320(blockPos.down()).getBlock();
		Block block2 = blockView.method_8320(blockPos.up()).getBlock();
		Block block3 = blockView.method_8320(blockPos.north()).getBlock();
		Block block4 = blockView.method_8320(blockPos.east()).getBlock();
		Block block5 = blockView.method_8320(blockPos.south()).getBlock();
		Block block6 = blockView.method_8320(blockPos.west()).getBlock();
		return this.method_9564()
			.method_11657(field_11330, Boolean.valueOf(block == this || block == Blocks.field_10528 || block == Blocks.field_10471))
			.method_11657(field_11327, Boolean.valueOf(block2 == this || block2 == Blocks.field_10528))
			.method_11657(field_11332, Boolean.valueOf(block3 == this || block3 == Blocks.field_10528))
			.method_11657(field_11335, Boolean.valueOf(block4 == this || block4 == Blocks.field_10528))
			.method_11657(field_11331, Boolean.valueOf(block5 == this || block5 == Blocks.field_10528))
			.method_11657(field_11328, Boolean.valueOf(block6 == this || block6 == Blocks.field_10528));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (!blockState.method_11591(iWorld, blockPos)) {
			iWorld.method_8397().method_8676(blockPos, this, 1);
			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			Block block = blockState2.getBlock();
			boolean bl = block == this || block == Blocks.field_10528 || direction == Direction.DOWN && block == Blocks.field_10471;
			return blockState.method_11657((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(bl));
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.method_11591(world, blockPos)) {
			world.method_8651(blockPos, true);
		}
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState2 = viewableWorld.method_8320(blockPos.down());
		boolean bl = !viewableWorld.method_8320(blockPos.up()).isAir() && !blockState2.isAir();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.method_10093(direction);
			Block block = viewableWorld.method_8320(blockPos2).getBlock();
			if (block == this) {
				if (bl) {
					return false;
				}

				Block block2 = viewableWorld.method_8320(blockPos2.down()).getBlock();
				if (block2 == this || block2 == Blocks.field_10471) {
					return true;
				}
			}
		}

		Block block3 = blockState2.getBlock();
		return block3 == this || block3 == Blocks.field_10471;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11332, field_11335, field_11331, field_11328, field_11327, field_11330);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
