package net.minecraft.world.gen.chunk;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
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
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.apache.commons.lang3.mutable.MutableBoolean;

/**
 * In charge of shaping, adding biome specific surface blocks, and carving chunks,
 * as well as populating the generated chunks with {@linkplain net.minecraft.world.gen.feature.Feature features} and {@linkplain net.minecraft.entity.Entity entities}.
 * Biome placement starts here, however all vanilla and most modded chunk generators delegate this to a {@linkplain net.minecraft.world.biome.source.BiomeSource biome source}.
 */
public abstract class ChunkGenerator {
	public static final Codec<ChunkGenerator> CODEC = Registries.CHUNK_GENERATOR.getCodec().dispatchStable(ChunkGenerator::getCodec, Function.identity());
	protected final BiomeSource biomeSource;
	private final Supplier<List<PlacedFeatureIndexer.IndexedFeatures>> indexedFeaturesListSupplier;
	private final Function<RegistryEntry<Biome>, GenerationSettings> generationSettingsGetter;

	public ChunkGenerator(BiomeSource biomeSource) {
		this(biomeSource, biomeEntry -> ((Biome)biomeEntry.value()).getGenerationSettings());
	}

	public ChunkGenerator(BiomeSource biomeSource, Function<RegistryEntry<Biome>, GenerationSettings> generationSettingsGetter) {
		this.biomeSource = biomeSource;
		this.generationSettingsGetter = generationSettingsGetter;
		this.indexedFeaturesListSupplier = Suppliers.memoize(
			() -> PlacedFeatureIndexer.collectIndexedFeatures(
					List.copyOf(biomeSource.getBiomes()), biomeEntry -> ((GenerationSettings)generationSettingsGetter.apply(biomeEntry)).getFeatures(), true
				)
		);
	}

	protected abstract Codec<? extends ChunkGenerator> getCodec();

	public StructurePlacementCalculator createStructurePlacementCalculator(RegistryWrapper<StructureSet> structureSetRegistry, NoiseConfig noiseConfig, long seed) {
		return StructurePlacementCalculator.create(noiseConfig, seed, this.biomeSource, structureSetRegistry);
	}

	public Optional<RegistryKey<Codec<? extends ChunkGenerator>>> getCodecKey() {
		return Registries.CHUNK_GENERATOR.getKey(this.getCodec());
	}

	public CompletableFuture<Chunk> populateBiomes(Executor executor, NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
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
		BiomeAccess biomeAccess,
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
		StructurePlacementCalculator structurePlacementCalculator = world.getChunkManager().getStructurePlacementCalculator();
		Map<StructurePlacement, Set<RegistryEntry<Structure>>> map = new Object2ObjectArrayMap<>();

		for (RegistryEntry<Structure> registryEntry : structures) {
			for (StructurePlacement structurePlacement : structurePlacementCalculator.getPlacements(registryEntry)) {
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
							structurePlacementCalculator.getStructureSeed(),
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
		List<ChunkPos> list = world.getChunkManager().getStructurePlacementCalculator().getPlacementPositions(placement);
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
			StructurePresence structurePresence = structureAccessor.getStructurePresence(pos, registryEntry.value(), placement, skipReferencedStructures);
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
		if (!SharedConstants.isOutsideGenerationArea(chunkPos)) {
			ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, world.getBottomSectionCoord());
			BlockPos blockPos = chunkSectionPos.getMinPos();
			Registry<Structure> registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
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
				Registry<PlacedFeature> registry2 = world.getRegistryManager().get(RegistryKeys.PLACED_FEATURE);
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
				accessor.acceptStructureStarts(structure, (LongSet)entry.getValue(), start -> {
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
		StructurePlacementCalculator placementCalculator,
		StructureAccessor structureAccessor,
		Chunk chunk,
		StructureTemplateManager structureTemplateManager
	) {
		ChunkPos chunkPos = chunk.getPos();
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);
		NoiseConfig noiseConfig = placementCalculator.getNoiseConfig();
		placementCalculator.getStructureSets()
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

					if (structurePlacement.shouldGenerate(placementCalculator, chunkPos.x, chunkPos.z)) {
						if (list.size() == 1) {
							this.trySetStructureStart(
								(StructureSet.WeightedEntry)list.get(0),
								structureAccessor,
								registryManager,
								noiseConfig,
								structureTemplateManager,
								placementCalculator.getStructureSeed(),
								chunk,
								chunkPos,
								chunkSectionPos
							);
						} else {
							ArrayList<StructureSet.WeightedEntry> arrayList = new ArrayList(list.size());
							arrayList.addAll(list);
							ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
							chunkRandom.setCarverSeed(placementCalculator.getStructureSeed(), chunkPos.x, chunkPos.z);
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
									weightedEntry4,
									structureAccessor,
									registryManager,
									noiseConfig,
									structureTemplateManager,
									placementCalculator.getStructureSeed(),
									chunk,
									chunkPos,
									chunkSectionPos
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
						Optional<? extends Registry<Structure>> optional = world.getRegistryManager().getOptional(RegistryKeys.STRUCTURE);
						crashReportSection.add(
							"Id",
							(CrashCallable<String>)(() -> (String)optional.map(structureTypeRegistry -> structureTypeRegistry.getId(structureStart.getStructure()).toString())
									.orElse("UNKNOWN"))
						);
						crashReportSection.add("Name", (CrashCallable<String>)(() -> Registries.STRUCTURE_TYPE.getId(structureStart.getStructure().getType()).toString()));
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

	public abstract void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos);

	@Deprecated
	public GenerationSettings getGenerationSettings(RegistryEntry<Biome> biomeEntry) {
		return (GenerationSettings)this.generationSettingsGetter.apply(biomeEntry);
	}
}
