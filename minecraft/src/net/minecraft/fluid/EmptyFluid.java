package net.minecraft.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class EmptyFluid extends Fluid {
	@Override
	public Item getBucketItem() {
		return Items.AIR;
	}

	@Override
	public boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		return true;
	}

	@Override
	public Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state) {
		return Vec3d.ZERO;
	}

	@Override
	public int getTickRate(WorldView worldView) {
		return 0;
	}

	@Override
	protected boolean isEmpty() {
		return true;
	}

	@Override
	protected float getBlastResistance() {
		return 0.0F;
	}

	@Override
	public float getHeight(FluidState fluidState, BlockView blockView, BlockPos blockPos) {
		return 0.0F;
	}

	@Override
	public float getHeight(FluidState fluidState) {
		return 0.0F;
	}

	@Override
	protected BlockState toBlockState(FluidState fluidState) {
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public boolean isStill(FluidState fluidState) {
		return false;
	}

	@Override
	public int getLevel(FluidState fluidState) {
		return 0;
	}

	@Override
	public VoxelShape getShape(FluidState fluidState, BlockView blockView, BlockPos blockPos) {
		return VoxelShapes.empty();
	}
}
