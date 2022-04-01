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
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.storage.NbtScannable;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.slf4j.Logger;

public class StructureLocator {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int START_NOT_PRESENT_REFERENCE = -1;
	private final NbtScannable chunkIoWorker;
	private final DynamicRegistryManager registryManager;
	private final Registry<Biome> biomeRegistry;
	private final Registry<ConfiguredStructureFeature<?, ?>> configuredStructureFeatureRegistry;
	private final StructureManager structureManager;
	private final RegistryKey<World> worldKey;
	private final ChunkGenerator chunkGenerator;
	private final HeightLimitView world;
	private final BiomeSource biomeSource;
	private final long seed;
	private final DataFixer dataFixer;
	private final Long2ObjectMap<Object2IntMap<ConfiguredStructureFeature<?, ?>>> cachedFeaturesByChunkPos = new Long2ObjectOpenHashMap<>();
	private final Map<ConfiguredStructureFeature<?, ?>, Long2BooleanMap> generationPossibilityByFeature = new HashMap();

	public StructureLocator(
		NbtScannable chunkIoWorker,
		DynamicRegistryManager registryManager,
		StructureManager structureManager,
		RegistryKey<World> worldKey,
		ChunkGenerator chunkGenerator,
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
		this.world = world;
		this.biomeSource = biomeSource;
		this.seed = seed;
		this.dataFixer = dataFixer;
		this.biomeRegistry = registryManager.getManaged(Registry.BIOME_KEY);
		this.configuredStructureFeatureRegistry = registryManager.getManaged(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
	}

	public StructurePresence getStructurePresence(ChunkPos chunkPos, ConfiguredStructureFeature<?, ?> configuredStructureFeature, boolean skipExistingChunk) {
		long l = chunkPos.toLong();
		Object2IntMap<ConfiguredStructureFeature<?, ?>> object2IntMap = this.cachedFeaturesByChunkPos.get(l);
		if (object2IntMap != null) {
			return this.getStructurePresence(object2IntMap, configuredStructureFeature, skipExistingChunk);
		} else {
			StructurePresence structurePresence = this.getStructurePresence(chunkPos, configuredStructureFeature, skipExistingChunk, l);
			if (structurePresence != null) {
				return structurePresence;
			} else {
				boolean bl = ((Long2BooleanMap)this.generationPossibilityByFeature
						.computeIfAbsent(configuredStructureFeature, configuredStructureFeaturex -> new Long2BooleanOpenHashMap()))
					.computeIfAbsent(l, (Long2BooleanFunction)(lx -> this.isGenerationPossible(chunkPos, configuredStructureFeature)));
				return !bl ? StructurePresence.START_NOT_PRESENT : StructurePresence.CHUNK_LOAD_NEEDED;
			}
		}
	}

	/**
	 * {@return whether {@code feature} is able to generate in {@code pos}}
	 * 
	 * <p>This method only performs simple checks like biomes.
	 */
	private <FC extends FeatureConfig, F extends StructureFeature<FC>> boolean isGenerationPossible(ChunkPos pos, ConfiguredStructureFeature<FC, F> feature) {
		return feature.feature
			.canGenerate(
				this.registryManager,
				this.chunkGenerator,
				this.biomeSource,
				this.structureManager,
				this.seed,
				pos,
				feature.config,
				this.world,
				feature.getBiomes()::contains
			);
	}

	@Nullable
	private StructurePresence getStructurePresence(
		ChunkPos pos, ConfiguredStructureFeature<?, ?> configuredStructureFeature, boolean skipExistingChunk, long posLong
	) {
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

				Object2IntMap<ConfiguredStructureFeature<?, ?>> object2IntMap = this.collectStructuresAndReferences(nbtCompound2);
				if (object2IntMap == null) {
					return null;
				} else {
					this.cache(posLong, object2IntMap);
					return this.getStructurePresence(object2IntMap, configuredStructureFeature, skipExistingChunk);
				}
			}
		}
	}

	@Nullable
	private Object2IntMap<ConfiguredStructureFeature<?, ?>> collectStructuresAndReferences(NbtCompound nbt) {
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
					Object2IntMap<ConfiguredStructureFeature<?, ?>> object2IntMap = new Object2IntOpenHashMap<>();
					Registry<ConfiguredStructureFeature<?, ?>> registry = this.registryManager.get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);

					for (String string : nbtCompound2.getKeys()) {
						Identifier identifier = Identifier.tryParse(string);
						if (identifier != null) {
							ConfiguredStructureFeature<?, ?> configuredStructureFeature = registry.get(identifier);
							if (configuredStructureFeature != null) {
								NbtCompound nbtCompound3 = nbtCompound2.getCompound(string);
								if (!nbtCompound3.isEmpty()) {
									String string2 = nbtCompound3.getString("id");
									if (!"INVALID".equals(string2)) {
										int i = nbtCompound3.getInt("references");
										object2IntMap.put(configuredStructureFeature, i);
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

	private static Object2IntMap<ConfiguredStructureFeature<?, ?>> createMapIfEmpty(Object2IntMap<ConfiguredStructureFeature<?, ?>> map) {
		return map.isEmpty() ? Object2IntMaps.emptyMap() : map;
	}

	private StructurePresence getStructurePresence(
		Object2IntMap<ConfiguredStructureFeature<?, ?>> referencesByStructure, ConfiguredStructureFeature<?, ?> configuredStructureFeature, boolean skipExistingChunk
	) {
		int i = referencesByStructure.getOrDefault(configuredStructureFeature, -1);
		return i == -1 || skipExistingChunk && i != 0 ? StructurePresence.START_NOT_PRESENT : StructurePresence.START_PRESENT;
	}

	public void cache(ChunkPos pos, Map<ConfiguredStructureFeature<?, ?>, StructureStart> structureStarts) {
		long l = pos.toLong();
		Object2IntMap<ConfiguredStructureFeature<?, ?>> object2IntMap = new Object2IntOpenHashMap<>();
		structureStarts.forEach((configuredStructureFeature, structureStart) -> {
			if (structureStart.hasChildren()) {
				object2IntMap.put(configuredStructureFeature, structureStart.getReferences());
			}
		});
		this.cache(l, object2IntMap);
	}

	private void cache(long pos, Object2IntMap<ConfiguredStructureFeature<?, ?>> referencesByStructure) {
		this.cachedFeaturesByChunkPos.put(pos, createMapIfEmpty(referencesByStructure));
		this.generationPossibilityByFeature.values().forEach(generationPossibilityByChunkPos -> generationPossibilityByChunkPos.remove(pos));
	}

	public void incrementReferences(ChunkPos pos, ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
		this.cachedFeaturesByChunkPos.compute(pos.toLong(), (posx, referencesByStructure) -> {
			if (referencesByStructure == null || referencesByStructure.isEmpty()) {
				referencesByStructure = new Object2IntOpenHashMap();
			}

			referencesByStructure.computeInt(configuredStructureFeature, (configuredStructureFeaturexx, references) -> references == null ? 1 : references + 1);
			return referencesByStructure;
		});
	}
}
