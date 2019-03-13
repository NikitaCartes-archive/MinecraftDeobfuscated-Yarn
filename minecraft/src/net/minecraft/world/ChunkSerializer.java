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
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeatures;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
	private static final Logger LOGGER = LogManager.getLogger();

	public static ProtoChunk method_12395(World world, StructureManager structureManager, ChunkPos chunkPos, CompoundTag compoundTag) {
		ChunkGenerator<?> chunkGenerator = world.method_8398().getChunkGenerator();
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
					biomes[i] = biomeSource.method_8758(mutable.set((i & 15) + chunkPos.getStartX(), 0, (i >> 4 & 15) + chunkPos.getStartZ()));
				}
			}
		} else {
			for (int j = 0; j < biomes.length; j++) {
				biomes[j] = biomeSource.method_8758(mutable.set((j & 15) + chunkPos.getStartX(), 0, (j >> 4 & 15) + chunkPos.getStartZ()));
			}
		}

		UpgradeData upgradeData = compoundTag2.containsKey("UpgradeData", 10)
			? new UpgradeData(compoundTag2.getCompound("UpgradeData"))
			: UpgradeData.NO_UPGRADE_DATA;
		ChunkTickScheduler<Block> chunkTickScheduler = new ChunkTickScheduler<>(
			block -> block == null || block.method_9564().isAir(),
			Registry.BLOCK::method_10221,
			Registry.BLOCK::method_10223,
			chunkPos,
			compoundTag2.method_10554("ToBeTicked", 9)
		);
		ChunkTickScheduler<Fluid> chunkTickScheduler2 = new ChunkTickScheduler<>(
			fluid -> fluid == null || fluid == Fluids.EMPTY,
			Registry.FLUID::method_10221,
			Registry.FLUID::method_10223,
			chunkPos,
			compoundTag2.method_10554("LiquidsToBeTicked", 9)
		);
		boolean bl = compoundTag2.getBoolean("isLightOn");
		ListTag listTag = compoundTag2.method_10554("Sections", 10);
		int k = 16;
		ChunkSection[] chunkSections = new ChunkSection[16];
		boolean bl2 = world.method_8597().hasSkyLight();
		ChunkManager chunkManager = world.method_8398();

		for (int l = 0; l < listTag.size(); l++) {
			CompoundTag compoundTag3 = listTag.getCompoundTag(l);
			int m = compoundTag3.getByte("Y");
			if (compoundTag3.containsKey("Palette", 9) && compoundTag3.containsKey("BlockStates", 12)) {
				ChunkSection chunkSection = new ChunkSection(m << 4);
				chunkSection.method_12265().method_12329(compoundTag3.method_10554("Palette", 10), compoundTag3.getLongArray("BlockStates"));
				chunkSection.calculateCounts();
				if (!chunkSection.isEmpty()) {
					chunkSections[m] = chunkSection;
				}
			}

			if (bl) {
				if (compoundTag3.containsKey("BlockLight", 7)) {
					chunkManager.method_12130()
						.method_15558(LightType.BLOCK, ChunkSectionPos.from(chunkPos, m), new ChunkNibbleArray(compoundTag3.getByteArray("BlockLight")));
				}

				if (bl2 && compoundTag3.containsKey("SkyLight", 7)) {
					chunkManager.method_12130().method_15558(LightType.SKY, ChunkSectionPos.from(chunkPos, m), new ChunkNibbleArray(compoundTag3.getByteArray("SkyLight")));
				}
			}
		}

		long n = compoundTag2.getLong("InhabitedTime");
		ChunkStatus.ChunkType chunkType = method_12377(compoundTag);
		Chunk chunk;
		if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
			chunk = new WorldChunk(
				world.getWorld(),
				chunkPos,
				biomes,
				upgradeData,
				chunkTickScheduler,
				chunkTickScheduler2,
				n,
				chunkSections,
				worldChunk -> method_12386(compoundTag2, worldChunk)
			);
		} else {
			ProtoChunk protoChunk = new ProtoChunk(chunkPos, upgradeData, chunkSections, chunkTickScheduler, chunkTickScheduler2);
			chunk = protoChunk;
			protoChunk.setBiomeArray(biomes);
			protoChunk.setInhabitedTime(n);
			protoChunk.setStatus(ChunkStatus.get(compoundTag2.getString("Status")));
			if (!bl && protoChunk.method_12009().isAfter(ChunkStatus.LIGHT)) {
				for (BlockPos blockPos : BlockPos.iterateBoxPositions(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 255, chunkPos.getEndZ())) {
					if (chunk.method_8320(blockPos).getLuminance() != 0) {
						protoChunk.method_12315(blockPos);
					}
				}
			}
		}

		chunk.setLightOn(bl);
		CompoundTag compoundTag4 = compoundTag2.getCompound("Heightmaps");
		EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);

		for (Heightmap.Type type : Heightmap.Type.values()) {
			String string = type.getName();
			if (compoundTag4.containsKey(string, 12)) {
				if (chunkType == ChunkStatus.ChunkType.PROTOCHUNK || type.method_16136()) {
					chunk.method_12037(type, compoundTag4.getLongArray(string));
				}
			} else if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK && type.method_16136()) {
				enumSet.add(type);
			}
		}

		Heightmap.populateHeightmaps(chunk, enumSet);
		CompoundTag compoundTag5 = compoundTag2.getCompound("Structures");
		chunk.setStructureStarts(method_12392(chunkGenerator, structureManager, biomeSource, compoundTag5));
		chunk.setStructureReferences(method_12387(compoundTag5));
		if (compoundTag2.getBoolean("shouldSave")) {
			chunk.setShouldSave(true);
		}

		ListTag listTag2 = compoundTag2.method_10554("PostProcessing", 9);

		for (int o = 0; o < listTag2.size(); o++) {
			ListTag listTag3 = listTag2.getListTag(o);

			for (int p = 0; p < listTag3.size(); p++) {
				chunk.markBlockForPostProcessing(listTag3.getShort(p), o);
			}
		}

		if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
			return new ReadOnlyChunk((WorldChunk)chunk);
		} else {
			ProtoChunk protoChunk2 = (ProtoChunk)chunk;
			ListTag listTag3 = compoundTag2.method_10554("Entities", 10);

			for (int p = 0; p < listTag3.size(); p++) {
				protoChunk2.method_12302(listTag3.getCompoundTag(p));
			}

			ListTag listTag4 = compoundTag2.method_10554("TileEntities", 10);

			for (int q = 0; q < listTag4.size(); q++) {
				CompoundTag compoundTag6 = listTag4.getCompoundTag(q);
				chunk.method_12042(compoundTag6);
			}

			ListTag listTag5 = compoundTag2.method_10554("Lights", 9);

			for (int r = 0; r < listTag5.size(); r++) {
				ListTag listTag6 = listTag5.getListTag(r);

				for (int s = 0; s < listTag6.size(); s++) {
					protoChunk2.addLightSource(listTag6.getShort(s), r);
				}
			}

			CompoundTag compoundTag6 = compoundTag2.getCompound("CarvingMasks");

			for (String string2 : compoundTag6.getKeys()) {
				GenerationStep.Carver carver = GenerationStep.Carver.valueOf(string2);
				protoChunk2.method_12307(carver, BitSet.valueOf(compoundTag6.getByteArray(string2)));
			}

			return protoChunk2;
		}
	}

	public static CompoundTag method_12410(World world, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();
		compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag.method_10566("Level", compoundTag2);
		compoundTag2.putInt("xPos", chunkPos.x);
		compoundTag2.putInt("zPos", chunkPos.z);
		compoundTag2.putLong("LastUpdate", world.getTime());
		compoundTag2.putLong("InhabitedTime", chunk.getInhabitedTime());
		compoundTag2.putString("Status", chunk.method_12009().getName());
		UpgradeData upgradeData = chunk.method_12003();
		if (!upgradeData.method_12349()) {
			compoundTag2.method_10566("UpgradeData", upgradeData.method_12350());
		}

		ChunkSection[] chunkSections = chunk.method_12006();
		ListTag listTag = new ListTag();
		LightingProvider lightingProvider = world.method_8398().method_12130();

		for (int i = -1; i < 17; i++) {
			int j = i;
			ChunkSection chunkSection = (ChunkSection)Arrays.stream(chunkSections)
				.filter(chunkSectionx -> chunkSectionx != null && chunkSectionx.getYOffset() >> 4 == j)
				.findFirst()
				.orElse(WorldChunk.field_12852);
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK).method_15544(ChunkSectionPos.from(chunkPos, j));
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.SKY).method_15544(ChunkSectionPos.from(chunkPos, j));
			if (chunkSection != WorldChunk.field_12852 || chunkNibbleArray != null || chunkNibbleArray2 != null) {
				CompoundTag compoundTag3 = new CompoundTag();
				compoundTag3.putByte("Y", (byte)(j & 0xFF));
				if (chunkSection != WorldChunk.field_12852) {
					chunkSection.method_12265().method_12330(compoundTag3, "Palette", "BlockStates");
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

		compoundTag2.method_10566("Sections", listTag);
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
			BlockEntity blockEntity = chunk.method_8321(blockPos);
			if (blockEntity != null) {
				CompoundTag compoundTag4 = new CompoundTag();
				blockEntity.method_11007(compoundTag4);
				if (chunk.method_12009().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
					compoundTag4.putBoolean("keepPacked", false);
				}

				listTag2.add(compoundTag4);
			} else {
				CompoundTag compoundTag4 = chunk.method_12024(blockPos);
				if (compoundTag4 != null) {
					if (chunk.method_12009().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
						compoundTag4.putBoolean("keepPacked", true);
					}

					listTag2.add(compoundTag4);
				}
			}
		}

		compoundTag2.method_10566("TileEntities", listTag2);
		ListTag listTag3 = new ListTag();
		if (chunk.method_12009().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
			WorldChunk worldChunk = (WorldChunk)chunk;
			worldChunk.method_12232(false);

			for (int l = 0; l < worldChunk.method_12215().length; l++) {
				for (Entity entity : worldChunk.method_12215()[l]) {
					CompoundTag compoundTag5 = new CompoundTag();
					if (entity.method_5662(compoundTag5)) {
						worldChunk.method_12232(true);
						listTag3.add(compoundTag5);
					}
				}
			}
		} else {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			listTag3.addAll(protoChunk.getEntities());

			for (BlockPos blockPos2 : chunk.getBlockEntityPositions()) {
				BlockEntity blockEntity2 = chunk.method_8321(blockPos2);
				if (blockEntity2 != null) {
					CompoundTag compoundTag5 = new CompoundTag();
					blockEntity2.method_11007(compoundTag5);
					listTag2.add(compoundTag5);
				} else {
					listTag2.add(chunk.method_12024(blockPos2));
				}
			}

			compoundTag2.method_10566("Lights", method_12393(protoChunk.getLightSourcesBySection()));
			CompoundTag compoundTag3x = new CompoundTag();

			for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
				compoundTag3x.putByteArray(carver.toString(), chunk.method_12025(carver).toByteArray());
			}

			compoundTag2.method_10566("CarvingMasks", compoundTag3x);
		}

		compoundTag2.method_10566("Entities", listTag3);
		if (world.method_8397() instanceof ServerTickScheduler) {
			compoundTag2.method_10566("TileTicks", ((ServerTickScheduler)world.method_8397()).method_8669(chunkPos));
		}

		if (chunk.getBlockTickScheduler() instanceof ChunkTickScheduler) {
			compoundTag2.method_10566("ToBeTicked", ((ChunkTickScheduler)chunk.getBlockTickScheduler()).method_12367());
		}

		if (world.method_8405() instanceof ServerTickScheduler) {
			compoundTag2.method_10566("LiquidTicks", ((ServerTickScheduler)world.method_8405()).method_8669(chunkPos));
		}

		if (chunk.getFluidTickScheduler() instanceof ChunkTickScheduler) {
			compoundTag2.method_10566("LiquidsToBeTicked", ((ChunkTickScheduler)chunk.getFluidTickScheduler()).method_12367());
		}

		compoundTag2.method_10566("PostProcessing", method_12393(chunk.getPostProcessingLists()));
		CompoundTag compoundTag6 = new CompoundTag();

		for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
			if (chunk.method_12009().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK || ((Heightmap.Type)entry.getKey()).method_16136()) {
				compoundTag6.method_10566(((Heightmap.Type)entry.getKey()).getName(), new LongArrayTag(((Heightmap)entry.getValue()).asLongArray()));
			}
		}

		compoundTag2.method_10566("Heightmaps", compoundTag6);
		compoundTag2.method_10566("Structures", method_12385(chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences()));
		return compoundTag;
	}

	public static ChunkStatus.ChunkType method_12377(@Nullable CompoundTag compoundTag) {
		if (compoundTag != null) {
			ChunkStatus chunkStatus = ChunkStatus.get(compoundTag.getCompound("Level").getString("Status"));
			if (chunkStatus != null) {
				return chunkStatus.getChunkType();
			}
		}

		return ChunkStatus.ChunkType.PROTOCHUNK;
	}

	private static void method_12386(CompoundTag compoundTag, WorldChunk worldChunk) {
		ListTag listTag = compoundTag.method_10554("Entities", 10);
		World world = worldChunk.getWorld();

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			EntityType.method_17842(compoundTag2, world, entity -> {
				worldChunk.addEntity(entity);
				return entity;
			});
			worldChunk.method_12232(true);
		}

		ListTag listTag2 = compoundTag.method_10554("TileEntities", 10);

		for (int j = 0; j < listTag2.size(); j++) {
			CompoundTag compoundTag3 = listTag2.getCompoundTag(j);
			boolean bl = compoundTag3.getBoolean("keepPacked");
			if (bl) {
				worldChunk.method_12042(compoundTag3);
			} else {
				BlockEntity blockEntity = BlockEntity.method_11005(compoundTag3);
				if (blockEntity != null) {
					worldChunk.addBlockEntity(blockEntity);
				}
			}
		}

		if (compoundTag.containsKey("TileTicks", 9) && world.method_8397() instanceof ServerTickScheduler) {
			((ServerTickScheduler)world.method_8397()).method_8665(compoundTag.method_10554("TileTicks", 10));
		}

		if (compoundTag.containsKey("LiquidTicks", 9) && world.method_8405() instanceof ServerTickScheduler) {
			((ServerTickScheduler)world.method_8405()).method_8665(compoundTag.method_10554("LiquidTicks", 10));
		}
	}

	private static CompoundTag method_12385(ChunkPos chunkPos, Map<String, StructureStart> map, Map<String, LongSet> map2) {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();

		for (Entry<String, StructureStart> entry : map.entrySet()) {
			compoundTag2.method_10566((String)entry.getKey(), ((StructureStart)entry.getValue()).method_14972(chunkPos.x, chunkPos.z));
		}

		compoundTag.method_10566("Starts", compoundTag2);
		CompoundTag compoundTag3 = new CompoundTag();

		for (Entry<String, LongSet> entry2 : map2.entrySet()) {
			compoundTag3.method_10566((String)entry2.getKey(), new LongArrayTag((LongSet)entry2.getValue()));
		}

		compoundTag.method_10566("References", compoundTag3);
		return compoundTag;
	}

	private static Map<String, StructureStart> method_12392(
		ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BiomeSource biomeSource, CompoundTag compoundTag
	) {
		Map<String, StructureStart> map = Maps.<String, StructureStart>newHashMap();
		CompoundTag compoundTag2 = compoundTag.getCompound("Starts");

		for (String string : compoundTag2.getKeys()) {
			map.put(string, StructureFeatures.method_14842(chunkGenerator, structureManager, biomeSource, compoundTag2.getCompound(string)));
		}

		return map;
	}

	private static Map<String, LongSet> method_12387(CompoundTag compoundTag) {
		Map<String, LongSet> map = Maps.<String, LongSet>newHashMap();
		CompoundTag compoundTag2 = compoundTag.getCompound("References");

		for (String string : compoundTag2.getKeys()) {
			map.put(string, new LongOpenHashSet(compoundTag2.getLongArray(string)));
		}

		return map;
	}

	public static ListTag method_12393(ShortList[] shortLists) {
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
