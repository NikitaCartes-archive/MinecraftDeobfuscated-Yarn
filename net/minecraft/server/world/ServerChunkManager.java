/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.LightType;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SpawnDensityCapper;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.NbtScannable;
import org.jetbrains.annotations.Nullable;

public class ServerChunkManager
extends ChunkManager {
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
    private final ChunkTicketManager ticketManager;
    final ServerWorld world;
    final Thread serverThread;
    final ServerLightingProvider lightingProvider;
    private final MainThreadExecutor mainThreadExecutor;
    public final ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
    private final PersistentStateManager persistentStateManager;
    private long lastMobSpawningTime;
    private boolean spawnMonsters = true;
    private boolean spawnAnimals = true;
    private static final int CACHE_SIZE = 4;
    private final long[] chunkPosCache = new long[4];
    private final ChunkStatus[] chunkStatusCache = new ChunkStatus[4];
    private final Chunk[] chunkCache = new Chunk[4];
    @Nullable
    @Debug
    private SpawnHelper.Info spawnInfo;

    public ServerChunkManager(ServerWorld world, LevelStorage.Session session, DataFixer dataFixer, StructureManager structureManager, Executor workerExecutor, ChunkGenerator chunkGenerator, int viewDistance, int simulationDistance, boolean dsync, WorldGenerationProgressListener worldGenerationProgressListener, ChunkStatusChangeListener chunkStatusChangeListener, Supplier<PersistentStateManager> persistentStateManagerFactory) {
        this.world = world;
        this.mainThreadExecutor = new MainThreadExecutor(world);
        this.serverThread = Thread.currentThread();
        File file = session.getWorldDirectory(world.getRegistryKey()).resolve("data").toFile();
        file.mkdirs();
        this.persistentStateManager = new PersistentStateManager(file, dataFixer);
        this.threadedAnvilChunkStorage = new ThreadedAnvilChunkStorage(world, session, dataFixer, structureManager, workerExecutor, this.mainThreadExecutor, this, chunkGenerator, worldGenerationProgressListener, chunkStatusChangeListener, persistentStateManagerFactory, viewDistance, dsync);
        this.lightingProvider = this.threadedAnvilChunkStorage.getLightingProvider();
        this.ticketManager = this.threadedAnvilChunkStorage.getTicketManager();
        this.ticketManager.setSimulationDistance(simulationDistance);
        this.initChunkCaches();
    }

    @Override
    public ServerLightingProvider getLightingProvider() {
        return this.lightingProvider;
    }

    @Nullable
    private ChunkHolder getChunkHolder(long pos) {
        return this.threadedAnvilChunkStorage.getChunkHolder(pos);
    }

    public int getTotalChunksLoadedCount() {
        return this.threadedAnvilChunkStorage.getTotalChunksLoadedCount();
    }

    private void putInCache(long pos, Chunk chunk, ChunkStatus status) {
        for (int i = 3; i > 0; --i) {
            this.chunkPosCache[i] = this.chunkPosCache[i - 1];
            this.chunkStatusCache[i] = this.chunkStatusCache[i - 1];
            this.chunkCache[i] = this.chunkCache[i - 1];
        }
        this.chunkPosCache[0] = pos;
        this.chunkStatusCache[0] = status;
        this.chunkCache[0] = chunk;
    }

    @Override
    @Nullable
    public Chunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
        Chunk chunk2;
        if (Thread.currentThread() != this.serverThread) {
            return CompletableFuture.supplyAsync(() -> this.getChunk(x, z, leastStatus, create), this.mainThreadExecutor).join();
        }
        Profiler profiler = this.world.getProfiler();
        profiler.visit("getChunk");
        long l = ChunkPos.toLong(x, z);
        for (int i = 0; i < 4; ++i) {
            if (l != this.chunkPosCache[i] || leastStatus != this.chunkStatusCache[i] || (chunk2 = this.chunkCache[i]) == null && create) continue;
            return chunk2;
        }
        profiler.visit("getChunkCacheMiss");
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getChunkFuture(x, z, leastStatus, create);
        this.mainThreadExecutor.runTasks(completableFuture::isDone);
        chunk2 = completableFuture.join().map(chunk -> chunk, unloaded -> {
            if (create) {
                throw Util.throwOrPause(new IllegalStateException("Chunk not there when requested: " + unloaded));
            }
            return null;
        });
        this.putInCache(l, chunk2, leastStatus);
        return chunk2;
    }

    @Override
    @Nullable
    public WorldChunk getWorldChunk(int chunkX, int chunkZ) {
        if (Thread.currentThread() != this.serverThread) {
            return null;
        }
        this.world.getProfiler().visit("getChunkNow");
        long l = ChunkPos.toLong(chunkX, chunkZ);
        for (int i = 0; i < 4; ++i) {
            if (l != this.chunkPosCache[i] || this.chunkStatusCache[i] != ChunkStatus.FULL) continue;
            Chunk chunk = this.chunkCache[i];
            return chunk instanceof WorldChunk ? (WorldChunk)chunk : null;
        }
        ChunkHolder chunkHolder = this.getChunkHolder(l);
        if (chunkHolder == null) {
            return null;
        }
        Either either = chunkHolder.getValidFutureFor(ChunkStatus.FULL).getNow(null);
        if (either == null) {
            return null;
        }
        Chunk chunk2 = either.left().orElse(null);
        if (chunk2 != null) {
            this.putInCache(l, chunk2, ChunkStatus.FULL);
            if (chunk2 instanceof WorldChunk) {
                return (WorldChunk)chunk2;
            }
        }
        return null;
    }

    private void initChunkCaches() {
        Arrays.fill(this.chunkPosCache, ChunkPos.MARKER);
        Arrays.fill(this.chunkStatusCache, null);
        Arrays.fill(this.chunkCache, null);
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFutureSyncOnMainThread(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        CompletionStage<Either<Chunk, ChunkHolder.Unloaded>> completableFuture2;
        boolean bl;
        boolean bl2 = bl = Thread.currentThread() == this.serverThread;
        if (bl) {
            completableFuture2 = this.getChunkFuture(chunkX, chunkZ, leastStatus, create);
            this.mainThreadExecutor.runTasks(() -> completableFuture2.isDone());
        } else {
            completableFuture2 = CompletableFuture.supplyAsync(() -> this.getChunkFuture(chunkX, chunkZ, leastStatus, create), this.mainThreadExecutor).thenCompose(completableFuture -> completableFuture);
        }
        return completableFuture2;
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkFuture(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        long l = chunkPos.toLong();
        int i = 33 + ChunkStatus.getDistanceFromFull(leastStatus);
        ChunkHolder chunkHolder = this.getChunkHolder(l);
        if (create) {
            this.ticketManager.addTicketWithLevel(ChunkTicketType.UNKNOWN, chunkPos, i, chunkPos);
            if (this.isMissingForLevel(chunkHolder, i)) {
                Profiler profiler = this.world.getProfiler();
                profiler.push("chunkLoad");
                this.tick();
                chunkHolder = this.getChunkHolder(l);
                profiler.pop();
                if (this.isMissingForLevel(chunkHolder, i)) {
                    throw Util.throwOrPause(new IllegalStateException("No chunk holder after ticket has been added"));
                }
            }
        }
        if (this.isMissingForLevel(chunkHolder, i)) {
            return ChunkHolder.UNLOADED_CHUNK_FUTURE;
        }
        return chunkHolder.getChunkAt(leastStatus, this.threadedAnvilChunkStorage);
    }

    private boolean isMissingForLevel(@Nullable ChunkHolder holder, int maxLevel) {
        return holder == null || holder.getLevel() > maxLevel;
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        int i;
        ChunkHolder chunkHolder = this.getChunkHolder(new ChunkPos(x, z).toLong());
        return !this.isMissingForLevel(chunkHolder, i = 33 + ChunkStatus.getDistanceFromFull(ChunkStatus.FULL));
    }

    @Override
    public BlockView getChunk(int chunkX, int chunkZ) {
        long l = ChunkPos.toLong(chunkX, chunkZ);
        ChunkHolder chunkHolder = this.getChunkHolder(l);
        if (chunkHolder == null) {
            return null;
        }
        int i = CHUNK_STATUSES.size() - 1;
        while (true) {
            ChunkStatus chunkStatus;
            Optional<Chunk> optional;
            if ((optional = chunkHolder.getFutureFor(chunkStatus = CHUNK_STATUSES.get(i)).getNow(ChunkHolder.UNLOADED_CHUNK).left()).isPresent()) {
                return optional.get();
            }
            if (chunkStatus == ChunkStatus.LIGHT.getPrevious()) break;
            --i;
        }
        return null;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    public boolean executeQueuedTasks() {
        return this.mainThreadExecutor.runTask();
    }

    boolean tick() {
        boolean bl = this.ticketManager.tick(this.threadedAnvilChunkStorage);
        boolean bl2 = this.threadedAnvilChunkStorage.updateHolderMap();
        if (bl || bl2) {
            this.initChunkCaches();
            return true;
        }
        return false;
    }

    public boolean isTickingFutureReady(long pos) {
        ChunkHolder chunkHolder = this.getChunkHolder(pos);
        if (chunkHolder == null) {
            return false;
        }
        if (!this.world.shouldTickBlocksInChunk(pos)) {
            return false;
        }
        Either either = chunkHolder.getTickingFuture().getNow(null);
        return either != null && either.left().isPresent();
    }

    public void save(boolean flush) {
        this.tick();
        this.threadedAnvilChunkStorage.save(flush);
    }

    @Override
    public void close() throws IOException {
        this.save(true);
        this.lightingProvider.close();
        this.threadedAnvilChunkStorage.close();
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {
        this.world.getProfiler().push("purge");
        this.ticketManager.purge();
        this.tick();
        this.world.getProfiler().swap("chunks");
        if (tickChunks) {
            this.tickChunks();
        }
        this.world.getProfiler().swap("unload");
        this.threadedAnvilChunkStorage.tick(shouldKeepTicking);
        this.world.getProfiler().pop();
        this.initChunkCaches();
    }

    private void tickChunks() {
        SpawnHelper.Info info;
        long l = this.world.getTime();
        long m = l - this.lastMobSpawningTime;
        this.lastMobSpawningTime = l;
        boolean bl = this.world.isDebugWorld();
        if (bl) {
            this.threadedAnvilChunkStorage.tickEntityMovement();
            return;
        }
        WorldProperties worldProperties = this.world.getLevelProperties();
        Profiler profiler = this.world.getProfiler();
        profiler.push("pollingChunks");
        int i = this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
        boolean bl2 = worldProperties.getTime() % 400L == 0L;
        profiler.push("naturalSpawnCount");
        int j = this.ticketManager.getTickedChunkCount();
        this.spawnInfo = info = SpawnHelper.setupSpawn(j, this.world.iterateEntities(), this::ifChunkLoaded, new SpawnDensityCapper(this.threadedAnvilChunkStorage));
        profiler.swap("filteringLoadedChunks");
        ArrayList<ChunkWithHolder> list = Lists.newArrayListWithCapacity(j);
        for (ChunkHolder chunkHolder : this.threadedAnvilChunkStorage.entryIterator()) {
            WorldChunk worldChunk = chunkHolder.getWorldChunk();
            if (worldChunk == null) continue;
            list.add(new ChunkWithHolder(worldChunk, chunkHolder));
        }
        profiler.swap("spawnAndTick");
        boolean bl3 = this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
        Collections.shuffle(list);
        for (ChunkWithHolder chunkWithHolder : list) {
            WorldChunk worldChunk2 = chunkWithHolder.chunk;
            ChunkPos chunkPos = worldChunk2.getPos();
            if (!this.world.shouldTick(chunkPos) || !this.threadedAnvilChunkStorage.shouldTick(chunkPos)) continue;
            worldChunk2.increaseInhabitedTime(m);
            if (bl3 && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(chunkPos)) {
                SpawnHelper.spawn(this.world, worldChunk2, info, this.spawnAnimals, this.spawnMonsters, bl2);
            }
            if (!this.world.shouldTickBlocksInChunk(chunkPos.toLong())) continue;
            this.world.tickChunk(worldChunk2, i);
        }
        profiler.swap("customSpawners");
        if (bl3) {
            this.world.tickSpawners(this.spawnMonsters, this.spawnAnimals);
        }
        profiler.swap("broadcast");
        list.forEach(chunk -> chunk.holder.flushUpdates(chunk.chunk));
        profiler.pop();
        profiler.pop();
        this.threadedAnvilChunkStorage.tickEntityMovement();
    }

    private void ifChunkLoaded(long pos, Consumer<WorldChunk> chunkConsumer) {
        ChunkHolder chunkHolder = this.getChunkHolder(pos);
        if (chunkHolder != null) {
            chunkHolder.getAccessibleFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left().ifPresent(chunkConsumer);
        }
    }

    @Override
    public String getDebugString() {
        return Integer.toString(this.getLoadedChunkCount());
    }

    @VisibleForTesting
    public int getPendingTasks() {
        return this.mainThreadExecutor.getTaskCount();
    }

    public ChunkGenerator getChunkGenerator() {
        return this.threadedAnvilChunkStorage.getChunkGenerator();
    }

    public NoiseConfig getNoiseConfig() {
        return this.threadedAnvilChunkStorage.getNoiseConfig();
    }

    @Override
    public int getLoadedChunkCount() {
        return this.threadedAnvilChunkStorage.getLoadedChunkCount();
    }

    public void markForUpdate(BlockPos pos) {
        int j;
        int i = ChunkSectionPos.getSectionCoord(pos.getX());
        ChunkHolder chunkHolder = this.getChunkHolder(ChunkPos.toLong(i, j = ChunkSectionPos.getSectionCoord(pos.getZ())));
        if (chunkHolder != null) {
            chunkHolder.markForBlockUpdate(pos);
        }
    }

    @Override
    public void onLightUpdate(LightType type, ChunkSectionPos pos) {
        this.mainThreadExecutor.execute(() -> {
            ChunkHolder chunkHolder = this.getChunkHolder(pos.toChunkPos().toLong());
            if (chunkHolder != null) {
                chunkHolder.markForLightUpdate(type, pos.getSectionY());
            }
        });
    }

    /**
     * Adds a chunk ticket to the ticket manager.
     * 
     * <p>Addition of a ticket may load chunk(s) at some point in the future depending on the loading level in the ticket's vicinity.
     */
    public <T> void addTicket(ChunkTicketType<T> ticketType, ChunkPos pos, int radius, T argument) {
        this.ticketManager.addTicket(ticketType, pos, radius, argument);
    }

    /**
     * Removes a chunk ticket from the ticket manager.
     * 
     * <p>Removal of a ticket may unload chunk(s) at some point in the future depending on the loading levels in the ticket's vicinity after removal.
     */
    public <T> void removeTicket(ChunkTicketType<T> ticketType, ChunkPos pos, int radius, T argument) {
        this.ticketManager.removeTicket(ticketType, pos, radius, argument);
    }

    @Override
    public void setChunkForced(ChunkPos pos, boolean forced) {
        this.ticketManager.setChunkForced(pos, forced);
    }

    /**
     * Updates the chunk section position of the {@code player}. This can either be a
     * result of the player's movement or its camera entity's movement.
     * 
     * <p>This updates the section position player's client is currently watching and
     * the player's position in its entity tracker.
     */
    public void updatePosition(ServerPlayerEntity player) {
        if (!player.isRemoved()) {
            this.threadedAnvilChunkStorage.updatePosition(player);
        }
    }

    public void unloadEntity(Entity entity) {
        this.threadedAnvilChunkStorage.unloadEntity(entity);
    }

    public void loadEntity(Entity entity) {
        this.threadedAnvilChunkStorage.loadEntity(entity);
    }

    public void sendToNearbyPlayers(Entity entity, Packet<?> packet) {
        this.threadedAnvilChunkStorage.sendToNearbyPlayers(entity, packet);
    }

    public void sendToOtherNearbyPlayers(Entity entity, Packet<?> packet) {
        this.threadedAnvilChunkStorage.sendToOtherNearbyPlayers(entity, packet);
    }

    public void applyViewDistance(int watchDistance) {
        this.threadedAnvilChunkStorage.setViewDistance(watchDistance);
    }

    public void applySimulationDistance(int simulationDistance) {
        this.ticketManager.setSimulationDistance(simulationDistance);
    }

    @Override
    public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
        this.spawnMonsters = spawnMonsters;
        this.spawnAnimals = spawnAnimals;
    }

    public String getChunkLoadingDebugInfo(ChunkPos pos) {
        return this.threadedAnvilChunkStorage.getChunkLoadingDebugInfo(pos);
    }

    public PersistentStateManager getPersistentStateManager() {
        return this.persistentStateManager;
    }

    public PointOfInterestStorage getPointOfInterestStorage() {
        return this.threadedAnvilChunkStorage.getPointOfInterestStorage();
    }

    public NbtScannable getChunkIoWorker() {
        return this.threadedAnvilChunkStorage.getWorker();
    }

    @Nullable
    @Debug
    public SpawnHelper.Info getSpawnInfo() {
        return this.spawnInfo;
    }

    public void removePersistentTickets() {
        this.ticketManager.removePersistentTickets();
    }

    @Override
    public /* synthetic */ LightingProvider getLightingProvider() {
        return this.getLightingProvider();
    }

    @Override
    public /* synthetic */ BlockView getWorld() {
        return this.getWorld();
    }

    final class MainThreadExecutor
    extends ThreadExecutor<Runnable> {
        MainThreadExecutor(World world) {
            super("Chunk source main thread executor for " + world.getRegistryKey().getValue());
        }

        @Override
        protected Runnable createTask(Runnable runnable) {
            return runnable;
        }

        @Override
        protected boolean canExecute(Runnable task) {
            return true;
        }

        @Override
        protected boolean shouldExecuteAsync() {
            return true;
        }

        @Override
        protected Thread getThread() {
            return ServerChunkManager.this.serverThread;
        }

        @Override
        protected void executeTask(Runnable task) {
            ServerChunkManager.this.world.getProfiler().visit("runTask");
            super.executeTask(task);
        }

        @Override
        protected boolean runTask() {
            if (ServerChunkManager.this.tick()) {
                return true;
            }
            ServerChunkManager.this.lightingProvider.tick();
            return super.runTask();
        }
    }

    record ChunkWithHolder(WorldChunk chunk, ChunkHolder holder) {
    }
}

