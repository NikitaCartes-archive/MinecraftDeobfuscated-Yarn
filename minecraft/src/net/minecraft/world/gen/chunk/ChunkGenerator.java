package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Arrays;
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
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructurePresence;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;

/**
 * In charge of shaping, adding biome specific surface blocks, and carving chunks,
 * as well as populating the generated chunks with {@linkplain net.minecraft.world.gen.feature.Feature features} and {@linkplain net.minecraft.entity.Entity entities}.
 * Biome placement starts here, however all vanilla and most modded chunk generators delegate this to a {@linkplain net.minecraft.world.biome.source.BiomeSource biome source}.
 */
public abstract class ChunkGenerator implements BiomeAccess.Storage {
	public static final Codec<ChunkGenerator> CODEC = Registry.CHUNK_GENERATOR.getCodec().dispatchStable(ChunkGenerator::getCodec, Function.identity());
	/**
	 * Used to control the population step without replacing the actual biome that comes from the original {@link #biomeSource}.
	 * 
	 * <p>This is used by {@link FlatChunkGenerator} to overwrite biome properties like whether lakes generate, while preserving the original biome ID.
	 */
	protected final BiomeSource populationSource;
	protected final BiomeSource biomeSource;
	private final StructuresConfig structuresConfig;
	private final Map<ConcentricRingsStructurePlacement, ArrayList<ChunkPos>> field_36405;
	private final long field_36406;

	public ChunkGenerator(BiomeSource biomeSource, StructuresConfig structuresConfig) {
		this(biomeSource, biomeSource, structuresConfig, 0L);
	}

	public ChunkGenerator(BiomeSource biomeSource, BiomeSource biomeSource2, StructuresConfig structuresConfig, long worldSeed) {
		this.populationSource = biomeSource;
		this.biomeSource = biomeSource2;
		this.structuresConfig = structuresConfig;
		this.field_36406 = worldSeed;
		this.field_36405 = new Object2ObjectArrayMap<>();

		for (Entry<StructureFeature<?>, StructurePlacement> entry : structuresConfig.getStructures().entrySet()) {
			if (entry.getValue() instanceof ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
				this.field_36405.put(concentricRingsStructurePlacement, new ArrayList());
			}
		}
	}

	protected void method_40145() {
		for (Entry<StructureFeature<?>, StructurePlacement> entry : this.structuresConfig.getStructures().entrySet()) {
			if (entry.getValue() instanceof ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
				this.generateStrongholdPositions((StructureFeature<?>)entry.getKey(), concentricRingsStructurePlacement);
			}
		}
	}

	private void generateStrongholdPositions(StructureFeature<?> structureFeature, ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
		if (concentricRingsStructurePlacement.count() != 0) {
			Predicate<RegistryKey<Biome>> predicate = ((Set)this.structuresConfig
				.getConfiguredStructureFeature(structureFeature)
				.values()
				.stream()
				.collect(Collectors.toUnmodifiableSet()))::contains;
			List<ChunkPos> list = this.getConcentricRingsStartChunks(concentricRingsStructurePlacement);
			int i = concentricRingsStructurePlacement.distance();
			int j = concentricRingsStructurePlacement.count();
			int k = concentricRingsStructurePlacement.spread();
			Random random = new Random();
			random.setSeed(this.field_36406);
			double d = random.nextDouble() * Math.PI * 2.0;
			int l = 0;
			int m = 0;

			for (int n = 0; n < j; n++) {
				double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
				int o = (int)Math.round(Math.cos(d) * e);
				int p = (int)Math.round(Math.sin(d) * e);
				BlockPos blockPos = this.populationSource
					.locateBiome(
						ChunkSectionPos.getOffsetPos(o, 8),
						0,
						ChunkSectionPos.getOffsetPos(p, 8),
						112,
						registryEntry -> registryEntry.matches(predicate),
						random,
						this.getMultiNoiseSampler()
					);
				if (blockPos != null) {
					o = ChunkSectionPos.getSectionCoord(blockPos.getX());
					p = ChunkSectionPos.getSectionCoord(blockPos.getZ());
				}

				list.add(new ChunkPos(o, p));
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

	protected abstract Codec<? extends ChunkGenerator> getCodec();

	public Optional<RegistryKey<Codec<? extends ChunkGenerator>>> getCodecKey() {
		return Registry.CHUNK_GENERATOR.getKey(this.getCodec());
	}

	public abstract ChunkGenerator withSeed(long seed);

	public CompletableFuture<Chunk> populateBiomes(
		Registry<Biome> biomeRegistry, Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk
	) {
		return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
			chunk.populateBiomes(this.biomeSource::getBiome, this.getMultiNoiseSampler());
			return chunk;
		}), Util.getMainWorkerExecutor());
	}

	public abstract MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler();

	@Override
	public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.getBiomeSource().getBiome(biomeX, biomeY, biomeZ, this.getMultiNoiseSampler());
	}

	/**
	 * Generates caves for the given chunk.
	 */
	public abstract void carve(
		ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep
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
	public BlockPos locateStructure(ServerWorld world, StructureFeature<?> feature, BlockPos center, int radius, boolean skipExistingChunks) {
		StructurePlacement structurePlacement = this.structuresConfig.getForType(feature);
		Collection<RegistryKey<Biome>> collection = this.structuresConfig.getConfiguredStructureFeature(feature).values();
		if (structurePlacement != null && !collection.isEmpty()) {
			Set<RegistryKey<Biome>> set = (Set<RegistryKey<Biome>>)this.biomeSource
				.getBiomes()
				.flatMap(registryEntry -> registryEntry.getKey().stream())
				.collect(Collectors.toSet());
			if (collection.stream().noneMatch(set::contains)) {
				return null;
			} else if (structurePlacement instanceof ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
				return this.method_40148(center, concentricRingsStructurePlacement);
			} else if (structurePlacement instanceof RandomSpreadStructurePlacement randomSpreadStructurePlacement) {
				return method_40146(feature, world, world.getStructureAccessor(), center, radius, skipExistingChunks, world.getSeed(), randomSpreadStructurePlacement);
			} else {
				throw new IllegalStateException("Invalid structure placement type");
			}
		} else {
			return null;
		}
	}

	@Nullable
	private BlockPos method_40148(BlockPos blockPos, ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
		List<ChunkPos> list = this.getConcentricRingsStartChunks(concentricRingsStructurePlacement);
		BlockPos blockPos2 = null;
		double d = Double.MAX_VALUE;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (ChunkPos chunkPos : list) {
			mutable.set(ChunkSectionPos.getOffsetPos(chunkPos.x, 8), 32, ChunkSectionPos.getOffsetPos(chunkPos.z, 8));
			double e = mutable.getSquaredDistance(blockPos);
			if (blockPos2 == null) {
				blockPos2 = new BlockPos(mutable);
				d = e;
			} else if (e < d) {
				blockPos2 = new BlockPos(mutable);
				d = e;
			}
		}

		return blockPos2;
	}

	@Nullable
	private static BlockPos method_40146(
		StructureFeature<?> structureFeature,
		WorldView worldView,
		StructureAccessor structureAccessor,
		BlockPos blockPos,
		int i,
		boolean bl,
		long l,
		RandomSpreadStructurePlacement randomSpreadStructurePlacement
	) {
		int j = randomSpreadStructurePlacement.spacing();
		int k = ChunkSectionPos.getSectionCoord(blockPos.getX());
		int m = ChunkSectionPos.getSectionCoord(blockPos.getZ());

		for (int n = 0; n <= i; n++) {
			for (int o = -n; o <= n; o++) {
				boolean bl2 = o == -n || o == n;

				for (int p = -n; p <= n; p++) {
					boolean bl3 = p == -n || p == n;
					if (bl2 || bl3) {
						int q = k + j * o;
						int r = m + j * p;
						ChunkPos chunkPos = randomSpreadStructurePlacement.getStartChunk(l, q, r);
						StructurePresence structurePresence = structureAccessor.getStructurePresence(chunkPos, structureFeature, bl);
						if (structurePresence != StructurePresence.START_NOT_PRESENT) {
							if (!bl && structurePresence == StructurePresence.START_PRESENT) {
								return StructureFeature.getLocatedPos(randomSpreadStructurePlacement, chunkPos);
							}

							Chunk chunk = worldView.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);
							StructureStart<?> structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk), structureFeature, chunk);
							if (structureStart != null && structureStart.hasChildren()) {
								if (bl && structureStart.isInExistingChunk()) {
									structureAccessor.incrementReferences(structureStart);
									return StructureFeature.getLocatedPos(randomSpreadStructurePlacement, structureStart.getPos());
								}

								if (!bl) {
									return StructureFeature.getLocatedPos(randomSpreadStructurePlacement, structureStart.getPos());
								}
							}

							if (n == 0) {
								break;
							}
						}
					}
				}

				if (n == 0) {
					break;
				}
			}
		}

		return null;
	}

	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
		ChunkPos chunkPos = chunk.getPos();
		if (!SharedConstants.method_37896(chunkPos)) {
			ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, world.getBottomSectionCoord());
			BlockPos blockPos = chunkSectionPos.getMinPos();
			Map<Integer, List<StructureFeature<?>>> map = (Map<Integer, List<StructureFeature<?>>>)Registry.STRUCTURE_FEATURE
				.stream()
				.collect(Collectors.groupingBy(structureFeature -> structureFeature.getGenerationStep().ordinal()));
			List<BiomeSource.class_6827> list = this.populationSource.method_38115();
			ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));
			long l = chunkRandom.setPopulationSeed(world.getSeed(), blockPos.getX(), blockPos.getZ());
			Set<Biome> set = new ObjectArraySet<>();
			if (this instanceof FlatChunkGenerator) {
				this.populationSource.getBiomes().map(RegistryEntry::value).forEach(set::add);
			} else {
				ChunkPos.stream(chunkSectionPos.toChunkPos(), 1).forEach(chunkPosx -> {
					Chunk chunkx = world.getChunk(chunkPosx.x, chunkPosx.z);

					for (ChunkSection chunkSection : chunkx.getSectionArray()) {
						chunkSection.getBiomeContainer().method_39793(registryEntry -> set.add((Biome)registryEntry.value()));
					}
				});
				set.retainAll((Collection)this.populationSource.getBiomes().map(RegistryEntry::value).collect(Collectors.toSet()));
			}

			int i = list.size();

			try {
				Registry<PlacedFeature> registry = world.getRegistryManager().get(Registry.PLACED_FEATURE_KEY);
				Registry<StructureFeature<?>> registry2 = world.getRegistryManager().get(Registry.STRUCTURE_FEATURE_KEY);
				int j = Math.max(GenerationStep.Feature.values().length, i);

				for (int k = 0; k < j; k++) {
					int m = 0;
					if (structureAccessor.shouldGenerateStructures()) {
						for (StructureFeature<?> structureFeature : (List)map.getOrDefault(k, Collections.emptyList())) {
							chunkRandom.setDecoratorSeed(l, m, k);
							Supplier<String> supplier = () -> (String)registry2.getKey(structureFeature).map(Object::toString).orElseGet(structureFeature::toString);

							try {
								world.setCurrentlyGeneratingStructureName(supplier);
								structureAccessor.getStructureStarts(chunkSectionPos, structureFeature)
									.forEach(structureStart -> structureStart.place(world, structureAccessor, this, chunkRandom, getBlockBoxForChunk(chunk), chunkPos));
							} catch (Exception var29) {
								CrashReport crashReport = CrashReport.create(var29, "Feature placement");
								crashReport.addElement("Feature").add("Description", supplier::get);
								throw new CrashException(crashReport);
							}

							m++;
						}
					}

					if (k < i) {
						IntSet intSet = new IntArraySet();

						for (Biome biome : set) {
							List<RegistryEntryList<PlacedFeature>> list3 = biome.getGenerationSettings().getFeatures();
							if (k < list3.size()) {
								RegistryEntryList<PlacedFeature> registryEntryList = (RegistryEntryList<PlacedFeature>)list3.get(k);
								BiomeSource.class_6827 lv = (BiomeSource.class_6827)list.get(k);
								registryEntryList.stream().map(RegistryEntry::value).forEach(placedFeaturex -> intSet.add(lv.indexMapping().applyAsInt(placedFeaturex)));
							}
						}

						int n = intSet.size();
						int[] is = intSet.toIntArray();
						Arrays.sort(is);
						BiomeSource.class_6827 lv2 = (BiomeSource.class_6827)list.get(k);

						for (int o = 0; o < n; o++) {
							int p = is[o];
							PlacedFeature placedFeature = (PlacedFeature)lv2.features().get(p);
							Supplier<String> supplier2 = () -> (String)registry.getKey(placedFeature).map(Object::toString).orElseGet(placedFeature::toString);
							chunkRandom.setDecoratorSeed(l, p, k);

							try {
								world.setCurrentlyGeneratingStructureName(supplier2);
								placedFeature.generate(world, this, chunkRandom, blockPos);
							} catch (Exception var30) {
								CrashReport crashReport2 = CrashReport.create(var30, "Feature placement");
								crashReport2.addElement("Feature").add("Description", supplier2::get);
								throw new CrashException(crashReport2);
							}
						}
					}
				}

				world.setCurrentlyGeneratingStructureName(null);
			} catch (Exception var31) {
				CrashReport crashReport3 = CrashReport.create(var31, "Biome decoration");
				crashReport3.addElement("Generation").add("CenterX", chunkPos.x).add("CenterZ", chunkPos.z).add("Seed", l);
				throw new CrashException(crashReport3);
			}
		}
	}

	private static BlockBox getBlockBoxForChunk(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		HeightLimitView heightLimitView = chunk.getHeightLimitView();
		int k = heightLimitView.getBottomY() + 1;
		int l = heightLimitView.getTopY() - 1;
		return new BlockBox(i, k, j, i + 15, l, j + 15);
	}

	/**
	 * Places the surface blocks of the biomes after the noise has been generated.
	 */
	public abstract void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk);

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

	public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(RegistryEntry<Biome> registryEntry, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
		return registryEntry.value().getSpawnSettings().getSpawnEntries(group);
	}

	/**
	 * Determines which structures should start in the given chunk and creates their starting points.
	 */
	public void setStructureStarts(
		DynamicRegistryManager registryManager, StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long worldSeed
	) {
		ChunkPos chunkPos = chunk.getPos();
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);
		Registry<ConfiguredStructureFeature<?, ?>> registry = registryManager.get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);

		label45:
		for (StructureFeature<?> structureFeature : Registry.STRUCTURE_FEATURE) {
			StructurePlacement structurePlacement = this.structuresConfig.getForType(structureFeature);
			if (structurePlacement != null) {
				StructureStart<?> structureStart = structureAccessor.getStructureStart(chunkSectionPos, structureFeature, chunk);
				if (structureStart == null || !structureStart.hasChildren()) {
					int i = getStructureReferences(structureAccessor, chunk, chunkSectionPos, structureFeature);
					if (structurePlacement.isStartChunk(this, chunkPos.x, chunkPos.z)) {
						for (Entry<RegistryKey<ConfiguredStructureFeature<?, ?>>, Collection<RegistryKey<Biome>>> entry : this.structuresConfig
							.getConfiguredStructureFeature(structureFeature)
							.asMap()
							.entrySet()) {
							Optional<ConfiguredStructureFeature<?, ?>> optional = registry.getOrEmpty((RegistryKey<ConfiguredStructureFeature<?, ?>>)entry.getKey());
							if (!optional.isEmpty()) {
								Predicate<RegistryKey<Biome>> predicate = Set.copyOf((Collection)entry.getValue())::contains;
								StructureStart<?> structureStart2 = ((ConfiguredStructureFeature)optional.get())
									.tryPlaceStart(
										registryManager,
										this,
										this.populationSource,
										structureManager,
										worldSeed,
										chunkPos,
										i,
										chunk,
										registryEntry -> this.method_40149(registryEntry).matches(predicate)
									);
								if (structureStart2.hasChildren()) {
									structureAccessor.setStructureStart(chunkSectionPos, structureFeature, structureStart2, chunk);
									continue label45;
								}
							}
						}
					}

					structureAccessor.setStructureStart(chunkSectionPos, structureFeature, StructureStart.DEFAULT, chunk);
				}
			}
		}
	}

	private static int getStructureReferences(StructureAccessor structureAccessor, Chunk chunk, ChunkSectionPos sectionPos, StructureFeature<?> structureFeature) {
		StructureStart<?> structureStart = structureAccessor.getStructureStart(sectionPos, structureFeature, chunk);
		return structureStart != null ? structureStart.getReferences() : 0;
	}

	protected RegistryEntry<Biome> method_40149(RegistryEntry<Biome> registryEntry) {
		return registryEntry;
	}

	/**
	 * Finds all structures that the given chunk intersects, and adds references to their starting chunks to it.
	 * A radius of 8 chunks around the given chunk will be searched for structure starts.
	 */
	public void addStructureReferences(StructureWorldAccess world, StructureAccessor structureAccessor, Chunk chunk) {
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
						if (structureStart.hasChildren() && structureStart.getBoundingBox().intersectsXZ(l, m, l + 15, m + 15)) {
							structureAccessor.addStructureReference(chunkSectionPos, structureStart.getFeature(), p, chunk);
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
	public abstract CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk);

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

	public List<ChunkPos> getConcentricRingsStartChunks(ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
		return (List<ChunkPos>)this.field_36405.get(concentricRingsStructurePlacement);
	}

	public long getSeed() {
		return this.field_36406;
	}

	static {
		Registry.register(Registry.CHUNK_GENERATOR, "noise", NoiseChunkGenerator.CODEC);
		Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatChunkGenerator.CODEC);
		Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugChunkGenerator.CODEC);
	}
}
