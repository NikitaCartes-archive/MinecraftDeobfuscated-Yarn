package net.minecraft.world.gen.chunk.placement;

import com.google.common.base.Stopwatch;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.slf4j.Logger;

public class StructurePlacementCalculator {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final NoiseConfig noiseConfig;
	private final BiomeSource biomeSource;
	private final long structureSeed;
	private final long concentricRingSeed;
	private final Map<Structure, List<StructurePlacement>> structuresToPlacements = new Object2ObjectOpenHashMap<>();
	private final Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> concentricPlacementsToPositions = new Object2ObjectArrayMap<>();
	private boolean calculated;
	private final List<RegistryEntry<StructureSet>> structureSets;

	public static StructurePlacementCalculator create(
		NoiseConfig noiseConfig, long seed, BiomeSource biomeSource, Stream<RegistryEntry<StructureSet>> structureSets
	) {
		List<RegistryEntry<StructureSet>> list = structureSets.filter(structureSet -> hasValidBiome((StructureSet)structureSet.value(), biomeSource)).toList();
		return new StructurePlacementCalculator(noiseConfig, biomeSource, seed, 0L, list);
	}

	public static StructurePlacementCalculator create(
		NoiseConfig noiseConfig, long seed, BiomeSource biomeSource, RegistryWrapper<StructureSet> structureSetRegistry
	) {
		List<RegistryEntry<StructureSet>> list = (List<RegistryEntry<StructureSet>>)structureSetRegistry.streamEntries()
			.filter(structureSet -> hasValidBiome((StructureSet)structureSet.value(), biomeSource))
			.collect(Collectors.toUnmodifiableList());
		return new StructurePlacementCalculator(noiseConfig, biomeSource, seed, seed, list);
	}

	private static boolean hasValidBiome(StructureSet structureSet, BiomeSource biomeSource) {
		Stream<RegistryEntry<Biome>> stream = structureSet.structures().stream().flatMap(structure -> {
			Structure structure2 = structure.structure().value();
			return structure2.getValidBiomes().stream();
		});
		return stream.anyMatch(biomeSource.getBiomes()::contains);
	}

	private StructurePlacementCalculator(
		NoiseConfig noiseConfig, BiomeSource biomeSource, long structureSeed, long concentricRingSeed, List<RegistryEntry<StructureSet>> structureSets
	) {
		this.noiseConfig = noiseConfig;
		this.structureSeed = structureSeed;
		this.biomeSource = biomeSource;
		this.concentricRingSeed = concentricRingSeed;
		this.structureSets = structureSets;
	}

	public List<RegistryEntry<StructureSet>> getStructureSets() {
		return this.structureSets;
	}

	private void calculate() {
		Set<RegistryEntry<Biome>> set = this.biomeSource.getBiomes();
		this.getStructureSets()
			.forEach(
				structureSet -> {
					StructureSet structureSet2 = (StructureSet)structureSet.value();
					boolean bl = false;

					for (StructureSet.WeightedEntry weightedEntry : structureSet2.structures()) {
						Structure structure = weightedEntry.structure().value();
						if (structure.getValidBiomes().stream().anyMatch(set::contains)) {
							((List)this.structuresToPlacements.computeIfAbsent(structure, structurex -> new ArrayList())).add(structureSet2.placement());
							bl = true;
						}
					}

					if (bl && structureSet2.placement() instanceof ConcentricRingsStructurePlacement concentricRingsStructurePlacement) {
						this.concentricPlacementsToPositions
							.put(concentricRingsStructurePlacement, this.calculateConcentricsRingPlacementPos(structureSet, concentricRingsStructurePlacement));
					}
				}
			);
	}

	private CompletableFuture<List<ChunkPos>> calculateConcentricsRingPlacementPos(
		RegistryEntry<StructureSet> structureSetEntry, ConcentricRingsStructurePlacement placement
	) {
		if (placement.getCount() == 0) {
			return CompletableFuture.completedFuture(List.of());
		} else {
			Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
			int i = placement.getDistance();
			int j = placement.getCount();
			List<CompletableFuture<ChunkPos>> list = new ArrayList(j);
			int k = placement.getSpread();
			RegistryEntryList<Biome> registryEntryList = placement.getPreferredBiomes();
			Random random = Random.create();
			random.setSeed(this.concentricRingSeed);
			double d = random.nextDouble() * Math.PI * 2.0;
			int l = 0;
			int m = 0;

			for (int n = 0; n < j; n++) {
				double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
				int o = (int)Math.round(Math.cos(d) * e);
				int p = (int)Math.round(Math.sin(d) * e);
				Random random2 = random.split();
				list.add(
					CompletableFuture.supplyAsync(
						() -> {
							Pair<BlockPos, RegistryEntry<Biome>> pair = this.biomeSource
								.locateBiome(
									ChunkSectionPos.getOffsetPos(o, 8),
									0,
									ChunkSectionPos.getOffsetPos(p, 8),
									112,
									registryEntryList::contains,
									random2,
									this.noiseConfig.getMultiNoiseSampler()
								);
							if (pair != null) {
								BlockPos blockPos = pair.getFirst();
								return new ChunkPos(ChunkSectionPos.getSectionCoord(blockPos.getX()), ChunkSectionPos.getSectionCoord(blockPos.getZ()));
							} else {
								return new ChunkPos(o, p);
							}
						},
						Util.getMainWorkerExecutor().named("structureRings")
					)
				);
				d += (Math.PI * 2) / (double)k;
				if (++l == k) {
					m++;
					l = 0;
					k += 2 * k / (m + 1);
					k = Math.min(k, j - n);
					d += random.nextDouble() * Math.PI * 2.0;
				}
			}

			return Util.combineSafe(list).thenApply(positions -> {
				double dx = (double)stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0;
				LOGGER.debug("Calculation for {} took {}s", structureSetEntry, dx);
				return positions;
			});
		}
	}

	public void tryCalculate() {
		if (!this.calculated) {
			this.calculate();
			this.calculated = true;
		}
	}

	@Nullable
	public List<ChunkPos> getPlacementPositions(ConcentricRingsStructurePlacement placement) {
		this.tryCalculate();
		CompletableFuture<List<ChunkPos>> completableFuture = (CompletableFuture<List<ChunkPos>>)this.concentricPlacementsToPositions.get(placement);
		return completableFuture != null ? (List)completableFuture.join() : null;
	}

	public List<StructurePlacement> getPlacements(RegistryEntry<Structure> structureEntry) {
		this.tryCalculate();
		return (List<StructurePlacement>)this.structuresToPlacements.getOrDefault(structureEntry.value(), List.of());
	}

	public NoiseConfig getNoiseConfig() {
		return this.noiseConfig;
	}

	public boolean canGenerate(RegistryEntry<StructureSet> structureSetEntry, int centerChunkX, int centerChunkZ, int chunkCount) {
		StructurePlacement structurePlacement = structureSetEntry.value().placement();

		for (int i = centerChunkX - chunkCount; i <= centerChunkX + chunkCount; i++) {
			for (int j = centerChunkZ - chunkCount; j <= centerChunkZ + chunkCount; j++) {
				if (structurePlacement.shouldGenerate(this, i, j)) {
					return true;
				}
			}
		}

		return false;
	}

	public long getStructureSeed() {
		return this.structureSeed;
	}
}
