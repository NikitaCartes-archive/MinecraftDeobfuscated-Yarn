package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
	private static final Logger LOGGER = LogManager.getLogger();

	public static ProtoChunk deserialize(ServerWorld world, StructureManager structureManager, PointOfInterestStorage poiStorage, ChunkPos pos, NbtCompound nbt) {
		ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
		BiomeSource biomeSource = chunkGenerator.getBiomeSource();
		NbtCompound nbtCompound = nbt.getCompound("Level");
		ChunkPos chunkPos = new ChunkPos(nbtCompound.getInt("xPos"), nbtCompound.getInt("zPos"));
		if (!Objects.equals(pos, chunkPos)) {
			LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", pos, pos, chunkPos);
		}

		BiomeArray biomeArray = new BiomeArray(
			world.getRegistryManager().get(Registry.BIOME_KEY), pos, biomeSource, nbtCompound.contains("Biomes", 11) ? nbtCompound.getIntArray("Biomes") : null
		);
		UpgradeData upgradeData = nbtCompound.contains("UpgradeData", 10) ? new UpgradeData(nbtCompound.getCompound("UpgradeData")) : UpgradeData.NO_UPGRADE_DATA;
		ChunkTickScheduler<Block> chunkTickScheduler = new ChunkTickScheduler<>(
			block -> block == null || block.getDefaultState().isAir(), pos, nbtCompound.getList("ToBeTicked", 9)
		);
		ChunkTickScheduler<Fluid> chunkTickScheduler2 = new ChunkTickScheduler<>(
			fluid -> fluid == null || fluid == Fluids.EMPTY, pos, nbtCompound.getList("LiquidsToBeTicked", 9)
		);
		boolean bl = nbtCompound.getBoolean("isLightOn");
		NbtList nbtList = nbtCompound.getList("Sections", 10);
		int i = 16;
		ChunkSection[] chunkSections = new ChunkSection[16];
		boolean bl2 = world.getDimension().hasSkyLight();
		ChunkManager chunkManager = world.getChunkManager();
		LightingProvider lightingProvider = chunkManager.getLightingProvider();
		if (bl) {
			lightingProvider.setRetainData(pos, true);
		}

		for (int j = 0; j < nbtList.size(); j++) {
			NbtCompound nbtCompound2 = nbtList.getCompound(j);
			int k = nbtCompound2.getByte("Y");
			if (nbtCompound2.contains("Palette", 9) && nbtCompound2.contains("BlockStates", 12)) {
				ChunkSection chunkSection = new ChunkSection(k << 4);
				chunkSection.getContainer().read(nbtCompound2.getList("Palette", 10), nbtCompound2.getLongArray("BlockStates"));
				chunkSection.calculateCounts();
				if (!chunkSection.isEmpty()) {
					chunkSections[k] = chunkSection;
				}

				poiStorage.initForPalette(pos, chunkSection);
			}

			if (bl) {
				if (nbtCompound2.contains("BlockLight", 7)) {
					lightingProvider.enqueueSectionData(LightType.BLOCK, ChunkSectionPos.from(pos, k), new ChunkNibbleArray(nbtCompound2.getByteArray("BlockLight")), true);
				}

				if (bl2 && nbtCompound2.contains("SkyLight", 7)) {
					lightingProvider.enqueueSectionData(LightType.SKY, ChunkSectionPos.from(pos, k), new ChunkNibbleArray(nbtCompound2.getByteArray("SkyLight")), true);
				}
			}
		}

		long l = nbtCompound.getLong("InhabitedTime");
		ChunkStatus.ChunkType chunkType = getChunkType(nbt);
		Chunk chunk;
		if (chunkType == ChunkStatus.ChunkType.field_12807) {
			TickScheduler<Block> tickScheduler;
			if (nbtCompound.contains("TileTicks", 9)) {
				tickScheduler = SimpleTickScheduler.fromNbt(nbtCompound.getList("TileTicks", 10), Registry.BLOCK::getId, Registry.BLOCK::get);
			} else {
				tickScheduler = chunkTickScheduler;
			}

			TickScheduler<Fluid> tickScheduler2;
			if (nbtCompound.contains("LiquidTicks", 9)) {
				tickScheduler2 = SimpleTickScheduler.fromNbt(nbtCompound.getList("LiquidTicks", 10), Registry.FLUID::getId, Registry.FLUID::get);
			} else {
				tickScheduler2 = chunkTickScheduler2;
			}

			chunk = new WorldChunk(
				world.toServerWorld(), pos, biomeArray, upgradeData, tickScheduler, tickScheduler2, l, chunkSections, chunkx -> writeEntities(nbtCompound, chunkx)
			);
		} else {
			ProtoChunk protoChunk = new ProtoChunk(pos, upgradeData, chunkSections, chunkTickScheduler, chunkTickScheduler2);
			protoChunk.setBiomes(biomeArray);
			chunk = protoChunk;
			protoChunk.setInhabitedTime(l);
			protoChunk.setStatus(ChunkStatus.byId(nbtCompound.getString("Status")));
			if (protoChunk.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
				protoChunk.setLightingProvider(lightingProvider);
			}

			if (!bl && protoChunk.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
				for (BlockPos blockPos : BlockPos.iterate(pos.getStartX(), 0, pos.getStartZ(), pos.getEndX(), 255, pos.getEndZ())) {
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
			if (nbtCompound3.contains(string, 12)) {
				chunk.setHeightmap(type, nbtCompound3.getLongArray(string));
			} else {
				enumSet.add(type);
			}
		}

		Heightmap.populateHeightmaps(chunk, enumSet);
		NbtCompound nbtCompound4 = nbtCompound.getCompound("Structures");
		chunk.setStructureStarts(readStructureStarts(structureManager, nbtCompound4, world.getSeed()));
		chunk.setStructureReferences(readStructureReferences(pos, nbtCompound4));
		if (nbtCompound.getBoolean("shouldSave")) {
			chunk.setShouldSave(true);
		}

		NbtList nbtList2 = nbtCompound.getList("PostProcessing", 9);

		for (int m = 0; m < nbtList2.size(); m++) {
			NbtList nbtList3 = nbtList2.getList(m);

			for (int n = 0; n < nbtList3.size(); n++) {
				chunk.markBlockForPostProcessing(nbtList3.getShort(n), m);
			}
		}

		if (chunkType == ChunkStatus.ChunkType.field_12807) {
			return new ReadOnlyChunk((WorldChunk)chunk);
		} else {
			ProtoChunk protoChunk2 = (ProtoChunk)chunk;
			NbtList nbtList3 = nbtCompound.getList("Entities", 10);

			for (int n = 0; n < nbtList3.size(); n++) {
				protoChunk2.addEntity(nbtList3.getCompound(n));
			}

			NbtList nbtList4 = nbtCompound.getList("TileEntities", 10);

			for (int o = 0; o < nbtList4.size(); o++) {
				NbtCompound nbtCompound5 = nbtList4.getCompound(o);
				chunk.addPendingBlockEntityNbt(nbtCompound5);
			}

			NbtList nbtList5 = nbtCompound.getList("Lights", 9);

			for (int p = 0; p < nbtList5.size(); p++) {
				NbtList nbtList6 = nbtList5.getList(p);

				for (int q = 0; q < nbtList6.size(); q++) {
					protoChunk2.addLightSource(nbtList6.getShort(q), p);
				}
			}

			NbtCompound nbtCompound5 = nbtCompound.getCompound("CarvingMasks");

			for (String string2 : nbtCompound5.getKeys()) {
				GenerationStep.Carver carver = GenerationStep.Carver.valueOf(string2);
				protoChunk2.setCarvingMask(carver, BitSet.valueOf(nbtCompound5.getByteArray(string2)));
			}

			return protoChunk2;
		}
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
		boolean bl = chunk.isLightOn();

		for (int i = -1; i < 17; i++) {
			int j = i;
			ChunkSection chunkSection = (ChunkSection)Arrays.stream(chunkSections)
				.filter(chunkSectionx -> chunkSectionx != null && chunkSectionx.getYOffset() >> 4 == j)
				.findFirst()
				.orElse(WorldChunk.EMPTY_SECTION);
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(chunkPos, j));
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.SKY).getLightSection(ChunkSectionPos.from(chunkPos, j));
			if (chunkSection != WorldChunk.EMPTY_SECTION || chunkNibbleArray != null || chunkNibbleArray2 != null) {
				NbtCompound nbtCompound3 = new NbtCompound();
				nbtCompound3.putByte("Y", (byte)(j & 0xFF));
				if (chunkSection != WorldChunk.EMPTY_SECTION) {
					chunkSection.getContainer().write(nbtCompound3, "Palette", "BlockStates");
				}

				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					nbtCompound3.putByteArray("BlockLight", chunkNibbleArray.asByteArray());
				}

				if (chunkNibbleArray2 != null && !chunkNibbleArray2.isUninitialized()) {
					nbtCompound3.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
				}

				nbtList.add(nbtCompound3);
			}
		}

		nbtCompound2.put("Sections", nbtList);
		if (bl) {
			nbtCompound2.putBoolean("isLightOn", true);
		}

		BiomeArray biomeArray = chunk.getBiomeArray();
		if (biomeArray != null) {
			nbtCompound2.putIntArray("Biomes", biomeArray.toIntArray());
		}

		NbtList nbtList2 = new NbtList();

		for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
			NbtCompound nbtCompound4 = chunk.getPackedBlockEntityNbt(blockPos);
			if (nbtCompound4 != null) {
				nbtList2.add(nbtCompound4);
			}
		}

		nbtCompound2.put("TileEntities", nbtList2);
		NbtList nbtList3 = new NbtList();
		if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.field_12807) {
			WorldChunk worldChunk = (WorldChunk)chunk;
			worldChunk.setUnsaved(false);

			for (int k = 0; k < worldChunk.getEntitySectionArray().length; k++) {
				for (Entity entity : worldChunk.getEntitySectionArray()[k]) {
					NbtCompound nbtCompound5 = new NbtCompound();
					if (entity.saveNbt(nbtCompound5)) {
						worldChunk.setUnsaved(true);
						nbtList3.add(nbtCompound5);
					}
				}
			}
		} else {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			nbtList3.addAll(protoChunk.getEntities());
			nbtCompound2.put("Lights", toNbt(protoChunk.getLightSourcesBySection()));
			NbtCompound nbtCompound4 = new NbtCompound();

			for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
				BitSet bitSet = protoChunk.getCarvingMask(carver);
				if (bitSet != null) {
					nbtCompound4.putByteArray(carver.toString(), bitSet.toByteArray());
				}
			}

			nbtCompound2.put("CarvingMasks", nbtCompound4);
		}

		nbtCompound2.put("Entities", nbtList3);
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
		NbtCompound nbtCompound3x = new NbtCompound();

		for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
			if (chunk.getStatus().getHeightmapTypes().contains(entry.getKey())) {
				nbtCompound3x.put(((Heightmap.Type)entry.getKey()).getName(), new NbtLongArray(((Heightmap)entry.getValue()).asLongArray()));
			}
		}

		nbtCompound2.put("Heightmaps", nbtCompound3x);
		nbtCompound2.put("Structures", writeStructures(chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences()));
		return nbtCompound;
	}

	public static ChunkStatus.ChunkType getChunkType(@Nullable NbtCompound nbt) {
		if (nbt != null) {
			ChunkStatus chunkStatus = ChunkStatus.byId(nbt.getCompound("Level").getString("Status"));
			if (chunkStatus != null) {
				return chunkStatus.getChunkType();
			}
		}

		return ChunkStatus.ChunkType.field_12808;
	}

	private static void writeEntities(NbtCompound tag, WorldChunk chunk) {
		NbtList nbtList = tag.getList("Entities", 10);
		World world = chunk.getWorld();

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			EntityType.loadEntityWithPassengers(nbtCompound, world, entity -> {
				chunk.addEntity(entity);
				return entity;
			});
			chunk.setUnsaved(true);
		}

		NbtList nbtList2 = tag.getList("TileEntities", 10);

		for (int j = 0; j < nbtList2.size(); j++) {
			NbtCompound nbtCompound2 = nbtList2.getCompound(j);
			boolean bl = nbtCompound2.getBoolean("keepPacked");
			if (bl) {
				chunk.addPendingBlockEntityNbt(nbtCompound2);
			} else {
				BlockPos blockPos = new BlockPos(nbtCompound2.getInt("x"), nbtCompound2.getInt("y"), nbtCompound2.getInt("z"));
				BlockEntity blockEntity = BlockEntity.createFromTag(chunk.getBlockState(blockPos), nbtCompound2);
				if (blockEntity != null) {
					chunk.addBlockEntity(blockEntity);
				}
			}
		}
	}

	private static NbtCompound writeStructures(
		ChunkPos pos, Map<StructureFeature<?>, StructureStart<?>> structureStarts, Map<StructureFeature<?>, LongSet> structureReferences
	) {
		NbtCompound nbtCompound = new NbtCompound();
		NbtCompound nbtCompound2 = new NbtCompound();

		for (Entry<StructureFeature<?>, StructureStart<?>> entry : structureStarts.entrySet()) {
			nbtCompound2.put(((StructureFeature)entry.getKey()).getName(), ((StructureStart)entry.getValue()).toTag(pos.x, pos.z));
		}

		nbtCompound.put("Starts", nbtCompound2);
		NbtCompound nbtCompound3 = new NbtCompound();

		for (Entry<StructureFeature<?>, LongSet> entry2 : structureReferences.entrySet()) {
			nbtCompound3.put(((StructureFeature)entry2.getKey()).getName(), new NbtLongArray((LongSet)entry2.getValue()));
		}

		nbtCompound.put("References", nbtCompound3);
		return nbtCompound;
	}

	private static Map<StructureFeature<?>, StructureStart<?>> readStructureStarts(StructureManager structureManager, NbtCompound tag, long worldSeed) {
		Map<StructureFeature<?>, StructureStart<?>> map = Maps.<StructureFeature<?>, StructureStart<?>>newHashMap();
		NbtCompound nbtCompound = tag.getCompound("Starts");

		for (String string : nbtCompound.getKeys()) {
			String string2 = string.toLowerCase(Locale.ROOT);
			StructureFeature<?> structureFeature = (StructureFeature<?>)StructureFeature.STRUCTURES.get(string2);
			if (structureFeature == null) {
				LOGGER.error("Unknown structure start: {}", string2);
			} else {
				StructureStart<?> structureStart = StructureFeature.readStructureStart(structureManager, nbtCompound.getCompound(string), worldSeed);
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
			map.put(StructureFeature.STRUCTURES.get(string.toLowerCase(Locale.ROOT)), new LongOpenHashSet(Arrays.stream(nbtCompound.getLongArray(string)).filter(l -> {
				ChunkPos chunkPos2 = new ChunkPos(l);
				if (chunkPos2.method_24022(pos) > 8) {
					LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", string, chunkPos2, pos);
					return false;
				} else {
					return true;
				}
			}).toArray()));
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
