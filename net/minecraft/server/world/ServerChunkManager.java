/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.LightType;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.Nullable;

public class ServerChunkManager
extends ChunkManager {
    private static final int CHUNKS_ELIGIBLE_FOR_SPAWNING = (int)Math.pow(17.0, 2.0);
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
    private final ChunkTicketManager ticketManager;
    private final ChunkGenerator<?> chunkGenerator;
    private final ServerWorld world;
    private final Thread serverThread;
    private final ServerLightingProvider lightProvider;
    private final MainThreadExecutor mainThreadExecutor;
    public final ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
    private final PersistentStateManager persistentStateManager;
    private long lastMobSpawningTime;
    private boolean spawnMonsters = true;
    private boolean spawnAnimals = true;
    private final long[] chunkPosCache = new long[4];
    private final ChunkStatus[] chunkStatusCache = new ChunkStatus[4];
    private final Chunk[] chunkCache = new Chunk[4];

    public ServerChunkManager(ServerWorld serverWorld, File file, DataFixer dataFixer, StructureManager structureManager, Executor workerExecutor, ChunkGenerator<?> chunkGenerator, int i, WorldGenerationProgressListener worldGenerationProgressListener, Supplier<PersistentStateManager> supplier) {
        this.world = serverWorld;
        this.mainThreadExecutor = new MainThreadExecutor(serverWorld);
        this.chunkGenerator = chunkGenerator;
        this.serverThread = Thread.currentThread();
        File file2 = serverWorld.getDimension().getType().getSaveDirectory(file);
        File file3 = new File(file2, "data");
        file3.mkdirs();
        this.persistentStateManager = new PersistentStateManager(file3, dataFixer);
        this.threadedAnvilChunkStorage = new ThreadedAnvilChunkStorage(serverWorld, file, dataFixer, structureManager, workerExecutor, this.mainThreadExecutor, this, this.getChunkGenerator(), worldGenerationProgressListener, supplier, i);
        this.lightProvider = this.threadedAnvilChunkStorage.getLightProvider();
        this.ticketManager = this.threadedAnvilChunkStorage.getTicketManager();
        this.initChunkCaches();
    }

    @Override
    public ServerLightingProvider getLightingProvider() {
        return this.lightProvider;
    }

    @Nullable
    private ChunkHolder getChunkHolder(long pos) {
        return this.threadedAnvilChunkStorage.getChunkHolder(pos);
    }

    public int getTotalChunksLoadedCount() {
        return this.threadedAnvilChunkStorage.getTotalChunksLoadedCount();
    }

    private void method_21738(long l, Chunk chunk, ChunkStatus chunkStatus) {
        for (int i = 3; i > 0; --i) {
            this.chunkPosCache[i] = this.chunkPosCache[i - 1];
            this.chunkStatusCache[i] = this.chunkStatusCache[i - 1];
            this.chunkCache[i] = this.chunkCache[i - 1];
        }
        this.chunkPosCache[0] = l;
        this.chunkStatusCache[0] = chunkStatus;
        this.chunkCache[0] = chunk;
    }

    @Override
    @Nullable
    public Chunk getChunk(int x, int z, ChunkStatus leastStatus, boolean create) {
        Chunk chunk2;
        if (Thread.currentThread() != this.serverThread) {
            return CompletableFuture.supplyAsync(() -> this.getChunk(x, z, leastStatus, create), this.mainThreadExecutor).join();
        }
        long l = ChunkPos.toLong(x, z);
        for (int i = 0; i < 4; ++i) {
            if (l != this.chunkPosCache[i] || leastStatus != this.chunkStatusCache[i] || (chunk2 = this.chunkCache[i]) == null && create) continue;
            return chunk2;
        }
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getChunkFuture(x, z, leastStatus, create);
        this.mainThreadExecutor.runTasks(completableFuture::isDone);
        chunk2 = completableFuture.join().map(chunk -> chunk, unloaded -> {
            if (create) {
                throw Util.throwOrPause(new IllegalStateException("Chunk not there when requested: " + unloaded));
            }
            return null;
        });
        this.method_21738(l, chunk2, leastStatus);
        return chunk2;
    }

    @Override
    @Nullable
    public WorldChunk getWorldChunk(int chunkX, int chunkZ) {
        if (Thread.currentThread() != this.serverThread) {
            return null;
        }
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
        Either either = chunkHolder.method_21737(ChunkStatus.FULL).getNow(null);
        if (either == null) {
            return null;
        }
        Chunk chunk2 = either.left().orElse(null);
        if (chunk2 != null) {
            this.method_21738(l, chunk2, ChunkStatus.FULL);
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

    @Environment(value=EnvType.CLIENT)
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
        int i = 33 + ChunkStatus.getTargetGenerationRadius(leastStatus);
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
        return chunkHolder.createFuture(leastStatus, this.threadedAnvilChunkStorage);
    }

    private boolean isMissingForLevel(@Nullable ChunkHolder holder, int maxLevel) {
        return holder == null || holder.getLevel() > maxLevel;
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        int i;
        ChunkHolder chunkHolder = this.getChunkHolder(new ChunkPos(x, z).toLong());
        return !this.isMissingForLevel(chunkHolder, i = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL));
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
            if ((optional = chunkHolder.getFuture(chunkStatus = CHUNK_STATUSES.get(i)).getNow(ChunkHolder.UNLOADED_CHUNK).left()).isPresent()) {
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

    private boolean tick() {
        boolean bl = this.ticketManager.tick(this.threadedAnvilChunkStorage);
        boolean bl2 = this.threadedAnvilChunkStorage.updateHolderMap();
        if (bl || bl2) {
            this.initChunkCaches();
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldTickEntity(Entity entity) {
        long l = ChunkPos.toLong(MathHelper.floor(entity.getX()) >> 4, MathHelper.floor(entity.getZ()) >> 4);
        return this.method_20585(l, ChunkHolder::getEntityTickingFuture);
    }

    @Override
    public boolean shouldTickChunk(ChunkPos pos) {
        return this.method_20585(pos.toLong(), ChunkHolder::getEntityTickingFuture);
    }

    @Override
    public boolean shouldTickBlock(BlockPos pos) {
        long l = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
        return this.method_20585(l, ChunkHolder::getTickingFuture);
    }

    public boolean method_20727(Entity entity) {
        long l = ChunkPos.toLong(MathHelper.floor(entity.getX()) >> 4, MathHelper.floor(entity.getZ()) >> 4);
        return this.method_20585(l, ChunkHolder::method_20725);
    }

    private boolean method_20585(long pos, Function<ChunkHolder, CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>>> function) {
        ChunkHolder chunkHolder = this.getChunkHolder(pos);
        if (chunkHolder == null) {
            return false;
        }
        Either<WorldChunk, ChunkHolder.Unloaded> either = function.apply(chunkHolder).getNow(ChunkHolder.UNLOADED_WORLD_CHUNK);
        return either.left().isPresent();
    }

    public void save(boolean flush) {
        this.tick();
        this.threadedAnvilChunkStorage.save(flush);
    }

    @Override
    public void close() throws IOException {
        this.save(true);
        this.lightProvider.close();
        this.threadedAnvilChunkStorage.close();
    }

    @Override
    public void tick(BooleanSupplier booleanSupplier) {
        this.world.getProfiler().push("purge");
        this.ticketManager.purge();
        this.tick();
        this.world.getProfiler().swap("chunks");
        this.tickChunks();
        this.world.getProfiler().swap("unload");
        this.threadedAnvilChunkStorage.tick(booleanSupplier);
        this.world.getProfiler().pop();
        this.initChunkCaches();
    }

    private void tickChunks() {
        long l = this.world.getTime();
        long m = l - this.lastMobSpawningTime;
        this.lastMobSpawningTime = l;
        LevelProperties levelProperties = this.world.getLevelProperties();
        boolean bl = levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG_ALL_BLOCK_STATES;
        boolean bl2 = this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
        if (!bl) {
            this.world.getProfiler().push("pollingChunks");
            int i = this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
            BlockPos blockPos = this.world.getSpawnPos();
            boolean bl3 = levelProperties.getTime() % 400L == 0L;
            this.world.getProfiler().push("naturalSpawnCount");
            int j = this.ticketManager.getLevelCount();
            EntityCategory[] entityCategorys = EntityCategory.values();
            Object2IntMap<EntityCategory> object2IntMap = this.world.getMobCountsByCategory();
            this.world.getProfiler().pop();
            this.threadedAnvilChunkStorage.entryIterator().forEach(chunkHolder -> {
                Optional<WorldChunk> optional = chunkHolder.getEntityTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
                if (!optional.isPresent()) {
                    return;
                }
                WorldChunk worldChunk = optional.get();
                this.world.getProfiler().push("broadcast");
                chunkHolder.flushUpdates(worldChunk);
                this.world.getProfiler().pop();
                ChunkPos chunkPos = chunkHolder.getPos();
                if (this.threadedAnvilChunkStorage.isTooFarFromPlayersToSpawnMobs(chunkPos)) {
                    return;
                }
                worldChunk.setInhabitedTime(worldChunk.getInhabitedTime() + m);
                if (bl2 && (this.spawnMonsters || this.spawnAnimals) && this.world.getWorldBorder().contains(worldChunk.getPos())) {
                    this.world.getProfiler().push("spawner");
                    for (EntityCategory entityCategory : entityCategorys) {
                        if (entityCategory == EntityCategory.MISC || entityCategory.isPeaceful() && !this.spawnAnimals || !entityCategory.isPeaceful() && !this.spawnMonsters || entityCategory.isAnimal() && !bl3) continue;
                        int k = entityCategory.getSpawnCap() * j / CHUNKS_ELIGIBLE_FOR_SPAWNING;
                        if (object2IntMap.getInt((Object)entityCategory) > k) continue;
                        SpawnHelper.spawnEntitiesInChunk(entityCategory, this.world, worldChunk, blockPos);
                    }
                    this.world.getProfiler().pop();
                }
                this.world.tickChunk(worldChunk, i);
            });
            this.world.getProfiler().push("customSpawners");
            if (bl2) {
                this.chunkGenerator.spawnEntities(this.world, this.spawnMonsters, this.spawnAnimals);
            }
            this.world.getProfiler().pop();
            this.world.getProfiler().pop();
        }
        this.threadedAnvilChunkStorage.tickPlayerMovement();
    }

    @Override
    public String getDebugString() {
        return "ServerChunkCache: " + this.getLoadedChunkCount();
    }

    @VisibleForTesting
    public int method_21694() {
        return this.mainThreadExecutor.getTaskCount();
    }

    public ChunkGenerator<?> getChunkGenerator() {
        return this.chunkGenerator;
    }

    public int getLoadedChunkCount() {
        return this.threadedAnvilChunkStorage.getLoadedChunkCount();
    }

    public void markForUpdate(BlockPos pos) {
        int j;
        int i = pos.getX() >> 4;
        ChunkHolder chunkHolder = this.getChunkHolder(ChunkPos.toLong(i, j = pos.getZ() >> 4));
        if (chunkHolder != null) {
            chunkHolder.markForBlockUpdate(pos.getX() & 0xF, pos.getY(), pos.getZ() & 0xF);
        }
    }

    @Override
    public void onLightUpdate(LightType type, ChunkSectionPos chunkSectionPos) {
        this.mainThreadExecutor.execute(() -> {
            ChunkHolder chunkHolder = this.getChunkHolder(chunkSectionPos.toChunkPos().toLong());
            if (chunkHolder != null) {
                chunkHolder.markForLightUpdate(type, chunkSectionPos.getSectionY());
            }
        });
    }

    public <T> void addTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
        this.ticketManager.addTicket(chunkTicketType, chunkPos, i, object);
    }

    public <T> void removeTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
        this.ticketManager.removeTicket(chunkTicketType, chunkPos, i, object);
    }

    @Override
    public void setChunkForced(ChunkPos pos, boolean forced) {
        this.ticketManager.setChunkForced(pos, forced);
    }

    public void updateCameraPosition(ServerPlayerEntity player) {
        this.threadedAnvilChunkStorage.updateCameraPosition(player);
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

    @Override
    public void setMobSpawnOptions(boolean spawnMonsters, boolean spawnAnimals) {
        this.spawnMonsters = spawnMonsters;
        this.spawnAnimals = spawnAnimals;
    }

    @Environment(value=EnvType.CLIENT)
    public String method_23273(ChunkPos chunkPos) {
        return this.threadedAnvilChunkStorage.method_23272(chunkPos);
    }

    public PersistentStateManager getPersistentStateManager() {
        return this.persistentStateManager;
    }

    public PointOfInterestStorage getPointOfInterestStorage() {
        return this.threadedAnvilChunkStorage.getPointOfInterestStorage();
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
        private MainThreadExecutor(World world) {
            super("Chunk source main thread executor for " + Registry.DIMENSION.getId(world.getDimension().getType()));
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
        protected boolean runTask() {
            if (ServerChunkManager.this.tick()) {
                return true;
            }
            ServerChunkManager.this.lightProvider.tick();
            return super.runTask();
        }
    }
}

