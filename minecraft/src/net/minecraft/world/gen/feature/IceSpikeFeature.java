package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class IceSpikeFeature extends Feature<DefaultFeatureConfig> {
	public IceSpikeFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean method_13408(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		while (structureWorldAccess.isAir(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.method_10074();
		}

		if (!structureWorldAccess.getBlockState(blockPos).isOf(Blocks.field_10491)) {
			return false;
		} else {
			blockPos = blockPos.up(random.nextInt(4));
			int i = random.nextInt(4) + 7;
			int j = i / 4 + random.nextInt(2);
			if (j > 1 && random.nextInt(60) == 0) {
				blockPos = blockPos.up(10 + random.nextInt(30));
			}

			for (int k = 0; k < i; k++) {
				float f = (1.0F - (float)k / (float)i) * (float)j;
				int l = MathHelper.ceil(f);

				for (int m = -l; m <= l; m++) {
					float g = (float)MathHelper.abs(m) - 0.25F;

					for (int n = -l; n <= l; n++) {
						float h = (float)MathHelper.abs(n) - 0.25F;
						if ((m == 0 && n == 0 || !(g * g + h * h > f * f)) && (m != -l && m != l && n != -l && n != l || !(random.nextFloat() > 0.75F))) {
							BlockState blockState = structureWorldAccess.getBlockState(blockPos.add(m, k, n));
							Block block = blockState.getBlock();
							if (blockState.isAir() || isSoil(block) || block == Blocks.field_10491 || block == Blocks.field_10295) {
								this.setBlockState(structureWorldAccess, blockPos.add(m, k, n), Blocks.field_10225.getDefaultState());
							}

							if (k != 0 && l > 1) {
								blockState = structureWorldAccess.getBlockState(blockPos.add(m, -k, n));
								block = blockState.getBlock();
								if (blockState.isAir() || isSoil(block) || block == Blocks.field_10491 || block == Blocks.field_10295) {
									this.setBlockState(structureWorldAccess, blockPos.add(m, -k, n), Blocks.field_10225.getDefaultState());
								}
							}
						}
					}
				}
			}

			int k = j - 1;
			if (k < 0) {
				k = 0;
			} else if (k > 1) {
				k = 1;
			}

			for (int o = -k; o <= k; o++) {
				for (int l = -k; l <= k; l++) {
					BlockPos blockPos2 = blockPos.add(o, -1, l);
					int p = 50;
					if (Math.abs(o) == 1 && Math.abs(l) == 1) {
						p = random.nextInt(5);
					}

					while (blockPos2.getY() > 50) {
						BlockState blockState2 = structureWorldAccess.getBlockState(blockPos2);
						Block block2 = blockState2.getBlock();
						if (!blockState2.isAir() && !isSoil(block2) && block2 != Blocks.field_10491 && block2 != Blocks.field_10295 && block2 != Blocks.field_10225) {
							break;
						}

						this.setBlockState(structureWorldAccess, blockPos2, Blocks.field_10225.getDefaultState());
						blockPos2 = blockPos2.method_10074();
						if (--p <= 0) {
							blockPos2 = blockPos2.method_10087(random.nextInt(5) + 1);
							p = random.nextInt(5);
						}
					}
				}
			}

			return true;
		}
	}
}
