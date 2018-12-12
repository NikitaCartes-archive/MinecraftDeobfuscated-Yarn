package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
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

			for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(blockPos.add(-i, 0, -j), blockPos.add(i, 1, j))) {
				int k = blockPos.getX() - blockPos2.getX();
				int l = blockPos.getZ() - blockPos2.getZ();
				if ((float)(k * k + l * l) <= random.nextFloat() * 10.0F - random.nextFloat() * 6.0F) {
					this.method_16708(iWorld, blockPos2);
				} else if ((double)random.nextFloat() < 0.031) {
					this.method_16708(iWorld, blockPos2);
				}
			}

			return true;
		}
	}

	private boolean method_16707(IWorld iWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState = iWorld.getBlockState(blockPos2);
		return Block.isFaceFullCube(blockState.getCollisionShape(iWorld, blockPos2), Direction.UP) || blockState.getBlock() == Blocks.field_10194;
	}

	private void method_16708(IWorld iWorld, BlockPos blockPos) {
		if (iWorld.isAir(blockPos) && this.method_16707(iWorld, blockPos)) {
			iWorld.setBlockState(blockPos, this.method_16843(iWorld), 4);
		}
	}

	protected abstract BlockState method_16843(IWorld iWorld);
}
