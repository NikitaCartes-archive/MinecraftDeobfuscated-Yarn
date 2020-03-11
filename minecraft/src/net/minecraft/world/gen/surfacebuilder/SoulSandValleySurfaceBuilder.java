package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;

public class SoulSandValleySurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	private static final BlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();
	private static final BlockState SOUL_SOIL = Blocks.SOUL_SOIL.getDefaultState();
	private long seed;
	private OctavePerlinNoiseSampler field_22212;
	private OctavePerlinNoiseSampler field_22205;
	private OctavePerlinNoiseSampler field_22206;
	private OctavePerlinNoiseSampler field_22207;
	private OctavePerlinNoiseSampler field_22208;

	public SoulSandValleySurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
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
		long m,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		int n = l + 1;
		int o = i & 15;
		int p = j & 15;
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int r = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		double e = 0.03125;
		boolean bl = this.field_22208.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) * 75.0 + random.nextDouble() > 0.0;
		double f = this.field_22205.sample((double)i, (double)l, (double)j);
		double g = this.field_22212.sample((double)i, (double)l, (double)j);
		double h = this.field_22207.sample((double)i, (double)l, (double)j);
		double s = this.field_22206.sample((double)i, (double)l, (double)j);
		BlockState blockState3 = h > s ? SOUL_SOIL : SOUL_SAND;
		BlockState blockState4 = f > g ? SOUL_SOIL : SOUL_SAND;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockState blockState5 = chunk.getBlockState(mutable.set(o, 128, p));

		for (int t = 127; t >= 0; t--) {
			mutable.set(o, t, p);
			BlockState blockState6 = chunk.getBlockState(mutable);
			if (blockState5.getBlock() == blockState.getBlock() && (blockState6.isAir() || blockState6 == blockState2)) {
				for (int u = 0; u < q; u++) {
					mutable.move(Direction.UP);
					if (chunk.getBlockState(mutable).getBlock() != blockState.getBlock()) {
						break;
					}

					chunk.setBlockState(mutable, blockState3, false);
				}

				mutable.set(o, t, p);
			}

			if ((blockState5.isAir() || blockState5 == blockState2) && blockState6.getBlock() == blockState.getBlock()) {
				for (int u = 0; u < r && chunk.getBlockState(mutable).getBlock() == blockState.getBlock(); u++) {
					if (bl && t >= n - 4 && t <= n + 1) {
						chunk.setBlockState(mutable, GRAVEL, false);
					} else {
						chunk.setBlockState(mutable, blockState4, false);
					}

					mutable.move(Direction.DOWN);
				}
			}

			blockState5 = blockState6;
		}
	}

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed
			|| this.field_22212 == null
			|| this.field_22205 == null
			|| this.field_22206 == null
			|| this.field_22207 == null
			|| this.field_22208 == null) {
			this.field_22212 = new OctavePerlinNoiseSampler(new ChunkRandom(seed), ImmutableList.of(-4));
			this.field_22205 = new OctavePerlinNoiseSampler(new ChunkRandom(seed + 1L), ImmutableList.of(-4));
			this.field_22206 = new OctavePerlinNoiseSampler(new ChunkRandom(seed + 2L), ImmutableList.of(-4));
			this.field_22207 = new OctavePerlinNoiseSampler(new ChunkRandom(seed + 3L), ImmutableList.of(-4));
			this.field_22208 = new OctavePerlinNoiseSampler(new ChunkRandom(seed + 4L), ImmutableList.of(0));
		}

		this.seed = seed;
	}
}
