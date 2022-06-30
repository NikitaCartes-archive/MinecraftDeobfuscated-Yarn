package net.minecraft.world.gen.chunk;

import com.google.common.base.Stopwatch;
import com.google.common.base.Suppliers;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplateManager;
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
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
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
import net.minecraft.world.biome.GenerationSettings;
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
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

/**
 * In charge of shaping, adding biome specific surface blocks, and carving chunks,
 * as well as populating the generated chunks with {@linkplain net.minecraft.world.gen.feature.Feature features} and {@linkplain net.minecraft.entity.Entity entities}.
 * Biome placement starts here, however all vanilla and most modded chunk generators delegate this to a {@linkplain net.minecraft.world.biome.source.BiomeSource biome source}.
 */
public abstract class ChunkGenerator {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<ChunkGenerator> CODEC = Registry.CHUNK_GENERATOR.getCodec().dispatchStable(ChunkGenerator::getCodec, Function.identity());
	protected final Registry<StructureSet> structureSetRegistry;
	protected final BiomeSource biomeSource;
	private final Supplier<List<PlacedFeatureIndexer.IndexedFeatures>> indexedFeaturesListSupplier;
	protected final Optional<RegistryEntryList<StructureSet>> structureOverrides;
	private final Function<RegistryEntry<Biome>, GenerationSettings> generationSettingsGetter;
	private final Map<Structure, List<StructurePlacement>> structurePlacements = new Object2ObjectOpenHashMap<>();
	private final Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> concentricRingPositions = new Object2ObjectArrayMap<>();
	private boolean hasComputedStructurePlacements;

	protected static <T extends ChunkGenerator> P1<Mu<T>, Registry<StructureSet>> createStructureSetRegistryGetter(Instance<T> instance) {
		return instance.group(RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_KEY).forGetter(chunkGenerator -> chunkGenerator.structureSetRegistry));
	}

	public ChunkGenerator(Registry<StructureSet> structureSetRegistry, Optional<RegistryEntryList<StructureSet>> structureOverrides, BiomeSource biomeSource) {
		this(structureSetRegistry, structureOverrides, biomeSource, biomeEntry -> ((Biome)biomeEntry.value()).getGenerationSettings());
	}

	public ChunkGenerator(
		Registry<StructureSet> structureSetRegistry,
		Optional<RegistryEntryList<StructureSet>> structureOverrides,
		BiomeSource biomeSource,
		Function<RegistryEntry<Biome>, GenerationSettings> generationSettingsGetter
	) {
		this.structureSetRegistry = structureSetRegistry;
		this.biomeSource = biomeSource;
		this.generationSettingsGetter = generationSettingsGetter;
		this.structureOverrides = structureOverrides;
		this.indexedFeaturesListSupplier = Suppliers.memoize(
			() -> PlacedFeatureIndexer.collectIndexedFeatures(
					List.copyOf(biomeSource.getBiomes()), biomeEntry -> ((GenerationSettings)generationSettingsGetter.apply(biomeEntry)).getFeatures(), true
				)
		);
	}

	public Stream<RegistryEntry<StructureSet>> streamStructureSets() {
		return this.structureOverrides.isPresent()
			? ((RegistryEntryList)this.structureOverrides.get()).stream()
			: this.structureSetRegistry.streamEntries().map(RegistryEntry::upcast);
	}

	private void computeStructurePlacements(NoiseConfig noiseConfig) {
		Set<RegistryEntry<Biome>> set = this.biomeSource.getBiomes();
		this.streamStructureSets()
			.forEach(
				structureSet -> {
					StructureSet structureSet2 = (StructureSet)structureSet.value();
					boolean bl = false;

					for (StructureSet.WeightedEntry weightedEntry : structureSet2.structures()) {
						Structure structure = weightedEntry.structure().value();
						if (structure.getValidBiomes().stream().anyMatch(set::contains)) {
							((List)this.structurePlacements.computeIfAbsent(structure, structureType -> new ArrayList())).add(structureSet2.placement());
							bl = true;
						}
					}

					if (bl && structureSet2.placement() instanceof ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
						this.concentricRingPositions
							.put(concentricRingsStructurePlacement, this.generateConcentricRingPositions(structureSet, noiseConfig, concentricRingsStructurePlacement));
					}
				}
			);
	}

	private CompletableFuture<List<ChunkPos>> generateConcentricRingPositions(
		RegistryEntry<StructureSet> structureSet, NoiseConfig noiseConfig, ConcentricRingsStructurePlacement concentricRingsStructurePlacement
	) {
		return concentricRingsStructurePlacement.getCount() == 0
			? CompletableFuture.completedFuture(List.of())
			: CompletableFuture.supplyAsync(
				Util.debugSupplier(
					"placement calculation",
					() -> {
						Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
						List<ChunkPos> list = new ArrayList();
						int i = concentricRingsStructurePlacement.getDistance();
						int j = concentricRingsStructurePlacement.getCount();
						int k = concentricRingsStructurePlacement.getSpread();
						RegistryEntryList<Biome> registryEntryList = concentricRingsStructurePlacement.getPreferredBiomes();
						Random random = Random.create();
						random.setSeed(this instanceof FlatChunkGenerator ? 0L : noiseConfig.getLegacyWorldSeed());
						double d = random.nextDouble() * Math.PI * 2.0;
						int l = 0;
						int m = 0;

						for (int n = 0; n < j; n++) {
							double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
							int o = (int)Math.round(Math.cos(d) * e);
							int p = (int)Math.round(Math.sin(d) * e);
							Pair<BlockPos, RegistryEntry<Biome>> pair = this.biomeSource
								.locateBiome(
									ChunkSectionPos.getOffsetPos(o, 8),
									0,
									ChunkSectionPos.getOffsetPos(p, 8),
									112,
									registryEntryList::contains,
									random,
									noiseConfig.getMultiNoiseSampler()
								);
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
						LOGGER.debug("Calculation for {} took {}s", structureSet, f);
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
			chunk.populateBiomes(this.biomeSource, noiseConfig.getMultiNoiseSampler());
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
		BiomeAccess world,
		StructureAccessor structureAccessor,
		Chunk chunk,
		GenerationStep.Carver carverStep
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
	 * @param skipReferencedStructures whether to exclude structures that were previously located (has positive
	 * {@link StructureStart#references})
	 */
	@Nullable
	public Pair<BlockPos, RegistryEntry<Structure>> locateStructure(
		ServerWorld world, RegistryEntryList<Structure> structures, BlockPos center, int radius, boolean skipReferencedStructures
	) {
		Map<StructurePlacement, Set<RegistryEntry<Structure>>> map = new Object2ObjectArrayMap<>();

		for (RegistryEntry<Structure> registryEntry : structures) {
			for (StructurePlacement structurePlacement : this.getStructurePlacement(registryEntry, world.getChunkManager().getNoiseConfig())) {
				((Set)map.computeIfAbsent(structurePlacement, placement -> new ObjectArraySet())).add(registryEntry);
			}
		}

		if (map.isEmpty()) {
			return null;
		} else {
			Pair<BlockPos, RegistryEntry<Structure>> pair = null;
			double d = Double.MAX_VALUE;
			StructureAccessor structureAccessor = world.getStructureAccessor();
			List<Entry<StructurePlacement, Set<RegistryEntry<Structure>>>> list = new ArrayList(map.size());

			for (Entry<StructurePlacement, Set<RegistryEntry<Structure>>> entry : map.entrySet()) {
				StructurePlacement structurePlacement2 = (StructurePlacement)entry.getKey();
				if (structurePlacement2 instanceof ConcentricRingsStructurePlacement) {
					ConcentricRingsStructurePlacement concentricRingsStructurePlacement = (ConcentricRingsStructurePlacement)structurePlacement2;
					Pair<BlockPos, RegistryEntry<Structure>> pair2 = this.locateConcentricRingsStructure(
						(Set<RegistryEntry<Structure>>)entry.getValue(), world, structureAccessor, center, skipReferencedStructures, concentricRingsStructurePlacement
					);
					if (pair2 != null) {
						BlockPos blockPos = pair2.getFirst();
						double e = center.getSquaredDistance(blockPos);
						if (e < d) {
							d = e;
							pair = pair2;
						}
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

					for (Entry<StructurePlacement, Set<RegistryEntry<Structure>>> entry2 : list) {
						RandomSpreadStructurePlacement randomSpreadStructurePlacement = (RandomSpreadStructurePlacement)entry2.getKey();
						Pair<BlockPos, RegistryEntry<Structure>> pair3 = locateRandomSpreadStructure(
							(Set<RegistryEntry<Structure>>)entry2.getValue(),
							world,
							structureAccessor,
							i,
							j,
							k,
							skipReferencedStructures,
							world.getSeed(),
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

	@Nullable
	private Pair<BlockPos, RegistryEntry<Structure>> locateConcentricRingsStructure(
		Set<RegistryEntry<Structure>> structures,
		ServerWorld world,
		StructureAccessor structureAccessor,
		BlockPos center,
		boolean skipReferencedStructures,
		ConcentricRingsStructurePlacement placement
	) {
		List<ChunkPos> list = this.getConcentricRingsStartChunks(placement, world.getChunkManager().getNoiseConfig());
		if (list == null) {
			throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
		} else {
			Pair<BlockPos, RegistryEntry<Structure>> pair = null;
			double d = Double.MAX_VALUE;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (ChunkPos chunkPos : list) {
				mutable.set(ChunkSectionPos.getOffsetPos(chunkPos.x, 8), 32, ChunkSectionPos.getOffsetPos(chunkPos.z, 8));
				double e = mutable.getSquaredDistance(center);
				boolean bl = pair == null || e < d;
				if (bl) {
					Pair<BlockPos, RegistryEntry<Structure>> pair2 = locateStructure(structures, world, structureAccessor, skipReferencedStructures, placement, chunkPos);
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
	private static Pair<BlockPos, RegistryEntry<Structure>> locateRandomSpreadStructure(
		Set<RegistryEntry<Structure>> structures,
		WorldView world,
		StructureAccessor structureAccessor,
		int centerChunkX,
		int centerChunkZ,
		int radius,
		boolean skipReferencedStructures,
		long seed,
		RandomSpreadStructurePlacement placement
	) {
		int i = placement.getSpacing();

		for (int j = -radius; j <= radius; j++) {
			boolean bl = j == -radius || j == radius;

			for (int k = -radius; k <= radius; k++) {
				boolean bl2 = k == -radius || k == radius;
				if (bl || bl2) {
					int l = centerChunkX + i * j;
					int m = centerChunkZ + i * k;
					ChunkPos chunkPos = placement.getStartChunk(seed, l, m);
					Pair<BlockPos, RegistryEntry<Structure>> pair = locateStructure(structures, world, structureAccessor, skipReferencedStructures, placement, chunkPos);
					if (pair != null) {
						return pair;
					}
				}
			}
		}

		return null;
	}

	@Nullable
	private static Pair<BlockPos, RegistryEntry<Structure>> locateStructure(
		Set<RegistryEntry<Structure>> structures,
		WorldView world,
		StructureAccessor structureAccessor,
		boolean skipReferencedStructures,
		StructurePlacement placement,
		ChunkPos pos
	) {
		for (RegistryEntry<Structure> registryEntry : structures) {
			StructurePresence structurePresence = structureAccessor.getStructurePresence(pos, registryEntry.value(), skipReferencedStructures);
			if (structurePresence != StructurePresence.START_NOT_PRESENT) {
				if (!skipReferencedStructures && structurePresence == StructurePresence.START_PRESENT) {
					return Pair.of(placement.getLocatePos(pos), registryEntry);
				}

				Chunk chunk = world.getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS);
				StructureStart structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk), registryEntry.value(), chunk);
				if (structureStart != null && structureStart.hasChildren() && (!skipReferencedStructures || checkNotReferenced(structureAccessor, structureStart))) {
					return Pair.of(placement.getLocatePos(structureStart.getPos()), registryEntry);
				}
			}
		}

		return null;
	}

	private static boolean checkNotReferenced(StructureAccessor structureAccessor, StructureStart start) {
		if (start.isNeverReferenced()) {
			structureAccessor.incrementReferences(start);
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
			Registry<Structure> registry = world.getRegistryManager().get(Registry.STRUCTURE_KEY);
			Map<Integer, List<Structure>> map = (Map<Integer, List<Structure>>)registry.stream()
				.collect(Collectors.groupingBy(structureType -> structureType.getFeatureGenerationStep().ordinal()));
			List<PlacedFeatureIndexer.IndexedFeatures> list = (List<PlacedFeatureIndexer.IndexedFeatures>)this.indexedFeaturesListSupplier.get();
			ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));
			long l = chunkRandom.setPopulationSeed(world.getSeed(), blockPos.getX(), blockPos.getZ());
			Set<RegistryEntry<Biome>> set = new ObjectArraySet<>();
			ChunkPos.stream(chunkSectionPos.toChunkPos(), 1).forEach(chunkPosx -> {
				Chunk chunkx = world.getChunk(chunkPosx.x, chunkPosx.z);

				for (ChunkSection chunkSection : chunkx.getSectionArray()) {
					chunkSection.getBiomeContainer().forEachValue(set::add);
				}
			});
			set.retainAll(this.biomeSource.getBiomes());
			int i = list.size();

			try {
				Registry<PlacedFeature> registry2 = world.getRegistryManager().get(Registry.PLACED_FEATURE_KEY);
				int j = Math.max(GenerationStep.Feature.values().length, i);

				for (int k = 0; k < j; k++) {
					int m = 0;
					if (structureAccessor.shouldGenerateStructures()) {
						for (Structure structure : (List)map.getOrDefault(k, Collections.emptyList())) {
							chunkRandom.setDecoratorSeed(l, m, k);
							Supplier<String> supplier = () -> (String)registry.getKey(structure).map(Object::toString).orElseGet(structure::toString);

							try {
								world.setCurrentlyGeneratingStructureName(supplier);
								structureAccessor.getStructureStarts(chunkSectionPos, structure)
									.forEach(start -> start.place(world, structureAccessor, this, chunkRandom, getBlockBoxForChunk(chunk), chunkPos));
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

						for (RegistryEntry<Biome> registryEntry : set) {
							List<RegistryEntryList<PlacedFeature>> list3 = ((GenerationSettings)this.generationSettingsGetter.apply(registryEntry)).getFeatures();
							if (k < list3.size()) {
								RegistryEntryList<PlacedFeature> registryEntryList = (RegistryEntryList<PlacedFeature>)list3.get(k);
								PlacedFeatureIndexer.IndexedFeatures indexedFeatures = (PlacedFeatureIndexer.IndexedFeatures)list.get(k);
								registryEntryList.stream().map(RegistryEntry::value).forEach(placedFeaturex -> intSet.add(indexedFeatures.indexMapping().applyAsInt(placedFeaturex)));
							}
						}

						int n = intSet.size();
						int[] is = intSet.toIntArray();
						Arrays.sort(is);
						PlacedFeatureIndexer.IndexedFeatures indexedFeatures2 = (PlacedFeatureIndexer.IndexedFeatures)list.get(k);

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

	public boolean shouldStructureGenerateInRange(
		RegistryEntry<StructureSet> structureSet, NoiseConfig noiseConfig, long seed, int chunkX, int chunkZ, int chunkRange
	) {
		StructureSet structureSet2 = structureSet.value();
		if (structureSet2 == null) {
			return false;
		} else {
			StructurePlacement structurePlacement = structureSet2.placement();

			for (int i = chunkX - chunkRange; i <= chunkX + chunkRange; i++) {
				for (int j = chunkZ - chunkRange; j <= chunkZ + chunkRange; j++) {
					if (structurePlacement.shouldGenerate(this, noiseConfig, seed, i, j)) {
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
		Map<Structure, LongSet> map = accessor.getStructureReferences(pos);

		for (Entry<Structure, LongSet> entry : map.entrySet()) {
			Structure structure = (Structure)entry.getKey();
			StructureSpawns structureSpawns = (StructureSpawns)structure.getStructureSpawns().get(group);
			if (structureSpawns != null) {
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				Predicate<StructureStart> predicate = structureSpawns.boundingBox() == StructureSpawns.BoundingBox.PIECE
					? start -> accessor.structureContains(pos, start)
					: start -> start.getBoundingBox().contains(pos);
				accessor.method_41032(structure, (LongSet)entry.getValue(), start -> {
					if (mutableBoolean.isFalse() && predicate.test(start)) {
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
		DynamicRegistryManager registryManager,
		NoiseConfig noiseConfig,
		StructureAccessor structureAccessor,
		Chunk chunk,
		StructureTemplateManager structureTemplateManager,
		long seed
	) {
		ChunkPos chunkPos = chunk.getPos();
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);
		this.streamStructureSets()
			.forEach(
				structureSet -> {
					StructurePlacement structurePlacement = ((StructureSet)structureSet.value()).placement();
					List<StructureSet.WeightedEntry> list = ((StructureSet)structureSet.value()).structures();

					for (StructureSet.WeightedEntry weightedEntry : list) {
						StructureStart structureStart = structureAccessor.getStructureStart(chunkSectionPos, weightedEntry.structure().value(), chunk);
						if (structureStart != null && structureStart.hasChildren()) {
							return;
						}
					}

					if (structurePlacement.shouldGenerate(this, noiseConfig, seed, chunkPos.x, chunkPos.z)) {
						if (list.size() == 1) {
							this.trySetStructureStart(
								(StructureSet.WeightedEntry)list.get(0),
								structureAccessor,
								registryManager,
								noiseConfig,
								structureTemplateManager,
								seed,
								chunk,
								chunkPos,
								chunkSectionPos
							);
						} else {
							ArrayList<StructureSet.WeightedEntry> arrayList = new ArrayList(list.size());
							arrayList.addAll(list);
							ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
							chunkRandom.setCarverSeed(seed, chunkPos.x, chunkPos.z);
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
								if (this.trySetStructureStart(
									weightedEntry4, structureAccessor, registryManager, noiseConfig, structureTemplateManager, seed, chunk, chunkPos, chunkSectionPos
								)) {
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

	private boolean trySetStructureStart(
		StructureSet.WeightedEntry weightedEntry,
		StructureAccessor structureAccessor,
		DynamicRegistryManager dynamicRegistryManager,
		NoiseConfig noiseConfig,
		StructureTemplateManager structureManager,
		long seed,
		Chunk chunk,
		ChunkPos pos,
		ChunkSectionPos sectionPos
	) {
		Structure structure = weightedEntry.structure().value();
		int i = getStructureReferences(structureAccessor, chunk, sectionPos, structure);
		RegistryEntryList<Biome> registryEntryList = structure.getValidBiomes();
		Predicate<RegistryEntry<Biome>> predicate = registryEntryList::contains;
		StructureStart structureStart = structure.createStructureStart(
			dynamicRegistryManager, this, this.biomeSource, noiseConfig, structureManager, seed, pos, i, chunk, predicate
		);
		if (structureStart.hasChildren()) {
			structureAccessor.setStructureStart(sectionPos, structure, structureStart, chunk);
			return true;
		} else {
			return false;
		}
	}

	private static int getStructureReferences(StructureAccessor structureAccessor, Chunk chunk, ChunkSectionPos sectionPos, Structure structure) {
		StructureStart structureStart = structureAccessor.getStructureStart(sectionPos, structure, chunk);
		return structureStart != null ? structureStart.getReferences() : 0;
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
							structureAccessor.addStructureReference(chunkSectionPos, structureStart.getStructure(), p, chunk);
							DebugInfoSender.sendStructureStart(world, structureStart);
						}
					} catch (Exception var21) {
						CrashReport crashReport = CrashReport.create(var21, "Generating structure reference");
						CrashReportSection crashReportSection = crashReport.addElement("Structure");
						Optional<? extends Registry<Structure>> optional = world.getRegistryManager().getOptional(Registry.STRUCTURE_KEY);
						crashReportSection.add(
							"Id",
							(CrashCallable<String>)(() -> (String)optional.map(structureTypeRegistry -> structureTypeRegistry.getId(structureStart.getStructure()).toString())
									.orElse("UNKNOWN"))
						);
						crashReportSection.add("Name", (CrashCallable<String>)(() -> Registry.STRUCTURE_TYPE.getId(structureStart.getStructure().getType()).toString()));
						crashReportSection.add("Class", (CrashCallable<String>)(() -> structureStart.getStructure().getClass().getCanonicalName()));
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

	public void computeStructurePlacementsIfNeeded(NoiseConfig noiseConfig) {
		if (!this.hasComputedStructurePlacements) {
			this.computeStructurePlacements(noiseConfig);
			this.hasComputedStructurePlacements = true;
		}
	}

	@Nullable
	public List<ChunkPos> getConcentricRingsStartChunks(ConcentricRingsStructurePlacement structurePlacement, NoiseConfig noiseConfig) {
		this.computeStructurePlacementsIfNeeded(noiseConfig);
		CompletableFuture<List<ChunkPos>> completableFuture = (CompletableFuture<List<ChunkPos>>)this.concentricRingPositions.get(structurePlacement);
		return completableFuture != null ? (List)completableFuture.join() : null;
	}

	private List<StructurePlacement> getStructurePlacement(RegistryEntry<Structure> structureEntry, NoiseConfig noiseConfig) {
		this.computeStructurePlacementsIfNeeded(noiseConfig);
		return (List<StructurePlacement>)this.structurePlacements.getOrDefault(structureEntry.value(), List.of());
	}

	public abstract void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos);

	@Deprecated
	public GenerationSettings getGenerationSettings(RegistryEntry<Biome> biomeEntry) {
		return (GenerationSettings)this.generationSettingsGetter.apply(biomeEntry);
	}
}
