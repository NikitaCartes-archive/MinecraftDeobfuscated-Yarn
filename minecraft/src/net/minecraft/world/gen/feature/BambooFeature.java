package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BambooFeature extends Feature<ProbabilityConfig> {
	private static final BlockState BAMBOO = Blocks.BAMBOO
		.getDefaultState()
		.with(BambooBlock.AGE, Integer.valueOf(1))
		.with(BambooBlock.LEAVES, BambooLeaves.NONE)
		.with(BambooBlock.STAGE, Integer.valueOf(0));
	private static final BlockState BAMBOO_TOP_1 = BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.LARGE).with(BambooBlock.STAGE, Integer.valueOf(1));
	private static final BlockState BAMBOO_TOP_2 = BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.LARGE);
	private static final BlockState BAMBOO_TOP_3 = BAMBOO.with(BambooBlock.LEAVES, BambooLeaves.SMALL);

	public BambooFeature(Codec<ProbabilityConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, ProbabilityConfig probabilityConfig
	) {
		int i = 0;
		BlockPos.Mutable mutable = blockPos.mutableCopy();
		BlockPos.Mutable mutable2 = blockPos.mutableCopy();
		if (structureWorldAccess.isAir(mutable)) {
			if (Blocks.BAMBOO.getDefaultState().canPlaceAt(structureWorldAccess, mutable)) {
				int j = random.nextInt(12) + 5;
				if (random.nextFloat() < probabilityConfig.probability) {
					int k = random.nextInt(4) + 1;

					for (int l = blockPos.getX() - k; l <= blockPos.getX() + k; l++) {
						for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k; m++) {
							int n = l - blockPos.getX();
							int o = m - blockPos.getZ();
							if (n * n + o * o <= k * k) {
								mutable2.set(l, structureWorldAccess.getTopY(Heightmap.Type.WORLD_SURFACE, l, m) - 1, m);
								if (isSoil(structureWorldAccess.getBlockState(mutable2).getBlock())) {
									structureWorldAccess.setBlockState(mutable2, Blocks.PODZOL.getDefaultState(), 2);
								}
							}
						}
					}
				}

				for (int k = 0; k < j && structureWorldAccess.isAir(mutable); k++) {
					structureWorldAccess.setBlockState(mutable, BAMBOO, 2);
					mutable.move(Direction.UP, 1);
				}

				if (mutable.getY() - blockPos.getY() >= 3) {
					structureWorldAccess.setBlockState(mutable, BAMBOO_TOP_1, 2);
					structureWorldAccess.setBlockState(mutable.move(Direction.DOWN, 1), BAMBOO_TOP_2, 2);
					structureWorldAccess.setBlockState(mutable.move(Direction.DOWN, 1), BAMBOO_TOP_3, 2);
				}
			}

			i++;
		}

		return i > 0;
	}
}
