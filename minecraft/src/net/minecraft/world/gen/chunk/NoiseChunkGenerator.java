package net.minecraft.world.gen.chunk;

import com.google.common.annotations.VisibleForTesting;
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
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.AtomicSimpleRandom;
import net.minecraft.util.math.random.ChunkRandom;
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
import net.minecraft.world.chunk.BelowZeroRetrogen;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.random.RandomSeed;
import org.apache.commons.lang3.mutable.MutableObject;

public final class NoiseChunkGenerator extends ChunkGenerator {
	public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create(
		instance -> createStructureSetRegistryGetter(instance)
				.and(
					instance.group(
						RegistryOps.createRegistryCodec(Registry.NOISE_KEY).forGetter(generator -> generator.noiseRegistry),
						BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.populationSource),
						ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(generator -> generator.settings)
					)
				)
				.apply(instance, instance.stable(NoiseChunkGenerator::new))
	);
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	protected final BlockState defaultBlock;
	private final Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry;
	protected final RegistryEntry<ChunkGeneratorSettings> settings;
	private final AquiferSampler.FluidLevelSampler fluidLevelSampler;

	public NoiseChunkGenerator(
		Registry<StructureSet> structureSetRegistry,
		Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry,
		BiomeSource biomeSource,
		RegistryEntry<ChunkGeneratorSettings> settings
	) {
		this(structureSetRegistry, noiseRegistry, biomeSource, biomeSource, settings);
	}

	private NoiseChunkGenerator(
		Registry<StructureSet> structureSetRegistry,
		Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry,
		BiomeSource populationSource,
		BiomeSource biomeSource,
		RegistryEntry<ChunkGeneratorSettings> settings
	) {
		super(structureSetRegistry, Optional.empty(), populationSource, biomeSource);
		this.noiseRegistry = noiseRegistry;
		this.settings = settings;
		ChunkGeneratorSettings chunkGeneratorSettings = this.settings.value();
		this.defaultBlock = chunkGeneratorSettings.defaultBlock();
		AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(-54, Blocks.LAVA.getDefaultState());
		int i = chunkGeneratorSettings.seaLevel();
		AquiferSampler.FluidLevel fluidLevel2 = new AquiferSampler.FluidLevel(i, chunkGeneratorSettings.defaultFluid());
		AquiferSampler.FluidLevel fluidLevel3 = new AquiferSampler.FluidLevel(DimensionType.MIN_HEIGHT * 2, Blocks.AIR.getDefaultState());
		this.fluidLevelSampler = (x, y, z) -> y < Math.min(-54, i) ? fluidLevel : fluidLevel2;
	}

	@Override
	public CompletableFuture<Chunk> populateBiomes(
		Registry<Biome> biomeRegistry, Executor executor, NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk
	) {
		return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
			this.populateBiomes(blender, noiseConfig, structureAccessor, chunk);
			return chunk;
		}), Util.getMainWorkerExecutor());
	}

	private void populateBiomes(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(chunkx -> this.method_41537(chunkx, structureAccessor, blender, noiseConfig));
		BiomeSupplier biomeSupplier = BelowZeroRetrogen.getBiomeSupplier(blender.getBiomeSupplier(this.biomeSource), chunk);
		chunk.populateBiomes(biomeSupplier, chunkNoiseSampler.createMultiNoiseSampler(noiseConfig.getNoiseRouter(), this.settings.value().spawnTarget()));
	}

	private ChunkNoiseSampler method_41537(Chunk chunk, StructureAccessor structureAccessor, Blender blender, NoiseConfig noiseConfig) {
		return ChunkNoiseSampler.create(
			chunk, noiseConfig, StructureWeightSampler.method_42695(structureAccessor, chunk.getPos()), this.settings.value(), this.fluidLevelSampler, blender
		);
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	public RegistryEntry<ChunkGeneratorSettings> getSettings() {
		return this.settings;
	}

	public boolean matchesSettings(RegistryKey<ChunkGeneratorSettings> settings) {
		return this.settings.matchesKey(settings);
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
		return this.sampleHeightmap(world, noiseConfig, x, z, null, heightmap.getBlockPredicate()).orElse(world.getBottomY());
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
		MutableObject<VerticalBlockSample> mutableObject = new MutableObject<>();
		this.sampleHeightmap(world, noiseConfig, x, z, mutableObject, null);
		return mutableObject.getValue();
	}

	@Override
	public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
		DecimalFormat decimalFormat = new DecimalFormat("0.000");
		NoiseRouter noiseRouter = noiseConfig.getNoiseRouter();
		DensityFunction.UnblendedNoisePos unblendedNoisePos = new DensityFunction.UnblendedNoisePos(pos.getX(), pos.getY(), pos.getZ());
		double d = noiseRouter.ridges().sample(unblendedNoisePos);
		text.add(
			"NoiseRouter T: "
				+ decimalFormat.format(noiseRouter.temperature().sample(unblendedNoisePos))
				+ " V: "
				+ decimalFormat.format(noiseRouter.vegetation().sample(unblendedNoisePos))
				+ " C: "
				+ decimalFormat.format(noiseRouter.continents().sample(unblendedNoisePos))
				+ " E: "
				+ decimalFormat.format(noiseRouter.erosion().sample(unblendedNoisePos))
				+ " D: "
				+ decimalFormat.format(noiseRouter.depth().sample(unblendedNoisePos))
				+ " W: "
				+ decimalFormat.format(d)
				+ " PV: "
				+ decimalFormat.format((double)DensityFunctions.method_41546((float)d))
				+ " AS: "
				+ decimalFormat.format(noiseRouter.initialDensityWithoutJaggedness().sample(unblendedNoisePos))
				+ " N: "
				+ decimalFormat.format(noiseRouter.finalDensity().sample(unblendedNoisePos))
		);
	}

	private OptionalInt sampleHeightmap(
		HeightLimitView heightLimitView,
		NoiseConfig noiseConfig,
		int i,
		int j,
		@Nullable MutableObject<VerticalBlockSample> mutableObject,
		@Nullable Predicate<BlockState> predicate
	) {
		GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig().method_42368(heightLimitView);
		int k = generationShapeConfig.verticalBlockSize();
		int l = generationShapeConfig.minimumY();
		int m = MathHelper.floorDiv(l, k);
		int n = MathHelper.floorDiv(generationShapeConfig.height(), k);
		if (n <= 0) {
			return OptionalInt.empty();
		} else {
			BlockState[] blockStates;
			if (mutableObject == null) {
				blockStates = null;
			} else {
				blockStates = new BlockState[generationShapeConfig.height()];
				mutableObject.setValue(new VerticalBlockSample(l, blockStates));
			}

			int o = generationShapeConfig.horizontalBlockSize();
			int p = Math.floorDiv(i, o);
			int q = Math.floorDiv(j, o);
			int r = Math.floorMod(i, o);
			int s = Math.floorMod(j, o);
			int t = p * o;
			int u = q * o;
			double d = (double)r / (double)o;
			double e = (double)s / (double)o;
			ChunkNoiseSampler chunkNoiseSampler = new ChunkNoiseSampler(
				1,
				noiseConfig,
				t,
				u,
				generationShapeConfig,
				DensityFunctionTypes.Beardifier.INSTANCE,
				this.settings.value(),
				this.fluidLevelSampler,
				Blender.getNoBlending()
			);
			chunkNoiseSampler.sampleStartNoise();
			chunkNoiseSampler.sampleEndNoise(0);

			for (int v = n - 1; v >= 0; v--) {
				chunkNoiseSampler.sampleNoiseCorners(v, 0);

				for (int w = k - 1; w >= 0; w--) {
					int x = (m + v) * k + w;
					double f = (double)w / (double)k;
					chunkNoiseSampler.sampleNoiseY(x, f);
					chunkNoiseSampler.sampleNoiseX(i, d);
					chunkNoiseSampler.sampleNoise(j, e);
					BlockState blockState = chunkNoiseSampler.sampleBlockState();
					BlockState blockState2 = blockState == null ? this.defaultBlock : blockState;
					if (blockStates != null) {
						int y = v * k + w;
						blockStates[y] = blockState2;
					}

					if (predicate != null && predicate.test(blockState2)) {
						chunkNoiseSampler.method_40537();
						return OptionalInt.of(x + 1);
					}
				}
			}

			chunkNoiseSampler.method_40537();
			return OptionalInt.empty();
		}
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
		if (!SharedConstants.method_37896(chunk.getPos())) {
			HeightContext heightContext = new HeightContext(this, region);
			this.method_41538(
				chunk, heightContext, noiseConfig, structures, region.getBiomeAccess(), region.getRegistryManager().get(Registry.BIOME_KEY), Blender.getBlender(region)
			);
		}
	}

	@VisibleForTesting
	public void method_41538(
		Chunk chunk,
		HeightContext heightContext,
		NoiseConfig noiseConfig,
		StructureAccessor structureAccessor,
		BiomeAccess biomeAccess,
		Registry<Biome> registry,
		Blender blender
	) {
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(chunkx -> this.method_41537(chunkx, structureAccessor, blender, noiseConfig));
		ChunkGeneratorSettings chunkGeneratorSettings = this.settings.value();
		noiseConfig.getSurfaceBuilder()
			.buildSurface(
				noiseConfig,
				biomeAccess,
				registry,
				chunkGeneratorSettings.usesLegacyRandom(),
				heightContext,
				chunk,
				chunkNoiseSampler,
				chunkGeneratorSettings.surfaceRule()
			);
	}

	@Override
	public void carve(
		ChunkRegion chunkRegion,
		long seed,
		NoiseConfig noiseConfig,
		BiomeAccess world,
		StructureAccessor structureAccessor,
		Chunk chunk,
		GenerationStep.Carver carverStep
	) {
		BiomeAccess biomeAccess = world.withSource((ix, jx, kx) -> this.populationSource.getBiome(ix, jx, kx, noiseConfig.getMultiNoiseSampler()));
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			chunkx -> this.method_41537(chunkx, structureAccessor, Blender.getBlender(chunkRegion), noiseConfig)
		);
		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
		CarverContext carverContext = new CarverContext(
			this, chunkRegion.getRegistryManager(), chunk.getHeightLimitView(), chunkNoiseSampler, noiseConfig, this.settings.value().surfaceRule()
		);
		CarvingMask carvingMask = ((ProtoChunk)chunk).getOrCreateCarvingMask(carverStep);

		for (int j = -8; j <= 8; j++) {
			for (int k = -8; k <= 8; k++) {
				ChunkPos chunkPos2 = new ChunkPos(chunkPos.x + j, chunkPos.z + k);
				Chunk chunk2 = chunkRegion.getChunk(chunkPos2.x, chunkPos2.z);
				GenerationSettings generationSettings = chunk2.setBiomeIfAbsent(
						() -> this.populationSource
								.getBiome(BiomeCoords.fromBlock(chunkPos2.getStartX()), 0, BiomeCoords.fromBlock(chunkPos2.getStartZ()), noiseConfig.getMultiNoiseSampler())
					)
					.value()
					.getGenerationSettings();
				Iterable<RegistryEntry<ConfiguredCarver<?>>> iterable = generationSettings.getCarversForStep(carverStep);
				int l = 0;

				for (RegistryEntry<ConfiguredCarver<?>> registryEntry : iterable) {
					ConfiguredCarver<?> configuredCarver = registryEntry.value();
					chunkRandom.setCarverSeed(seed + (long)l, chunkPos2.x, chunkPos2.z);
					if (configuredCarver.shouldCarve(chunkRandom)) {
						configuredCarver.carve(carverContext, chunk, biomeAccess::getBiome, chunkRandom, aquiferSampler, chunkPos2, carvingMask);
					}

					l++;
				}
			}
		}
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
		GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig().method_42368(chunk.getHeightLimitView());
		int i = generationShapeConfig.minimumY();
		int j = MathHelper.floorDiv(i, generationShapeConfig.verticalBlockSize());
		int k = MathHelper.floorDiv(generationShapeConfig.height(), generationShapeConfig.verticalBlockSize());
		if (k <= 0) {
			return CompletableFuture.completedFuture(chunk);
		} else {
			int l = chunk.getSectionIndex(k * generationShapeConfig.verticalBlockSize() - 1 + i);
			int m = chunk.getSectionIndex(i);
			Set<ChunkSection> set = Sets.<ChunkSection>newHashSet();

			for (int n = l; n >= m; n--) {
				ChunkSection chunkSection = chunk.getSection(n);
				chunkSection.lock();
				set.add(chunkSection);
			}

			return CompletableFuture.supplyAsync(
					Util.debugSupplier("wgen_fill_noise", () -> this.populateNoise(blender, structureAccessor, noiseConfig, chunk, j, k)), Util.getMainWorkerExecutor()
				)
				.whenCompleteAsync((chunkx, throwable) -> {
					for (ChunkSection chunkSectionx : set) {
						chunkSectionx.unlock();
					}
				}, executor);
		}
	}

	private Chunk populateNoise(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int i, int j) {
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(chunkx -> this.method_41537(chunkx, structureAccessor, blender, noiseConfig));
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		ChunkPos chunkPos = chunk.getPos();
		int k = chunkPos.getStartX();
		int l = chunkPos.getStartZ();
		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
		chunkNoiseSampler.sampleStartNoise();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int m = chunkNoiseSampler.method_42361();
		int n = chunkNoiseSampler.method_42362();
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
}
