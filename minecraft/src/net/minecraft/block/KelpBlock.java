package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class KelpBlock extends AbstractPlantStemBlock implements FluidFillable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);

	protected KelpBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.field_11036, SHAPE, true, 0.14);
	}

	@Override
	protected boolean chooseStemState(BlockState state) {
		return state.isOf(Blocks.field_10382);
	}

	@Override
	protected Block getPlant() {
		return Blocks.field_10463;
	}

	@Override
	protected boolean canAttachTo(Block block) {
		return block != Blocks.field_10092;
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		return false;
	}

	@Override
	protected int method_26376(Random random) {
		return 1;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return fluidState.isIn(FluidTags.field_15517) && fluidState.getLevel() == 8 ? super.getPlacementState(ctx) : null;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Fluids.WATER.getStill(false);
	}
}
