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
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.ShortTag;
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

	public static ProtoChunk deserialize(ServerWorld world, StructureManager structureManager, PointOfInterestStorage poiStorage, ChunkPos pos, CompoundTag tag) {
		ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
		BiomeSource biomeSource = chunkGenerator.getBiomeSource();
		CompoundTag compoundTag = tag.getCompound("Level");
		ChunkPos chunkPos = new ChunkPos(compoundTag.getInt("xPos"), compoundTag.getInt("zPos"));
		if (!Objects.equals(pos, chunkPos)) {
			LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", pos, pos, chunkPos);
		}

		BiomeArray biomeArray = new BiomeArray(
			world.getRegistryManager().get(Registry.BIOME_KEY), world, pos, biomeSource, compoundTag.contains("Biomes", 11) ? compoundTag.getIntArray("Biomes") : null
		);
		UpgradeData upgradeData = compoundTag.contains("UpgradeData", 10)
			? new UpgradeData(compoundTag.getCompound("UpgradeData"), world)
			: UpgradeData.NO_UPGRADE_DATA;
		ChunkTickScheduler<Block> chunkTickScheduler = new ChunkTickScheduler<>(
			block -> block == null || block.getDefaultState().isAir(), pos, compoundTag.getList("ToBeTicked", 9), world
		);
		ChunkTickScheduler<Fluid> chunkTickScheduler2 = new ChunkTickScheduler<>(
			fluid -> fluid == null || fluid == Fluids.EMPTY, pos, compoundTag.getList("LiquidsToBeTicked", 9), world
		);
		boolean bl = compoundTag.getBoolean("isLightOn");
		ListTag listTag = compoundTag.getList("Sections", 10);
		int i = world.countVerticalSections();
		ChunkSection[] chunkSections = new ChunkSection[i];
		boolean bl2 = world.getDimension().hasSkyLight();
		ChunkManager chunkManager = world.getChunkManager();
		LightingProvider lightingProvider = chunkManager.getLightingProvider();
		if (bl) {
			lightingProvider.setRetainData(pos, true);
		}

		for (int j = 0; j < listTag.size(); j++) {
			CompoundTag compoundTag2 = listTag.getCompound(j);
			int k = compoundTag2.getByte("Y");
			if (compoundTag2.contains("Palette", 9) && compoundTag2.contains("BlockStates", 12)) {
				ChunkSection chunkSection = new ChunkSection(k);
				chunkSection.getContainer().read(compoundTag2.getList("Palette", 10), compoundTag2.getLongArray("BlockStates"));
				chunkSection.calculateCounts();
				if (!chunkSection.isEmpty()) {
					chunkSections[world.sectionCoordToIndex(k)] = chunkSection;
				}

				poiStorage.initForPalette(pos, chunkSection);
			}

			if (bl) {
				if (compoundTag2.contains("BlockLight", 7)) {
					lightingProvider.enqueueSectionData(LightType.BLOCK, ChunkSectionPos.from(pos, k), new ChunkNibbleArray(compoundTag2.getByteArray("BlockLight")), true);
				}

				if (bl2 && compoundTag2.contains("SkyLight", 7)) {
					lightingProvider.enqueueSectionData(LightType.SKY, ChunkSectionPos.from(pos, k), new ChunkNibbleArray(compoundTag2.getByteArray("SkyLight")), true);
				}
			}
		}

		long l = compoundTag.getLong("InhabitedTime");
		ChunkStatus.ChunkType chunkType = getChunkType(tag);
		Chunk chunk;
		if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
			TickScheduler<Block> tickScheduler;
			if (compoundTag.contains("TileTicks", 9)) {
				tickScheduler = SimpleTickScheduler.fromNbt(compoundTag.getList("TileTicks", 10), Registry.BLOCK::getId, Registry.BLOCK::get);
			} else {
				tickScheduler = chunkTickScheduler;
			}

			TickScheduler<Fluid> tickScheduler2;
			if (compoundTag.contains("LiquidTicks", 9)) {
				tickScheduler2 = SimpleTickScheduler.fromNbt(compoundTag.getList("LiquidTicks", 10), Registry.FLUID::getId, Registry.FLUID::get);
			} else {
				tickScheduler2 = chunkTickScheduler2;
			}

			chunk = new WorldChunk(
				world.toServerWorld(),
				pos,
				biomeArray,
				upgradeData,
				tickScheduler,
				tickScheduler2,
				l,
				chunkSections,
				worldChunk -> loadEntities(world, compoundTag, worldChunk)
			);
		} else {
			ProtoChunk protoChunk = new ProtoChunk(pos, upgradeData, chunkSections, chunkTickScheduler, chunkTickScheduler2, world);
			protoChunk.setBiomes(biomeArray);
			chunk = protoChunk;
			protoChunk.setInhabitedTime(l);
			protoChunk.setStatus(ChunkStatus.byId(compoundTag.getString("Status")));
			if (protoChunk.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
				protoChunk.setLightingProvider(lightingProvider);
			}

			if (!bl && protoChunk.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
				for (BlockPos blockPos : BlockPos.iterate(pos.getStartX(), world.getBottomY(), pos.getStartZ(), pos.getEndX(), world.getTopY() - 1, pos.getEndZ())) {
					if (chunk.getBlockState(blockPos).getLuminance() != 0) {
						protoChunk.addLightSource(blockPos);
					}
				}
			}
		}

		chunk.setLightOn(bl);
		CompoundTag compoundTag3 = compoundTag.getCompound("Heightmaps");
		EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);

		for (Heightmap.Type type : chunk.getStatus().getHeightmapTypes()) {
			String string = type.getName();
			if (compoundTag3.contains(string, 12)) {
				chunk.setHeightmap(type, compoundTag3.getLongArray(string));
			} else {
				enumSet.add(type);
			}
		}

		Heightmap.populateHeightmaps(chunk, enumSet);
		CompoundTag compoundTag4 = compoundTag.getCompound("Structures");
		chunk.setStructureStarts(readStructureStarts(structureManager, compoundTag4, world.getSeed()));
		chunk.setStructureReferences(readStructureReferences(pos, compoundTag4));
		if (compoundTag.getBoolean("shouldSave")) {
			chunk.setShouldSave(true);
		}

		ListTag listTag2 = compoundTag.getList("PostProcessing", 9);

		for (int m = 0; m < listTag2.size(); m++) {
			ListTag listTag3 = listTag2.getList(m);

			for (int n = 0; n < listTag3.size(); n++) {
				chunk.markBlockForPostProcessing(listTag3.getShort(n), m);
			}
		}

		if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
			return new ReadOnlyChunk((WorldChunk)chunk);
		} else {
			ProtoChunk protoChunk2 = (ProtoChunk)chunk;
			ListTag listTag3 = compoundTag.getList("Entities", 10);

			for (int n = 0; n < listTag3.size(); n++) {
				protoChunk2.addEntity(listTag3.getCompound(n));
			}

			ListTag listTag4 = compoundTag.getList("TileEntities", 10);

			for (int o = 0; o < listTag4.size(); o++) {
				CompoundTag compoundTag5 = listTag4.getCompound(o);
				chunk.addPendingBlockEntityNbt(compoundTag5);
			}

			ListTag listTag5 = compoundTag.getList("Lights", 9);

			for (int p = 0; p < listTag5.size(); p++) {
				ListTag listTag6 = listTag5.getList(p);

				for (int q = 0; q < listTag6.size(); q++) {
					protoChunk2.addLightSource(listTag6.getShort(q), p);
				}
			}

			CompoundTag compoundTag5 = compoundTag.getCompound("CarvingMasks");

			for (String string2 : compoundTag5.getKeys()) {
				GenerationStep.Carver carver = GenerationStep.Carver.valueOf(string2);
				protoChunk2.setCarvingMask(carver, BitSet.valueOf(compoundTag5.getByteArray(string2)));
			}

			return protoChunk2;
		}
	}

	public static CompoundTag serialize(ServerWorld world, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();
		compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag.put("Level", compoundTag2);
		compoundTag2.putInt("xPos", chunkPos.x);
		compoundTag2.putInt("zPos", chunkPos.z);
		compoundTag2.putLong("LastUpdate", world.getTime());
		compoundTag2.putLong("InhabitedTime", chunk.getInhabitedTime());
		compoundTag2.putString("Status", chunk.getStatus().getId());
		UpgradeData upgradeData = chunk.getUpgradeData();
		if (!upgradeData.isDone()) {
			compoundTag2.put("UpgradeData", upgradeData.toNbt());
		}

		ChunkSection[] chunkSections = chunk.getSectionArray();
		ListTag listTag = new ListTag();
		LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();
		boolean bl = chunk.isLightOn();

		for (int i = lightingProvider.method_31929(); i < lightingProvider.method_31930(); i++) {
			int j = i;
			ChunkSection chunkSection = (ChunkSection)Arrays.stream(chunkSections)
				.filter(chunkSectionx -> chunkSectionx != null && ChunkSectionPos.getSectionCoord(chunkSectionx.getYOffset()) == j)
				.findFirst()
				.orElse(WorldChunk.EMPTY_SECTION);
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(chunkPos, j));
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.SKY).getLightSection(ChunkSectionPos.from(chunkPos, j));
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
		if (bl) {
			compoundTag2.putBoolean("isLightOn", true);
		}

		BiomeArray biomeArray = chunk.getBiomeArray();
		if (biomeArray != null) {
			compoundTag2.putIntArray("Biomes", biomeArray.toIntArray());
		}

		ListTag listTag2 = new ListTag();

		for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
			CompoundTag compoundTag4 = chunk.getPackedBlockEntityNbt(blockPos);
			if (compoundTag4 != null) {
				listTag2.add(compoundTag4);
			}
		}

		compoundTag2.put("TileEntities", listTag2);
		if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK) {
			ProtoChunk protoChunk = (ProtoChunk)chunk;
			ListTag listTag3 = new ListTag();
			listTag3.addAll(protoChunk.getEntities());
			compoundTag2.put("Entities", listTag3);
			compoundTag2.put("Lights", toNbt(protoChunk.getLightSourcesBySection()));
			CompoundTag compoundTag4 = new CompoundTag();

			for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
				BitSet bitSet = protoChunk.getCarvingMask(carver);
				if (bitSet != null) {
					compoundTag4.putByteArray(carver.toString(), bitSet.toByteArray());
				}
			}

			compoundTag2.put("CarvingMasks", compoundTag4);
		}

		TickScheduler<Block> tickScheduler = chunk.getBlockTickScheduler();
		if (tickScheduler instanceof ChunkTickScheduler) {
			compoundTag2.put("ToBeTicked", ((ChunkTickScheduler)tickScheduler).toNbt());
		} else if (tickScheduler instanceof SimpleTickScheduler) {
			compoundTag2.put("TileTicks", ((SimpleTickScheduler)tickScheduler).toNbt());
		} else {
			compoundTag2.put("TileTicks", world.getBlockTickScheduler().toNbt(chunkPos));
		}

		TickScheduler<Fluid> tickScheduler2 = chunk.getFluidTickScheduler();
		if (tickScheduler2 instanceof ChunkTickScheduler) {
			compoundTag2.put("LiquidsToBeTicked", ((ChunkTickScheduler)tickScheduler2).toNbt());
		} else if (tickScheduler2 instanceof SimpleTickScheduler) {
			compoundTag2.put("LiquidTicks", ((SimpleTickScheduler)tickScheduler2).toNbt());
		} else {
			compoundTag2.put("LiquidTicks", world.getFluidTickScheduler().toNbt(chunkPos));
		}

		compoundTag2.put("PostProcessing", toNbt(chunk.getPostProcessingLists()));
		CompoundTag compoundTag4 = new CompoundTag();

		for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
			if (chunk.getStatus().getHeightmapTypes().contains(entry.getKey())) {
				compoundTag4.put(((Heightmap.Type)entry.getKey()).getName(), new LongArrayTag(((Heightmap)entry.getValue()).asLongArray()));
			}
		}

		compoundTag2.put("Heightmaps", compoundTag4);
		compoundTag2.put("Structures", writeStructures(chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences()));
		return compoundTag;
	}

	public static ChunkStatus.ChunkType getChunkType(@Nullable CompoundTag tag) {
		if (tag != null) {
			ChunkStatus chunkStatus = ChunkStatus.byId(tag.getCompound("Level").getString("Status"));
			if (chunkStatus != null) {
				return chunkStatus.getChunkType();
			}
		}

		return ChunkStatus.ChunkType.PROTOCHUNK;
	}

	private static void loadEntities(ServerWorld world, CompoundTag tag, WorldChunk chunk) {
		if (tag.contains("Entities", 9)) {
			ListTag listTag = tag.getList("Entities", 10);
			if (!listTag.isEmpty()) {
				world.loadEntities(EntityType.streamFromNbt(listTag, world));
			}
		}

		ListTag listTag = tag.getList("TileEntities", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompound(i);
			boolean bl = compoundTag.getBoolean("keepPacked");
			if (bl) {
				chunk.addPendingBlockEntityNbt(compoundTag);
			} else {
				BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
				BlockEntity blockEntity = BlockEntity.createFromNbt(blockPos, chunk.getBlockState(blockPos), compoundTag);
				if (blockEntity != null) {
					chunk.setBlockEntity(blockEntity);
				}
			}
		}
	}

	private static CompoundTag writeStructures(
		ChunkPos pos, Map<StructureFeature<?>, StructureStart<?>> structureStarts, Map<StructureFeature<?>, LongSet> structureReferences
	) {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();

		for (Entry<StructureFeature<?>, StructureStart<?>> entry : structureStarts.entrySet()) {
			compoundTag2.put(((StructureFeature)entry.getKey()).getName(), ((StructureStart)entry.getValue()).toNbt(pos));
		}

		compoundTag.put("Starts", compoundTag2);
		CompoundTag compoundTag3 = new CompoundTag();

		for (Entry<StructureFeature<?>, LongSet> entry2 : structureReferences.entrySet()) {
			compoundTag3.put(((StructureFeature)entry2.getKey()).getName(), new LongArrayTag((LongSet)entry2.getValue()));
		}

		compoundTag.put("References", compoundTag3);
		return compoundTag;
	}

	private static Map<StructureFeature<?>, StructureStart<?>> readStructureStarts(StructureManager structureManager, CompoundTag tag, long worldSeed) {
		Map<StructureFeature<?>, StructureStart<?>> map = Maps.<StructureFeature<?>, StructureStart<?>>newHashMap();
		CompoundTag compoundTag = tag.getCompound("Starts");

		for (String string : compoundTag.getKeys()) {
			String string2 = string.toLowerCase(Locale.ROOT);
			StructureFeature<?> structureFeature = (StructureFeature<?>)StructureFeature.STRUCTURES.get(string2);
			if (structureFeature == null) {
				LOGGER.error("Unknown structure start: {}", string2);
			} else {
				StructureStart<?> structureStart = StructureFeature.readStructureStart(structureManager, compoundTag.getCompound(string), worldSeed);
				if (structureStart != null) {
					map.put(structureFeature, structureStart);
				}
			}
		}

		return map;
	}

	private static Map<StructureFeature<?>, LongSet> readStructureReferences(ChunkPos pos, CompoundTag tag) {
		Map<StructureFeature<?>, LongSet> map = Maps.<StructureFeature<?>, LongSet>newHashMap();
		CompoundTag compoundTag = tag.getCompound("References");

		for (String string : compoundTag.getKeys()) {
			map.put(
				StructureFeature.STRUCTURES.get(string.toLowerCase(Locale.ROOT)), new LongOpenHashSet(Arrays.stream(compoundTag.getLongArray(string)).filter(packedPos -> {
					ChunkPos chunkPos2 = new ChunkPos(packedPos);
					if (chunkPos2.getChebyshevDistance(pos) > 8) {
						LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", string, chunkPos2, pos);
						return false;
					} else {
						return true;
					}
				}).toArray())
			);
		}

		return map;
	}

	public static ListTag toNbt(ShortList[] lists) {
		ListTag listTag = new ListTag();

		for (ShortList shortList : lists) {
			ListTag listTag2 = new ListTag();
			if (shortList != null) {
				for (Short short_ : shortList) {
					listTag2.add(ShortTag.of(short_));
				}
			}

			listTag.add(listTag2);
		}

		return listTag;
	}
}
