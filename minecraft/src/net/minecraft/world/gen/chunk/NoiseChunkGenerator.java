package net.minecraft.world.gen.chunk;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoise;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.AquiferSampler;
import net.minecraft.world.gen.BlockInterpolator;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GrimstoneInterpolator;
import net.minecraft.world.gen.NoiseCaveSampler;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.SimpleRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.feature.StructureFeature;

public final class NoiseChunkGenerator extends ChunkGenerator {
	public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BiomeSource.CODEC.fieldOf("biome_source").forGetter(noiseChunkGenerator -> noiseChunkGenerator.populationSource),
					Codec.LONG.fieldOf("seed").stable().forGetter(noiseChunkGenerator -> noiseChunkGenerator.seed),
					ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(noiseChunkGenerator -> noiseChunkGenerator.settings)
				)
				.apply(instance, instance.stable(NoiseChunkGenerator::new))
	);
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private static final BlockState[] EMPTY = new BlockState[0];
	private final int verticalNoiseResolution;
	private final int horizontalNoiseResolution;
	private final int noiseSizeX;
	private final int noiseSizeY;
	private final int noiseSizeZ;
	private final NoiseSampler surfaceDepthNoise;
	private final DoublePerlinNoiseSampler field_28843;
	private final DoublePerlinNoiseSampler waterLevelNoise;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;
	private final long seed;
	protected final Supplier<ChunkGeneratorSettings> settings;
	private final int worldHeight;
	private final NoiseColumnSampler noiseColumnSampler;
	private final boolean hasAquifers;
	private final BlockInterpolator blockInterpolator;

	public NoiseChunkGenerator(BiomeSource biomeSource, long seed, Supplier<ChunkGeneratorSettings> settings) {
		this(biomeSource, biomeSource, seed, settings);
	}

	private NoiseChunkGenerator(BiomeSource populationSource, BiomeSource biomeSource, long seed, Supplier<ChunkGeneratorSettings> settings) {
		super(populationSource, biomeSource, ((ChunkGeneratorSettings)settings.get()).getStructuresConfig(), seed);
		this.seed = seed;
		ChunkGeneratorSettings chunkGeneratorSettings = (ChunkGeneratorSettings)settings.get();
		this.settings = settings;
		GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
		this.worldHeight = generationShapeConfig.getHeight();
		this.verticalNoiseResolution = BiomeCoords.toBlock(generationShapeConfig.getSizeVertical());
		this.horizontalNoiseResolution = BiomeCoords.toBlock(generationShapeConfig.getSizeHorizontal());
		this.defaultBlock = chunkGeneratorSettings.getDefaultBlock();
		this.defaultFluid = chunkGeneratorSettings.getDefaultFluid();
		this.noiseSizeX = 16 / this.horizontalNoiseResolution;
		this.noiseSizeY = generationShapeConfig.getHeight() / this.verticalNoiseResolution;
		this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
		ChunkRandom chunkRandom = new ChunkRandom(seed);
		InterpolatedNoise interpolatedNoise = new InterpolatedNoise(chunkRandom);
		this.surfaceDepthNoise = (NoiseSampler)(generationShapeConfig.hasSimplexSurfaceNoise()
			? new OctaveSimplexNoiseSampler(chunkRandom, IntStream.rangeClosed(-3, 0))
			: new OctavePerlinNoiseSampler(chunkRandom, IntStream.rangeClosed(-3, 0)));
		chunkRandom.skip(2620);
		OctavePerlinNoiseSampler octavePerlinNoiseSampler = new OctavePerlinNoiseSampler(chunkRandom, IntStream.rangeClosed(-15, 0));
		SimplexNoiseSampler simplexNoiseSampler;
		if (generationShapeConfig.hasIslandNoiseOverride()) {
			ChunkRandom chunkRandom2 = new ChunkRandom(seed);
			chunkRandom2.skip(17292);
			simplexNoiseSampler = new SimplexNoiseSampler(chunkRandom2);
		} else {
			simplexNoiseSampler = null;
		}

		this.field_28843 = DoublePerlinNoiseSampler.create(new SimpleRandom(chunkRandom.nextLong()), -3, 1.0);
		this.waterLevelNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(chunkRandom.nextLong()), -3, 1.0, 0.0, 2.0);
		NoiseCaveSampler noiseCaveSampler = chunkGeneratorSettings.hasNoiseCaves()
			? new NoiseCaveSampler(chunkRandom, generationShapeConfig.getMinimumY() / this.verticalNoiseResolution)
			: null;
		this.noiseColumnSampler = new NoiseColumnSampler(
			populationSource,
			this.horizontalNoiseResolution,
			this.verticalNoiseResolution,
			this.noiseSizeY,
			generationShapeConfig,
			interpolatedNoise,
			simplexNoiseSampler,
			octavePerlinNoiseSampler,
			noiseCaveSampler
		);
		this.hasAquifers = chunkGeneratorSettings.hasAquifers();
		this.blockInterpolator = new GrimstoneInterpolator(seed, this.defaultBlock, Blocks.DEEPSLATE.getDefaultState());
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ChunkGenerator withSeed(long seed) {
		return new NoiseChunkGenerator(this.populationSource.withSeed(seed), seed, this.settings);
	}

	public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settingsKey) {
		return this.seed == seed && ((ChunkGeneratorSettings)this.settings.get()).equals(settingsKey);
	}

	private double[] sampleNoiseColumn(int x, int z, int minY, int noiseSizeY) {
		double[] ds = new double[noiseSizeY + 1];
		this.noiseColumnSampler
			.sampleNoiseColumn(ds, x, z, ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig(), this.getSeaLevel(), minY, noiseSizeY);
		return ds;
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
		int i = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY(), world.getBottomY());
		int j = Math.min(
			((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY()
				+ ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getHeight(),
			world.getTopY()
		);
		int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
		int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
		return l <= 0 ? world.getBottomY() : this.sampleHeightmap(x, z, null, heightmap.getBlockPredicate(), k, l).orElse(world.getBottomY());
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		int i = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY(), world.getBottomY());
		int j = Math.min(
			((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY()
				+ ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getHeight(),
			world.getTopY()
		);
		int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
		int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
		if (l <= 0) {
			return new VerticalBlockSample(i, EMPTY);
		} else {
			BlockState[] blockStates = new BlockState[l * this.verticalNoiseResolution];
			this.sampleHeightmap(x, z, blockStates, null, k, l);
			return new VerticalBlockSample(i, blockStates);
		}
	}

	private OptionalInt sampleHeightmap(int x, int z, @Nullable BlockState[] states, @Nullable Predicate<BlockState> predicate, int minY, int noiseSizeY) {
		int i = ChunkSectionPos.getSectionCoord(x);
		int j = ChunkSectionPos.getSectionCoord(z);
		int k = Math.floorDiv(x, this.horizontalNoiseResolution);
		int l = Math.floorDiv(z, this.horizontalNoiseResolution);
		int m = Math.floorMod(x, this.horizontalNoiseResolution);
		int n = Math.floorMod(z, this.horizontalNoiseResolution);
		double d = (double)m / (double)this.horizontalNoiseResolution;
		double e = (double)n / (double)this.horizontalNoiseResolution;
		double[][] ds = new double[][]{
			this.sampleNoiseColumn(k, l, minY, noiseSizeY),
			this.sampleNoiseColumn(k, l + 1, minY, noiseSizeY),
			this.sampleNoiseColumn(k + 1, l, minY, noiseSizeY),
			this.sampleNoiseColumn(k + 1, l + 1, minY, noiseSizeY)
		};
		AquiferSampler aquiferSampler = this.hasAquifers
			? new AquiferSampler(
				i,
				j,
				this.field_28843,
				this.waterLevelNoise,
				(ChunkGeneratorSettings)this.settings.get(),
				this.noiseColumnSampler,
				noiseSizeY * this.verticalNoiseResolution
			)
			: null;

		for (int o = noiseSizeY - 1; o >= 0; o--) {
			double f = ds[0][o];
			double g = ds[1][o];
			double h = ds[2][o];
			double p = ds[3][o];
			double q = ds[0][o + 1];
			double r = ds[1][o + 1];
			double s = ds[2][o + 1];
			double t = ds[3][o + 1];

			for (int u = this.verticalNoiseResolution - 1; u >= 0; u--) {
				double v = (double)u / (double)this.verticalNoiseResolution;
				double w = MathHelper.lerp3(v, d, e, f, q, h, s, g, r, p, t);
				int y = o * this.verticalNoiseResolution + u;
				int aa = y + minY * this.verticalNoiseResolution;
				BlockState blockState = this.getBlockState(StructureWeightSampler.INSTANCE, aquiferSampler, this.blockInterpolator, x, aa, z, w);
				if (states != null) {
					states[y] = blockState;
				}

				if (predicate != null && predicate.test(blockState)) {
					return OptionalInt.of(aa + 1);
				}
			}
		}

		return OptionalInt.empty();
	}

	protected BlockState getBlockState(
		StructureWeightSampler structures, @Nullable AquiferSampler aquiferSampler, BlockInterpolator blockInterpolator, int x, int y, int z, double noise
	) {
		double d = MathHelper.clamp(noise / 200.0, -1.0, 1.0);
		d = d / 2.0 - d * d * d / 24.0;
		d += structures.getWeight(x, y, z);
		if (aquiferSampler != null) {
			aquiferSampler.apply(x, y, z);
			d += aquiferSampler.getDensityAddition();
		}

		BlockState blockState;
		if (d > 0.0) {
			blockState = blockInterpolator.sample(x, y, z, (ChunkGeneratorSettings)this.settings.get());
		} else {
			int i = aquiferSampler == null ? this.getSeaLevel() : aquiferSampler.getWaterLevel();
			if (y < i) {
				blockState = this.defaultFluid;
			} else {
				blockState = AIR;
			}
		}

		return blockState;
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setTerrainSeed(i, j);
		ChunkPos chunkPos2 = chunk.getPos();
		int k = chunkPos2.getStartX();
		int l = chunkPos2.getStartZ();
		double d = 0.0625;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = 0; m < 16; m++) {
			for (int n = 0; n < 16; n++) {
				int o = k + m;
				int p = l + n;
				int q = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, m, n) + 1;
				double e = this.surfaceDepthNoise.sample((double)o * 0.0625, (double)p * 0.0625, 0.0625, (double)m * 0.0625) * 15.0;
				region.getBiome(mutable.set(k + m, q, l + n))
					.buildSurface(chunkRandom, chunk, o, p, q, e, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), region.getSeed());
			}
		}

		this.buildBedrock(chunk, chunkRandom);
	}

	private void buildBedrock(Chunk chunk, Random random) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = chunk.getPos().getStartX();
		int j = chunk.getPos().getStartZ();
		ChunkGeneratorSettings chunkGeneratorSettings = (ChunkGeneratorSettings)this.settings.get();
		int k = chunkGeneratorSettings.getGenerationShapeConfig().getMinimumY();
		int l = k + chunkGeneratorSettings.getBedrockFloorY();
		int m = this.worldHeight - 1 + k - chunkGeneratorSettings.getBedrockCeilingY();
		int n = 5;
		int o = chunk.getBottomY();
		int p = chunk.getTopY();
		boolean bl = m + 5 - 1 >= o && m < p;
		boolean bl2 = l + 5 - 1 >= o && l < p;
		if (bl || bl2) {
			for (BlockPos blockPos : BlockPos.iterate(i, 0, j, i + 15, 0, j + 15)) {
				if (bl) {
					for (int q = 0; q < 5; q++) {
						if (q <= random.nextInt(5)) {
							chunk.setBlockState(mutable.set(blockPos.getX(), m - q, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
						}
					}
				}

				if (bl2) {
					for (int qx = 4; qx >= 0; qx--) {
						if (qx <= random.nextInt(5)) {
							chunk.setBlockState(mutable.set(blockPos.getX(), l + qx, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
						}
					}
				}
			}
		}
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk) {
		GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();
		int i = Math.max(generationShapeConfig.getMinimumY(), chunk.getBottomY());
		int j = Math.min(generationShapeConfig.getMinimumY() + generationShapeConfig.getHeight(), chunk.getTopY());
		int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
		int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
		if (l <= 0) {
			return CompletableFuture.completedFuture(chunk);
		} else {
			int m = chunk.getSectionIndex(l * this.verticalNoiseResolution - 1 + i);
			int n = chunk.getSectionIndex(i);
			Set<ChunkSection> set = Sets.<ChunkSection>newHashSet();

			for (int o = m; o >= n; o--) {
				ChunkSection chunkSection = chunk.getSection(o);
				chunkSection.lock();
				set.add(chunkSection);
			}

			return CompletableFuture.supplyAsync(() -> this.populateNoise(accessor, chunk, k, l), Util.getMainWorkerExecutor()).thenApplyAsync(chunkx -> {
				for (ChunkSection chunkSectionx : set) {
					chunkSectionx.unlock();
				}

				return chunkx;
			}, executor);
		}
	}

	private Chunk populateNoise(StructureAccessor accessor, Chunk chunk, int minY, int noiseSizeY) {
		GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();
		int i = generationShapeConfig.getMinimumY();
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		ChunkPos chunkPos = chunk.getPos();
		int j = chunkPos.x;
		int k = chunkPos.z;
		int l = chunkPos.getStartX();
		int m = chunkPos.getStartZ();
		StructureWeightSampler structureWeightSampler = new StructureWeightSampler(accessor, chunk);
		AquiferSampler aquiferSampler = this.hasAquifers
			? new AquiferSampler(
				j,
				k,
				this.field_28843,
				this.waterLevelNoise,
				(ChunkGeneratorSettings)this.settings.get(),
				this.noiseColumnSampler,
				noiseSizeY * this.verticalNoiseResolution
			)
			: null;
		double[][][] ds = new double[2][this.noiseSizeZ + 1][noiseSizeY + 1];

		for (int n = 0; n < this.noiseSizeZ + 1; n++) {
			ds[0][n] = new double[noiseSizeY + 1];
			double[] es = ds[0][n];
			int o = j * this.noiseSizeX;
			int p = k * this.noiseSizeZ + n;
			this.noiseColumnSampler.sampleNoiseColumn(es, o, p, generationShapeConfig, this.getSeaLevel(), minY, noiseSizeY);
			ds[1][n] = new double[noiseSizeY + 1];
		}

		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int q = 0; q < this.noiseSizeX; q++) {
			int o = j * this.noiseSizeX + q + 1;

			for (int p = 0; p < this.noiseSizeZ + 1; p++) {
				double[] fs = ds[1][p];
				int r = k * this.noiseSizeZ + p;
				this.noiseColumnSampler.sampleNoiseColumn(fs, o, r, generationShapeConfig, this.getSeaLevel(), minY, noiseSizeY);
			}

			for (int p = 0; p < this.noiseSizeZ; p++) {
				ChunkSection chunkSection = chunk.getSection(chunk.countVerticalSections() - 1);

				for (int r = noiseSizeY - 1; r >= 0; r--) {
					double d = ds[0][p][r];
					double e = ds[0][p + 1][r];
					double f = ds[1][p][r];
					double g = ds[1][p + 1][r];
					double h = ds[0][p][r + 1];
					double s = ds[0][p + 1][r + 1];
					double t = ds[1][p][r + 1];
					double u = ds[1][p + 1][r + 1];

					for (int v = this.verticalNoiseResolution - 1; v >= 0; v--) {
						int w = r * this.verticalNoiseResolution + v + i;
						int x = w & 15;
						int y = chunk.getSectionIndex(w);
						if (chunk.getSectionIndex(chunkSection.getYOffset()) != y) {
							chunkSection = chunk.getSection(y);
						}

						double z = (double)v / (double)this.verticalNoiseResolution;
						double aa = MathHelper.lerp(z, d, h);
						double ab = MathHelper.lerp(z, f, t);
						double ac = MathHelper.lerp(z, e, s);
						double ad = MathHelper.lerp(z, g, u);

						for (int ae = 0; ae < this.horizontalNoiseResolution; ae++) {
							int af = l + q * this.horizontalNoiseResolution + ae;
							int ag = af & 15;
							double ah = (double)ae / (double)this.horizontalNoiseResolution;
							double ai = MathHelper.lerp(ah, aa, ab);
							double aj = MathHelper.lerp(ah, ac, ad);

							for (int ak = 0; ak < this.horizontalNoiseResolution; ak++) {
								int al = m + p * this.horizontalNoiseResolution + ak;
								int am = al & 15;
								double an = (double)ak / (double)this.horizontalNoiseResolution;
								double ao = MathHelper.lerp(an, ai, aj);
								BlockState blockState = this.getBlockState(structureWeightSampler, aquiferSampler, this.blockInterpolator, af, w, al, ao);
								if (blockState != AIR) {
									if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
										mutable.set(af, w, al);
										((ProtoChunk)chunk).addLightSource(mutable);
									}

									chunkSection.setBlockState(ag, x, am, blockState, false);
									heightmap.trackUpdate(ag, w, am, blockState);
									heightmap2.trackUpdate(ag, w, am, blockState);
									if (aquiferSampler != null && aquiferSampler.needsFluidTick() && !blockState.getFluidState().isEmpty()) {
										mutable.set(af, w, al);
										chunk.getFluidTickScheduler().schedule(mutable, blockState.getFluidState().getFluid(), 0);
									}
								}
							}
						}
					}
				}
			}

			this.swapElements(ds);
		}

		return chunk;
	}

	public <T> void swapElements(T[] array) {
		T object = array[0];
		array[0] = array[1];
		array[1] = object;
	}

	@Override
	public int getWorldHeight() {
		return this.worldHeight;
	}

	@Override
	public int getSeaLevel() {
		return ((ChunkGeneratorSettings)this.settings.get()).getSeaLevel();
	}

	@Override
	public int getMinimumY() {
		return ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY();
	}

	@Override
	public List<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
		if (accessor.getStructureAt(pos, true, StructureFeature.SWAMP_HUT).hasChildren()) {
			if (group == SpawnGroup.MONSTER) {
				return StructureFeature.SWAMP_HUT.getMonsterSpawns();
			}

			if (group == SpawnGroup.CREATURE) {
				return StructureFeature.SWAMP_HUT.getCreatureSpawns();
			}
		}

		if (group == SpawnGroup.MONSTER) {
			if (accessor.getStructureAt(pos, false, StructureFeature.PILLAGER_OUTPOST).hasChildren()) {
				return StructureFeature.PILLAGER_OUTPOST.getMonsterSpawns();
			}

			if (accessor.getStructureAt(pos, false, StructureFeature.MONUMENT).hasChildren()) {
				return StructureFeature.MONUMENT.getMonsterSpawns();
			}

			if (accessor.getStructureAt(pos, true, StructureFeature.FORTRESS).hasChildren()) {
				return StructureFeature.FORTRESS.getMonsterSpawns();
			}
		}

		return super.getEntitySpawnList(biome, accessor, group, pos);
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		if (!((ChunkGeneratorSettings)this.settings.get()).isMobGenerationDisabled()) {
			ChunkPos chunkPos = region.getCenterPos();
			Biome biome = region.getBiome(chunkPos.getStartPos());
			ChunkRandom chunkRandom = new ChunkRandom();
			chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
			SpawnHelper.populateEntities(region, biome, chunkPos, chunkRandom);
		}
	}
}
