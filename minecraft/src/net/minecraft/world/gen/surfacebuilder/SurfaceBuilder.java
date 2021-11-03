package net.minecraft.world.gen.surfacebuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
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
	private final Map<Identifier, RandomDeriver> field_35633 = new ConcurrentHashMap();
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

	protected RandomDeriver method_39482(Identifier identifier) {
		return (RandomDeriver)this.field_35633.computeIfAbsent(identifier, identifier2 -> this.randomDeriver.createRandom(identifier).createRandomDeriver());
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
		MaterialRules.MaterialRuleContext materialRuleContext = new MaterialRules.MaterialRuleContext(this, chunk, biomeAccess::getBiome, biomeRegistry, context);
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
					this.method_39102(blockColumn, m, n, o, chunk);
				}

				int r = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, k, l) + 1;
				int s = (int)(d * 2.75 + 3.0 + abstractRandom.nextDouble() * 0.25);
				int t = registryKey != BiomeKeys.WOODED_BADLANDS && registryKey != BiomeKeys.BADLANDS ? Integer.MAX_VALUE : 15;
				materialRuleContext.initHorizontalContext(m, n, s);
				int u = 0;
				int v = 0;
				int w = Integer.MIN_VALUE;
				int x = Integer.MAX_VALUE;
				int y = chunk.getBottomY();

				for (int z = r; z >= y && v < t; z--) {
					BlockState blockState = blockColumn.getState(z);
					if (blockState.isAir()) {
						u = 0;
						w = Integer.MIN_VALUE;
					} else if (!blockState.getFluidState().isEmpty()) {
						if (w == Integer.MIN_VALUE) {
							w = z + 1;
						}
					} else {
						if (x >= z) {
							x = DimensionType.field_35479;

							for (int aa = z - 1; aa >= y - 1; aa--) {
								BlockState blockState2 = blockColumn.getState(aa);
								if (!this.isDefaultBlock(blockState2)) {
									x = aa + 1;
									break;
								}
							}
						}

						u++;
						v++;
						int aax = z - x + 1;
						materialRuleContext.initVerticalContext(q, u, aax, w, m, z, n);
						BlockState blockState2 = blockStateRule.tryApply(m, z, n);
						if (blockState2 != null) {
							blockColumn.setState(z, blockState2);
						}
					}
				}

				if (registryKey == BiomeKeys.FROZEN_OCEAN || registryKey == BiomeKeys.DEEP_FROZEN_OCEAN) {
					this.method_39104(q, biome, blockColumn, mutable2, m, n, o);
				}
			}
		}
	}

	private boolean isDefaultBlock(BlockState state) {
		return !state.isAir() && state.getFluidState().isEmpty();
	}

	@Deprecated
	public Optional<BlockState> method_39110(
		MaterialRules.MaterialRule rule, CarverContext context, Function<BlockPos, Biome> function, Chunk chunk, BlockPos pos, boolean bl
	) {
		MaterialRules.MaterialRuleContext materialRuleContext = new MaterialRules.MaterialRuleContext(
			this, chunk, function, context.getRegistryManager().get(Registry.BIOME_KEY), context
		);
		MaterialRules.BlockStateRule blockStateRule = (MaterialRules.BlockStateRule)rule.apply(materialRuleContext);
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		AbstractRandom abstractRandom = this.randomDeriver.createRandom(i, 0, k);
		double d = this.surfaceNoise.sample((double)i, 0.0, (double)k);
		int l = (int)(d * 2.75 + 3.0 + abstractRandom.nextDouble() * 0.25);
		materialRuleContext.initHorizontalContext(i, k, l);
		int m = j - 16;
		materialRuleContext.initVerticalContext(m, 1, 1, bl ? j + 1 : Integer.MIN_VALUE, i, j, k);
		BlockState blockState = blockStateRule.tryApply(i, j, k);
		return Optional.ofNullable(blockState);
	}

	private void method_39102(BlockColumn blockColumn, int x, int z, int surfaceY, HeightLimitView heightLimitView) {
		double d = 0.2;
		double e = Math.min(
			Math.abs(this.field_35497.sample((double)x, 0.0, (double)z) * 8.25), this.field_35495.sample((double)x * 0.2, 0.0, (double)z * 0.2) * 15.0
		);
		if (!(e <= 0.0)) {
			double f = 0.75;
			double g = 1.5;
			double h = Math.abs(this.field_35496.sample((double)x * 0.75, 0.0, (double)z * 0.75) * 1.5);
			double i = 64.0 + Math.min(e * e * 2.5, Math.ceil(h * 50.0) + 24.0);
			int j = MathHelper.floor(i);
			if (surfaceY <= j) {
				for (int k = j; k >= heightLimitView.getBottomY(); k--) {
					BlockState blockState = blockColumn.getState(k);
					if (blockState.isOf(this.defaultBlock.getBlock())) {
						break;
					}

					if (blockState.isOf(Blocks.WATER)) {
						return;
					}
				}

				for (int k = j; k >= heightLimitView.getBottomY() && blockColumn.getState(k).isAir(); k--) {
					blockColumn.setState(k, this.defaultBlock);
				}
			}
		}
	}

	private void method_39104(int i, Biome biome, BlockColumn blockColumn, BlockPos.Mutable mutablePos, int x, int z, int surfaceY) {
		float f = biome.getTemperature(mutablePos.set(x, 63, z));
		double d = 1.28;
		double e = Math.min(
			Math.abs(this.field_35500.sample((double)x, 0.0, (double)z) * 8.25), this.field_35498.sample((double)x * 1.28, 0.0, (double)z * 1.28) * 15.0
		);
		if (!(e <= 1.8)) {
			double g = 1.17;
			double h = 1.5;
			double j = Math.abs(this.field_35499.sample((double)x * 1.17, 0.0, (double)z * 1.17) * 1.5);
			double k = Math.min(e * e * 1.2, Math.ceil(j * 40.0) + 14.0);
			if (f > 0.1F) {
				k -= 2.0;
			}

			double l;
			if (k > 2.0) {
				l = (double)this.seaLevel - k - 7.0;
				k += (double)this.seaLevel;
			} else {
				k = 0.0;
				l = 0.0;
			}

			double m = k;
			AbstractRandom abstractRandom = this.randomDeriver.createRandom(x, 0, z);
			int n = 2 + abstractRandom.nextInt(4);
			int o = this.seaLevel + 18 + abstractRandom.nextInt(10);
			int p = 0;

			for (int q = Math.max(surfaceY, (int)k + 1); q >= i; q--) {
				if (blockColumn.getState(q).isAir() && q < (int)m && abstractRandom.nextDouble() > 0.01
					|| blockColumn.getState(q).getMaterial() == Material.WATER && q > (int)l && q < this.seaLevel && l != 0.0 && abstractRandom.nextDouble() > 0.15) {
					if (p <= n && q > o) {
						blockColumn.setState(q, SNOW_BLOCK);
						p++;
					} else {
						blockColumn.setState(q, PACKED_ICE);
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
