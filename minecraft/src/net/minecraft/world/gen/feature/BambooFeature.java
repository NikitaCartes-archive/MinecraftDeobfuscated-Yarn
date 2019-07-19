package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class BambooFeature extends Feature<ProbabilityConfig> {
	private static final BlockState BAMBOO = Blocks.BAMBOO
		.getDefaultState()
		.with(BambooBlock.AGE, Integer.valueOf(1))
		.with(BambooBlock.LEAVES, BambooLeaves.NONE)
		.with(BambooBlock.STAGE, Integer.valueOf(0));
	private static final BlockState BAMBOO_TOP_1 = BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.LARGE).with(BambooBlock.STAGE, Integer.valueOf(1));
	private static final BlockState BAMBOO_TOP_2 = BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.LARGE);
	private static final BlockState BAMBOO_TOP_3 = BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.SMALL);

	public BambooFeature(Function<Dynamic<?>, ? extends ProbabilityConfig> configFactory) {
		super(configFactory);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, ProbabilityConfig probabilityConfig
	) {
		int i = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
		BlockPos.Mutable mutable2 = new BlockPos.Mutable(blockPos);
		if (iWorld.isAir(mutable)) {
			if (Blocks.BAMBOO.getDefaultState().canPlaceAt(iWorld, mutable)) {
				int j = random.nextInt(12) + 5;
				if (random.nextFloat() < probabilityConfig.probability) {
					int k = random.nextInt(4) + 1;

					for (int l = blockPos.getX() - k; l <= blockPos.getX() + k; l++) {
						for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k; m++) {
							int n = l - blockPos.getX();
							int o = m - blockPos.getZ();
							if (n * n + o * o <= k * k) {
								mutable2.set(l, iWorld.getTop(Heightmap.Type.WORLD_SURFACE, l, m) - 1, m);
								if (iWorld.getBlockState(mutable2).getBlock().matches(BlockTags.DIRT_LIKE)) {
									iWorld.setBlockState(mutable2, Blocks.PODZOL.getDefaultState(), 2);
								}
							}
						}
					}
				}

				for (int k = 0; k < j && iWorld.isAir(mutable); k++) {
					iWorld.setBlockState(mutable, BAMBOO, 2);
					mutable.setOffset(Direction.UP, 1);
				}

				if (mutable.getY() - blockPos.getY() >= 3) {
					iWorld.setBlockState(mutable, BAMBOO_TOP_1, 2);
					iWorld.setBlockState(mutable.setOffset(Direction.DOWN, 1), BAMBOO_TOP_2, 2);
					iWorld.setBlockState(mutable.setOffset(Direction.DOWN, 1), BAMBOO_TOP_3, 2);
				}
			}

			i++;
		}

		return i > 0;
	}
}
