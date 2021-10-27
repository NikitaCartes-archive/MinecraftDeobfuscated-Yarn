package net.minecraft.world.gen.surfacebuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
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
	private final DoublePerlinNoiseSampler field_35495;
	private final DoublePerlinNoiseSampler field_35496;
	private final DoublePerlinNoiseSampler field_35497;
	private final DoublePerlinNoiseSampler field_35498;
	private final DoublePerlinNoiseSampler field_35499;
	private final DoublePerlinNoiseSampler field_35500;
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
		this.randomDeriver = randomProvider.create(l).createRandomDeriver();
		this.terracottaBandsOffsetNoise = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.CLAY_BANDS_OFFSET);
		this.terracottaBands = createTerracottaBands(this.randomDeriver.createRandom(new Identifier("clay_bands")));
		this.surfaceNoise = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.SURFACE);
		this.field_35495 = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.BADLANDS_PILLAR);
		this.field_35496 = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.BADLANDS_PILLAR_ROOF);
		this.field_35497 = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.BADLANDS_SURFACE);
		this.field_35498 = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.ICEBERG_PILLAR);
		this.field_35499 = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.ICEBERG_PILLAR_ROOF);
		this.field_35500 = NoiseParametersKeys.method_39173(registry, this.randomDeriver, NoiseParametersKeys.ICEBERG_SURFACE);
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
					this.method_39102(q, d, blockColumn, m, n, o, chunk);
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
					} else if (!blockState.getFluidState().isEmpty()) {
						if (y == Integer.MIN_VALUE) {
							y = aa + 1;
						}
					} else {
						if (z >= aa) {
							z = DimensionType.field_35479;

							for (int ab = aa - 1; ab >= u - 1; ab--) {
								BlockState blockState2 = blockColumn.getState(ab);
								if (!this.method_39333(blockState2)) {
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
							blockColumn.setState(aa, blockState3);
						}
					}
				}

				if (registryKey == BiomeKeys.FROZEN_OCEAN || registryKey == BiomeKeys.DEEP_FROZEN_OCEAN) {
					this.method_39104(q, biome, d, blockColumn, mutable2, m, n, o);
				}
			}
		}
	}

	private boolean method_39333(BlockState blockState) {
		return !blockState.isAir() && blockState.getFluidState().isEmpty();
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

	private void method_39102(int i, double d, BlockColumn chunk, int x, int z, int j, HeightLimitView heightLimitView) {
		double e = 0.2;
		double f = Math.min(
			Math.abs(this.field_35497.sample((double)x, 0.0, (double)z) * 8.25), this.field_35495.sample((double)x * 0.2, 0.0, (double)z * 0.2) * 15.0
		);
		if (!(f <= 0.0)) {
			double g = 0.75;
			double h = 1.5;
			double k = Math.abs(this.field_35496.sample((double)x * 0.75, 0.0, (double)z * 0.75) * 1.5);
			double l = 64.0 + Math.min(f * f * 2.5, Math.ceil(k * 50.0) + 24.0);
			int m = MathHelper.floor(l);
			if (j <= m) {
				for (int n = m; n >= heightLimitView.getBottomY(); n--) {
					BlockState blockState = chunk.getState(n);
					if (blockState.isOf(this.defaultBlock.getBlock())) {
						break;
					}

					if (blockState.isOf(Blocks.WATER)) {
						return;
					}
				}

				for (int n = m; n >= heightLimitView.getBottomY() && chunk.getState(n).isAir(); n--) {
					chunk.setState(n, this.defaultBlock);
				}
			}
		}
	}

	private void method_39104(int i, Biome biome, double d, BlockColumn chunk, BlockPos.Mutable mutablePos, int x, int z, int surfaceY) {
		float f = biome.getTemperature(mutablePos.set(x, 63, z));
		double e = 1.28;
		double g = Math.min(
			Math.abs(this.field_35500.sample((double)x, 0.0, (double)z) * 8.25), this.field_35498.sample((double)x * 1.28, 0.0, (double)z * 1.28) * 15.0
		);
		if (!(g <= 1.8)) {
			double h = 1.17;
			double j = 1.5;
			double k = Math.abs(this.field_35499.sample((double)x * 1.17, 0.0, (double)z * 1.17) * 1.5);
			double l = Math.min(g * g * 1.2, Math.ceil(k * 40.0) + 14.0);
			if (f > 0.1F) {
				l -= 2.0;
			}

			double m;
			if (l > 2.0) {
				l += (double)this.seaLevel;
				m = (double)this.seaLevel - l - 7.0;
			} else {
				l = 0.0;
				m = 0.0;
			}

			double n = l;
			AbstractRandom abstractRandom = this.randomDeriver.createRandom(x, 0, z);
			int o = 2 + abstractRandom.nextInt(4);
			int p = this.seaLevel + 18 + abstractRandom.nextInt(10);
			int q = 0;

			for (int r = Math.max(surfaceY, (int)l + 1); r >= i; r--) {
				if (chunk.getState(r).isAir() && r < (int)n && abstractRandom.nextDouble() > 0.01
					|| chunk.getState(r).getMaterial() == Material.WATER && r > (int)m && r < this.seaLevel && m != 0.0 && abstractRandom.nextDouble() > 0.15) {
					if (q <= o && r > p) {
						chunk.setState(r, SNOW_BLOCK);
						q++;
					} else {
						chunk.setState(r, PACKED_ICE);
					}
				}
			}
		}
	}

	private static BlockState[] createTerracottaBands(AbstractRandom random) {
		BlockState[] blockStates = new BlockState[192];
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
		int ix = random.nextBetween(9, 15);
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
		int i = random.nextBetween(6, 15);

		for (int j = 0; j < i; j++) {
			int k = minBandSize + random.nextInt(3);
			int l = random.nextInt(terracottaBands.length);

			for (int m = 0; l + m < terracottaBands.length && m < k; m++) {
				terracottaBands[l + m] = state;
			}
		}
	}

	protected BlockState getTerracottaBlock(int x, int y, int z) {
		int i = (int)Math.round(this.terracottaBandsOffsetNoise.sample((double)x, 0.0, (double)z) * 4.0);
		return this.terracottaBands[(y + i + this.terracottaBands.length) % this.terracottaBands.length];
	}
}
