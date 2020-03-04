package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class AbstractPileFeature extends Feature<BlockPileFeatureConfig> {
	public AbstractPileFeature(Function<Dynamic<?>, ? extends BlockPileFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, BlockPileFeatureConfig blockPileFeatureConfig
	) {
		if (blockPos.getY() < 5) {
			return false;
		} else {
			int i = 2 + random.nextInt(2);
			int j = 2 + random.nextInt(2);

			for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-i, 0, -j), blockPos.add(i, 1, j))) {
				int k = blockPos.getX() - blockPos2.getX();
				int l = blockPos.getZ() - blockPos2.getZ();
				if ((float)(k * k + l * l) <= random.nextFloat() * 10.0F - random.nextFloat() * 6.0F) {
					this.addPileBlock(iWorld, blockPos2, random, blockPileFeatureConfig);
				} else if ((double)random.nextFloat() < 0.031) {
					this.addPileBlock(iWorld, blockPos2, random, blockPileFeatureConfig);
				}
			}

			return true;
		}
	}

	private boolean canPlacePileBlock(IWorld world, BlockPos pos, Random random) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.getBlock() == Blocks.GRASS_PATH ? random.nextBoolean() : blockState.isSideSolidFullSquare(world, blockPos, Direction.UP);
	}

	private void addPileBlock(IWorld world, BlockPos pos, Random random, BlockPileFeatureConfig config) {
		if (world.isAir(pos) && this.canPlacePileBlock(world, pos, random)) {
			world.setBlockState(pos, config.stateProvider.getBlockState(random, pos), 4);
		}
	}
}
