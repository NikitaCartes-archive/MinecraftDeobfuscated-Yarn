package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.world.ChunkPosDistanceLevelPropagator;
import net.minecraft.world.SimulationDistanceLevelPropagator;
import net.minecraft.world.chunk.WorldChunk;
import org.slf4j.Logger;

public abstract class ChunkTicketManager {
	static final Logger LOGGER = LogUtils.getLogger();
	static final int NEARBY_PLAYER_TICKET_LEVEL = ChunkLevels.getLevelFromType(ChunkLevelType.ENTITY_TICKING);
	private static final int field_29765 = 4;
	final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos = new Long2ObjectOpenHashMap<>();
	final Long2ObjectOpenHashMap<SortedArraySet<ChunkTicket<?>>> ticketsByPosition = new Long2ObjectOpenHashMap<>();
	private final ChunkTicketManager.TicketDistanceLevelPropagator distanceFromTicketTracker = new ChunkTicketManager.TicketDistanceLevelPropagator();
	private final ChunkTicketManager.DistanceFromNearestPlayerTracker distanceFromNearestPlayerTracker = new ChunkTicketManager.DistanceFromNearestPlayerTracker(8);
	private final SimulationDistanceLevelPropagator simulationDistanceTracker = new SimulationDistanceLevelPropagator();
	private final ChunkTicketManager.NearbyChunkTicketUpdater nearbyChunkTicketUpdater = new ChunkTicketManager.NearbyChunkTicketUpdater(32);
	final Set<ChunkHolder> chunkHoldersWithPendingUpdates = new ReferenceOpenHashSet<>();
	final ChunkTaskPrioritySystem levelUpdateListener;
	final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> playerTicketThrottler;
	final MessageListener<ChunkTaskPrioritySystem.UnblockingMessage> playerTicketThrottlerUnblocker;
	final LongSet freshPlayerTicketPositions = new LongOpenHashSet();
	final Executor mainThreadExecutor;
	private long age;
	private int simulationDistance = 10;

	protected ChunkTicketManager(Executor workerExecutor, Executor mainThreadExecutor) {
		MessageListener<Runnable> messageListener = MessageListener.create("player ticket throttler", mainThreadExecutor::execute);
		ChunkTaskPrioritySystem chunkTaskPrioritySystem = new ChunkTaskPrioritySystem(ImmutableList.of(messageListener), workerExecutor, 4);
		this.levelUpdateListener = chunkTaskPrioritySystem;
		this.playerTicketThrottler = chunkTaskPrioritySystem.createExecutor(messageListener, true);
		this.playerTicketThrottlerUnblocker = chunkTaskPrioritySystem.createUnblockingExecutor(messageListener);
		this.mainThreadExecutor = mainThreadExecutor;
	}

	protected void purgeExpiredTickets() {
		this.age++;
		ObjectIterator<Entry<SortedArraySet<ChunkTicket<?>>>> objectIterator = this.ticketsByPosition.long2ObjectEntrySet().fastIterator();

		while (objectIterator.hasNext()) {
			Entry<SortedArraySet<ChunkTicket<?>>> entry = (Entry<SortedArraySet<ChunkTicket<?>>>)objectIterator.next();
			Iterator<ChunkTicket<?>> iterator = ((SortedArraySet)entry.getValue()).iterator();
			boolean bl = false;

			while (iterator.hasNext()) {
				ChunkTicket<?> chunkTicket = (ChunkTicket<?>)iterator.next();
				if (chunkTicket.isExpired(this.age)) {
					iterator.remove();
					bl = true;
					this.simulationDistanceTracker.remove(entry.getLongKey(), chunkTicket);
				}
			}

			if (bl) {
				this.distanceFromTicketTracker.updateLevel(entry.getLongKey(), getLevel((SortedArraySet<ChunkTicket<?>>)entry.getValue()), false);
			}

			if (((SortedArraySet)entry.getValue()).isEmpty()) {
				objectIterator.remove();
			}
		}
	}

	private static int getLevel(SortedArraySet<ChunkTicket<?>> tickets) {
		return !tickets.isEmpty() ? tickets.first().getLevel() : ChunkLevels.INACCESSIBLE + 1;
	}

	protected abstract boolean isUnloaded(long pos);

	@Nullable
	protected abstract ChunkHolder getChunkHolder(long pos);

	@Nullable
	protected abstract ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int i);

	/**
	 * Update the states related to chunk tickets and chunk loading levels, which mainly involves three kind of updates:
	 * <ul>
	 * <li>Add or remove PLAYER tickets when necessary.</li>
	 * <li>Update the expected loading states of chunks depending on their new levels.</li>
	 * <li>Special updates of chunks with PLAYER tickets added recently.</li>
	 * </ul>
	 */
	public boolean update(ServerChunkLoadingManager chunkLoadingManager) {
		this.distanceFromNearestPlayerTracker.updateLevels();
		this.simulationDistanceTracker.updateLevels();
		this.nearbyChunkTicketUpdater.updateLevels();
		int i = Integer.MAX_VALUE - this.distanceFromTicketTracker.update(Integer.MAX_VALUE);
		boolean bl = i != 0;
		if (bl) {
		}

		if (!this.chunkHoldersWithPendingUpdates.isEmpty()) {
			for (ChunkHolder chunkHolder : this.chunkHoldersWithPendingUpdates) {
				chunkHolder.updateStatus(chunkLoadingManager);
			}

			for (ChunkHolder chunkHolder : this.chunkHoldersWithPendingUpdates) {
				chunkHolder.updateFutures(chunkLoadingManager, this.mainThreadExecutor);
			}

			this.chunkHoldersWithPendingUpdates.clear();
			return true;
		} else {
			if (!this.freshPlayerTicketPositions.isEmpty()) {
				LongIterator longIterator = this.freshPlayerTicketPositions.iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					if (this.getTicketSet(l).stream().anyMatch(ticket -> ticket.getType() == ChunkTicketType.PLAYER)) {
						ChunkHolder chunkHolder2 = chunkLoadingManager.getCurrentChunkHolder(l);
						if (chunkHolder2 == null) {
							throw new IllegalStateException();
						}

						CompletableFuture<OptionalChunk<WorldChunk>> completableFuture = chunkHolder2.getEntityTickingFuture();
						completableFuture.thenAccept(
							optionalChunk -> this.mainThreadExecutor.execute(() -> this.playerTicketThrottlerUnblocker.send(ChunkTaskPrioritySystem.createUnblockingMessage(() -> {
									}, l, false)))
						);
					}
				}

				this.freshPlayerTicketPositions.clear();
			}

			return bl;
		}
	}

	void addTicket(long position, ChunkTicket<?> ticket) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = this.getTicketSet(position);
		int i = getLevel(sortedArraySet);
		ChunkTicket<?> chunkTicket = sortedArraySet.addAndGet(ticket);
		chunkTicket.setTickCreated(this.age);
		if (ticket.getLevel() < i) {
			this.distanceFromTicketTracker.updateLevel(position, ticket.getLevel(), true);
		}
	}

	void removeTicket(long pos, ChunkTicket<?> ticket) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = this.getTicketSet(pos);
		if (sortedArraySet.remove(ticket)) {
		}

		if (sortedArraySet.isEmpty()) {
			this.ticketsByPosition.remove(pos);
		}

		this.distanceFromTicketTracker.updateLevel(pos, getLevel(sortedArraySet), false);
	}

	public <T> void addTicketWithLevel(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
		this.addTicket(pos.toLong(), new ChunkTicket<>(type, level, argument));
	}

	public <T> void removeTicketWithLevel(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(type, level, argument);
		this.removeTicket(pos.toLong(), chunkTicket);
	}

	public <T> void addTicket(ChunkTicketType<T> type, ChunkPos pos, int radius, T argument) {
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(type, ChunkLevels.getLevelFromType(ChunkLevelType.FULL) - radius, argument);
		long l = pos.toLong();
		this.addTicket(l, chunkTicket);
		this.simulationDistanceTracker.add(l, chunkTicket);
	}

	public <T> void removeTicket(ChunkTicketType<T> type, ChunkPos pos, int radius, T argument) {
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(type, ChunkLevels.getLevelFromType(ChunkLevelType.FULL) - radius, argument);
		long l = pos.toLong();
		this.removeTicket(l, chunkTicket);
		this.simulationDistanceTracker.remove(l, chunkTicket);
	}

	private SortedArraySet<ChunkTicket<?>> getTicketSet(long position) {
		return this.ticketsByPosition
			.computeIfAbsent(position, (Long2ObjectFunction<? extends SortedArraySet<ChunkTicket<?>>>)(pos -> (SortedArraySet<ChunkTicket<?>>)SortedArraySet.create(4)));
	}

	protected void setChunkForced(ChunkPos pos, boolean forced) {
		ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<>(ChunkTicketType.FORCED, ServerChunkLoadingManager.field_29670, pos);
		long l = pos.toLong();
		if (forced) {
			this.addTicket(l, chunkTicket);
			this.simulationDistanceTracker.add(l, chunkTicket);
		} else {
			this.removeTicket(l, chunkTicket);
			this.simulationDistanceTracker.remove(l, chunkTicket);
		}
	}

	public void handleChunkEnter(ChunkSectionPos pos, ServerPlayerEntity player) {
		ChunkPos chunkPos = pos.toChunkPos();
		long l = chunkPos.toLong();
		this.playersByChunkPos
			.computeIfAbsent(l, (Long2ObjectFunction<? extends ObjectSet<ServerPlayerEntity>>)(sectionPos -> new ObjectOpenHashSet<>()))
			.add(player);
		this.distanceFromNearestPlayerTracker.updateLevel(l, 0, true);
		this.nearbyChunkTicketUpdater.updateLevel(l, 0, true);
		this.simulationDistanceTracker.add(ChunkTicketType.PLAYER, chunkPos, this.getPlayerSimulationLevel(), chunkPos);
	}

	public void handleChunkLeave(ChunkSectionPos pos, ServerPlayerEntity player) {
		ChunkPos chunkPos = pos.toChunkPos();
		long l = chunkPos.toLong();
		ObjectSet<ServerPlayerEntity> objectSet = this.playersByChunkPos.get(l);
		objectSet.remove(player);
		if (objectSet.isEmpty()) {
			this.playersByChunkPos.remove(l);
			this.distanceFromNearestPlayerTracker.updateLevel(l, Integer.MAX_VALUE, false);
			this.nearbyChunkTicketUpdater.updateLevel(l, Integer.MAX_VALUE, false);
			this.simulationDistanceTracker.remove(ChunkTicketType.PLAYER, chunkPos, this.getPlayerSimulationLevel(), chunkPos);
		}
	}

	private int getPlayerSimulationLevel() {
		return Math.max(0, ChunkLevels.getLevelFromType(ChunkLevelType.ENTITY_TICKING) - this.simulationDistance);
	}

	public boolean shouldTickEntities(long chunkPos) {
		return ChunkLevels.shouldTickEntities(this.simulationDistanceTracker.getLevel(chunkPos));
	}

	public boolean shouldTickBlocks(long chunkPos) {
		return ChunkLevels.shouldTickBlocks(this.simulationDistanceTracker.getLevel(chunkPos));
	}

	protected String getTicket(long pos) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = this.ticketsByPosition.get(pos);
		return sortedArraySet != null && !sortedArraySet.isEmpty() ? sortedArraySet.first().toString() : "no_ticket";
	}

	protected void setWatchDistance(int viewDistance) {
		this.nearbyChunkTicketUpdater.setWatchDistance(viewDistance);
	}

	public void setSimulationDistance(int simulationDistance) {
		if (simulationDistance != this.simulationDistance) {
			this.simulationDistance = simulationDistance;
			this.simulationDistanceTracker.updatePlayerTickets(this.getPlayerSimulationLevel());
		}
	}

	public int getTickedChunkCount() {
		this.distanceFromNearestPlayerTracker.updateLevels();
		return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.size();
	}

	public boolean shouldTick(long chunkPos) {
		this.distanceFromNearestPlayerTracker.updateLevels();
		return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.containsKey(chunkPos);
	}

	public LongIterator iterateChunkPosToTick() {
		this.distanceFromNearestPlayerTracker.updateLevels();
		return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.keySet().iterator();
	}

	public String toDumpString() {
		return this.levelUpdateListener.getDebugString();
	}

	private void dump(String path) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(path));

			try {
				for (Entry<SortedArraySet<ChunkTicket<?>>> entry : this.ticketsByPosition.long2ObjectEntrySet()) {
					ChunkPos chunkPos = new ChunkPos(entry.getLongKey());

					for (ChunkTicket<?> chunkTicket : (SortedArraySet)entry.getValue()) {
						fileOutputStream.write(
							(chunkPos.x + "\t" + chunkPos.z + "\t" + chunkTicket.getType() + "\t" + chunkTicket.getLevel() + "\t\n").getBytes(StandardCharsets.UTF_8)
						);
					}
				}
			} catch (Throwable var9) {
				try {
					fileOutputStream.close();
				} catch (Throwable var8) {
					var9.addSuppressed(var8);
				}

				throw var9;
			}

			fileOutputStream.close();
		} catch (IOException var10) {
			LOGGER.error("Failed to dump tickets to {}", path, var10);
		}
	}

	@VisibleForTesting
	SimulationDistanceLevelPropagator getSimulationDistanceTracker() {
		return this.simulationDistanceTracker;
	}

	public LongSet getChunks() {
		return this.simulationDistanceTracker.getTrackedChunks();
	}

	public void removePersistentTickets() {
		ImmutableSet<ChunkTicketType<?>> immutableSet = ImmutableSet.of(ChunkTicketType.UNKNOWN, ChunkTicketType.POST_TELEPORT);
		ObjectIterator<Entry<SortedArraySet<ChunkTicket<?>>>> objectIterator = this.ticketsByPosition.long2ObjectEntrySet().fastIterator();

		while (objectIterator.hasNext()) {
			Entry<SortedArraySet<ChunkTicket<?>>> entry = (Entry<SortedArraySet<ChunkTicket<?>>>)objectIterator.next();
			Iterator<ChunkTicket<?>> iterator = ((SortedArraySet)entry.getValue()).iterator();
			boolean bl = false;

			while (iterator.hasNext()) {
				ChunkTicket<?> chunkTicket = (ChunkTicket<?>)iterator.next();
				if (!immutableSet.contains(chunkTicket.getType())) {
					iterator.remove();
					bl = true;
					this.simulationDistanceTracker.remove(entry.getLongKey(), chunkTicket);
				}
			}

			if (bl) {
				this.distanceFromTicketTracker.updateLevel(entry.getLongKey(), getLevel((SortedArraySet<ChunkTicket<?>>)entry.getValue()), false);
			}

			if (((SortedArraySet)entry.getValue()).isEmpty()) {
				objectIterator.remove();
			}
		}
	}

	public boolean shouldDelayShutdown() {
		return !this.ticketsByPosition.isEmpty();
	}

	class DistanceFromNearestPlayerTracker extends ChunkPosDistanceLevelPropagator {
		protected final Long2ByteMap distanceFromNearestPlayer = new Long2ByteOpenHashMap();
		protected final int maxDistance;

		protected DistanceFromNearestPlayerTracker(final int maxDistance) {
			super(maxDistance + 2, 16, 256);
			this.maxDistance = maxDistance;
			this.distanceFromNearestPlayer.defaultReturnValue((byte)(maxDistance + 2));
		}

		@Override
		protected int getLevel(long id) {
			return this.distanceFromNearestPlayer.get(id);
		}

		@Override
		protected void setLevel(long id, int level) {
			byte b;
			if (level > this.maxDistance) {
				b = this.distanceFromNearestPlayer.remove(id);
			} else {
				b = this.distanceFromNearestPlayer.put(id, (byte)level);
			}

			this.onDistanceChange(id, b, level);
		}

		protected void onDistanceChange(long pos, int oldDistance, int distance) {
		}

		@Override
		protected int getInitialLevel(long id) {
			return this.isPlayerInChunk(id) ? 0 : Integer.MAX_VALUE;
		}

		private boolean isPlayerInChunk(long chunkPos) {
			ObjectSet<ServerPlayerEntity> objectSet = ChunkTicketManager.this.playersByChunkPos.get(chunkPos);
			return objectSet != null && !objectSet.isEmpty();
		}

		public void updateLevels() {
			this.applyPendingUpdates(Integer.MAX_VALUE);
		}

		private void dump(String path) {
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(new File(path));

				try {
					for (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry entry : this.distanceFromNearestPlayer.long2ByteEntrySet()) {
						ChunkPos chunkPos = new ChunkPos(entry.getLongKey());
						String string = Byte.toString(entry.getByteValue());
						fileOutputStream.write((chunkPos.x + "\t" + chunkPos.z + "\t" + string + "\n").getBytes(StandardCharsets.UTF_8));
					}
				} catch (Throwable var8) {
					try {
						fileOutputStream.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}

					throw var8;
				}

				fileOutputStream.close();
			} catch (IOException var9) {
				ChunkTicketManager.LOGGER.error("Failed to dump chunks to {}", path, var9);
			}
		}
	}

	class NearbyChunkTicketUpdater extends ChunkTicketManager.DistanceFromNearestPlayerTracker {
		private int watchDistance;
		private final Long2IntMap distances = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
		private final LongSet positionsAffected = new LongOpenHashSet();

		protected NearbyChunkTicketUpdater(final int i) {
			super(i);
			this.watchDistance = 0;
			this.distances.defaultReturnValue(i + 2);
		}

		@Override
		protected void onDistanceChange(long pos, int oldDistance, int distance) {
			this.positionsAffected.add(pos);
		}

		public void setWatchDistance(int watchDistance) {
			for (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry entry : this.distanceFromNearestPlayer.long2ByteEntrySet()) {
				byte b = entry.getByteValue();
				long l = entry.getLongKey();
				this.updateTicket(l, b, this.isWithinViewDistance(b), b <= watchDistance);
			}

			this.watchDistance = watchDistance;
		}

		private void updateTicket(long pos, int distance, boolean oldWithinViewDistance, boolean withinViewDistance) {
			if (oldWithinViewDistance != withinViewDistance) {
				ChunkTicket<?> chunkTicket = new ChunkTicket<>(ChunkTicketType.PLAYER, ChunkTicketManager.NEARBY_PLAYER_TICKET_LEVEL, new ChunkPos(pos));
				if (withinViewDistance) {
					ChunkTicketManager.this.playerTicketThrottler.send(ChunkTaskPrioritySystem.createMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> {
							if (this.isWithinViewDistance(this.getLevel(pos))) {
								ChunkTicketManager.this.addTicket(pos, chunkTicket);
								ChunkTicketManager.this.freshPlayerTicketPositions.add(pos);
							} else {
								ChunkTicketManager.this.playerTicketThrottlerUnblocker.send(ChunkTaskPrioritySystem.createUnblockingMessage(() -> {
								}, pos, false));
							}
						}), pos, () -> distance));
				} else {
					ChunkTicketManager.this.playerTicketThrottlerUnblocker
						.send(
							ChunkTaskPrioritySystem.createUnblockingMessage(
								() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> ChunkTicketManager.this.removeTicket(pos, chunkTicket)), pos, true
							)
						);
				}
			}
		}

		@Override
		public void updateLevels() {
			super.updateLevels();
			if (!this.positionsAffected.isEmpty()) {
				LongIterator longIterator = this.positionsAffected.iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					int i = this.distances.get(l);
					int j = this.getLevel(l);
					if (i != j) {
						ChunkTicketManager.this.levelUpdateListener.updateLevel(new ChunkPos(l), () -> this.distances.get(l), j, level -> {
							if (level >= this.distances.defaultReturnValue()) {
								this.distances.remove(l);
							} else {
								this.distances.put(l, level);
							}
						});
						this.updateTicket(l, j, this.isWithinViewDistance(i), this.isWithinViewDistance(j));
					}
				}

				this.positionsAffected.clear();
			}
		}

		private boolean isWithinViewDistance(int distance) {
			return distance <= this.watchDistance;
		}
	}

	class TicketDistanceLevelPropagator extends ChunkPosDistanceLevelPropagator {
		private static final int UNLOADED = ChunkLevels.INACCESSIBLE + 1;

		public TicketDistanceLevelPropagator() {
			super(UNLOADED + 1, 16, 256);
		}

		@Override
		protected int getInitialLevel(long id) {
			SortedArraySet<ChunkTicket<?>> sortedArraySet = ChunkTicketManager.this.ticketsByPosition.get(id);
			if (sortedArraySet == null) {
				return Integer.MAX_VALUE;
			} else {
				return sortedArraySet.isEmpty() ? Integer.MAX_VALUE : sortedArraySet.first().getLevel();
			}
		}

		@Override
		protected int getLevel(long id) {
			if (!ChunkTicketManager.this.isUnloaded(id)) {
				ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(id);
				if (chunkHolder != null) {
					return chunkHolder.getLevel();
				}
			}

			return UNLOADED;
		}

		@Override
		protected void setLevel(long id, int level) {
			ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(id);
			int i = chunkHolder == null ? UNLOADED : chunkHolder.getLevel();
			if (i != level) {
				chunkHolder = ChunkTicketManager.this.setLevel(id, level, chunkHolder, i);
				if (chunkHolder != null) {
					ChunkTicketManager.this.chunkHoldersWithPendingUpdates.add(chunkHolder);
				}
			}
		}

		public int update(int distance) {
			return this.applyPendingUpdates(distance);
		}
	}
}
