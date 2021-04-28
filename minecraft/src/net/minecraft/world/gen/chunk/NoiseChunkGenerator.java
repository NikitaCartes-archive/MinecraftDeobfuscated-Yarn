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
import net.minecraft.class_6350;
import net.minecraft.class_6357;
import net.minecraft.class_6358;
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
	private final int verticalNoiseResolution;
	private final int horizontalNoiseResolution;
	private final int noiseSizeX;
	private final int noiseSizeY;
	private final int noiseSizeZ;
	private final NoiseSampler surfaceDepthNoise;
	private final DoublePerlinNoiseSampler edgeDensityNoise;
	private final DoublePerlinNoiseSampler waterLevelNoise;
	private final DoublePerlinNoiseSampler field_33578;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;
	private final long seed;
	protected final Supplier<ChunkGeneratorSettings> settings;
	private final int worldHeight;
	private final NoiseColumnSampler noiseColumnSampler;
	private final BlockSource deepslateSource;
	private final OreVeinGenerator oreVeinGenerator;
	private final class_6358 field_33644;

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
		this.waterLevelNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(chunkRandom.nextLong()), -3, 1.0, 0.0, 2.0);
		this.field_33578 = DoublePerlinNoiseSampler.create(new SimpleRandom(chunkRandom.nextLong()), -1, 1.0, 0.0);
		class_6357 lv;
		if (chunkGeneratorSettings.hasNoiseCaves()) {
			lv = new NoiseCaveSampler(chunkRandom, generationShapeConfig.getMinimumY() / this.verticalNoiseResolution);
		} else {
			lv = class_6357.field_33652;
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
			lv
		);
		this.deepslateSource = new DeepslateBlockSource(seed, this.defaultBlock, Blocks.DEEPSLATE.getDefaultState(), chunkGeneratorSettings);
		this.oreVeinGenerator = new OreVeinGenerator(
			seed, this.defaultBlock, this.horizontalNoiseResolution, this.verticalNoiseResolution, chunkGeneratorSettings.getGenerationShapeConfig().getMinimumY()
		);
		this.field_33644 = new class_6358(seed);
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
		class_6350 lv = this.method_36386(minY, noiseSizeY, new ChunkPos(i, j));

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
				BlockState blockState = this.getBlockState(StructureWeightSampler.INSTANCE, lv, this.deepslateSource, class_6357.field_33652, x, aa, z, w);
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

	private class_6350 method_36386(int i, int j, ChunkPos chunkPos) {
		return !this.hasAquifers()
			? class_6350.method_36381(this.getSeaLevel(), this.defaultFluid)
			: class_6350.method_36382(
				chunkPos,
				this.edgeDensityNoise,
				this.waterLevelNoise,
				this.field_33578,
				(ChunkGeneratorSettings)this.settings.get(),
				this.noiseColumnSampler,
				i * this.verticalNoiseResolution,
				j * this.verticalNoiseResolution
			);
	}

	protected BlockState getBlockState(
		StructureWeightSampler structures, class_6350 aquiferSampler, BlockSource blockInterpolator, class_6357 arg, int i, int j, int k, double d
	) {
		double e = MathHelper.clamp(d / 200.0, -1.0, 1.0);
		e = e / 2.0 - e * e * e / 24.0;
		e = arg.sample(e, i, j, k);
		e += structures.getWeight(i, j, k);
		return aquiferSampler.apply(blockInterpolator, i, j, k, e);
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
				int r = ((ChunkGeneratorSettings)this.settings.get()).getMinSurfaceLevel();
				region.getBiome(mutable.set(k + m, q, l + n))
					.buildSurface(chunkRandom, chunk, o, p, q, e, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), r, region.getSeed());
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

	private Chunk populateNoise(StructureAccessor accessor, Chunk chunk, int i, int j) {
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		ChunkPos chunkPos = chunk.getPos();
		int k = chunkPos.getStartX();
		int l = chunkPos.getStartZ();
		StructureWeightSampler structureWeightSampler = new StructureWeightSampler(accessor, chunk);
		class_6350 lv = this.method_36386(i, j, chunkPos);
		NoiseInterpolator noiseInterpolator = new NoiseInterpolator(this.noiseSizeX, j, this.noiseSizeZ, chunkPos, i, this::sampleNoiseColumn);
		List<NoiseInterpolator> list = Lists.<NoiseInterpolator>newArrayList(noiseInterpolator);
		Consumer<NoiseInterpolator> consumer = list::add;
		DoubleFunction<BlockSource> doubleFunction = this.method_36387(i, chunkPos, consumer);
		DoubleFunction<class_6357> doubleFunction2 = this.method_36462(i, chunkPos, consumer);
		list.forEach(NoiseInterpolator::sampleStartNoise);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = 0; m < this.noiseSizeX; m++) {
			int n = m;
			list.forEach(noiseInterpolatorx -> noiseInterpolatorx.sampleEndNoise(n));

			for (int o = 0; o < this.noiseSizeZ; o++) {
				ChunkSection chunkSection = chunk.getSection(chunk.countVerticalSections() - 1);

				for (int p = j - 1; p >= 0; p--) {
					int q = o;
					int r = p;
					list.forEach(noiseInterpolatorx -> noiseInterpolatorx.sampleNoiseCorners(r, q));

					for (int s = this.verticalNoiseResolution - 1; s >= 0; s--) {
						int t = (i + p) * this.verticalNoiseResolution + s;
						int u = t & 15;
						int v = chunk.getSectionIndex(t);
						if (chunk.getSectionIndex(chunkSection.getYOffset()) != v) {
							chunkSection = chunk.getSection(v);
						}

						double d = (double)s / (double)this.verticalNoiseResolution;
						list.forEach(noiseInterpolatorx -> noiseInterpolatorx.sampleNoiseY(d));

						for (int w = 0; w < this.horizontalNoiseResolution; w++) {
							int x = k + m * this.horizontalNoiseResolution + w;
							int y = x & 15;
							double e = (double)w / (double)this.horizontalNoiseResolution;
							list.forEach(noiseInterpolatorx -> noiseInterpolatorx.sampleNoiseX(e));

							for (int z = 0; z < this.horizontalNoiseResolution; z++) {
								int aa = l + o * this.horizontalNoiseResolution + z;
								int ab = aa & 15;
								double f = (double)z / (double)this.horizontalNoiseResolution;
								double g = noiseInterpolator.sampleNoise(f);
								BlockState blockState = this.getBlockState(
									structureWeightSampler, lv, (BlockSource)doubleFunction.apply(f), (class_6357)doubleFunction2.apply(f), x, t, aa, g
								);
								if (blockState != AIR) {
									if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
										mutable.set(x, t, aa);
										((ProtoChunk)chunk).addLightSource(mutable);
									}

									chunkSection.setBlockState(y, u, ab, blockState, false);
									heightmap.trackUpdate(y, t, ab, blockState);
									heightmap2.trackUpdate(y, t, ab, blockState);
									if (lv.needsFluidTick() && !blockState.getFluidState().isEmpty()) {
										mutable.set(x, t, aa);
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

	private DoubleFunction<class_6357> method_36462(int i, ChunkPos chunkPos, Consumer<NoiseInterpolator> consumer) {
		if (!((ChunkGeneratorSettings)this.settings.get()).method_36468()) {
			return d -> class_6357.field_33652;
		} else {
			NoiseChunkGenerator.class_6356 lv = new NoiseChunkGenerator.class_6356(chunkPos, i);
			lv.method_36467(consumer);
			return lv::method_36466;
		}
	}

	private DoubleFunction<BlockSource> method_36387(int i, ChunkPos chunkPos, Consumer<NoiseInterpolator> consumer) {
		if (!((ChunkGeneratorSettings)this.settings.get()).method_36396()) {
			return d -> this.deepslateSource;
		} else {
			NoiseChunkGenerator.OreVeinSource oreVeinSource = new NoiseChunkGenerator.OreVeinSource(chunkPos, i, this.seed + 1L);
			oreVeinSource.method_36395(consumer);
			BlockSource blockSource = (ix, j, k) -> {
				BlockState blockState = oreVeinSource.sample(ix, j, k);
				return blockState != this.defaultBlock ? blockState : this.deepslateSource.sample(ix, j, k);
			};
			return d -> {
				oreVeinSource.method_36394(d);
				return blockSource;
			};
		}
	}

	@Override
	protected class_6350 method_36380(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY(), chunk.getBottomY());
		int j = MathHelper.floorDiv(i, this.verticalNoiseResolution);
		return this.method_36386(j, this.noiseSizeY, chunkPos);
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

	class OreVeinSource implements BlockSource {
		private final NoiseInterpolator field_33581;
		private final NoiseInterpolator field_33582;
		private final NoiseInterpolator field_33583;
		private double field_33584;
		private final long seed;
		private final ChunkRandom random = new ChunkRandom();

		public OreVeinSource(ChunkPos pos, int i, long seed) {
			this.field_33581 = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				pos,
				i,
				NoiseChunkGenerator.this.oreVeinGenerator::method_36401
			);
			this.field_33582 = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				pos,
				i,
				NoiseChunkGenerator.this.oreVeinGenerator::method_36404
			);
			this.field_33583 = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				pos,
				i,
				NoiseChunkGenerator.this.oreVeinGenerator::method_36405
			);
			this.seed = seed;
		}

		public void method_36395(Consumer<NoiseInterpolator> consumer) {
			consumer.accept(this.field_33581);
			consumer.accept(this.field_33582);
			consumer.accept(this.field_33583);
		}

		public void method_36394(double d) {
			this.field_33584 = d;
		}

		@Override
		public BlockState sample(int x, int y, int z) {
			double d = this.field_33581.sampleNoise(this.field_33584);
			double e = this.field_33582.sampleNoise(this.field_33584);
			double f = this.field_33583.sampleNoise(this.field_33584);
			this.random.setGrimstoneSeed(this.seed, x, y, z);
			return NoiseChunkGenerator.this.oreVeinGenerator.sample(this.random, x, y, z, d, e, f);
		}
	}

	class class_6356 implements class_6357 {
		private final NoiseInterpolator field_33646;
		private final NoiseInterpolator field_33647;
		private final NoiseInterpolator field_33648;
		private final NoiseInterpolator field_33649;
		private double field_33650;

		public class_6356(ChunkPos chunkPos, int i) {
			this.field_33646 = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				chunkPos,
				i,
				NoiseChunkGenerator.this.field_33644::method_36471
			);
			this.field_33647 = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				chunkPos,
				i,
				NoiseChunkGenerator.this.field_33644::method_36474
			);
			this.field_33648 = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				chunkPos,
				i,
				NoiseChunkGenerator.this.field_33644::method_36475
			);
			this.field_33649 = new NoiseInterpolator(
				NoiseChunkGenerator.this.noiseSizeX,
				NoiseChunkGenerator.this.noiseSizeY,
				NoiseChunkGenerator.this.noiseSizeZ,
				chunkPos,
				i,
				NoiseChunkGenerator.this.field_33644::method_36476
			);
		}

		public class_6357 method_36466(double d) {
			this.field_33650 = d;
			return this;
		}

		@Override
		public double sample(double d, int i, int j, int k) {
			double e = this.field_33646.sampleNoise(this.field_33650);
			double f = this.field_33647.sampleNoise(this.field_33650);
			double g = this.field_33648.sampleNoise(this.field_33650);
			double h = this.field_33649.sampleNoise(this.field_33650);
			return NoiseChunkGenerator.this.field_33644.method_36470(d, i, j, k, e, f, g, h, NoiseChunkGenerator.this.getMinimumY());
		}

		public void method_36467(Consumer<NoiseInterpolator> consumer) {
			consumer.accept(this.field_33646);
			consumer.accept(this.field_33647);
			consumer.accept(this.field_33648);
			consumer.accept(this.field_33649);
		}
	}
}
