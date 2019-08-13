package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.EntityContext;
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
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		TallPlantBlock tallPlantBlock = (TallPlantBlock)(this == Blocks.field_10112 ? Blocks.field_10313 : Blocks.field_10214);
		if (tallPlantBlock.getDefaultState().canPlaceAt(world, blockPos) && world.isAir(blockPos.up())) {
			tallPlantBlock.placeAt(world, blockPos, 2);
		}
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.field_10655;
	}
}
