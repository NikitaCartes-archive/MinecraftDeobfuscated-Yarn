package net.minecraft.world.gen.chunk;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaTerrainParameters;
import net.minecraft.world.chunk.BelowZeroRetrogen;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class NoiseChunkGenerator extends ChunkGenerator {
	public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create(
		instance -> method_41042(instance)
				.and(
					instance.group(
						RegistryOps.createRegistryCodec(Registry.NOISE_WORLDGEN).forGetter(generator -> generator.noiseRegistry),
						BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.populationSource),
						Codec.LONG.fieldOf("seed").stable().forGetter(generator -> generator.seed),
						ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(generator -> generator.settings)
					)
				)
				.apply(instance, instance.stable(NoiseChunkGenerator::new))
	);
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private static final BlockState[] EMPTY = new BlockState[0];
	protected final BlockState defaultBlock;
	private final Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry;
	private final long seed;
	protected final RegistryEntry<ChunkGeneratorSettings> settings;
	private final NoiseRouter noiseRouter;
	private final MultiNoiseUtil.MultiNoiseSampler noiseColumnSampler;
	private final SurfaceBuilder surfaceBuilder;
	private final AquiferSampler.FluidLevelSampler fluidLevelSampler;

	public NoiseChunkGenerator(
		Registry<StructureSet> noiseRegistry,
		Registry<DoublePerlinNoiseSampler.NoiseParameters> structuresRegistry,
		BiomeSource biomeSource,
		long seed,
		RegistryEntry<ChunkGeneratorSettings> settings
	) {
		this(noiseRegistry, structuresRegistry, biomeSource, biomeSource, seed, settings);
	}

	private NoiseChunkGenerator(
		Registry<StructureSet> noiseRegistry,
		Registry<DoublePerlinNoiseSampler.NoiseParameters> structuresRegistry,
		BiomeSource populationSource,
		BiomeSource biomeSource,
		long seed,
		RegistryEntry<ChunkGeneratorSettings> settings
	) {
		super(noiseRegistry, Optional.empty(), populationSource, biomeSource, seed);
		this.noiseRegistry = structuresRegistry;
		this.seed = seed;
		this.settings = settings;
		ChunkGeneratorSettings chunkGeneratorSettings = this.settings.value();
		this.defaultBlock = chunkGeneratorSettings.defaultBlock();
		GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.generationShapeConfig();
		this.noiseRouter = chunkGeneratorSettings.method_41099(structuresRegistry, seed);
		this.noiseColumnSampler = new MultiNoiseUtil.MultiNoiseSampler(
			this.noiseRouter.temperature(),
			this.noiseRouter.humidity(),
			this.noiseRouter.continents(),
			this.noiseRouter.erosion(),
			this.noiseRouter.depth(),
			this.noiseRouter.ridges(),
			this.noiseRouter.spawnTarget()
		);
		AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(-54, Blocks.LAVA.getDefaultState());
		int i = chunkGeneratorSettings.seaLevel();
		AquiferSampler.FluidLevel fluidLevel2 = new AquiferSampler.FluidLevel(i, chunkGeneratorSettings.defaultFluid());
		AquiferSampler.FluidLevel fluidLevel3 = new AquiferSampler.FluidLevel(generationShapeConfig.minimumY() - 1, Blocks.AIR.getDefaultState());
		this.fluidLevelSampler = (x, y, z) -> y < Math.min(-54, i) ? fluidLevel : fluidLevel2;
		this.surfaceBuilder = new SurfaceBuilder(structuresRegistry, this.defaultBlock, i, seed, chunkGeneratorSettings.getRandomProvider());
	}

	@Override
	public CompletableFuture<Chunk> populateBiomes(
		Registry<Biome> biomeRegistry, Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk
	) {
		return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
			this.populateBiomes(blender, structureAccessor, chunk);
			return chunk;
		}), Util.getMainWorkerExecutor());
	}

	private void populateBiomes(Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			this.noiseRouter, () -> new StructureWeightSampler(structureAccessor, chunk), this.settings.value(), this.fluidLevelSampler, blender
		);
		BiomeSupplier biomeSupplier = BelowZeroRetrogen.getBiomeSupplier(blender.getBiomeSupplier(this.biomeSource), chunk);
		chunk.populateBiomes(biomeSupplier, chunkNoiseSampler.createMultiNoiseSampler(this.noiseRouter));
	}

	@Debug
	public NoiseRouter getNoiseRouter() {
		return this.noiseRouter;
	}

	@Override
	public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
		return this.noiseColumnSampler;
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new NoiseChunkGenerator(this.field_37053, this.noiseRegistry, this.populationSource.withSeed(seed), seed, this.settings);
	}

	public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settingsKey) {
		return this.seed == seed && this.settings.matchesKey(settingsKey);
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
		GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig();
		int i = Math.max(generationShapeConfig.minimumY(), world.getBottomY());
		int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), world.getTopY());
		int k = MathHelper.floorDiv(i, generationShapeConfig.verticalBlockSize());
		int l = MathHelper.floorDiv(j - i, generationShapeConfig.verticalBlockSize());
		return l <= 0 ? world.getBottomY() : this.sampleHeightmap(x, z, null, heightmap.getBlockPredicate(), k, l).orElse(world.getBottomY());
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig();
		int i = Math.max(generationShapeConfig.minimumY(), world.getBottomY());
		int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), world.getTopY());
		int k = MathHelper.floorDiv(i, generationShapeConfig.verticalBlockSize());
		int l = MathHelper.floorDiv(j - i, generationShapeConfig.verticalBlockSize());
		if (l <= 0) {
			return new VerticalBlockSample(i, EMPTY);
		} else {
			BlockState[] blockStates = new BlockState[l * generationShapeConfig.verticalBlockSize()];
			this.sampleHeightmap(x, z, blockStates, null, k, l);
			return new VerticalBlockSample(i, blockStates);
		}
	}

	@Override
	public void getDebugHudText(List<String> text, BlockPos pos) {
		DecimalFormat decimalFormat = new DecimalFormat("0.000");
		DensityFunction.UnblendedNoisePos unblendedNoisePos = new DensityFunction.UnblendedNoisePos(pos.getX(), pos.getY(), pos.getZ());
		double d = this.noiseRouter.ridges().sample(unblendedNoisePos);
		text.add(
			"NoiseRouter T: "
				+ decimalFormat.format(this.noiseRouter.temperature().sample(unblendedNoisePos))
				+ " H: "
				+ decimalFormat.format(this.noiseRouter.humidity().sample(unblendedNoisePos))
				+ " C: "
				+ decimalFormat.format(this.noiseRouter.continents().sample(unblendedNoisePos))
				+ " E: "
				+ decimalFormat.format(this.noiseRouter.erosion().sample(unblendedNoisePos))
				+ " D: "
				+ decimalFormat.format(this.noiseRouter.depth().sample(unblendedNoisePos))
				+ " W: "
				+ decimalFormat.format(d)
				+ " PV: "
				+ decimalFormat.format((double)VanillaTerrainParameters.getNormalizedWeirdness((float)d))
				+ " AS: "
				+ decimalFormat.format(this.noiseRouter.initialDensityWithoutJaggedness().sample(unblendedNoisePos))
				+ " N: "
				+ decimalFormat.format(this.noiseRouter.finalDensity().sample(unblendedNoisePos))
		);
	}

	private OptionalInt sampleHeightmap(int i, int j, @Nullable BlockState[] states, @Nullable Predicate<BlockState> predicate, int k, int l) {
		GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig();
		int m = generationShapeConfig.horizontalBlockSize();
		int n = generationShapeConfig.verticalBlockSize();
		int o = Math.floorDiv(i, m);
		int p = Math.floorDiv(j, m);
		int q = Math.floorMod(i, m);
		int r = Math.floorMod(j, m);
		int s = o * m;
		int t = p * m;
		double d = (double)q / (double)m;
		double e = (double)r / (double)m;
		ChunkNoiseSampler chunkNoiseSampler = ChunkNoiseSampler.create(s, t, k, l, this.noiseRouter, this.settings.value(), this.fluidLevelSampler);
		chunkNoiseSampler.sampleStartNoise();
		chunkNoiseSampler.sampleEndNoise(0);

		for (int u = l - 1; u >= 0; u--) {
			chunkNoiseSampler.sampleNoiseCorners(u, 0);

			for (int v = n - 1; v >= 0; v--) {
				int w = (k + u) * n + v;
				double f = (double)v / (double)n;
				chunkNoiseSampler.sampleNoiseY(w, f);
				chunkNoiseSampler.sampleNoiseX(i, d);
				chunkNoiseSampler.sampleNoise(j, e);
				BlockState blockState = chunkNoiseSampler.sampleBlockState();
				BlockState blockState2 = blockState == null ? this.defaultBlock : blockState;
				if (states != null) {
					int x = u * n + v;
					states[x] = blockState2;
				}

				if (predicate != null && predicate.test(blockState2)) {
					chunkNoiseSampler.method_40537();
					return OptionalInt.of(w + 1);
				}
			}
		}

		chunkNoiseSampler.method_40537();
		return OptionalInt.empty();
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
		if (!SharedConstants.method_37896(chunk.getPos())) {
			HeightContext heightContext = new HeightContext(this, region);
			ChunkGeneratorSettings chunkGeneratorSettings = this.settings.value();
			ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
				this.noiseRouter, () -> new StructureWeightSampler(structures, chunk), chunkGeneratorSettings, this.fluidLevelSampler, Blender.getBlender(region)
			);
			this.surfaceBuilder
				.buildSurface(
					region.getBiomeAccess(),
					region.getRegistryManager().get(Registry.BIOME_KEY),
					chunkGeneratorSettings.usesLegacyRandom(),
					heightContext,
					chunk,
					chunkNoiseSampler,
					chunkGeneratorSettings.surfaceRule()
				);
		}
	}

	@Override
	public void carve(
		ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep
	) {
		BiomeAccess biomeAccess2 = biomeAccess.withSource((x, y, z) -> this.populationSource.getBiome(x, y, z, this.getMultiNoiseSampler()));
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			this.noiseRouter, () -> new StructureWeightSampler(structureAccessor, chunk), this.settings.value(), this.fluidLevelSampler, Blender.getBlender(chunkRegion)
		);
		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
		CarverContext carverContext = new CarverContext(this, chunkRegion.getRegistryManager(), chunk.getHeightLimitView(), chunkNoiseSampler);
		CarvingMask carvingMask = ((ProtoChunk)chunk).getOrCreateCarvingMask(generationStep);

		for (int j = -8; j <= 8; j++) {
			for (int k = -8; k <= 8; k++) {
				ChunkPos chunkPos2 = new ChunkPos(chunkPos.x + j, chunkPos.z + k);
				Chunk chunk2 = chunkRegion.getChunk(chunkPos2.x, chunkPos2.z);
				GenerationSettings generationSettings = chunk2.setBiomeIfAbsent(
						() -> this.populationSource
								.getBiome(BiomeCoords.fromBlock(chunkPos2.getStartX()), 0, BiomeCoords.fromBlock(chunkPos2.getStartZ()), this.getMultiNoiseSampler())
					)
					.value()
					.getGenerationSettings();
				Iterable<RegistryEntry<ConfiguredCarver<?>>> iterable = generationSettings.getCarversForStep(generationStep);
				int l = 0;

				for (RegistryEntry<ConfiguredCarver<?>> registryEntry : iterable) {
					ConfiguredCarver<?> configuredCarver = registryEntry.value();
					chunkRandom.setCarverSeed(seed + (long)l, chunkPos2.x, chunkPos2.z);
					if (configuredCarver.shouldCarve(chunkRandom)) {
						configuredCarver.carve(carverContext, chunk, biomeAccess2::getBiome, chunkRandom, aquiferSampler, chunkPos2, carvingMask);
					}

					l++;
				}
			}
		}
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig();
		HeightLimitView heightLimitView = chunk.getHeightLimitView();
		int i = Math.max(generationShapeConfig.minimumY(), heightLimitView.getBottomY());
		int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), heightLimitView.getTopY());
		int k = MathHelper.floorDiv(i, generationShapeConfig.verticalBlockSize());
		int l = MathHelper.floorDiv(j - i, generationShapeConfig.verticalBlockSize());
		if (l <= 0) {
			return CompletableFuture.completedFuture(chunk);
		} else {
			int m = chunk.getSectionIndex(l * generationShapeConfig.verticalBlockSize() - 1 + i);
			int n = chunk.getSectionIndex(i);
			Set<ChunkSection> set = Sets.<ChunkSection>newHashSet();

			for (int o = m; o >= n; o--) {
				ChunkSection chunkSection = chunk.getSection(o);
				chunkSection.lock();
				set.add(chunkSection);
			}

			return CompletableFuture.supplyAsync(
					Util.debugSupplier("wgen_fill_noise", () -> this.populateNoise(blender, structureAccessor, chunk, k, l)), Util.getMainWorkerExecutor()
				)
				.whenCompleteAsync((chunkx, throwable) -> {
					for (ChunkSection chunkSectionx : set) {
						chunkSectionx.unlock();
					}
				}, executor);
		}
	}

	private Chunk populateNoise(Blender blender, StructureAccessor structureAccessor, Chunk chunk, int i, int j) {
		ChunkGeneratorSettings chunkGeneratorSettings = this.settings.value();
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			this.noiseRouter, () -> new StructureWeightSampler(structureAccessor, chunk), chunkGeneratorSettings, this.fluidLevelSampler, blender
		);
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		ChunkPos chunkPos = chunk.getPos();
		int k = chunkPos.getStartX();
		int l = chunkPos.getStartZ();
		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
		chunkNoiseSampler.sampleStartNoise();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.generationShapeConfig();
		int m = generationShapeConfig.horizontalBlockSize();
		int n = generationShapeConfig.verticalBlockSize();
		int o = 16 / m;
		int p = 16 / m;

		for (int q = 0; q < o; q++) {
			chunkNoiseSampler.sampleEndNoise(q);

			for (int r = 0; r < p; r++) {
				ChunkSection chunkSection = chunk.getSection(chunk.countVerticalSections() - 1);

				for (int s = j - 1; s >= 0; s--) {
					chunkNoiseSampler.sampleNoiseCorners(s, r);

					for (int t = n - 1; t >= 0; t--) {
						int u = (i + s) * n + t;
						int v = u & 15;
						int w = chunk.getSectionIndex(u);
						if (chunk.getSectionIndex(chunkSection.getYOffset()) != w) {
							chunkSection = chunk.getSection(w);
						}

						double d = (double)t / (double)n;
						chunkNoiseSampler.sampleNoiseY(u, d);

						for (int x = 0; x < m; x++) {
							int y = k + q * m + x;
							int z = y & 15;
							double e = (double)x / (double)m;
							chunkNoiseSampler.sampleNoiseX(y, e);

							for (int aa = 0; aa < m; aa++) {
								int ab = l + r * m + aa;
								int ac = ab & 15;
								double f = (double)aa / (double)m;
								chunkNoiseSampler.sampleNoise(ab, f);
								BlockState blockState = chunkNoiseSampler.sampleBlockState();
								if (blockState == null) {
									blockState = this.defaultBlock;
								}

								blockState = this.getBlockState(chunkNoiseSampler, y, u, ab, blockState);
								if (blockState != AIR && !SharedConstants.method_37896(chunk.getPos())) {
									if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
										mutable.set(y, u, ab);
										((ProtoChunk)chunk).addLightSource(mutable);
									}

									chunkSection.setBlockState(z, v, ac, blockState, false);
									heightmap.trackUpdate(z, u, ac, blockState);
									heightmap2.trackUpdate(z, u, ac, blockState);
									if (aquiferSampler.needsFluidTick() && !blockState.getFluidState().isEmpty()) {
										mutable.set(y, u, ab);
										chunk.markBlockForPostProcessing(mutable);
									}
								}
							}
						}
					}
				}
			}

			chunkNoiseSampler.swapBuffers();
		}

		chunkNoiseSampler.method_40537();
		return chunk;
	}

	private BlockState getBlockState(ChunkNoiseSampler chunkNoiseSampler, int x, int y, int z, BlockState state) {
		return state;
	}

	@Override
	public int getWorldHeight() {
		return this.settings.value().generationShapeConfig().height();
	}

	@Override
	public int getSeaLevel() {
		return this.settings.value().seaLevel();
	}

	@Override
	public int getMinimumY() {
		return this.settings.value().generationShapeConfig().minimumY();
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		if (!this.settings.value().mobGenerationDisabled()) {
			ChunkPos chunkPos = region.getCenterPos();
			RegistryEntry<Biome> registryEntry = region.getBiome(chunkPos.getStartPos().withY(region.getTopY() - 1));
			ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
			chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
			SpawnHelper.populateEntities(region, registryEntry, chunkPos, chunkRandom);
		}
	}

	@Deprecated
	public Optional<BlockState> applyMaterialRule(
		CarverContext context, Function<BlockPos, RegistryEntry<Biome>> posToBiome, Chunk chunk, ChunkNoiseSampler chunkNoiseSampler, BlockPos pos, boolean hasFluid
	) {
		return this.surfaceBuilder.applyMaterialRule(this.settings.value().surfaceRule(), context, posToBiome, chunk, chunkNoiseSampler, pos, hasFluid);
	}
}
