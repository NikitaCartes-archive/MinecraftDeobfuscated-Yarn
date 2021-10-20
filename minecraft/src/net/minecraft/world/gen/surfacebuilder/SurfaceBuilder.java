package net.minecraft.world.gen.surfacebuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.chunk.BlockColumn;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomDeriver;

public class SurfaceBuilder {
	private static final int field_35273 = 8;
	private static final int field_35274 = 15;
	private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
	private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
	private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();
	private static final BlockState YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.getDefaultState();
	private static final BlockState BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.getDefaultState();
	private static final BlockState RED_TERRACOTTA = Blocks.RED_TERRACOTTA.getDefaultState();
	private static final BlockState LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.getDefaultState();
	private static final BlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
	private static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.getDefaultState();
	private final NoiseColumnSampler noiseColumnSampler;
	private final BlockState defaultBlock;
	private final int seaLevel;
	private final BlockState[] terracottaBands;
	private final DoublePerlinNoiseSampler terracottaBandsOffsetNoise;
	private final DoublePerlinNoiseSampler icebergAndBadlandsPillarNoise;
	private final DoublePerlinNoiseSampler icebergAndBadlandsPillarRoofNoise;
	private final Registry<DoublePerlinNoiseSampler.NoiseParameters> field_35415;
	private final Map<RegistryKey<DoublePerlinNoiseSampler.NoiseParameters>, DoublePerlinNoiseSampler> noiseSamplers = new ConcurrentHashMap();
	private final RandomDeriver randomDeriver;
	private final DoublePerlinNoiseSampler surfaceNoise;

	public SurfaceBuilder(
		NoiseColumnSampler noiseColumnSampler,
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry,
		BlockState blockState,
		int i,
		long l,
		ChunkRandom.RandomProvider randomProvider
	) {
		this.noiseColumnSampler = noiseColumnSampler;
		this.field_35415 = registry;
		this.defaultBlock = blockState;
		this.seaLevel = i;
		this.randomDeriver = randomProvider.create(l).createBlockPosRandomDeriver();
		this.terracottaBandsOffsetNoise = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.CLAY_BANDS_OFFSET);
		this.terracottaBands = createTerracottaBands(this.randomDeriver.createRandom("clay_bands"));
		this.surfaceNoise = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.SURFACE);
		this.icebergAndBadlandsPillarNoise = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.ICEBERG_AND_BADLANDS_PILLAR);
		this.icebergAndBadlandsPillarRoofNoise = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.ICEBERG_AND_BADLANDS_PILLAR_ROOF);
	}

	protected DoublePerlinNoiseSampler getNoiseSampler(RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> registryKey) {
		return (DoublePerlinNoiseSampler)this.noiseSamplers
			.computeIfAbsent(registryKey, registryKey2 -> NoiseParametersKeys.method_39173(this.field_35415, this.randomDeriver, registryKey));
	}

	public void buildSurface(
		BiomeAccess biomeAccess,
		Registry<Biome> biomeRegistry,
		boolean useLegacyRandom,
		HeightContext context,
		Chunk chunk,
		ChunkNoiseSampler chunkNoiseSampler,
		MaterialRules.MaterialRule surfaceRule
	) {
		final BlockPos.Mutable mutable = new BlockPos.Mutable();
		final ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		BlockColumn blockColumn = new BlockColumn() {
			@Override
			public BlockState getState(int y) {
				return chunk.getBlockState(mutable.setY(y));
			}

			@Override
			public void setState(int y, BlockState state) {
				chunk.setBlockState(mutable.setY(y), state, false);
			}

			public String toString() {
				return "ChunkBlockColumn " + chunkPos;
			}
		};
		MaterialRules.MaterialRuleContext materialRuleContext = new MaterialRules.MaterialRuleContext(this, context);
		MaterialRules.BlockStateRule blockStateRule = (MaterialRules.BlockStateRule)surfaceRule.apply(materialRuleContext);
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				int m = i + k;
				int n = j + l;
				int o = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, k, l) + 1;
				AbstractRandom abstractRandom = this.randomDeriver.createRandom(m, 0, n);
				double d = this.surfaceNoise.sample((double)m, 0.0, (double)n);
				mutable.setX(m).setZ(n);
				int p = this.noiseColumnSampler.method_38383(m, n, chunkNoiseSampler.getInterpolatedTerrainNoisePoint(m, n));
				int q = p - 8;
				Biome biome = biomeAccess.getBiome(mutable2.set(m, useLegacyRandom ? 0 : o, n));
				RegistryKey<Biome> registryKey = (RegistryKey<Biome>)biomeRegistry.getKey(biome)
					.orElseThrow(() -> new IllegalStateException("Unregistered biome: " + biome));
				if (registryKey == BiomeKeys.ERODED_BADLANDS) {
					this.method_39102(q, d, blockColumn, m, n, o);
				}

				int r = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, k, l) + 1;
				int s = (int)(d * 2.75 + 3.0 + abstractRandom.nextDouble() * 0.25);
				int t;
				int u;
				if (registryKey != BiomeKeys.BASALT_DELTAS
					&& registryKey != BiomeKeys.SOUL_SAND_VALLEY
					&& registryKey != BiomeKeys.WARPED_FOREST
					&& registryKey != BiomeKeys.CRIMSON_FOREST
					&& registryKey != BiomeKeys.NETHER_WASTES) {
					t = r;
					u = q;
				} else {
					t = 127;
					u = 0;
				}

				int v = registryKey != BiomeKeys.WOODED_BADLANDS && registryKey != BiomeKeys.BADLANDS ? Integer.MAX_VALUE : 15;
				materialRuleContext.initWorldDependentPredicates(chunk, m, n, s);
				int w = 0;
				int x = 0;
				int y = Integer.MIN_VALUE;
				int z = Integer.MAX_VALUE;

				for (int aa = t; aa >= u && x < v; aa--) {
					BlockState blockState = blockColumn.getState(aa);
					if (blockState.isAir()) {
						w = 0;
						y = Integer.MIN_VALUE;
					} else if (!blockState.isOf(this.defaultBlock.getBlock())) {
						if (y == Integer.MIN_VALUE) {
							y = aa + 1;
						}
					} else {
						if (materialRuleContext.needsCeilingStoneDepth() && z >= aa) {
							z = Integer.MIN_VALUE;

							for (int ab = aa - 1; ab >= u; ab--) {
								BlockState blockState2 = blockColumn.getState(ab);
								if (!blockState2.isOf(this.defaultBlock.getBlock())) {
									z = ab + 1;
									break;
								}
							}
						}

						w++;
						x++;
						int abx = aa - z + 1;
						Biome biome2 = biomeAccess.getBiome(mutable2.set(m, aa, n));
						RegistryKey<Biome> registryKey2 = (RegistryKey<Biome>)biomeRegistry.getKey(biome2)
							.orElseThrow(() -> new IllegalStateException("Unregistered biome: " + biome));
						materialRuleContext.initContextDependentPredicates(registryKey2, biome2, s, w, abx, y, m, aa, n);
						BlockState blockState3 = blockStateRule.tryApply(m, aa, n);
						if (blockState3 != null) {
							blockColumn.setState(aa, this.getBlockStateToPlace(blockColumn, aa, blockState3, (double)y));
						}
					}
				}

				if (registryKey == BiomeKeys.FROZEN_OCEAN || registryKey == BiomeKeys.DEEP_FROZEN_OCEAN) {
					this.method_39104(q, biome, d, blockColumn, mutable2, m, n, o);
				}
			}
		}
	}

	@Deprecated
	public Optional<BlockState> method_39110(
		MaterialRules.MaterialRule rule, CarverContext context, Biome biome, RegistryKey<Biome> biomeKey, Chunk chunk, BlockPos pos, boolean bl
	) {
		MaterialRules.MaterialRuleContext materialRuleContext = new MaterialRules.MaterialRuleContext(this, context);
		MaterialRules.BlockStateRule blockStateRule = (MaterialRules.BlockStateRule)rule.apply(materialRuleContext);
		AbstractRandom abstractRandom = this.randomDeriver.createRandom(pos.getX(), 0, pos.getZ());
		double d = this.surfaceNoise.sample((double)pos.getX(), 0.0, (double)pos.getZ());
		int i = (int)(d * 2.75 + 3.0 + abstractRandom.nextDouble() * 0.25);
		materialRuleContext.initWorldDependentPredicates(chunk, pos.getX(), pos.getZ(), i);
		materialRuleContext.initContextDependentPredicates(biomeKey, biome, i, 1, 1, bl ? pos.getY() + 1 : Integer.MIN_VALUE, pos.getX(), pos.getY(), pos.getZ());
		BlockState blockState = blockStateRule.tryApply(pos.getX(), pos.getY(), pos.getZ());
		return Optional.ofNullable(blockState);
	}

	private void method_39102(int i, double d, BlockColumn chunk, int x, int z, int j) {
		double e = Math.min(Math.abs(d * 8.25), this.icebergAndBadlandsPillarNoise.sample((double)x * 0.25, 0.0, (double)z * 0.25) * 15.0);
		if (!(e <= 0.0)) {
			double f = 0.001953125;
			double g = Math.abs(this.icebergAndBadlandsPillarRoofNoise.sample((double)x * 0.001953125, 0.0, (double)z * 0.001953125));
			double h = 64.0 + Math.min(e * e * 2.5, Math.ceil(g * 50.0) + 14.0);
			int k = Math.max(j, (int)h + 1);

			for (int l = k; l >= i; l--) {
				BlockState blockState = chunk.getState(l);
				if (blockState.isOf(this.defaultBlock.getBlock())) {
					break;
				}

				if (blockState.isOf(Blocks.WATER)) {
					return;
				}
			}

			for (int l = k; l >= i; l--) {
				if (chunk.getState(l).isAir() && l < (int)h) {
					chunk.setState(l, this.defaultBlock);
				}
			}
		}
	}

	private void method_39104(int i, Biome biome, double d, BlockColumn chunk, BlockPos.Mutable mutablePos, int x, int z, int surfaceY) {
		float f = biome.getTemperature(mutablePos.set(x, 63, z));
		double e = Math.min(Math.abs(d * 8.25), this.icebergAndBadlandsPillarNoise.sample((double)x * 0.1, 0.0, (double)z * 0.1) * 15.0);
		if (!(e <= 1.8)) {
			double g = 0.09765625;
			double h = Math.abs(this.icebergAndBadlandsPillarRoofNoise.sample((double)x * 0.09765625, 0.0, (double)z * 0.09765625));
			double j = Math.min(e * e * 1.2, Math.ceil(h * 40.0) + 14.0);
			if (f > 0.1F) {
				j -= 2.0;
			}

			double k;
			if (j > 2.0) {
				j += (double)this.seaLevel;
				k = (double)this.seaLevel - j - 7.0;
			} else {
				j = 0.0;
				k = 0.0;
			}

			double l = j;
			AbstractRandom abstractRandom = this.randomDeriver.createRandom(x, 0, z);
			int m = 2 + abstractRandom.nextInt(4);
			int n = this.seaLevel + 18 + abstractRandom.nextInt(10);
			int o = 0;

			for (int p = Math.max(surfaceY, (int)j + 1); p >= i; p--) {
				if (chunk.getState(p).isAir() && p < (int)l && abstractRandom.nextDouble() > 0.01
					|| chunk.getState(p).getMaterial() == Material.WATER && p > (int)k && p < this.seaLevel && k != 0.0 && abstractRandom.nextDouble() > 0.15) {
					if (o <= m && p > n) {
						chunk.setState(p, SNOW_BLOCK);
						o++;
					} else {
						chunk.setState(p, PACKED_ICE);
					}
				}
			}
		}
	}

	private BlockState getBlockStateToPlace(BlockColumn chunk, int y, BlockState state, double waterHeight) {
		if ((double)y <= waterHeight && state.isOf(Blocks.GRASS_BLOCK)) {
			return Blocks.DIRT.getDefaultState();
		} else if (chunk.getState(y - 1).isOf(this.defaultBlock.getBlock())) {
			return state;
		} else if (state.isOf(Blocks.SAND)) {
			return Blocks.SANDSTONE.getDefaultState();
		} else if (state.isOf(Blocks.RED_SAND)) {
			return Blocks.RED_SANDSTONE.getDefaultState();
		} else {
			return state.isOf(Blocks.GRAVEL) ? Blocks.STONE.getDefaultState() : state;
		}
	}

	private static BlockState[] createTerracottaBands(AbstractRandom random) {
		BlockState[] blockStates = new BlockState[64];
		Arrays.fill(blockStates, TERRACOTTA);

		for (int i = 0; i < blockStates.length; i++) {
			i += random.nextInt(5) + 1;
			if (i < blockStates.length) {
				blockStates[i] = ORANGE_TERRACOTTA;
			}
		}

		addTerracottaBands(random, blockStates, 1, YELLOW_TERRACOTTA);
		addTerracottaBands(random, blockStates, 2, BROWN_TERRACOTTA);
		addTerracottaBands(random, blockStates, 1, RED_TERRACOTTA);
		int ix = random.nextInt(3) + 3;
		int j = 0;

		for (int k = 0; j < ix && k < blockStates.length; k += random.nextInt(16) + 4) {
			blockStates[k] = WHITE_TERRACOTTA;
			if (k - 1 > 0 && random.nextBoolean()) {
				blockStates[k - 1] = LIGHT_GRAY_TERRACOTTA;
			}

			if (k + 1 < blockStates.length && random.nextBoolean()) {
				blockStates[k + 1] = LIGHT_GRAY_TERRACOTTA;
			}

			j++;
		}

		return blockStates;
	}

	private static void addTerracottaBands(AbstractRandom random, BlockState[] terracottaBands, int minBandSize, BlockState state) {
		int i = random.nextInt(4) + 2;

		for (int j = 0; j < i; j++) {
			int k = minBandSize + random.nextInt(3);
			int l = random.nextInt(terracottaBands.length);

			for (int m = 0; l + m < terracottaBands.length && m < k; m++) {
				terracottaBands[l + m] = state;
			}
		}
	}

	protected BlockState getTerracottaBlock(int x, int y, int z) {
		int i = (int)Math.round(this.terracottaBandsOffsetNoise.sample((double)x, 0.0, (double)z) * 2.0);
		return this.terracottaBands[(y + i + this.terracottaBands.length) % this.terracottaBands.length];
	}
}
