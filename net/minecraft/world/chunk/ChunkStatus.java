/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;

public class ChunkStatus {
    private static final EnumSet<Heightmap.Type> PRE_CARVER_HEIGHTMAPS = EnumSet.of(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.WORLD_SURFACE_WG);
    private static final EnumSet<Heightmap.Type> POST_CARVER_HEIGHTMAPS = EnumSet.of(Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE, Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
    private static final class_4305 field_19345 = (chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> {
        if (chunk instanceof ProtoChunk && !chunk.getStatus().isAtLeast(chunkStatus)) {
            ((ProtoChunk)chunk).setStatus(chunkStatus);
        }
        return CompletableFuture.completedFuture(Either.left(chunk));
    };
    public static final ChunkStatus EMPTY = ChunkStatus.register("empty", null, -1, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> {});
    public static final ChunkStatus STRUCTURE_STARTS = ChunkStatus.register("structure_starts", EMPTY, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ChunkStatus chunkStatus, ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, StructureManager structureManager, ServerLightingProvider serverLightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk chunk) -> {
        if (!chunk.getStatus().isAtLeast(chunkStatus)) {
            if (serverWorld.getLevelProperties().hasStructures()) {
                chunkGenerator.setStructureStarts(chunk, chunkGenerator, structureManager);
            }
            if (chunk instanceof ProtoChunk) {
                ((ProtoChunk)chunk).setStatus(chunkStatus);
            }
        }
        return CompletableFuture.completedFuture(Either.left(chunk));
    });
    public static final ChunkStatus STRUCTURE_REFERENCES = ChunkStatus.register("structure_references", STRUCTURE_STARTS, 8, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.addStructureReferences(new ChunkRegion(serverWorld, list), chunk));
    public static final ChunkStatus BIOMES = ChunkStatus.register("biomes", STRUCTURE_REFERENCES, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.populateBiomes(chunk));
    public static final ChunkStatus NOISE = ChunkStatus.register("noise", BIOMES, 8, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.populateNoise(new ChunkRegion(serverWorld, list), chunk));
    public static final ChunkStatus SURFACE = ChunkStatus.register("surface", NOISE, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.buildSurface(chunk));
    public static final ChunkStatus CARVERS = ChunkStatus.register("carvers", SURFACE, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.AIR));
    public static final ChunkStatus LIQUID_CARVERS = ChunkStatus.register("liquid_carvers", CARVERS, 0, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.carve(chunk, GenerationStep.Carver.LIQUID));
    public static final ChunkStatus FEATURES = ChunkStatus.register("features", LIQUID_CARVERS, 8, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ChunkStatus chunkStatus, ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, StructureManager structureManager, ServerLightingProvider serverLightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk chunk) -> {
        chunk.setLightingProvider(serverLightingProvider);
        if (!chunk.getStatus().isAtLeast(chunkStatus)) {
            Heightmap.populateHeightmaps(chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE));
            chunkGenerator.generateFeatures(new ChunkRegion(serverWorld, list));
            if (chunk instanceof ProtoChunk) {
                ((ProtoChunk)chunk).setStatus(chunkStatus);
            }
        }
        return CompletableFuture.completedFuture(Either.left(chunk));
    });
    public static final ChunkStatus LIGHT = ChunkStatus.method_20611("light", FEATURES, 1, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> ChunkStatus.method_20610(chunkStatus, serverLightingProvider, chunk), (chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> ChunkStatus.method_20610(chunkStatus, serverLightingProvider, chunk));
    public static final ChunkStatus SPAWN = ChunkStatus.register("spawn", LIGHT, 0, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.populateEntities(new ChunkRegion(serverWorld, list)));
    public static final ChunkStatus HEIGHTMAPS = ChunkStatus.register("heightmaps", SPAWN, 0, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> {});
    public static final ChunkStatus FULL = ChunkStatus.method_20611("full", HEIGHTMAPS, 0, POST_CARVER_HEIGHTMAPS, ChunkType.LEVELCHUNK, (chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> (CompletableFuture)function.apply(chunk), (chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> (CompletableFuture)function.apply(chunk));
    private static final List<ChunkStatus> DISTANCE_TO_TARGET_GENERATION_STATUS = ImmutableList.of(FULL, FEATURES, LIQUID_CARVERS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS);
    private static final IntList STATUS_TO_TARGET_GENERATION_RADIUS = Util.make(new IntArrayList(ChunkStatus.createOrderedList().size()), intArrayList -> {
        int i = 0;
        for (int j = ChunkStatus.createOrderedList().size() - 1; j >= 0; --j) {
            while (i + 1 < DISTANCE_TO_TARGET_GENERATION_STATUS.size() && j <= DISTANCE_TO_TARGET_GENERATION_STATUS.get(i + 1).getIndex()) {
                ++i;
            }
            intArrayList.add(0, i);
        }
    });
    private final String id;
    private final int index;
    private final ChunkStatus previous;
    private final Task task;
    private final class_4305 field_19346;
    private final int taskMargin;
    private final ChunkType chunkType;
    private final EnumSet<Heightmap.Type> heightMapTypes;

    private static CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> method_20610(ChunkStatus chunkStatus, ServerLightingProvider serverLightingProvider, Chunk chunk) {
        boolean bl = ChunkStatus.method_20608(chunkStatus, chunk);
        if (!chunk.getStatus().isAtLeast(chunkStatus)) {
            ((ProtoChunk)chunk).setStatus(chunkStatus);
        }
        return serverLightingProvider.light(chunk, bl).thenApply(Either::left);
    }

    private static ChunkStatus register(String string, @Nullable ChunkStatus chunkStatus, int i, EnumSet<Heightmap.Type> enumSet, ChunkType chunkType, SimpleTask simpleTask) {
        return ChunkStatus.register(string, chunkStatus, i, enumSet, chunkType, (Task)simpleTask);
    }

    private static ChunkStatus register(String string, @Nullable ChunkStatus chunkStatus, int i, EnumSet<Heightmap.Type> enumSet, ChunkType chunkType, Task task) {
        return ChunkStatus.method_20611(string, chunkStatus, i, enumSet, chunkType, task, field_19345);
    }

    private static ChunkStatus method_20611(String string, @Nullable ChunkStatus chunkStatus, int i, EnumSet<Heightmap.Type> enumSet, ChunkType chunkType, Task task, class_4305 arg) {
        return Registry.register(Registry.CHUNK_STATUS, string, new ChunkStatus(string, chunkStatus, i, enumSet, chunkType, task, arg));
    }

    public static List<ChunkStatus> createOrderedList() {
        ChunkStatus chunkStatus;
        ArrayList<ChunkStatus> list = Lists.newArrayList();
        for (chunkStatus = FULL; chunkStatus.getPrevious() != chunkStatus; chunkStatus = chunkStatus.getPrevious()) {
            list.add(chunkStatus);
        }
        list.add(chunkStatus);
        Collections.reverse(list);
        return list;
    }

    private static boolean method_20608(ChunkStatus chunkStatus, Chunk chunk) {
        return chunk.getStatus().isAtLeast(chunkStatus) && chunk.isLightOn();
    }

    public static ChunkStatus getTargetGenerationStatus(int i) {
        if (i >= DISTANCE_TO_TARGET_GENERATION_STATUS.size()) {
            return EMPTY;
        }
        if (i < 0) {
            return FULL;
        }
        return DISTANCE_TO_TARGET_GENERATION_STATUS.get(i);
    }

    public static int getMaxTargetGenerationRadius() {
        return DISTANCE_TO_TARGET_GENERATION_STATUS.size();
    }

    public static int getTargetGenerationRadius(ChunkStatus chunkStatus) {
        return STATUS_TO_TARGET_GENERATION_RADIUS.getInt(chunkStatus.getIndex());
    }

    ChunkStatus(String string, @Nullable ChunkStatus chunkStatus, int i, EnumSet<Heightmap.Type> enumSet, ChunkType chunkType, Task task, class_4305 arg) {
        this.id = string;
        this.previous = chunkStatus == null ? this : chunkStatus;
        this.task = task;
        this.field_19346 = arg;
        this.taskMargin = i;
        this.chunkType = chunkType;
        this.heightMapTypes = enumSet;
        this.index = chunkStatus == null ? 0 : chunkStatus.getIndex() + 1;
    }

    public int getIndex() {
        return this.index;
    }

    public String getId() {
        return this.id;
    }

    public ChunkStatus getPrevious() {
        return this.previous;
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runTask(ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, StructureManager structureManager, ServerLightingProvider serverLightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list) {
        return this.task.doWork(this, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, list.get(list.size() / 2));
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> method_20612(ServerWorld serverWorld, StructureManager structureManager, ServerLightingProvider serverLightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, Chunk chunk) {
        return this.field_19346.doWork(this, serverWorld, structureManager, serverLightingProvider, function, chunk);
    }

    public int getTaskMargin() {
        return this.taskMargin;
    }

    public ChunkType getChunkType() {
        return this.chunkType;
    }

    public static ChunkStatus get(String string) {
        return Registry.CHUNK_STATUS.get(Identifier.tryParse(string));
    }

    public EnumSet<Heightmap.Type> getHeightmapTypes() {
        return this.heightMapTypes;
    }

    public boolean isAtLeast(ChunkStatus chunkStatus) {
        return this.getIndex() >= chunkStatus.getIndex();
    }

    public String toString() {
        return Registry.CHUNK_STATUS.getId(this).toString();
    }

    public static enum ChunkType {
        PROTOCHUNK,
        LEVELCHUNK;

    }

    static interface SimpleTask
    extends Task {
        @Override
        default public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(ChunkStatus chunkStatus, ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, StructureManager structureManager, ServerLightingProvider serverLightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk chunk) {
            if (!chunk.getStatus().isAtLeast(chunkStatus)) {
                this.doWork(serverWorld, chunkGenerator, list, chunk);
                if (chunk instanceof ProtoChunk) {
                    ((ProtoChunk)chunk).setStatus(chunkStatus);
                }
            }
            return CompletableFuture.completedFuture(Either.left(chunk));
        }

        public void doWork(ServerWorld var1, ChunkGenerator<?> var2, List<Chunk> var3, Chunk var4);
    }

    static interface class_4305 {
        public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(ChunkStatus var1, ServerWorld var2, StructureManager var3, ServerLightingProvider var4, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> var5, Chunk var6);
    }

    static interface Task {
        public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(ChunkStatus var1, ServerWorld var2, ChunkGenerator<?> var3, StructureManager var4, ServerLightingProvider var5, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> var6, List<Chunk> var7, Chunk var8);
    }
}

