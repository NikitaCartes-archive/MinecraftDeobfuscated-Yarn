package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.BlockColumn;

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
		BlockColumn blockColumn,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		int l,
		int m,
		long n,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		BlockState blockState3 = WHITE_TERRACOTTA;
		SurfaceConfig surfaceConfig = biome.getGenerationSettings().getSurfaceConfig();
		BlockState blockState4 = surfaceConfig.getUnderMaterial();
		BlockState blockState5 = surfaceConfig.getTopMaterial();
		BlockState blockState6 = blockState4;
		int o = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int p = -1;
		boolean bl2 = false;
		int q = 0;

		for (int r = k; r >= m; r--) {
			if (q < 15) {
				BlockState blockState7 = blockColumn.getState(r);
				if (blockState7.isAir()) {
					p = -1;
				} else if (blockState7.isOf(blockState.getBlock())) {
					if (p == -1) {
						bl2 = false;
						if (o <= 0) {
							blockState3 = Blocks.AIR.getDefaultState();
							blockState6 = blockState;
						} else if (r >= l - 4 && r <= l + 1) {
							blockState3 = WHITE_TERRACOTTA;
							blockState6 = blockState4;
						}

						if (r < l && (blockState3 == null || blockState3.isAir())) {
							blockState3 = blockState2;
						}

						p = o + Math.max(0, r - l);
						if (r < l - 1) {
							blockColumn.setState(r, blockState6);
							if (blockState6 == WHITE_TERRACOTTA) {
								blockColumn.setState(r, ORANGE_TERRACOTTA);
							}
						} else if (r > 96 + o * 2) {
							if (bl) {
								blockColumn.setState(r, Blocks.COARSE_DIRT.getDefaultState());
							} else {
								blockColumn.setState(r, Blocks.GRASS_BLOCK.getDefaultState());
							}
						} else if (r <= l + 10 + o) {
							blockColumn.setState(r, blockState5);
							bl2 = true;
						} else {
							BlockState blockState8;
							if (r < 64 || r > 159) {
								blockState8 = ORANGE_TERRACOTTA;
							} else if (bl) {
								blockState8 = TERRACOTTA;
							} else {
								blockState8 = this.calculateLayerBlockState(i, r, j);
							}

							blockColumn.setState(r, blockState8);
						}
					} else if (p > 0) {
						p--;
						if (bl2) {
							blockColumn.setState(r, ORANGE_TERRACOTTA);
						} else {
							blockColumn.setState(r, this.calculateLayerBlockState(i, r, j));
						}
					}

					q++;
				}
			}
		}
	}
}
