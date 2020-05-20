package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class WoodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
	private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
	private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
	private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

	public WoodedBadlandsSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Override
	public void generate(
		Random random,
		Chunk chunk,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		int l,
		long m,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		int n = i & 15;
		int o = j & 15;
		BlockState blockState3 = WHITE_TERRACOTTA;
		BlockState blockState4 = biome.getSurfaceConfig().getUnderMaterial();
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int q = -1;
		boolean bl2 = false;
		int r = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int s = k; s >= 0; s--) {
			if (r < 15) {
				mutable.set(n, s, o);
				BlockState blockState5 = chunk.getBlockState(mutable);
				if (blockState5.isAir()) {
					q = -1;
				} else if (blockState5.isOf(blockState.getBlock())) {
					if (q == -1) {
						bl2 = false;
						if (p <= 0) {
							blockState3 = Blocks.AIR.getDefaultState();
							blockState4 = blockState;
						} else if (s >= l - 4 && s <= l + 1) {
							blockState3 = WHITE_TERRACOTTA;
							blockState4 = biome.getSurfaceConfig().getUnderMaterial();
						}

						if (s < l && (blockState3 == null || blockState3.isAir())) {
							blockState3 = blockState2;
						}

						q = p + Math.max(0, s - l);
						if (s < l - 1) {
							chunk.setBlockState(mutable, blockState4, false);
							if (blockState4 == WHITE_TERRACOTTA) {
								chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
							}
						} else if (s > 86 + p * 2) {
							if (bl) {
								chunk.setBlockState(mutable, Blocks.COARSE_DIRT.getDefaultState(), false);
							} else {
								chunk.setBlockState(mutable, Blocks.GRASS_BLOCK.getDefaultState(), false);
							}
						} else if (s <= l + 3 + p) {
							chunk.setBlockState(mutable, biome.getSurfaceConfig().getTopMaterial(), false);
							bl2 = true;
						} else {
							BlockState blockState6;
							if (s < 64 || s > 127) {
								blockState6 = ORANGE_TERRACOTTA;
							} else if (bl) {
								blockState6 = TERRACOTTA;
							} else {
								blockState6 = this.calculateLayerBlockState(i, s, j);
							}

							chunk.setBlockState(mutable, blockState6, false);
						}
					} else if (q > 0) {
						q--;
						if (bl2) {
							chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
						} else {
							chunk.setBlockState(mutable, this.calculateLayerBlockState(i, s, j), false);
						}
					}

					r++;
				}
			}
		}
	}
}
