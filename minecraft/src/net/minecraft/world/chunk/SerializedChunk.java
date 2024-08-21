package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtShort;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.StorageKey;
import net.minecraft.world.tick.ChunkTickScheduler;
import net.minecraft.world.tick.SimpleTickScheduler;
import net.minecraft.world.tick.Tick;
import org.slf4j.Logger;

public record SerializedChunk(
	Registry<Biome> biomeRegistry,
	ChunkPos chunkPos,
	int minSectionY,
	long lastUpdateTime,
	long inhabitedTime,
	ChunkStatus chunkStatus,
	@Nullable BlendingData.Serialized blendingData,
	@Nullable BelowZeroRetrogen belowZeroRetrogen,
	UpgradeData upgradeData,
	@Nullable long[] carvingMask,
	Map<Heightmap.Type, long[]> heightmaps,
	Chunk.TickSchedulers packedTicks,
	ShortList[] postProcessingSections,
	boolean lightCorrect,
	List<SerializedChunk.SectionData> sectionData,
	List<NbtCompound> entities,
	List<NbtCompound> blockEntities,
	NbtCompound structureData
) {
	private static final Codec<PalettedContainer<BlockState>> CODEC = PalettedContainer.createPalettedContainerCodec(
		Block.STATE_IDS, BlockState.CODEC, PalettedContainer.PaletteProvider.BLOCK_STATE, Blocks.AIR.getDefaultState()
	);
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String UPGRADE_DATA_KEY = "UpgradeData";
	private static final String BLOCK_TICKS = "block_ticks";
	private static final String FLUID_TICKS = "fluid_ticks";
	public static final String X_POS_KEY = "xPos";
	public static final String Z_POS_KEY = "zPos";
	public static final String HEIGHTMAPS_KEY = "Heightmaps";
	public static final String IS_LIGHT_ON_KEY = "isLightOn";
	public static final String SECTIONS_KEY = "sections";
	public static final String BLOCK_LIGHT_KEY = "BlockLight";
	public static final String SKY_LIGHT_KEY = "SkyLight";

	@Nullable
	public static SerializedChunk fromNbt(HeightLimitView world, DynamicRegistryManager registryManager, NbtCompound nbt) {
		if (!nbt.contains("Status", NbtElement.STRING_TYPE)) {
			return null;
		} else {
			ChunkPos chunkPos = new ChunkPos(nbt.getInt("xPos"), nbt.getInt("zPos"));
			long l = nbt.getLong("LastUpdate");
			long m = nbt.getLong("InhabitedTime");
			ChunkStatus chunkStatus = ChunkStatus.byId(nbt.getString("Status"));
			UpgradeData upgradeData = nbt.contains("UpgradeData", NbtElement.COMPOUND_TYPE)
				? new UpgradeData(nbt.getCompound("UpgradeData"), world)
				: UpgradeData.NO_UPGRADE_DATA;
			boolean bl = nbt.getBoolean("isLightOn");
			BlendingData.Serialized serialized;
			if (nbt.contains("blending_data", NbtElement.COMPOUND_TYPE)) {
				serialized = (BlendingData.Serialized)BlendingData.Serialized.CODEC
					.parse(NbtOps.INSTANCE, nbt.getCompound("blending_data"))
					.resultOrPartial(LOGGER::error)
					.orElse(null);
			} else {
				serialized = null;
			}

			BelowZeroRetrogen belowZeroRetrogen;
			if (nbt.contains("below_zero_retrogen", NbtElement.COMPOUND_TYPE)) {
				belowZeroRetrogen = (BelowZeroRetrogen)BelowZeroRetrogen.CODEC
					.parse(NbtOps.INSTANCE, nbt.getCompound("below_zero_retrogen"))
					.resultOrPartial(LOGGER::error)
					.orElse(null);
			} else {
				belowZeroRetrogen = null;
			}

			long[] ls;
			if (nbt.contains("carving_mask", NbtElement.LONG_ARRAY_TYPE)) {
				ls = nbt.getLongArray("carving_mask");
			} else {
				ls = null;
			}

			NbtCompound nbtCompound = nbt.getCompound("Heightmaps");
			Map<Heightmap.Type, long[]> map = new EnumMap(Heightmap.Type.class);

			for (Heightmap.Type type : chunkStatus.getHeightmapTypes()) {
				String string = type.getName();
				if (nbtCompound.contains(string, NbtElement.LONG_ARRAY_TYPE)) {
					map.put(type, nbtCompound.getLongArray(string));
				}
			}

			List<Tick<Block>> list = Tick.tick(
				nbt.getList("block_ticks", NbtElement.COMPOUND_TYPE), id -> Registries.BLOCK.getOrEmpty(Identifier.tryParse(id)), chunkPos
			);
			List<Tick<Fluid>> list2 = Tick.tick(
				nbt.getList("fluid_ticks", NbtElement.COMPOUND_TYPE), id -> Registries.FLUID.getOrEmpty(Identifier.tryParse(id)), chunkPos
			);
			Chunk.TickSchedulers tickSchedulers = new Chunk.TickSchedulers(list, list2);
			NbtList nbtList = nbt.getList("PostProcessing", NbtElement.LIST_TYPE);
			ShortList[] shortLists = new ShortList[nbtList.size()];

			for (int i = 0; i < nbtList.size(); i++) {
				NbtList nbtList2 = nbtList.getList(i);
				ShortList shortList = new ShortArrayList(nbtList2.size());

				for (int j = 0; j < nbtList2.size(); j++) {
					shortList.add(nbtList2.getShort(j));
				}

				shortLists[i] = shortList;
			}

			List<NbtCompound> list3 = Lists.transform(nbt.getList("entities", NbtElement.COMPOUND_TYPE), entity -> (NbtCompound)entity);
			List<NbtCompound> list4 = Lists.transform(nbt.getList("block_entities", NbtElement.COMPOUND_TYPE), blockEntity -> (NbtCompound)blockEntity);
			NbtCompound nbtCompound2 = nbt.getCompound("structures");
			NbtList nbtList3 = nbt.getList("sections", NbtElement.COMPOUND_TYPE);
			List<SerializedChunk.SectionData> list5 = new ArrayList(nbtList3.size());
			Registry<Biome> registry = registryManager.get(RegistryKeys.BIOME);
			Codec<ReadableContainer<RegistryEntry<Biome>>> codec = createCodec(registry);

			for (int k = 0; k < nbtList3.size(); k++) {
				NbtCompound nbtCompound3 = nbtList3.getCompound(k);
				int n = nbtCompound3.getByte("Y");
				ChunkSection chunkSection;
				if (n >= world.getBottomSectionCoord() && n <= world.getTopSectionCoord()) {
					PalettedContainer<BlockState> palettedContainer;
					if (nbtCompound3.contains("block_states", NbtElement.COMPOUND_TYPE)) {
						palettedContainer = CODEC.parse(NbtOps.INSTANCE, nbtCompound3.getCompound("block_states"))
							.promotePartial(error -> logRecoverableError(chunkPos, n, error))
							.getOrThrow(SerializedChunk.ChunkLoadingException::new);
					} else {
						palettedContainer = new PalettedContainer<>(Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE);
					}

					ReadableContainer<RegistryEntry<Biome>> readableContainer;
					if (nbtCompound3.contains("biomes", NbtElement.COMPOUND_TYPE)) {
						readableContainer = codec.parse(NbtOps.INSTANCE, nbtCompound3.getCompound("biomes"))
							.promotePartial(error -> logRecoverableError(chunkPos, n, error))
							.getOrThrow(SerializedChunk.ChunkLoadingException::new);
					} else {
						readableContainer = new PalettedContainer<>(registry.getIndexedEntries(), registry.entryOf(BiomeKeys.PLAINS), PalettedContainer.PaletteProvider.BIOME);
					}

					chunkSection = new ChunkSection(palettedContainer, readableContainer);
				} else {
					chunkSection = null;
				}

				ChunkNibbleArray chunkNibbleArray = nbtCompound3.contains("BlockLight", NbtElement.BYTE_ARRAY_TYPE)
					? new ChunkNibbleArray(nbtCompound3.getByteArray("BlockLight"))
					: null;
				ChunkNibbleArray chunkNibbleArray2 = nbtCompound3.contains("SkyLight", NbtElement.BYTE_ARRAY_TYPE)
					? new ChunkNibbleArray(nbtCompound3.getByteArray("SkyLight"))
					: null;
				list5.add(new SerializedChunk.SectionData(n, chunkSection, chunkNibbleArray, chunkNibbleArray2));
			}

			return new SerializedChunk(
				registry,
				chunkPos,
				world.getBottomSectionCoord(),
				l,
				m,
				chunkStatus,
				serialized,
				belowZeroRetrogen,
				upgradeData,
				ls,
				map,
				tickSchedulers,
				shortLists,
				bl,
				list5,
				list3,
				list4,
				nbtCompound2
			);
		}
	}

	public ProtoChunk convert(ServerWorld world, PointOfInterestStorage poiStorage, StorageKey key, ChunkPos expectedPos) {
		if (!Objects.equals(expectedPos, this.chunkPos)) {
			LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", expectedPos, expectedPos, this.chunkPos);
			world.getServer().onChunkMisplacement(this.chunkPos, expectedPos, key);
		}

		int i = world.countVerticalSections();
		ChunkSection[] chunkSections = new ChunkSection[i];
		boolean bl = world.getDimension().hasSkyLight();
		ChunkManager chunkManager = world.getChunkManager();
		LightingProvider lightingProvider = chunkManager.getLightingProvider();
		Registry<Biome> registry = world.getRegistryManager().get(RegistryKeys.BIOME);
		boolean bl2 = false;

		for (SerializedChunk.SectionData sectionData : this.sectionData) {
			ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(expectedPos, sectionData.y);
			if (sectionData.chunkSection != null) {
				chunkSections[world.sectionCoordToIndex(sectionData.y)] = sectionData.chunkSection;
				poiStorage.initForPalette(chunkSectionPos, sectionData.chunkSection);
			}

			boolean bl3 = sectionData.blockLight != null;
			boolean bl4 = bl && sectionData.skyLight != null;
			if (bl3 || bl4) {
				if (!bl2) {
					lightingProvider.setRetainData(expectedPos, true);
					bl2 = true;
				}

				if (bl3) {
					lightingProvider.enqueueSectionData(LightType.BLOCK, chunkSectionPos, sectionData.blockLight);
				}

				if (bl4) {
					lightingProvider.enqueueSectionData(LightType.SKY, chunkSectionPos, sectionData.skyLight);
				}
			}
		}

		ChunkType chunkType = this.chunkStatus.getChunkType();
		Chunk chunk;
		if (chunkType == ChunkType.LEVELCHUNK) {
			ChunkTickScheduler<Block> chunkTickScheduler = new ChunkTickScheduler<>(this.packedTicks.blocks());
			ChunkTickScheduler<Fluid> chunkTickScheduler2 = new ChunkTickScheduler<>(this.packedTicks.fluids());
			chunk = new WorldChunk(
				world.toServerWorld(),
				expectedPos,
				this.upgradeData,
				chunkTickScheduler,
				chunkTickScheduler2,
				this.inhabitedTime,
				chunkSections,
				getEntityLoadingCallback(world, this.entities, this.blockEntities),
				BlendingData.fromSerialized(this.blendingData)
			);
		} else {
			SimpleTickScheduler<Block> simpleTickScheduler = SimpleTickScheduler.tick(this.packedTicks.blocks());
			SimpleTickScheduler<Fluid> simpleTickScheduler2 = SimpleTickScheduler.tick(this.packedTicks.fluids());
			ProtoChunk protoChunk = new ProtoChunk(
				expectedPos, this.upgradeData, chunkSections, simpleTickScheduler, simpleTickScheduler2, world, registry, BlendingData.fromSerialized(this.blendingData)
			);
			chunk = protoChunk;
			protoChunk.setInhabitedTime(this.inhabitedTime);
			if (this.belowZeroRetrogen != null) {
				protoChunk.setBelowZeroRetrogen(this.belowZeroRetrogen);
			}

			protoChunk.setStatus(this.chunkStatus);
			if (this.chunkStatus.isAtLeast(ChunkStatus.INITIALIZE_LIGHT)) {
				protoChunk.setLightingProvider(lightingProvider);
			}
		}

		chunk.setLightOn(this.lightCorrect);
		EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);

		for (Heightmap.Type type : chunk.getStatus().getHeightmapTypes()) {
			long[] ls = (long[])this.heightmaps.get(type);
			if (ls != null) {
				chunk.setHeightmap(type, ls);
			} else {
				enumSet.add(type);
			}
		}

		Heightmap.populateHeightmaps(chunk, enumSet);
		chunk.setStructureStarts(readStructureStarts(StructureContext.from(world), this.structureData, world.getSeed()));
		chunk.setStructureReferences(readStructureReferences(world.getRegistryManager(), expectedPos, this.structureData));

		for (int j = 0; j < this.postProcessingSections.length; j++) {
			chunk.markBlocksForPostProcessing(this.postProcessingSections[j], j);
		}

		if (chunkType == ChunkType.LEVELCHUNK) {
			return new WrapperProtoChunk((WorldChunk)chunk, false);
		} else {
			ProtoChunk protoChunk2 = (ProtoChunk)chunk;

			for (NbtCompound nbtCompound : this.entities) {
				protoChunk2.addEntity(nbtCompound);
			}

			for (NbtCompound nbtCompound : this.blockEntities) {
				protoChunk2.addPendingBlockEntityNbt(nbtCompound);
			}

			if (this.carvingMask != null) {
				protoChunk2.setCarvingMask(new CarvingMask(this.carvingMask, chunk.getBottomY()));
			}

			return protoChunk2;
		}
	}

	private static void logRecoverableError(ChunkPos chunkPos, int y, String message) {
		LOGGER.error("Recoverable errors when loading section [{}, {}, {}]: {}", chunkPos.x, y, chunkPos.z, message);
	}

	private static Codec<ReadableContainer<RegistryEntry<Biome>>> createCodec(Registry<Biome> biomeRegistry) {
		return PalettedContainer.createReadableContainerCodec(
			biomeRegistry.getIndexedEntries(), biomeRegistry.getEntryCodec(), PalettedContainer.PaletteProvider.BIOME, biomeRegistry.entryOf(BiomeKeys.PLAINS)
		);
	}

	public static SerializedChunk fromChunk(ServerWorld world, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		List<SerializedChunk.SectionData> list = new ArrayList();
		ChunkSection[] chunkSections = chunk.getSectionArray();
		LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();

		for (int i = lightingProvider.getBottomY(); i < lightingProvider.getTopY(); i++) {
			int j = chunk.sectionCoordToIndex(i);
			boolean bl = j >= 0 && j < chunkSections.length;
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(chunkPos, i));
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.SKY).getLightSection(ChunkSectionPos.from(chunkPos, i));
			ChunkNibbleArray chunkNibbleArray3 = chunkNibbleArray != null && !chunkNibbleArray.isUninitialized() ? chunkNibbleArray.copy() : null;
			ChunkNibbleArray chunkNibbleArray4 = chunkNibbleArray2 != null && !chunkNibbleArray2.isUninitialized() ? chunkNibbleArray2.copy() : null;
			if (bl || chunkNibbleArray3 != null || chunkNibbleArray4 != null) {
				ChunkSection chunkSection = bl ? chunkSections[j].copy() : null;
				list.add(new SerializedChunk.SectionData(i, chunkSection, chunkNibbleArray3, chunkNibbleArray4));
			}
		}

		List<NbtCompound> list2 = new ArrayList(chunk.getBlockEntityPositions().size());

		for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
			NbtCompound nbtCompound = chunk.getPackedBlockEntityNbt(blockPos, world.getRegistryManager());
			if (nbtCompound != null) {
				list2.add(nbtCompound);
			}
		}

		List<NbtCompound> list3 = new ArrayList();
		long[] ls = null;
		if (chunk.getStatus().getChunkType() == ChunkType.PROTOCHUNK) {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			list3.addAll(protoChunk.getEntities());
			CarvingMask carvingMask = protoChunk.getCarvingMask();
			if (carvingMask != null) {
				ls = carvingMask.getMask();
			}
		}

		Map<Heightmap.Type, long[]> map = new EnumMap(Heightmap.Type.class);

		for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
			if (chunk.getStatus().getHeightmapTypes().contains(entry.getKey())) {
				long[] ms = ((Heightmap)entry.getValue()).asLongArray();
				map.put((Heightmap.Type)entry.getKey(), (long[])ms.clone());
			}
		}

		Chunk.TickSchedulers tickSchedulers = chunk.getTickSchedulers(world.getTime());
		ShortList[] shortLists = (ShortList[])Arrays.stream(chunk.getPostProcessingLists())
			.map(postProcessings -> postProcessings != null ? new ShortArrayList(postProcessings) : null)
			.toArray(ShortList[]::new);
		NbtCompound nbtCompound2 = writeStructures(StructureContext.from(world), chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences());
		return new SerializedChunk(
			world.getRegistryManager().get(RegistryKeys.BIOME),
			chunkPos,
			chunk.getBottomSectionCoord(),
			world.getTime(),
			chunk.getInhabitedTime(),
			chunk.getStatus(),
			Nullables.map(chunk.getBlendingData(), BlendingData::toSerialized),
			chunk.getBelowZeroRetrogen(),
			chunk.getUpgradeData().copy(),
			ls,
			map,
			tickSchedulers,
			shortLists,
			chunk.isLightOn(),
			list,
			list3,
			list2,
			nbtCompound2
		);
	}

	public NbtCompound serialize() {
		NbtCompound nbtCompound = NbtHelper.putDataVersion(new NbtCompound());
		nbtCompound.putInt("xPos", this.chunkPos.x);
		nbtCompound.putInt("yPos", this.minSectionY);
		nbtCompound.putInt("zPos", this.chunkPos.z);
		nbtCompound.putLong("LastUpdate", this.lastUpdateTime);
		nbtCompound.putLong("InhabitedTime", this.inhabitedTime);
		nbtCompound.putString("Status", Registries.CHUNK_STATUS.getId(this.chunkStatus).toString());
		if (this.blendingData != null) {
			BlendingData.Serialized.CODEC
				.encodeStart(NbtOps.INSTANCE, this.blendingData)
				.resultOrPartial(LOGGER::error)
				.ifPresent(blendingData -> nbtCompound.put("blending_data", blendingData));
		}

		if (this.belowZeroRetrogen != null) {
			BelowZeroRetrogen.CODEC
				.encodeStart(NbtOps.INSTANCE, this.belowZeroRetrogen)
				.resultOrPartial(LOGGER::error)
				.ifPresent(belowZeroRetrogen -> nbtCompound.put("below_zero_retrogen", belowZeroRetrogen));
		}

		if (!this.upgradeData.isDone()) {
			nbtCompound.put("UpgradeData", this.upgradeData.toNbt());
		}

		NbtList nbtList = new NbtList();
		Codec<ReadableContainer<RegistryEntry<Biome>>> codec = createCodec(this.biomeRegistry);

		for (SerializedChunk.SectionData sectionData : this.sectionData) {
			NbtCompound nbtCompound2 = new NbtCompound();
			ChunkSection chunkSection = sectionData.chunkSection;
			if (chunkSection != null) {
				nbtCompound2.put("block_states", CODEC.encodeStart(NbtOps.INSTANCE, chunkSection.getBlockStateContainer()).getOrThrow());
				nbtCompound2.put("biomes", codec.encodeStart(NbtOps.INSTANCE, chunkSection.getBiomeContainer()).getOrThrow());
			}

			if (sectionData.blockLight != null) {
				nbtCompound2.putByteArray("BlockLight", sectionData.blockLight.asByteArray());
			}

			if (sectionData.skyLight != null) {
				nbtCompound2.putByteArray("SkyLight", sectionData.skyLight.asByteArray());
			}

			if (!nbtCompound2.isEmpty()) {
				nbtCompound2.putByte("Y", (byte)sectionData.y);
				nbtList.add(nbtCompound2);
			}
		}

		nbtCompound.put("sections", nbtList);
		if (this.lightCorrect) {
			nbtCompound.putBoolean("isLightOn", true);
		}

		NbtList nbtList2 = new NbtList();
		nbtList2.addAll(this.blockEntities);
		nbtCompound.put("block_entities", nbtList2);
		if (this.chunkStatus.getChunkType() == ChunkType.PROTOCHUNK) {
			NbtList nbtList3 = new NbtList();
			nbtList3.addAll(this.entities);
			nbtCompound.put("entities", nbtList3);
			if (this.carvingMask != null) {
				nbtCompound.putLongArray("carving_mask", this.carvingMask);
			}
		}

		serializeTicks(nbtCompound, this.packedTicks);
		nbtCompound.put("PostProcessing", toNbt(this.postProcessingSections));
		NbtCompound nbtCompound3 = new NbtCompound();
		this.heightmaps.forEach((type, values) -> nbtCompound3.put(type.getName(), new NbtLongArray(values)));
		nbtCompound.put("Heightmaps", nbtCompound3);
		nbtCompound.put("structures", this.structureData);
		return nbtCompound;
	}

	private static void serializeTicks(NbtCompound nbt, Chunk.TickSchedulers schedulers) {
		NbtList nbtList = new NbtList();

		for (Tick<Block> tick : schedulers.blocks()) {
			nbtList.add(tick.toNbt(block -> Registries.BLOCK.getId(block).toString()));
		}

		nbt.put("block_ticks", nbtList);
		NbtList nbtList2 = new NbtList();

		for (Tick<Fluid> tick2 : schedulers.fluids()) {
			nbtList2.add(tick2.toNbt(fluid -> Registries.FLUID.getId(fluid).toString()));
		}

		nbt.put("fluid_ticks", nbtList2);
	}

	public static ChunkType getChunkType(@Nullable NbtCompound nbt) {
		return nbt != null ? ChunkStatus.byId(nbt.getString("Status")).getChunkType() : ChunkType.PROTOCHUNK;
	}

	@Nullable
	private static WorldChunk.EntityLoader getEntityLoadingCallback(ServerWorld world, List<NbtCompound> entities, List<NbtCompound> blockEntities) {
		return entities.isEmpty() && blockEntities.isEmpty() ? null : chunk -> {
			if (!entities.isEmpty()) {
				world.loadEntities(EntityType.streamFromNbt(entities, world, SpawnReason.LOAD));
			}

			for (NbtCompound nbtCompound : blockEntities) {
				boolean bl = nbtCompound.getBoolean("keepPacked");
				if (bl) {
					chunk.addPendingBlockEntityNbt(nbtCompound);
				} else {
					BlockPos blockPos = BlockEntity.posFromNbt(nbtCompound);
					BlockEntity blockEntity = BlockEntity.createFromNbt(blockPos, chunk.getBlockState(blockPos), nbtCompound, world.getRegistryManager());
					if (blockEntity != null) {
						chunk.setBlockEntity(blockEntity);
					}
				}
			}
		};
	}

	private static NbtCompound writeStructures(StructureContext context, ChunkPos pos, Map<Structure, StructureStart> starts, Map<Structure, LongSet> references) {
		NbtCompound nbtCompound = new NbtCompound();
		NbtCompound nbtCompound2 = new NbtCompound();
		Registry<Structure> registry = context.registryManager().get(RegistryKeys.STRUCTURE);

		for (Entry<Structure, StructureStart> entry : starts.entrySet()) {
			Identifier identifier = registry.getId((Structure)entry.getKey());
			nbtCompound2.put(identifier.toString(), ((StructureStart)entry.getValue()).toNbt(context, pos));
		}

		nbtCompound.put("starts", nbtCompound2);
		NbtCompound nbtCompound3 = new NbtCompound();

		for (Entry<Structure, LongSet> entry2 : references.entrySet()) {
			if (!((LongSet)entry2.getValue()).isEmpty()) {
				Identifier identifier2 = registry.getId((Structure)entry2.getKey());
				nbtCompound3.put(identifier2.toString(), new NbtLongArray((LongSet)entry2.getValue()));
			}
		}

		nbtCompound.put("References", nbtCompound3);
		return nbtCompound;
	}

	private static Map<Structure, StructureStart> readStructureStarts(StructureContext context, NbtCompound nbt, long worldSeed) {
		Map<Structure, StructureStart> map = Maps.<Structure, StructureStart>newHashMap();
		Registry<Structure> registry = context.registryManager().get(RegistryKeys.STRUCTURE);
		NbtCompound nbtCompound = nbt.getCompound("starts");

		for (String string : nbtCompound.getKeys()) {
			Identifier identifier = Identifier.tryParse(string);
			Structure structure = registry.get(identifier);
			if (structure == null) {
				LOGGER.error("Unknown structure start: {}", identifier);
			} else {
				StructureStart structureStart = StructureStart.fromNbt(context, nbtCompound.getCompound(string), worldSeed);
				if (structureStart != null) {
					map.put(structure, structureStart);
				}
			}
		}

		return map;
	}

	private static Map<Structure, LongSet> readStructureReferences(DynamicRegistryManager registryManager, ChunkPos pos, NbtCompound nbt) {
		Map<Structure, LongSet> map = Maps.<Structure, LongSet>newHashMap();
		Registry<Structure> registry = registryManager.get(RegistryKeys.STRUCTURE);
		NbtCompound nbtCompound = nbt.getCompound("References");

		for (String string : nbtCompound.getKeys()) {
			Identifier identifier = Identifier.tryParse(string);
			Structure structure = registry.get(identifier);
			if (structure == null) {
				LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", identifier, pos);
			} else {
				long[] ls = nbtCompound.getLongArray(string);
				if (ls.length != 0) {
					map.put(structure, new LongOpenHashSet(Arrays.stream(ls).filter(packedPos -> {
						ChunkPos chunkPos2 = new ChunkPos(packedPos);
						if (chunkPos2.getChebyshevDistance(pos) > 8) {
							LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", identifier, chunkPos2, pos);
							return false;
						} else {
							return true;
						}
					}).toArray()));
				}
			}
		}

		return map;
	}

	private static NbtList toNbt(ShortList[] lists) {
		NbtList nbtList = new NbtList();

		for (ShortList shortList : lists) {
			NbtList nbtList2 = new NbtList();
			if (shortList != null) {
				for (int i = 0; i < shortList.size(); i++) {
					nbtList2.add(NbtShort.of(shortList.getShort(i)));
				}
			}

			nbtList.add(nbtList2);
		}

		return nbtList;
	}

	public static class ChunkLoadingException extends NbtException {
		public ChunkLoadingException(String string) {
			super(string);
		}
	}

	public static record SectionData(int y, @Nullable ChunkSection chunkSection, @Nullable ChunkNibbleArray blockLight, @Nullable ChunkNibbleArray skyLight) {
	}
}
