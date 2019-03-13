package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class GrassPathBlock extends Block {
	protected static final VoxelShape field_11106 = FarmlandBlock.field_11010;

	protected GrassPathBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return !this.method_9564().method_11591(itemPlacementContext.method_8045(), itemPlacementContext.method_8037())
			? Block.method_9582(this.method_9564(), Blocks.field_10566.method_9564(), itemPlacementContext.method_8045(), itemPlacementContext.method_8037())
			: super.method_9605(itemPlacementContext);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction == Direction.UP && !blockState.method_11591(iWorld, blockPos)) {
			iWorld.method_8397().method_8676(blockPos, this, 1);
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		FarmlandBlock.method_10125(blockState, world, blockPos);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState2 = viewableWorld.method_8320(blockPos.up());
		return !blockState2.method_11620().method_15799() || blockState2.getBlock() instanceof FenceGateBlock;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11106;
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
