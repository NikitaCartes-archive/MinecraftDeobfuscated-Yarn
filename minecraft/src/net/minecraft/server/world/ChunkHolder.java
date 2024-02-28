package net.minecraft.server.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
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
	public static final OptionalChunk<Chunk> UNLOADED_CHUNK = OptionalChunk.of("Unloaded chunk");
	public static final CompletableFuture<OptionalChunk<Chunk>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
	public static final OptionalChunk<WorldChunk> UNLOADED_WORLD_CHUNK = OptionalChunk.of("Unloaded level chunk");
	public static final OptionalChunk<Chunk> CHUNK_LOADING_NOT_FINISHED = OptionalChunk.of("Not done yet");
	private static final CompletableFuture<OptionalChunk<WorldChunk>> UNLOADED_WORLD_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_WORLD_CHUNK);
	private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.createOrderedList();
	private final AtomicReferenceArray<CompletableFuture<OptionalChunk<Chunk>>> futuresByStatus = new AtomicReferenceArray(CHUNK_STATUSES.size());
	private final HeightLimitView world;
	private volatile CompletableFuture<OptionalChunk<WorldChunk>> accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private volatile CompletableFuture<OptionalChunk<WorldChunk>> tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private volatile CompletableFuture<OptionalChunk<WorldChunk>> entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private CompletableFuture<Chunk> savingFuture = CompletableFuture.completedFuture(null);
	@Nullable
	private final AtomicStack<ChunkHolder.MultithreadAction> actionStack = null;
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
	private CompletableFuture<Void> field_26930 = CompletableFuture.completedFuture(null);
	private CompletableFuture<?> postProcessingFuture = CompletableFuture.completedFuture(null);

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

	public CompletableFuture<OptionalChunk<Chunk>> getFutureFor(ChunkStatus leastStatus) {
		CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.futuresByStatus.get(leastStatus.getIndex());
		return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
	}

	public CompletableFuture<OptionalChunk<Chunk>> getValidFutureFor(ChunkStatus leastStatus) {
		return ChunkLevels.getStatus(this.level).isAtLeast(leastStatus) ? this.getFutureFor(leastStatus) : UNLOADED_CHUNK_FUTURE;
	}

	public CompletableFuture<OptionalChunk<WorldChunk>> getTickingFuture() {
		return this.tickingFuture;
	}

	public CompletableFuture<OptionalChunk<WorldChunk>> getEntityTickingFuture() {
		return this.entityTickingFuture;
	}

	public CompletableFuture<OptionalChunk<WorldChunk>> getAccessibleFuture() {
		return this.accessibleFuture;
	}

	@Nullable
	public WorldChunk getWorldChunk() {
		return (WorldChunk)((OptionalChunk)this.getTickingFuture().getNow(UNLOADED_WORLD_CHUNK)).orElse(null);
	}

	public CompletableFuture<?> getPostProcessingFuture() {
		return this.postProcessingFuture;
	}

	@Nullable
	public WorldChunk getPostProcessedChunk() {
		return !this.postProcessingFuture.isDone() ? null : this.getWorldChunk();
	}

	@Nullable
	public ChunkStatus getCurrentStatus() {
		for (int i = CHUNK_STATUSES.size() - 1; i >= 0; i--) {
			ChunkStatus chunkStatus = (ChunkStatus)CHUNK_STATUSES.get(i);
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = this.getFutureFor(chunkStatus);
			if (((OptionalChunk)completableFuture.getNow(UNLOADED_CHUNK)).isPresent()) {
				return chunkStatus;
			}
		}

		return null;
	}

	@Nullable
	public Chunk getCurrentChunk() {
		for (int i = CHUNK_STATUSES.size() - 1; i >= 0; i--) {
			ChunkStatus chunkStatus = (ChunkStatus)CHUNK_STATUSES.get(i);
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = this.getFutureFor(chunkStatus);
			if (!completableFuture.isCompletedExceptionally()) {
				Chunk chunk = (Chunk)((OptionalChunk)completableFuture.getNow(UNLOADED_CHUNK)).orElse(null);
				if (chunk != null) {
					return chunk;
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
		Chunk chunk = (Chunk)((OptionalChunk)this.getValidFutureFor(ChunkStatus.INITIALIZE_LIGHT).getNow(UNLOADED_CHUNK)).orElse(null);
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

	public void flushUpdates(WorldChunk chunk) {
		if (this.pendingBlockUpdates || !this.skyLightUpdateBits.isEmpty() || !this.blockLightUpdateBits.isEmpty()) {
			World world = chunk.getWorld();
			if (!this.skyLightUpdateBits.isEmpty() || !this.blockLightUpdateBits.isEmpty()) {
				List<ServerPlayerEntity> list = this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, true);
				if (!list.isEmpty()) {
					LightUpdateS2CPacket lightUpdateS2CPacket = new LightUpdateS2CPacket(
						chunk.getPos(), this.lightingProvider, this.skyLightUpdateBits, this.blockLightUpdateBits
					);
					this.sendPacketToPlayers(list, lightUpdateS2CPacket);
				}

				this.skyLightUpdateBits.clear();
				this.blockLightUpdateBits.clear();
			}

			if (this.pendingBlockUpdates) {
				List<ServerPlayerEntity> list = this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, false);

				for (int i = 0; i < this.blockUpdatesBySection.length; i++) {
					ShortSet shortSet = this.blockUpdatesBySection[i];
					if (shortSet != null) {
						this.blockUpdatesBySection[i] = null;
						if (!list.isEmpty()) {
							int j = this.world.sectionIndexToCoord(i);
							ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk.getPos(), j);
							if (shortSet.size() == 1) {
								BlockPos blockPos = chunkSectionPos.unpackBlockPos(shortSet.iterator().nextShort());
								BlockState blockState = world.getBlockState(blockPos);
								this.sendPacketToPlayers(list, new BlockUpdateS2CPacket(blockPos, blockState));
								this.tryUpdateBlockEntityAt(list, world, blockPos, blockState);
							} else {
								ChunkSection chunkSection = chunk.getSection(i);
								ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket = new ChunkDeltaUpdateS2CPacket(chunkSectionPos, shortSet, chunkSection);
								this.sendPacketToPlayers(list, chunkDeltaUpdateS2CPacket);
								chunkDeltaUpdateS2CPacket.visitUpdates((pos, state) -> this.tryUpdateBlockEntityAt(list, world, pos, state));
							}
						}
					}
				}

				this.pendingBlockUpdates = false;
			}
		}
	}

	private void tryUpdateBlockEntityAt(List<ServerPlayerEntity> players, World world, BlockPos pos, BlockState state) {
		if (state.hasBlockEntity()) {
			this.sendBlockEntityUpdatePacket(players, world, pos);
		}
	}

	private void sendBlockEntityUpdatePacket(List<ServerPlayerEntity> players, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null) {
			Packet<?> packet = blockEntity.toUpdatePacket();
			if (packet != null) {
				this.sendPacketToPlayers(players, packet);
			}
		}
	}

	private void sendPacketToPlayers(List<ServerPlayerEntity> players, Packet<?> packet) {
		players.forEach(player -> player.networkHandler.sendPacket(packet));
	}

	public CompletableFuture<OptionalChunk<Chunk>> getChunkAt(ChunkStatus targetStatus, ThreadedAnvilChunkStorage chunkStorage) {
		int i = targetStatus.getIndex();
		CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.futuresByStatus.get(i);
		if (completableFuture != null) {
			OptionalChunk<Chunk> optionalChunk = (OptionalChunk<Chunk>)completableFuture.getNow(CHUNK_LOADING_NOT_FINISHED);
			if (optionalChunk == null) {
				String string = "value in future for status: " + targetStatus + " was incorrectly set to null at chunk: " + this.pos;
				throw chunkStorage.crash(new IllegalStateException("null value previously set for chunk status"), string);
			}

			if (optionalChunk == CHUNK_LOADING_NOT_FINISHED || optionalChunk.isPresent()) {
				return completableFuture;
			}
		}

		if (ChunkLevels.getStatus(this.level).isAtLeast(targetStatus)) {
			CompletableFuture<OptionalChunk<Chunk>> completableFuture2 = chunkStorage.getChunk(this, targetStatus);
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

		this.savingFuture = this.savingFuture.thenCombine(then, (result, thenResult) -> result);
	}

	private void combineSavingFuture(CompletableFuture<? extends OptionalChunk<? extends Chunk>> then, String thenDesc) {
		if (this.actionStack != null) {
			this.actionStack.push(new ChunkHolder.MultithreadAction(Thread.currentThread(), then, thenDesc));
		}

		this.savingFuture = this.savingFuture.thenCombine(then, (chunk, otherChunk) -> OptionalChunk.orElse(otherChunk, chunk));
	}

	public void combinePostProcessingFuture(CompletableFuture<?> postProcessingFuture) {
		if (this.postProcessingFuture.isDone()) {
			this.postProcessingFuture = postProcessingFuture;
		} else {
			this.postProcessingFuture = this.postProcessingFuture.thenCombine(postProcessingFuture, (object, object2) -> null);
		}
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
		CompletableFuture<OptionalChunk<WorldChunk>> completableFuture,
		Executor executor,
		ChunkLevelType chunkLevelType
	) {
		this.field_26930.cancel(false);
		CompletableFuture<Void> completableFuture2 = new CompletableFuture();
		completableFuture2.thenRunAsync(() -> threadedAnvilChunkStorage.onChunkStatusChange(this.pos, chunkLevelType), executor);
		this.field_26930 = completableFuture2;
		completableFuture.thenAccept(optionalChunk -> optionalChunk.ifPresent(worldChunk -> completableFuture2.complete(null)));
	}

	private void method_31408(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, ChunkLevelType chunkLevelType) {
		this.field_26930.cancel(false);
		threadedAnvilChunkStorage.onChunkStatusChange(this.pos, chunkLevelType);
	}

	/**
	 * Updates {@code Futures} indicating the expected loading state of the underlying chunk of this {@code ChunkHolder}.
	 * Note that the method merely makes actual loading process possible, but do not perform these actions.
	 */
	protected void updateFutures(ThreadedAnvilChunkStorage chunkStorage, Executor executor) {
		ChunkStatus chunkStatus = ChunkLevels.getStatus(this.lastTickLevel);
		ChunkStatus chunkStatus2 = ChunkLevels.getStatus(this.level);
		boolean bl = ChunkLevels.isAccessible(this.lastTickLevel);
		boolean bl2 = ChunkLevels.isAccessible(this.level);
		ChunkLevelType chunkLevelType = ChunkLevels.getType(this.lastTickLevel);
		ChunkLevelType chunkLevelType2 = ChunkLevels.getType(this.level);
		if (bl) {
			OptionalChunk<Chunk> optionalChunk = OptionalChunk.of((Supplier<String>)(() -> "Unloaded ticket level " + this.pos));

			for (int i = bl2 ? chunkStatus2.getIndex() + 1 : 0; i <= chunkStatus.getIndex(); i++) {
				CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.futuresByStatus.get(i);
				if (completableFuture == null) {
					this.futuresByStatus.set(i, CompletableFuture.completedFuture(optionalChunk));
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
			CompletableFuture<OptionalChunk<Chunk>> completableFuture = (CompletableFuture<OptionalChunk<Chunk>>)this.futuresByStatus.get(i);
			if (completableFuture != null) {
				Chunk chunk2 = (Chunk)((OptionalChunk)completableFuture.getNow(UNLOADED_CHUNK)).orElse(null);
				if (chunk2 instanceof ProtoChunk) {
					this.futuresByStatus.set(i, CompletableFuture.completedFuture(OptionalChunk.of(chunk)));
				}
			}
		}

		this.combineSavingFuture(CompletableFuture.completedFuture(OptionalChunk.of(chunk.getWrappedChunk())), "replaceProto");
	}

	public List<Pair<ChunkStatus, CompletableFuture<OptionalChunk<Chunk>>>> collectFuturesByStatus() {
		List<Pair<ChunkStatus, CompletableFuture<OptionalChunk<Chunk>>>> list = new ArrayList();

		for (int i = 0; i < CHUNK_STATUSES.size(); i++) {
			list.add(Pair.of((ChunkStatus)CHUNK_STATUSES.get(i), (CompletableFuture)this.futuresByStatus.get(i)));
		}

		return list;
	}

	@FunctionalInterface
	public interface LevelUpdateListener {
		void updateLevel(ChunkPos pos, IntSupplier levelGetter, int targetLevel, IntConsumer levelSetter);
	}

	static record MultithreadAction(Thread thread, CompletableFuture<?> action, String actionDesc) {
	}

	public interface PlayersWatchingChunkProvider {
		List<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge);
	}
}
