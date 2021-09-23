package net.minecraft.world.gen.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.ChunkRandom;

/**
 * In charge of shaping, adding biome specific surface blocks, and carving chunks,
 * as well as populating the generated chunks with {@linkplain net.minecraft.world.gen.feature.Feature features} and {@linkplain net.minecraft.entity.Entity entities}.
 * Biome placement starts here, however all vanilla and most modded chunk generators delegate this to a {@linkplain net.minecraft.world.biome.source.BiomeSource biome source}.
 */
public abstract class ChunkGenerator implements BiomeAccess.Storage {
	public static final Codec<ChunkGenerator> CODEC = Registry.CHUNK_GENERATOR.dispatchStable(ChunkGenerator::getCodec, Function.identity());
	/**
	 * Used to control the population step without replacing the actual biome that comes from the original {@link #biomeSource}.
	 * 
	 * <p>This is used by {@link FlatChunkGenerator} to overwrite biome properties like whether lakes generate, while preserving the original biome ID.
	 */
	protected final BiomeSource populationSource;
	protected final BiomeSource biomeSource;
	private final StructuresConfig structuresConfig;
	private final long worldSeed;
	private final List<ChunkPos> strongholds = Lists.<ChunkPos>newArrayList();

	public ChunkGenerator(BiomeSource biomeSource, StructuresConfig structuresConfig) {
		this(biomeSource, biomeSource, structuresConfig, 0L);
	}

	public ChunkGenerator(BiomeSource populationSource, BiomeSource biomeSource, StructuresConfig structuresConfig, long worldSeed) {
		this.populationSource = populationSource;
		this.biomeSource = biomeSource;
		this.structuresConfig = structuresConfig;
		this.worldSeed = worldSeed;
	}

	private void generateStrongholdPositions() {
		if (this.strongholds.isEmpty()) {
			StrongholdConfig strongholdConfig = this.structuresConfig.getStronghold();
			if (strongholdConfig != null && strongholdConfig.getCount() != 0) {
				List<Biome> list = Lists.<Biome>newArrayList();

				for (Biome biome : this.populationSource.getBiomes()) {
					if (method_38266(biome)) {
						list.add(biome);
					}
				}

				int i = strongholdConfig.getDistance();
				int j = strongholdConfig.getCount();
				int k = strongholdConfig.getSpread();
				Random random = new Random();
				random.setSeed(this.worldSeed);
				double d = random.nextDouble() * Math.PI * 2.0;
				int l = 0;
				int m = 0;

				for (int n = 0; n < j; n++) {
					double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
					int o = (int)Math.round(Math.cos(d) * e);
					int p = (int)Math.round(Math.sin(d) * e);
					BlockPos blockPos = this.populationSource
						.locateBiome(ChunkSectionPos.getOffsetPos(o, 8), 0, ChunkSectionPos.getOffsetPos(p, 8), 112, list::contains, random, this.method_38276());
					if (blockPos != null) {
						o = ChunkSectionPos.getSectionCoord(blockPos.getX());
						p = ChunkSectionPos.getSectionCoord(blockPos.getZ());
					}

					this.strongholds.add(new ChunkPos(o, p));
					d += (Math.PI * 2) / (double)k;
					if (++l == k) {
						m++;
						l = 0;
						k += 2 * k / (m + 1);
						k = Math.min(k, j - n);
						d += random.nextDouble() * Math.PI * 2.0;
					}
				}
			}
		}
	}

	private static boolean method_38266(Biome biome) {
		Biome.Category category = biome.getCategory();
		return category != Biome.Category.OCEAN
			&& category != Biome.Category.RIVER
			&& category != Biome.Category.BEACH
			&& category != Biome.Category.SWAMP
			&& category != Biome.Category.NETHER
			&& category != Biome.Category.THEEND;
	}

	protected abstract Codec<? extends ChunkGenerator> getCodec();

	public abstract ChunkGenerator withSeed(long seed);

	public CompletableFuture<Chunk> populateBiomes(Executor executor, Registry<Biome> registry, StructureAccessor structureAccessor, Chunk chunk) {
		return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
			chunk.method_38257(this.biomeSource, this.method_38276());
			return chunk;
		}), Util.getMainWorkerExecutor());
	}

	public abstract MultiNoiseUtil.MultiNoiseSampler method_38276();

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.getBiomeSource().getBiome(biomeX, biomeY, biomeZ, this.method_38276());
	}

	/**
	 * Generates caves for the given chunk.
	 */
	public abstract void carve(
		ChunkRegion chunkRegion, long l, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carver
	);

	/**
	 * Tries to find the closest structure of a given type near a given block.
	 * <p>
	 * New chunks will only be generated up to the {@link net.minecraft.world.chunk.ChunkStatus#STRUCTURE_STARTS} phase by this method.
	 * <p>
	 * The radius is ignored for strongholds.
	 * 
	 * @return {@code null} if no structure could be found within the given search radius
	 * 
	 * @param radius the search radius in chunks around the chunk the given block position is in; a radius of 0 will only search in the given chunk
	 * @param skipExistingChunks whether only structures that are not referenced by generated chunks (chunks past the STRUCTURE_STARTS stage) are returned, excluding strongholds
	 */
	@Nullable
	public BlockPos locateStructure(ServerWorld serverWorld, StructureFeature<?> structureFeature, BlockPos center, int radius, boolean skipExistingChunks) {
		if (structureFeature == StructureFeature.STRONGHOLD) {
			this.generateStrongholdPositions();
			BlockPos blockPos = null;
			double d = Double.MAX_VALUE;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (ChunkPos chunkPos : this.strongholds) {
				mutable.set(ChunkSectionPos.getOffsetPos(chunkPos.x, 8), 32, ChunkSectionPos.getOffsetPos(chunkPos.z, 8));
				double e = mutable.getSquaredDistance(center);
				if (blockPos == null) {
					blockPos = new BlockPos(mutable);
					d = e;
				} else if (e < d) {
					blockPos = new BlockPos(mutable);
					d = e;
				}
			}

			return blockPos;
		} else {
			StructureConfig structureConfig = this.structuresConfig.getForType(structureFeature);
			ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> immutableMultimap = this.structuresConfig
				.getConfiguredStructureFeature(structureFeature);
			if (structureConfig != null && !immutableMultimap.isEmpty()) {
				Registry<Biome> registry = serverWorld.getRegistryManager().get(Registry.BIOME_KEY);
				Set<RegistryKey<Biome>> set = (Set<RegistryKey<Biome>>)this.biomeSource
					.getBiomes()
					.stream()
					.flatMap(biome -> registry.getKey(biome).stream())
					.collect(Collectors.toSet());
				return immutableMultimap.values().stream().noneMatch(set::contains)
					? null
					: structureFeature.locateStructure(
						serverWorld, serverWorld.getStructureAccessor(), center, radius, skipExistingChunks, serverWorld.getSeed(), structureConfig
					);
			} else {
				return null;
			}
		}
	}

	public void generateFeatures(StructureWorldAccess world, ChunkPos pos, StructureAccessor structureAccessor) {
		int i = pos.x;
		int j = pos.z;
		int k = pos.getStartX();
		int l = pos.getStartZ();
		if (!SharedConstants.method_37896(k, l)) {
			BlockPos blockPos = new BlockPos(k, world.getBottomY(), l);
			int m = ChunkSectionPos.getSectionCoord(blockPos.getX());
			int n = ChunkSectionPos.getSectionCoord(blockPos.getZ());
			int o = ChunkSectionPos.getBlockCoord(m);
			int p = ChunkSectionPos.getBlockCoord(n);
			int q = world.getBottomY() + 1;
			int r = world.getTopY() - 1;
			Map<Integer, List<StructureFeature<?>>> map = (Map<Integer, List<StructureFeature<?>>>)Registry.STRUCTURE_FEATURE
				.stream()
				.collect(Collectors.groupingBy(structureFeature -> structureFeature.getGenerationStep().ordinal()));
			ImmutableList<ImmutableList<ConfiguredFeature<?, ?>>> immutableList = this.populationSource.method_38115();
			ChunkRandom chunkRandom = new ChunkRandom();
			long s = chunkRandom.setPopulationSeed(world.getSeed(), k, l);

			try {
				Registry<ConfiguredFeature<?, ?>> registry = world.getRegistryManager().get(Registry.CONFIGURED_FEATURE_KEY);
				Registry<StructureFeature<?>> registry2 = world.getRegistryManager().get(Registry.STRUCTURE_FEATURE_KEY);
				int t = Math.max(GenerationStep.Feature.values().length, immutableList.size());

				for (int u = 0; u < t; u++) {
					int v = 0;
					if (structureAccessor.shouldGenerateStructures()) {
						for (StructureFeature<?> structureFeature : (List)map.getOrDefault(u, Collections.emptyList())) {
							chunkRandom.setDecoratorSeed(s, v, u);
							Supplier<String> supplier = () -> (String)registry2.getKey(structureFeature).map(Object::toString).orElseGet(structureFeature::toString);

							try {
								world.method_36972(supplier);
								structureAccessor.getStructuresWithChildren(ChunkSectionPos.from(blockPos), structureFeature)
									.forEach(
										structureStart -> structureStart.generateStructure(
												world, structureAccessor, this, chunkRandom, new BlockBox(o, q, p, o + 15, r, p + 15), new ChunkPos(m, n)
											)
									);
							} catch (Exception var31) {
								CrashReport crashReport = CrashReport.create(var31, "Feature placement");
								crashReport.addElement("Feature").add("Description", supplier::get);
								throw new CrashException(crashReport);
							}

							v++;
						}
					}

					if (immutableList.size() > u) {
						for (ConfiguredFeature<?, ?> configuredFeature : (ImmutableList)immutableList.get(u)) {
							Supplier<String> supplier2 = () -> (String)registry.getKey(configuredFeature).map(Object::toString).orElseGet(configuredFeature::toString);
							chunkRandom.setDecoratorSeed(s, v, u);

							try {
								world.method_36972(supplier2);
								configuredFeature.generate(Optional.of(configuredFeature), world, this, chunkRandom, blockPos);
							} catch (Exception var32) {
								CrashReport crashReport2 = CrashReport.create(var32, "Feature placement");
								crashReport2.addElement("Feature").add("Description", supplier2::get);
								throw new CrashException(crashReport2);
							}

							v++;
						}
					}
				}

				world.method_36972(null);
			} catch (Exception var33) {
				CrashReport crashReport3 = CrashReport.create(var33, "Biome decoration");
				crashReport3.addElement("Generation").add("CenterX", i).add("CenterZ", j).add("Seed", s);
				throw new CrashException(crashReport3);
			}
		}
	}

	/**
	 * Places the surface blocks of the biomes after the noise has been generated.
	 */
	public abstract void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, Chunk chunk);

	public abstract void populateEntities(ChunkRegion region);

	public StructuresConfig getStructuresConfig() {
		return this.structuresConfig;
	}

	public int getSpawnHeight(HeightLimitView world) {
		return 64;
	}

	public BiomeSource getBiomeSource() {
		return this.biomeSource;
	}

	public abstract int getWorldHeight();

	public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
		return biome.getSpawnSettings().getSpawnEntries(group);
	}

	/**
	 * Determines which structures should start in the given chunk and creates their starting points.
	 */
	public void setStructureStarts(
		DynamicRegistryManager registryManager, StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long worldSeed
	) {
		ChunkPos chunkPos = chunk.getPos();
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);
		StructureConfig structureConfig = this.structuresConfig.getForType(StructureFeature.STRONGHOLD);
		if (structureConfig != null) {
			StructureStart<?> structureStart = ConfiguredStructureFeatures.STRONGHOLD
				.tryPlaceStart(
					registryManager,
					this,
					this.populationSource,
					structureManager,
					worldSeed,
					chunkPos,
					method_38264(structureAccessor, chunk, chunkSectionPos, StructureFeature.STRONGHOLD),
					structureConfig,
					chunk,
					ChunkGenerator::method_38266
				);
			structureAccessor.setStructureStart(chunkSectionPos, StructureFeature.STRONGHOLD, structureStart, chunk);
		}

		Registry<Biome> registry = registryManager.get(Registry.BIOME_KEY);

		label39:
		for (StructureFeature<?> structureFeature : Registry.STRUCTURE_FEATURE) {
			if (structureFeature != StructureFeature.STRONGHOLD) {
				StructureConfig structureConfig2 = this.structuresConfig.getForType(structureFeature);
				if (structureConfig2 != null) {
					int i = method_38264(structureAccessor, chunk, chunkSectionPos, structureFeature);

					for (Entry<ConfiguredStructureFeature<?, ?>, Collection<RegistryKey<Biome>>> entry : this.structuresConfig
						.getConfiguredStructureFeature(structureFeature)
						.asMap()
						.entrySet()) {
						StructureStart<?> structureStart2 = ((ConfiguredStructureFeature)entry.getKey())
							.tryPlaceStart(
								registryManager,
								this,
								this.populationSource,
								structureManager,
								worldSeed,
								chunkPos,
								i,
								structureConfig2,
								chunk,
								biome -> this.method_38274(registry, ((Collection)entry.getValue())::contains, biome)
							);
						if (structureStart2.hasChildren()) {
							structureAccessor.setStructureStart(chunkSectionPos, structureFeature, structureStart2, chunk);
							continue label39;
						}
					}

					structureAccessor.setStructureStart(chunkSectionPos, structureFeature, StructureStart.DEFAULT, chunk);
				}
			}
		}
	}

	private static int method_38264(StructureAccessor structureAccessor, Chunk chunk, ChunkSectionPos chunkSectionPos, StructureFeature<?> structureFeature) {
		StructureStart<?> structureStart = structureAccessor.getStructureStart(chunkSectionPos, structureFeature, chunk);
		return structureStart != null ? structureStart.getReferences() : 0;
	}

	protected boolean method_38274(Registry<Biome> registry, Predicate<RegistryKey<Biome>> predicate, Biome biome) {
		return registry.getKey(biome).filter(predicate).isPresent();
	}

	/**
	 * Finds all structures that the given chunk intersects, and adds references to their starting chunks to it.
	 * A radius of 8 chunks around the given chunk will be searched for structure starts.
	 */
	public void addStructureReferences(StructureWorldAccess world, StructureAccessor accessor, Chunk chunk) {
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		int j = chunkPos.x;
		int k = chunkPos.z;
		int l = chunkPos.getStartX();
		int m = chunkPos.getStartZ();
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);

		for (int n = j - 8; n <= j + 8; n++) {
			for (int o = k - 8; o <= k + 8; o++) {
				long p = ChunkPos.toLong(n, o);

				for (StructureStart<?> structureStart : world.getChunk(n, o).getStructureStarts().values()) {
					try {
						if (structureStart.hasChildren() && structureStart.setBoundingBoxFromChildren().intersectsXZ(l, m, l + 15, m + 15)) {
							accessor.addStructureReference(chunkSectionPos, structureStart.getFeature(), p, chunk);
							DebugInfoSender.sendStructureStart(world, structureStart);
						}
					} catch (Exception var20) {
						CrashReport crashReport = CrashReport.create(var20, "Generating structure reference");
						CrashReportSection crashReportSection = crashReport.addElement("Structure");
						crashReportSection.add("Id", (CrashCallable<String>)(() -> Registry.STRUCTURE_FEATURE.getId(structureStart.getFeature()).toString()));
						crashReportSection.add("Name", (CrashCallable<String>)(() -> structureStart.getFeature().getName()));
						crashReportSection.add("Class", (CrashCallable<String>)(() -> structureStart.getFeature().getClass().getCanonicalName()));
						throw new CrashException(crashReport);
					}
				}
			}
		}
	}

	/**
	 * Generates the base shape of the chunk out of the basic block states as decided by this chunk generator's config.
	 */
	public abstract CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk);

	public abstract int getSeaLevel();

	public abstract int getMinimumY();

	/**
	 * Returns the raw noise height of a column for use in structure generation.
	 */
	public abstract int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world);

	/**
	 * Returns a sample of all the block states in a column for use in structure generation.
	 */
	public abstract VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world);

	public int getHeightOnGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
		return this.getHeight(x, z, heightmap, world);
	}

	public int getHeightInGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
		return this.getHeight(x, z, heightmap, world) - 1;
	}

	public boolean isStrongholdStartingChunk(ChunkPos pos) {
		this.generateStrongholdPositions();
		return this.strongholds.contains(pos);
	}

	static {
		Registry.register(Registry.CHUNK_GENERATOR, "noise", NoiseChunkGenerator.CODEC);
		Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatChunkGenerator.CODEC);
		Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugChunkGenerator.CODEC);
	}
}
