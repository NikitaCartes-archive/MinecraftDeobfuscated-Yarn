/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.world;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
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
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> borderFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<WorldChunk, Unloaded>> entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
    private CompletableFuture<Chunk> future = CompletableFuture.completedFuture(null);
    private int lastTickLevel;
    private int level;
    private int completedLevel;
    private final ChunkPos pos;
    private final short[] blockUpdatePositions = new short[64];
    private int blockUpdateCount;
    private int sectionsNeedingUpdateMask;
    private int lightSentWithBlocksBits;
    private int blockLightUpdateBits;
    private int skyLightUpdateBits;
    private final LightingProvider lightingProvider;
    private final LevelUpdateListener levelUpdateListener;
    private final PlayersWatchingChunkProvider playersWatchingChunkProvider;
    private boolean field_19238;

    public ChunkHolder(ChunkPos chunkPos, int i, LightingProvider lightingProvider, LevelUpdateListener levelUpdateListener, PlayersWatchingChunkProvider playersWatchingChunkProvider) {
        this.pos = chunkPos;
        this.lightingProvider = lightingProvider;
        this.levelUpdateListener = levelUpdateListener;
        this.playersWatchingChunkProvider = playersWatchingChunkProvider;
        this.level = this.lastTickLevel = ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
        this.completedLevel = this.lastTickLevel;
        this.setLevel(i);
    }

    public CompletableFuture<Either<Chunk, Unloaded>> getFuture(ChunkStatus chunkStatus) {
        CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(chunkStatus.getIndex());
        return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
    }

    public CompletableFuture<Either<Chunk, Unloaded>> method_21737(ChunkStatus chunkStatus) {
        if (ChunkHolder.getTargetGenerationStatus(this.level).isAtLeast(chunkStatus)) {
            return this.getFuture(chunkStatus);
        }
        return UNLOADED_CHUNK_FUTURE;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> getTickingFuture() {
        return this.tickingFuture;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> getEntityTickingFuture() {
        return this.entityTickingFuture;
    }

    public CompletableFuture<Either<WorldChunk, Unloaded>> method_20725() {
        return this.borderFuture;
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
    public ChunkStatus getCompletedStatus() {
        for (int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            ChunkStatus chunkStatus = CHUNK_STATUSES.get(i);
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.getFuture(chunkStatus);
            if (!completableFuture.getNow(UNLOADED_CHUNK).left().isPresent()) continue;
            return chunkStatus;
        }
        return null;
    }

    @Nullable
    public Chunk getCompletedChunk() {
        for (int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            Optional<Chunk> optional;
            ChunkStatus chunkStatus = CHUNK_STATUSES.get(i);
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.getFuture(chunkStatus);
            if (completableFuture.isCompletedExceptionally() || !(optional = completableFuture.getNow(UNLOADED_CHUNK).left()).isPresent()) continue;
            return optional.get();
        }
        return null;
    }

    public CompletableFuture<Chunk> getFuture() {
        return this.future;
    }

    public void markForBlockUpdate(int i, int j, int k) {
        WorldChunk worldChunk = this.getWorldChunk();
        if (worldChunk == null) {
            return;
        }
        this.sectionsNeedingUpdateMask |= 1 << (j >> 4);
        if (this.blockUpdateCount < 64) {
            short s = (short)(i << 12 | k << 8 | j);
            for (int l = 0; l < this.blockUpdateCount; ++l) {
                if (this.blockUpdatePositions[l] != s) continue;
                return;
            }
            this.blockUpdatePositions[this.blockUpdateCount++] = s;
        }
    }

    public void markForLightUpdate(LightType lightType, int i) {
        WorldChunk worldChunk = this.getWorldChunk();
        if (worldChunk == null) {
            return;
        }
        worldChunk.setShouldSave(true);
        if (lightType == LightType.SKY) {
            this.skyLightUpdateBits |= 1 << i - -1;
        } else {
            this.blockLightUpdateBits |= 1 << i - -1;
        }
    }

    public void flushUpdates(WorldChunk worldChunk) {
        int j;
        int i;
        if (this.blockUpdateCount == 0 && this.skyLightUpdateBits == 0 && this.blockLightUpdateBits == 0) {
            return;
        }
        World world = worldChunk.getWorld();
        if (this.blockUpdateCount == 64) {
            this.lightSentWithBlocksBits = -1;
        }
        if (this.skyLightUpdateBits != 0 || this.blockLightUpdateBits != 0) {
            this.sendPacketToPlayersWatching(new LightUpdateS2CPacket(worldChunk.getPos(), this.lightingProvider, this.skyLightUpdateBits & ~this.lightSentWithBlocksBits, this.blockLightUpdateBits & ~this.lightSentWithBlocksBits), true);
            i = this.skyLightUpdateBits & this.lightSentWithBlocksBits;
            j = this.blockLightUpdateBits & this.lightSentWithBlocksBits;
            if (i != 0 || j != 0) {
                this.sendPacketToPlayersWatching(new LightUpdateS2CPacket(worldChunk.getPos(), this.lightingProvider, i, j), false);
            }
            this.skyLightUpdateBits = 0;
            this.blockLightUpdateBits = 0;
            this.lightSentWithBlocksBits &= ~(this.skyLightUpdateBits & this.blockLightUpdateBits);
        }
        if (this.blockUpdateCount == 1) {
            i = (this.blockUpdatePositions[0] >> 12 & 0xF) + this.pos.x * 16;
            j = this.blockUpdatePositions[0] & 0xFF;
            int k = (this.blockUpdatePositions[0] >> 8 & 0xF) + this.pos.z * 16;
            BlockPos blockPos = new BlockPos(i, j, k);
            this.sendPacketToPlayersWatching(new BlockUpdateS2CPacket(world, blockPos), false);
            if (world.getBlockState(blockPos).getBlock().hasBlockEntity()) {
                this.sendBlockEntityUpdatePacket(world, blockPos);
            }
        } else if (this.blockUpdateCount == 64) {
            this.sendPacketToPlayersWatching(new ChunkDataS2CPacket(worldChunk, this.sectionsNeedingUpdateMask), false);
        } else if (this.blockUpdateCount != 0) {
            this.sendPacketToPlayersWatching(new ChunkDeltaUpdateS2CPacket(this.blockUpdateCount, this.blockUpdatePositions, worldChunk), false);
            for (i = 0; i < this.blockUpdateCount; ++i) {
                j = (this.blockUpdatePositions[i] >> 12 & 0xF) + this.pos.x * 16;
                int k = this.blockUpdatePositions[i] & 0xFF;
                int l = (this.blockUpdatePositions[i] >> 8 & 0xF) + this.pos.z * 16;
                BlockPos blockPos2 = new BlockPos(j, k, l);
                if (!world.getBlockState(blockPos2).getBlock().hasBlockEntity()) continue;
                this.sendBlockEntityUpdatePacket(world, blockPos2);
            }
        }
        this.blockUpdateCount = 0;
        this.sectionsNeedingUpdateMask = 0;
    }

    private void sendBlockEntityUpdatePacket(World world, BlockPos blockPos) {
        BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket;
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity != null && (blockEntityUpdateS2CPacket = blockEntity.toUpdatePacket()) != null) {
            this.sendPacketToPlayersWatching(blockEntityUpdateS2CPacket, false);
        }
    }

    private void sendPacketToPlayersWatching(Packet<?> packet, boolean bl) {
        this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, bl).forEach(serverPlayerEntity -> serverPlayerEntity.networkHandler.sendPacket(packet));
    }

    public CompletableFuture<Either<Chunk, Unloaded>> createFuture(ChunkStatus chunkStatus, ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
        Either either;
        int i = chunkStatus.getIndex();
        CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(i);
        if (completableFuture != null && ((either = (Either)completableFuture.getNow(null)) == null || either.left().isPresent())) {
            return completableFuture;
        }
        if (ChunkHolder.getTargetGenerationStatus(this.level).isAtLeast(chunkStatus)) {
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture2 = threadedAnvilChunkStorage.createChunkFuture(this, chunkStatus);
            this.updateFuture(completableFuture2);
            this.futuresByStatus.set(i, completableFuture2);
            return completableFuture2;
        }
        return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
    }

    private void updateFuture(CompletableFuture<? extends Either<? extends Chunk, Unloaded>> completableFuture) {
        this.future = this.future.thenCombine(completableFuture, (chunk2, either) -> either.map(chunk -> chunk, unloaded -> chunk2));
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

    private void setCompletedLevel(int i) {
        this.completedLevel = i;
    }

    public void setLevel(int i) {
        this.level = i;
    }

    protected void tick(ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
        CompletableFuture<Either<Chunk, Unloaded>> completableFuture;
        ChunkStatus chunkStatus = ChunkHolder.getTargetGenerationStatus(this.lastTickLevel);
        ChunkStatus chunkStatus2 = ChunkHolder.getTargetGenerationStatus(this.level);
        boolean bl = this.lastTickLevel <= ThreadedAnvilChunkStorage.MAX_LEVEL;
        boolean bl2 = this.level <= ThreadedAnvilChunkStorage.MAX_LEVEL;
        LevelType levelType = ChunkHolder.getLevelType(this.lastTickLevel);
        LevelType levelType2 = ChunkHolder.getLevelType(this.level);
        if (bl) {
            int i;
            Either either2 = Either.right(new Unloaded(){

                public String toString() {
                    return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
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
        this.field_19238 |= bl4;
        if (!bl3 && bl4) {
            this.borderFuture = threadedAnvilChunkStorage.createBorderFuture(this);
            this.updateFuture(this.borderFuture);
        }
        if (bl3 && !bl4) {
            completableFuture = this.borderFuture;
            this.borderFuture = UNLOADED_WORLD_CHUNK_FUTURE;
            this.updateFuture((CompletableFuture<? extends Either<? extends Chunk, Unloaded>>)completableFuture.thenApply(either -> either.ifLeft(threadedAnvilChunkStorage::method_20576)));
        }
        boolean bl5 = levelType.isAfter(LevelType.TICKING);
        boolean bl6 = levelType2.isAfter(LevelType.TICKING);
        if (!bl5 && bl6) {
            this.tickingFuture = threadedAnvilChunkStorage.createTickingFuture(this);
            this.updateFuture(this.tickingFuture);
        }
        if (bl5 && !bl6) {
            this.tickingFuture.complete(UNLOADED_WORLD_CHUNK);
            this.tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
        }
        boolean bl7 = levelType.isAfter(LevelType.ENTITY_TICKING);
        boolean bl8 = levelType2.isAfter(LevelType.ENTITY_TICKING);
        if (!bl7 && bl8) {
            if (this.entityTickingFuture != UNLOADED_WORLD_CHUNK_FUTURE) {
                throw new IllegalStateException();
            }
            this.entityTickingFuture = threadedAnvilChunkStorage.createEntityTickingChunkFuture(this.pos);
            this.updateFuture(this.entityTickingFuture);
        }
        if (bl7 && !bl8) {
            this.entityTickingFuture.complete(UNLOADED_WORLD_CHUNK);
            this.entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
        }
        this.levelUpdateListener.updateLevel(this.pos, this::getCompletedLevel, this.level, this::setCompletedLevel);
        this.lastTickLevel = this.level;
    }

    public static ChunkStatus getTargetGenerationStatus(int i) {
        if (i < 33) {
            return ChunkStatus.FULL;
        }
        return ChunkStatus.getTargetGenerationStatus(i - 33);
    }

    public static LevelType getLevelType(int i) {
        return LEVEL_TYPES[MathHelper.clamp(33 - i + 1, 0, LEVEL_TYPES.length - 1)];
    }

    public boolean method_20384() {
        return this.field_19238;
    }

    public void method_20385() {
        this.field_19238 = ChunkHolder.getLevelType(this.level).isAfter(LevelType.BORDER);
    }

    public void method_20456(ReadOnlyChunk readOnlyChunk) {
        for (int i = 0; i < this.futuresByStatus.length(); ++i) {
            Optional<Chunk> optional;
            CompletableFuture<Either<Chunk, Unloaded>> completableFuture = this.futuresByStatus.get(i);
            if (completableFuture == null || !(optional = completableFuture.getNow(UNLOADED_CHUNK).left()).isPresent() || !(optional.get() instanceof ProtoChunk)) continue;
            this.futuresByStatus.set(i, CompletableFuture.completedFuture(Either.left(readOnlyChunk)));
        }
        this.updateFuture(CompletableFuture.completedFuture(Either.left(readOnlyChunk.getWrappedChunk())));
    }

    public static interface PlayersWatchingChunkProvider {
        public Stream<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos var1, boolean var2);
    }

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

