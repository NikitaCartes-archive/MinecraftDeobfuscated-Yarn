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

public abstract class AbstractPileFeature extends Feature<DefaultFeatureConfig> {
	public AbstractPileFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_16709(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
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
					this.addPileBlock(iWorld, blockPos2, random);
				} else if ((double)random.nextFloat() < 0.031) {
					this.addPileBlock(iWorld, blockPos2, random);
				}
			}

			return true;
		}
	}

	private boolean canPlacePileBlock(IWorld iWorld, BlockPos blockPos, Random random) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState = iWorld.getBlockState(blockPos2);
		return blockState.getBlock() == Blocks.field_10194 ? random.nextBoolean() : blockState.isSideSolidFullSquare(iWorld, blockPos2, Direction.field_11036);
	}

	private void addPileBlock(IWorld iWorld, BlockPos blockPos, Random random) {
		if (iWorld.isAir(blockPos) && this.canPlacePileBlock(iWorld, blockPos, random)) {
			iWorld.setBlockState(blockPos, this.getPileBlockState(iWorld), 4);
		}
	}

	protected abstract BlockState getPileBlockState(IWorld iWorld);
}
