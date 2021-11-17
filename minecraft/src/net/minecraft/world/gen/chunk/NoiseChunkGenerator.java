package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.BelowZeroRetrogen;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.ChainedBlockSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import net.minecraft.world.gen.feature.PillagerOutpostFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.SwampHutFeature;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class NoiseChunkGenerator extends ChunkGenerator {
	public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryLookupCodec.of(Registry.NOISE_WORLDGEN).forGetter(noiseChunkGenerator -> noiseChunkGenerator.noiseRegistry),
					BiomeSource.CODEC.fieldOf("biome_source").forGetter(noiseChunkGenerator -> noiseChunkGenerator.populationSource),
					Codec.LONG.fieldOf("seed").stable().forGetter(noiseChunkGenerator -> noiseChunkGenerator.seed),
					ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(noiseChunkGenerator -> noiseChunkGenerator.settings)
				)
				.apply(instance, instance.stable(NoiseChunkGenerator::new))
	);
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private static final BlockState[] EMPTY = new BlockState[0];
	protected final BlockState defaultBlock;
	private final Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry;
	private final long seed;
	protected final Supplier<ChunkGeneratorSettings> settings;
	private final NoiseColumnSampler noiseColumnSampler;
	private final SurfaceBuilder surfaceBuilder;
	private final BlockSource blockStateSampler;
	private final AquiferSampler.FluidLevelSampler fluidLevelSampler;

	public NoiseChunkGenerator(
		Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry, BiomeSource biomeSource, long seed, Supplier<ChunkGeneratorSettings> supplier
	) {
		this(noiseRegistry, biomeSource, biomeSource, seed, supplier);
	}

	private NoiseChunkGenerator(
		Registry<DoublePerlinNoiseSampler.NoiseParameters> noiseRegistry,
		BiomeSource biomeSource,
		BiomeSource biomeSource2,
		long seed,
		Supplier<ChunkGeneratorSettings> supplier
	) {
		super(biomeSource, biomeSource2, ((ChunkGeneratorSettings)supplier.get()).getStructuresConfig(), seed);
		this.noiseRegistry = noiseRegistry;
		this.seed = seed;
		this.settings = supplier;
		ChunkGeneratorSettings chunkGeneratorSettings = (ChunkGeneratorSettings)this.settings.get();
		this.defaultBlock = chunkGeneratorSettings.getDefaultBlock();
		GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
		this.noiseColumnSampler = new NoiseColumnSampler(
			generationShapeConfig, chunkGeneratorSettings.hasNoiseCaves(), seed, noiseRegistry, chunkGeneratorSettings.getRandomProvider()
		);
		Builder<BlockSource> builder = ImmutableList.builder();
		builder.add(ChunkNoiseSampler::sampleInitialNoiseBlockState);
		builder.add(ChunkNoiseSampler::sampleOreVeins);
		this.blockStateSampler = new ChainedBlockSource(builder.build());
		AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(-54, Blocks.LAVA.getDefaultState());
		AquiferSampler.FluidLevel fluidLevel2 = new AquiferSampler.FluidLevel(chunkGeneratorSettings.getSeaLevel(), chunkGeneratorSettings.getDefaultFluid());
		AquiferSampler.FluidLevel fluidLevel3 = new AquiferSampler.FluidLevel(generationShapeConfig.minimumY() - 1, Blocks.AIR.getDefaultState());
		this.fluidLevelSampler = (x, y, z) -> y < -54 ? fluidLevel : fluidLevel2;
		this.surfaceBuilder = new SurfaceBuilder(
			this.noiseColumnSampler, noiseRegistry, this.defaultBlock, chunkGeneratorSettings.getSeaLevel(), seed, chunkGeneratorSettings.getRandomProvider()
		);
	}

	@Override
	public CompletableFuture<Chunk> populateBiomes(Registry<Biome> registry, Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
			this.method_38327(registry, blender, structureAccessor, chunk);
			return chunk;
		}), Util.getMainWorkerExecutor());
	}

	private void method_38327(Registry<Biome> registry, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			this.noiseColumnSampler,
			() -> new StructureWeightSampler(structureAccessor, chunk),
			(ChunkGeneratorSettings)this.settings.get(),
			this.fluidLevelSampler,
			blender
		);
		BiomeSupplier biomeSupplier = BelowZeroRetrogen.getBiomeSupplier(blender.getBiomeSupplier(this.biomeSource), registry, chunk);
		chunk.method_38257(biomeSupplier, (i, y, j) -> this.noiseColumnSampler.method_39329(i, y, j, chunkNoiseSampler.createMultiNoisePoint(i, j)));
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
		return new NoiseChunkGenerator(this.noiseRegistry, this.populationSource.withSeed(seed), seed, this.settings);
	}

	public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settingsKey) {
		return this.seed == seed && ((ChunkGeneratorSettings)this.settings.get()).equals(settingsKey);
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
		GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();
		int i = Math.max(generationShapeConfig.minimumY(), world.getBottomY());
		int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), world.getTopY());
		int k = MathHelper.floorDiv(i, generationShapeConfig.method_39545());
		int l = MathHelper.floorDiv(j - i, generationShapeConfig.method_39545());
		return l <= 0 ? world.getBottomY() : this.sampleHeightmap(x, z, null, heightmap.getBlockPredicate(), k, l).orElse(world.getBottomY());
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();
		int i = Math.max(generationShapeConfig.minimumY(), world.getBottomY());
		int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), world.getTopY());
		int k = MathHelper.floorDiv(i, generationShapeConfig.method_39545());
		int l = MathHelper.floorDiv(j - i, generationShapeConfig.method_39545());
		if (l <= 0) {
			return new VerticalBlockSample(i, EMPTY);
		} else {
			BlockState[] blockStates = new BlockState[l * generationShapeConfig.method_39545()];
			this.sampleHeightmap(x, z, blockStates, null, k, l);
			return new VerticalBlockSample(i, blockStates);
		}
	}

	private OptionalInt sampleHeightmap(int i, int j, @Nullable BlockState[] states, @Nullable Predicate<BlockState> predicate, int k, int l) {
		GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();
		int m = generationShapeConfig.method_39546();
		int n = generationShapeConfig.method_39545();
		int o = Math.floorDiv(i, m);
		int p = Math.floorDiv(j, m);
		int q = Math.floorMod(i, m);
		int r = Math.floorMod(j, m);
		int s = o * m;
		int t = p * m;
		double d = (double)q / (double)m;
		double e = (double)r / (double)m;
		ChunkNoiseSampler chunkNoiseSampler = ChunkNoiseSampler.method_39542(
			s, t, k, l, this.noiseColumnSampler, (ChunkGeneratorSettings)this.settings.get(), this.fluidLevelSampler
		);
		chunkNoiseSampler.sampleStartNoise();
		chunkNoiseSampler.sampleEndNoise(0);

		for (int u = l - 1; u >= 0; u--) {
			chunkNoiseSampler.sampleNoiseCorners(u, 0);

			for (int v = n - 1; v >= 0; v--) {
				int w = (k + u) * n + v;
				double f = (double)v / (double)n;
				chunkNoiseSampler.sampleNoiseY(f);
				chunkNoiseSampler.sampleNoiseX(d);
				chunkNoiseSampler.sampleNoise(e);
				BlockState blockState = this.blockStateSampler.apply(chunkNoiseSampler, i, w, j);
				BlockState blockState2 = blockState == null ? this.defaultBlock : blockState;
				if (states != null) {
					int x = u * n + v;
					states[x] = blockState2;
				}

				if (predicate != null && predicate.test(blockState2)) {
					return OptionalInt.of(w + 1);
				}
			}
		}

		return OptionalInt.empty();
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
		if (!SharedConstants.method_37896(chunk.getPos())) {
			HeightContext heightContext = new HeightContext(this, region);
			ChunkGeneratorSettings chunkGeneratorSettings = (ChunkGeneratorSettings)this.settings.get();
			ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
				this.noiseColumnSampler, () -> new StructureWeightSampler(structures, chunk), chunkGeneratorSettings, this.fluidLevelSampler, Blender.getBlender(region)
			);
			this.surfaceBuilder
				.buildSurface(
					region.getBiomeAccess(),
					region.getRegistryManager().get(Registry.BIOME_KEY),
					chunkGeneratorSettings.usesLegacyRandom(),
					heightContext,
					chunk,
					chunkNoiseSampler,
					chunkGeneratorSettings.getSurfaceRule()
				);
		}
	}

	@Override
	public void carve(
		ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep
	) {
		BiomeAccess biomeAccess2 = biomeAccess.withSource((ix, jx, kx) -> this.populationSource.getBiome(ix, jx, kx, this.getMultiNoiseSampler()));
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			this.noiseColumnSampler,
			() -> new StructureWeightSampler(structureAccessor, chunk),
			(ChunkGeneratorSettings)this.settings.get(),
			this.fluidLevelSampler,
			Blender.getBlender(chunkRegion)
		);
		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
		CarverContext carverContext = new CarverContext(this, chunkRegion.getRegistryManager(), chunk.getHeightLimitView(), chunkNoiseSampler);
		CarvingMask carvingMask = ((ProtoChunk)chunk).getOrCreateCarvingMask(generationStep);

		for (int j = -8; j <= 8; j++) {
			for (int k = -8; k <= 8; k++) {
				ChunkPos chunkPos2 = new ChunkPos(chunkPos.x + j, chunkPos.z + k);
				Chunk chunk2 = chunkRegion.getChunk(chunkPos2.x, chunkPos2.z);
				GenerationSettings generationSettings = chunk2.method_38258(
						() -> this.populationSource
								.getBiome(BiomeCoords.fromBlock(chunkPos2.getStartX()), 0, BiomeCoords.fromBlock(chunkPos2.getStartZ()), this.getMultiNoiseSampler())
					)
					.getGenerationSettings();
				List<Supplier<ConfiguredCarver<?>>> list = generationSettings.getCarversForStep(generationStep);
				ListIterator<Supplier<ConfiguredCarver<?>>> listIterator = list.listIterator();

				while (listIterator.hasNext()) {
					int l = listIterator.nextIndex();
					ConfiguredCarver<?> configuredCarver = (ConfiguredCarver<?>)((Supplier)listIterator.next()).get();
					chunkRandom.setCarverSeed(seed + (long)l, chunkPos2.x, chunkPos2.z);
					if (configuredCarver.shouldCarve(chunkRandom)) {
						configuredCarver.carve(carverContext, chunk, biomeAccess2::getBiome, chunkRandom, aquiferSampler, chunkPos2, carvingMask);
					}
				}
			}
		}
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
		GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();
		HeightLimitView heightLimitView = chunk.getHeightLimitView();
		int i = Math.max(generationShapeConfig.minimumY(), heightLimitView.getBottomY());
		int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), heightLimitView.getTopY());
		int k = MathHelper.floorDiv(i, generationShapeConfig.method_39545());
		int l = MathHelper.floorDiv(j - i, generationShapeConfig.method_39545());
		if (l <= 0) {
			return CompletableFuture.completedFuture(chunk);
		} else {
			int m = chunk.getSectionIndex(l * generationShapeConfig.method_39545() - 1 + i);
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
		ChunkGeneratorSettings chunkGeneratorSettings = (ChunkGeneratorSettings)this.settings.get();
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			this.noiseColumnSampler, () -> new StructureWeightSampler(structureAccessor, chunk), chunkGeneratorSettings, this.fluidLevelSampler, blender
		);
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		ChunkPos chunkPos = chunk.getPos();
		int k = chunkPos.getStartX();
		int l = chunkPos.getStartZ();
		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
		chunkNoiseSampler.sampleStartNoise();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
		int m = generationShapeConfig.method_39546();
		int n = generationShapeConfig.method_39545();
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
						chunkNoiseSampler.sampleNoiseY(d);

						for (int x = 0; x < m; x++) {
							int y = k + q * m + x;
							int z = y & 15;
							double e = (double)x / (double)m;
							chunkNoiseSampler.sampleNoiseX(e);

							for (int aa = 0; aa < m; aa++) {
								int ab = l + r * m + aa;
								int ac = ab & 15;
								double f = (double)aa / (double)m;
								chunkNoiseSampler.sampleNoise(f);
								BlockState blockState = this.blockStateSampler.apply(chunkNoiseSampler, y, u, ab);
								if (blockState == null) {
									blockState = this.defaultBlock;
								}

								blockState = this.method_38323(u, y, ab, blockState);
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

		return chunk;
	}

	private BlockState method_38323(int y, int x, int z, BlockState block) {
		return block;
	}

	@Override
	public int getWorldHeight() {
		return ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().height();
	}

	@Override
	public int getSeaLevel() {
		return ((ChunkGeneratorSettings)this.settings.get()).getSeaLevel();
	}

	@Override
	public int getMinimumY() {
		return ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().minimumY();
	}

	@Override
	public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
		if (!accessor.hasStructureReferences(pos)) {
			return super.getEntitySpawnList(biome, accessor, group, pos);
		} else {
			if (accessor.getStructureContaining(pos, StructureFeature.SWAMP_HUT).hasChildren()) {
				if (group == SpawnGroup.MONSTER) {
					return SwampHutFeature.MONSTER_SPAWNS;
				}

				if (group == SpawnGroup.CREATURE) {
					return SwampHutFeature.CREATURE_SPAWNS;
				}
			}

			if (group == SpawnGroup.MONSTER) {
				if (accessor.getStructureAt(pos, StructureFeature.PILLAGER_OUTPOST).hasChildren()) {
					return PillagerOutpostFeature.MONSTER_SPAWNS;
				}

				if (accessor.getStructureAt(pos, StructureFeature.MONUMENT).hasChildren()) {
					return OceanMonumentFeature.MONSTER_SPAWNS;
				}

				if (accessor.getStructureContaining(pos, StructureFeature.FORTRESS).hasChildren()) {
					return NetherFortressFeature.MONSTER_SPAWNS;
				}
			}

			return (group == SpawnGroup.UNDERGROUND_WATER_CREATURE || group == SpawnGroup.AXOLOTLS)
					&& accessor.getStructureAt(pos, StructureFeature.MONUMENT).hasChildren()
				? SpawnSettings.EMPTY_ENTRY_POOL
				: super.getEntitySpawnList(biome, accessor, group, pos);
		}
	}

	@Override
	public void populateEntities(ChunkRegion region) {
		if (!((ChunkGeneratorSettings)this.settings.get()).isMobGenerationDisabled()) {
			ChunkPos chunkPos = region.getCenterPos();
			Biome biome = region.getBiome(chunkPos.getStartPos().withY(region.getTopY() - 1));
			ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
			chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
			SpawnHelper.populateEntities(region, biome, chunkPos, chunkRandom);
		}
	}

	@Deprecated
	public Optional<BlockState> method_39041(
		CarverContext context, Function<BlockPos, Biome> function, Chunk chunk, ChunkNoiseSampler chunkNoiseSampler, BlockPos blockPos, boolean bl
	) {
		return this.surfaceBuilder
			.method_39110(((ChunkGeneratorSettings)this.settings.get()).getSurfaceRule(), context, function, chunk, chunkNoiseSampler, blockPos, bl);
	}
}
