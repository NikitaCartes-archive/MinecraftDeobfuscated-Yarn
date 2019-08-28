/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.client.network.packet.ChunkDataS2CPacket;
import net.minecraft.client.network.packet.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.client.network.packet.EntityAttachS2CPacket;
import net.minecraft.client.network.packet.EntityPassengersSetS2CPacket;
import net.minecraft.client.network.packet.LightUpdateS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.LevelPrioritizedQueue;
import net.minecraft.server.world.PlayerChunkWatchingManager;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Actor;
import net.minecraft.util.CsvWriter;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SessionLockException;
import net.minecraft.world.VersionedChunkStorage;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ThreadedAnvilChunkStorage
extends VersionedChunkStorage
implements ChunkHolder.PlayersWatchingChunkProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int MAX_LEVEL = 33 + ChunkStatus.getMaxTargetGenerationRadius();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> currentChunkHolders = new Long2ObjectLinkedOpenHashMap();
    private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> chunkHolders = this.currentChunkHolders.clone();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> field_18807 = new Long2ObjectLinkedOpenHashMap();
    private final LongSet field_18307 = new LongOpenHashSet();
    private final ServerWorld world;
    private final ServerLightingProvider serverLightingProvider;
    private final ThreadExecutor<Runnable> mainThreadExecutor;
    private final ChunkGenerator<?> chunkGenerator;
    private final Supplier<PersistentStateManager> persistentStateManagerFactory;
    private final PointOfInterestStorage pointOfInterestStorage;
    private final LongSet unloadedChunks = new LongOpenHashSet();
    private boolean chunkHolderListDirty;
    private final ChunkTaskPrioritySystem chunkTaskPrioritySystem;
    private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> worldgenActor;
    private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> mainActor;
    private final WorldGenerationProgressListener worldGenerationProgressListener;
    private final TicketManager ticketManager;
    private final AtomicInteger totalChunksLoadedCount = new AtomicInteger();
    private final StructureManager structureManager;
    private final File saveDir;
    private final PlayerChunkWatchingManager playerChunkWatchingManager = new PlayerChunkWatchingManager();
    private final Int2ObjectMap<EntityTracker> entityTrackers = new Int2ObjectOpenHashMap<EntityTracker>();
    private final Queue<Runnable> field_19343 = Queues.newConcurrentLinkedQueue();
    private int watchDistance;

    public ThreadedAnvilChunkStorage(ServerWorld serverWorld, File file, DataFixer dataFixer, StructureManager structureManager, Executor executor, ThreadExecutor<Runnable> threadExecutor, ChunkProvider chunkProvider, ChunkGenerator<?> chunkGenerator, WorldGenerationProgressListener worldGenerationProgressListener, Supplier<PersistentStateManager> supplier, int i) {
        super(new File(serverWorld.getDimension().getType().getFile(file), "region"), dataFixer);
        this.structureManager = structureManager;
        this.saveDir = serverWorld.getDimension().getType().getFile(file);
        this.world = serverWorld;
        this.chunkGenerator = chunkGenerator;
        this.mainThreadExecutor = threadExecutor;
        MailboxProcessor<Runnable> mailboxProcessor = MailboxProcessor.create(executor, "worldgen");
        Actor<Runnable> actor = Actor.createConsumerActor("main", threadExecutor::method_18858);
        this.worldGenerationProgressListener = worldGenerationProgressListener;
        MailboxProcessor<Runnable> mailboxProcessor2 = MailboxProcessor.create(executor, "light");
        this.chunkTaskPrioritySystem = new ChunkTaskPrioritySystem(ImmutableList.of(mailboxProcessor, actor, mailboxProcessor2), executor, Integer.MAX_VALUE);
        this.worldgenActor = this.chunkTaskPrioritySystem.createExecutingActor(mailboxProcessor, false);
        this.mainActor = this.chunkTaskPrioritySystem.createExecutingActor(actor, false);
        this.serverLightingProvider = new ServerLightingProvider(chunkProvider, this, this.world.getDimension().hasSkyLight(), mailboxProcessor2, this.chunkTaskPrioritySystem.createExecutingActor(mailboxProcessor2, false));
        this.ticketManager = new TicketManager(executor, threadExecutor);
        this.persistentStateManagerFactory = supplier;
        this.pointOfInterestStorage = new PointOfInterestStorage(new File(this.saveDir, "poi"), dataFixer);
        this.setViewDistance(i);
    }

    private static double getSquaredDistance(ChunkPos chunkPos, Entity entity) {
        double d = chunkPos.x * 16 + 8;
        double e = chunkPos.z * 16 + 8;
        double f = d - entity.x;
        double g = e - entity.z;
        return f * f + g * g;
    }

    private static int getChebyshevDistance(ChunkPos chunkPos, ServerPlayerEntity serverPlayerEntity, boolean bl) {
        int j;
        int i;
        if (bl) {
            ChunkSectionPos chunkSectionPos = serverPlayerEntity.getCameraPosition();
            i = chunkSectionPos.getChunkX();
            j = chunkSectionPos.getChunkZ();
        } else {
            i = MathHelper.floor(serverPlayerEntity.x / 16.0);
            j = MathHelper.floor(serverPlayerEntity.z / 16.0);
        }
        return ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, i, j);
    }

    private static int getChebyshevDistance(ChunkPos chunkPos, int i, int j) {
        int k = chunkPos.x - i;
        int l = chunkPos.z - j;
        return Math.max(Math.abs(k), Math.abs(l));
    }

    protected ServerLightingProvider getLightProvider() {
        return this.serverLightingProvider;
    }

    @Nullable
    protected ChunkHolder getCurrentChunkHolder(long l) {
        return this.currentChunkHolders.get(l);
    }

    @Nullable
    protected ChunkHolder getChunkHolder(long l) {
        return this.chunkHolders.get(l);
    }

    protected IntSupplier getCompletedLevelSupplier(long l) {
        return () -> {
            ChunkHolder chunkHolder = this.getChunkHolder(l);
            if (chunkHolder == null) {
                return LevelPrioritizedQueue.LEVEL_COUNT - 1;
            }
            return Math.min(chunkHolder.getCompletedLevel(), LevelPrioritizedQueue.LEVEL_COUNT - 1);
        };
    }

    @Environment(value=EnvType.CLIENT)
    public String getDebugString(ChunkPos chunkPos) {
        ChunkHolder chunkHolder = this.getChunkHolder(chunkPos.toLong());
        if (chunkHolder == null) {
            return "null";
        }
        String string = chunkHolder.getLevel() + "\n";
        ChunkStatus chunkStatus = chunkHolder.getCompletedStatus();
        Chunk chunk = chunkHolder.getCompletedChunk();
        if (chunkStatus != null) {
            string = string + "St: \u00a7" + chunkStatus.getIndex() + chunkStatus + '\u00a7' + "r\n";
        }
        if (chunk != null) {
            string = string + "Ch: \u00a7" + chunk.getStatus().getIndex() + chunk.getStatus() + '\u00a7' + "r\n";
        }
        ChunkHolder.LevelType levelType = chunkHolder.getLevelType();
        string = string + "\u00a7" + levelType.ordinal() + (Object)((Object)levelType);
        return string + '\u00a7' + "r";
    }

    private CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> createChunkRegionFuture(ChunkPos chunkPos, final int i, IntFunction<ChunkStatus> intFunction) {
        ArrayList<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> list2 = Lists.newArrayList();
        final int j = chunkPos.x;
        final int k = chunkPos.z;
        for (int l = -i; l <= i; ++l) {
            for (int m = -i; m <= i; ++m) {
                int n = Math.max(Math.abs(m), Math.abs(l));
                final ChunkPos chunkPos2 = new ChunkPos(j + m, k + l);
                long o = chunkPos2.toLong();
                ChunkHolder chunkHolder = this.getCurrentChunkHolder(o);
                if (chunkHolder == null) {
                    return CompletableFuture.completedFuture(Either.right(new ChunkHolder.Unloaded(){

                        public String toString() {
                            return "Unloaded " + chunkPos2.toString();
                        }
                    }));
                }
                ChunkStatus chunkStatus = intFunction.apply(n);
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.createFuture(chunkStatus, this);
                list2.add(completableFuture);
            }
        }
        CompletableFuture completableFuture2 = SystemUtil.thenCombine(list2);
        return completableFuture2.thenApply(list -> {
            ArrayList list2 = Lists.newArrayList();
            int l = 0;
            for (final Either either : list) {
                Optional optional = either.left();
                if (!optional.isPresent()) {
                    final int m = l;
                    return Either.right(new ChunkHolder.Unloaded(){

                        public String toString() {
                            return "Unloaded " + new ChunkPos(j + m % (i * 2 + 1), k + m / (i * 2 + 1)) + " " + ((ChunkHolder.Unloaded)either.right().get()).toString();
                        }
                    });
                }
                list2.add(optional.get());
                ++l;
            }
            return Either.left(list2);
        });
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> createEntityTickingChunkFuture(ChunkPos chunkPos) {
        return this.createChunkRegionFuture(chunkPos, 2, i -> ChunkStatus.FULL).thenApplyAsync(either -> either.mapLeft(list -> (WorldChunk)list.get(list.size() / 2)), (Executor)this.mainThreadExecutor);
    }

    @Nullable
    private ChunkHolder setLevel(long l, int i, @Nullable ChunkHolder chunkHolder, int j) {
        if (j > MAX_LEVEL && i > MAX_LEVEL) {
            return chunkHolder;
        }
        if (chunkHolder != null) {
            chunkHolder.setLevel(i);
        }
        if (chunkHolder != null) {
            if (i > MAX_LEVEL) {
                this.unloadedChunks.add(l);
            } else {
                this.unloadedChunks.remove(l);
            }
        }
        if (i <= MAX_LEVEL && chunkHolder == null) {
            chunkHolder = this.field_18807.remove(l);
            if (chunkHolder != null) {
                chunkHolder.setLevel(i);
            } else {
                chunkHolder = new ChunkHolder(new ChunkPos(l), i, this.serverLightingProvider, this.chunkTaskPrioritySystem, this);
            }
            this.currentChunkHolders.put(l, chunkHolder);
            this.chunkHolderListDirty = true;
        }
        return chunkHolder;
    }

    @Override
    public void close() throws IOException {
        this.chunkTaskPrioritySystem.close();
        this.pointOfInterestStorage.close();
        super.close();
    }

    protected void save(boolean bl) {
        if (bl) {
            List list = this.chunkHolders.values().stream().filter(ChunkHolder::method_20384).peek(ChunkHolder::method_20385).collect(Collectors.toList());
            MutableBoolean mutableBoolean = new MutableBoolean();
            do {
                mutableBoolean.setFalse();
                list.stream().map(chunkHolder -> {
                    CompletableFuture<Chunk> completableFuture;
                    do {
                        completableFuture = chunkHolder.getFuture();
                        this.mainThreadExecutor.waitFor(completableFuture::isDone);
                    } while (completableFuture != chunkHolder.getFuture());
                    return completableFuture.join();
                }).filter(chunk -> chunk instanceof ReadOnlyChunk || chunk instanceof WorldChunk).filter(this::save).forEach(chunk -> mutableBoolean.setTrue());
            } while (mutableBoolean.isTrue());
            this.method_20605(() -> true);
            LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)this.saveDir.getName());
        } else {
            this.chunkHolders.values().stream().filter(ChunkHolder::method_20384).forEach(chunkHolder -> {
                Chunk chunk = chunkHolder.getFuture().getNow(null);
                if (chunk instanceof ReadOnlyChunk || chunk instanceof WorldChunk) {
                    this.save(chunk);
                    chunkHolder.method_20385();
                }
            });
        }
    }

    protected void tick(BooleanSupplier booleanSupplier) {
        Profiler profiler = this.world.getProfiler();
        profiler.push("poi");
        this.pointOfInterestStorage.tick(booleanSupplier);
        profiler.swap("chunk_unload");
        if (!this.world.isSavingDisabled()) {
            this.method_20605(booleanSupplier);
        }
        profiler.pop();
    }

    private void method_20605(BooleanSupplier booleanSupplier) {
        Runnable runnable;
        LongIterator longIterator = this.unloadedChunks.iterator();
        int i = 0;
        while (longIterator.hasNext() && (booleanSupplier.getAsBoolean() || i < 200 || this.unloadedChunks.size() > 2000)) {
            long l = longIterator.nextLong();
            ChunkHolder chunkHolder = this.currentChunkHolders.remove(l);
            if (chunkHolder != null) {
                this.field_18807.put(l, chunkHolder);
                this.chunkHolderListDirty = true;
                ++i;
                this.method_20458(l, chunkHolder);
            }
            longIterator.remove();
        }
        while ((booleanSupplier.getAsBoolean() || this.field_19343.size() > 2000) && (runnable = this.field_19343.poll()) != null) {
            runnable.run();
        }
    }

    private void method_20458(long l, ChunkHolder chunkHolder) {
        CompletableFuture<Chunk> completableFuture = chunkHolder.getFuture();
        ((CompletableFuture)completableFuture.thenAcceptAsync(chunk -> {
            CompletableFuture<Chunk> completableFuture2 = chunkHolder.getFuture();
            if (completableFuture2 != completableFuture) {
                this.method_20458(l, chunkHolder);
                return;
            }
            if (this.field_18807.remove(l, (Object)chunkHolder) && chunk != null) {
                if (chunk instanceof WorldChunk) {
                    ((WorldChunk)chunk).setLoadedToWorld(false);
                }
                this.save((Chunk)chunk);
                if (this.field_18307.remove(l) && chunk instanceof WorldChunk) {
                    WorldChunk worldChunk = (WorldChunk)chunk;
                    this.world.unloadEntities(worldChunk);
                }
                this.serverLightingProvider.method_20386(chunk.getPos());
                this.serverLightingProvider.tick();
                this.worldGenerationProgressListener.setChunkStatus(chunk.getPos(), null);
            }
        }, this.field_19343::add)).whenComplete((void_, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Failed to save chunk " + chunkHolder.getPos(), (Throwable)throwable);
            }
        });
    }

    protected boolean updateHolderMap() {
        if (!this.chunkHolderListDirty) {
            return false;
        }
        this.chunkHolders = this.currentChunkHolders.clone();
        this.chunkHolderListDirty = false;
        return true;
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> createChunkFuture(ChunkHolder chunkHolder, ChunkStatus chunkStatus) {
        ChunkPos chunkPos = chunkHolder.getPos();
        if (chunkStatus == ChunkStatus.EMPTY) {
            return this.method_20619(chunkPos);
        }
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.createFuture(chunkStatus.getPrevious(), this);
        return completableFuture.thenComposeAsync(either -> {
            Chunk chunk2;
            Optional optional = either.left();
            if (!optional.isPresent()) {
                return CompletableFuture.completedFuture(either);
            }
            if (chunkStatus == ChunkStatus.LIGHT) {
                this.ticketManager.addTicketWithLevel(ChunkTicketType.LIGHT, chunkPos, 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FEATURES), chunkPos);
            }
            if ((chunk2 = (Chunk)optional.get()).getStatus().isAtLeast(chunkStatus)) {
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkStatus == ChunkStatus.LIGHT ? this.method_20617(chunkHolder, chunkStatus) : chunkStatus.method_20612(this.world, this.structureManager, this.serverLightingProvider, chunk -> this.convertToFullChunk(chunkHolder), chunk2);
                this.worldGenerationProgressListener.setChunkStatus(chunkPos, chunkStatus);
                return completableFuture;
            }
            return this.method_20617(chunkHolder, chunkStatus);
        }, (Executor)this.mainThreadExecutor);
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> method_20619(ChunkPos chunkPos) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CompoundTag compoundTag = this.getUpdatedChunkTag(chunkPos);
                if (compoundTag != null) {
                    boolean bl;
                    boolean bl2 = bl = compoundTag.containsKey("Level", 10) && compoundTag.getCompound("Level").containsKey("Status", 8);
                    if (bl) {
                        ProtoChunk chunk = ChunkSerializer.deserialize(this.world, this.structureManager, this.pointOfInterestStorage, chunkPos, compoundTag);
                        chunk.setLastSaveTime(this.world.getTime());
                        return Either.left(chunk);
                    }
                    LOGGER.error("Chunk file at {} is missing level data, skipping", (Object)chunkPos);
                }
            } catch (CrashException crashException) {
                Throwable throwable = crashException.getCause();
                if (throwable instanceof IOException) {
                    LOGGER.error("Couldn't load chunk {}", (Object)chunkPos, (Object)throwable);
                }
                throw crashException;
            } catch (Exception exception) {
                LOGGER.error("Couldn't load chunk {}", (Object)chunkPos, (Object)exception);
            }
            return Either.left(new ProtoChunk(chunkPos, UpgradeData.NO_UPGRADE_DATA));
        }, this.mainThreadExecutor);
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> method_20617(ChunkHolder chunkHolder, ChunkStatus chunkStatus) {
        ChunkPos chunkPos = chunkHolder.getPos();
        CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.createChunkRegionFuture(chunkPos, chunkStatus.getTaskMargin(), i -> this.getRequiredStatusForGeneration(chunkStatus, i));
        return completableFuture.thenComposeAsync(either -> either.map(list -> {
            try {
                CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkStatus.runTask(this.world, this.chunkGenerator, this.structureManager, this.serverLightingProvider, chunk -> this.convertToFullChunk(chunkHolder), (List<Chunk>)list);
                this.worldGenerationProgressListener.setChunkStatus(chunkPos, chunkStatus);
                return completableFuture;
            } catch (Exception exception) {
                CrashReport crashReport = CrashReport.create(exception, "Exception generating new chunk");
                CrashReportSection crashReportSection = crashReport.addElement("Chunk to be generated");
                crashReportSection.add("Location", String.format("%d,%d", chunkPos.x, chunkPos.z));
                crashReportSection.add("Position hash", ChunkPos.toLong(chunkPos.x, chunkPos.z));
                crashReportSection.add("Generator", this.chunkGenerator);
                throw new CrashException(crashReport);
            }
        }, unloaded -> {
            this.method_20441(chunkPos);
            return CompletableFuture.completedFuture(Either.right(unloaded));
        }), runnable -> this.worldgenActor.send(ChunkTaskPrioritySystem.createExecutorMessage(chunkHolder, runnable)));
    }

    protected void method_20441(ChunkPos chunkPos) {
        this.mainThreadExecutor.method_18858(SystemUtil.debugRunnable(() -> this.ticketManager.removeTicketWithLevel(ChunkTicketType.LIGHT, chunkPos, 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FEATURES), chunkPos), () -> "release light ticket " + chunkPos));
    }

    private ChunkStatus getRequiredStatusForGeneration(ChunkStatus chunkStatus, int i) {
        ChunkStatus chunkStatus2 = i == 0 ? chunkStatus.getPrevious() : ChunkStatus.getTargetGenerationStatus(ChunkStatus.getTargetGenerationRadius(chunkStatus) + i);
        return chunkStatus2;
    }

    private CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> convertToFullChunk(ChunkHolder chunkHolder) {
        CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getFuture(ChunkStatus.FULL.getPrevious());
        return completableFuture.thenApplyAsync(either -> {
            ChunkStatus chunkStatus = ChunkHolder.getTargetGenerationStatus(chunkHolder.getLevel());
            if (!chunkStatus.isAtLeast(ChunkStatus.FULL)) {
                return ChunkHolder.UNLOADED_CHUNK;
            }
            return either.mapLeft(chunk -> {
                WorldChunk worldChunk;
                ChunkPos chunkPos = chunkHolder.getPos();
                if (chunk instanceof ReadOnlyChunk) {
                    worldChunk = ((ReadOnlyChunk)chunk).getWrappedChunk();
                } else {
                    worldChunk = new WorldChunk(this.world, (ProtoChunk)chunk);
                    chunkHolder.method_20456(new ReadOnlyChunk(worldChunk));
                }
                worldChunk.setLevelTypeProvider(() -> ChunkHolder.getLevelType(chunkHolder.getLevel()));
                worldChunk.loadToWorld();
                if (this.field_18307.add(chunkPos.toLong())) {
                    worldChunk.setLoadedToWorld(true);
                    this.world.addBlockEntities(worldChunk.getBlockEntities().values());
                    ArrayList<Entity> list = null;
                    for (TypeFilterableList<Entity> typeFilterableList : worldChunk.getEntitySectionArray()) {
                        for (Entity entity : typeFilterableList) {
                            if (entity instanceof PlayerEntity || this.world.loadEntity(entity)) continue;
                            if (list == null) {
                                list = Lists.newArrayList(entity);
                                continue;
                            }
                            list.add(entity);
                        }
                    }
                    if (list != null) {
                        list.forEach(worldChunk::remove);
                    }
                }
                return worldChunk;
            });
        }, runnable -> this.mainActor.send(ChunkTaskPrioritySystem.createRunnableMessage(runnable, chunkHolder.getPos().toLong(), chunkHolder::getLevel)));
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> createTickingFuture(ChunkHolder chunkHolder) {
        ChunkPos chunkPos = chunkHolder.getPos();
        CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>> completableFuture = this.createChunkRegionFuture(chunkPos, 1, i -> ChunkStatus.FULL);
        CompletionStage completableFuture2 = completableFuture.thenApplyAsync(either -> either.flatMap(list -> {
            WorldChunk worldChunk = (WorldChunk)list.get(list.size() / 2);
            worldChunk.runPostProcessing();
            return Either.left(worldChunk);
        }), runnable -> this.mainActor.send(ChunkTaskPrioritySystem.createExecutorMessage(chunkHolder, runnable)));
        ((CompletableFuture)completableFuture2).thenAcceptAsync(either -> either.mapLeft(worldChunk -> {
            this.totalChunksLoadedCount.getAndIncrement();
            Packet[] packets = new Packet[2];
            this.getPlayersWatchingChunk(chunkPos, false).forEach(serverPlayerEntity -> this.sendChunkDataPackets((ServerPlayerEntity)serverPlayerEntity, packets, (WorldChunk)worldChunk));
            return Either.left(worldChunk);
        }), runnable -> this.mainActor.send(ChunkTaskPrioritySystem.createExecutorMessage(chunkHolder, runnable)));
        return completableFuture2;
    }

    public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> createBorderFuture(ChunkHolder chunkHolder) {
        return chunkHolder.createFuture(ChunkStatus.FULL, this).thenApplyAsync(either -> either.mapLeft(chunk -> {
            WorldChunk worldChunk = (WorldChunk)chunk;
            worldChunk.method_20530();
            return worldChunk;
        }), runnable -> this.mainActor.send(ChunkTaskPrioritySystem.createExecutorMessage(chunkHolder, runnable)));
    }

    public int getTotalChunksLoadedCount() {
        return this.totalChunksLoadedCount.get();
    }

    private boolean save(Chunk chunk) {
        this.pointOfInterestStorage.method_20436(chunk.getPos());
        if (!chunk.needsSaving()) {
            return false;
        }
        try {
            this.world.checkSessionLock();
        } catch (SessionLockException sessionLockException) {
            LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)sessionLockException);
            return false;
        }
        chunk.setLastSaveTime(this.world.getTime());
        chunk.setShouldSave(false);
        ChunkPos chunkPos = chunk.getPos();
        try {
            CompoundTag compoundTag;
            ChunkStatus chunkStatus = chunk.getStatus();
            if (chunkStatus.getChunkType() != ChunkStatus.ChunkType.LEVELCHUNK) {
                compoundTag = this.getUpdatedChunkTag(chunkPos);
                if (compoundTag != null && ChunkSerializer.getChunkType(compoundTag) == ChunkStatus.ChunkType.LEVELCHUNK) {
                    return false;
                }
                if (chunkStatus == ChunkStatus.EMPTY && chunk.getStructureStarts().values().stream().noneMatch(StructureStart::hasChildren)) {
                    return false;
                }
            }
            compoundTag = ChunkSerializer.serialize(this.world, chunk);
            this.setTagAt(chunkPos, compoundTag);
            return true;
        } catch (Exception exception) {
            LOGGER.error("Failed to save chunk {},{}", (Object)chunkPos.x, (Object)chunkPos.z, (Object)exception);
            return false;
        }
    }

    protected void setViewDistance(int i) {
        int j = MathHelper.clamp(i + 1, 3, 33);
        if (j != this.watchDistance) {
            int k = this.watchDistance;
            this.watchDistance = j;
            this.ticketManager.setWatchDistance(this.watchDistance);
            for (ChunkHolder chunkHolder : this.currentChunkHolders.values()) {
                ChunkPos chunkPos = chunkHolder.getPos();
                Packet[] packets = new Packet[2];
                this.getPlayersWatchingChunk(chunkPos, false).forEach(serverPlayerEntity -> {
                    int j = ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, serverPlayerEntity, true);
                    boolean bl = j <= k;
                    boolean bl2 = j <= this.watchDistance;
                    this.sendWatchPackets((ServerPlayerEntity)serverPlayerEntity, chunkPos, packets, bl, bl2);
                });
            }
        }
    }

    protected void sendWatchPackets(ServerPlayerEntity serverPlayerEntity, ChunkPos chunkPos, Packet<?>[] packets, boolean bl, boolean bl2) {
        ChunkHolder chunkHolder;
        if (serverPlayerEntity.world != this.world) {
            return;
        }
        if (bl2 && !bl && (chunkHolder = this.getChunkHolder(chunkPos.toLong())) != null) {
            WorldChunk worldChunk = chunkHolder.getWorldChunk();
            if (worldChunk != null) {
                this.sendChunkDataPackets(serverPlayerEntity, packets, worldChunk);
            }
            DebugRendererInfoManager.method_19775(this.world, chunkPos);
        }
        if (!bl2 && bl) {
            serverPlayerEntity.sendUnloadChunkPacket(chunkPos);
        }
    }

    public int getLoadedChunkCount() {
        return this.chunkHolders.size();
    }

    protected TicketManager getTicketManager() {
        return this.ticketManager;
    }

    protected Iterable<ChunkHolder> entryIterator() {
        return Iterables.unmodifiableIterable(this.chunkHolders.values());
    }

    void exportChunks(Writer writer) throws IOException {
        CsvWriter csvWriter = CsvWriter.makeHeader().addColumn("x").addColumn("z").addColumn("level").addColumn("in_memory").addColumn("status").addColumn("full_status").addColumn("accessible_ready").addColumn("ticking_ready").addColumn("entity_ticking_ready").addColumn("ticket").addColumn("spawning").addColumn("entity_count").addColumn("block_entity_count").startBody(writer);
        for (Long2ObjectMap.Entry entry : this.chunkHolders.long2ObjectEntrySet()) {
            ChunkPos chunkPos = new ChunkPos(entry.getLongKey());
            ChunkHolder chunkHolder = (ChunkHolder)entry.getValue();
            Optional<Chunk> optional = Optional.ofNullable(chunkHolder.getCompletedChunk());
            Optional<Object> optional2 = optional.flatMap(chunk -> chunk instanceof WorldChunk ? Optional.of((WorldChunk)chunk) : Optional.empty());
            csvWriter.printRow(chunkPos.x, chunkPos.z, chunkHolder.getLevel(), optional.isPresent(), optional.map(Chunk::getStatus).orElse(null), optional2.map(WorldChunk::getLevelType).orElse(null), ThreadedAnvilChunkStorage.method_21676(chunkHolder.method_20725()), ThreadedAnvilChunkStorage.method_21676(chunkHolder.getTickingFuture()), ThreadedAnvilChunkStorage.method_21676(chunkHolder.getEntityTickingFuture()), this.ticketManager.method_21623(entry.getLongKey()), !this.isTooFarFromPlayersToSpawnMobs(chunkPos), optional2.map(worldChunk -> Stream.of(worldChunk.getEntitySectionArray()).mapToInt(TypeFilterableList::size).sum()).orElse(0), optional2.map(worldChunk -> worldChunk.getBlockEntities().size()).orElse(0));
        }
    }

    private static String method_21676(CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture) {
        try {
            Either either = completableFuture.getNow(null);
            if (either != null) {
                return either.map(worldChunk -> "done", unloaded -> "unloaded");
            }
            return "not completed";
        } catch (CompletionException completionException) {
            return "failed " + completionException.getCause().getMessage();
        } catch (CancellationException cancellationException) {
            return "cancelled";
        }
    }

    @Nullable
    private CompoundTag getUpdatedChunkTag(ChunkPos chunkPos) throws IOException {
        CompoundTag compoundTag = this.getTagAt(chunkPos);
        if (compoundTag == null) {
            return null;
        }
        return this.updateChunkTag(this.world.getDimension().getType(), this.persistentStateManagerFactory, compoundTag);
    }

    boolean isTooFarFromPlayersToSpawnMobs(ChunkPos chunkPos) {
        long l = chunkPos.toLong();
        if (!this.ticketManager.method_20800(l)) {
            return true;
        }
        return this.playerChunkWatchingManager.getPlayersWatchingChunk(l).noneMatch(serverPlayerEntity -> !serverPlayerEntity.isSpectator() && ThreadedAnvilChunkStorage.getSquaredDistance(chunkPos, serverPlayerEntity) < 16384.0);
    }

    private boolean doesNotGenerateChunks(ServerPlayerEntity serverPlayerEntity) {
        return serverPlayerEntity.isSpectator() && !this.world.getGameRules().getBoolean(GameRules.SPECTATORS_GENERATE_CHUNKS);
    }

    void handlePlayerAddedOrRemoved(ServerPlayerEntity serverPlayerEntity, boolean bl) {
        boolean bl2 = this.doesNotGenerateChunks(serverPlayerEntity);
        boolean bl3 = this.playerChunkWatchingManager.method_21715(serverPlayerEntity);
        int i = MathHelper.floor(serverPlayerEntity.x) >> 4;
        int j = MathHelper.floor(serverPlayerEntity.z) >> 4;
        if (bl) {
            this.playerChunkWatchingManager.add(ChunkPos.toLong(i, j), serverPlayerEntity, bl2);
            this.method_20726(serverPlayerEntity);
            if (!bl2) {
                this.ticketManager.handleChunkEnter(ChunkSectionPos.from(serverPlayerEntity), serverPlayerEntity);
            }
        } else {
            ChunkSectionPos chunkSectionPos = serverPlayerEntity.getCameraPosition();
            this.playerChunkWatchingManager.remove(chunkSectionPos.toChunkPos().toLong(), serverPlayerEntity);
            if (!bl3) {
                this.ticketManager.handleChunkLeave(chunkSectionPos, serverPlayerEntity);
            }
        }
        for (int k = i - this.watchDistance; k <= i + this.watchDistance; ++k) {
            for (int l = j - this.watchDistance; l <= j + this.watchDistance; ++l) {
                ChunkPos chunkPos = new ChunkPos(k, l);
                this.sendWatchPackets(serverPlayerEntity, chunkPos, new Packet[2], !bl, bl);
            }
        }
    }

    private ChunkSectionPos method_20726(ServerPlayerEntity serverPlayerEntity) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(serverPlayerEntity);
        serverPlayerEntity.setCameraPosition(chunkSectionPos);
        serverPlayerEntity.networkHandler.sendPacket(new ChunkRenderDistanceCenterS2CPacket(chunkSectionPos.getChunkX(), chunkSectionPos.getChunkZ()));
        return chunkSectionPos;
    }

    public void updateCameraPosition(ServerPlayerEntity serverPlayerEntity) {
        boolean bl3;
        for (EntityTracker entityTracker : this.entityTrackers.values()) {
            if (entityTracker.entity == serverPlayerEntity) {
                entityTracker.updateCameraPosition(this.world.getPlayers());
                continue;
            }
            entityTracker.updateCameraPosition(serverPlayerEntity);
        }
        int i = MathHelper.floor(serverPlayerEntity.x) >> 4;
        int j = MathHelper.floor(serverPlayerEntity.z) >> 4;
        ChunkSectionPos chunkSectionPos = serverPlayerEntity.getCameraPosition();
        ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from(serverPlayerEntity);
        long l = chunkSectionPos.toChunkPos().toLong();
        long m = chunkSectionPos2.toChunkPos().toLong();
        boolean bl = this.playerChunkWatchingManager.isWatchDisabled(serverPlayerEntity);
        boolean bl2 = this.doesNotGenerateChunks(serverPlayerEntity);
        boolean bl4 = bl3 = chunkSectionPos.asLong() != chunkSectionPos2.asLong();
        if (bl3 || bl != bl2) {
            this.method_20726(serverPlayerEntity);
            if (!bl) {
                this.ticketManager.handleChunkLeave(chunkSectionPos, serverPlayerEntity);
            }
            if (!bl2) {
                this.ticketManager.handleChunkEnter(chunkSectionPos2, serverPlayerEntity);
            }
            if (!bl && bl2) {
                this.playerChunkWatchingManager.disableWatch(serverPlayerEntity);
            }
            if (bl && !bl2) {
                this.playerChunkWatchingManager.enableWatch(serverPlayerEntity);
            }
            if (l != m) {
                this.playerChunkWatchingManager.movePlayer(l, m, serverPlayerEntity);
            }
        }
        int k = chunkSectionPos.getChunkX();
        int n = chunkSectionPos.getChunkZ();
        if (Math.abs(k - i) <= this.watchDistance * 2 && Math.abs(n - j) <= this.watchDistance * 2) {
            int o = Math.min(i, k) - this.watchDistance;
            int p = Math.min(j, n) - this.watchDistance;
            int q = Math.max(i, k) + this.watchDistance;
            int r = Math.max(j, n) + this.watchDistance;
            for (int s = o; s <= q; ++s) {
                for (int t = p; t <= r; ++t) {
                    ChunkPos chunkPos = new ChunkPos(s, t);
                    boolean bl42 = ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, k, n) <= this.watchDistance;
                    boolean bl5 = ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, i, j) <= this.watchDistance;
                    this.sendWatchPackets(serverPlayerEntity, chunkPos, new Packet[2], bl42, bl5);
                }
            }
        } else {
            boolean bl7;
            boolean bl6;
            ChunkPos chunkPos2;
            int p;
            int o;
            for (o = k - this.watchDistance; o <= k + this.watchDistance; ++o) {
                for (p = n - this.watchDistance; p <= n + this.watchDistance; ++p) {
                    chunkPos2 = new ChunkPos(o, p);
                    bl6 = true;
                    bl7 = false;
                    this.sendWatchPackets(serverPlayerEntity, chunkPos2, new Packet[2], true, false);
                }
            }
            for (o = i - this.watchDistance; o <= i + this.watchDistance; ++o) {
                for (p = j - this.watchDistance; p <= j + this.watchDistance; ++p) {
                    chunkPos2 = new ChunkPos(o, p);
                    bl6 = false;
                    bl7 = true;
                    this.sendWatchPackets(serverPlayerEntity, chunkPos2, new Packet[2], false, true);
                }
            }
        }
    }

    @Override
    public Stream<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean bl) {
        return this.playerChunkWatchingManager.getPlayersWatchingChunk(chunkPos.toLong()).filter(serverPlayerEntity -> {
            int i = ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, serverPlayerEntity, true);
            if (i > this.watchDistance) {
                return false;
            }
            return !bl || i == this.watchDistance;
        });
    }

    protected void loadEntity(Entity entity) {
        if (entity instanceof EnderDragonPart) {
            return;
        }
        if (entity instanceof LightningEntity) {
            return;
        }
        EntityType<?> entityType = entity.getType();
        int i = entityType.getMaxTrackDistance() * 16;
        int j = entityType.getTrackTickInterval();
        if (this.entityTrackers.containsKey(entity.getEntityId())) {
            throw SystemUtil.throwOrPause(new IllegalStateException("Entity is already tracked!"));
        }
        EntityTracker entityTracker = new EntityTracker(entity, i, j, entityType.alwaysUpdateVelocity());
        this.entityTrackers.put(entity.getEntityId(), entityTracker);
        entityTracker.updateCameraPosition(this.world.getPlayers());
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            this.handlePlayerAddedOrRemoved(serverPlayerEntity, true);
            for (EntityTracker entityTracker2 : this.entityTrackers.values()) {
                if (entityTracker2.entity == serverPlayerEntity) continue;
                entityTracker2.updateCameraPosition(serverPlayerEntity);
            }
        }
    }

    protected void unloadEntity(Entity entity) {
        EntityTracker entityTracker2;
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            this.handlePlayerAddedOrRemoved(serverPlayerEntity, false);
            for (EntityTracker entityTracker : this.entityTrackers.values()) {
                entityTracker.stopTracking(serverPlayerEntity);
            }
        }
        if ((entityTracker2 = (EntityTracker)this.entityTrackers.remove(entity.getEntityId())) != null) {
            entityTracker2.stopTracking();
        }
    }

    protected void tickPlayerMovement() {
        ArrayList<ServerPlayerEntity> list = Lists.newArrayList();
        List<ServerPlayerEntity> list2 = this.world.getPlayers();
        for (EntityTracker entityTracker : this.entityTrackers.values()) {
            ChunkSectionPos chunkSectionPos2;
            ChunkSectionPos chunkSectionPos = entityTracker.lastCameraPosition;
            if (!Objects.equals(chunkSectionPos, chunkSectionPos2 = ChunkSectionPos.from(entityTracker.entity))) {
                entityTracker.updateCameraPosition(list2);
                Entity entity = entityTracker.entity;
                if (entity instanceof ServerPlayerEntity) {
                    list.add((ServerPlayerEntity)entity);
                }
                entityTracker.lastCameraPosition = chunkSectionPos2;
            }
            entityTracker.entry.method_18756();
        }
        for (EntityTracker entityTracker : this.entityTrackers.values()) {
            entityTracker.updateCameraPosition(list);
        }
    }

    protected void sendToOtherNearbyPlayers(Entity entity, Packet<?> packet) {
        EntityTracker entityTracker = (EntityTracker)this.entityTrackers.get(entity.getEntityId());
        if (entityTracker != null) {
            entityTracker.sendToOtherNearbyPlayers(packet);
        }
    }

    protected void sendToNearbyPlayers(Entity entity, Packet<?> packet) {
        EntityTracker entityTracker = (EntityTracker)this.entityTrackers.get(entity.getEntityId());
        if (entityTracker != null) {
            entityTracker.sendToNearbyPlayers(packet);
        }
    }

    private void sendChunkDataPackets(ServerPlayerEntity serverPlayerEntity, Packet<?>[] packets, WorldChunk worldChunk) {
        if (packets[0] == null) {
            packets[0] = new ChunkDataS2CPacket(worldChunk, 65535);
            packets[1] = new LightUpdateS2CPacket(worldChunk.getPos(), this.serverLightingProvider);
        }
        serverPlayerEntity.sendInitialChunkPackets(worldChunk.getPos(), packets[0], packets[1]);
        DebugRendererInfoManager.method_19775(this.world, worldChunk.getPos());
        ArrayList<Entity> list = Lists.newArrayList();
        ArrayList<Entity> list2 = Lists.newArrayList();
        for (EntityTracker entityTracker : this.entityTrackers.values()) {
            Entity entity = entityTracker.entity;
            if (entity == serverPlayerEntity || entity.chunkX != worldChunk.getPos().x || entity.chunkZ != worldChunk.getPos().z) continue;
            entityTracker.updateCameraPosition(serverPlayerEntity);
            if (entity instanceof MobEntity && ((MobEntity)entity).getHoldingEntity() != null) {
                list.add(entity);
            }
            if (entity.getPassengerList().isEmpty()) continue;
            list2.add(entity);
        }
        if (!list.isEmpty()) {
            for (Entity entity2 : list) {
                serverPlayerEntity.networkHandler.sendPacket(new EntityAttachS2CPacket(entity2, ((MobEntity)entity2).getHoldingEntity()));
            }
        }
        if (!list2.isEmpty()) {
            for (Entity entity2 : list2) {
                serverPlayerEntity.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity2));
            }
        }
    }

    protected PointOfInterestStorage getPointOfInterestStorage() {
        return this.pointOfInterestStorage;
    }

    public CompletableFuture<Void> method_20576(WorldChunk worldChunk) {
        return this.mainThreadExecutor.method_20493(() -> worldChunk.method_20471(this.world));
    }

    class EntityTracker {
        private final EntityTrackerEntry entry;
        private final Entity entity;
        private final int maxDistance;
        private ChunkSectionPos lastCameraPosition;
        private final Set<ServerPlayerEntity> playersTracking = Sets.newHashSet();

        public EntityTracker(Entity entity, int i, int j, boolean bl) {
            this.entry = new EntityTrackerEntry(ThreadedAnvilChunkStorage.this.world, entity, j, bl, this::sendToOtherNearbyPlayers);
            this.entity = entity;
            this.maxDistance = i;
            this.lastCameraPosition = ChunkSectionPos.from(entity);
        }

        public boolean equals(Object object) {
            if (object instanceof EntityTracker) {
                return ((EntityTracker)object).entity.getEntityId() == this.entity.getEntityId();
            }
            return false;
        }

        public int hashCode() {
            return this.entity.getEntityId();
        }

        public void sendToOtherNearbyPlayers(Packet<?> packet) {
            for (ServerPlayerEntity serverPlayerEntity : this.playersTracking) {
                serverPlayerEntity.networkHandler.sendPacket(packet);
            }
        }

        public void sendToNearbyPlayers(Packet<?> packet) {
            this.sendToOtherNearbyPlayers(packet);
            if (this.entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
            }
        }

        public void stopTracking() {
            for (ServerPlayerEntity serverPlayerEntity : this.playersTracking) {
                this.entry.stopTracking(serverPlayerEntity);
            }
        }

        public void stopTracking(ServerPlayerEntity serverPlayerEntity) {
            if (this.playersTracking.remove(serverPlayerEntity)) {
                this.entry.stopTracking(serverPlayerEntity);
            }
        }

        public void updateCameraPosition(ServerPlayerEntity serverPlayerEntity) {
            boolean bl;
            if (serverPlayerEntity == this.entity) {
                return;
            }
            Vec3d vec3d = new Vec3d(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z).subtract(this.entry.method_18759());
            int i = Math.min(this.maxDistance, (ThreadedAnvilChunkStorage.this.watchDistance - 1) * 16);
            boolean bl2 = bl = vec3d.x >= (double)(-i) && vec3d.x <= (double)i && vec3d.z >= (double)(-i) && vec3d.z <= (double)i && this.entity.canBeSpectated(serverPlayerEntity);
            if (bl) {
                ChunkPos chunkPos;
                ChunkHolder chunkHolder;
                boolean bl22 = this.entity.teleporting;
                if (!bl22 && (chunkHolder = ThreadedAnvilChunkStorage.this.getChunkHolder((chunkPos = new ChunkPos(this.entity.chunkX, this.entity.chunkZ)).toLong())) != null && chunkHolder.getWorldChunk() != null) {
                    boolean bl3 = bl22 = ThreadedAnvilChunkStorage.getChebyshevDistance(chunkPos, serverPlayerEntity, false) <= ThreadedAnvilChunkStorage.this.watchDistance;
                }
                if (bl22 && this.playersTracking.add(serverPlayerEntity)) {
                    this.entry.startTracking(serverPlayerEntity);
                }
            } else if (this.playersTracking.remove(serverPlayerEntity)) {
                this.entry.stopTracking(serverPlayerEntity);
            }
        }

        public void updateCameraPosition(List<ServerPlayerEntity> list) {
            for (ServerPlayerEntity serverPlayerEntity : list) {
                this.updateCameraPosition(serverPlayerEntity);
            }
        }
    }

    class TicketManager
    extends ChunkTicketManager {
        protected TicketManager(Executor executor, Executor executor2) {
            super(executor, executor2);
        }

        @Override
        protected boolean isUnloaded(long l) {
            return ThreadedAnvilChunkStorage.this.unloadedChunks.contains(l);
        }

        @Override
        @Nullable
        protected ChunkHolder getChunkHolder(long l) {
            return ThreadedAnvilChunkStorage.this.getCurrentChunkHolder(l);
        }

        @Override
        @Nullable
        protected ChunkHolder setLevel(long l, int i, @Nullable ChunkHolder chunkHolder, int j) {
            return ThreadedAnvilChunkStorage.this.setLevel(l, i, chunkHolder, j);
        }
    }
}

