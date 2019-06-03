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

public class ChunkHolder {
	public static final Either<Chunk, ChunkHolder.Unloaded> UNLOADED_CHUNK = Either.right(ChunkHolder.Unloaded.INSTANCE);
	public static final CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
	public static final Either<WorldChunk, ChunkHolder.Unloaded> UNLOADED_WORLD_CHUNK = Either.right(ChunkHolder.Unloaded.INSTANCE);
	private static final CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> UNLOADED_WORLD_CHUNK_FUTURE = CompletableFuture.completedFuture(
		UNLOADED_WORLD_CHUNK
	);
	private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
	private static final ChunkHolder.LevelType[] LEVEL_TYPES = ChunkHolder.LevelType.values();
	private final AtomicReferenceArray<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> futuresByStatus = new AtomicReferenceArray(CHUNK_STATUSES.size());
	private volatile CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> borderFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private volatile CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private volatile CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
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
	private final ChunkHolder.LevelUpdateListener levelUpdateListener;
	private final ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider;
	private boolean field_19238;

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
		this.lastTickLevel = ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
		this.level = this.lastTickLevel;
		this.completedLevel = this.lastTickLevel;
		this.setLevel(i);
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getFuture(ChunkStatus chunkStatus) {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.futuresByStatus
			.get(chunkStatus.getIndex());
		return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> getTickingFuture() {
		return this.tickingFuture;
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> getEntityTickingFuture() {
		return this.entityTickingFuture;
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> method_20725() {
		return this.borderFuture;
	}

	@Nullable
	public WorldChunk getWorldChunk() {
		CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = this.getTickingFuture();
		Either<WorldChunk, ChunkHolder.Unloaded> either = (Either<WorldChunk, ChunkHolder.Unloaded>)completableFuture.getNow(null);
		return either == null ? null : (WorldChunk)either.left().orElse(null);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public ChunkStatus getCompletedStatus() {
		for (int i = CHUNK_STATUSES.size() - 1; i >= 0; i--) {
			ChunkStatus chunkStatus = (ChunkStatus)CHUNK_STATUSES.get(i);
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getFuture(chunkStatus);
			if (((Either)completableFuture.getNow(UNLOADED_CHUNK)).left().isPresent()) {
				return chunkStatus;
			}
		}

		return null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Chunk getCompletedChunk() {
		for (int i = CHUNK_STATUSES.size() - 1; i >= 0; i--) {
			ChunkStatus chunkStatus = (ChunkStatus)CHUNK_STATUSES.get(i);
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getFuture(chunkStatus);
			if (!completableFuture.isCompletedExceptionally()) {
				Optional<Chunk> optional = ((Either)completableFuture.getNow(UNLOADED_CHUNK)).left();
				if (optional.isPresent()) {
					return (Chunk)optional.get();
				}
			}
		}

		return null;
	}

	public CompletableFuture<Chunk> getFuture() {
		return this.future;
	}

	public void markForBlockUpdate(int i, int j, int k) {
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

	public void markForLightUpdate(LightType lightType, int i) {
		WorldChunk worldChunk = this.getWorldChunk();
		if (worldChunk != null) {
			worldChunk.setShouldSave(true);
			if (lightType == LightType.field_9284) {
				this.skyLightUpdateBits |= 1 << i - -1;
			} else {
				this.blockLightUpdateBits |= 1 << i - -1;
			}
		}
	}

	public void flushUpdates(WorldChunk worldChunk) {
		if (this.blockUpdateCount != 0 || this.skyLightUpdateBits != 0 || this.blockLightUpdateBits != 0) {
			World world = worldChunk.getWorld();
			if (this.blockUpdateCount == 64) {
				this.lightSentWithBlocksBits = -1;
			}

			if (this.skyLightUpdateBits != 0 || this.blockLightUpdateBits != 0) {
				this.sendPacketToPlayersWatching(
					new LightUpdateS2CPacket(
						worldChunk.getPos(),
						this.lightingProvider,
						this.skyLightUpdateBits & ~this.lightSentWithBlocksBits,
						this.blockLightUpdateBits & ~this.lightSentWithBlocksBits
					),
					true
				);
				int i = this.skyLightUpdateBits & this.lightSentWithBlocksBits;
				int j = this.blockLightUpdateBits & this.lightSentWithBlocksBits;
				if (i != 0 || j != 0) {
					this.sendPacketToPlayersWatching(new LightUpdateS2CPacket(worldChunk.getPos(), this.lightingProvider, i, j), false);
				}

				this.skyLightUpdateBits = 0;
				this.blockLightUpdateBits = 0;
				this.lightSentWithBlocksBits = this.lightSentWithBlocksBits & ~(this.skyLightUpdateBits & this.blockLightUpdateBits);
			}

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

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> createFuture(ChunkStatus chunkStatus, ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
		int i = chunkStatus.getIndex();
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.futuresByStatus
			.get(i);
		if (completableFuture != null) {
			Either<Chunk, ChunkHolder.Unloaded> either = (Either<Chunk, ChunkHolder.Unloaded>)completableFuture.getNow(null);
			if (either == null || either.left().isPresent()) {
				return completableFuture;
			}
		}

		if (getTargetGenerationStatus(this.level).isAtLeast(chunkStatus)) {
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture2 = threadedAnvilChunkStorage.createChunkFuture(this, chunkStatus);
			this.updateFuture(completableFuture2);
			this.futuresByStatus.set(i, completableFuture2);
			return completableFuture2;
		} else {
			return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
		}
	}

	private void updateFuture(CompletableFuture<? extends Either<? extends Chunk, ChunkHolder.Unloaded>> completableFuture) {
		this.future = this.future.thenCombine(completableFuture, (chunk, either) -> either.map(chunkx -> chunkx, unloaded -> chunk));
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
		ChunkStatus chunkStatus = getTargetGenerationStatus(this.lastTickLevel);
		ChunkStatus chunkStatus2 = getTargetGenerationStatus(this.level);
		boolean bl = this.lastTickLevel <= ThreadedAnvilChunkStorage.MAX_LEVEL;
		boolean bl2 = this.level <= ThreadedAnvilChunkStorage.MAX_LEVEL;
		ChunkHolder.LevelType levelType = getLevelType(this.lastTickLevel);
		ChunkHolder.LevelType levelType2 = getLevelType(this.level);
		if (bl) {
			Either<Chunk, ChunkHolder.Unloaded> either = Either.right(new ChunkHolder.Unloaded() {
				public String toString() {
					return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
				}
			});

			for (int i = bl2 ? chunkStatus2.getIndex() + 1 : 0; i <= chunkStatus.getIndex(); i++) {
				CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.futuresByStatus
					.get(i);
				if (completableFuture != null) {
					completableFuture.complete(either);
				} else {
					this.futuresByStatus.set(i, CompletableFuture.completedFuture(either));
				}
			}
		}

		boolean bl3 = levelType.isAfter(ChunkHolder.LevelType.field_13876);
		boolean bl4 = levelType2.isAfter(ChunkHolder.LevelType.field_13876);
		this.field_19238 |= bl4;
		if (!bl3 && bl4) {
			this.borderFuture = threadedAnvilChunkStorage.createBorderFuture(this);
			this.updateFuture(this.borderFuture);
		}

		if (bl3 && !bl4) {
			CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = this.borderFuture;
			this.borderFuture = UNLOADED_WORLD_CHUNK_FUTURE;
			this.updateFuture(completableFuture.thenApply(either -> either.ifLeft(threadedAnvilChunkStorage::method_20576)));
		}

		boolean bl5 = levelType.isAfter(ChunkHolder.LevelType.field_13875);
		boolean bl6 = levelType2.isAfter(ChunkHolder.LevelType.field_13875);
		if (!bl5 && bl6) {
			this.tickingFuture = threadedAnvilChunkStorage.createTickingFuture(this);
			this.updateFuture(this.tickingFuture);
		}

		if (bl5 && !bl6) {
			this.tickingFuture.complete(UNLOADED_WORLD_CHUNK);
			this.tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		boolean bl7 = levelType.isAfter(ChunkHolder.LevelType.field_13877);
		boolean bl8 = levelType2.isAfter(ChunkHolder.LevelType.field_13877);
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
		return i < 33 ? ChunkStatus.field_12803 : ChunkStatus.getTargetGenerationStatus(i - 33);
	}

	public static ChunkHolder.LevelType getLevelType(int i) {
		return LEVEL_TYPES[MathHelper.clamp(33 - i + 1, 0, LEVEL_TYPES.length - 1)];
	}

	public boolean method_20384() {
		return this.field_19238;
	}

	public void method_20385() {
		this.field_19238 = getLevelType(this.level).isAfter(ChunkHolder.LevelType.field_13876);
	}

	public void method_20456(ReadOnlyChunk readOnlyChunk) {
		for (int i = 0; i < this.futuresByStatus.length(); i++) {
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.futuresByStatus
				.get(i);
			if (completableFuture != null) {
				Optional<Chunk> optional = ((Either)completableFuture.getNow(UNLOADED_CHUNK)).left();
				if (optional.isPresent() && optional.get() instanceof ProtoChunk) {
					this.futuresByStatus.set(i, CompletableFuture.completedFuture(Either.left(readOnlyChunk)));
				}
			}
		}

		this.updateFuture(CompletableFuture.completedFuture(Either.left(readOnlyChunk.getWrappedChunk())));
	}

	public static enum LevelType {
		field_19334,
		field_13876,
		field_13875,
		field_13877;

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
