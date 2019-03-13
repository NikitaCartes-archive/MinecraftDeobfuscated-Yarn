package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WaterlilyBlock extends PlantBlock {
	protected static final VoxelShape field_11728 = Block.method_9541(1.0, 0.0, 1.0, 15.0, 1.5, 15.0);

	protected WaterlilyBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		super.method_9548(blockState, world, blockPos, entity);
		if (entity instanceof BoatEntity) {
			world.method_8651(new BlockPos(blockPos), true);
		}
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11728;
	}

	@Override
	protected boolean method_9695(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		FluidState fluidState = blockView.method_8316(blockPos);
		return fluidState.getFluid() == Fluids.WATER || blockState.method_11620() == Material.ICE;
	}
}
