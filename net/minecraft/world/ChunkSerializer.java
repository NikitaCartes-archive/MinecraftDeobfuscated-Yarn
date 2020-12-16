/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.TickScheduler;
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
import org.jetbrains.annotations.Nullable;

public class ChunkSerializer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static ProtoChunk deserialize(ServerWorld world, StructureManager structureManager, PointOfInterestStorage poiStorage, ChunkPos pos, CompoundTag tag) {
        int n;
        ListTag listTag3;
        Chunk chunk;
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
        BiomeSource biomeSource = chunkGenerator.getBiomeSource();
        CompoundTag compoundTag = tag.getCompound("Level");
        ChunkPos chunkPos = new ChunkPos(compoundTag.getInt("xPos"), compoundTag.getInt("zPos"));
        if (!Objects.equals(pos, chunkPos)) {
            LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", (Object)pos, (Object)pos, (Object)chunkPos);
        }
        BiomeArray biomeArray = new BiomeArray(world.getRegistryManager().get(Registry.BIOME_KEY), world, pos, biomeSource, compoundTag.contains("Biomes", 11) ? compoundTag.getIntArray("Biomes") : null);
        UpgradeData upgradeData = compoundTag.contains("UpgradeData", 10) ? new UpgradeData(compoundTag.getCompound("UpgradeData"), world) : UpgradeData.NO_UPGRADE_DATA;
        ChunkTickScheduler<Block> chunkTickScheduler = new ChunkTickScheduler<Block>(block -> block == null || block.getDefaultState().isAir(), pos, compoundTag.getList("ToBeTicked", 9), world);
        ChunkTickScheduler<Fluid> chunkTickScheduler2 = new ChunkTickScheduler<Fluid>(fluid -> fluid == null || fluid == Fluids.EMPTY, pos, compoundTag.getList("LiquidsToBeTicked", 9), world);
        boolean bl = compoundTag.getBoolean("isLightOn");
        ListTag listTag = compoundTag.getList("Sections", 10);
        int i = world.method_32890();
        ChunkSection[] chunkSections = new ChunkSection[i];
        boolean bl2 = world.getDimension().hasSkyLight();
        ServerChunkManager chunkManager = world.getChunkManager();
        LightingProvider lightingProvider = ((ChunkManager)chunkManager).getLightingProvider();
        if (bl) {
            lightingProvider.setRetainData(pos, true);
        }
        for (int j = 0; j < listTag.size(); ++j) {
            CompoundTag compoundTag2 = listTag.getCompound(j);
            byte k = compoundTag2.getByte("Y");
            if (compoundTag2.contains("Palette", 9) && compoundTag2.contains("BlockStates", 12)) {
                ChunkSection chunkSection = new ChunkSection(k);
                chunkSection.getContainer().read(compoundTag2.getList("Palette", 10), compoundTag2.getLongArray("BlockStates"));
                chunkSection.calculateCounts();
                if (!chunkSection.isEmpty()) {
                    chunkSections[world.getSectionIndexFromSection((int)k)] = chunkSection;
                }
                poiStorage.initForPalette(pos, chunkSection);
            }
            if (!bl) continue;
            if (compoundTag2.contains("BlockLight", 7)) {
                lightingProvider.enqueueSectionData(LightType.BLOCK, ChunkSectionPos.from(pos, k), new ChunkNibbleArray(compoundTag2.getByteArray("BlockLight")), true);
            }
            if (!bl2 || !compoundTag2.contains("SkyLight", 7)) continue;
            lightingProvider.enqueueSectionData(LightType.SKY, ChunkSectionPos.from(pos, k), new ChunkNibbleArray(compoundTag2.getByteArray("SkyLight")), true);
        }
        long l = compoundTag.getLong("InhabitedTime");
        ChunkStatus.ChunkType chunkType = ChunkSerializer.getChunkType(tag);
        if (chunkType == ChunkStatus.ChunkType.field_12807) {
            TickScheduler<Block> tickScheduler = compoundTag.contains("TileTicks", 9) ? SimpleTickScheduler.fromNbt(compoundTag.getList("TileTicks", 10), Registry.BLOCK::getId, Registry.BLOCK::get) : chunkTickScheduler;
            TickScheduler<Fluid> tickScheduler2 = compoundTag.contains("LiquidTicks", 9) ? SimpleTickScheduler.fromNbt(compoundTag.getList("LiquidTicks", 10), Registry.FLUID::getId, Registry.FLUID::get) : chunkTickScheduler2;
            chunk = new WorldChunk(world.toServerWorld(), pos, biomeArray, upgradeData, tickScheduler, tickScheduler2, l, chunkSections, worldChunk -> ChunkSerializer.writeEntities(world, compoundTag, worldChunk));
        } else {
            ProtoChunk protoChunk = new ProtoChunk(pos, upgradeData, chunkSections, chunkTickScheduler, chunkTickScheduler2, world);
            protoChunk.setBiomes(biomeArray);
            chunk = protoChunk;
            chunk.setInhabitedTime(l);
            protoChunk.setStatus(ChunkStatus.byId(compoundTag.getString("Status")));
            if (chunk.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
                protoChunk.setLightingProvider(lightingProvider);
            }
            if (!bl && chunk.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
                for (BlockPos blockPos : BlockPos.iterate(pos.getStartX(), world.getSectionCount(), pos.getStartZ(), pos.getEndX(), world.getTopHeightLimit() - 1, pos.getEndZ())) {
                    if (chunk.getBlockState(blockPos).getLuminance() == 0) continue;
                    protoChunk.addLightSource(blockPos);
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
                continue;
            }
            enumSet.add(type);
        }
        Heightmap.populateHeightmaps(chunk, enumSet);
        CompoundTag compoundTag2 = compoundTag.getCompound("Structures");
        chunk.setStructureStarts(ChunkSerializer.readStructureStarts(structureManager, compoundTag2, world.getSeed()));
        chunk.setStructureReferences(ChunkSerializer.readStructureReferences(pos, compoundTag2));
        if (compoundTag.getBoolean("shouldSave")) {
            chunk.setShouldSave(true);
        }
        ListTag listTag2 = compoundTag.getList("PostProcessing", 9);
        for (int m = 0; m < listTag2.size(); ++m) {
            listTag3 = listTag2.getList(m);
            for (n = 0; n < listTag3.size(); ++n) {
                chunk.markBlockForPostProcessing(listTag3.getShort(n), m);
            }
        }
        if (chunkType == ChunkStatus.ChunkType.field_12807) {
            return new ReadOnlyChunk((WorldChunk)chunk);
        }
        ProtoChunk protoChunk2 = (ProtoChunk)chunk;
        listTag3 = compoundTag.getList("Entities", 10);
        for (n = 0; n < listTag3.size(); ++n) {
            protoChunk2.addEntity(listTag3.getCompound(n));
        }
        ListTag listTag4 = compoundTag.getList("TileEntities", 10);
        for (int o = 0; o < listTag4.size(); ++o) {
            CompoundTag compoundTag5 = listTag4.getCompound(o);
            chunk.addPendingBlockEntityTag(compoundTag5);
        }
        ListTag listTag5 = compoundTag.getList("Lights", 9);
        for (int p = 0; p < listTag5.size(); ++p) {
            ListTag listTag6 = listTag5.getList(p);
            for (int q = 0; q < listTag6.size(); ++q) {
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

    public static CompoundTag serialize(ServerWorld world, Chunk chunk) {
        TickScheduler<Block> tickScheduler;
        CompoundTag compoundTag4;
        BiomeArray biomeArray;
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
            compoundTag2.put("UpgradeData", upgradeData.toTag());
        }
        ChunkSection[] chunkSections = chunk.getSectionArray();
        ListTag listTag = new ListTag();
        ServerLightingProvider lightingProvider = world.getChunkManager().getLightingProvider();
        boolean bl = chunk.isLightOn();
        for (int i = lightingProvider.method_31929(); i < lightingProvider.method_31930(); ++i) {
            int j = i;
            ChunkSection chunkSection2 = Arrays.stream(chunkSections).filter(chunkSection -> chunkSection != null && ChunkSectionPos.getSectionCoord(chunkSection.getYOffset()) == j).findFirst().orElse(WorldChunk.EMPTY_SECTION);
            ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(chunkPos, j));
            ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.SKY).getLightSection(ChunkSectionPos.from(chunkPos, j));
            if (chunkSection2 == WorldChunk.EMPTY_SECTION && chunkNibbleArray == null && chunkNibbleArray2 == null) continue;
            CompoundTag compoundTag3 = new CompoundTag();
            compoundTag3.putByte("Y", (byte)(j & 0xFF));
            if (chunkSection2 != WorldChunk.EMPTY_SECTION) {
                chunkSection2.getContainer().write(compoundTag3, "Palette", "BlockStates");
            }
            if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
                compoundTag3.putByteArray("BlockLight", chunkNibbleArray.asByteArray());
            }
            if (chunkNibbleArray2 != null && !chunkNibbleArray2.isUninitialized()) {
                compoundTag3.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
            }
            listTag.add(compoundTag3);
        }
        compoundTag2.put("Sections", listTag);
        if (bl) {
            compoundTag2.putBoolean("isLightOn", true);
        }
        if ((biomeArray = chunk.getBiomeArray()) != null) {
            compoundTag2.putIntArray("Biomes", biomeArray.toIntArray());
        }
        ListTag listTag2 = new ListTag();
        for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
            compoundTag4 = chunk.getPackedBlockEntityTag(blockPos);
            if (compoundTag4 == null) continue;
            listTag2.add(compoundTag4);
        }
        compoundTag2.put("TileEntities", listTag2);
        if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.field_12808) {
            ProtoChunk protoChunk = (ProtoChunk)chunk;
            ListTag listTag3 = new ListTag();
            listTag3.addAll(protoChunk.getEntities());
            compoundTag2.put("Entities", listTag3);
            compoundTag2.put("Lights", ChunkSerializer.toNbt(protoChunk.getLightSourcesBySection()));
            compoundTag4 = new CompoundTag();
            for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
                BitSet bitSet = protoChunk.getCarvingMask(carver);
                if (bitSet == null) continue;
                compoundTag4.putByteArray(carver.toString(), bitSet.toByteArray());
            }
            compoundTag2.put("CarvingMasks", compoundTag4);
        }
        if ((tickScheduler = chunk.getBlockTickScheduler()) instanceof ChunkTickScheduler) {
            compoundTag2.put("ToBeTicked", ((ChunkTickScheduler)tickScheduler).toNbt());
        } else if (tickScheduler instanceof SimpleTickScheduler) {
            compoundTag2.put("TileTicks", ((SimpleTickScheduler)tickScheduler).toNbt());
        } else {
            compoundTag2.put("TileTicks", ((ServerTickScheduler)world.getBlockTickScheduler()).toTag(chunkPos));
        }
        TickScheduler<Fluid> tickScheduler2 = chunk.getFluidTickScheduler();
        if (tickScheduler2 instanceof ChunkTickScheduler) {
            compoundTag2.put("LiquidsToBeTicked", ((ChunkTickScheduler)tickScheduler2).toNbt());
        } else if (tickScheduler2 instanceof SimpleTickScheduler) {
            compoundTag2.put("LiquidTicks", ((SimpleTickScheduler)tickScheduler2).toNbt());
        } else {
            compoundTag2.put("LiquidTicks", ((ServerTickScheduler)world.getFluidTickScheduler()).toTag(chunkPos));
        }
        compoundTag2.put("PostProcessing", ChunkSerializer.toNbt(chunk.getPostProcessingLists()));
        compoundTag4 = new CompoundTag();
        for (Map.Entry entry : chunk.getHeightmaps()) {
            if (!chunk.getStatus().getHeightmapTypes().contains(entry.getKey())) continue;
            compoundTag4.put(((Heightmap.Type)entry.getKey()).getName(), new LongArrayTag(((Heightmap)entry.getValue()).asLongArray()));
        }
        compoundTag2.put("Heightmaps", compoundTag4);
        compoundTag2.put("Structures", ChunkSerializer.writeStructures(chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences()));
        return compoundTag;
    }

    public static ChunkStatus.ChunkType getChunkType(@Nullable CompoundTag tag) {
        ChunkStatus chunkStatus;
        if (tag != null && (chunkStatus = ChunkStatus.byId(tag.getCompound("Level").getString("Status"))) != null) {
            return chunkStatus.getChunkType();
        }
        return ChunkStatus.ChunkType.field_12808;
    }

    private static void writeEntities(ServerWorld world, CompoundTag tag, WorldChunk chunk) {
        ListTag listTag;
        if (tag.contains("Entities", 9) && !(listTag = tag.getList("Entities", 10)).isEmpty()) {
            world.method_31423(EntityType.method_31489(listTag, world));
        }
        listTag = tag.getList("TileEntities", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            boolean bl = compoundTag.getBoolean("keepPacked");
            if (bl) {
                chunk.addPendingBlockEntityTag(compoundTag);
                continue;
            }
            BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
            BlockEntity blockEntity = BlockEntity.createFromTag(blockPos, chunk.getBlockState(blockPos), compoundTag);
            if (blockEntity == null) continue;
            chunk.setBlockEntity(blockEntity);
        }
    }

    private static CompoundTag writeStructures(ChunkPos pos, Map<StructureFeature<?>, StructureStart<?>> structureStarts, Map<StructureFeature<?>, LongSet> structureReferences) {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag compoundTag2 = new CompoundTag();
        for (Map.Entry<StructureFeature<?>, StructureStart<?>> entry : structureStarts.entrySet()) {
            compoundTag2.put(entry.getKey().getName(), entry.getValue().toTag(pos.x, pos.z));
        }
        compoundTag.put("Starts", compoundTag2);
        CompoundTag compoundTag3 = new CompoundTag();
        for (Map.Entry<StructureFeature<?>, LongSet> entry2 : structureReferences.entrySet()) {
            compoundTag3.put(entry2.getKey().getName(), new LongArrayTag(entry2.getValue()));
        }
        compoundTag.put("References", compoundTag3);
        return compoundTag;
    }

    private static Map<StructureFeature<?>, StructureStart<?>> readStructureStarts(StructureManager structureManager, CompoundTag tag, long worldSeed) {
        HashMap<StructureFeature<?>, StructureStart<?>> map = Maps.newHashMap();
        CompoundTag compoundTag = tag.getCompound("Starts");
        for (String string : compoundTag.getKeys()) {
            String string2 = string.toLowerCase(Locale.ROOT);
            StructureFeature structureFeature = (StructureFeature)StructureFeature.STRUCTURES.get(string2);
            if (structureFeature == null) {
                LOGGER.error("Unknown structure start: {}", (Object)string2);
                continue;
            }
            StructureStart<?> structureStart = StructureFeature.readStructureStart(structureManager, compoundTag.getCompound(string), worldSeed);
            if (structureStart == null) continue;
            map.put(structureFeature, structureStart);
        }
        return map;
    }

    private static Map<StructureFeature<?>, LongSet> readStructureReferences(ChunkPos pos, CompoundTag tag) {
        HashMap<StructureFeature<?>, LongSet> map = Maps.newHashMap();
        CompoundTag compoundTag = tag.getCompound("References");
        for (String string : compoundTag.getKeys()) {
            map.put((StructureFeature<?>)StructureFeature.STRUCTURES.get(string.toLowerCase(Locale.ROOT)), new LongOpenHashSet(Arrays.stream(compoundTag.getLongArray(string)).filter(packedPos -> {
                ChunkPos chunkPos2 = new ChunkPos(packedPos);
                if (chunkPos2.method_24022(pos) > 8) {
                    LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", (Object)string, (Object)chunkPos2, (Object)pos);
                    return false;
                }
                return true;
            }).toArray()));
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

