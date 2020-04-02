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
    private static final NoGenTask STATUS_BUMP_NO_GEN_TASK = (chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> {
        if (chunk instanceof ProtoChunk && !chunk.getStatus().isAtLeast(chunkStatus)) {
            ((ProtoChunk)chunk).setStatus(chunkStatus);
        }
        return CompletableFuture.completedFuture(Either.left(chunk));
    };
    public static final ChunkStatus EMPTY = ChunkStatus.register("empty", null, -1, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> {});
    public static final ChunkStatus STRUCTURE_STARTS = ChunkStatus.register("structure_starts", EMPTY, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ChunkStatus chunkStatus, ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, StructureManager structureManager, ServerLightingProvider serverLightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk chunk) -> {
        if (!chunk.getStatus().isAtLeast(chunkStatus)) {
            if (serverWorld.getLevelProperties().hasStructures()) {
                chunkGenerator.setStructureStarts(serverWorld.getStructureAccessor(), serverWorld.getBiomeAccess().withSource(chunkGenerator.getBiomeSource()), chunk, chunkGenerator, structureManager);
            }
            if (chunk instanceof ProtoChunk) {
                ((ProtoChunk)chunk).setStatus(chunkStatus);
            }
        }
        return CompletableFuture.completedFuture(Either.left(chunk));
    });
    public static final ChunkStatus STRUCTURE_REFERENCES = ChunkStatus.register("structure_references", STRUCTURE_STARTS, 8, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.addStructureReferences(new ChunkRegion(serverWorld, list), serverWorld.getStructureAccessor(), chunk));
    public static final ChunkStatus BIOMES = ChunkStatus.register("biomes", STRUCTURE_REFERENCES, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.populateBiomes(chunk));
    public static final ChunkStatus NOISE = ChunkStatus.register("noise", BIOMES, 8, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.populateNoise(new ChunkRegion(serverWorld, list), serverWorld.getStructureAccessor(), chunk));
    public static final ChunkStatus SURFACE = ChunkStatus.register("surface", NOISE, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.buildSurface(new ChunkRegion(serverWorld, list), chunk));
    public static final ChunkStatus CARVERS = ChunkStatus.register("carvers", SURFACE, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.carve(serverWorld.getBiomeAccess().withSource(chunkGenerator.getBiomeSource()), chunk, GenerationStep.Carver.AIR));
    public static final ChunkStatus LIQUID_CARVERS = ChunkStatus.register("liquid_carvers", CARVERS, 0, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.carve(serverWorld.getBiomeAccess().withSource(chunkGenerator.getBiomeSource()), chunk, GenerationStep.Carver.LIQUID));
    public static final ChunkStatus FEATURES = ChunkStatus.register("features", LIQUID_CARVERS, 8, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ChunkStatus chunkStatus, ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, StructureManager structureManager, ServerLightingProvider serverLightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk chunk) -> {
        ProtoChunk protoChunk = (ProtoChunk)chunk;
        protoChunk.setLightingProvider(serverLightingProvider);
        if (!chunk.getStatus().isAtLeast(chunkStatus)) {
            Heightmap.populateHeightmaps(chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE));
            chunkGenerator.generateFeatures(new ChunkRegion(serverWorld, list), serverWorld.getStructureAccessor());
            protoChunk.setStatus(chunkStatus);
        }
        return CompletableFuture.completedFuture(Either.left(chunk));
    });
    public static final ChunkStatus LIGHT = ChunkStatus.register("light", FEATURES, 1, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> ChunkStatus.getLightingFuture(chunkStatus, serverLightingProvider, chunk), (chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> ChunkStatus.getLightingFuture(chunkStatus, serverLightingProvider, chunk));
    public static final ChunkStatus SPAWN = ChunkStatus.register("spawn", LIGHT, 0, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> chunkGenerator.populateEntities(new ChunkRegion(serverWorld, list)));
    public static final ChunkStatus HEIGHTMAPS = ChunkStatus.register("heightmaps", SPAWN, 0, POST_CARVER_HEIGHTMAPS, ChunkType.PROTOCHUNK, (ServerWorld serverWorld, ChunkGenerator<?> chunkGenerator, List<Chunk> list, Chunk chunk) -> {});
    public static final ChunkStatus FULL = ChunkStatus.register("full", HEIGHTMAPS, 0, POST_CARVER_HEIGHTMAPS, ChunkType.LEVELCHUNK, (chunkStatus, serverWorld, chunkGenerator, structureManager, serverLightingProvider, function, list, chunk) -> (CompletableFuture)function.apply(chunk), (chunkStatus, serverWorld, structureManager, serverLightingProvider, function, chunk) -> (CompletableFuture)function.apply(chunk));
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
    private final NoGenTask noGenTask;
    private final int taskMargin;
    private final ChunkType chunkType;
    private final EnumSet<Heightmap.Type> heightMapTypes;

    private static CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getLightingFuture(ChunkStatus status, ServerLightingProvider lightingProvider, Chunk chunk) {
        boolean bl = ChunkStatus.shouldExcludeBlockLight(status, chunk);
        if (!chunk.getStatus().isAtLeast(status)) {
            ((ProtoChunk)chunk).setStatus(status);
        }
        return lightingProvider.light(chunk, bl).thenApply(Either::left);
    }

    private static ChunkStatus register(String id, @Nullable ChunkStatus previous, int taskMargin, EnumSet<Heightmap.Type> heightMapTypes, ChunkType chunkType, SimpleTask task) {
        return ChunkStatus.register(id, previous, taskMargin, heightMapTypes, chunkType, (Task)task);
    }

    private static ChunkStatus register(String id, @Nullable ChunkStatus previous, int taskMargin, EnumSet<Heightmap.Type> heightMapTypes, ChunkType chunkType, Task task) {
        return ChunkStatus.register(id, previous, taskMargin, heightMapTypes, chunkType, task, STATUS_BUMP_NO_GEN_TASK);
    }

    private static ChunkStatus register(String id, @Nullable ChunkStatus previous, int taskMargin, EnumSet<Heightmap.Type> heightMapTypes, ChunkType chunkType, Task task, NoGenTask noGenTask) {
        return Registry.register(Registry.CHUNK_STATUS, id, new ChunkStatus(id, previous, taskMargin, heightMapTypes, chunkType, task, noGenTask));
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

    private static boolean shouldExcludeBlockLight(ChunkStatus status, Chunk chunk) {
        return chunk.getStatus().isAtLeast(status) && chunk.isLightOn();
    }

    public static ChunkStatus getTargetGenerationStatus(int distance) {
        if (distance >= DISTANCE_TO_TARGET_GENERATION_STATUS.size()) {
            return EMPTY;
        }
        if (distance < 0) {
            return FULL;
        }
        return DISTANCE_TO_TARGET_GENERATION_STATUS.get(distance);
    }

    public static int getMaxTargetGenerationRadius() {
        return DISTANCE_TO_TARGET_GENERATION_STATUS.size();
    }

    public static int getTargetGenerationRadius(ChunkStatus status) {
        return STATUS_TO_TARGET_GENERATION_RADIUS.getInt(status.getIndex());
    }

    ChunkStatus(String id, @Nullable ChunkStatus previous, int taskMargin, EnumSet<Heightmap.Type> heightMapTypes, ChunkType chunkType, Task task, NoGenTask noGenTask) {
        this.id = id;
        this.previous = previous == null ? this : previous;
        this.task = task;
        this.noGenTask = noGenTask;
        this.taskMargin = taskMargin;
        this.chunkType = chunkType;
        this.heightMapTypes = heightMapTypes;
        this.index = previous == null ? 0 : previous.getIndex() + 1;
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

    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runNoGenTask(ServerWorld serverWorld, StructureManager structureManager, ServerLightingProvider serverLightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, Chunk chunk) {
        return this.noGenTask.doWork(this, serverWorld, structureManager, serverLightingProvider, function, chunk);
    }

    public int getTaskMargin() {
        return this.taskMargin;
    }

    public ChunkType getChunkType() {
        return this.chunkType;
    }

    public static ChunkStatus get(String id) {
        return Registry.CHUNK_STATUS.get(Identifier.tryParse(id));
    }

    public EnumSet<Heightmap.Type> getHeightmapTypes() {
        return this.heightMapTypes;
    }

    public boolean isAtLeast(ChunkStatus chunk) {
        return this.getIndex() >= chunk.getIndex();
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

    static interface NoGenTask {
        public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(ChunkStatus var1, ServerWorld var2, StructureManager var3, ServerLightingProvider var4, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> var5, Chunk var6);
    }

    static interface Task {
        public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(ChunkStatus var1, ServerWorld var2, ChunkGenerator<?> var3, StructureManager var4, ServerLightingProvider var5, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> var6, List<Chunk> var7, Chunk var8);
    }
}

