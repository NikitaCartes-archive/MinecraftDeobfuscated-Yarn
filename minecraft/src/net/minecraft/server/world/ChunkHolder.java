package net.minecraft.server.world;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.AtomicStack;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;
import net.minecraft.world.chunk.light.LightingProvider;

public class ChunkHolder {
	public static final Either<Chunk, ChunkHolder.Unloaded> UNLOADED_CHUNK = Either.right(ChunkHolder.Unloaded.INSTANCE);
	public static final CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
	public static final Either<WorldChunk, ChunkHolder.Unloaded> UNLOADED_WORLD_CHUNK = Either.right(ChunkHolder.Unloaded.INSTANCE);
	private static final Either<Chunk, ChunkHolder.Unloaded> field_36388 = Either.right(ChunkHolder.Unloaded.INSTANCE);
	private static final CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> UNLOADED_WORLD_CHUNK_FUTURE = CompletableFuture.completedFuture(
		UNLOADED_WORLD_CHUNK
	);
	private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
	private final AtomicReferenceArray<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> futuresByStatus = new AtomicReferenceArray(CHUNK_STATUSES.size());
	private final HeightLimitView world;
	private volatile CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private volatile CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private volatile CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private CompletableFuture<Chunk> savingFuture = CompletableFuture.completedFuture(null);
	@Nullable
	private final AtomicStack<ChunkHolder.MultithreadAction> actionStack = null;
	private int lastTickLevel;
	private int level;
	private int completedLevel;
	final ChunkPos pos;
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
	private CompletableFuture<Void> field_26930 = CompletableFuture.completedFuture(null);

	public ChunkHolder(
		ChunkPos pos,
		int level,
		HeightLimitView world,
		LightingProvider lightingProvider,
		ChunkHolder.LevelUpdateListener levelUpdateListener,
		ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider
	) {
		this.pos = pos;
		this.world = world;
		this.lightingProvider = lightingProvider;
		this.levelUpdateListener = levelUpdateListener;
		this.playersWatchingChunkProvider = playersWatchingChunkProvider;
		this.lastTickLevel = ChunkLevels.INACCESSIBLE + 1;
		this.level = this.lastTickLevel;
		this.completedLevel = this.lastTickLevel;
		this.setLevel(level);
		this.blockUpdatesBySection = new ShortSet[world.countVerticalSections()];
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getFutureFor(ChunkStatus leastStatus) {
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.futuresByStatus
			.get(leastStatus.getIndex());
		return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getValidFutureFor(ChunkStatus leastStatus) {
		return ChunkLevels.getStatus(this.level).isAtLeast(leastStatus) ? this.getFutureFor(leastStatus) : UNLOADED_CHUNK_FUTURE;
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
	public WorldChunk method_41205() {
		CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = this.getAccessibleFuture();
		Either<WorldChunk, ChunkHolder.Unloaded> either = (Either<WorldChunk, ChunkHolder.Unloaded>)completableFuture.getNow(null);
		return either == null ? null : (WorldChunk)either.left().orElse(null);
	}

	@Nullable
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

	public void markForBlockUpdate(BlockPos pos) {
		WorldChunk worldChunk = this.getWorldChunk();
		if (worldChunk != null) {
			int i = this.world.getSectionIndex(pos.getY());
			if (this.blockUpdatesBySection[i] == null) {
				this.pendingBlockUpdates = true;
				this.blockUpdatesBySection[i] = new ShortOpenHashSet();
			}

			this.blockUpdatesBySection[i].add(ChunkSectionPos.packLocal(pos));
		}
	}

	/**
	 * @param y chunk section y coordinate
	 */
	public void markForLightUpdate(LightType lightType, int y) {
		Either<Chunk, ChunkHolder.Unloaded> either = (Either<Chunk, ChunkHolder.Unloaded>)this.getValidFutureFor(ChunkStatus.INITIALIZE_LIGHT).getNow(null);
		if (either != null) {
			Chunk chunk = (Chunk)either.left().orElse(null);
			if (chunk != null) {
				chunk.setNeedsSaving(true);
				WorldChunk worldChunk = this.getWorldChunk();
				if (worldChunk != null) {
					int i = this.lightingProvider.getBottomY();
					int j = this.lightingProvider.getTopY();
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
		}
	}

	public void flushUpdates(WorldChunk chunk) {
		if (this.pendingBlockUpdates || !this.skyLightUpdateBits.isEmpty() || !this.blockLightUpdateBits.isEmpty()) {
			World world = chunk.getWorld();
			if (!this.skyLightUpdateBits.isEmpty() || !this.blockLightUpdateBits.isEmpty()) {
				this.sendPacketToPlayersWatching(new LightUpdateS2CPacket(chunk.getPos(), this.lightingProvider, this.skyLightUpdateBits, this.blockLightUpdateBits), true);
				this.skyLightUpdateBits.clear();
				this.blockLightUpdateBits.clear();
			}

			for (int i = 0; i < this.blockUpdatesBySection.length; i++) {
				ShortSet shortSet = this.blockUpdatesBySection[i];
				if (shortSet != null) {
					int j = this.world.sectionIndexToCoord(i);
					ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk.getPos(), j);
					if (shortSet.size() == 1) {
						BlockPos blockPos = chunkSectionPos.unpackBlockPos(shortSet.iterator().nextShort());
						BlockState blockState = world.getBlockState(blockPos);
						this.sendPacketToPlayersWatching(new BlockUpdateS2CPacket(blockPos, blockState), false);
						this.tryUpdateBlockEntityAt(world, blockPos, blockState);
					} else {
						ChunkSection chunkSection = chunk.getSection(i);
						ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket = new ChunkDeltaUpdateS2CPacket(chunkSectionPos, shortSet, chunkSection);
						this.sendPacketToPlayersWatching(chunkDeltaUpdateS2CPacket, false);
						chunkDeltaUpdateS2CPacket.visitUpdates((pos, state) -> this.tryUpdateBlockEntityAt(world, pos, state));
					}

					this.blockUpdatesBySection[i] = null;
				}
			}

			this.pendingBlockUpdates = false;
		}
	}

	private void tryUpdateBlockEntityAt(World world, BlockPos pos, BlockState state) {
		if (state.hasBlockEntity()) {
			this.sendBlockEntityUpdatePacket(world, pos);
		}
	}

	private void sendBlockEntityUpdatePacket(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null) {
			Packet<?> packet = blockEntity.toUpdatePacket();
			if (packet != null) {
				this.sendPacketToPlayersWatching(packet, false);
			}
		}
	}

	private void sendPacketToPlayersWatching(Packet<?> packet, boolean onlyOnWatchDistanceEdge) {
		this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, onlyOnWatchDistanceEdge).forEach(player -> player.networkHandler.sendPacket(packet));
	}

	public CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> getChunkAt(ChunkStatus targetStatus, ThreadedAnvilChunkStorage chunkStorage) {
		int i = targetStatus.getIndex();
		CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.futuresByStatus
			.get(i);
		if (completableFuture != null) {
			Either<Chunk, ChunkHolder.Unloaded> either = (Either<Chunk, ChunkHolder.Unloaded>)completableFuture.getNow(field_36388);
			if (either == null) {
				String string = "value in future for status: " + targetStatus + " was incorrectly set to null at chunk: " + this.pos;
				throw chunkStorage.crash(new IllegalStateException("null value previously set for chunk status"), string);
			}

			if (either == field_36388 || either.right().isEmpty()) {
				return completableFuture;
			}
		}

		if (ChunkLevels.getStatus(this.level).isAtLeast(targetStatus)) {
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture2 = chunkStorage.getChunk(this, targetStatus);
			this.combineSavingFuture(completableFuture2, "schedule " + targetStatus);
			this.futuresByStatus.set(i, completableFuture2);
			return completableFuture2;
		} else {
			return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
		}
	}

	protected void combineSavingFuture(String thenDesc, CompletableFuture<?> then) {
		if (this.actionStack != null) {
			this.actionStack.push(new ChunkHolder.MultithreadAction(Thread.currentThread(), then, thenDesc));
		}

		this.savingFuture = this.savingFuture.thenCombine(then, (chunk, object) -> chunk);
	}

	private void combineSavingFuture(CompletableFuture<? extends Either<? extends Chunk, ChunkHolder.Unloaded>> then, String thenDesc) {
		if (this.actionStack != null) {
			this.actionStack.push(new ChunkHolder.MultithreadAction(Thread.currentThread(), then, thenDesc));
		}

		this.savingFuture = this.savingFuture.thenCombine(then, (chunk, either) -> either.map(chunkx -> chunkx, unloaded -> chunk));
	}

	public ChunkLevelType getLevelType() {
		return ChunkLevels.getType(this.level);
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
		ChunkLevelType chunkLevelType
	) {
		this.field_26930.cancel(false);
		CompletableFuture<Void> completableFuture2 = new CompletableFuture();
		completableFuture2.thenRunAsync(() -> threadedAnvilChunkStorage.onChunkStatusChange(this.pos, chunkLevelType), executor);
		this.field_26930 = completableFuture2;
		completableFuture.thenAccept(either -> either.ifLeft(worldChunk -> completableFuture2.complete(null)));
	}

	private void method_31408(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, ChunkLevelType chunkLevelType) {
		this.field_26930.cancel(false);
		threadedAnvilChunkStorage.onChunkStatusChange(this.pos, chunkLevelType);
	}

	protected void tick(ThreadedAnvilChunkStorage chunkStorage, Executor executor) {
		ChunkStatus chunkStatus = ChunkLevels.getStatus(this.lastTickLevel);
		ChunkStatus chunkStatus2 = ChunkLevels.getStatus(this.level);
		boolean bl = ChunkLevels.isAccessible(this.lastTickLevel);
		boolean bl2 = ChunkLevels.isAccessible(this.level);
		ChunkLevelType chunkLevelType = ChunkLevels.getType(this.lastTickLevel);
		ChunkLevelType chunkLevelType2 = ChunkLevels.getType(this.level);
		if (bl) {
			Either<Chunk, ChunkHolder.Unloaded> either = Either.right(new ChunkHolder.Unloaded() {
				public String toString() {
					return "Unloaded ticket level " + ChunkHolder.this.pos;
				}
			});

			for (int i = bl2 ? chunkStatus2.getIndex() + 1 : 0; i <= chunkStatus.getIndex(); i++) {
				CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.futuresByStatus
					.get(i);
				if (completableFuture == null) {
					this.futuresByStatus.set(i, CompletableFuture.completedFuture(either));
				}
			}
		}

		boolean bl3 = chunkLevelType.isAfter(ChunkLevelType.FULL);
		boolean bl4 = chunkLevelType2.isAfter(ChunkLevelType.FULL);
		this.accessible |= bl4;
		if (!bl3 && bl4) {
			this.accessibleFuture = chunkStorage.makeChunkAccessible(this);
			this.method_31409(chunkStorage, this.accessibleFuture, executor, ChunkLevelType.FULL);
			this.combineSavingFuture(this.accessibleFuture, "full");
		}

		if (bl3 && !bl4) {
			this.accessibleFuture.complete(UNLOADED_WORLD_CHUNK);
			this.accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		boolean bl5 = chunkLevelType.isAfter(ChunkLevelType.BLOCK_TICKING);
		boolean bl6 = chunkLevelType2.isAfter(ChunkLevelType.BLOCK_TICKING);
		if (!bl5 && bl6) {
			this.tickingFuture = chunkStorage.makeChunkTickable(this);
			this.method_31409(chunkStorage, this.tickingFuture, executor, ChunkLevelType.BLOCK_TICKING);
			this.combineSavingFuture(this.tickingFuture, "ticking");
		}

		if (bl5 && !bl6) {
			this.tickingFuture.complete(UNLOADED_WORLD_CHUNK);
			this.tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		boolean bl7 = chunkLevelType.isAfter(ChunkLevelType.ENTITY_TICKING);
		boolean bl8 = chunkLevelType2.isAfter(ChunkLevelType.ENTITY_TICKING);
		if (!bl7 && bl8) {
			if (this.entityTickingFuture != UNLOADED_WORLD_CHUNK_FUTURE) {
				throw (IllegalStateException)Util.throwOrPause(new IllegalStateException());
			}

			this.entityTickingFuture = chunkStorage.makeChunkEntitiesTickable(this);
			this.method_31409(chunkStorage, this.entityTickingFuture, executor, ChunkLevelType.ENTITY_TICKING);
			this.combineSavingFuture(this.entityTickingFuture, "entity ticking");
		}

		if (bl7 && !bl8) {
			this.entityTickingFuture.complete(UNLOADED_WORLD_CHUNK);
			this.entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		if (!chunkLevelType2.isAfter(chunkLevelType)) {
			this.method_31408(chunkStorage, chunkLevelType2);
		}

		this.levelUpdateListener.updateLevel(this.pos, this::getCompletedLevel, this.level, this::setCompletedLevel);
		this.lastTickLevel = this.level;
	}

	public boolean isAccessible() {
		return this.accessible;
	}

	public void updateAccessibleStatus() {
		this.accessible = ChunkLevels.getType(this.level).isAfter(ChunkLevelType.FULL);
	}

	public void setCompletedChunk(WrapperProtoChunk chunk) {
		for (int i = 0; i < this.futuresByStatus.length(); i++) {
			CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = (CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>)this.futuresByStatus
				.get(i);
			if (completableFuture != null) {
				Optional<Chunk> optional = ((Either)completableFuture.getNow(UNLOADED_CHUNK)).left();
				if (!optional.isEmpty() && optional.get() instanceof ProtoChunk) {
					this.futuresByStatus.set(i, CompletableFuture.completedFuture(Either.left(chunk)));
				}
			}
		}

		this.combineSavingFuture(CompletableFuture.completedFuture(Either.left(chunk.getWrappedChunk())), "replaceProto");
	}

	public List<Pair<ChunkStatus, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>>> collectFuturesByStatus() {
		List<Pair<ChunkStatus, CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>>> list = new ArrayList();

		for (int i = 0; i < CHUNK_STATUSES.size(); i++) {
			list.add(Pair.of((ChunkStatus)CHUNK_STATUSES.get(i), (CompletableFuture)this.futuresByStatus.get(i)));
		}

		return list;
	}

	@FunctionalInterface
	public interface LevelUpdateListener {
		void updateLevel(ChunkPos pos, IntSupplier levelGetter, int targetLevel, IntConsumer levelSetter);
	}

	static final class MultithreadAction {
		private final Thread thread;
		private final CompletableFuture<?> action;
		private final String actionDesc;

		MultithreadAction(Thread thread, CompletableFuture<?> action, String actionDesc) {
			this.thread = thread;
			this.action = action;
			this.actionDesc = actionDesc;
		}
	}

	public interface PlayersWatchingChunkProvider {
		List<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge);
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
