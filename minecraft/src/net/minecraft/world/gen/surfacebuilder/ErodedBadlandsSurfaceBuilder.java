package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.BlockColumn;

public class ErodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
	private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
	private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
	private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

	public ErodedBadlandsSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
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
		double e = 0.0;
		double f = Math.min(Math.abs(d), this.heightCutoffNoise.sample((double)i * 0.25, (double)j * 0.25, false) * 15.0);
		if (f > 0.0) {
			double g = 0.001953125;
			double h = Math.abs(this.heightNoise.sample((double)i * 0.001953125, (double)j * 0.001953125, false));
			e = f * f * 2.5;
			double o = Math.ceil(h * 50.0) + 14.0;
			if (e > o) {
				e = o;
			}

			e += 64.0;
		}

		BlockState blockState3 = WHITE_TERRACOTTA;
		SurfaceConfig surfaceConfig = biome.getGenerationSettings().getSurfaceConfig();
		BlockState blockState4 = surfaceConfig.getUnderMaterial();
		BlockState blockState5 = surfaceConfig.getTopMaterial();
		BlockState blockState6 = blockState4;
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int q = -1;
		boolean bl2 = false;

		for (int r = Math.max(k, (int)e + 1); r >= m; r--) {
			BlockState blockState7 = blockColumn.getState(r);
			if (blockState7.isOf(blockState.getBlock())) {
				break;
			}

			if (blockState7.isOf(Blocks.WATER)) {
				return;
			}
		}

		for (int r = Math.max(k, (int)e + 1); r >= m; r--) {
			if (blockColumn.getState(r).isAir() && r < (int)e) {
				blockColumn.setState(r, blockState);
			}

			BlockState blockState7x = blockColumn.getState(r);
			if (blockState7x.isAir()) {
				q = -1;
			} else if (blockState7x.isOf(blockState.getBlock())) {
				if (q == -1) {
					bl2 = false;
					if (p <= 0) {
						blockState3 = Blocks.AIR.getDefaultState();
						blockState6 = blockState;
					} else if (r >= l - 4 && r <= l + 1) {
						blockState3 = WHITE_TERRACOTTA;
						blockState6 = blockState4;
					}

					if (r < l && (blockState3 == null || blockState3.isAir())) {
						blockState3 = blockState2;
					}

					q = p + Math.max(0, r - l);
					if (r >= l - 1) {
						if (r > l + 10 + p) {
							BlockState blockState8;
							if (r < 64 || r > 159) {
								blockState8 = ORANGE_TERRACOTTA;
							} else if (bl) {
								blockState8 = TERRACOTTA;
							} else {
								blockState8 = this.calculateLayerBlockState(i, r, j);
							}

							blockColumn.setState(r, blockState8);
						} else {
							blockColumn.setState(r, blockState5);
							bl2 = true;
						}
					} else {
						blockColumn.setState(r, blockState6);
						if (blockState6.isOf(Blocks.WHITE_TERRACOTTA)
							|| blockState6.isOf(Blocks.ORANGE_TERRACOTTA)
							|| blockState6.isOf(Blocks.MAGENTA_TERRACOTTA)
							|| blockState6.isOf(Blocks.LIGHT_BLUE_TERRACOTTA)
							|| blockState6.isOf(Blocks.YELLOW_TERRACOTTA)
							|| blockState6.isOf(Blocks.LIME_TERRACOTTA)
							|| blockState6.isOf(Blocks.PINK_TERRACOTTA)
							|| blockState6.isOf(Blocks.GRAY_TERRACOTTA)
							|| blockState6.isOf(Blocks.LIGHT_GRAY_TERRACOTTA)
							|| blockState6.isOf(Blocks.CYAN_TERRACOTTA)
							|| blockState6.isOf(Blocks.PURPLE_TERRACOTTA)
							|| blockState6.isOf(Blocks.BLUE_TERRACOTTA)
							|| blockState6.isOf(Blocks.BROWN_TERRACOTTA)
							|| blockState6.isOf(Blocks.GREEN_TERRACOTTA)
							|| blockState6.isOf(Blocks.RED_TERRACOTTA)
							|| blockState6.isOf(Blocks.BLACK_TERRACOTTA)) {
							blockColumn.setState(r, ORANGE_TERRACOTTA);
						}
					}
				} else if (q > 0) {
					q--;
					if (bl2) {
						blockColumn.setState(r, ORANGE_TERRACOTTA);
					} else {
						blockColumn.setState(r, this.calculateLayerBlockState(i, r, j));
					}
				}
			}
		}
	}
}
