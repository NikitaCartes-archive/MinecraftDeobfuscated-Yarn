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
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.nbt.scanner.SelectiveNbtCollector;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.storage.NbtScannable;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.slf4j.Logger;

public class StructureLocator {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int START_NOT_PRESENT_REFERENCE = -1;
	private final NbtScannable chunkIoWorker;
	private final DynamicRegistryManager registryManager;
	private final Registry<Biome> biomeRegistry;
	private final Registry<StructureType> structureTypeRegistry;
	private final StructureManager structureManager;
	private final RegistryKey<World> worldKey;
	private final ChunkGenerator chunkGenerator;
	private final NoiseConfig noiseConfig;
	private final HeightLimitView world;
	private final BiomeSource biomeSource;
	private final long seed;
	private final DataFixer dataFixer;
	private final Long2ObjectMap<Object2IntMap<StructureType>> cachedFeaturesByChunkPos = new Long2ObjectOpenHashMap<>();
	private final Map<StructureType, Long2BooleanMap> generationPossibilityByFeature = new HashMap();

	public StructureLocator(
		NbtScannable chunkIoWorker,
		DynamicRegistryManager registryManager,
		StructureManager structureManager,
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
		this.structureManager = structureManager;
		this.worldKey = worldKey;
		this.chunkGenerator = chunkGenerator;
		this.noiseConfig = noiseConfig;
		this.world = world;
		this.biomeSource = biomeSource;
		this.seed = seed;
		this.dataFixer = dataFixer;
		this.biomeRegistry = registryManager.getManaged(Registry.BIOME_KEY);
		this.structureTypeRegistry = registryManager.getManaged(Registry.STRUCTURE_KEY);
	}

	public StructurePresence getStructurePresence(ChunkPos pos, StructureType type, boolean skipReferencedStructures) {
		long l = pos.toLong();
		Object2IntMap<StructureType> object2IntMap = this.cachedFeaturesByChunkPos.get(l);
		if (object2IntMap != null) {
			return this.getStructurePresence(object2IntMap, type, skipReferencedStructures);
		} else {
			StructurePresence structurePresence = this.getStructurePresence(pos, type, skipReferencedStructures, l);
			if (structurePresence != null) {
				return structurePresence;
			} else {
				boolean bl = ((Long2BooleanMap)this.generationPossibilityByFeature.computeIfAbsent(type, feature -> new Long2BooleanOpenHashMap()))
					.computeIfAbsent(l, (Long2BooleanFunction)(chunkPos -> this.isGenerationPossible(pos, type)));
				return !bl ? StructurePresence.START_NOT_PRESENT : StructurePresence.CHUNK_LOAD_NEEDED;
			}
		}
	}

	/**
	 * {@return whether {@code feature} is able to generate in {@code pos}}
	 * 
	 * <p>This method only performs simple checks like biomes.
	 */
	private boolean isGenerationPossible(ChunkPos pos, StructureType feature) {
		return feature.getStructurePosition(
				new StructureType.Context(
					this.registryManager,
					this.chunkGenerator,
					this.biomeSource,
					this.noiseConfig,
					this.structureManager,
					this.seed,
					pos,
					this.world,
					feature.getValidBiomes()::contains
				)
			)
			.isPresent();
	}

	@Nullable
	private StructurePresence getStructurePresence(ChunkPos pos, StructureType feature, boolean skipReferencedStructures, long posLong) {
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
					nbtCompound2 = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbtCompound, i);
				} catch (Exception var12) {
					LOGGER.warn("Failed to partially datafix chunk {}", pos, var12);
					return StructurePresence.CHUNK_LOAD_NEEDED;
				}

				Object2IntMap<StructureType> object2IntMap = this.collectStructuresAndReferences(nbtCompound2);
				if (object2IntMap == null) {
					return null;
				} else {
					this.cache(posLong, object2IntMap);
					return this.getStructurePresence(object2IntMap, feature, skipReferencedStructures);
				}
			}
		}
	}

	@Nullable
	private Object2IntMap<StructureType> collectStructuresAndReferences(NbtCompound nbt) {
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
					Object2IntMap<StructureType> object2IntMap = new Object2IntOpenHashMap<>();
					Registry<StructureType> registry = this.registryManager.get(Registry.STRUCTURE_KEY);

					for (String string : nbtCompound2.getKeys()) {
						Identifier identifier = Identifier.tryParse(string);
						if (identifier != null) {
							StructureType structureType = registry.get(identifier);
							if (structureType != null) {
								NbtCompound nbtCompound3 = nbtCompound2.getCompound(string);
								if (!nbtCompound3.isEmpty()) {
									String string2 = nbtCompound3.getString("id");
									if (!"INVALID".equals(string2)) {
										int i = nbtCompound3.getInt("references");
										object2IntMap.put(structureType, i);
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

	private static Object2IntMap<StructureType> createMapIfEmpty(Object2IntMap<StructureType> map) {
		return map.isEmpty() ? Object2IntMaps.emptyMap() : map;
	}

	private StructurePresence getStructurePresence(Object2IntMap<StructureType> referencesByStructure, StructureType feature, boolean skipReferencedStructures) {
		int i = referencesByStructure.getOrDefault(feature, -1);
		return i == -1 || skipReferencedStructures && i != 0 ? StructurePresence.START_NOT_PRESENT : StructurePresence.START_PRESENT;
	}

	public void cache(ChunkPos pos, Map<StructureType, StructureStart> structureStarts) {
		long l = pos.toLong();
		Object2IntMap<StructureType> object2IntMap = new Object2IntOpenHashMap<>();
		structureStarts.forEach((start, structureStart) -> {
			if (structureStart.hasChildren()) {
				object2IntMap.put(start, structureStart.getReferences());
			}
		});
		this.cache(l, object2IntMap);
	}

	private void cache(long pos, Object2IntMap<StructureType> referencesByStructure) {
		this.cachedFeaturesByChunkPos.put(pos, createMapIfEmpty(referencesByStructure));
		this.generationPossibilityByFeature.values().forEach(generationPossibilityByChunkPos -> generationPossibilityByChunkPos.remove(pos));
	}

	public void incrementReferences(ChunkPos pos, StructureType feature) {
		this.cachedFeaturesByChunkPos.compute(pos.toLong(), (posx, referencesByStructure) -> {
			if (referencesByStructure == null || referencesByStructure.isEmpty()) {
				referencesByStructure = new Object2IntOpenHashMap();
			}

			referencesByStructure.computeInt(feature, (featurex, references) -> references == null ? 1 : references + 1);
			return referencesByStructure;
		});
	}
}
