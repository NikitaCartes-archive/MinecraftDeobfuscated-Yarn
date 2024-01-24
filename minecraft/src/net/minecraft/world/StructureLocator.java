package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.nbt.scanner.SelectiveNbtCollector;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.storage.NbtScannable;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.slf4j.Logger;

public class StructureLocator {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int START_NOT_PRESENT_REFERENCE = -1;
	private final NbtScannable chunkIoWorker;
	private final DynamicRegistryManager registryManager;
	private final StructureTemplateManager structureTemplateManager;
	private final RegistryKey<World> worldKey;
	private final ChunkGenerator chunkGenerator;
	private final NoiseConfig noiseConfig;
	private final HeightLimitView world;
	private final BiomeSource biomeSource;
	private final long seed;
	private final DataFixer dataFixer;
	private final Long2ObjectMap<Object2IntMap<Structure>> cachedStructuresByChunkPos = new Long2ObjectOpenHashMap<>();
	private final Map<Structure, Long2BooleanMap> generationPossibilityByStructure = new HashMap();

	public StructureLocator(
		NbtScannable chunkIoWorker,
		DynamicRegistryManager registryManager,
		StructureTemplateManager structureTemplateManager,
		RegistryKey<World> worldKey,
		ChunkGenerator chunkGenerator,
		NoiseConfig noiseConfig,
		HeightLimitView world,
		BiomeSource biomeSource,
		long seed,
		DataFixer dataFixer
	) {
		this.chunkIoWorker = chunkIoWorker;
		this.registryManager = registryManager;
		this.structureTemplateManager = structureTemplateManager;
		this.worldKey = worldKey;
		this.chunkGenerator = chunkGenerator;
		this.noiseConfig = noiseConfig;
		this.world = world;
		this.biomeSource = biomeSource;
		this.seed = seed;
		this.dataFixer = dataFixer;
	}

	public StructurePresence getStructurePresence(ChunkPos pos, Structure type, StructurePlacement placement, boolean skipReferencedStructures) {
		long l = pos.toLong();
		Object2IntMap<Structure> object2IntMap = this.cachedStructuresByChunkPos.get(l);
		if (object2IntMap != null) {
			return this.getStructurePresence(object2IntMap, type, skipReferencedStructures);
		} else {
			StructurePresence structurePresence = this.getStructurePresence(pos, type, skipReferencedStructures, l);
			if (structurePresence != null) {
				return structurePresence;
			} else if (!placement.applyFrequencyReduction(pos.x, pos.z, this.seed)) {
				return StructurePresence.START_NOT_PRESENT;
			} else {
				boolean bl = ((Long2BooleanMap)this.generationPossibilityByStructure.computeIfAbsent(type, structure2 -> new Long2BooleanOpenHashMap()))
					.computeIfAbsent(l, (Long2BooleanFunction)(chunkPos -> this.isGenerationPossible(pos, type)));
				return !bl ? StructurePresence.START_NOT_PRESENT : StructurePresence.CHUNK_LOAD_NEEDED;
			}
		}
	}

	/**
	 * {@return whether {@code structure} is able to generate in {@code pos}}
	 * 
	 * <p>This method only performs simple checks like biomes.
	 */
	private boolean isGenerationPossible(ChunkPos pos, Structure structure) {
		return structure.getValidStructurePosition(
				new Structure.Context(
					this.registryManager,
					this.chunkGenerator,
					this.biomeSource,
					this.noiseConfig,
					this.structureTemplateManager,
					this.seed,
					pos,
					this.world,
					structure.getValidBiomes()::contains
				)
			)
			.isPresent();
	}

	@Nullable
	private StructurePresence getStructurePresence(ChunkPos pos, Structure structure, boolean skipReferencedStructures, long posLong) {
		SelectiveNbtCollector selectiveNbtCollector = new SelectiveNbtCollector(
			new NbtScanQuery(NbtInt.TYPE, "DataVersion"),
			new NbtScanQuery("Level", "Structures", NbtCompound.TYPE, "Starts"),
			new NbtScanQuery("structures", NbtCompound.TYPE, "starts")
		);

		try {
			this.chunkIoWorker.scanChunk(pos, selectiveNbtCollector).join();
		} catch (Exception var13) {
			LOGGER.warn("Failed to read chunk {}", pos, var13);
			return StructurePresence.CHUNK_LOAD_NEEDED;
		}

		if (!(selectiveNbtCollector.getRoot() instanceof NbtCompound nbtCompound)) {
			return null;
		} else {
			int i = VersionedChunkStorage.getDataVersion(nbtCompound);
			if (i <= 1493) {
				return StructurePresence.CHUNK_LOAD_NEEDED;
			} else {
				VersionedChunkStorage.saveContextToNbt(nbtCompound, this.worldKey, this.chunkGenerator.getCodecKey());

				NbtCompound nbtCompound2;
				try {
					nbtCompound2 = DataFixTypes.CHUNK.update(this.dataFixer, nbtCompound, i);
				} catch (Exception var12) {
					LOGGER.warn("Failed to partially datafix chunk {}", pos, var12);
					return StructurePresence.CHUNK_LOAD_NEEDED;
				}

				Object2IntMap<Structure> object2IntMap = this.collectStructuresAndReferences(nbtCompound2);
				if (object2IntMap == null) {
					return null;
				} else {
					this.cache(posLong, object2IntMap);
					return this.getStructurePresence(object2IntMap, structure, skipReferencedStructures);
				}
			}
		}
	}

	@Nullable
	private Object2IntMap<Structure> collectStructuresAndReferences(NbtCompound nbt) {
		if (!nbt.contains("structures", NbtElement.COMPOUND_TYPE)) {
			return null;
		} else {
			NbtCompound nbtCompound = nbt.getCompound("structures");
			if (!nbtCompound.contains("starts", NbtElement.COMPOUND_TYPE)) {
				return null;
			} else {
				NbtCompound nbtCompound2 = nbtCompound.getCompound("starts");
				if (nbtCompound2.isEmpty()) {
					return Object2IntMaps.emptyMap();
				} else {
					Object2IntMap<Structure> object2IntMap = new Object2IntOpenHashMap<>();
					Registry<Structure> registry = this.registryManager.get(RegistryKeys.STRUCTURE);

					for (String string : nbtCompound2.getKeys()) {
						Identifier identifier = Identifier.tryParse(string);
						if (identifier != null) {
							Structure structure = registry.get(identifier);
							if (structure != null) {
								NbtCompound nbtCompound3 = nbtCompound2.getCompound(string);
								if (!nbtCompound3.isEmpty()) {
									String string2 = nbtCompound3.getString("id");
									if (!"INVALID".equals(string2)) {
										int i = nbtCompound3.getInt("references");
										object2IntMap.put(structure, i);
									}
								}
							}
						}
					}

					return object2IntMap;
				}
			}
		}
	}

	private static Object2IntMap<Structure> createMapIfEmpty(Object2IntMap<Structure> map) {
		return map.isEmpty() ? Object2IntMaps.emptyMap() : map;
	}

	private StructurePresence getStructurePresence(Object2IntMap<Structure> referencesByStructure, Structure structure, boolean skipReferencedStructures) {
		int i = referencesByStructure.getOrDefault(structure, -1);
		return i == -1 || skipReferencedStructures && i != 0 ? StructurePresence.START_NOT_PRESENT : StructurePresence.START_PRESENT;
	}

	public void cache(ChunkPos pos, Map<Structure, StructureStart> structureStarts) {
		long l = pos.toLong();
		Object2IntMap<Structure> object2IntMap = new Object2IntOpenHashMap<>();
		structureStarts.forEach((start, structureStart) -> {
			if (structureStart.hasChildren()) {
				object2IntMap.put(start, structureStart.getReferences());
			}
		});
		this.cache(l, object2IntMap);
	}

	private void cache(long pos, Object2IntMap<Structure> referencesByStructure) {
		this.cachedStructuresByChunkPos.put(pos, createMapIfEmpty(referencesByStructure));
		this.generationPossibilityByStructure.values().forEach(generationPossibilityByChunkPos -> generationPossibilityByChunkPos.remove(pos));
	}

	public void incrementReferences(ChunkPos pos, Structure structure) {
		this.cachedStructuresByChunkPos.compute(pos.toLong(), (posx, referencesByStructure) -> {
			if (referencesByStructure == null || referencesByStructure.isEmpty()) {
				referencesByStructure = new Object2IntOpenHashMap();
			}

			referencesByStructure.computeInt(structure, (feature, references) -> references == null ? 1 : references + 1);
			return referencesByStructure;
		});
	}
}
