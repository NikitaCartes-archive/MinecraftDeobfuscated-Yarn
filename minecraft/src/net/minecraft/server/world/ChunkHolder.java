package net.minecraft.server.world;

import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.class_9761;
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
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

public class ChunkHolder extends class_9761 {
	public static final OptionalChunk<WorldChunk> UNLOADED_WORLD_CHUNK = OptionalChunk.of("Unloaded level chunk");
	private static final CompletableFuture<OptionalChunk<WorldChunk>> UNLOADED_WORLD_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_WORLD_CHUNK);
	private final HeightLimitView world;
	private volatile CompletableFuture<OptionalChunk<WorldChunk>> accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private volatile CompletableFuture<OptionalChunk<WorldChunk>> tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private volatile CompletableFuture<OptionalChunk<WorldChunk>> entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
	private int lastTickLevel;
	private int level;
	private int completedLevel;
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
	private CompletableFuture<?> field_26930 = CompletableFuture.completedFuture(null);
	private CompletableFuture<?> postProcessingFuture = CompletableFuture.completedFuture(null);
	private CompletableFuture<?> field_51858 = CompletableFuture.completedFuture(null);

	public ChunkHolder(
		ChunkPos pos,
		int level,
		HeightLimitView world,
		LightingProvider lightingProvider,
		ChunkHolder.LevelUpdateListener levelUpdateListener,
		ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider
	) {
		super(pos);
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

	@Nullable
	public WorldChunk getPostProcessedChunk() {
		return !this.postProcessingFuture.isDone() ? null : this.getWorldChunk();
	}

	public CompletableFuture<?> getPostProcessingFuture() {
		return this.postProcessingFuture;
	}

	public void combinePostProcessingFuture(CompletableFuture<?> postProcessingFuture) {
		if (this.postProcessingFuture.isDone()) {
			this.postProcessingFuture = postProcessingFuture;
		} else {
			this.postProcessingFuture = this.postProcessingFuture.thenCombine(postProcessingFuture, (object, object2) -> null);
		}
	}

	public CompletableFuture<?> getSavingFuture() {
		return this.field_51858;
	}

	public boolean method_60436() {
		return this.method_60470() == 0 && this.field_51858.isDone();
	}

	private void combineSavingFuture(CompletableFuture<?> completableFuture) {
		if (this.field_51858.isDone()) {
			this.field_51858 = completableFuture;
		} else {
			this.field_51858 = this.field_51858.thenCombine(completableFuture, (object, thenResult) -> null);
		}
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
		Chunk chunk = this.method_60463(ChunkStatus.INITIALIZE_LIGHT);
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
				List<ServerPlayerEntity> list = this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.field_51868, true);
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
				List<ServerPlayerEntity> list = this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.field_51868, false);

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

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
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
		completableFuture2.thenRunAsync(() -> threadedAnvilChunkStorage.onChunkStatusChange(this.field_51868, chunkLevelType), executor);
		this.field_26930 = completableFuture2;
		completableFuture.thenAccept(optionalChunk -> optionalChunk.ifPresent(worldChunk -> completableFuture2.complete(null)));
	}

	private void method_31408(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, ChunkLevelType chunkLevelType) {
		this.field_26930.cancel(false);
		threadedAnvilChunkStorage.onChunkStatusChange(this.field_51868, chunkLevelType);
	}

	/**
	 * Updates {@code Futures} indicating the expected loading state of the underlying chunk of this {@code ChunkHolder}.
	 * Note that the method merely makes actual loading process possible, but do not perform these actions.
	 */
	protected void updateFutures(ThreadedAnvilChunkStorage chunkStorage, Executor executor) {
		ChunkLevelType chunkLevelType = ChunkLevels.getType(this.lastTickLevel);
		ChunkLevelType chunkLevelType2 = ChunkLevels.getType(this.level);
		boolean bl = chunkLevelType.isAfter(ChunkLevelType.FULL);
		boolean bl2 = chunkLevelType2.isAfter(ChunkLevelType.FULL);
		this.accessible |= bl2;
		if (!bl && bl2) {
			this.accessibleFuture = chunkStorage.makeChunkAccessible(this);
			this.method_31409(chunkStorage, this.accessibleFuture, executor, ChunkLevelType.FULL);
			this.combineSavingFuture(this.accessibleFuture);
		}

		if (bl && !bl2) {
			this.accessibleFuture.complete(UNLOADED_WORLD_CHUNK);
			this.accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		boolean bl3 = chunkLevelType.isAfter(ChunkLevelType.BLOCK_TICKING);
		boolean bl4 = chunkLevelType2.isAfter(ChunkLevelType.BLOCK_TICKING);
		if (!bl3 && bl4) {
			this.tickingFuture = chunkStorage.makeChunkTickable(this);
			this.method_31409(chunkStorage, this.tickingFuture, executor, ChunkLevelType.BLOCK_TICKING);
			this.combineSavingFuture(this.tickingFuture);
		}

		if (bl3 && !bl4) {
			this.tickingFuture.complete(UNLOADED_WORLD_CHUNK);
			this.tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		boolean bl5 = chunkLevelType.isAfter(ChunkLevelType.ENTITY_TICKING);
		boolean bl6 = chunkLevelType2.isAfter(ChunkLevelType.ENTITY_TICKING);
		if (!bl5 && bl6) {
			if (this.entityTickingFuture != UNLOADED_WORLD_CHUNK_FUTURE) {
				throw (IllegalStateException)Util.throwOrPause(new IllegalStateException());
			}

			this.entityTickingFuture = chunkStorage.makeChunkEntitiesTickable(this);
			this.method_31409(chunkStorage, this.entityTickingFuture, executor, ChunkLevelType.ENTITY_TICKING);
			this.combineSavingFuture(this.entityTickingFuture);
		}

		if (bl5 && !bl6) {
			this.entityTickingFuture.complete(UNLOADED_WORLD_CHUNK);
			this.entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
		}

		if (!chunkLevelType2.isAfter(chunkLevelType)) {
			this.method_31408(chunkStorage, chunkLevelType2);
		}

		this.levelUpdateListener.updateLevel(this.field_51868, this::getCompletedLevel, this.level, this::setCompletedLevel);
		this.lastTickLevel = this.level;
	}

	public boolean isAccessible() {
		return this.accessible;
	}

	public void updateAccessibleStatus() {
		this.accessible = ChunkLevels.getType(this.level).isAfter(ChunkLevelType.FULL);
	}

	@FunctionalInterface
	public interface LevelUpdateListener {
		void updateLevel(ChunkPos pos, IntSupplier levelGetter, int targetLevel, IntConsumer levelSetter);
	}

	public interface PlayersWatchingChunkProvider {
		List<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge);
	}
}
