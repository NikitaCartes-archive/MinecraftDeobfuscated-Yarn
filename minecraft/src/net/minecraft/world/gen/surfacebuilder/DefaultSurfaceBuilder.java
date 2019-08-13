package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class DefaultSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	public DefaultSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
	}

	public void method_15219(
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
		this.generate(
			random,
			chunk,
			biome,
			i,
			j,
			k,
			d,
			blockState,
			blockState2,
			ternarySurfaceConfig.getTopMaterial(),
			ternarySurfaceConfig.getUnderMaterial(),
			ternarySurfaceConfig.getUnderwaterMaterial(),
			l
		);
	}

	protected void generate(
		Random random,
		Chunk chunk,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		BlockState blockState3,
		BlockState blockState4,
		BlockState blockState5,
		int l
	) {
		BlockState blockState6 = blockState3;
		BlockState blockState7 = blockState4;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int m = -1;
		int n = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int o = i & 15;
		int p = j & 15;

		for (int q = k; q >= 0; q--) {
			mutable.set(o, q, p);
			BlockState blockState8 = chunk.getBlockState(mutable);
			if (blockState8.isAir()) {
				m = -1;
			} else if (blockState8.getBlock() == blockState.getBlock()) {
				if (m == -1) {
					if (n <= 0) {
						blockState6 = Blocks.field_10124.getDefaultState();
						blockState7 = blockState;
					} else if (q >= l - 4 && q <= l + 1) {
						blockState6 = blockState3;
						blockState7 = blockState4;
					}

					if (q < l && (blockState6 == null || blockState6.isAir())) {
						if (biome.getTemperature(mutable.set(i, q, j)) < 0.15F) {
							blockState6 = Blocks.field_10295.getDefaultState();
						} else {
							blockState6 = blockState2;
						}

						mutable.set(o, q, p);
					}

					m = n;
					if (q >= l - 1) {
						chunk.setBlockState(mutable, blockState6, false);
					} else if (q < l - 7 - n) {
						blockState6 = Blocks.field_10124.getDefaultState();
						blockState7 = blockState;
						chunk.setBlockState(mutable, blockState5, false);
					} else {
						chunk.setBlockState(mutable, blockState7, false);
					}
				} else if (m > 0) {
					m--;
					chunk.setBlockState(mutable, blockState7, false);
					if (m == 0 && blockState7.getBlock() == Blocks.field_10102 && n > 1) {
						m = random.nextInt(4) + Math.max(0, q - 63);
						blockState7 = blockState7.getBlock() == Blocks.field_10534 ? Blocks.field_10344.getDefaultState() : Blocks.field_9979.getDefaultState();
					}
				}
			}
		}
	}
}
