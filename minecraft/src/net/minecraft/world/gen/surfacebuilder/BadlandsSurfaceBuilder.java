package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;

public class BadlandsSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	private static final BlockState WHITE_TERRACOTTA = Blocks.field_10611.getDefaultState();
	private static final BlockState ORANGE_TERRACOTTA = Blocks.field_10184.getDefaultState();
	private static final BlockState TERRACOTTA = Blocks.field_10415.getDefaultState();
	private static final BlockState YELLOW_TERRACOTTA = Blocks.field_10143.getDefaultState();
	private static final BlockState BROWN_TERRACOTTA = Blocks.field_10123.getDefaultState();
	private static final BlockState RED_TERRACOTTA = Blocks.field_10328.getDefaultState();
	private static final BlockState LIGHT_GRAY_TERRACOTTA = Blocks.field_10590.getDefaultState();
	protected BlockState[] layerBlocks;
	protected long seed;
	protected OctaveSimplexNoiseSampler heightCutoffNoise;
	protected OctaveSimplexNoiseSampler heightNoise;
	protected OctaveSimplexNoiseSampler layerNoise;

	public BadlandsSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	public void method_15208(
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
		SurfaceConfig surfaceConfig = biome.getGenerationSettings().getSurfaceConfig();
		BlockState blockState4 = surfaceConfig.getUnderMaterial();
		BlockState blockState5 = surfaceConfig.getTopMaterial();
		BlockState blockState6 = blockState4;
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int q = -1;
		boolean bl2 = false;
		int r = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int s = k; s >= 0; s--) {
			if (r < 15) {
				mutable.set(n, s, o);
				BlockState blockState7 = chunk.getBlockState(mutable);
				if (blockState7.isAir()) {
					q = -1;
				} else if (blockState7.isOf(blockState.getBlock())) {
					if (q == -1) {
						bl2 = false;
						if (p <= 0) {
							blockState3 = Blocks.field_10124.getDefaultState();
							blockState6 = blockState;
						} else if (s >= l - 4 && s <= l + 1) {
							blockState3 = WHITE_TERRACOTTA;
							blockState6 = blockState4;
						}

						if (s < l && (blockState3 == null || blockState3.isAir())) {
							blockState3 = blockState2;
						}

						q = p + Math.max(0, s - l);
						if (s >= l - 1) {
							if (s <= l + 3 + p) {
								chunk.setBlockState(mutable, blockState5, false);
								bl2 = true;
							} else {
								BlockState blockState8;
								if (s < 64 || s > 127) {
									blockState8 = ORANGE_TERRACOTTA;
								} else if (bl) {
									blockState8 = TERRACOTTA;
								} else {
									blockState8 = this.calculateLayerBlockState(i, s, j);
								}

								chunk.setBlockState(mutable, blockState8, false);
							}
						} else {
							chunk.setBlockState(mutable, blockState6, false);
							Block block = blockState6.getBlock();
							if (block == Blocks.field_10611
								|| block == Blocks.field_10184
								|| block == Blocks.field_10015
								|| block == Blocks.field_10325
								|| block == Blocks.field_10143
								|| block == Blocks.field_10014
								|| block == Blocks.field_10444
								|| block == Blocks.field_10349
								|| block == Blocks.field_10590
								|| block == Blocks.field_10235
								|| block == Blocks.field_10570
								|| block == Blocks.field_10409
								|| block == Blocks.field_10123
								|| block == Blocks.field_10526
								|| block == Blocks.field_10328
								|| block == Blocks.field_10626) {
								chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
							}
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

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed || this.layerBlocks == null) {
			this.initLayerBlocks(seed);
		}

		if (this.seed != seed || this.heightCutoffNoise == null || this.heightNoise == null) {
			ChunkRandom chunkRandom = new ChunkRandom(seed);
			this.heightCutoffNoise = new OctaveSimplexNoiseSampler(chunkRandom, IntStream.rangeClosed(-3, 0));
			this.heightNoise = new OctaveSimplexNoiseSampler(chunkRandom, ImmutableList.of(0));
		}

		this.seed = seed;
	}

	protected void initLayerBlocks(long seed) {
		this.layerBlocks = new BlockState[64];
		Arrays.fill(this.layerBlocks, TERRACOTTA);
		ChunkRandom chunkRandom = new ChunkRandom(seed);
		this.layerNoise = new OctaveSimplexNoiseSampler(chunkRandom, ImmutableList.of(0));

		for (int i = 0; i < 64; i++) {
			i += chunkRandom.nextInt(5) + 1;
			if (i < 64) {
				this.layerBlocks[i] = ORANGE_TERRACOTTA;
			}
		}

		int ix = chunkRandom.nextInt(4) + 2;

		for (int j = 0; j < ix; j++) {
			int k = chunkRandom.nextInt(3) + 1;
			int l = chunkRandom.nextInt(64);

			for (int m = 0; l + m < 64 && m < k; m++) {
				this.layerBlocks[l + m] = YELLOW_TERRACOTTA;
			}
		}

		int j = chunkRandom.nextInt(4) + 2;

		for (int k = 0; k < j; k++) {
			int l = chunkRandom.nextInt(3) + 2;
			int m = chunkRandom.nextInt(64);

			for (int n = 0; m + n < 64 && n < l; n++) {
				this.layerBlocks[m + n] = BROWN_TERRACOTTA;
			}
		}

		int k = chunkRandom.nextInt(4) + 2;

		for (int l = 0; l < k; l++) {
			int m = chunkRandom.nextInt(3) + 1;
			int n = chunkRandom.nextInt(64);

			for (int o = 0; n + o < 64 && o < m; o++) {
				this.layerBlocks[n + o] = RED_TERRACOTTA;
			}
		}

		int l = chunkRandom.nextInt(3) + 3;
		int m = 0;

		for (int n = 0; n < l; n++) {
			int o = 1;
			m += chunkRandom.nextInt(16) + 4;

			for (int p = 0; m + p < 64 && p < 1; p++) {
				this.layerBlocks[m + p] = WHITE_TERRACOTTA;
				if (m + p > 1 && chunkRandom.nextBoolean()) {
					this.layerBlocks[m + p - 1] = LIGHT_GRAY_TERRACOTTA;
				}

				if (m + p < 63 && chunkRandom.nextBoolean()) {
					this.layerBlocks[m + p + 1] = LIGHT_GRAY_TERRACOTTA;
				}
			}
		}
	}

	protected BlockState calculateLayerBlockState(int x, int y, int z) {
		int i = (int)Math.round(this.layerNoise.sample((double)x / 512.0, (double)z / 512.0, false) * 2.0);
		return this.layerBlocks[(y + i + 64) % 64];
	}
}
