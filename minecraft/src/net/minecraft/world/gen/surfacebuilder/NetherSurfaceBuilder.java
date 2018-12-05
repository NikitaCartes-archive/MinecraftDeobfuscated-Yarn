package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.class_2919;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.config.surfacebuilder.TernarySurfaceConfig;

public class NetherSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	private static final BlockState field_15660 = Blocks.field_10543.getDefaultState();
	private static final BlockState field_15658 = Blocks.field_10515.getDefaultState();
	private static final BlockState field_15659 = Blocks.field_10255.getDefaultState();
	private static final BlockState field_15662 = Blocks.field_10114.getDefaultState();
	protected long field_15661;
	protected OctavePerlinNoiseSampler field_15663;

	public NetherSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
	}

	public void method_15300(
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
		int n = l + 1;
		int o = i & 15;
		int p = j & 15;
		double e = 0.03125;
		boolean bl = this.field_15663.sample((double)i * 0.03125, (double)j * 0.03125, 0.0) + random.nextDouble() * 0.2 > 0.0;
		boolean bl2 = this.field_15663.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) + random.nextDouble() * 0.2 > 0.0;
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int r = -1;
		BlockState blockState3 = field_15658;
		BlockState blockState4 = field_15658;

		for (int s = 127; s >= 0; s--) {
			mutable.set(o, s, p);
			BlockState blockState5 = chunk.getBlockState(mutable);
			if (blockState5.getBlock() != null && !blockState5.isAir()) {
				if (blockState5.getBlock() == blockState.getBlock()) {
					if (r == -1) {
						if (q <= 0) {
							blockState3 = field_15660;
							blockState4 = field_15658;
						} else if (s >= n - 4 && s <= n + 1) {
							blockState3 = field_15658;
							blockState4 = field_15658;
							if (bl2) {
								blockState3 = field_15659;
								blockState4 = field_15658;
							}

							if (bl) {
								blockState3 = field_15662;
								blockState4 = field_15662;
							}
						}

						if (s < n && (blockState3 == null || blockState3.isAir())) {
							blockState3 = blockState2;
						}

						r = q;
						if (s >= n - 1) {
							chunk.setBlockState(mutable, blockState3, false);
						} else {
							chunk.setBlockState(mutable, blockState4, false);
						}
					} else if (r > 0) {
						r--;
						chunk.setBlockState(mutable, blockState4, false);
					}
				}
			} else {
				r = -1;
			}
		}
	}

	@Override
	public void method_15306(long l) {
		if (this.field_15661 != l || this.field_15663 == null) {
			this.field_15663 = new OctavePerlinNoiseSampler(new class_2919(l), 4);
		}

		this.field_15661 = l;
	}
}
