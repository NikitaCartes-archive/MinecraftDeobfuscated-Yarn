package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class SwampSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public SwampSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

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
		int m,
		long n,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		double e = Biome.FOLIAGE_NOISE.sample((double)i * 0.25, (double)j * 0.25, false);
		if (e > 0.0) {
			int o = i & 15;
			int p = j & 15;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int q = k; q >= m; q--) {
				mutable.set(o, q, p);
				if (!chunk.getBlockState(mutable).isAir()) {
					if (q == 62 && !chunk.getBlockState(mutable).isOf(blockState2.getBlock()) && !this.method_37852(chunk, o, q, p, mutable)) {
						chunk.setBlockState(mutable.set(o, q, p), blockState2, false);
					}
					break;
				}
			}
		}

		SurfaceBuilder.DEFAULT.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, n, ternarySurfaceConfig);
	}

	private boolean method_37852(Chunk chunk, int i, int j, int k, BlockPos.Mutable mutable) {
		for (Direction direction : Direction.values()) {
			if (direction != Direction.UP) {
				mutable.set(i, j, k).move(direction);
				if (chunk.getBlockState(mutable).isAir()) {
					return true;
				}
			}
		}

		return false;
	}
}
