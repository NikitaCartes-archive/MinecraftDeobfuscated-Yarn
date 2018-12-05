package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WaterlilyBlock extends PlantBlock {
	protected static final VoxelShape field_11728 = Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 1.5, 15.0);

	protected WaterlilyBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		super.onEntityCollision(blockState, world, blockPos, entity);
		if (entity instanceof BoatEntity) {
			world.breakBlock(new BlockPos(blockPos), true);
		}
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_11728;
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		FluidState fluidState = blockView.getFluidState(blockPos);
		return fluidState.getFluid() == Fluids.WATER || blockState.getMaterial() == Material.ICE;
	}
}
