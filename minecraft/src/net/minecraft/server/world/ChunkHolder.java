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
import javax.annotation.Nullable;
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
	private final HeightLimitView field_26929;
	private volatile CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private volatile CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private volatile CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
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
	private final ChunkHolder.LevelUpdateListener levelUpdateListener;
	private final ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider;
	private boolean accessible;
	private boolean field_26744;
	private CompletableFuture<Void> field_26930 = CompletableFuture.completedFuture(null);

	public ChunkHolder(
		ChunkPos pos,
		int level,
		HeightLimitView heightLimitView,
		LightingProvider lightingProvider,
		ChunkHolder.LevelUpdateListener levelUpdateListener,
		ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider
	) {
		this.pos = pos;
		this.field_26929 = heightLimitView;
		this.lightingProvider = lightingProvider;
		this.levelUpdateListener = levelUpdateListener;
		this.playersWatchingChunkProvider = playersWatchingChunkProvider;
		this.lastTickLevel = ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
		this.level = this.lastTickLevel;
		this.completedLevel = this.lastTickLevel;
		this.setLevel(level);
		this.blockUpdatesBySection = new ShortSet[heightLimitView.getSections()];
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getFutureFor(ChunkStatus leastStatus) {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.futuresByStatus
			.get(leastStatus.getIndex());
		return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getValidFutureFor(ChunkStatus leastStatus) {
		return getTargetStatusForLevel(this.level).isAtLeast(leastStatus) ? this.getFutureFor(leastStatus) : UNLOADED_CHUNK_FUTURE;
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> getTickingFuture() {
		return this.tickingFuture;
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> getEntityTickingFuture() {
		return this.entityTickingFuture;
	}

	public CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> getAccessibleFuture() {
		return this.accessibleFuture;
	}

	@Nullable
	public WorldChunk getWorldChunk() {
		CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = this.getTickingFuture();
		Either<WorldChunk, ChunkHolder.Unloaded> either = (Either<WorldChunk, ChunkHolder.Unloaded>)completableFuture.getNow(null);
		return either == null ? null : (WorldChunk)either.left().orElse(null);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public ChunkStatus getCurrentStatus() {
		for (int i = CHUNK_STATUSES.size() - 1; i >= 0; i--) {
			ChunkStatus chunkStatus = (ChunkStatus)CHUNK_STATUSES.get(i);
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getFutureFor(chunkStatus);
			if (((Either)completableFuture.getNow(UNLOADED_CHUNK)).left().isPresent()) {
				return chunkStatus;
			}
		}

		return null;
	}

	@Nullable
	public Chunk getCurrentChunk() {
		for (int i = CHUNK_STATUSES.size() - 1; i >= 0; i--) {
			ChunkStatus chunkStatus = (ChunkStatus)CHUNK_STATUSES.get(i);
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = this.getFutureFor(chunkStatus);
			if (!completableFuture.isCompletedExceptionally()) {
				Optional<Chunk> optional = ((Either)completableFuture.getNow(UNLOADED_CHUNK)).left();
				if (optional.isPresent()) {
					return (Chunk)optional.get();
				}
			}
		}

		return null;
	}

	public CompletableFuture<Chunk> getSavingFuture() {
		return this.savingFuture;
	}

	public void markForBlockUpdate(BlockPos blockPos) {
		WorldChunk worldChunk = this.getWorldChunk();
		if (worldChunk != null) {
			int i = this.field_26929.getSectionIndex(blockPos.getY());
			if (this.blockUpdatesBySection[i] == null) {
				this.pendingBlockUpdates = true;
				this.blockUpdatesBySection[i] = new ShortArraySet();
			}

			this.blockUpdatesBySection[i].add(ChunkSectionPos.packLocal(blockPos));
		}
	}

	/**
	 * @param y chunk section y coordinate
	 */
	public void markForLightUpdate(LightType lightType, int y) {
		WorldChunk worldChunk = this.getWorldChunk();
		if (worldChunk != null) {
			worldChunk.setShouldSave(true);
			int i = this.lightingProvider.method_31929();
			int j = this.lightingProvider.method_31930();
			if (y >= i && y <= j) {
				int k = y - i;
				if (lightType == LightType.SKY) {
					this.skyLightUpdateBits.set(k);
				} else {
					this.blockLightUpdateBits.set(k);
				}
			}
		}
	}

	public void flushUpdates(WorldChunk worldChunk) {
		if (this.pendingBlockUpdates || !this.skyLightUpdateBits.isEmpty() || !this.blockLightUpdateBits.isEmpty()) {
			World world = worldChunk.getWorld();
			int i = 0;

			for (int j = 0; j < this.blockUpdatesBySection.length; j++) {
				i += this.blockUpdatesBySection[j] != null ? this.blockUpdatesBySection[j].size() : 0;
			}

			this.field_26744 |= i >= 64;
			if (!this.skyLightUpdateBits.isEmpty() || !this.blockLightUpdateBits.isEmpty()) {
				this.sendPacketToPlayersWatching(
					new LightUpdateS2CPacket(worldChunk.getPos(), this.lightingProvider, this.skyLightUpdateBits, this.blockLightUpdateBits, true), !this.field_26744
				);
				this.skyLightUpdateBits.clear();
				this.blockLightUpdateBits.clear();
			}

			for (int j = 0; j < this.blockUpdatesBySection.length; j++) {
				ShortSet shortSet = this.blockUpdatesBySection[j];
				if (shortSet != null) {
					int k = this.field_26929.getSection(j);
					ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(worldChunk.getPos(), k);
					if (shortSet.size() == 1) {
						BlockPos blockPos = chunkSectionPos.unpackBlockPos(shortSet.iterator().nextShort());
						BlockState blockState = world.getBlockState(blockPos);
						this.sendPacketToPlayersWatching(new BlockUpdateS2CPacket(blockPos, blockState), false);
						this.tryUpdateBlockEntityAt(world, blockPos, blockState);
					} else {
						ChunkSection chunkSection = worldChunk.getSectionArray()[j];
						ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket = new ChunkDeltaUpdateS2CPacket(chunkSectionPos, shortSet, chunkSection, this.field_26744);
						this.sendPacketToPlayersWatching(chunkDeltaUpdateS2CPacket, false);
						chunkDeltaUpdateS2CPacket.visitUpdates((blockPos, blockState) -> this.tryUpdateBlockEntityAt(world, blockPos, blockState));
					}

					this.blockUpdatesBySection[j] = null;
				}
			}

			this.pendingBlockUpdates = false;
		}
	}

	private void tryUpdateBlockEntityAt(World world, BlockPos blockPos, BlockState blockState) {
		if (blockState.hasBlockEntity()) {
			this.sendBlockEntityUpdatePacket(world, blockPos);
		}
	}

	private void sendBlockEntityUpdatePacket(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null) {
			BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket = blockEntity.toUpdatePacket();
			if (blockEntityUpdateS2CPacket != null) {
				this.sendPacketToPlayersWatching(blockEntityUpdateS2CPacket, false);
			}
		}
	}

	private void sendPacketToPlayersWatching(Packet<?> packet, boolean onlyOnWatchDistanceEdge) {
		this.playersWatchingChunkProvider
			.getPlayersWatchingChunk(this.pos, onlyOnWatchDistanceEdge)
			.forEach(serverPlayerEntity -> serverPlayerEntity.networkHandler.sendPacket(packet));
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkAt(ChunkStatus targetStatus, ThreadedAnvilChunkStorage chunkStorage) {
		int i = targetStatus.getIndex();
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.futuresByStatus
			.get(i);
		if (completableFuture != null) {
			Either<Chunk, ChunkHolder.Unloaded> either = (Either<Chunk, ChunkHolder.Unloaded>)completableFuture.getNow(null);
			if (either == null || either.left().isPresent()) {
				return completableFuture;
			}
		}

		if (getTargetStatusForLevel(this.level).isAtLeast(targetStatus)) {
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture2 = chunkStorage.getChunk(this, targetStatus);
			this.combineSavingFuture(completableFuture2);
			this.futuresByStatus.set(i, completableFuture2);
			return completableFuture2;
		} else {
			return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
		}
	}

	private void combineSavingFuture(CompletableFuture<? extends Either<? extends Chunk, ChunkHolder.Unloaded>> then) {
		this.savingFuture = this.savingFuture.thenCombine(then, (chunk, either) -> either.map(chunkx -> chunkx, unloaded -> chunk));
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

	private void setCompletedLevel(int level) {
		this.completedLevel = level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	private void method_31409(
		ThreadedAnvilChunkStorage threadedAnvilChunkStorage,
		CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture,
		Executor executor,
		ChunkHolder.LevelType levelType
	) {
		this.field_26930.cancel(false);
		CompletableFuture<Void> completableFuture2 = new CompletableFuture();
		completableFuture2.thenRunAsync(() -> threadedAnvilChunkStorage.method_31414(this.pos, levelType), executor);
		this.field_26930 = completableFuture2;
		completableFuture.thenAccept(either -> either.ifLeft(worldChunk -> completableFuture2.complete(null)));
	}

	private void method_31408(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, ChunkHolder.LevelType levelType) {
		this.field_26930.cancel(false);
		threadedAnvilChunkStorage.method_31414(this.pos, levelType);
	}

	protected void tick(ThreadedAnvilChunkStorage chunkStorage, Executor executor) {
		ChunkStatus chunkStatus = getTargetStatusForLevel(this.lastTickLevel);
		ChunkStatus chunkStatus2 = getTargetStatusForLevel(this.level);
		boolean bl = this.lastTickLevel <= ThreadedAnvilChunkStorage.MAX_LEVEL;
		boolean bl2 = this.level <= ThreadedAnvilChunkStorage.MAX_LEVEL;
		ChunkHolder.LevelType levelType = getLevelType(this.lastTickLevel);
		ChunkHolder.LevelType levelType2 = getLevelType(this.level);
		if (bl) {
			Either<Chunk, ChunkHolder.Unloaded> either = Either.right(new ChunkHolder.Unloaded() {
				public String toString() {
					return "Unloaded ticket level " + ChunkHolder.this.pos;
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

		boolean bl3 = levelType.isAfter(ChunkHolder.LevelType.BORDER);
		boolean bl4 = levelType2.isAfter(ChunkHolder.LevelType.BORDER);
		this.accessible |= bl4;
		if (!bl3 && bl4) {
			this.accessibleFuture = chunkStorage.method_31417(this);
			this.method_31409(chunkStorage, this.accessibleFuture, executor, ChunkHolder.LevelType.BORDER);
			this.combineSavingFuture(this.accessibleFuture);
		}

		if (bl3 && !bl4) {
			CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = this.accessibleFuture;
			this.accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
			this.combineSavingFuture(completableFuture.thenApply(either -> either.ifLeft(chunkStorage::enableTickSchedulers)));
		}

		boolean bl5 = levelType.isAfter(ChunkHolder.LevelType.TICKING);
		boolean bl6 = levelType2.isAfter(ChunkHolder.LevelType.TICKING);
		if (!bl5 && bl6) {
			this.tickingFuture = chunkStorage.makeChunkTickable(this);
			this.method_31409(chunkStorage, this.tickingFuture, executor, ChunkHolder.LevelType.TICKING);
			this.combineSavingFuture(this.tickingFuture);
		}

		if (bl5 && !bl6) {
			this.tickingFuture.complete(UNLOADED_WORLD_CHUNK);
			this.tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		boolean bl7 = levelType.isAfter(ChunkHolder.LevelType.ENTITY_TICKING);
		boolean bl8 = levelType2.isAfter(ChunkHolder.LevelType.ENTITY_TICKING);
		if (!bl7 && bl8) {
			if (this.entityTickingFuture != UNLOADED_WORLD_CHUNK_FUTURE) {
				throw (IllegalStateException)Util.throwOrPause(new IllegalStateException());
			}

			this.entityTickingFuture = chunkStorage.makeChunkEntitiesTickable(this.pos);
			this.method_31409(chunkStorage, this.entityTickingFuture, executor, ChunkHolder.LevelType.ENTITY_TICKING);
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
		return level < 33 ? ChunkStatus.FULL : ChunkStatus.byDistanceFromFull(level - 33);
	}

	public static ChunkHolder.LevelType getLevelType(int distance) {
		return LEVEL_TYPES[MathHelper.clamp(33 - distance + 1, 0, LEVEL_TYPES.length - 1)];
	}

	public boolean isAccessible() {
		return this.accessible;
	}

	public void updateAccessibleStatus() {
		this.accessible = getLevelType(this.level).isAfter(ChunkHolder.LevelType.BORDER);
	}

	public void setCompletedChunk(ReadOnlyChunk readOnlyChunk) {
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

		this.combineSavingFuture(CompletableFuture.completedFuture(Either.left(readOnlyChunk.getWrappedChunk())));
	}

	public static enum LevelType {
		INACCESSIBLE,
		BORDER,
		TICKING,
		ENTITY_TICKING;

		public boolean isAfter(ChunkHolder.LevelType levelType) {
			return this.ordinal() >= levelType.ordinal();
		}
	}

	@FunctionalInterface
	public interface LevelUpdateListener {
		void updateLevel(ChunkPos pos, IntSupplier levelGetter, int targetLevel, IntConsumer levelSetter);
	}

	public interface PlayersWatchingChunkProvider {
		Stream<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge);
	}

	/**
	 * Used to represent a chunk that has not been loaded yet.
	 */
	public interface Unloaded {
		ChunkHolder.Unloaded INSTANCE = new ChunkHolder.Unloaded() {
			public String toString() {
				return "UNLOADED";
			}
		};
	}
}
