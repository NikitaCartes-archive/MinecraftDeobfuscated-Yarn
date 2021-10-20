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
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.ChainedBlockSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.LayerTransitionBlockSource;
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
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class NoiseChunkGenerator extends ChunkGenerator {
	public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryLookupCodec.of(Registry.NOISE_WORLDGEN).forGetter(noiseChunkGenerator -> noiseChunkGenerator.field_35361),
					BiomeSource.CODEC.fieldOf("biome_source").forGetter(noiseChunkGenerator -> noiseChunkGenerator.populationSource),
					Codec.LONG.fieldOf("seed").stable().forGetter(noiseChunkGenerator -> noiseChunkGenerator.seed),
					ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(noiseChunkGenerator -> noiseChunkGenerator.settings)
				)
				.apply(instance, instance.stable(NoiseChunkGenerator::new))
	);
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private static final BlockState[] EMPTY = new BlockState[0];
	private static final int field_35129 = 5;
	private final int verticalNoiseResolution;
	private final int horizontalNoiseResolution;
	private final int noiseSizeX;
	private final int noiseSizeY;
	private final int noiseSizeZ;
	protected final BlockState defaultBlock;
	private final Registry<DoublePerlinNoiseSampler.NoiseParameters> field_35361;
	private final long seed;
	protected final Supplier<ChunkGeneratorSettings> settings;
	private final int worldHeight;
	private final NoiseColumnSampler noiseColumnSampler;
	private final SurfaceBuilder surfaceBuilder;
	private final BlockSource blockStateSampler;
	private final AquiferSampler.FluidLevelSampler fluidLevelSampler;

	public NoiseChunkGenerator(
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry, BiomeSource biomeSource, long l, Supplier<ChunkGeneratorSettings> supplier
	) {
		this(registry, biomeSource, biomeSource, l, supplier);
	}

	private NoiseChunkGenerator(
		Registry<DoublePerlinNoiseSampler.NoiseParameters> registry,
		BiomeSource biomeSource,
		BiomeSource biomeSource2,
		long l,
		Supplier<ChunkGeneratorSettings> supplier
	) {
		super(biomeSource, biomeSource2, ((ChunkGeneratorSettings)supplier.get()).getStructuresConfig(), l);
		this.field_35361 = registry;
		this.seed = l;
		ChunkGeneratorSettings chunkGeneratorSettings = (ChunkGeneratorSettings)supplier.get();
		this.settings = supplier;
		GenerationShapeConfig generationShapeConfig = chunkGeneratorSettings.getGenerationShapeConfig();
		this.worldHeight = generationShapeConfig.height();
		this.verticalNoiseResolution = BiomeCoords.toBlock(generationShapeConfig.verticalSize());
		this.horizontalNoiseResolution = BiomeCoords.toBlock(generationShapeConfig.horizontalSize());
		this.defaultBlock = chunkGeneratorSettings.getDefaultBlock();
		this.noiseSizeX = 16 / this.horizontalNoiseResolution;
		this.noiseSizeY = generationShapeConfig.height() / this.verticalNoiseResolution;
		this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
		this.noiseColumnSampler = new NoiseColumnSampler(
			this.horizontalNoiseResolution,
			this.verticalNoiseResolution,
			this.noiseSizeY,
			generationShapeConfig,
			chunkGeneratorSettings.hasNoiseCaves(),
			l,
			registry,
			chunkGeneratorSettings.getRandomProvider()
		);
		Builder<BlockSource> builder = ImmutableList.builder();
		AbstractRandom abstractRandom = chunkGeneratorSettings.createRandom(l);
		int i = chunkGeneratorSettings.getBedrockFloorY();
		if (i > -5 && i < this.worldHeight) {
			int j = this.getMinimumY() + i;
			builder.add(new LayerTransitionBlockSource(abstractRandom.createBlockPosRandomDeriver(), Blocks.BEDROCK.getDefaultState(), null, j, j + 5));
		}

		int j = chunkGeneratorSettings.getBedrockCeilingY();
		if (j > -5 && j < this.worldHeight) {
			int k = this.getMinimumY() + this.worldHeight - 1 + j;
			builder.add(new LayerTransitionBlockSource(abstractRandom.createBlockPosRandomDeriver(), null, Blocks.BEDROCK.getDefaultState(), k - 5, k));
		}

		builder.add(ChunkNoiseSampler::sampleInitialNoiseBlockState);
		builder.add(ChunkNoiseSampler::sampleOreVeins);
		if (chunkGeneratorSettings.hasDeepslate()) {
			builder.add(new LayerTransitionBlockSource(this.noiseColumnSampler.getDepthBasedLayerRandomDeriver(), Blocks.DEEPSLATE.getDefaultState(), null, -8, 0));
		}

		this.blockStateSampler = new ChainedBlockSource(builder.build());
		AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(-54, Blocks.LAVA.getDefaultState());
		AquiferSampler.FluidLevel fluidLevel2 = new AquiferSampler.FluidLevel(chunkGeneratorSettings.getSeaLevel(), chunkGeneratorSettings.getDefaultFluid());
		AquiferSampler.FluidLevel fluidLevel3 = new AquiferSampler.FluidLevel(
			chunkGeneratorSettings.getGenerationShapeConfig().minimumY() - 1, Blocks.AIR.getDefaultState()
		);
		this.fluidLevelSampler = (x, y, z) -> y < -54 ? fluidLevel : fluidLevel2;
		this.surfaceBuilder = new SurfaceBuilder(
			this.noiseColumnSampler, registry, this.defaultBlock, chunkGeneratorSettings.getSeaLevel(), l, chunkGeneratorSettings.getRandomProvider()
		);
	}

	@Override
	public CompletableFuture<Chunk> populateBiomes(Executor executor, Registry<Biome> biomeRegistry, StructureAccessor structureAccessor, Chunk chunk) {
		return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
			this.method_38327(biomeRegistry, structureAccessor, chunk);
			return chunk;
		}), Util.getMainWorkerExecutor());
	}

	private void method_38327(Registry<Biome> biomeRegistry, StructureAccessor world, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().minimumY(), chunk.getBottomY());
		int j = Math.min(
			((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().minimumY()
				+ ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().height(),
			chunk.getTopY()
		);
		int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
		int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			k,
			l,
			chunkPos.getStartX(),
			chunkPos.getStartZ(),
			this.horizontalNoiseResolution,
			this.verticalNoiseResolution,
			this.noiseColumnSampler,
			() -> new StructureWeightSampler(world, chunk),
			this.settings,
			this.fluidLevelSampler
		);
		chunk.method_38257(this.biomeSource, (x, y, z) -> {
			double d = chunkNoiseSampler.getNoiseX(x, z);
			double e = chunkNoiseSampler.getNoiseZ(x, z);
			float f = (float)chunkNoiseSampler.getContinentalness(x, z);
			float g = (float)chunkNoiseSampler.getErosion(x, z);
			float h = (float)chunkNoiseSampler.getWeirdness(x, z);
			double ix = chunkNoiseSampler.getTerrainNoisePoint(x, z).offset();
			return this.noiseColumnSampler.sample(x, y, z, d, e, f, g, h, ix);
		});
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
		return new NoiseChunkGenerator(this.field_35361, this.populationSource.withSeed(seed), seed, this.settings);
	}

	public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settingsKey) {
		return this.seed == seed && ((ChunkGeneratorSettings)this.settings.get()).equals(settingsKey);
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
		int i = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().minimumY(), world.getBottomY());
		int j = Math.min(
			((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().minimumY()
				+ ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().height(),
			world.getTopY()
		);
		int k = MathHelper.floorDiv(i, this.verticalNoiseResolution);
		int l = MathHelper.floorDiv(j - i, this.verticalNoiseResolution);
		return l <= 0 ? world.getBottomY() : this.sampleHeightmap(x, z, null, heightmap.getBlockPredicate(), k, l).orElse(world.getBottomY());
	}

	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
		int i = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().minimumY(), world.getBottomY());
		int j = Math.min(
			((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().minimumY()
				+ ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().height(),
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

	private OptionalInt sampleHeightmap(int x, int z, @Nullable BlockState[] states, @Nullable Predicate<BlockState> predicate, int minimumY, int height) {
		int i = Math.floorDiv(x, this.horizontalNoiseResolution);
		int j = Math.floorDiv(z, this.horizontalNoiseResolution);
		int k = Math.floorMod(x, this.horizontalNoiseResolution);
		int l = Math.floorMod(z, this.horizontalNoiseResolution);
		int m = i * this.horizontalNoiseResolution;
		int n = j * this.horizontalNoiseResolution;
		double d = (double)k / (double)this.horizontalNoiseResolution;
		double e = (double)l / (double)this.horizontalNoiseResolution;
		ChunkNoiseSampler chunkNoiseSampler = new ChunkNoiseSampler(
			this.horizontalNoiseResolution,
			this.verticalNoiseResolution,
			1,
			height,
			minimumY,
			this.noiseColumnSampler,
			m,
			n,
			(xx, y, zx) -> 0.0,
			this.settings,
			this.fluidLevelSampler
		);
		chunkNoiseSampler.sampleStartNoise();
		chunkNoiseSampler.sampleEndNoise(0);

		for (int o = height - 1; o >= 0; o--) {
			chunkNoiseSampler.sampleNoiseCorners(o, 0);

			for (int p = this.verticalNoiseResolution - 1; p >= 0; p--) {
				int q = (minimumY + o) * this.verticalNoiseResolution + p;
				double f = (double)p / (double)this.verticalNoiseResolution;
				chunkNoiseSampler.sampleNoiseY(f);
				chunkNoiseSampler.sampleNoiseX(d);
				chunkNoiseSampler.sampleNoise(e);
				BlockState blockState = this.blockStateSampler.apply(chunkNoiseSampler, x, q, z);
				BlockState blockState2 = blockState == null ? this.defaultBlock : blockState;
				if (states != null) {
					int r = o * this.verticalNoiseResolution + p;
					states[r] = blockState2;
				}

				if (predicate != null && predicate.test(blockState2)) {
					return OptionalInt.of(q + 1);
				}
			}
		}

		return OptionalInt.empty();
	}

	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		if (!SharedConstants.method_37896(chunkPos.getStartX(), chunkPos.getStartZ())) {
			int i = chunkPos.getStartX();
			int j = chunkPos.getStartZ();
			int k = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().minimumY(), chunk.getBottomY());
			int l = Math.min(
				((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().minimumY()
					+ ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().height(),
				chunk.getTopY()
			);
			int m = MathHelper.floorDiv(k, this.verticalNoiseResolution);
			int n = MathHelper.floorDiv(l - k, this.verticalNoiseResolution);
			HeightContext heightContext = new HeightContext(this, region);
			ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
				m,
				n,
				i,
				j,
				this.horizontalNoiseResolution,
				this.verticalNoiseResolution,
				this.noiseColumnSampler,
				() -> new StructureWeightSampler(structures, chunk),
				this.settings,
				this.fluidLevelSampler
			);
			this.surfaceBuilder
				.buildSurface(
					region.getBiomeAccess(),
					region.getRegistryManager().get(Registry.BIOME_KEY),
					((ChunkGeneratorSettings)this.settings.get()).usesLegacyRandom(),
					heightContext,
					chunk,
					chunkNoiseSampler,
					((ChunkGeneratorSettings)this.settings.get()).getSurfaceRule()
				);
		}
	}

	@Override
	public void carve(
		ChunkRegion chunkRegion, long l, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep
	) {
		BiomeAccess biomeAccess2 = biomeAccess.withSource((ix, jx, kx) -> this.populationSource.getBiome(ix, jx, kx, this.getMultiNoiseSampler()));
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		CarverContext carverContext = new CarverContext(this, chunkRegion.getRegistryManager(), chunk);
		ChunkPos chunkPos2 = chunk.getPos();
		GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();
		int j = Math.max(generationShapeConfig.minimumY(), chunk.getBottomY());
		int k = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), chunk.getTopY());
		int m = MathHelper.floorDiv(j, this.verticalNoiseResolution);
		int n = MathHelper.floorDiv(k - j, this.verticalNoiseResolution);
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			m,
			n,
			chunkPos2.getStartX(),
			chunkPos2.getStartZ(),
			this.horizontalNoiseResolution,
			this.verticalNoiseResolution,
			this.noiseColumnSampler,
			() -> new StructureWeightSampler(structureAccessor, chunk),
			this.settings,
			this.fluidLevelSampler
		);
		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
		CarvingMask carvingMask = ((ProtoChunk)chunk).getOrCreateCarvingMask(generationStep);

		for (int o = -8; o <= 8; o++) {
			for (int p = -8; p <= 8; p++) {
				ChunkPos chunkPos3 = new ChunkPos(chunkPos.x + o, chunkPos.z + p);
				Chunk chunk2 = chunkRegion.getChunk(chunkPos3.x, chunkPos3.z);
				GenerationSettings generationSettings = chunk2.method_38258(
						() -> this.populationSource
								.getBiome(BiomeCoords.fromBlock(chunkPos3.getStartX()), 0, BiomeCoords.fromBlock(chunkPos3.getStartZ()), this.getMultiNoiseSampler())
					)
					.getGenerationSettings();
				List<Supplier<ConfiguredCarver<?>>> list = generationSettings.getCarversForStep(generationStep);
				ListIterator<Supplier<ConfiguredCarver<?>>> listIterator = list.listIterator();

				while (listIterator.hasNext()) {
					int q = listIterator.nextIndex();
					ConfiguredCarver<?> configuredCarver = (ConfiguredCarver<?>)((Supplier)listIterator.next()).get();
					chunkRandom.setCarverSeed(l + (long)q, chunkPos3.x, chunkPos3.z);
					if (configuredCarver.shouldCarve(chunkRandom)) {
						configuredCarver.carve(carverContext, chunk, biomeAccess2::getBiome, chunkRandom, aquiferSampler, chunkPos3, carvingMask);
					}
				}
			}
		}
	}

	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk) {
		GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();
		int i = Math.max(generationShapeConfig.minimumY(), chunk.getBottomY());
		int j = Math.min(generationShapeConfig.minimumY() + generationShapeConfig.height(), chunk.getTopY());
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

			return CompletableFuture.supplyAsync(Util.debugSupplier("wgen_fill_noise", () -> this.populateNoise(accessor, chunk, k, l)), Util.getMainWorkerExecutor())
				.whenCompleteAsync((chunkx, throwable) -> {
					for (ChunkSection chunkSectionx : set) {
						chunkSectionx.unlock();
					}
				}, executor);
		}
	}

	private Chunk populateNoise(StructureAccessor accessor, Chunk chunk, int startY, int noiseSizeY) {
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
			startY,
			noiseSizeY,
			i,
			j,
			this.horizontalNoiseResolution,
			this.verticalNoiseResolution,
			this.noiseColumnSampler,
			() -> new StructureWeightSampler(accessor, chunk),
			this.settings,
			this.fluidLevelSampler
		);
		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
		chunkNoiseSampler.sampleStartNoise();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = 0; k < this.noiseSizeX; k++) {
			chunkNoiseSampler.sampleEndNoise(k);

			for (int l = 0; l < this.noiseSizeZ; l++) {
				ChunkSection chunkSection = chunk.getSection(chunk.countVerticalSections() - 1);

				for (int m = noiseSizeY - 1; m >= 0; m--) {
					chunkNoiseSampler.sampleNoiseCorners(m, l);

					for (int n = this.verticalNoiseResolution - 1; n >= 0; n--) {
						int o = (startY + m) * this.verticalNoiseResolution + n;
						int p = o & 15;
						int q = chunk.getSectionIndex(o);
						if (chunk.getSectionIndex(chunkSection.getYOffset()) != q) {
							chunkSection = chunk.getSection(q);
						}

						double d = (double)n / (double)this.verticalNoiseResolution;
						chunkNoiseSampler.sampleNoiseY(d);

						for (int r = 0; r < this.horizontalNoiseResolution; r++) {
							int s = i + k * this.horizontalNoiseResolution + r;
							int t = s & 15;
							double e = (double)r / (double)this.horizontalNoiseResolution;
							chunkNoiseSampler.sampleNoiseX(e);

							for (int u = 0; u < this.horizontalNoiseResolution; u++) {
								int v = j + l * this.horizontalNoiseResolution + u;
								int w = v & 15;
								double f = (double)u / (double)this.horizontalNoiseResolution;
								chunkNoiseSampler.sampleNoise(f);
								BlockState blockState = this.blockStateSampler.apply(chunkNoiseSampler, s, o, v);
								if (blockState == null) {
									blockState = this.defaultBlock;
								}

								blockState = this.method_38323(o, s, v, blockState);
								if (blockState != AIR && !SharedConstants.method_37896(s, v)) {
									if (blockState.getLuminance() != 0 && chunk instanceof ProtoChunk) {
										mutable.set(s, o, v);
										((ProtoChunk)chunk).addLightSource(mutable);
									}

									chunkSection.setBlockState(t, p, w, blockState, false);
									heightmap.trackUpdate(t, o, w, blockState);
									heightmap2.trackUpdate(t, o, w, blockState);
									if (aquiferSampler.needsFluidTick() && !blockState.getFluidState().isEmpty()) {
										mutable.set(s, o, v);
										chunk.getFluidTickScheduler().schedule(mutable, blockState.getFluidState().getFluid(), 0);
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
		return this.worldHeight;
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
		if (!accessor.method_38852(pos)) {
			return super.getEntitySpawnList(biome, accessor, group, pos);
		} else {
			if (accessor.method_38854(pos, StructureFeature.SWAMP_HUT).hasChildren()) {
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

				if (accessor.method_38854(pos, StructureFeature.FORTRESS).hasChildren()) {
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
			Biome biome = region.getBiome(chunkPos.getStartPos());
			ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
			chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
			SpawnHelper.populateEntities(region, biome, chunkPos, chunkRandom);
		}
	}

	@Deprecated
	public Optional<BlockState> method_39041(CarverContext context, Biome biome, Chunk chunk, BlockPos pos, boolean bl) {
		RegistryKey<Biome> registryKey = (RegistryKey<Biome>)context.getRegistryManager()
			.get(Registry.BIOME_KEY)
			.getKey(biome)
			.orElseThrow(() -> new IllegalStateException("Unregistered biome: " + biome));
		return this.surfaceBuilder.method_39110(((ChunkGeneratorSettings)this.settings.get()).getSurfaceRule(), context, biome, registryKey, chunk, pos, bl);
	}
}
