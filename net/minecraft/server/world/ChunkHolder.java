/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

public class ChunkHolder {
    public static final Either<Chunk, Unloaded> UNLOADED_CHUNK = Either.right(Unloaded.INSTANCE);
    public static final CompletableFuture<Either<Chunk, Unloaded>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
    public static final Either<WorldChunk, Unloaded> UNLOADED_WORLD_CHUNK = Either.right(Unloaded.INSTANCE);
    private static final CompletableFuture<Either<WorldChunk, Unloaded>> UNLOADED_WORLD_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_WORLD_CHUNK);
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
    private static final LevelType[] LEVEL_TYPES = LevelType.values();
    private final AtomicReferenceArray<CompletableFuture<Either<Chunk, Unloaded>>> futuresByStatus = new AtomicReferenceArray(CHUNK_STATUSES.size());
    private final HeightLimitView field_26929;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private CompletableFuture<Chunk> savingFuture = CompletableFuture.completedFuture(null);
    private int lastTickLevel;
    private int level;
    private int completedLevel;
    private final ChunkPos pos;
    /**
     * Indicates that {@link #blockUpdatesBySection} contains at least one entry.
     */
    private boolean pendingBlockUpdates;
    /**
     * Contains the packed chunk-local positions that have been marked for update
     * by {@link #markForBlockUpdate}, grouped by their vertical chunk section.
     * <p>
     * Entries for a section are null if the section has no positions marked for update.
     */
    private final ShortSet[] blockUpdatesBySection;
    private final BitSet blockLightUpdateBits = new BitSet();
    private final BitSet skyLightUpdateBits = new BitSet();
    private final LightingProvider lightingProvider;
    private final LevelUpdateListener levelUpdateListener;
    private final PlayersWatchingChunkProvider playersWatchingChunkProvider;
    private boolean accessible;
    private boolean field_26744;
    private CompletableFuture<Void> field_26930 = CompletableFuture.completedFuture(null);

    public ChunkHolder(ChunkPos pos, int level, HeightLimitView heightLimitView, LightingProvider lightingProvider, LevelUpdateListener levelUpdateListener, PlayersWatchingChunkProvider playersWatchingChunkProvider) {
        this.pos = pos;
        this.field_26929 = heightLimitView;
        this.lightingProvider = lightingProvider;
        this.levelUpdateListener = levelUpdateListener;
        this.playersWatchingChunkProvider = playersWatchingChunkProvider;
        this.level = this.lastTickLevel = ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        this.completedLevel = this.lastTickLevel;
        this.setLevel(level);
        this.blockUpdatesBySection = new ShortSet[heightLimitView.getSections()];
    }

    public CompletableFuture<Either<Chunk, Unloaded>> getFutureFor(ChunkStatus leastStatus) {
        CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(leastStatus.getIndex());
        return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
    }

    public CompletableFuture<Either<Chunk, Unloaded>> getValidFutureFor(ChunkStatus leastStatus) {
        if (ChunkHolder.getTargetStatusForLevel(this.level).isAtLeast(leastStatus)) {
            return this.getFutureFor(leastStatus);
        }
        return UNLOADED_CHUNK_FUTURE;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> getTickingFuture() {
        return this.tickingFuture;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> getEntityTickingFuture() {
        return this.entityTickingFuture;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> getAccessibleFuture() {
        return this.accessibleFuture;
    }

    @Nullable
    public WorldChunk getWorldChunk() {
        CompletableFuture<Either<WorldChunk, Unloaded>> completableFuture = this.getTickingFuture();
        Either either = completableFuture.getNow(null);
        if (either == null) {
            return null;
        }
        return either.left().orElse(null);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ChunkStatus getCurrentStatus() {
        for (int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            ChunkStatus chunkStatus = CHUNK_STATUSES.get(i);
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.getFutureFor(chunkStatus);
            if (!completableFuture.getNow(UNLOADED_CHUNK).left().isPresent()) continue;
            return chunkStatus;
        }
        return null;
    }

    @Nullable
    public Chunk getCurrentChunk() {
        for (int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            Optional<Chunk> optional;
            ChunkStatus chunkStatus = CHUNK_STATUSES.get(i);
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.getFutureFor(chunkStatus);
            if (completableFuture.isCompletedExceptionally() || !(optional = completableFuture.getNow(UNLOADED_CHUNK).left()).isPresent()) continue;
            return optional.get();
        }
        return null;
    }

    public CompletableFuture<Chunk> getSavingFuture() {
        return this.savingFuture;
    }

    public void markForBlockUpdate(BlockPos blockPos) {
        WorldChunk worldChunk = this.getWorldChunk();
        if (worldChunk == null) {
            return;
        }
        int i = this.field_26929.getSectionIndex(blockPos.getY());
        if (this.blockUpdatesBySection[i] == null) {
            this.pendingBlockUpdates = true;
            this.blockUpdatesBySection[i] = new ShortArraySet();
        }
        this.blockUpdatesBySection[i].add(ChunkSectionPos.packLocal(blockPos));
    }

    /**
     * @param y chunk section y coordinate
     */
    public void markForLightUpdate(LightType lightType, int y) {
        WorldChunk worldChunk = this.getWorldChunk();
        if (worldChunk == null) {
            return;
        }
        worldChunk.setShouldSave(true);
        int i = this.lightingProvider.method_31929();
        int j = this.lightingProvider.method_31930();
        if (y < i || y > j) {
            return;
        }
        int k = y - i;
        if (lightType == LightType.SKY) {
            this.skyLightUpdateBits.set(k);
        } else {
            this.blockLightUpdateBits.set(k);
        }
    }

    public void flushUpdates(WorldChunk worldChunk) {
        int j;
        if (!this.pendingBlockUpdates && this.skyLightUpdateBits.isEmpty() && this.blockLightUpdateBits.isEmpty()) {
            return;
        }
        World world = worldChunk.getWorld();
        int i = 0;
        for (j = 0; j < this.blockUpdatesBySection.length; ++j) {
            i += this.blockUpdatesBySection[j] != null ? this.blockUpdatesBySection[j].size() : 0;
        }
        this.field_26744 |= i >= 64;
        if (!this.skyLightUpdateBits.isEmpty() || !this.blockLightUpdateBits.isEmpty()) {
            this.sendPacketToPlayersWatching(new LightUpdateS2CPacket(worldChunk.getPos(), this.lightingProvider, this.skyLightUpdateBits, this.blockLightUpdateBits, true), !this.field_26744);
            this.skyLightUpdateBits.clear();
            this.blockLightUpdateBits.clear();
        }
        for (j = 0; j < this.blockUpdatesBySection.length; ++j) {
            ShortSet shortSet = this.blockUpdatesBySection[j];
            if (shortSet == null) continue;
            int k = this.field_26929.getSection(j);
            ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(worldChunk.getPos(), k);
            if (shortSet.size() == 1) {
                BlockPos blockPos2 = chunkSectionPos.unpackBlockPos(shortSet.iterator().nextShort());
                BlockState blockState2 = world.getBlockState(blockPos2);
                this.sendPacketToPlayersWatching(new BlockUpdateS2CPacket(blockPos2, blockState2), false);
                this.tryUpdateBlockEntityAt(world, blockPos2, blockState2);
            } else {
                ChunkSection chunkSection = worldChunk.getSectionArray()[j];
                ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket = new ChunkDeltaUpdateS2CPacket(chunkSectionPos, shortSet, chunkSection, this.field_26744);
                this.sendPacketToPlayersWatching(chunkDeltaUpdateS2CPacket, false);
                chunkDeltaUpdateS2CPacket.visitUpdates((blockPos, blockState) -> this.tryUpdateBlockEntityAt(world, (BlockPos)blockPos, (BlockState)blockState));
            }
            this.blockUpdatesBySection[j] = null;
        }
        this.pendingBlockUpdates = false;
    }

    private void tryUpdateBlockEntityAt(World world, BlockPos blockPos, BlockState blockState) {
        if (blockState.hasBlockEntity()) {
            this.sendBlockEntityUpdatePacket(world, blockPos);
        }
    }

    private void sendBlockEntityUpdatePacket(World world, BlockPos pos) {
        BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null && (blockEntityUpdateS2CPacket = blockEntity.toUpdatePacket()) != null) {
            this.sendPacketToPlayersWatching(blockEntityUpdateS2CPacket, false);
        }
    }

    private void sendPacketToPlayersWatching(Packet<?> packet, boolean onlyOnWatchDistanceEdge) {
        this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, onlyOnWatchDistanceEdge).forEach(serverPlayerEntity -> serverPlayerEntity.networkHandler.sendPacket(packet));
    }

    public CompletableFuture<Either<Chunk, Unloaded>> getChunkAt(ChunkStatus targetStatus, ThreadedAnvilChunkStorage chunkStorage) {
        Either either;
        int i = targetStatus.getIndex();
        CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(i);
        if (completableFuture != null && ((either = (Either)completableFuture.getNow(null)) == null || either.left().isPresent())) {
            return completableFuture;
        }
        if (ChunkHolder.getTargetStatusForLevel(this.level).isAtLeast(targetStatus)) {
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture2 = chunkStorage.getChunk(this, targetStatus);
            this.combineSavingFuture(completableFuture2);
            this.futuresByStatus.set(i, completableFuture2);
            return completableFuture2;
        }
        return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
    }

    private void combineSavingFuture(CompletableFuture<? extends Either<? extends Chunk, Unloaded>> then) {
        this.savingFuture = this.savingFuture.thenCombine(then, (chunk2, either) -> either.map(chunk -> chunk, unloaded -> chunk2));
    }

    @Environment(value=EnvType.CLIENT)
    public LevelType getLevelType() {
        return ChunkHolder.getLevelType(this.level);
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    public int getLevel() {
        return this.level;
    }

    public int getCompletedLevel() {
        return this.completedLevel;
    }

    private void setCompletedLevel(int level) {
        this.completedLevel = level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    private void method_31409(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, CompletableFuture<Either<WorldChunk, Unloaded>> completableFuture, Executor executor, LevelType levelType) {
        this.field_26930.cancel(false);
        CompletableFuture completableFuture2 = new CompletableFuture();
        completableFuture2.thenRunAsync(() -> threadedAnvilChunkStorage.method_31414(this.pos, levelType), executor);
        this.field_26930 = completableFuture2;
        completableFuture.thenAccept(either -> either.ifLeft(worldChunk -> completableFuture2.complete(null)));
    }

    private void method_31408(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, LevelType levelType) {
        this.field_26930.cancel(false);
        threadedAnvilChunkStorage.method_31414(this.pos, levelType);
    }

    protected void tick(ThreadedAnvilChunkStorage chunkStorage, Executor executor) {
        CompletableFuture<Either<Chunk, Unloaded>> completableFuture;
        ChunkStatus chunkStatus = ChunkHolder.getTargetStatusForLevel(this.lastTickLevel);
        ChunkStatus chunkStatus2 = ChunkHolder.getTargetStatusForLevel(this.level);
        boolean bl = this.lastTickLevel <= ThreadedAnvilChunkStorage.MAX_LEVEL;
        boolean bl2 = this.level <= ThreadedAnvilChunkStorage.MAX_LEVEL;
        LevelType levelType = ChunkHolder.getLevelType(this.lastTickLevel);
        LevelType levelType2 = ChunkHolder.getLevelType(this.level);
        if (bl) {
            int i;
            Either either2 = Either.right(new Unloaded(){

                public String toString() {
                    return "Unloaded ticket level " + ChunkHolder.this.pos;
                }
            });
            int n = i = bl2 ? chunkStatus2.getIndex() + 1 : 0;
            while (i <= chunkStatus.getIndex()) {
                completableFuture = this.futuresByStatus.get(i);
                if (completableFuture != null) {
                    completableFuture.complete(either2);
                } else {
                    this.futuresByStatus.set(i, CompletableFuture.completedFuture(either2));
                }
                ++i;
            }
        }
        boolean bl3 = levelType.isAfter(LevelType.BORDER);
        boolean bl4 = levelType2.isAfter(LevelType.BORDER);
        this.accessible |= bl4;
        if (!bl3 && bl4) {
            this.accessibleFuture = chunkStorage.method_31417(this);
            this.method_31409(chunkStorage, this.accessibleFuture, executor, LevelType.BORDER);
            this.combineSavingFuture(this.accessibleFuture);
        }
        if (bl3 && !bl4) {
            completableFuture = this.accessibleFuture;
            this.accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
            this.combineSavingFuture((CompletableFuture<? extends Either<? extends Chunk, Unloaded>>)completableFuture.thenApply(either -> either.ifLeft(chunkStorage::enableTickSchedulers)));
        }
        boolean bl5 = levelType.isAfter(LevelType.TICKING);
        boolean bl6 = levelType2.isAfter(LevelType.TICKING);
        if (!bl5 && bl6) {
            this.tickingFuture = chunkStorage.makeChunkTickable(this);
            this.method_31409(chunkStorage, this.tickingFuture, executor, LevelType.TICKING);
            this.combineSavingFuture(this.tickingFuture);
        }
        if (bl5 && !bl6) {
            this.tickingFuture.complete(UNLOADED_WORLD_CHUNK);
            this.tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
        }
        boolean bl7 = levelType.isAfter(LevelType.ENTITY_TICKING);
        boolean bl8 = levelType2.isAfter(LevelType.ENTITY_TICKING);
        if (!bl7 && bl8) {
            if (this.entityTickingFuture != UNLOADED_WORLD_CHUNK_FUTURE) {
                throw Util.throwOrPause(new IllegalStateException());
            }
            this.entityTickingFuture = chunkStorage.makeChunkEntitiesTickable(this.pos);
            this.method_31409(chunkStorage, this.entityTickingFuture, executor, LevelType.ENTITY_TICKING);
            this.combineSavingFuture(this.entityTickingFuture);
        }
        if (bl7 && !bl8) {
            this.entityTickingFuture.complete(UNLOADED_WORLD_CHUNK);
            this.entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
        }
        if (!levelType2.isAfter(levelType)) {
            this.method_31408(chunkStorage, levelType2);
        }
        this.levelUpdateListener.updateLevel(this.pos, this::getCompletedLevel, this.level, this::setCompletedLevel);
        this.lastTickLevel = this.level;
    }

    public static ChunkStatus getTargetStatusForLevel(int level) {
        if (level < 33) {
            return ChunkStatus.FULL;
        }
        return ChunkStatus.byDistanceFromFull(level - 33);
    }

    public static LevelType getLevelType(int distance) {
        return LEVEL_TYPES[MathHelper.clamp(33 - distance + 1, 0, LEVEL_TYPES.length - 1)];
    }

    public boolean isAccessible() {
        return this.accessible;
    }

    public void updateAccessibleStatus() {
        this.accessible = ChunkHolder.getLevelType(this.level).isAfter(LevelType.BORDER);
    }

    public void setCompletedChunk(ReadOnlyChunk readOnlyChunk) {
        for (int i = 0; i < this.futuresByStatus.length(); ++i) {
            Optional<Chunk> optional;
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(i);
            if (completableFuture == null || !(optional = completableFuture.getNow(UNLOADED_CHUNK).left()).isPresent() || !(optional.get() instanceof ProtoChunk)) continue;
            this.futuresByStatus.set(i, CompletableFuture.completedFuture(Either.left(readOnlyChunk)));
        }
        this.combineSavingFuture(CompletableFuture.completedFuture(Either.left(readOnlyChunk.getWrappedChunk())));
    }

    public static interface PlayersWatchingChunkProvider {
        public Stream<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos var1, boolean var2);
    }

    @FunctionalInterface
    public static interface LevelUpdateListener {
        public void updateLevel(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4);
    }

    public static interface Unloaded {
        public static final Unloaded INSTANCE = new Unloaded(){

            public String toString() {
                return "UNLOADED";
            }
        };
    }

    public static enum LevelType {
        INACCESSIBLE,
        BORDER,
        TICKING,
        ENTITY_TICKING;


        public boolean isAfter(LevelType levelType) {
            return this.ordinal() >= levelType.ordinal();
        }
    }
}

