package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.ListIterator;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.class_6643;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
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
import net.minecraft.world.gen.DeepslateBlockSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import net.minecraft.world.gen.feature.PillagerOutpostFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.SwampHutFeature;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

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
	private static final int field_34589 = 8;
	private final int verticalNoiseResolution;
	private final int horizontalNoiseResolution;
	private final int noiseSizeX;
	private final int noiseSizeY;
	private final int noiseSizeZ;
	protected final BlockState defaultBlock;
	private final long seed;
	protected final Supplier<ChunkGeneratorSettings> settings;
	private final int worldHeight;
	private final NoiseColumnSampler noiseColumnSampler;
	private final BlockSource blockStateSampler;
	private final AquiferSampler.FluidLevelSampler fluidLevelSampler;

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
		this.noiseSizeX = 16 / this.horizontalNoiseResolution;
		this.noiseSizeY = generationShapeConfig.getHeight() / this.verticalNoiseResolution;
		this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
		this.noiseColumnSampler = new NoiseColumnSampler(
			this.horizontalNoiseResolution,
			this.verticalNoiseResolution,
			this.noiseSizeY,
			generationShapeConfig,
			chunkGeneratorSettings.getMultiNoiseParameters(),
			chunkGeneratorSettings.hasNoiseCaves(),
			seed
		);
		Builder<BlockSource> builder = ImmutableList.builder();
		builder.add(ChunkNoiseSampler::sampleInitialNoiseBlockState);
		builder.add(ChunkNoiseSampler::sampleOreVeins);
		if (chunkGeneratorSettings.hasDeepslate()) {
			AtomicSimpleRandom atomicSimpleRandom = new AtomicSimpleRandom(seed);
			builder.add(new DeepslateBlockSource(atomicSimpleRandom.createBlockPosRandomDeriver(), Blocks.DEEPSLATE.getDefaultState()));
		}

		this.blockStateSampler = new ChainedBlockSource(builder.build());
		AquiferSampler.FluidLevel fluidLevel = new AquiferSampler.FluidLevel(-54, Blocks.LAVA.getDefaultState());
		AquiferSampler.FluidLevel fluidLevel2 = new AquiferSampler.FluidLevel(chunkGeneratorSettings.getSeaLevel(), chunkGeneratorSettings.getDefaultFluid());
		AquiferSampler.FluidLevel fluidLevel3 = new AquiferSampler.FluidLevel(
			chunkGeneratorSettings.getGenerationShapeConfig().getMinimumY() - 1, Blocks.AIR.getDefaultState()
		);
		this.fluidLevelSampler = (x, y, z) -> y < -54 ? fluidLevel : fluidLevel2;
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
		int i = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY(), chunk.getBottomY());
		int j = Math.min(
			((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY()
				+ ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getHeight(),
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
			return this.noiseColumnSampler.method_38378(x, y, z, d, e, f, g, h, ix);
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
		return new NoiseChunkGenerator(this.populationSource.withSeed(seed), seed, this.settings);
	}

	public boolean matchesSettings(long seed, RegistryKey<ChunkGeneratorSettings> settingsKey) {
		return this.seed == seed && ((ChunkGeneratorSettings)this.settings.get()).equals(settingsKey);
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
	public void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		if (!SharedConstants.method_37896(chunkPos.getStartX(), chunkPos.getStartZ())) {
			ChunkRandom chunkRandom = new ChunkRandom();
			chunkRandom.setTerrainSeed(i, j);
			final ChunkPos chunkPos2 = chunk.getPos();
			int k = chunkPos2.getStartX();
			int l = chunkPos2.getStartZ();
			double d = 0.0625;
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			final BlockPos.Mutable mutable2 = new BlockPos.Mutable();
			int m = Math.max(((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY(), chunk.getBottomY());
			int n = Math.min(
				((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY()
					+ ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getHeight(),
				chunk.getTopY()
			);
			int o = MathHelper.floorDiv(m, this.verticalNoiseResolution);
			int p = MathHelper.floorDiv(n - m, this.verticalNoiseResolution);
			ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
				o,
				p,
				k,
				l,
				this.horizontalNoiseResolution,
				this.verticalNoiseResolution,
				this.noiseColumnSampler,
				() -> new StructureWeightSampler(structureAccessor, chunk),
				this.settings,
				this.fluidLevelSampler
			);
			BlockState blockState = ((ChunkGeneratorSettings)this.settings.get()).getDefaultFluid();
			BlockColumn blockColumn = new BlockColumn() {
				@Override
				public BlockState getState(int y) {
					return chunk.getBlockState(mutable2.setY(y));
				}

				@Override
				public void setState(int y, BlockState state) {
					chunk.setBlockState(mutable2.setY(y), state, false);
				}

				public String toString() {
					return "ChunkBlockColumn " + chunkPos2;
				}
			};

			for (int q = 0; q < 16; q++) {
				for (int r = 0; r < 16; r++) {
					int s = k + q;
					int t = l + r;
					int u = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, q, r) + 1;
					double e = this.noiseColumnSampler.getNoiseSampler().sample((double)s * 0.0625, (double)t * 0.0625, 0.0625, (double)q * 0.0625) * 15.0;
					mutable2.setX(s).setZ(t);
					int v = this.noiseColumnSampler.method_38383(s, t, chunkNoiseSampler.getInterpolatedTerrainNoisePoint(s, t));
					int w = v - 8;
					Biome biome = region.getBiome(mutable.set(s, u, t));
					biome.buildSurface(chunkRandom, blockColumn, s, t, u, e, this.defaultBlock, blockState, this.getSeaLevel(), w, region.getSeed());
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
	public void carve(
		ChunkRegion chunkRegion, long l, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep
	) {
		BiomeAccess biomeAccess2 = biomeAccess.withSource((ix, jx, kx) -> this.populationSource.getBiome(ix, jx, kx, this.getMultiNoiseSampler()));
		ChunkRandom chunkRandom = new ChunkRandom();
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		CarverContext carverContext = new CarverContext(this, chunk);
		ChunkPos chunkPos2 = chunk.getPos();
		GenerationShapeConfig generationShapeConfig = ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig();
		int j = Math.max(generationShapeConfig.getMinimumY(), chunk.getBottomY());
		int k = Math.min(generationShapeConfig.getMinimumY() + generationShapeConfig.getHeight(), chunk.getTopY());
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
		class_6643 lv = ((ProtoChunk)chunk).getOrCreateCarvingMask(generationStep);

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
						configuredCarver.carve(carverContext, chunk, biomeAccess2::getBiome, chunkRandom, aquiferSampler, chunkPos3, lv);
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
		return ((ChunkGeneratorSettings)this.settings.get()).getGenerationShapeConfig().getMinimumY();
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
			ChunkRandom chunkRandom = new ChunkRandom();
			chunkRandom.setPopulationSeed(region.getSeed(), chunkPos.getStartX(), chunkPos.getStartZ());
			SpawnHelper.populateEntities(region, biome, chunkPos, chunkRandom);
		}
	}
}
