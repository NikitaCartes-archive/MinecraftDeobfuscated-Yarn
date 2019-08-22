/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTaskPrioritySystem;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Actor;
import net.minecraft.util.ChunkPosLevelPropagator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class ChunkTicketManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int NEARBY_PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL) - 2;
    private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos = new Long2ObjectOpenHashMap<ObjectSet<ServerPlayerEntity>>();
    private final Long2ObjectOpenHashMap<ObjectSortedSet<ChunkTicket<?>>> ticketsByPosition = new Long2ObjectOpenHashMap();
    private final class_4077 distanceFromTicketTracker = new class_4077();
    private final DistanceFromNearestPlayerTracker distanceFromNearestPlayerTracker = new DistanceFromNearestPlayerTracker(8);
    private final NearbyChunkTicketUpdater nearbyChunkTicketUpdater = new NearbyChunkTicketUpdater(33);
    private final Set<ChunkHolder> chunkHolders = Sets.newHashSet();
    private final ChunkTaskPrioritySystem levelUpdateListener;
    private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> playerTicketThrottler;
    private final Actor<ChunkTaskPrioritySystem.SorterMessage> playerTicketThrottlerSorter;
    private final LongSet chunkPositions = new LongOpenHashSet();
    private final Executor mainThreadExecutor;
    private long location;

    protected ChunkTicketManager(Executor executor, Executor executor2) {
        ChunkTaskPrioritySystem chunkTaskPrioritySystem;
        Actor<Runnable> actor = Actor.createConsumerActor("player ticket throttler", executor2::execute);
        this.levelUpdateListener = chunkTaskPrioritySystem = new ChunkTaskPrioritySystem(ImmutableList.of(actor), executor, 4);
        this.playerTicketThrottler = chunkTaskPrioritySystem.createExecutingActor(actor, true);
        this.playerTicketThrottlerSorter = chunkTaskPrioritySystem.createSortingActor(actor);
        this.mainThreadExecutor = executor2;
    }

    protected void purge() {
        ++this.location;
        ObjectIterator objectIterator = this.ticketsByPosition.long2ObjectEntrySet().fastIterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectIterator.next();
            if (((ObjectSortedSet)entry.getValue()).removeIf(chunkTicket -> chunkTicket.method_20627(this.location))) {
                this.distanceFromTicketTracker.update(entry.getLongKey(), this.getLevel((ObjectSortedSet)entry.getValue()), false);
            }
            if (!((ObjectSortedSet)entry.getValue()).isEmpty()) continue;
            objectIterator.remove();
        }
    }

    private int getLevel(ObjectSortedSet<ChunkTicket<?>> objectSortedSet) {
        ObjectIterator objectBidirectionalIterator = objectSortedSet.iterator();
        if (objectBidirectionalIterator.hasNext()) {
            return ((ChunkTicket)objectBidirectionalIterator.next()).getLevel();
        }
        return ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
    }

    protected abstract boolean isUnloaded(long var1);

    @Nullable
    protected abstract ChunkHolder getChunkHolder(long var1);

    @Nullable
    protected abstract ChunkHolder setLevel(long var1, int var3, @Nullable ChunkHolder var4, int var5);

    public boolean tick(ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
        boolean bl;
        this.distanceFromNearestPlayerTracker.updateLevels();
        this.nearbyChunkTicketUpdater.updateLevels();
        int i = Integer.MAX_VALUE - this.distanceFromTicketTracker.method_18746(Integer.MAX_VALUE);
        boolean bl2 = bl = i != 0;
        if (bl) {
            // empty if block
        }
        if (!this.chunkHolders.isEmpty()) {
            this.chunkHolders.forEach(chunkHolder -> chunkHolder.tick(threadedAnvilChunkStorage));
            this.chunkHolders.clear();
            return true;
        }
        if (!this.chunkPositions.isEmpty()) {
            LongIterator longIterator = this.chunkPositions.iterator();
            while (longIterator.hasNext()) {
                long l = longIterator.nextLong();
                if (!this.getTicketSet(l).stream().anyMatch(chunkTicket -> chunkTicket.getType() == ChunkTicketType.PLAYER)) continue;
                ChunkHolder chunkHolder2 = threadedAnvilChunkStorage.getCurrentChunkHolder(l);
                if (chunkHolder2 == null) {
                    throw new IllegalStateException();
                }
                CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder2.getEntityTickingFuture();
                completableFuture.thenAccept(either -> this.mainThreadExecutor.execute(() -> this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> {}, l, false))));
            }
            this.chunkPositions.clear();
        }
        return bl;
    }

    private void addTicket(long l, ChunkTicket<?> chunkTicket) {
        ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(l);
        ObjectIterator objectBidirectionalIterator = objectSortedSet.iterator();
        int i = objectBidirectionalIterator.hasNext() ? ((ChunkTicket)objectBidirectionalIterator.next()).getLevel() : ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        if (objectSortedSet.add(chunkTicket)) {
            // empty if block
        }
        if (chunkTicket.getLevel() < i) {
            this.distanceFromTicketTracker.update(l, chunkTicket.getLevel(), true);
        }
    }

    private void removeTicket(long l, ChunkTicket<?> chunkTicket) {
        ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(l);
        if (objectSortedSet.remove(chunkTicket)) {
            // empty if block
        }
        if (objectSortedSet.isEmpty()) {
            this.ticketsByPosition.remove(l);
        }
        this.distanceFromTicketTracker.update(l, this.getLevel(objectSortedSet), false);
    }

    public <T> void addTicketWithLevel(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
        this.addTicket(chunkPos.toLong(), new ChunkTicket<T>(chunkTicketType, i, object, this.location));
    }

    public <T> void removeTicketWithLevel(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
        ChunkTicket<T> chunkTicket = new ChunkTicket<T>(chunkTicketType, i, object, this.location);
        this.removeTicket(chunkPos.toLong(), chunkTicket);
    }

    public <T> void addTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
        this.addTicket(chunkPos.toLong(), new ChunkTicket<T>(chunkTicketType, 33 - i, object, this.location));
    }

    public <T> void removeTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
        ChunkTicket<T> chunkTicket = new ChunkTicket<T>(chunkTicketType, 33 - i, object, this.location);
        this.removeTicket(chunkPos.toLong(), chunkTicket);
    }

    private ObjectSortedSet<ChunkTicket<?>> getTicketSet(long l2) {
        return this.ticketsByPosition.computeIfAbsent(l2, l -> new ObjectAVLTreeSet());
    }

    protected void setChunkForced(ChunkPos chunkPos, boolean bl) {
        ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<ChunkPos>(ChunkTicketType.FORCED, 31, chunkPos, this.location);
        if (bl) {
            this.addTicket(chunkPos.toLong(), chunkTicket);
        } else {
            this.removeTicket(chunkPos.toLong(), chunkTicket);
        }
    }

    public void handleChunkEnter(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
        long l2 = chunkSectionPos.toChunkPos().toLong();
        this.playersByChunkPos.computeIfAbsent(l2, l -> new ObjectOpenHashSet()).add(serverPlayerEntity);
        this.distanceFromNearestPlayerTracker.update(l2, 0, true);
        this.nearbyChunkTicketUpdater.update(l2, 0, true);
    }

    public void handleChunkLeave(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
        long l = chunkSectionPos.toChunkPos().toLong();
        ObjectSet objectSet = (ObjectSet)this.playersByChunkPos.get(l);
        objectSet.remove(serverPlayerEntity);
        if (objectSet.isEmpty()) {
            this.playersByChunkPos.remove(l);
            this.distanceFromNearestPlayerTracker.update(l, Integer.MAX_VALUE, false);
            this.nearbyChunkTicketUpdater.update(l, Integer.MAX_VALUE, false);
        }
    }

    protected String method_21623(long l) {
        ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.ticketsByPosition.get(l);
        String string = objectSortedSet == null || objectSortedSet.isEmpty() ? "no_ticket" : ((ChunkTicket)objectSortedSet.first()).toString();
        return string;
    }

    protected void setWatchDistance(int i) {
        this.nearbyChunkTicketUpdater.setWatchDistance(i);
    }

    public int getLevelCount() {
        this.distanceFromNearestPlayerTracker.updateLevels();
        return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.size();
    }

    public boolean method_20800(long l) {
        this.distanceFromNearestPlayerTracker.updateLevels();
        return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.containsKey(l);
    }

    public String method_21683() {
        return this.levelUpdateListener.method_21680();
    }

    class class_4077
    extends ChunkPosLevelPropagator {
        public class_4077() {
            super(ThreadedAnvilChunkStorage.MAX_LEVEL + 2, 16, 256);
        }

        @Override
        protected int getInitialLevel(long l) {
            ObjectSortedSet objectSortedSet = (ObjectSortedSet)ChunkTicketManager.this.ticketsByPosition.get(l);
            if (objectSortedSet == null) {
                return Integer.MAX_VALUE;
            }
            ObjectIterator objectBidirectionalIterator = objectSortedSet.iterator();
            if (!objectBidirectionalIterator.hasNext()) {
                return Integer.MAX_VALUE;
            }
            return ((ChunkTicket)objectBidirectionalIterator.next()).getLevel();
        }

        @Override
        protected int getLevel(long l) {
            ChunkHolder chunkHolder;
            if (!ChunkTicketManager.this.isUnloaded(l) && (chunkHolder = ChunkTicketManager.this.getChunkHolder(l)) != null) {
                return chunkHolder.getLevel();
            }
            return ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        }

        @Override
        protected void setLevel(long l, int i) {
            int j;
            ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(l);
            int n = j = chunkHolder == null ? ThreadedAnvilChunkStorage.MAX_LEVEL + 1 : chunkHolder.getLevel();
            if (j == i) {
                return;
            }
            if ((chunkHolder = ChunkTicketManager.this.setLevel(l, i, chunkHolder, j)) != null) {
                ChunkTicketManager.this.chunkHolders.add(chunkHolder);
            }
        }

        public int method_18746(int i) {
            return this.updateAllRecursively(i);
        }
    }

    class NearbyChunkTicketUpdater
    extends DistanceFromNearestPlayerTracker {
        private int watchDistance;
        private final Long2IntMap distances;
        private final LongSet positionsAffected;

        protected NearbyChunkTicketUpdater(int i) {
            super(i);
            this.distances = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
            this.positionsAffected = new LongOpenHashSet();
            this.watchDistance = 0;
            this.distances.defaultReturnValue(i + 2);
        }

        @Override
        protected void onDistanceChange(long l, int i, int j) {
            this.positionsAffected.add(l);
        }

        public void setWatchDistance(int i) {
            for (Long2ByteMap.Entry entry : this.distanceFromNearestPlayer.long2ByteEntrySet()) {
                byte b = entry.getByteValue();
                long l = entry.getLongKey();
                this.updateTicket(l, b, this.isWithinViewDistance(b), b <= i - 2);
            }
            this.watchDistance = i;
        }

        private void updateTicket(long l, int i, boolean bl, boolean bl2) {
            if (bl != bl2) {
                ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<ChunkPos>(ChunkTicketType.PLAYER, NEARBY_PLAYER_TICKET_LEVEL, new ChunkPos(l), ChunkTicketManager.this.location);
                if (bl2) {
                    ChunkTicketManager.this.playerTicketThrottler.send(ChunkTaskPrioritySystem.createRunnableMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> {
                        if (this.isWithinViewDistance(this.getLevel(l))) {
                            ChunkTicketManager.this.addTicket(l, chunkTicket);
                            ChunkTicketManager.this.chunkPositions.add(l);
                        } else {
                            ChunkTicketManager.this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> {}, l, false));
                        }
                    }), l, () -> i));
                } else {
                    ChunkTicketManager.this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> ChunkTicketManager.this.removeTicket(l, chunkTicket)), l, true));
                }
            }
        }

        @Override
        public void updateLevels() {
            super.updateLevels();
            if (!this.positionsAffected.isEmpty()) {
                LongIterator longIterator = this.positionsAffected.iterator();
                while (longIterator.hasNext()) {
                    int j;
                    long l = longIterator.nextLong();
                    int i2 = this.distances.get(l);
                    if (i2 == (j = this.getLevel(l))) continue;
                    ChunkTicketManager.this.levelUpdateListener.updateLevel(new ChunkPos(l), () -> this.distances.get(l), j, i -> {
                        if (i >= this.distances.defaultReturnValue()) {
                            this.distances.remove(l);
                        } else {
                            this.distances.put(l, i);
                        }
                    });
                    this.updateTicket(l, j, this.isWithinViewDistance(i2), this.isWithinViewDistance(j));
                }
                this.positionsAffected.clear();
            }
        }

        private boolean isWithinViewDistance(int i) {
            return i <= this.watchDistance - 2;
        }
    }

    class DistanceFromNearestPlayerTracker
    extends ChunkPosLevelPropagator {
        protected final Long2ByteMap distanceFromNearestPlayer;
        protected final int maxDistance;

        protected DistanceFromNearestPlayerTracker(int i) {
            super(i + 2, 16, 256);
            this.distanceFromNearestPlayer = new Long2ByteOpenHashMap();
            this.maxDistance = i;
            this.distanceFromNearestPlayer.defaultReturnValue((byte)(i + 2));
        }

        @Override
        protected int getLevel(long l) {
            return this.distanceFromNearestPlayer.get(l);
        }

        @Override
        protected void setLevel(long l, int i) {
            byte b = i > this.maxDistance ? this.distanceFromNearestPlayer.remove(l) : this.distanceFromNearestPlayer.put(l, (byte)i);
            this.onDistanceChange(l, b, i);
        }

        protected void onDistanceChange(long l, int i, int j) {
        }

        @Override
        protected int getInitialLevel(long l) {
            return this.isPlayerInChunk(l) ? 0 : Integer.MAX_VALUE;
        }

        private boolean isPlayerInChunk(long l) {
            ObjectSet objectSet = (ObjectSet)ChunkTicketManager.this.playersByChunkPos.get(l);
            return objectSet != null && !objectSet.isEmpty();
        }

        public void updateLevels() {
            this.updateAllRecursively(Integer.MAX_VALUE);
        }
    }
}

