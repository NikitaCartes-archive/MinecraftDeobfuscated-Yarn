package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.class_6625;
import net.minecraft.class_6643;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtShort;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
	private static final Codec<PalettedContainer<BlockState>> CODEC = PalettedContainer.createCodec(
		Block.STATE_IDS, BlockState.CODEC, PalettedContainer.PaletteProvider.BLOCK_STATE, Blocks.AIR.getDefaultState()
	);
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String UPGRADE_DATA_KEY = "UpgradeData";

	public static ProtoChunk deserialize(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt) {
		NbtCompound nbtCompound = nbt.getCompound("Level");
		ChunkPos chunkPos2 = new ChunkPos(nbtCompound.getInt("xPos"), nbtCompound.getInt("zPos"));
		if (!Objects.equals(chunkPos, chunkPos2)) {
			LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", chunkPos, chunkPos, chunkPos2);
		}

		UpgradeData upgradeData = nbtCompound.contains("UpgradeData", NbtElement.COMPOUND_TYPE)
			? new UpgradeData(nbtCompound.getCompound("UpgradeData"), world)
			: UpgradeData.NO_UPGRADE_DATA;
		ChunkTickScheduler<Block> chunkTickScheduler = new ChunkTickScheduler<>(
			block -> block == null || block.getDefaultState().isAir(), chunkPos, nbtCompound.getList("ToBeTicked", NbtElement.LIST_TYPE), world
		);
		ChunkTickScheduler<Fluid> chunkTickScheduler2 = new ChunkTickScheduler<>(
			fluid -> fluid == null || fluid == Fluids.EMPTY, chunkPos, nbtCompound.getList("LiquidsToBeTicked", NbtElement.LIST_TYPE), world
		);
		boolean bl = nbtCompound.getBoolean("isLightOn");
		NbtList nbtList = nbtCompound.getList("Sections", NbtElement.COMPOUND_TYPE);
		int i = world.countVerticalSections();
		ChunkSection[] chunkSections = new ChunkSection[i];
		boolean bl2 = world.getDimension().hasSkyLight();
		ChunkManager chunkManager = world.getChunkManager();
		LightingProvider lightingProvider = chunkManager.getLightingProvider();
		if (bl) {
			lightingProvider.setRetainData(chunkPos, true);
		}

		Registry<Biome> registry = world.getRegistryManager().get(Registry.BIOME_KEY);
		Codec<PalettedContainer<Biome>> codec = createCodec(registry);

		for (int j = 0; j < nbtList.size(); j++) {
			NbtCompound nbtCompound2 = nbtList.getCompound(j);
			int k = nbtCompound2.getByte("Y");
			int l = world.sectionCoordToIndex(k);
			if (l >= 0 && l < chunkSections.length) {
				PalettedContainer<BlockState> palettedContainer;
				if (nbtCompound2.contains("block_states", NbtElement.COMPOUND_TYPE)) {
					palettedContainer = CODEC.parse(NbtOps.INSTANCE, nbtCompound2.getCompound("block_states"))
						.promotePartial(errorMessage -> logRecoverableError(chunkPos, k, errorMessage))
						.getOrThrow(false, LOGGER::error);
				} else {
					palettedContainer = new PalettedContainer<>(Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE);
				}

				PalettedContainer<Biome> palettedContainer2;
				if (nbtCompound2.contains("biomes", NbtElement.COMPOUND_TYPE)) {
					palettedContainer2 = codec.parse(NbtOps.INSTANCE, nbtCompound2.getCompound("biomes"))
						.promotePartial(errorMessage -> logRecoverableError(chunkPos, k, errorMessage))
						.getOrThrow(false, LOGGER::error);
				} else {
					palettedContainer2 = new PalettedContainer<>(registry, registry.getOrThrow(BiomeKeys.PLAINS), PalettedContainer.PaletteProvider.BIOME);
				}

				ChunkSection chunkSection = new ChunkSection(k, palettedContainer, palettedContainer2);
				chunkSections[l] = chunkSection;
				poiStorage.initForPalette(chunkPos, chunkSection);
			}

			if (bl) {
				if (nbtCompound2.contains("BlockLight", NbtElement.BYTE_ARRAY_TYPE)) {
					lightingProvider.enqueueSectionData(
						LightType.BLOCK, ChunkSectionPos.from(chunkPos, k), new ChunkNibbleArray(nbtCompound2.getByteArray("BlockLight")), true
					);
				}

				if (bl2 && nbtCompound2.contains("SkyLight", NbtElement.BYTE_ARRAY_TYPE)) {
					lightingProvider.enqueueSectionData(LightType.SKY, ChunkSectionPos.from(chunkPos, k), new ChunkNibbleArray(nbtCompound2.getByteArray("SkyLight")), true);
				}
			}
		}

		long m = nbtCompound.getLong("InhabitedTime");
		ChunkStatus.ChunkType chunkType = getChunkType(nbt);
		Chunk chunk;
		if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
			TickScheduler<Block> tickScheduler;
			if (nbtCompound.contains("TileTicks", NbtElement.LIST_TYPE)) {
				tickScheduler = SimpleTickScheduler.fromNbt(nbtCompound.getList("TileTicks", NbtElement.COMPOUND_TYPE), Registry.BLOCK::getId, Registry.BLOCK::get);
			} else {
				tickScheduler = chunkTickScheduler;
			}

			TickScheduler<Fluid> tickScheduler2;
			if (nbtCompound.contains("LiquidTicks", NbtElement.LIST_TYPE)) {
				tickScheduler2 = SimpleTickScheduler.fromNbt(nbtCompound.getList("LiquidTicks", NbtElement.COMPOUND_TYPE), Registry.FLUID::getId, Registry.FLUID::get);
			} else {
				tickScheduler2 = chunkTickScheduler2;
			}

			chunk = new WorldChunk(
				world.toServerWorld(), chunkPos, upgradeData, tickScheduler, tickScheduler2, m, chunkSections, worldChunk -> loadEntities(world, nbtCompound, worldChunk)
			);
		} else {
			ProtoChunk protoChunk = new ProtoChunk(chunkPos, upgradeData, chunkSections, chunkTickScheduler, chunkTickScheduler2, world, registry);
			chunk = protoChunk;
			protoChunk.setInhabitedTime(m);
			protoChunk.setStatus(ChunkStatus.byId(nbtCompound.getString("Status")));
			if (protoChunk.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
				protoChunk.setLightingProvider(lightingProvider);
			}

			if (!bl && protoChunk.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
				for (BlockPos blockPos : BlockPos.iterate(
					chunkPos.getStartX(), world.getBottomY(), chunkPos.getStartZ(), chunkPos.getEndX(), world.getTopY() - 1, chunkPos.getEndZ()
				)) {
					if (chunk.getBlockState(blockPos).getLuminance() != 0) {
						protoChunk.addLightSource(blockPos);
					}
				}
			}
		}

		chunk.setLightOn(bl);
		NbtCompound nbtCompound3 = nbtCompound.getCompound("Heightmaps");
		EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);

		for (Heightmap.Type type : chunk.getStatus().getHeightmapTypes()) {
			String string = type.getName();
			if (nbtCompound3.contains(string, NbtElement.LONG_ARRAY_TYPE)) {
				chunk.setHeightmap(type, nbtCompound3.getLongArray(string));
			} else {
				enumSet.add(type);
			}
		}

		Heightmap.populateHeightmaps(chunk, enumSet);
		NbtCompound nbtCompound4 = nbtCompound.getCompound("Structures");
		chunk.setStructureStarts(readStructureStarts(class_6625.method_38713(world), nbtCompound4, world.getSeed()));
		chunk.setStructureReferences(readStructureReferences(chunkPos, nbtCompound4));
		if (nbtCompound.getBoolean("shouldSave")) {
			chunk.setShouldSave(true);
		}

		NbtList nbtList2 = nbtCompound.getList("PostProcessing", NbtElement.LIST_TYPE);

		for (int n = 0; n < nbtList2.size(); n++) {
			NbtList nbtList3 = nbtList2.getList(n);

			for (int o = 0; o < nbtList3.size(); o++) {
				chunk.markBlockForPostProcessing(nbtList3.getShort(o), n);
			}
		}

		if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
			return new ReadOnlyChunk((WorldChunk)chunk, false);
		} else {
			ProtoChunk protoChunk2 = (ProtoChunk)chunk;
			NbtList nbtList3 = nbtCompound.getList("Entities", NbtElement.COMPOUND_TYPE);

			for (int o = 0; o < nbtList3.size(); o++) {
				protoChunk2.addEntity(nbtList3.getCompound(o));
			}

			NbtList nbtList4 = nbtCompound.getList("TileEntities", NbtElement.COMPOUND_TYPE);

			for (int p = 0; p < nbtList4.size(); p++) {
				NbtCompound nbtCompound5 = nbtList4.getCompound(p);
				chunk.addPendingBlockEntityNbt(nbtCompound5);
			}

			NbtList nbtList5 = nbtCompound.getList("Lights", NbtElement.LIST_TYPE);

			for (int q = 0; q < nbtList5.size(); q++) {
				NbtList nbtList6 = nbtList5.getList(q);

				for (int r = 0; r < nbtList6.size(); r++) {
					protoChunk2.addLightSource(nbtList6.getShort(r), q);
				}
			}

			NbtCompound nbtCompound5 = nbtCompound.getCompound("CarvingMasks");

			for (String string2 : nbtCompound5.getKeys()) {
				GenerationStep.Carver carver = GenerationStep.Carver.valueOf(string2);
				protoChunk2.setCarvingMask(carver, new class_6643(nbtCompound5.getLongArray(string2), chunk.getBottomY()));
			}

			return protoChunk2;
		}
	}

	private static void logRecoverableError(ChunkPos chunkPos, int y, String message) {
		LOGGER.error("Recoverable errors when loading section [" + chunkPos.x + ", " + y + ", " + chunkPos.z + "]: " + message);
	}

	private static Codec<PalettedContainer<Biome>> createCodec(Registry<Biome> biomeRegistry) {
		return PalettedContainer.createCodec(biomeRegistry, biomeRegistry, PalettedContainer.PaletteProvider.BIOME, biomeRegistry.getOrThrow(BiomeKeys.PLAINS));
	}

	public static NbtCompound serialize(ServerWorld world, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		NbtCompound nbtCompound = new NbtCompound();
		NbtCompound nbtCompound2 = new NbtCompound();
		nbtCompound.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		nbtCompound.put("Level", nbtCompound2);
		nbtCompound2.putInt("xPos", chunkPos.x);
		nbtCompound2.putInt("zPos", chunkPos.z);
		nbtCompound2.putLong("LastUpdate", world.getTime());
		nbtCompound2.putLong("InhabitedTime", chunk.getInhabitedTime());
		nbtCompound2.putString("Status", chunk.getStatus().getId());
		UpgradeData upgradeData = chunk.getUpgradeData();
		if (!upgradeData.isDone()) {
			nbtCompound2.put("UpgradeData", upgradeData.toNbt());
		}

		ChunkSection[] chunkSections = chunk.getSectionArray();
		NbtList nbtList = new NbtList();
		LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();
		Registry<Biome> registry = world.getRegistryManager().get(Registry.BIOME_KEY);
		Codec<PalettedContainer<Biome>> codec = createCodec(registry);
		boolean bl = chunk.isLightOn();

		for (int i = lightingProvider.getBottomY(); i < lightingProvider.getTopY(); i++) {
			int j = chunk.sectionCoordToIndex(i);
			boolean bl2 = j >= 0 && j < chunkSections.length;
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(chunkPos, i));
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.SKY).getLightSection(ChunkSectionPos.from(chunkPos, i));
			if (bl2 || chunkNibbleArray != null || chunkNibbleArray2 != null) {
				NbtCompound nbtCompound3 = new NbtCompound();
				if (bl2) {
					ChunkSection chunkSection = chunkSections[j];
					nbtCompound3.put("block_states", CODEC.encodeStart(NbtOps.INSTANCE, chunkSection.getBlockStateContainer()).getOrThrow(false, LOGGER::error));
					nbtCompound3.put("biomes", codec.encodeStart(NbtOps.INSTANCE, chunkSection.getBiomeContainer()).getOrThrow(false, LOGGER::error));
				}

				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					nbtCompound3.putByteArray("BlockLight", chunkNibbleArray.asByteArray());
				}

				if (chunkNibbleArray2 != null && !chunkNibbleArray2.isUninitialized()) {
					nbtCompound3.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
				}

				if (!nbtCompound3.isEmpty()) {
					nbtCompound3.putByte("Y", (byte)i);
					nbtList.add(nbtCompound3);
				}
			}
		}

		nbtCompound2.put("Sections", nbtList);
		if (bl) {
			nbtCompound2.putBoolean("isLightOn", true);
		}

		NbtList nbtList2 = new NbtList();

		for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
			NbtCompound nbtCompound4 = chunk.getPackedBlockEntityNbt(blockPos);
			if (nbtCompound4 != null) {
				nbtList2.add(nbtCompound4);
			}
		}

		nbtCompound2.put("TileEntities", nbtList2);
		if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK) {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			NbtList nbtList3 = new NbtList();
			nbtList3.addAll(protoChunk.getEntities());
			nbtCompound2.put("Entities", nbtList3);
			nbtCompound2.put("Lights", toNbt(protoChunk.getLightSourcesBySection()));
			NbtCompound nbtCompound4 = new NbtCompound();

			for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
				class_6643 lv = protoChunk.getCarvingMask(carver);
				if (lv != null) {
					nbtCompound4.putLongArray(carver.toString(), lv.method_38864());
				}
			}

			nbtCompound2.put("CarvingMasks", nbtCompound4);
		}

		TickScheduler<Block> tickScheduler = chunk.getBlockTickScheduler();
		if (tickScheduler instanceof ChunkTickScheduler) {
			nbtCompound2.put("ToBeTicked", ((ChunkTickScheduler)tickScheduler).toNbt());
		} else if (tickScheduler instanceof SimpleTickScheduler) {
			nbtCompound2.put("TileTicks", ((SimpleTickScheduler)tickScheduler).toNbt());
		} else {
			nbtCompound2.put("TileTicks", world.getBlockTickScheduler().toNbt(chunkPos));
		}

		TickScheduler<Fluid> tickScheduler2 = chunk.getFluidTickScheduler();
		if (tickScheduler2 instanceof ChunkTickScheduler) {
			nbtCompound2.put("LiquidsToBeTicked", ((ChunkTickScheduler)tickScheduler2).toNbt());
		} else if (tickScheduler2 instanceof SimpleTickScheduler) {
			nbtCompound2.put("LiquidTicks", ((SimpleTickScheduler)tickScheduler2).toNbt());
		} else {
			nbtCompound2.put("LiquidTicks", world.getFluidTickScheduler().toNbt(chunkPos));
		}

		nbtCompound2.put("PostProcessing", toNbt(chunk.getPostProcessingLists()));
		NbtCompound nbtCompound4 = new NbtCompound();

		for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
			if (chunk.getStatus().getHeightmapTypes().contains(entry.getKey())) {
				nbtCompound4.put(((Heightmap.Type)entry.getKey()).getName(), new NbtLongArray(((Heightmap)entry.getValue()).asLongArray()));
			}
		}

		nbtCompound2.put("Heightmaps", nbtCompound4);
		nbtCompound2.put("Structures", writeStructures(class_6625.method_38713(world), chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences()));
		return nbtCompound;
	}

	public static ChunkStatus.ChunkType getChunkType(@Nullable NbtCompound nbt) {
		if (nbt != null) {
			ChunkStatus chunkStatus = ChunkStatus.byId(nbt.getCompound("Level").getString("Status"));
			if (chunkStatus != null) {
				return chunkStatus.getChunkType();
			}
		}

		return ChunkStatus.ChunkType.PROTOCHUNK;
	}

	private static void loadEntities(ServerWorld world, NbtCompound nbt, WorldChunk chunk) {
		if (nbt.contains("Entities", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("Entities", NbtElement.COMPOUND_TYPE);
			if (!nbtList.isEmpty()) {
				world.loadEntities(EntityType.streamFromNbt(nbtList, world));
			}
		}

		NbtList nbtList = nbt.getList("TileEntities", NbtElement.COMPOUND_TYPE);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			boolean bl = nbtCompound.getBoolean("keepPacked");
			if (bl) {
				chunk.addPendingBlockEntityNbt(nbtCompound);
			} else {
				BlockPos blockPos = BlockEntity.posFromNbt(nbtCompound);
				BlockEntity blockEntity = BlockEntity.createFromNbt(blockPos, chunk.getBlockState(blockPos), nbtCompound);
				if (blockEntity != null) {
					chunk.setBlockEntity(blockEntity);
				}
			}
		}
	}

	private static NbtCompound writeStructures(
		class_6625 arg, ChunkPos pos, Map<StructureFeature<?>, StructureStart<?>> starts, Map<StructureFeature<?>, LongSet> references
	) {
		NbtCompound nbtCompound = new NbtCompound();
		NbtCompound nbtCompound2 = new NbtCompound();

		for (Entry<StructureFeature<?>, StructureStart<?>> entry : starts.entrySet()) {
			nbtCompound2.put(((StructureFeature)entry.getKey()).getName(), ((StructureStart)entry.getValue()).toNbt(arg, pos));
		}

		nbtCompound.put("Starts", nbtCompound2);
		NbtCompound nbtCompound3 = new NbtCompound();

		for (Entry<StructureFeature<?>, LongSet> entry2 : references.entrySet()) {
			nbtCompound3.put(((StructureFeature)entry2.getKey()).getName(), new NbtLongArray((LongSet)entry2.getValue()));
		}

		nbtCompound.put("References", nbtCompound3);
		return nbtCompound;
	}

	private static Map<StructureFeature<?>, StructureStart<?>> readStructureStarts(class_6625 arg, NbtCompound nbt, long worldSeed) {
		Map<StructureFeature<?>, StructureStart<?>> map = Maps.<StructureFeature<?>, StructureStart<?>>newHashMap();
		NbtCompound nbtCompound = nbt.getCompound("Starts");

		for (String string : nbtCompound.getKeys()) {
			String string2 = string.toLowerCase(Locale.ROOT);
			StructureFeature<?> structureFeature = (StructureFeature<?>)StructureFeature.STRUCTURES.get(string2);
			if (structureFeature == null) {
				LOGGER.error("Unknown structure start: {}", string2);
			} else {
				StructureStart<?> structureStart = StructureFeature.readStructureStart(arg, nbtCompound.getCompound(string), worldSeed);
				if (structureStart != null) {
					map.put(structureFeature, structureStart);
				}
			}
		}

		return map;
	}

	private static Map<StructureFeature<?>, LongSet> readStructureReferences(ChunkPos pos, NbtCompound nbt) {
		Map<StructureFeature<?>, LongSet> map = Maps.<StructureFeature<?>, LongSet>newHashMap();
		NbtCompound nbtCompound = nbt.getCompound("References");

		for (String string : nbtCompound.getKeys()) {
			String string2 = string.toLowerCase(Locale.ROOT);
			StructureFeature<?> structureFeature = (StructureFeature<?>)StructureFeature.STRUCTURES.get(string2);
			if (structureFeature == null) {
				LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", string2, pos);
			} else {
				map.put(structureFeature, new LongOpenHashSet(Arrays.stream(nbtCompound.getLongArray(string)).filter(packedPos -> {
					ChunkPos chunkPos2 = new ChunkPos(packedPos);
					if (chunkPos2.getChebyshevDistance(pos) > 8) {
						LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", string2, chunkPos2, pos);
						return false;
					} else {
						return true;
					}
				}).toArray()));
			}
		}

		return map;
	}

	public static NbtList toNbt(ShortList[] lists) {
		NbtList nbtList = new NbtList();

		for (ShortList shortList : lists) {
			NbtList nbtList2 = new NbtList();
			if (shortList != null) {
				for (Short short_ : shortList) {
					nbtList2.add(NbtShort.of(short_));
				}
			}

			nbtList.add(nbtList2);
		}

		return nbtList;
	}
}
