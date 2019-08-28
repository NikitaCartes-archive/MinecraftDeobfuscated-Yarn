package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.EntityContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FernBlock extends PlantBlock implements Fertilizable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

	protected FernBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return SHAPE;
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void grow(ServerWorld serverWorld, Random random, BlockPos blockPos, BlockState blockState) {
		TallPlantBlock tallPlantBlock = (TallPlantBlock)(this == Blocks.FERN ? Blocks.LARGE_FERN : Blocks.TALL_GRASS);
		if (tallPlantBlock.getDefaultState().canPlaceAt(serverWorld, blockPos) && serverWorld.method_22347(blockPos.up())) {
			tallPlantBlock.placeAt(serverWorld, blockPos, 2);
		}
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XYZ;
	}
}
