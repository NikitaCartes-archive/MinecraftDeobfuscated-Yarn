package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SeagrassBlock extends PlantBlock implements Fertilizable, FluidFillable {
	protected static final VoxelShape field_11485 = Block.method_9541(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

	protected SeagrassBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11485;
	}

	@Override
	protected boolean method_9695(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return Block.method_9501(blockState.method_11628(blockView, blockPos), Direction.UP) && blockState.getBlock() != Blocks.field_10092;
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.method_8037());
		return fluidState.method_15767(FluidTags.field_15517) && fluidState.getLevel() == 8 ? super.method_9605(itemPlacementContext) : null;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		BlockState blockState3 = super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		if (!blockState3.isAir()) {
			iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return blockState3;
	}

	@Override
	public boolean method_9651(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return true;
	}

	@Override
	public boolean method_9650(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return Fluids.WATER.method_15729(false);
	}

	@Override
	public void method_9652(World world, Random random, BlockPos blockPos, BlockState blockState) {
		BlockState blockState2 = Blocks.field_10238.method_9564();
		BlockState blockState3 = blockState2.method_11657(TallSeagrassBlock.field_11616, DoubleBlockHalf.field_12609);
		BlockPos blockPos2 = blockPos.up();
		if (world.method_8320(blockPos2).getBlock() == Blocks.field_10382) {
			world.method_8652(blockPos, blockState2, 2);
			world.method_8652(blockPos2, blockState3, 2);
		}
	}

	@Override
	public boolean method_10310(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return false;
	}

	@Override
	public boolean method_10311(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return false;
	}
}
