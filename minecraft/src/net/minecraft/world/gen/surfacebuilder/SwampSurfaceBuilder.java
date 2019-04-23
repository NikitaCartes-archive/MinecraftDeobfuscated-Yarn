package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class SwampSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public SwampSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
	}

	public void method_15333(
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
		double e = Biome.FOLIAGE_NOISE.sample((double)i * 0.25, (double)j * 0.25);
		if (e > 0.0) {
			int n = i & 15;
			int o = j & 15;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int p = k; p >= 0; p--) {
				mutable.set(n, p, o);
				if (!chunk.getBlockState(mutable).isAir()) {
					if (p == 62 && chunk.getBlockState(mutable).getBlock() != blockState2.getBlock()) {
						chunk.setBlockState(mutable, blockState2, false);
					}
					break;
				}
			}
		}

		SurfaceBuilder.field_15701.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, ternarySurfaceConfig);
	}
}
