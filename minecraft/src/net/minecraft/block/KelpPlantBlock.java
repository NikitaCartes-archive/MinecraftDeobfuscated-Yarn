package net.minecraft.block;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class KelpPlantBlock extends AbstractPlantBlock implements FluidFillable {
	protected KelpPlantBlock(AbstractBlock.Settings settings) {
		super(settings, Direction.field_11036, VoxelShapes.fullCube(), true);
	}

	@Override
	protected AbstractPlantStemBlock getStem() {
		return (AbstractPlantStemBlock)Blocks.field_9993;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Fluids.WATER.getStill(false);
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		return false;
	}
}
