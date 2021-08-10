package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
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
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.DeepslateBlockSource;
import net.minecraft.world.gen.NoiseCaveSampler;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.NoiseInterpolator;
import net.minecraft.world.gen.OreVeinGenerator;
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
	private static final int field_34238 = 16;
	private final int verticalNoiseResolution;
	private final int horizontalNoiseResolution;
	final int noiseSizeX;
	final int noiseSizeY;
	final int noiseSizeZ;
	private final NoiseSampler surfaceDepthNoise;
	private final DoublePerlinNoiseSampler edgeDensityNoise;
	private final DoublePerlinNoiseSampler fluidLevelNoise;
	private final DoublePerlinNoiseSampler fluidTypeNoise;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;
	private final long seed;
	protected final Supplier<ChunkGeneratorSettings> settings;
	private final int worldHeight;
	private final NoiseColumnSampler noiseColumnSampler;
	private final BlockSource deepslateSource;
	final OreVeinGenerator oreVeinGenerator;
	final NoodleCavesGenerator noodleCavesGenerator;

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
		InterpolatedNoiseSampler interpolatedNoiseSampler = new InterpolatedNoiseSampler(chunkRandom);
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

		this.edgeDensityNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(chunkRandom.nextLong()), -3, 1.0);
		this.fluidLevelNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(chunkRandom.nextLong()), -3, 0.2, 2.0, 1.0);
		this.fluidTypeNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(chunkRandom.nextLong()), -1, 1.0, 0.0);
		WeightSampler weightSampler;
		if (chunkGeneratorSettings.hasNoiseCaves()) {
			weightSampler = new NoiseCaveSampler(chunkRandom, generationShapeConfig.getMinimumY() / this.verticalNoiseResolution);
		} else {
			weightSampler = WeightSampler.DEFAULT;
		}

		this.noiseColumnSampler = new NoiseColumnSampler(
			populationSource,
			this.horizontalNoiseResolution,
			this.verticalNoiseResolution,
			this.noiseSizeY,
			generationShapeConfig,
			interpolatedNoiseSampler,
			simplexNoiseSampler,
			octavePerlinNoiseSampler,
			weightSampler
		);
		this.deepslateSource = new DeepslateBlockSource(seed, this.defaultBlock, Blocks.DEEPSLATE.getDefaultState(), chunkGeneratorSettings);
		this.oreVeinGenerator = new OreVeinGenerator(
			seed, this.defaultBlock, this.horizontalNoiseResolution, this.verticalNoiseResolution, chunkGeneratorSettings.getGenerationShapeConfig().getMinimumY()
		);
		this.noodleCavesGenerator = new NoodleCavesGenerator(seed);
	}

	private boolean hasAquifers() {
		return ((ChunkGeneratorSettings)this.settings.get()).hasAquifers();
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new NoiseChunkGenerator(this.populationSource.withSeed(seed), seed, this.settings);
	}

	public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settingsKey) {
		return this.seed == seed && ((ChunkGeneratorSettings)this.settings.get()).equals(settingsKey);
	}

	private double[] sampleNoiseColumn(int x, int z, int minY, int noiseSizeY) {
		double[] ds = new double[noiseSizeY + 1];
		this.sampleNoiseColumn(ds, x, z, minY, noiseSizeY);
		return ds;
	}

	private void sampleNoiseColumn(double[] buffer, int x, int z, int minY, int noiseSizeY) {
		GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();
		this.noiseColumnSampler.sampleNoiseColumn(buffer, x, z, generationShapeConfig, this.getSeaLevel(), minY, noiseSizeY);
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

	@Override
	public BlockSource getBlockSource() {
		return this.deepslateSource;
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
		AquiferSampler aquiferSampler = this.createBlockSampler(minY, noiseSizeY, new ChunkPos(i, j));

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
				BlockState blockState = this.getBlockState(StructureWeightSampler.INSTANCE, aquiferSampler, this.deepslateSource, WeightSampler.DEFAULT, x, aa, z, w);
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

	private AquiferSampler createBlockSampler(int startY, int deltaY, ChunkPos pos) {
		return !this.hasAquifers()
			? AquiferSampler.seaLevel(this.getSeaLevel(), this.defaultFluid)
			: AquiferSampler.aquifer(
				pos,
				this.edgeDensityNoise,
				this.fluidLevelNoise,
				this.fluidTypeNoise,
				(ChunkGeneratorSettings)this.settings.get(),
				this.noiseColumnSampler,
				startY * this.verticalNoiseResolution,
				deltaY * this.verticalNoiseResolution
			);
	}

	@Override
	protected int method_37828(ChunkPos chunkPos) {
		return this.noiseColumnSampler.method_37766(chunkPos.getCenterX(), chunkPos.getCenterZ());
	}

	protected BlockState getBlockState(
		StructureWeightSampler structures, AquiferSampler aquiferSampler, BlockSource blockInterpolator, WeightSampler weightSampler, int i, int j, int k, double d
	) {
		double e = MathHelper.clamp(d / 200.0, -1.0, 1.0);
		e = e / 2.0 - e * e * e / 24.0;
		e = weightSampler.sample(e, i, j, k);
		e += structures.getWeight(i, j, k);
		return aquiferSampler.apply(blockInterpolator, i, j, k, e);
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		if (!SharedConstants.method_37481(chunkPos.getStartX(), chunkPos.getStartZ())) {
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
					mutable.set(k + m, -64, l + n);
					int r = this.noiseColumnSampler.method_37766(mutable.getX(), mutable.getZ());
					int s = r - 16;
					Biome biome = region.getBiome(mutable.setY(q));
					biome.buildSurface(chunkRandom, chunk, o, p, q, e, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), s, region.getSeed());
				}
			}

			this.buildBedrock(chunk, chunkRandom);
		}
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
			return CompletableFuture.supplyAsync(() -> {
				Set<ChunkSection> set = Sets.<ChunkSection>newHashSet();

				Chunk var16;
				try {
					for (int mx = m; mx >= n; mx--) {
						ChunkSection chunkSection = chunk.getSection(mx);
						chunkSection.lock();
						set.add(chunkSection);
					}

					var16 = this.populateNoise(accessor, chunk, k, l);
				} finally {
					for (ChunkSection chunkSection3 : set) {
						chunkSection3.unlock();
					}
				}

				return var16;
			}, Util.getMainWorkerExecutor());
		}
	}

	private Chunk populateNoise(StructureAccessor accessor, Chunk chunk, int startY, int noiseSizeY) {
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		StructureWeightSampler structureWeightSampler = new StructureWeightSampler(accessor, chunk);
		AquiferSampler aquiferSampler = this.createBlockSampler(startY, noiseSizeY, chunkPos);
		NoiseInterpolator noiseInterpolator = new NoiseInterpolator(this.noiseSizeX, noiseSizeY, this.noiseSizeZ, chunkPos, startY, this::sampleNoiseColumn);
		List<NoiseInterpolator> list = Lists.<NoiseInterpolator>newArrayList(noiseInterpolator);
		Consumer<NoiseInterpolator> consumer = list::add;
		DoubleFunction<BlockSource> doubleFunction = this.createBlockSourceFactory(startY, chunkPos, consumer);
		DoubleFunction<WeightSampler> doubleFunction2 = this.createWeightSamplerFactory(startY, chunkPos, consumer);
		list.forEach(NoiseInterpolator::sampleStartNoise);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = 0; k < this.noiseSizeX; k++) {
			int l = k;
			list.forEach(noiseInterpolatorx -> noiseInterpolatorx.sampleEndNoise(l));

			for (int m = 0; m < this.noiseSizeZ; m++) {
				ChunkSection chunkSection = chunk.getSection(chunk.countVerticalSections() - 1);

				for (int n = noiseSizeY - 1; n >= 0; n--) {
					int o = m;
					int p = n;
					list.forEach(noiseInterpolatorx -> noiseInterpolatorx.sampleNoiseCorners(p, o));

					for (int q = this.verticalNoiseResolution - 1; q >= 0; q--) {
						int r = (startY + n) * this.verticalNoiseResolution + q;
						int s = r & 15;
						int t = chunk.getSectionIndex(r);
						if (chunk.getSectionIndex(chunkSection.getYOffset()) != t) {
							chunkSection = chunk.getSection(t);
						}

						double d = (double)q / (double)this.verticalNoiseResolution;
						list.forEach(noiseInterpolatorx -> noiseInterpolatorx.sampleNoiseY(d));

						for (int u = 0; u < this.horizontalNoiseResolution; u++) {
							int v = i + k * this.horizontalNoiseResolution + u;
							int w = v & 15;
							double e = (double)u / (double)this.horizontalNoiseResolution;
							list.forEach(noiseInterpolatorx -> noiseInterpolatorx.sampleNoiseX(e));

							for (int x = 0; x < this.horizontalNoiseResolution; x++) {
								int y = j + m * this.horizontalNoiseResolution + x;
								int z = y & 15;
								double f = (double)x / (double)this.horizontalNoiseResolution;
								double g = noiseInterpolator.sampleNoise(f);
								BlockState blockState = this.getBlockState(
									structureWeightSampler, aquiferSampler, (BlockSource)doubleFunction.apply(f), (WeightSampler)doubleFunction2.apply(f), v, r, y, g
								);
								if (blockState != AIR && !SharedConstants.method_37481(v, y)) {
									if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
										mutable.set(v, r, y);
										((ProtoChunk)chunk).addLightSource(mutable);
									}

									chunkSection.setBlockState(w, s, z, blockState, false);
									heightmap.trackUpdate(w, r, z, blockState);
									heightmap2.trackUpdate(w, r, z, blockState);
									if (aquiferSampler.needsFluidTick() && !blockState.getFluidState().isEmpty()) {
										mutable.set(v, r, y);
										chunk.getFluidTickScheduler().schedule(mutable, blockState.getFluidState().getFluid(), 0);
									}
								}
							}
						}
					}
				}
			}

			list.forEach(NoiseInterpolator::swapBuffers);
		}

		return chunk;
	}

	private DoubleFunction<WeightSampler> createWeightSamplerFactory(int minY, ChunkPos pos, Consumer<NoiseInterpolator> consumer) {
		if (!((ChunkGeneratorSettings)this.settings.get()).hasNoodleCaves()) {
			return d -> WeightSampler.DEFAULT;
		} else {
			NoiseChunkGenerator.NoodleCavesSampler noodleCavesSampler = new NoiseChunkGenerator.NoodleCavesSampler(pos, minY);
			noodleCavesSampler.feed(consumer);
			return noodleCavesSampler::setDeltaZ;
		}
	}

	private DoubleFunction<BlockSource> createBlockSourceFactory(int minY, ChunkPos pos, Consumer<NoiseInterpolator> consumer) {
		if (!((ChunkGeneratorSettings)this.settings.get()).hasOreVeins()) {
			return d -> this.deepslateSource;
		} else {
			NoiseChunkGenerator.OreVeinSource oreVeinSource = new NoiseChunkGenerator.OreVeinSource(pos, minY, this.seed + 1L);
			oreVeinSource.feed(consumer);
			BlockSource blockSource = (i, j, k) -> {
				BlockState blockState = oreVeinSource.sample(i, j, k);
				return blockState != this.defaultBlock ? blockState : this.deepslateSource.sample(i, j, k);
			};
			return deltaZ -> {
				oreVeinSource.setDeltaZ(deltaZ);
				return blockSource;
			};
		}
	}

	@Override
	protected AquiferSampler createAquiferSampler(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY(), chunk.getBottomY());
		int j = MathHelper.floorDiv(i, this.verticalNoiseResolution);
		return this.createBlockSampler(j, this.noiseSizeY, chunkPos);
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
	public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
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

		return group == SpawnGroup.UNDERGROUND_WATER_CREATURE && accessor.getStructureAt(pos, false, StructureFeature.MONUMENT).hasChildren()
			? StructureFeature.MONUMENT.getUndergroundWaterCreatureSpawns()
			: super.getEntitySpawnList(biome, accessor, group, pos);
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

	class NoodleCavesSampler implements WeightSampler {
		private final NoiseInterpolator frequencyNoiseInterpolator;
		private final NoiseInterpolator weightReducingNoiseInterpolator;
		private final NoiseInterpolator firstWeightNoiseinterpolator;
		private final NoiseInterpolator secondWeightNoiseInterpolator;
		private double deltaZ;

		public NoodleCavesSampler(ChunkPos pos, int minY) {
			this.frequencyNoiseInterpolator = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				pos,
				minY,
				NoiseChunkGenerator.this.noodleCavesGenerator::sampleFrequencyNoise
			);
			this.weightReducingNoiseInterpolator = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				pos,
				minY,
				NoiseChunkGenerator.this.noodleCavesGenerator::sampleWeightReducingNoise
			);
			this.firstWeightNoiseinterpolator = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				pos,
				minY,
				NoiseChunkGenerator.this.noodleCavesGenerator::sampleFirstWeightNoise
			);
			this.secondWeightNoiseInterpolator = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				pos,
				minY,
				NoiseChunkGenerator.this.noodleCavesGenerator::sampleSecondWeightNoise
			);
		}

		public WeightSampler setDeltaZ(double deltaZ) {
			this.deltaZ = deltaZ;
			return this;
		}

		@Override
		public double sample(double weight, int x, int y, int z) {
			double d = this.frequencyNoiseInterpolator.sampleNoise(this.deltaZ);
			double e = this.weightReducingNoiseInterpolator.sampleNoise(this.deltaZ);
			double f = this.firstWeightNoiseinterpolator.sampleNoise(this.deltaZ);
			double g = this.secondWeightNoiseInterpolator.sampleNoise(this.deltaZ);
			return NoiseChunkGenerator.this.noodleCavesGenerator.sampleWeight(weight, x, y, z, d, e, f, g, NoiseChunkGenerator.this.getMinimumY());
		}

		public void feed(Consumer<NoiseInterpolator> f) {
			f.accept(this.frequencyNoiseInterpolator);
			f.accept(this.weightReducingNoiseInterpolator);
			f.accept(this.firstWeightNoiseinterpolator);
			f.accept(this.secondWeightNoiseInterpolator);
		}
	}

	class OreVeinSource implements BlockSource {
		private final NoiseInterpolator oreFrequencyNoiseInterpolator;
		private final NoiseInterpolator firstOrePlacementNoiseInterpolator;
		private final NoiseInterpolator secondOrePlacementNoiseInterpolator;
		private double deltaZ;
		private final long seed;
		private final ChunkRandom random = new ChunkRandom();

		public OreVeinSource(ChunkPos pos, int minY, long seed) {
			this.oreFrequencyNoiseInterpolator = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				pos,
				minY,
				NoiseChunkGenerator.this.oreVeinGenerator::sampleOreFrequencyNoise
			);
			this.firstOrePlacementNoiseInterpolator = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				pos,
				minY,
				NoiseChunkGenerator.this.oreVeinGenerator::sampleFirstOrePlacementNoise
			);
			this.secondOrePlacementNoiseInterpolator = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				pos,
				minY,
				NoiseChunkGenerator.this.oreVeinGenerator::sampleSecondOrePlacementNoise
			);
			this.seed = seed;
		}

		public void feed(Consumer<NoiseInterpolator> f) {
			f.accept(this.oreFrequencyNoiseInterpolator);
			f.accept(this.firstOrePlacementNoiseInterpolator);
			f.accept(this.secondOrePlacementNoiseInterpolator);
		}

		public void setDeltaZ(double deltaZ) {
			this.deltaZ = deltaZ;
		}

		@Override
		public BlockState sample(int x, int y, int z) {
			double d = this.oreFrequencyNoiseInterpolator.sampleNoise(this.deltaZ);
			double e = this.firstOrePlacementNoiseInterpolator.sampleNoise(this.deltaZ);
			double f = this.secondOrePlacementNoiseInterpolator.sampleNoise(this.deltaZ);
			this.random.setDeepslateSeed(this.seed, x, y, z);
			return NoiseChunkGenerator.this.oreVeinGenerator.sample(this.random, x, y, z, d, e, f);
		}
	}
}
