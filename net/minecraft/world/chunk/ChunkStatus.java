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
    /**
     * A load task which only bumps the chunk status of the chunk.
     */
    private static final LoadTask STATUS_BUMP_LOAD_TASK = (targetStatus, world, structureManager, lightingProvider, function, chunk) -> {
        if (chunk instanceof ProtoChunk && !chunk.getStatus().isAtLeast(targetStatus)) {
            ((ProtoChunk)chunk).setStatus(targetStatus);
        }
        return CompletableFuture.completedFuture(Either.left(chunk));
    };
    public static final ChunkStatus EMPTY = ChunkStatus.register("empty", null, -1, PRE_CARVER_HEIGHTMAPS, ChunkType.field_12808, (ServerWorld world, ChunkGenerator generator, List<Chunk> surroundingChunks, Chunk chunk) -> {});
    public static final ChunkStatus STRUCTURE_STARTS = ChunkStatus.register("structure_starts", EMPTY, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.field_12808, (ChunkStatus targetStatus, ServerWorld world, ChunkGenerator generator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> surroundingChunks, Chunk chunk) -> {
        if (!chunk.getStatus().isAtLeast(targetStatus)) {
            if (world.getServer().getSaveProperties().getGeneratorOptions().shouldGenerateStructures()) {
                generator.setStructureStarts(world.getRegistryManager(), world.getStructureAccessor(), chunk, structureManager, world.getSeed());
            }
            if (chunk instanceof ProtoChunk) {
                ((ProtoChunk)chunk).setStatus(targetStatus);
            }
        }
        return CompletableFuture.completedFuture(Either.left(chunk));
    });
    public static final ChunkStatus STRUCTURE_REFERENCES = ChunkStatus.register("structure_references", STRUCTURE_STARTS, 8, PRE_CARVER_HEIGHTMAPS, ChunkType.field_12808, (ServerWorld world, ChunkGenerator generator, List<Chunk> surroundingChunks, Chunk chunk) -> {
        ChunkRegion chunkRegion = new ChunkRegion(world, surroundingChunks);
        generator.addStructureReferences(chunkRegion, world.getStructureAccessor().forRegion(chunkRegion), chunk);
    });
    public static final ChunkStatus BIOMES = ChunkStatus.register("biomes", STRUCTURE_REFERENCES, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.field_12808, (ServerWorld world, ChunkGenerator generator, List<Chunk> surroundingChunks, Chunk chunk) -> generator.populateBiomes(world.getRegistryManager().get(Registry.BIOME_KEY), chunk));
    public static final ChunkStatus NOISE = ChunkStatus.register("noise", BIOMES, 8, PRE_CARVER_HEIGHTMAPS, ChunkType.field_12808, (ServerWorld world, ChunkGenerator generator, List<Chunk> surroundingChunks, Chunk chunk) -> {
        ChunkRegion chunkRegion = new ChunkRegion(world, surroundingChunks);
        generator.populateNoise(chunkRegion, world.getStructureAccessor().forRegion(chunkRegion), chunk);
    });
    public static final ChunkStatus SURFACE = ChunkStatus.register("surface", NOISE, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.field_12808, (ServerWorld world, ChunkGenerator generator, List<Chunk> surroundingChunks, Chunk chunk) -> generator.buildSurface(new ChunkRegion(world, surroundingChunks), chunk));
    public static final ChunkStatus CARVERS = ChunkStatus.register("carvers", SURFACE, 0, PRE_CARVER_HEIGHTMAPS, ChunkType.field_12808, (ServerWorld world, ChunkGenerator generator, List<Chunk> surroundingChunks, Chunk chunk) -> generator.carve(world.getSeed(), world.getBiomeAccess(), chunk, GenerationStep.Carver.AIR));
    public static final ChunkStatus LIQUID_CARVERS = ChunkStatus.register("liquid_carvers", CARVERS, 0, POST_CARVER_HEIGHTMAPS, ChunkType.field_12808, (ServerWorld world, ChunkGenerator generator, List<Chunk> surroundingChunks, Chunk chunk) -> generator.carve(world.getSeed(), world.getBiomeAccess(), chunk, GenerationStep.Carver.LIQUID));
    public static final ChunkStatus FEATURES = ChunkStatus.register("features", LIQUID_CARVERS, 8, POST_CARVER_HEIGHTMAPS, ChunkType.field_12808, (ChunkStatus status, ServerWorld world, ChunkGenerator generator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> surroundingChunks, Chunk chunk) -> {
        ProtoChunk protoChunk = (ProtoChunk)chunk;
        protoChunk.setLightingProvider(lightingProvider);
        if (!chunk.getStatus().isAtLeast(status)) {
            Heightmap.populateHeightmaps(chunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE));
            ChunkRegion chunkRegion = new ChunkRegion(world, surroundingChunks);
            generator.generateFeatures(chunkRegion, world.getStructureAccessor().forRegion(chunkRegion));
            protoChunk.setStatus(status);
        }
        return CompletableFuture.completedFuture(Either.left(chunk));
    });
    public static final ChunkStatus LIGHT = ChunkStatus.register("light", FEATURES, 1, POST_CARVER_HEIGHTMAPS, ChunkType.field_12808, (targetStatus, world, generator, structureManager, lightingProvider, function, surroundingChunks, chunk) -> ChunkStatus.getLightingFuture(targetStatus, lightingProvider, chunk), (status, world, structureManager, lightingProvider, function, chunk) -> ChunkStatus.getLightingFuture(status, lightingProvider, chunk));
    public static final ChunkStatus SPAWN = ChunkStatus.register("spawn", LIGHT, 0, POST_CARVER_HEIGHTMAPS, ChunkType.field_12808, (ServerWorld targetStatus, ChunkGenerator generator, List<Chunk> surroundingChunks, Chunk chunk) -> generator.populateEntities(new ChunkRegion(targetStatus, surroundingChunks)));
    public static final ChunkStatus HEIGHTMAPS = ChunkStatus.register("heightmaps", SPAWN, 0, POST_CARVER_HEIGHTMAPS, ChunkType.field_12808, (ServerWorld world, ChunkGenerator generator, List<Chunk> surroundingChunks, Chunk chunk) -> {});
    public static final ChunkStatus FULL = ChunkStatus.register("full", HEIGHTMAPS, 0, POST_CARVER_HEIGHTMAPS, ChunkType.field_12807, (targetStatus, world, generator, structureManager, lightingProvider, function, surroundingChunks, chunk) -> (CompletableFuture)function.apply(chunk), (status, world, structureManager, lightingProvider, function, chunk) -> (CompletableFuture)function.apply(chunk));
    private static final List<ChunkStatus> DISTANCE_TO_STATUS = ImmutableList.of(FULL, FEATURES, LIQUID_CARVERS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS);
    private static final IntList STATUS_TO_DISTANCE = Util.make(new IntArrayList(ChunkStatus.createOrderedList().size()), intArrayList -> {
        int i = 0;
        for (int j = ChunkStatus.createOrderedList().size() - 1; j >= 0; --j) {
            while (i + 1 < DISTANCE_TO_STATUS.size() && j <= DISTANCE_TO_STATUS.get(i + 1).getIndex()) {
                ++i;
            }
            intArrayList.add(0, i);
        }
    });
    private final String id;
    private final int index;
    private final ChunkStatus previous;
    private final GenerationTask generationTask;
    private final LoadTask loadTask;
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

    private static ChunkStatus register(String id, @Nullable ChunkStatus previous, int taskMargin, EnumSet<Heightmap.Type> heightMapTypes, ChunkType chunkType, SimpleGenerationTask task) {
        return ChunkStatus.register(id, previous, taskMargin, heightMapTypes, chunkType, (GenerationTask)task);
    }

    private static ChunkStatus register(String id, @Nullable ChunkStatus previous, int taskMargin, EnumSet<Heightmap.Type> heightMapTypes, ChunkType chunkType, GenerationTask task) {
        return ChunkStatus.register(id, previous, taskMargin, heightMapTypes, chunkType, task, STATUS_BUMP_LOAD_TASK);
    }

    private static ChunkStatus register(String id, @Nullable ChunkStatus previous, int taskMargin, EnumSet<Heightmap.Type> heightMapTypes, ChunkType chunkType, GenerationTask task, LoadTask loadTask) {
        return Registry.register(Registry.CHUNK_STATUS, id, new ChunkStatus(id, previous, taskMargin, heightMapTypes, chunkType, task, loadTask));
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

    public static ChunkStatus byDistanceFromFull(int level) {
        if (level >= DISTANCE_TO_STATUS.size()) {
            return EMPTY;
        }
        if (level < 0) {
            return FULL;
        }
        return DISTANCE_TO_STATUS.get(level);
    }

    public static int getMaxDistanceFromFull() {
        return DISTANCE_TO_STATUS.size();
    }

    public static int getDistanceFromFull(ChunkStatus status) {
        return STATUS_TO_DISTANCE.getInt(status.getIndex());
    }

    ChunkStatus(String id, @Nullable ChunkStatus previous, int taskMargin, EnumSet<Heightmap.Type> heightMapTypes, ChunkType chunkType, GenerationTask generationTask, LoadTask loadTask) {
        this.id = id;
        this.previous = previous == null ? this : previous;
        this.generationTask = generationTask;
        this.loadTask = loadTask;
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

    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runGenerationTask(ServerWorld world, ChunkGenerator chunkGenerator, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> chunks) {
        return this.generationTask.doWork(this, world, chunkGenerator, structureManager, lightingProvider, function, chunks, chunks.get(chunks.size() / 2));
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> runLoadTask(ServerWorld world, StructureManager structureManager, ServerLightingProvider lightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, Chunk chunk) {
        return this.loadTask.doWork(this, world, structureManager, lightingProvider, function, chunk);
    }

    public int getTaskMargin() {
        return this.taskMargin;
    }

    public ChunkType getChunkType() {
        return this.chunkType;
    }

    public static ChunkStatus byId(String id) {
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
        field_12808,
        field_12807;

    }

    static interface SimpleGenerationTask
    extends GenerationTask {
        @Override
        default public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(ChunkStatus chunkStatus, ServerWorld serverWorld, ChunkGenerator chunkGenerator, StructureManager structureManager, ServerLightingProvider serverLightingProvider, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> function, List<Chunk> list, Chunk chunk) {
            if (!chunk.getStatus().isAtLeast(chunkStatus)) {
                this.doWork(serverWorld, chunkGenerator, list, chunk);
                if (chunk instanceof ProtoChunk) {
                    ((ProtoChunk)chunk).setStatus(chunkStatus);
                }
            }
            return CompletableFuture.completedFuture(Either.left(chunk));
        }

        public void doWork(ServerWorld var1, ChunkGenerator var2, List<Chunk> var3, Chunk var4);
    }

    static interface LoadTask {
        public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(ChunkStatus var1, ServerWorld var2, StructureManager var3, ServerLightingProvider var4, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> var5, Chunk var6);
    }

    static interface GenerationTask {
        public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> doWork(ChunkStatus var1, ServerWorld var2, ChunkGenerator var3, StructureManager var4, ServerLightingProvider var5, Function<Chunk, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> var6, List<Chunk> var7, Chunk var8);
    }
}

