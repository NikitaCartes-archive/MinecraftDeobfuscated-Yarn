package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4538;
import net.minecraft.entity.EntityContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlantedFeatureConfig;

public class MushroomPlantBlock extends PlantBlock implements Fertilizable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

	public MushroomPlantBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return SHAPE;
	}

	@Override
	public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if (random.nextInt(25) == 0) {
			int i = 5;
			int j = 4;

			for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-4, -1, -4), blockPos.add(4, 1, 4))) {
				if (serverWorld.getBlockState(blockPos2).getBlock() == this) {
					if (--i <= 0) {
						return;
					}
				}
			}

			BlockPos blockPos3 = blockPos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

			for (int k = 0; k < 4; k++) {
				if (serverWorld.method_22347(blockPos3) && blockState.canPlaceAt(serverWorld, blockPos3)) {
					blockPos = blockPos3;
				}

				blockPos3 = blockPos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
			}

			if (serverWorld.method_22347(blockPos3) && blockState.canPlaceAt(serverWorld, blockPos3)) {
				serverWorld.setBlockState(blockPos3, blockState, 2);
			}
		}
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.isFullOpaque(blockView, blockPos);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = arg.getBlockState(blockPos2);
		Block block = blockState2.getBlock();
		return block != Blocks.MYCELIUM && block != Blocks.PODZOL ? arg.method_22335(blockPos, 0) < 13 && this.canPlantOnTop(blockState2, arg, blockPos2) : true;
	}

	public boolean trySpawningBigMushroom(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, Random random) {
		serverWorld.clearBlockState(blockPos, false);
		Feature<PlantedFeatureConfig> feature = null;
		if (this == Blocks.BROWN_MUSHROOM) {
			feature = Feature.HUGE_BROWN_MUSHROOM;
		} else if (this == Blocks.RED_MUSHROOM) {
			feature = Feature.HUGE_RED_MUSHROOM;
		}

		if (feature != null
			&& feature.generate(
				serverWorld,
				(ChunkGenerator<? extends ChunkGeneratorConfig>)serverWorld.method_14178().getChunkGenerator(),
				random,
				blockPos,
				new PlantedFeatureConfig(true)
			)) {
			return true;
		} else {
			serverWorld.setBlockState(blockPos, blockState, 3);
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
	public void grow(ServerWorld serverWorld, Random random, BlockPos blockPos, BlockState blockState) {
		this.trySpawningBigMushroom(serverWorld, blockPos, blockState, random);
	}

	@Override
	public boolean shouldPostProcess(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}
}
