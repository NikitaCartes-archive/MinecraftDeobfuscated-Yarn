package net.minecraft.world.gen.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.RandomSeed;
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
import org.apache.commons.lang3.mutable.MutableObject;

public final class NoiseChunkGenerator extends ChunkGenerator {
	public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource),
					ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(generator -> generator.settings)
				)
				.apply(instance, instance.stable(NoiseChunkGenerator::new))
	);
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private final RegistryEntry<ChunkGeneratorSettings> settings;
	private final Supplier<AquiferSampler.FluidLevelSampler> fluidLevelSampler;

	public NoiseChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
		super(biomeSource);
		this.settings = settings;
		this.fluidLevelSampler = Suppliers.memoize(() -> createFluidLevelSampler(settings.value()));
	}

	private static AquiferSampler.FluidLevelSampler createFluidLevelSampler(ChunkGeneratorSettings settings) {
		AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(-54, Blocks.LAVA.getDefaultState());
		int i = settings.seaLevel();
		AquiferSampler.FluidLevel fluidLevel2 = new AquiferSampler.FluidLevel(i, settings.defaultFluid());
		AquiferSampler.FluidLevel fluidLevel3 = new AquiferSampler.FluidLevel(DimensionType.MIN_HEIGHT * 2, Blocks.AIR.getDefaultState());
		return (x, y, z) -> y < Math.min(-54, i) ? fluidLevel : fluidLevel2;
	}

	@Override
	public CompletableFuture<Chunk> populateBiomes(Executor executor, NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
			this.populateBiomes(blender, noiseConfig, structureAccessor, chunk);
			return chunk;
		}), Util.getMainWorkerExecutor());
	}

	private void populateBiomes(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			chunkx -> this.createChunkNoiseSampler(chunkx, structureAccessor, blender, noiseConfig)
		);
		BiomeSupplier biomeSupplier = BelowZeroRetrogen.getBiomeSupplier(blender.getBiomeSupplier(this.biomeSource), chunk);
		chunk.populateBiomes(biomeSupplier, chunkNoiseSampler.createMultiNoiseSampler(noiseConfig.getNoiseRouter(), this.settings.value().spawnTarget()));
	}

	private ChunkNoiseSampler createChunkNoiseSampler(Chunk chunk, StructureAccessor world, Blender blender, NoiseConfig noiseConfig) {
		return ChunkNoiseSampler.create(
			chunk,
			noiseConfig,
			StructureWeightSampler.createStructureWeightSampler(world, chunk.getPos()),
			this.settings.value(),
			(AquiferSampler.FluidLevelSampler)this.fluidLevelSampler.get(),
			blender
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
				+ decimalFormat.format((double)DensityFunctions.getPeaksValleysNoise((float)d))
				+ " AS: "
				+ decimalFormat.format(noiseRouter.initialDensityWithoutJaggedness().sample(unblendedNoisePos))
				+ " N: "
				+ decimalFormat.format(noiseRouter.finalDensity().sample(unblendedNoisePos))
		);
	}

	private OptionalInt sampleHeightmap(
		HeightLimitView world,
		NoiseConfig noiseConfig,
		int x,
		int z,
		@Nullable MutableObject<VerticalBlockSample> columnSample,
		@Nullable Predicate<BlockState> stopPredicate
	) {
		GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig().trimHeight(world);
		int i = generationShapeConfig.verticalBlockSize();
		int j = generationShapeConfig.minimumY();
		int k = MathHelper.floorDiv(j, i);
		int l = MathHelper.floorDiv(generationShapeConfig.height(), i);
		if (l <= 0) {
			return OptionalInt.empty();
		} else {
			BlockState[] blockStates;
			if (columnSample == null) {
				blockStates = null;
			} else {
				blockStates = new BlockState[generationShapeConfig.height()];
				columnSample.setValue(new VerticalBlockSample(j, blockStates));
			}

			int m = generationShapeConfig.horizontalBlockSize();
			int n = Math.floorDiv(x, m);
			int o = Math.floorDiv(z, m);
			int p = Math.floorMod(x, m);
			int q = Math.floorMod(z, m);
			int r = n * m;
			int s = o * m;
			double d = (double)p / (double)m;
			double e = (double)q / (double)m;
			ChunkNoiseSampler chunkNoiseSampler = new ChunkNoiseSampler(
				1,
				noiseConfig,
				r,
				s,
				generationShapeConfig,
				DensityFunctionTypes.Beardifier.INSTANCE,
				this.settings.value(),
				(AquiferSampler.FluidLevelSampler)this.fluidLevelSampler.get(),
				Blender.getNoBlending()
			);
			chunkNoiseSampler.sampleStartNoise();
			chunkNoiseSampler.sampleEndNoise(0);

			for (int t = l - 1; t >= 0; t--) {
				chunkNoiseSampler.sampleNoiseCorners(t, 0);

				for (int u = i - 1; u >= 0; u--) {
					int v = (k + t) * i + u;
					double f = (double)u / (double)i;
					chunkNoiseSampler.sampleNoiseY(v, f);
					chunkNoiseSampler.sampleNoiseX(x, d);
					chunkNoiseSampler.sampleNoiseZ(z, e);
					BlockState blockState = chunkNoiseSampler.sampleBlockState();
					BlockState blockState2 = blockState == null ? this.settings.value().defaultBlock() : blockState;
					if (blockStates != null) {
						int w = t * i + u;
						blockStates[w] = blockState2;
					}

					if (stopPredicate != null && stopPredicate.test(blockState2)) {
						chunkNoiseSampler.stopInterpolation();
						return OptionalInt.of(v + 1);
					}
				}
			}

			chunkNoiseSampler.stopInterpolation();
			return OptionalInt.empty();
		}
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
		if (!SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
			HeightContext heightContext = new HeightContext(this, region);
			this.buildSurface(
				chunk, heightContext, noiseConfig, structures, region.getBiomeAccess(), region.getRegistryManager().get(Registry.BIOME_KEY), Blender.getBlender(region)
			);
		}
	}

	@VisibleForTesting
	public void buildSurface(
		Chunk chunk,
		HeightContext heightContext,
		NoiseConfig noiseConfig,
		StructureAccessor structureAccessor,
		BiomeAccess biomeAccess,
		Registry<Biome> biomeRegistry,
		Blender blender
	) {
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			chunkx -> this.createChunkNoiseSampler(chunkx, structureAccessor, blender, noiseConfig)
		);
		ChunkGeneratorSettings chunkGeneratorSettings = this.settings.value();
		noiseConfig.getSurfaceBuilder()
			.buildSurface(
				noiseConfig,
				biomeAccess,
				biomeRegistry,
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
		BiomeAccess biomeAccess,
		StructureAccessor structureAccessor,
		Chunk chunk,
		GenerationStep.Carver carverStep
	) {
		BiomeAccess biomeAccess2 = biomeAccess.withSource(
			(biomeX, biomeY, biomeZ) -> this.biomeSource.getBiome(biomeX, biomeY, biomeZ, noiseConfig.getMultiNoiseSampler())
		);
		ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(RandomSeed.getSeed()));
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			chunkx -> this.createChunkNoiseSampler(chunkx, structureAccessor, Blender.getBlender(chunkRegion), noiseConfig)
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
				GenerationSettings generationSettings = chunk2.getOrCreateGenerationSettings(
					() -> this.getGenerationSettings(
							this.biomeSource
								.getBiome(BiomeCoords.fromBlock(chunkPos2.getStartX()), 0, BiomeCoords.fromBlock(chunkPos2.getStartZ()), noiseConfig.getMultiNoiseSampler())
						)
				);
				Iterable<RegistryEntry<ConfiguredCarver<?>>> iterable = generationSettings.getCarversForStep(carverStep);
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
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
		GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig().trimHeight(chunk.getHeightLimitView());
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

	private Chunk populateNoise(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight) {
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			chunkx -> this.createChunkNoiseSampler(chunkx, structureAccessor, blender, noiseConfig)
		);
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
		chunkNoiseSampler.sampleStartNoise();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int k = chunkNoiseSampler.getHorizontalBlockSize();
		int l = chunkNoiseSampler.getVerticalBlockSize();
		int m = 16 / k;
		int n = 16 / k;

		for (int o = 0; o < m; o++) {
			chunkNoiseSampler.sampleEndNoise(o);

			for (int p = 0; p < n; p++) {
				ChunkSection chunkSection = chunk.getSection(chunk.countVerticalSections() - 1);

				for (int q = cellHeight - 1; q >= 0; q--) {
					chunkNoiseSampler.sampleNoiseCorners(q, p);

					for (int r = l - 1; r >= 0; r--) {
						int s = (minimumCellY + q) * l + r;
						int t = s & 15;
						int u = chunk.getSectionIndex(s);
						if (chunk.getSectionIndex(chunkSection.getYOffset()) != u) {
							chunkSection = chunk.getSection(u);
						}

						double d = (double)r / (double)l;
						chunkNoiseSampler.sampleNoiseY(s, d);

						for (int v = 0; v < k; v++) {
							int w = i + o * k + v;
							int x = w & 15;
							double e = (double)v / (double)k;
							chunkNoiseSampler.sampleNoiseX(w, e);

							for (int y = 0; y < k; y++) {
								int z = j + p * k + y;
								int aa = z & 15;
								double f = (double)y / (double)k;
								chunkNoiseSampler.sampleNoiseZ(z, f);
								BlockState blockState = chunkNoiseSampler.sampleBlockState();
								if (blockState == null) {
									blockState = this.settings.value().defaultBlock();
								}

								blockState = this.getBlockState(chunkNoiseSampler, w, s, z, blockState);
								if (blockState != AIR && !SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
									if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
										mutable.set(w, s, z);
										((ProtoChunk)chunk).addLightSource(mutable);
									}

									chunkSection.setBlockState(x, t, aa, blockState, false);
									heightmap.trackUpdate(x, s, aa, blockState);
									heightmap2.trackUpdate(x, s, aa, blockState);
									if (aquiferSampler.needsFluidTick() && !blockState.getFluidState().isEmpty()) {
										mutable.set(w, s, z);
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

		chunkNoiseSampler.stopInterpolation();
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
			ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(RandomSeed.getSeed()));
			chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
			SpawnHelper.populateEntities(region, registryEntry, chunkPos, chunkRandom);
		}
	}
}
