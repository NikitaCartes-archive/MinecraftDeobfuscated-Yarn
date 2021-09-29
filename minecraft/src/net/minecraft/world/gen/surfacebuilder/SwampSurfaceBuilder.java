package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.BlockColumn;

public class SwampSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public SwampSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

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
		double e = Biome.FOLIAGE_NOISE.sample((double)i * 0.25, (double)j * 0.25, false);
		if (e > 0.0) {
			for (int o = k; o >= m; o--) {
				if (!blockColumn.getState(o).isAir()) {
					if (o == 62 && !blockColumn.getState(o).isOf(blockState2.getBlock())) {
						blockColumn.setState(o, blockState2);
					}
					break;
				}
			}
		}

		SurfaceBuilder.DEFAULT.generate(random, blockColumn, biome, i, j, k, d, blockState, blockState2, l, m, n, ternarySurfaceConfig);
	}
}
