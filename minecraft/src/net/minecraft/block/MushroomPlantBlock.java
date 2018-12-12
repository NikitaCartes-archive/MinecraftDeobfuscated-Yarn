package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class MushroomPlantBlock extends PlantBlock implements Fertilizable {
	protected static final VoxelShape field_11304 = Block.createCubeShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

	public MushroomPlantBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_11304;
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (random.nextInt(25) == 0) {
			int i = 5;
			int j = 4;

			for (BlockPos blockPos2 : BlockPos.iterateBoxPositionsMutable(blockPos.add(-4, -1, -4), blockPos.add(4, 1, 4))) {
				if (world.getBlockState(blockPos2).getBlock() == this) {
					if (--i <= 0) {
						return;
					}
				}
			}

			BlockPos blockPos3 = blockPos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

			for (int k = 0; k < 4; k++) {
				if (world.isAir(blockPos3) && blockState.canPlaceAt(world, blockPos3)) {
					blockPos = blockPos3;
				}

				blockPos3 = blockPos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
			}

			if (world.isAir(blockPos3) && blockState.canPlaceAt(world, blockPos3)) {
				world.setBlockState(blockPos3, blockState, 2);
			}
		}
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.isFullOpaque(blockView, blockPos);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		Block block = blockState2.getBlock();
		return block != Blocks.field_10402 && block != Blocks.field_10520
			? viewableWorld.getLightLevel(blockPos, 0) < 13 && this.canPlantOnTop(blockState2, viewableWorld, blockPos2)
			: true;
	}

	public boolean method_10349(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		iWorld.clearBlockState(blockPos);
		Feature<DefaultFeatureConfig> feature = null;
		if (this == Blocks.field_10251) {
			feature = Feature.field_13531;
		} else if (this == Blocks.field_10559) {
			feature = Feature.field_13571;
		}

		if (feature != null
			&& feature.method_13151(
				iWorld, (ChunkGenerator<? extends ChunkGeneratorConfig>)iWorld.getChunkManager().getChunkGenerator(), random, blockPos, FeatureConfig.field_13603
			)) {
			return true;
		} else {
			iWorld.setBlockState(blockPos, blockState, 3);
			return false;
		}
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return (double)random.nextFloat() < 0.4;
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		this.method_10349(world, blockPos, blockState, random);
	}

	@Override
	public boolean shouldPostProcess(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}
}
