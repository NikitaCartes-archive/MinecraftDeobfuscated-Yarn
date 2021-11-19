/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtShort;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.BelowZeroRetrogen;
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
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.tick.ChunkTickScheduler;
import net.minecraft.world.tick.SimpleTickScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ChunkSerializer {
    private static final Codec<PalettedContainer<BlockState>> CODEC = PalettedContainer.createCodec(Block.STATE_IDS, BlockState.CODEC, PalettedContainer.PaletteProvider.BLOCK_STATE, Blocks.AIR.getDefaultState());
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String UPGRADE_DATA_KEY = "UpgradeData";
    private static final String BLOCK_TICKS = "block_ticks";
    private static final String FLUID_TICKS = "fluid_ticks";

    public static ProtoChunk deserialize(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt) {
        Chunk chunk;
        ChunkPos chunkPos2 = new ChunkPos(nbt.getInt("xPos"), nbt.getInt("zPos"));
        if (!Objects.equals(chunkPos, chunkPos2)) {
            LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", (Object)chunkPos, (Object)chunkPos, (Object)chunkPos2);
        }
        UpgradeData upgradeData = nbt.contains(UPGRADE_DATA_KEY, 10) ? new UpgradeData(nbt.getCompound(UPGRADE_DATA_KEY), world) : UpgradeData.NO_UPGRADE_DATA;
        boolean bl = nbt.getBoolean("isLightOn");
        NbtList nbtList = nbt.getList("sections", 10);
        int i = world.countVerticalSections();
        ChunkSection[] chunkSections = new ChunkSection[i];
        boolean bl2 = world.getDimension().hasSkyLight();
        ServerChunkManager chunkManager = world.getChunkManager();
        LightingProvider lightingProvider = ((ChunkManager)chunkManager).getLightingProvider();
        if (bl) {
            lightingProvider.setRetainData(chunkPos, true);
        }
        Registry<Biome> registry = world.getRegistryManager().get(Registry.BIOME_KEY);
        Codec<PalettedContainer<Biome>> codec = ChunkSerializer.createCodec(registry);
        for (int j = 0; j < nbtList.size(); ++j) {
            NbtCompound nbtCompound = nbtList.getCompound(j);
            byte k = nbtCompound.getByte("Y");
            int l = world.sectionCoordToIndex(k);
            if (l >= 0 && l < chunkSections.length) {
                ChunkSection chunkSection;
                PalettedContainer palettedContainer = nbtCompound.contains("block_states", 10) ? (PalettedContainer)CODEC.parse(NbtOps.INSTANCE, nbtCompound.getCompound("block_states")).promotePartial(errorMessage -> ChunkSerializer.logRecoverableError(chunkPos, k, errorMessage)).getOrThrow(false, LOGGER::error) : new PalettedContainer(Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE);
                PalettedContainer palettedContainer2 = nbtCompound.contains("biomes", 10) ? (PalettedContainer)codec.parse(NbtOps.INSTANCE, nbtCompound.getCompound("biomes")).promotePartial(errorMessage -> ChunkSerializer.logRecoverableError(chunkPos, k, errorMessage)).getOrThrow(false, LOGGER::error) : new PalettedContainer(registry, registry.getOrThrow(BiomeKeys.PLAINS), PalettedContainer.PaletteProvider.BIOME);
                chunkSections[l] = chunkSection = new ChunkSection(k, palettedContainer, palettedContainer2);
                poiStorage.initForPalette(chunkPos, chunkSection);
            }
            if (!bl) continue;
            if (nbtCompound.contains("BlockLight", 7)) {
                lightingProvider.enqueueSectionData(LightType.BLOCK, ChunkSectionPos.from(chunkPos, k), new ChunkNibbleArray(nbtCompound.getByteArray("BlockLight")), true);
            }
            if (!bl2 || !nbtCompound.contains("SkyLight", 7)) continue;
            lightingProvider.enqueueSectionData(LightType.SKY, ChunkSectionPos.from(chunkPos, k), new ChunkNibbleArray(nbtCompound.getByteArray("SkyLight")), true);
        }
        long m = nbt.getLong("InhabitedTime");
        ChunkStatus.ChunkType chunkType = ChunkSerializer.getChunkType(nbt);
        BlendingData blendingData = nbt.contains("blending_data", 10) ? (BlendingData)BlendingData.CODEC.parse(new Dynamic<NbtCompound>(NbtOps.INSTANCE, nbt.getCompound("blending_data"))).resultOrPartial(LOGGER::error).orElse(null) : null;
        if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
            ChunkTickScheduler<Block> chunkTickScheduler = ChunkTickScheduler.create(nbt.getList(BLOCK_TICKS, 10), id -> Registry.BLOCK.getOrEmpty(Identifier.tryParse(id)), chunkPos);
            ChunkTickScheduler<Fluid> chunkTickScheduler2 = ChunkTickScheduler.create(nbt.getList(FLUID_TICKS, 10), id -> Registry.FLUID.getOrEmpty(Identifier.tryParse(id)), chunkPos);
            chunk = new WorldChunk(world.toServerWorld(), chunkPos, upgradeData, chunkTickScheduler, chunkTickScheduler2, m, chunkSections, ChunkSerializer.getEntityLoadingCallback(world, nbt), blendingData);
        } else {
            boolean bl3;
            SimpleTickScheduler<Block> simpleTickScheduler = SimpleTickScheduler.tick(nbt.getList(BLOCK_TICKS, 10), id -> Registry.BLOCK.getOrEmpty(Identifier.tryParse(id)), chunkPos);
            SimpleTickScheduler<Fluid> simpleTickScheduler2 = SimpleTickScheduler.tick(nbt.getList(FLUID_TICKS, 10), id -> Registry.FLUID.getOrEmpty(Identifier.tryParse(id)), chunkPos);
            ProtoChunk protoChunk = new ProtoChunk(chunkPos, upgradeData, chunkSections, simpleTickScheduler, simpleTickScheduler2, world, registry, blendingData);
            chunk = protoChunk;
            chunk.setInhabitedTime(m);
            if (nbt.contains("below_zero_retrogen", 10)) {
                BelowZeroRetrogen.CODEC.parse(new Dynamic<NbtCompound>(NbtOps.INSTANCE, nbt.getCompound("below_zero_retrogen"))).resultOrPartial(LOGGER::error).ifPresent(protoChunk::setBelowZeroRetrogen);
            }
            ChunkStatus chunkStatus = ChunkStatus.byId(nbt.getString("Status"));
            protoChunk.setStatus(chunkStatus);
            if (chunkStatus.isAtLeast(ChunkStatus.FEATURES)) {
                protoChunk.setLightingProvider(lightingProvider);
            }
            BelowZeroRetrogen belowZeroRetrogen = protoChunk.getBelowZeroRetrogen();
            boolean bl4 = bl3 = chunkStatus.isAtLeast(ChunkStatus.LIGHT) || belowZeroRetrogen != null && belowZeroRetrogen.getTargetStatus().isAtLeast(ChunkStatus.LIGHT);
            if (!bl && bl3) {
                for (BlockPos blockPos : BlockPos.iterate(chunkPos.getStartX(), world.getBottomY(), chunkPos.getStartZ(), chunkPos.getEndX(), world.getTopY() - 1, chunkPos.getEndZ())) {
                    if (chunk.getBlockState(blockPos).getLuminance() == 0) continue;
                    protoChunk.addLightSource(blockPos);
                }
            }
        }
        chunk.setLightOn(bl);
        NbtCompound nbtCompound2 = nbt.getCompound("Heightmaps");
        EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);
        for (Heightmap.Type type : chunk.getStatus().getHeightmapTypes()) {
            String string = type.getName();
            if (nbtCompound2.contains(string, 12)) {
                chunk.setHeightmap(type, nbtCompound2.getLongArray(string));
                continue;
            }
            enumSet.add(type);
        }
        Heightmap.populateHeightmaps(chunk, enumSet);
        NbtCompound nbtCompound3 = nbt.getCompound("structures");
        chunk.setStructureStarts(ChunkSerializer.readStructureStarts(StructureContext.from(world), nbtCompound3, world.getSeed()));
        chunk.setStructureReferences(ChunkSerializer.readStructureReferences(chunkPos, nbtCompound3));
        if (nbt.getBoolean("shouldSave")) {
            chunk.setShouldSave(true);
        }
        NbtList nbtList2 = nbt.getList("PostProcessing", 9);
        for (int n = 0; n < nbtList2.size(); ++n) {
            NbtList nbtList3 = nbtList2.getList(n);
            for (int o = 0; o < nbtList3.size(); ++o) {
                chunk.markBlockForPostProcessing(nbtList3.getShort(o), n);
            }
        }
        if (chunkType == ChunkStatus.ChunkType.LEVELCHUNK) {
            return new ReadOnlyChunk((WorldChunk)chunk, false);
        }
        ProtoChunk protoChunk2 = (ProtoChunk)chunk;
        NbtList nbtList3 = nbt.getList("entities", 10);
        for (int o = 0; o < nbtList3.size(); ++o) {
            protoChunk2.addEntity(nbtList3.getCompound(o));
        }
        NbtList nbtList4 = nbt.getList("block_entities", 10);
        for (int p = 0; p < nbtList4.size(); ++p) {
            NbtCompound nbtCompound4 = nbtList4.getCompound(p);
            chunk.addPendingBlockEntityNbt(nbtCompound4);
        }
        NbtList nbtList5 = nbt.getList("Lights", 9);
        for (int q = 0; q < nbtList5.size(); ++q) {
            NbtList nbtList6 = nbtList5.getList(q);
            for (int r = 0; r < nbtList6.size(); ++r) {
                protoChunk2.addLightSource(nbtList6.getShort(r), q);
            }
        }
        NbtCompound nbtCompound4 = nbt.getCompound("CarvingMasks");
        for (String string2 : nbtCompound4.getKeys()) {
            GenerationStep.Carver carver = GenerationStep.Carver.valueOf(string2);
            protoChunk2.setCarvingMask(carver, new CarvingMask(nbtCompound4.getLongArray(string2), chunk.getBottomY()));
        }
        return protoChunk2;
    }

    private static void logRecoverableError(ChunkPos chunkPos, int y, String message) {
        LOGGER.error("Recoverable errors when loading section [" + chunkPos.x + ", " + y + ", " + chunkPos.z + "]: " + message);
    }

    private static Codec<PalettedContainer<Biome>> createCodec(Registry<Biome> biomeRegistry) {
        return PalettedContainer.createCodec(biomeRegistry, biomeRegistry.getCodec(), PalettedContainer.PaletteProvider.BIOME, biomeRegistry.getOrThrow(BiomeKeys.PLAINS));
    }

    public static NbtCompound serialize(ServerWorld world, Chunk chunk) {
        NbtCompound nbtCompound3;
        UpgradeData upgradeData;
        BelowZeroRetrogen belowZeroRetrogen;
        ChunkPos chunkPos = chunk.getPos();
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        nbtCompound.putInt("xPos", chunkPos.x);
        nbtCompound.putInt("yPos", chunk.getBottomSectionCoord());
        nbtCompound.putInt("zPos", chunkPos.z);
        nbtCompound.putLong("LastUpdate", world.getTime());
        nbtCompound.putLong("InhabitedTime", chunk.getInhabitedTime());
        nbtCompound.putString("Status", chunk.getStatus().getId());
        BlendingData blendingData = chunk.getBlendingData();
        if (blendingData != null) {
            BlendingData.CODEC.encodeStart(NbtOps.INSTANCE, blendingData).resultOrPartial(LOGGER::error).ifPresent(nbtElement -> nbtCompound.put("blending_data", (NbtElement)nbtElement));
        }
        if ((belowZeroRetrogen = chunk.getBelowZeroRetrogen()) != null) {
            BelowZeroRetrogen.CODEC.encodeStart(NbtOps.INSTANCE, belowZeroRetrogen).resultOrPartial(LOGGER::error).ifPresent(nbtElement -> nbtCompound.put("below_zero_retrogen", (NbtElement)nbtElement));
        }
        if (!(upgradeData = chunk.getUpgradeData()).isDone()) {
            nbtCompound.put(UPGRADE_DATA_KEY, upgradeData.toNbt());
        }
        ChunkSection[] chunkSections = chunk.getSectionArray();
        NbtList nbtList = new NbtList();
        ServerLightingProvider lightingProvider = world.getChunkManager().getLightingProvider();
        Registry<Biome> registry = world.getRegistryManager().get(Registry.BIOME_KEY);
        Codec<PalettedContainer<Biome>> codec = ChunkSerializer.createCodec(registry);
        boolean bl = chunk.isLightOn();
        for (int i = lightingProvider.getBottomY(); i < lightingProvider.getTopY(); ++i) {
            int j = chunk.sectionCoordToIndex(i);
            boolean bl2 = j >= 0 && j < chunkSections.length;
            ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(chunkPos, i));
            ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.SKY).getLightSection(ChunkSectionPos.from(chunkPos, i));
            if (!bl2 && chunkNibbleArray == null && chunkNibbleArray2 == null) continue;
            NbtCompound nbtCompound2 = new NbtCompound();
            if (bl2) {
                ChunkSection chunkSection = chunkSections[j];
                nbtCompound2.put("block_states", CODEC.encodeStart(NbtOps.INSTANCE, chunkSection.getBlockStateContainer()).getOrThrow(false, LOGGER::error));
                nbtCompound2.put("biomes", codec.encodeStart(NbtOps.INSTANCE, chunkSection.getBiomeContainer()).getOrThrow(false, LOGGER::error));
            }
            if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
                nbtCompound2.putByteArray("BlockLight", chunkNibbleArray.asByteArray());
            }
            if (chunkNibbleArray2 != null && !chunkNibbleArray2.isUninitialized()) {
                nbtCompound2.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
            }
            if (nbtCompound2.isEmpty()) continue;
            nbtCompound2.putByte("Y", (byte)i);
            nbtList.add(nbtCompound2);
        }
        nbtCompound.put("sections", nbtList);
        if (bl) {
            nbtCompound.putBoolean("isLightOn", true);
        }
        NbtList nbtList2 = new NbtList();
        for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
            nbtCompound3 = chunk.getPackedBlockEntityNbt(blockPos);
            if (nbtCompound3 == null) continue;
            nbtList2.add(nbtCompound3);
        }
        nbtCompound.put("block_entities", nbtList2);
        if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.PROTOCHUNK) {
            ProtoChunk protoChunk = (ProtoChunk)chunk;
            NbtList nbtList3 = new NbtList();
            nbtList3.addAll(protoChunk.getEntities());
            nbtCompound.put("entities", nbtList3);
            nbtCompound.put("Lights", ChunkSerializer.toNbt(protoChunk.getLightSourcesBySection()));
            nbtCompound3 = new NbtCompound();
            for (GenerationStep.Carver carver : GenerationStep.Carver.values()) {
                CarvingMask carvingMask = protoChunk.getCarvingMask(carver);
                if (carvingMask == null) continue;
                nbtCompound3.putLongArray(carver.toString(), carvingMask.getMask());
            }
            nbtCompound.put("CarvingMasks", nbtCompound3);
        }
        ChunkSerializer.serializeTicks(world, nbtCompound, chunk.getTickSchedulers());
        nbtCompound.put("PostProcessing", ChunkSerializer.toNbt(chunk.getPostProcessingLists()));
        NbtCompound nbtCompound4 = new NbtCompound();
        for (Map.Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
            if (!chunk.getStatus().getHeightmapTypes().contains(entry.getKey())) continue;
            nbtCompound4.put(entry.getKey().getName(), new NbtLongArray(entry.getValue().asLongArray()));
        }
        nbtCompound.put("Heightmaps", nbtCompound4);
        nbtCompound.put("structures", ChunkSerializer.writeStructures(StructureContext.from(world), chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences()));
        return nbtCompound;
    }

    private static void serializeTicks(ServerWorld world, NbtCompound nbt, Chunk.TickSchedulers tickSchedulers) {
        long l = world.getLevelProperties().getTime();
        nbt.put(BLOCK_TICKS, tickSchedulers.blocks().toNbt(l, block -> Registry.BLOCK.getId((Block)block).toString()));
        nbt.put(FLUID_TICKS, tickSchedulers.fluids().toNbt(l, fluid -> Registry.FLUID.getId((Fluid)fluid).toString()));
    }

    public static ChunkStatus.ChunkType getChunkType(@Nullable NbtCompound nbt) {
        if (nbt != null) {
            return ChunkStatus.byId(nbt.getString("Status")).getChunkType();
        }
        return ChunkStatus.ChunkType.PROTOCHUNK;
    }

    @Nullable
    private static WorldChunk.EntityLoader getEntityLoadingCallback(ServerWorld world, NbtCompound nbt) {
        NbtList nbtList = ChunkSerializer.getList(nbt, "entities");
        NbtList nbtList2 = ChunkSerializer.getList(nbt, "block_entities");
        if (nbtList == null && nbtList2 == null) {
            return null;
        }
        return chunk -> {
            if (nbtList != null) {
                world.loadEntities(EntityType.streamFromNbt(nbtList, world));
            }
            if (nbtList2 != null) {
                for (int i = 0; i < nbtList2.size(); ++i) {
                    NbtCompound nbtCompound = nbtList2.getCompound(i);
                    boolean bl = nbtCompound.getBoolean("keepPacked");
                    if (bl) {
                        chunk.addPendingBlockEntityNbt(nbtCompound);
                        continue;
                    }
                    BlockPos blockPos = BlockEntity.posFromNbt(nbtCompound);
                    BlockEntity blockEntity = BlockEntity.createFromNbt(blockPos, chunk.getBlockState(blockPos), nbtCompound);
                    if (blockEntity == null) continue;
                    chunk.setBlockEntity(blockEntity);
                }
            }
        };
    }

    @Nullable
    private static NbtList getList(NbtCompound nbt, String key) {
        NbtList nbtList = nbt.getList(key, 10);
        return nbtList.isEmpty() ? null : nbtList;
    }

    private static NbtCompound writeStructures(StructureContext context, ChunkPos pos, Map<StructureFeature<?>, StructureStart<?>> starts, Map<StructureFeature<?>, LongSet> references) {
        NbtCompound nbtCompound = new NbtCompound();
        NbtCompound nbtCompound2 = new NbtCompound();
        for (Map.Entry<StructureFeature<?>, StructureStart<?>> entry : starts.entrySet()) {
            nbtCompound2.put(entry.getKey().getName(), entry.getValue().toNbt(context, pos));
        }
        nbtCompound.put("starts", nbtCompound2);
        NbtCompound nbtCompound3 = new NbtCompound();
        for (Map.Entry<StructureFeature<?>, LongSet> entry2 : references.entrySet()) {
            nbtCompound3.put(entry2.getKey().getName(), new NbtLongArray(entry2.getValue()));
        }
        nbtCompound.put("References", nbtCompound3);
        return nbtCompound;
    }

    private static Map<StructureFeature<?>, StructureStart<?>> readStructureStarts(StructureContext context, NbtCompound nbt, long worldSeed) {
        HashMap<StructureFeature<?>, StructureStart<?>> map = Maps.newHashMap();
        NbtCompound nbtCompound = nbt.getCompound("starts");
        for (String string : nbtCompound.getKeys()) {
            String string2 = string.toLowerCase(Locale.ROOT);
            StructureFeature structureFeature = (StructureFeature)StructureFeature.STRUCTURES.get(string2);
            if (structureFeature == null) {
                LOGGER.error("Unknown structure start: {}", (Object)string2);
                continue;
            }
            StructureStart<?> structureStart = StructureFeature.readStructureStart(context, nbtCompound.getCompound(string), worldSeed);
            if (structureStart == null) continue;
            map.put(structureFeature, structureStart);
        }
        return map;
    }

    private static Map<StructureFeature<?>, LongSet> readStructureReferences(ChunkPos pos, NbtCompound nbt) {
        HashMap<StructureFeature<?>, LongSet> map = Maps.newHashMap();
        NbtCompound nbtCompound = nbt.getCompound("References");
        for (String string : nbtCompound.getKeys()) {
            String string2 = string.toLowerCase(Locale.ROOT);
            StructureFeature structureFeature = (StructureFeature)StructureFeature.STRUCTURES.get(string2);
            if (structureFeature == null) {
                LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", (Object)string2, (Object)pos);
                continue;
            }
            map.put(structureFeature, new LongOpenHashSet(Arrays.stream(nbtCompound.getLongArray(string)).filter(packedPos -> {
                ChunkPos chunkPos2 = new ChunkPos(packedPos);
                if (chunkPos2.getChebyshevDistance(pos) > 8) {
                    LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", (Object)string2, (Object)chunkPos2, (Object)pos);
                    return false;
                }
                return true;
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

