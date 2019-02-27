package net.minecraft.server.world;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.network.packet.ChunkDataS2CPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateS2CPacket;
import net.minecraft.client.network.packet.LightUpdateS2CPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

public class ChunkHolder {
	public static final Either<Chunk, ChunkHolder.Unloaded> UNLOADED_CHUNK = Either.right(ChunkHolder.Unloaded.INSTANCE);
	public static final CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
	public static final Either<WorldChunk, ChunkHolder.Unloaded> UNLOADED_WORLD_CHUNK = Either.right(ChunkHolder.Unloaded.INSTANCE);
	private static final CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> UNLOADED_WORLD_CHUNK_FUTURE = CompletableFuture.completedFuture(
		UNLOADED_WORLD_CHUNK
	);
	private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
	private static final ChunkHolder.LevelType[] LEVEL_TYPES = ChunkHolder.LevelType.values();
	private final AtomicReferenceArray<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> chunkByStatus = new AtomicReferenceArray(CHUNK_STATUSES.size());
	private volatile CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> tickingFutureChunk = UNLOADED_WORLD_CHUNK_FUTURE;
	private volatile CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> entityTickingFutureChunk = UNLOADED_WORLD_CHUNK_FUTURE;
	private CompletableFuture<Chunk> chunk = CompletableFuture.completedFuture(null);
	private int field_16432;
	private int level;
	private int lastLevelUpdatedTo;
	private final ChunkPos pos;
	private final short[] blockUpdatePositions = new short[64];
	private int blockUpdateCount;
	private int sectionsNeedingUpdateMask;
	private int lightSentWithBlocksBits;
	private int skyLightUpdateBits;
	private int blockLightUpdateBits;
	private final LightingProvider lightingProvider;
	private final ChunkHolder.LevelUpdateListener levelUpdateListener;
	private final ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider;

	public ChunkHolder(
		ChunkPos chunkPos,
		int i,
		LightingProvider lightingProvider,
		ChunkHolder.LevelUpdateListener levelUpdateListener,
		ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider
	) {
		this.pos = chunkPos;
		this.lightingProvider = lightingProvider;
		this.levelUpdateListener = levelUpdateListener;
		this.playersWatchingChunkProvider = playersWatchingChunkProvider;
		this.field_16432 = ThreadedAnvilChunkStorage.field_18239 + 1;
		this.level = this.field_16432;
		this.lastLevelUpdatedTo = this.field_16432;
		this.setLevel(i);
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkForStatus(ChunkStatus chunkStatus) {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.chunkByStatus
			.get(chunkStatus.getIndex());
		return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkMinimumStatus(ChunkStatus chunkStatus) {
		return getTargetGenerationStatus(this.level).isAfter(chunkStatus) ? this.getChunkForStatus(chunkStatus) : UNLOADED_CHUNK_FUTURE;
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> method_16145() {
		return this.tickingFutureChunk;
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> method_14003() {
		return this.entityTickingFutureChunk;
	}

	@Nullable
	public WorldChunk getWorldChunk() {
		CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = this.method_16145();
		Either<WorldChunk, ChunkHolder.Unloaded> either = (Either<WorldChunk, ChunkHolder.Unloaded>)completableFuture.getNow(null);
		return either == null ? null : (WorldChunk)either.left().orElse(null);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public ChunkStatus method_16141() {
		for (int i = CHUNK_STATUSES.size() - 1; i >= 0; i--) {
			ChunkStatus chunkStatus = (ChunkStatus)CHUNK_STATUSES.get(i);
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getChunkForStatus(chunkStatus);
			if (((Either)completableFuture.getNow(UNLOADED_CHUNK)).left().isPresent()) {
				return chunkStatus;
			}
		}

		return null;
	}

	@Nullable
	public Chunk getChunk() {
		for (int i = CHUNK_STATUSES.size() - 1; i >= 0; i--) {
			ChunkStatus chunkStatus = (ChunkStatus)CHUNK_STATUSES.get(i);
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getChunkForStatus(chunkStatus);
			if (!completableFuture.isCompletedExceptionally()) {
				Optional<Chunk> optional = ((Either)completableFuture.getNow(UNLOADED_CHUNK)).left();
				if (optional.isPresent()) {
					return (Chunk)optional.get();
				}
			}
		}

		return null;
	}

	public CompletableFuture<Chunk> getChunkFuture() {
		return this.chunk;
	}

	public void markForUpdate(int i, int j, int k) {
		WorldChunk worldChunk = this.getWorldChunk();
		if (worldChunk != null) {
			this.sectionsNeedingUpdateMask |= 1 << (j >> 4);
			if (this.blockUpdateCount < 64) {
				short s = (short)(i << 12 | k << 8 | j);

				for (int l = 0; l < this.blockUpdateCount; l++) {
					if (this.blockUpdatePositions[l] == s) {
						return;
					}
				}

				this.blockUpdatePositions[this.blockUpdateCount++] = s;
			}
		}
	}

	public void method_14012(LightType lightType, int i) {
		WorldChunk worldChunk = this.getWorldChunk();
		if (worldChunk != null) {
			if (lightType == LightType.SKY) {
				this.blockLightUpdateBits |= 1 << i - -1;
			} else {
				this.skyLightUpdateBits |= 1 << i - -1;
			}
		}
	}

	public void flushUpdates(WorldChunk worldChunk) {
		if (this.blockUpdateCount != 0 || this.blockLightUpdateBits != 0 || this.skyLightUpdateBits != 0) {
			World world = worldChunk.getWorld();
			if (this.blockUpdateCount == 1) {
				int i = (this.blockUpdatePositions[0] >> 12 & 15) + this.pos.x * 16;
				int j = this.blockUpdatePositions[0] & 255;
				int k = (this.blockUpdatePositions[0] >> 8 & 15) + this.pos.z * 16;
				BlockPos blockPos = new BlockPos(i, j, k);
				this.sendPacketToPlayersWatching(new BlockUpdateS2CPacket(world, blockPos), false);
				if (world.getBlockState(blockPos).getBlock().hasBlockEntity()) {
					this.sendBlockEntityUpdatePacket(world, blockPos);
				}
			} else if (this.blockUpdateCount == 64) {
				this.sendPacketToPlayersWatching(new ChunkDataS2CPacket(worldChunk, this.sectionsNeedingUpdateMask), false);
				this.lightSentWithBlocksBits = this.sectionsNeedingUpdateMask << 1;
			} else if (this.blockUpdateCount != 0) {
				this.sendPacketToPlayersWatching(new ChunkDeltaUpdateS2CPacket(this.blockUpdateCount, this.blockUpdatePositions, worldChunk), false);

				for (int i = 0; i < this.blockUpdateCount; i++) {
					int j = (this.blockUpdatePositions[i] >> 12 & 15) + this.pos.x * 16;
					int k = this.blockUpdatePositions[i] & 255;
					int l = (this.blockUpdatePositions[i] >> 8 & 15) + this.pos.z * 16;
					BlockPos blockPos2 = new BlockPos(j, k, l);
					if (world.getBlockState(blockPos2).getBlock().hasBlockEntity()) {
						this.sendBlockEntityUpdatePacket(world, blockPos2);
					}
				}
			}

			this.blockUpdateCount = 0;
			this.sectionsNeedingUpdateMask = 0;
			if (this.blockLightUpdateBits != 0 || this.skyLightUpdateBits != 0) {
				this.sendPacketToPlayersWatching(
					new LightUpdateS2CPacket(
						worldChunk.getPos(),
						this.lightingProvider,
						this.blockLightUpdateBits & ~this.lightSentWithBlocksBits,
						this.skyLightUpdateBits & ~this.lightSentWithBlocksBits
					),
					true
				);
				int ix = this.blockLightUpdateBits & this.lightSentWithBlocksBits;
				int j = this.skyLightUpdateBits & this.lightSentWithBlocksBits;
				if (ix != 0 || j != 0) {
					this.sendPacketToPlayersWatching(new LightUpdateS2CPacket(worldChunk.getPos(), this.lightingProvider, ix, j), false);
				}

				this.blockLightUpdateBits = 0;
				this.skyLightUpdateBits = 0;
				this.lightSentWithBlocksBits = this.lightSentWithBlocksBits & ~(this.blockLightUpdateBits & this.skyLightUpdateBits);
			}
		}
	}

	private void sendBlockEntityUpdatePacket(World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity != null) {
			BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket = blockEntity.toUpdatePacket();
			if (blockEntityUpdateS2CPacket != null) {
				this.sendPacketToPlayersWatching(blockEntityUpdateS2CPacket, false);
			}
		}
	}

	private void sendPacketToPlayersWatching(Packet<?> packet, boolean bl) {
		this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, bl).forEach(serverPlayerEntity -> serverPlayerEntity.networkHandler.sendPacket(packet));
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunk(ChunkStatus chunkStatus, ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
		int i = chunkStatus.getIndex();
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.chunkByStatus.get(i);
		if (completableFuture != null) {
			Either<Chunk, ChunkHolder.Unloaded> either = (Either<Chunk, ChunkHolder.Unloaded>)completableFuture.getNow(null);
			if (either == null || either.left().isPresent()) {
				return completableFuture;
			}
		}

		if (getTargetGenerationStatus(this.level).isAfter(chunkStatus)) {
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture2 = threadedAnvilChunkStorage.getChunk(this, chunkStatus);
			this.updateChunk(completableFuture2);
			this.chunkByStatus.set(i, completableFuture2);
			return completableFuture2;
		} else {
			return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
		}
	}

	private void updateChunk(CompletableFuture<? extends Either<? extends Chunk, ChunkHolder.Unloaded>> completableFuture) {
		this.chunk = this.chunk.thenCombine(completableFuture, (chunk, either) -> either.map(chunkx -> chunkx, unloaded -> chunk));
	}

	@Environment(EnvType.CLIENT)
	public ChunkHolder.LevelType getLevelType() {
		return getLevelType(this.level);
	}

	public ChunkPos getPos() {
		return this.pos;
	}

	public int getLevel() {
		return this.level;
	}

	public int getLastLevelUpdatedTo() {
		return this.lastLevelUpdatedTo;
	}

	private void setLastLevelUpdatedTo(int i) {
		this.lastLevelUpdatedTo = i;
	}

	public void setLevel(int i) {
		this.level = i;
	}

	protected void update(ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
		ChunkStatus chunkStatus = getTargetGenerationStatus(this.field_16432);
		ChunkStatus chunkStatus2 = getTargetGenerationStatus(this.level);
		boolean bl = this.field_16432 <= ThreadedAnvilChunkStorage.field_18239;
		boolean bl2 = this.level <= ThreadedAnvilChunkStorage.field_18239;
		ChunkHolder.LevelType levelType = getLevelType(this.field_16432);
		ChunkHolder.LevelType levelType2 = getLevelType(this.level);
		boolean bl3 = levelType.isAfter(ChunkHolder.LevelType.TICKING);
		boolean bl4 = levelType2.isAfter(ChunkHolder.LevelType.TICKING);
		if (bl2) {
			for (int i = bl ? chunkStatus.getIndex() + 1 : 0; i <= chunkStatus2.getIndex(); i++) {
				this.getChunk((ChunkStatus)CHUNK_STATUSES.get(i), threadedAnvilChunkStorage);
			}
		}

		if (bl) {
			Either<Chunk, ChunkHolder.Unloaded> either = Either.right(new ChunkHolder.Unloaded() {
				public String toString() {
					return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
				}
			});

			for (int j = bl2 ? chunkStatus2.getIndex() + 1 : 0; j <= chunkStatus.getIndex(); j++) {
				CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.chunkByStatus
					.get(j);
				if (completableFuture != null) {
					completableFuture.complete(either);
				} else {
					this.chunkByStatus.set(j, CompletableFuture.completedFuture(either));
				}
			}
		}

		if (!bl3 && bl4) {
			this.tickingFutureChunk = threadedAnvilChunkStorage.method_17235(this);
			this.updateChunk(this.tickingFutureChunk);
		}

		if (bl3 && !bl4) {
			this.tickingFutureChunk.complete(UNLOADED_WORLD_CHUNK);
			this.tickingFutureChunk = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		boolean bl5 = levelType.isAfter(ChunkHolder.LevelType.ENTITY_TICKING);
		boolean bl6 = levelType2.isAfter(ChunkHolder.LevelType.ENTITY_TICKING);
		if (!bl5 && bl6) {
			if (this.entityTickingFutureChunk != UNLOADED_WORLD_CHUNK_FUTURE) {
				throw new IllegalStateException();
			}

			this.entityTickingFutureChunk = threadedAnvilChunkStorage.method_17247(this.pos);
			this.updateChunk(this.entityTickingFutureChunk);
		}

		if (bl5 && !bl6) {
			this.entityTickingFutureChunk.complete(UNLOADED_WORLD_CHUNK);
			this.entityTickingFutureChunk = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		this.levelUpdateListener.updateLevel(this.pos, this::getLastLevelUpdatedTo, this.level, this::setLastLevelUpdatedTo);
		this.field_16432 = this.level;
	}

	public static ChunkStatus getTargetGenerationStatus(int i) {
		return i <= 33 ? ChunkStatus.FULL : ChunkStatus.getTargetGenerationStatus(i - 33 - 1);
	}

	public static ChunkHolder.LevelType getLevelType(int i) {
		return LEVEL_TYPES[MathHelper.clamp(33 - i, 0, LEVEL_TYPES.length - 1)];
	}

	public static enum LevelType {
		BORDER,
		TICKING,
		ENTITY_TICKING;

		public boolean isAfter(ChunkHolder.LevelType levelType) {
			return this.ordinal() >= levelType.ordinal();
		}
	}

	public interface LevelUpdateListener {
		void updateLevel(ChunkPos chunkPos, IntSupplier intSupplier, int i, IntConsumer intConsumer);
	}

	public interface PlayersWatchingChunkProvider {
		Stream<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean bl);
	}

	public interface Unloaded {
		ChunkHolder.Unloaded INSTANCE = new ChunkHolder.Unloaded() {
			public String toString() {
				return "UNLOADED";
			}
		};
	}
}
