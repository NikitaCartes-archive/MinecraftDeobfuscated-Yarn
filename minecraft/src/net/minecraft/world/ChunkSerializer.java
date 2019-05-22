package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureFeatures;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.world.biome.Biome;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
	private static final Logger LOGGER = LogManager.getLogger();

	public static ProtoChunk deserialize(
		ServerWorld serverWorld, StructureManager structureManager, PointOfInterestStorage pointOfInterestStorage, ChunkPos chunkPos, CompoundTag compoundTag
	) {
		ChunkGenerator<?> chunkGenerator = serverWorld.method_14178().getChunkGenerator();
		BiomeSource biomeSource = chunkGenerator.getBiomeSource();
		CompoundTag compoundTag2 = compoundTag.getCompound("Level");
		ChunkPos chunkPos2 = new ChunkPos(compoundTag2.getInt("xPos"), compoundTag2.getInt("zPos"));
		if (!Objects.equals(chunkPos, chunkPos2)) {
			LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", chunkPos, chunkPos, chunkPos2);
		}

		Biome[] biomes = new Biome[256];
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		if (compoundTag2.containsKey("Biomes", 11)) {
			int[] is = compoundTag2.getIntArray("Biomes");

			for (int i = 0; i < is.length; i++) {
				biomes[i] = Registry.BIOME.get(is[i]);
				if (biomes[i] == null) {
					biomes[i] = biomeSource.getBiome(mutable.set((i & 15) + chunkPos.getStartX(), 0, (i >> 4 & 15) + chunkPos.getStartZ()));
				}
			}
		} else {
			for (int j = 0; j < biomes.length; j++) {
				biomes[j] = biomeSource.getBiome(mutable.set((j & 15) + chunkPos.getStartX(), 0, (j >> 4 & 15) + chunkPos.getStartZ()));
			}
		}

		UpgradeData upgradeData = compoundTag2.containsKey("UpgradeData", 10)
			? new UpgradeData(compoundTag2.getCompound("UpgradeData"))
			: UpgradeData.NO_UPGRADE_DATA;
		ChunkTickScheduler<Block> chunkTickScheduler = new ChunkTickScheduler<>(
			block -> block == null || block.getDefaultState().isAir(), chunkPos, compoundTag2.getList("ToBeTicked", 9)
		);
		ChunkTickScheduler<Fluid> chunkTickScheduler2 = new ChunkTickScheduler<>(
			fluid -> fluid == null || fluid == Fluids.field_15906, chunkPos, compoundTag2.getList("LiquidsToBeTicked", 9)
		);
		boolean bl = compoundTag2.getBoolean("isLightOn");
		ListTag listTag = compoundTag2.getList("Sections", 10);
		int k = 16;
		ChunkSection[] chunkSections = new ChunkSection[16];
		boolean bl2 = serverWorld.getDimension().hasSkyLight();
		ChunkManager chunkManager = serverWorld.method_14178();
		LightingProvider lightingProvider = chunkManager.getLightingProvider();
		if (bl) {
			lightingProvider.method_20601(chunkPos, true);
		}

		for (int l = 0; l < listTag.size(); l++) {
			CompoundTag compoundTag3 = listTag.getCompoundTag(l);
			int m = compoundTag3.getByte("Y");
			if (compoundTag3.containsKey("Palette", 9) && compoundTag3.containsKey("BlockStates", 12)) {
				ChunkSection chunkSection = new ChunkSection(m << 4);
				chunkSection.getContainer().read(compoundTag3.getList("Palette", 10), compoundTag3.getLongArray("BlockStates"));
				chunkSection.calculateCounts();
				if (!chunkSection.isEmpty()) {
					chunkSections[m] = chunkSection;
				}

				pointOfInterestStorage.initForPalette(chunkPos, chunkSection);
			}

			if (bl) {
				if (compoundTag3.containsKey("BlockLight", 7)) {
					lightingProvider.queueData(LightType.field_9282, ChunkSectionPos.from(chunkPos, m), new ChunkNibbleArray(compoundTag3.getByteArray("BlockLight")));
				}

				if (bl2 && compoundTag3.containsKey("SkyLight", 7)) {
					lightingProvider.queueData(LightType.field_9284, ChunkSectionPos.from(chunkPos, m), new ChunkNibbleArray(compoundTag3.getByteArray("SkyLight")));
				}
			}
		}

		long n = compoundTag2.getLong("InhabitedTime");
		ChunkStatus.ChunkType chunkType = getChunkType(compoundTag);
		Chunk chunk;
		if (chunkType == ChunkStatus.ChunkType.field_12807) {
			TickScheduler<Block> tickScheduler;
			if (compoundTag2.containsKey("TileTicks", 9)) {
				tickScheduler = SimpleTickScheduler.method_20512(compoundTag2.getList("TileTicks", 10), Registry.BLOCK::getId, Registry.BLOCK::get);
			} else {
				tickScheduler = chunkTickScheduler;
			}

			TickScheduler<Fluid> tickScheduler2;
			if (compoundTag2.containsKey("LiquidTicks", 9)) {
				tickScheduler2 = SimpleTickScheduler.method_20512(compoundTag2.getList("LiquidTicks", 10), Registry.FLUID::getId, Registry.FLUID::get);
			} else {
				tickScheduler2 = chunkTickScheduler2;
			}

			chunk = new WorldChunk(
				serverWorld.getWorld(),
				chunkPos,
				biomes,
				upgradeData,
				tickScheduler,
				tickScheduler2,
				n,
				chunkSections,
				worldChunk -> writeEntities(compoundTag2, worldChunk)
			);
		} else {
			ProtoChunk protoChunk = new ProtoChunk(chunkPos, upgradeData, chunkSections, chunkTickScheduler, chunkTickScheduler2);
			chunk = protoChunk;
			protoChunk.setBiomeArray(biomes);
			protoChunk.setInhabitedTime(n);
			protoChunk.setStatus(ChunkStatus.get(compoundTag2.getString("Status")));
			if (protoChunk.getStatus().isAtLeast(ChunkStatus.field_12795)) {
				protoChunk.setLightingProvider(lightingProvider);
			}

			if (!bl && protoChunk.getStatus().isAtLeast(ChunkStatus.field_12805)) {
				for (BlockPos blockPos : BlockPos.iterate(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 255, chunkPos.getEndZ())) {
					if (chunk.getBlockState(blockPos).getLuminance() != 0) {
						protoChunk.addLightSource(blockPos);
					}
				}
			}
		}

		chunk.setLightOn(bl);
		CompoundTag compoundTag4 = compoundTag2.getCompound("Heightmaps");
		EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);

		for (Heightmap.Type type : chunk.getStatus().isSurfaceGenerated()) {
			String string = type.getName();
			if (compoundTag4.containsKey(string, 12)) {
				chunk.setHeightmap(type, compoundTag4.getLongArray(string));
			} else {
				enumSet.add(type);
			}
		}

		Heightmap.populateHeightmaps(chunk, enumSet);
		CompoundTag compoundTag5 = compoundTag2.getCompound("Structures");
		chunk.setStructureStarts(readStructureStarts(chunkGenerator, structureManager, biomeSource, compoundTag5));
		chunk.setStructureReferences(readStructureReferences(compoundTag5));
		if (compoundTag2.getBoolean("shouldSave")) {
			chunk.setShouldSave(true);
		}

		ListTag listTag2 = compoundTag2.getList("PostProcessing", 9);

		for (int o = 0; o < listTag2.size(); o++) {
			ListTag listTag3 = listTag2.getListTag(o);

			for (int p = 0; p < listTag3.size(); p++) {
				chunk.markBlockForPostProcessing(listTag3.getShort(p), o);
			}
		}

		if (chunkType == ChunkStatus.ChunkType.field_12807) {
			return new ReadOnlyChunk((WorldChunk)chunk);
		} else {
			ProtoChunk protoChunk2 = (ProtoChunk)chunk;
			ListTag listTag3 = compoundTag2.getList("Entities", 10);

			for (int p = 0; p < listTag3.size(); p++) {
				protoChunk2.addEntity(listTag3.getCompoundTag(p));
			}

			ListTag listTag4 = compoundTag2.getList("TileEntities", 10);

			for (int q = 0; q < listTag4.size(); q++) {
				CompoundTag compoundTag6 = listTag4.getCompoundTag(q);
				chunk.addPendingBlockEntityTag(compoundTag6);
			}

			ListTag listTag5 = compoundTag2.getList("Lights", 9);

			for (int r = 0; r < listTag5.size(); r++) {
				ListTag listTag6 = listTag5.getListTag(r);

				for (int s = 0; s < listTag6.size(); s++) {
					protoChunk2.addLightSource(listTag6.getShort(s), r);
				}
			}

			CompoundTag compoundTag6 = compoundTag2.getCompound("CarvingMasks");

			for (String string2 : compoundTag6.getKeys()) {
				GenerationStep.Carver carver = GenerationStep.Carver.valueOf(string2);
				protoChunk2.setCarvingMask(carver, BitSet.valueOf(compoundTag6.getByteArray(string2)));
			}

			return protoChunk2;
		}
	}

	public static CompoundTag serialize(ServerWorld serverWorld, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();
		compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag.put("Level", compoundTag2);
		compoundTag2.putInt("xPos", chunkPos.x);
		compoundTag2.putInt("zPos", chunkPos.z);
		compoundTag2.putLong("LastUpdate", serverWorld.getTime());
		compoundTag2.putLong("InhabitedTime", chunk.getInhabitedTime());
		compoundTag2.putString("Status", chunk.getStatus().getName());
		UpgradeData upgradeData = chunk.getUpgradeData();
		if (!upgradeData.method_12349()) {
			compoundTag2.put("UpgradeData", upgradeData.toTag());
		}

		ChunkSection[] chunkSections = chunk.getSectionArray();
		ListTag listTag = new ListTag();
		LightingProvider lightingProvider = serverWorld.method_14178().method_17293();

		for (int i = -1; i < 17; i++) {
			int j = i;
			ChunkSection chunkSection = (ChunkSection)Arrays.stream(chunkSections)
				.filter(chunkSectionx -> chunkSectionx != null && chunkSectionx.getYOffset() >> 4 == j)
				.findFirst()
				.orElse(WorldChunk.EMPTY_SECTION);
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.field_9282).getChunkLightArray(ChunkSectionPos.from(chunkPos, j));
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.field_9284).getChunkLightArray(ChunkSectionPos.from(chunkPos, j));
			if (chunkSection != WorldChunk.EMPTY_SECTION || chunkNibbleArray != null || chunkNibbleArray2 != null) {
				CompoundTag compoundTag3 = new CompoundTag();
				compoundTag3.putByte("Y", (byte)(j & 0xFF));
				if (chunkSection != WorldChunk.EMPTY_SECTION) {
					chunkSection.getContainer().write(compoundTag3, "Palette", "BlockStates");
				}

				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					compoundTag3.putByteArray("BlockLight", chunkNibbleArray.asByteArray());
				}

				if (chunkNibbleArray2 != null && !chunkNibbleArray2.isUninitialized()) {
					compoundTag3.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
				}

				listTag.add(compoundTag3);
			}
		}

		compoundTag2.put("Sections", listTag);
		if (chunk.isLightOn()) {
			compoundTag2.putBoolean("isLightOn", true);
		}

		Biome[] biomes = chunk.getBiomeArray();
		int[] is = biomes != null ? new int[biomes.length] : new int[0];
		if (biomes != null) {
			for (int k = 0; k < biomes.length; k++) {
				is[k] = Registry.BIOME.getRawId(biomes[k]);
			}
		}

		compoundTag2.putIntArray("Biomes", is);
		ListTag listTag2 = new ListTag();

		for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
			CompoundTag compoundTag3x = chunk.method_20598(blockPos);
			if (compoundTag3x != null) {
				listTag2.add(compoundTag3x);
			}
		}

		compoundTag2.put("TileEntities", listTag2);
		ListTag listTag3 = new ListTag();
		if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.field_12807) {
			WorldChunk worldChunk = (WorldChunk)chunk;
			worldChunk.method_12232(false);

			for (int l = 0; l < worldChunk.getEntitySectionArray().length; l++) {
				for (Entity entity : worldChunk.getEntitySectionArray()[l]) {
					CompoundTag compoundTag4 = new CompoundTag();
					if (entity.saveToTag(compoundTag4)) {
						worldChunk.method_12232(true);
						listTag3.add(compoundTag4);
					}
				}
			}
		} else {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			listTag3.addAll(protoChunk.getEntities());
			compoundTag2.put("Lights", toNbt(protoChunk.getLightSourcesBySection()));
			CompoundTag compoundTag3x = new CompoundTag();

			for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
				compoundTag3x.putByteArray(carver.toString(), chunk.getCarvingMask(carver).toByteArray());
			}

			compoundTag2.put("CarvingMasks", compoundTag3x);
		}

		compoundTag2.put("Entities", listTag3);
		TickScheduler<Block> tickScheduler = chunk.getBlockTickScheduler();
		if (tickScheduler instanceof ChunkTickScheduler) {
			compoundTag2.put("ToBeTicked", ((ChunkTickScheduler)tickScheduler).toNbt());
		} else if (tickScheduler instanceof SimpleTickScheduler) {
			compoundTag2.put("TileTicks", ((SimpleTickScheduler)tickScheduler).toTag(serverWorld.getTime()));
		} else {
			compoundTag2.put("TileTicks", serverWorld.method_14196().toTag(chunkPos));
		}

		TickScheduler<Fluid> tickScheduler2 = chunk.getFluidTickScheduler();
		if (tickScheduler2 instanceof ChunkTickScheduler) {
			compoundTag2.put("LiquidsToBeTicked", ((ChunkTickScheduler)tickScheduler2).toNbt());
		} else if (tickScheduler2 instanceof SimpleTickScheduler) {
			compoundTag2.put("LiquidTicks", ((SimpleTickScheduler)tickScheduler2).toTag(serverWorld.getTime()));
		} else {
			compoundTag2.put("LiquidTicks", serverWorld.method_14179().toTag(chunkPos));
		}

		compoundTag2.put("PostProcessing", toNbt(chunk.getPostProcessingLists()));
		CompoundTag compoundTag5 = new CompoundTag();

		for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
			if (chunk.getStatus().isSurfaceGenerated().contains(entry.getKey())) {
				compoundTag5.put(((Heightmap.Type)entry.getKey()).getName(), new LongArrayTag(((Heightmap)entry.getValue()).asLongArray()));
			}
		}

		compoundTag2.put("Heightmaps", compoundTag5);
		compoundTag2.put("Structures", writeStructures(chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences()));
		return compoundTag;
	}

	public static ChunkStatus.ChunkType getChunkType(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null) {
			ChunkStatus chunkStatus = ChunkStatus.get(compoundTag.getCompound("Level").getString("Status"));
			if (chunkStatus != null) {
				return chunkStatus.getChunkType();
			}
		}

		return ChunkStatus.ChunkType.field_12808;
	}

	private static void writeEntities(CompoundTag compoundTag, WorldChunk worldChunk) {
		ListTag listTag = compoundTag.getList("Entities", 10);
		World world = worldChunk.getWorld();

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			EntityType.loadEntityWithPassengers(compoundTag2, world, entity -> {
				worldChunk.addEntity(entity);
				return entity;
			});
			worldChunk.method_12232(true);
		}

		ListTag listTag2 = compoundTag.getList("TileEntities", 10);

		for (int j = 0; j < listTag2.size(); j++) {
			CompoundTag compoundTag3 = listTag2.getCompoundTag(j);
			boolean bl = compoundTag3.getBoolean("keepPacked");
			if (bl) {
				worldChunk.addPendingBlockEntityTag(compoundTag3);
			} else {
				BlockEntity blockEntity = BlockEntity.createFromTag(compoundTag3);
				if (blockEntity != null) {
					worldChunk.addBlockEntity(blockEntity);
				}
			}
		}
	}

	private static CompoundTag writeStructures(ChunkPos chunkPos, Map<String, StructureStart> map, Map<String, LongSet> map2) {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();

		for (Entry<String, StructureStart> entry : map.entrySet()) {
			compoundTag2.put((String)entry.getKey(), ((StructureStart)entry.getValue()).toTag(chunkPos.x, chunkPos.z));
		}

		compoundTag.put("Starts", compoundTag2);
		CompoundTag compoundTag3 = new CompoundTag();

		for (Entry<String, LongSet> entry2 : map2.entrySet()) {
			compoundTag3.put((String)entry2.getKey(), new LongArrayTag((LongSet)entry2.getValue()));
		}

		compoundTag.put("References", compoundTag3);
		return compoundTag;
	}

	private static Map<String, StructureStart> readStructureStarts(
		ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BiomeSource biomeSource, CompoundTag compoundTag
	) {
		Map<String, StructureStart> map = Maps.<String, StructureStart>newHashMap();
		CompoundTag compoundTag2 = compoundTag.getCompound("Starts");

		for (String string : compoundTag2.getKeys()) {
			map.put(string, StructureFeatures.readStructureStart(chunkGenerator, structureManager, biomeSource, compoundTag2.getCompound(string)));
		}

		return map;
	}

	private static Map<String, LongSet> readStructureReferences(CompoundTag compoundTag) {
		Map<String, LongSet> map = Maps.<String, LongSet>newHashMap();
		CompoundTag compoundTag2 = compoundTag.getCompound("References");

		for (String string : compoundTag2.getKeys()) {
			map.put(string, new LongOpenHashSet(compoundTag2.getLongArray(string)));
		}

		return map;
	}

	public static ListTag toNbt(ShortList[] shortLists) {
		ListTag listTag = new ListTag();

		for (ShortList shortList : shortLists) {
			ListTag listTag2 = new ListTag();
			if (shortList != null) {
				for (Short short_ : shortList) {
					listTag2.add(new ShortTag(short_));
				}
			}

			listTag.add(listTag2);
		}

		return listTag;
	}
}
