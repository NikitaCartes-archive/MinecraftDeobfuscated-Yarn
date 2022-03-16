package net.minecraft.world.gen.chunk;

import com.google.common.base.Stopwatch;
import com.mojang.datafixers.Products.P1;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.dynamic.RegistryOps;
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
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

/**
 * In charge of shaping, adding biome specific surface blocks, and carving chunks,
 * as well as populating the generated chunks with {@linkplain net.minecraft.world.gen.feature.Feature features} and {@linkplain net.minecraft.entity.Entity entities}.
 * Biome placement starts here, however all vanilla and most modded chunk generators delegate this to a {@linkplain net.minecraft.world.biome.source.BiomeSource biome source}.
 */
public abstract class ChunkGenerator {
	public static final int field_37658 = 8;
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<ChunkGenerator> CODEC = Registry.CHUNK_GENERATOR.getCodec().dispatchStable(ChunkGenerator::getCodec, Function.identity());
	protected final Registry<StructureSet> field_37053;
	/**
	 * Used to control the population step without replacing the actual biome that comes from the original {@link #biomeSource}.
	 * 
	 * <p>This is used by {@link FlatChunkGenerator} to overwrite biome properties like whether lakes generate, while preserving the original biome ID.
	 */
	protected final BiomeSource populationSource;
	protected final BiomeSource biomeSource;
	protected final Optional<RegistryEntryList<StructureSet>> field_37054;
	private final Map<StructureFeature, List<StructurePlacement>> field_37055 = new Object2ObjectOpenHashMap<>();
	private final Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> field_36405 = new Object2ObjectArrayMap<>();
	private boolean field_37056;

	protected static final <T extends ChunkGenerator> P1<Mu<T>, Registry<StructureSet>> method_41042(Instance<T> instance) {
		return instance.group(RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_KEY).forGetter(chunkGenerator -> chunkGenerator.field_37053));
	}

	public ChunkGenerator(Registry<StructureSet> registry, Optional<RegistryEntryList<StructureSet>> optional, BiomeSource biomeSource) {
		this(registry, optional, biomeSource, biomeSource);
	}

	public ChunkGenerator(Registry<StructureSet> registry, Optional<RegistryEntryList<StructureSet>> optional, BiomeSource biomeSource, BiomeSource biomeSource2) {
		this.field_37053 = registry;
		this.populationSource = biomeSource;
		this.biomeSource = biomeSource2;
		this.field_37054 = optional;
	}

	public Stream<RegistryEntry<StructureSet>> method_41039() {
		return this.field_37054.isPresent() ? ((RegistryEntryList)this.field_37054.get()).stream() : this.field_37053.streamEntries().map(RegistryEntry::upcast);
	}

	private void method_41057(NoiseConfig noiseConfig) {
		Set<RegistryEntry<Biome>> set = this.biomeSource.getBiomes();
		this.method_41039()
			.forEach(
				registryEntry -> {
					StructureSet structureSet = (StructureSet)registryEntry.value();

					for (StructureSet.WeightedEntry weightedEntry : structureSet.structures()) {
						((List)this.field_37055.computeIfAbsent(weightedEntry.structure().value(), structureFeature -> new ArrayList())).add(structureSet.placement());
					}

					if (structureSet.placement() instanceof ConcentricRingsStructurePlacement concentricRingsStructurePlacement
						&& set.stream().anyMatch(concentricRingsStructurePlacement.getBiasedToBiomes()::contains)) {
						this.field_36405.put(concentricRingsStructurePlacement, this.generateStrongholdPositions(registryEntry, noiseConfig, concentricRingsStructurePlacement));
					}
				}
			);
	}

	private CompletableFuture<List<ChunkPos>> generateStrongholdPositions(
		RegistryEntry<StructureSet> registryEntry, NoiseConfig noiseConfig, ConcentricRingsStructurePlacement concentricRingsStructurePlacement
	) {
		return concentricRingsStructurePlacement.getStructureCount() == 0
			? CompletableFuture.completedFuture(List.of())
			: CompletableFuture.supplyAsync(
				Util.debugSupplier(
					"placement calculation",
					() -> {
						Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
						List<ChunkPos> list = new ArrayList();
						int i = concentricRingsStructurePlacement.method_41627();
						int j = concentricRingsStructurePlacement.getStructureCount();
						int k = concentricRingsStructurePlacement.method_41628();
						RegistryEntryList<Biome> registryEntryList = concentricRingsStructurePlacement.getBiasedToBiomes();
						Random random = new Random();
						random.setSeed(this instanceof FlatChunkGenerator ? 0L : noiseConfig.legacyWorldSeed());
						double d = random.nextDouble() * Math.PI * 2.0;
						int l = 0;
						int m = 0;

						for (int n = 0; n < j; n++) {
							double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
							int o = (int)Math.round(Math.cos(d) * e);
							int p = (int)Math.round(Math.sin(d) * e);
							Pair<BlockPos, RegistryEntry<Biome>> pair = this.populationSource
								.locateBiome(ChunkSectionPos.getOffsetPos(o, 8), 0, ChunkSectionPos.getOffsetPos(p, 8), 112, registryEntryList::contains, random, noiseConfig.sampler());
							if (pair != null) {
								BlockPos blockPos = pair.getFirst();
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

						double f = (double)stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0;
						LOGGER.debug("Calculation for {} took {}s", registryEntry, f);
						return list;
					}
				),
				Util.getMainWorkerExecutor()
			);
	}

	protected abstract Codec<? extends ChunkGenerator> getCodec();

	public Optional<RegistryKey<Codec<? extends ChunkGenerator>>> getCodecKey() {
		return Registry.CHUNK_GENERATOR.getKey(this.getCodec());
	}

	public CompletableFuture<Chunk> populateBiomes(
		Registry<Biome> biomeRegistry, Executor executor, NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk
	) {
		return CompletableFuture.supplyAsync(Util.debugSupplier("init_biomes", () -> {
			chunk.populateBiomes(this.biomeSource, noiseConfig.sampler());
			return chunk;
		}), Util.getMainWorkerExecutor());
	}

	/**
	 * Generates caves for the given chunk.
	 */
	public abstract void carve(
		ChunkRegion chunkRegion,
		long seed,
		NoiseConfig noiseConfig,
		BiomeAccess biomeAccess,
		StructureAccessor structureAccessor,
		Chunk chunk,
		GenerationStep.Carver carver
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
	public Pair<BlockPos, RegistryEntry<StructureFeature>> locateStructure(
		ServerWorld serverWorld, RegistryEntryList<StructureFeature> registryEntryList, BlockPos center, int radius, boolean skipExistingChunks
	) {
		Set<RegistryEntry<Biome>> set = (Set<RegistryEntry<Biome>>)registryEntryList.stream()
			.flatMap(registryEntry -> ((StructureFeature)registryEntry.value()).getValidBiomes().stream())
			.collect(Collectors.toSet());
		if (set.isEmpty()) {
			return null;
		} else {
			Set<RegistryEntry<Biome>> set2 = this.biomeSource.getBiomes();
			if (Collections.disjoint(set2, set)) {
				return null;
			} else {
				Pair<BlockPos, RegistryEntry<StructureFeature>> pair = null;
				double d = Double.MAX_VALUE;
				Map<StructurePlacement, Set<RegistryEntry<StructureFeature>>> map = new Object2ObjectArrayMap<>();

				for (RegistryEntry<StructureFeature> registryEntry : registryEntryList) {
					if (!set2.stream().noneMatch(registryEntry.value().getValidBiomes()::contains)) {
						for (StructurePlacement structurePlacement : this.method_41055(registryEntry, serverWorld.getChunkManager().getNoiseConfig())) {
							((Set)map.computeIfAbsent(structurePlacement, structurePlacementx -> new ObjectArraySet())).add(registryEntry);
						}
					}
				}

				StructureAccessor structureAccessor = serverWorld.getStructureAccessor();
				List<Entry<StructurePlacement, Set<RegistryEntry<StructureFeature>>>> list = new ArrayList(map.size());

				for (Entry<StructurePlacement, Set<RegistryEntry<StructureFeature>>> entry : map.entrySet()) {
					StructurePlacement structurePlacement2 = (StructurePlacement)entry.getKey();
					if (structurePlacement2 instanceof ConcentricRingsStructurePlacement) {
						ConcentricRingsStructurePlacement concentricRingsStructurePlacement = (ConcentricRingsStructurePlacement)structurePlacement2;
						Pair<BlockPos, RegistryEntry<StructureFeature>> pair2 = this.method_40148(
							(Set<RegistryEntry<StructureFeature>>)entry.getValue(), serverWorld, structureAccessor, center, skipExistingChunks, concentricRingsStructurePlacement
						);
						BlockPos blockPos = pair2.getFirst();
						double e = center.getSquaredDistance(blockPos);
						if (e < d) {
							d = e;
							pair = pair2;
						}
					} else if (structurePlacement2 instanceof RandomSpreadStructurePlacement) {
						list.add(entry);
					}
				}

				if (!list.isEmpty()) {
					int i = ChunkSectionPos.getSectionCoord(center.getX());
					int j = ChunkSectionPos.getSectionCoord(center.getZ());

					for (int k = 0; k <= radius; k++) {
						boolean bl = false;

						for (Entry<StructurePlacement, Set<RegistryEntry<StructureFeature>>> entry2 : list) {
							RandomSpreadStructurePlacement randomSpreadStructurePlacement = (RandomSpreadStructurePlacement)entry2.getKey();
							Pair<BlockPos, RegistryEntry<StructureFeature>> pair3 = method_40146(
								(Set<RegistryEntry<StructureFeature>>)entry2.getValue(),
								serverWorld,
								structureAccessor,
								i,
								j,
								k,
								skipExistingChunks,
								serverWorld.getSeed(),
								randomSpreadStructurePlacement
							);
							if (pair3 != null) {
								bl = true;
								double f = center.getSquaredDistance(pair3.getFirst());
								if (f < d) {
									d = f;
									pair = pair3;
								}
							}
						}

						if (bl) {
							return pair;
						}
					}
				}

				return pair;
			}
		}
	}

	@Nullable
	private Pair<BlockPos, RegistryEntry<StructureFeature>> method_40148(
		Set<RegistryEntry<StructureFeature>> set,
		ServerWorld serverWorld,
		StructureAccessor structureAccessor,
		BlockPos blockPos,
		boolean bl,
		ConcentricRingsStructurePlacement concentricRingsStructurePlacement
	) {
		List<ChunkPos> list = this.getConcentricRingsStartChunks(concentricRingsStructurePlacement, serverWorld.getChunkManager().getNoiseConfig());
		if (list == null) {
			throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
		} else {
			Pair<BlockPos, RegistryEntry<StructureFeature>> pair = null;
			double d = Double.MAX_VALUE;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (ChunkPos chunkPos : list) {
				mutable.set(ChunkSectionPos.getOffsetPos(chunkPos.x, 8), 32, ChunkSectionPos.getOffsetPos(chunkPos.z, 8));
				double e = mutable.getSquaredDistance(blockPos);
				boolean bl2 = pair == null || e < d;
				if (bl2) {
					Pair<BlockPos, RegistryEntry<StructureFeature>> pair2 = method_41522(set, serverWorld, structureAccessor, bl, concentricRingsStructurePlacement, chunkPos);
					if (pair2 != null) {
						pair = pair2;
						d = e;
					}
				}
			}

			return pair;
		}
	}

	@Nullable
	private static Pair<BlockPos, RegistryEntry<StructureFeature>> method_40146(
		Set<RegistryEntry<StructureFeature>> set,
		WorldView worldView,
		StructureAccessor structureAccessor,
		int i,
		int j,
		int k,
		boolean bl,
		long l,
		RandomSpreadStructurePlacement randomSpreadStructurePlacement
	) {
		int m = randomSpreadStructurePlacement.method_41632();

		for (int n = -k; n <= k; n++) {
			boolean bl2 = n == -k || n == k;

			for (int o = -k; o <= k; o++) {
				boolean bl3 = o == -k || o == k;
				if (bl2 || bl3) {
					int p = i + m * n;
					int q = j + m * o;
					ChunkPos chunkPos = randomSpreadStructurePlacement.getStartChunk(l, p, q);
					Pair<BlockPos, RegistryEntry<StructureFeature>> pair = method_41522(set, worldView, structureAccessor, bl, randomSpreadStructurePlacement, chunkPos);
					if (pair != null) {
						return pair;
					}
				}
			}
		}

		return null;
	}

	@Nullable
	private static Pair<BlockPos, RegistryEntry<StructureFeature>> method_41522(
		Set<RegistryEntry<StructureFeature>> set,
		WorldView worldView,
		StructureAccessor structureAccessor,
		boolean bl,
		StructurePlacement structurePlacement,
		ChunkPos chunkPos
	) {
		for (RegistryEntry<StructureFeature> registryEntry : set) {
			StructurePresence structurePresence = structureAccessor.getStructurePresence(chunkPos, registryEntry.value(), bl);
			if (structurePresence != StructurePresence.START_NOT_PRESENT) {
				if (!bl && structurePresence == StructurePresence.START_PRESENT) {
					return Pair.of(structurePlacement.method_41636(chunkPos), registryEntry);
				}

				Chunk chunk = worldView.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);
				StructureStart structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk), registryEntry.value(), chunk);
				if (structureStart != null && structureStart.hasChildren() && (!bl || method_41521(structureAccessor, structureStart))) {
					return Pair.of(structurePlacement.method_41636(structureStart.getPos()), registryEntry);
				}
			}
		}

		return null;
	}

	private static boolean method_41521(StructureAccessor structureAccessor, StructureStart structureStart) {
		if (structureStart.isInExistingChunk()) {
			structureAccessor.incrementReferences(structureStart);
			return true;
		} else {
			return false;
		}
	}

	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
		ChunkPos chunkPos = chunk.getPos();
		if (!SharedConstants.method_37896(chunkPos)) {
			ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, world.getBottomSectionCoord());
			BlockPos blockPos = chunkSectionPos.getMinPos();
			Registry<StructureFeature> registry = world.getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
			Map<Integer, List<StructureFeature>> map = (Map<Integer, List<StructureFeature>>)registry.stream()
				.collect(Collectors.groupingBy(structureFeature -> structureFeature.getFeatureGenerationStep().ordinal()));
			List<BiomeSource.IndexedFeatures> list = this.populationSource.getIndexedFeatures();
			ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));
			long l = chunkRandom.setPopulationSeed(world.getSeed(), blockPos.getX(), blockPos.getZ());
			Set<Biome> set = new ObjectArraySet<>();
			if (this instanceof FlatChunkGenerator) {
				this.populationSource.getBiomes().stream().map(RegistryEntry::value).forEach(set::add);
			} else {
				ChunkPos.stream(chunkSectionPos.toChunkPos(), 1).forEach(chunkPosx -> {
					Chunk chunkx = world.getChunk(chunkPosx.x, chunkPosx.z);

					for (ChunkSection chunkSection : chunkx.getSectionArray()) {
						chunkSection.getBiomeContainer().method_39793(registryEntry -> set.add((Biome)registryEntry.value()));
					}
				});
				set.retainAll((Collection)this.populationSource.getBiomes().stream().map(RegistryEntry::value).collect(Collectors.toSet()));
			}

			int i = list.size();

			try {
				Registry<PlacedFeature> registry2 = world.getRegistryManager().get(Registry.PLACED_FEATURE_KEY);
				int j = Math.max(GenerationStep.Feature.values().length, i);

				for (int k = 0; k < j; k++) {
					int m = 0;
					if (structureAccessor.shouldGenerateStructures()) {
						for (StructureFeature structureFeature : (List)map.getOrDefault(k, Collections.emptyList())) {
							chunkRandom.setDecoratorSeed(l, m, k);
							Supplier<String> supplier = () -> (String)registry.getKey(structureFeature).map(Object::toString).orElseGet(structureFeature::toString);

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
								BiomeSource.IndexedFeatures indexedFeatures = (BiomeSource.IndexedFeatures)list.get(k);
								registryEntryList.stream().map(RegistryEntry::value).forEach(placedFeaturex -> intSet.add(indexedFeatures.indexMapping().applyAsInt(placedFeaturex)));
							}
						}

						int n = intSet.size();
						int[] is = intSet.toIntArray();
						Arrays.sort(is);
						BiomeSource.IndexedFeatures indexedFeatures2 = (BiomeSource.IndexedFeatures)list.get(k);

						for (int o = 0; o < n; o++) {
							int p = is[o];
							PlacedFeature placedFeature = (PlacedFeature)indexedFeatures2.features().get(p);
							Supplier<String> supplier2 = () -> (String)registry2.getKey(placedFeature).map(Object::toString).orElseGet(placedFeature::toString);
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

	public boolean method_41053(RegistryEntry<StructureSet> registryEntry, NoiseConfig noiseConfig, long l, int i, int j, int k) {
		StructureSet structureSet = registryEntry.value();
		if (structureSet == null) {
			return false;
		} else {
			StructurePlacement structurePlacement = structureSet.placement();

			for (int m = i - k; m <= i + k; m++) {
				for (int n = j - k; n <= j + k; n++) {
					if (structurePlacement.method_41639(this, noiseConfig, l, m, n)) {
						return true;
					}
				}
			}

			return false;
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
	public abstract void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk);

	public abstract void populateEntities(ChunkRegion region);

	public int getSpawnHeight(HeightLimitView world) {
		return 64;
	}

	public BiomeSource getBiomeSource() {
		return this.biomeSource;
	}

	public abstract int getWorldHeight();

	public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(RegistryEntry<Biome> biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
		Map<StructureFeature, LongSet> map = accessor.method_41037(pos);

		for (Entry<StructureFeature, LongSet> entry : map.entrySet()) {
			StructureFeature structureFeature = (StructureFeature)entry.getKey();
			StructureSpawns structureSpawns = (StructureSpawns)structureFeature.getStructureSpawns().get(group);
			if (structureSpawns != null) {
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				Predicate<StructureStart> predicate = structureSpawns.boundingBox() == StructureSpawns.BoundingBox.PIECE
					? structureStart -> accessor.structureContains(pos, structureStart)
					: structureStart -> structureStart.getBoundingBox().contains(pos);
				accessor.method_41032(structureFeature, (LongSet)entry.getValue(), structureStart -> {
					if (mutableBoolean.isFalse() && predicate.test(structureStart)) {
						mutableBoolean.setTrue();
					}
				});
				if (mutableBoolean.isTrue()) {
					return structureSpawns.spawns();
				}
			}
		}

		return biome.value().getSpawnSettings().getSpawnEntries(group);
	}

	/**
	 * Determines which structures should start in the given chunk and creates their starting points.
	 */
	public void setStructureStarts(
		DynamicRegistryManager registryManager, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk, StructureManager structureManager, long l
	) {
		ChunkPos chunkPos = chunk.getPos();
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);
		this.method_41039()
			.forEach(
				registryEntry -> {
					StructurePlacement structurePlacement = ((StructureSet)registryEntry.value()).placement();
					List<StructureSet.WeightedEntry> list = ((StructureSet)registryEntry.value()).structures();

					for (StructureSet.WeightedEntry weightedEntry : list) {
						StructureStart structureStart = structureAccessor.getStructureStart(chunkSectionPos, weightedEntry.structure().value(), chunk);
						if (structureStart != null && structureStart.hasChildren()) {
							return;
						}
					}

					if (structurePlacement.method_41639(this, noiseConfig, l, chunkPos.x, chunkPos.z)) {
						if (list.size() == 1) {
							this.method_41044(
								(StructureSet.WeightedEntry)list.get(0), structureAccessor, registryManager, noiseConfig, structureManager, l, chunk, chunkPos, chunkSectionPos
							);
						} else {
							ArrayList<StructureSet.WeightedEntry> arrayList = new ArrayList(list.size());
							arrayList.addAll(list);
							ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
							chunkRandom.setCarverSeed(l, chunkPos.x, chunkPos.z);
							int i = 0;

							for (StructureSet.WeightedEntry weightedEntry2 : arrayList) {
								i += weightedEntry2.weight();
							}

							while (!arrayList.isEmpty()) {
								int j = chunkRandom.nextInt(i);
								int k = 0;

								for (StructureSet.WeightedEntry weightedEntry3 : arrayList) {
									j -= weightedEntry3.weight();
									if (j < 0) {
										break;
									}

									k++;
								}

								StructureSet.WeightedEntry weightedEntry4 = (StructureSet.WeightedEntry)arrayList.get(k);
								if (this.method_41044(weightedEntry4, structureAccessor, registryManager, noiseConfig, structureManager, l, chunk, chunkPos, chunkSectionPos)) {
									return;
								}

								arrayList.remove(k);
								i -= weightedEntry4.weight();
							}
						}
					}
				}
			);
	}

	private boolean method_41044(
		StructureSet.WeightedEntry weightedEntry,
		StructureAccessor structureAccessor,
		DynamicRegistryManager dynamicRegistryManager,
		NoiseConfig noiseConfig,
		StructureManager structureManager,
		long l,
		Chunk chunk,
		ChunkPos chunkPos,
		ChunkSectionPos chunkSectionPos
	) {
		StructureFeature structureFeature = weightedEntry.structure().value();
		int i = getStructureReferences(structureAccessor, chunk, chunkSectionPos, structureFeature);
		RegistryEntryList<Biome> registryEntryList = structureFeature.getValidBiomes();
		Predicate<RegistryEntry<Biome>> predicate = registryEntry -> registryEntryList.contains(this.filterBiome(registryEntry));
		StructureStart structureStart = structureFeature.method_41614(
			dynamicRegistryManager, this, this.populationSource, noiseConfig, structureManager, l, chunkPos, i, chunk, predicate
		);
		if (structureStart.hasChildren()) {
			structureAccessor.setStructureStart(chunkSectionPos, structureFeature, structureStart, chunk);
			return true;
		} else {
			return false;
		}
	}

	private static int getStructureReferences(StructureAccessor structureAccessor, Chunk chunk, ChunkSectionPos sectionPos, StructureFeature structureFeature) {
		StructureStart structureStart = structureAccessor.getStructureStart(sectionPos, structureFeature, chunk);
		return structureStart != null ? structureStart.getReferences() : 0;
	}

	protected RegistryEntry<Biome> filterBiome(RegistryEntry<Biome> biome) {
		return biome;
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

				for (StructureStart structureStart : world.getChunk(n, o).getStructureStarts().values()) {
					try {
						if (structureStart.hasChildren() && structureStart.getBoundingBox().intersectsXZ(l, m, l + 15, m + 15)) {
							structureAccessor.addStructureReference(chunkSectionPos, structureStart.getFeature(), p, chunk);
							DebugInfoSender.sendStructureStart(world, structureStart);
						}
					} catch (Exception var21) {
						CrashReport crashReport = CrashReport.create(var21, "Generating structure reference");
						CrashReportSection crashReportSection = crashReport.addElement("Structure");
						Optional<? extends Registry<StructureFeature>> optional = world.getRegistryManager().getOptional(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
						crashReportSection.add(
							"Id", (CrashCallable<String>)(() -> (String)optional.map(registry -> registry.getId(structureStart.getFeature()).toString()).orElse("UNKNOWN"))
						);
						crashReportSection.add("Name", (CrashCallable<String>)(() -> Registry.STRUCTURE_TYPE.getId(structureStart.getFeature().getType()).toString()));
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
	public abstract CompletableFuture<Chunk> populateNoise(
		Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk
	);

	public abstract int getSeaLevel();

	public abstract int getMinimumY();

	/**
	 * Returns the raw noise height of a column for use in structure generation.
	 */
	public abstract int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig);

	/**
	 * Returns a sample of all the block states in a column for use in structure generation.
	 */
	public abstract VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig);

	public int getHeightOnGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
		return this.getHeight(x, z, heightmap, world, noiseConfig);
	}

	public int getHeightInGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
		return this.getHeight(x, z, heightmap, world, noiseConfig) - 1;
	}

	public void method_41058(NoiseConfig noiseConfig) {
		if (!this.field_37056) {
			this.method_41057(noiseConfig);
			this.field_37056 = true;
		}
	}

	@Nullable
	public List<ChunkPos> getConcentricRingsStartChunks(ConcentricRingsStructurePlacement structurePlacement, NoiseConfig noiseConfig) {
		this.method_41058(noiseConfig);
		CompletableFuture<List<ChunkPos>> completableFuture = (CompletableFuture<List<ChunkPos>>)this.field_36405.get(structurePlacement);
		return completableFuture != null ? (List)completableFuture.join() : null;
	}

	private List<StructurePlacement> method_41055(RegistryEntry<StructureFeature> registryEntry, NoiseConfig noiseConfig) {
		this.method_41058(noiseConfig);
		return (List<StructurePlacement>)this.field_37055.getOrDefault(registryEntry.value(), List.of());
	}

	public abstract void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos blockPos);

	static {
		Registry.register(Registry.CHUNK_GENERATOR, "noise", NoiseChunkGenerator.CODEC);
		Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatChunkGenerator.CODEC);
		Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugChunkGenerator.CODEC);
	}
}
