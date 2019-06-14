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
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Actor;
import net.minecraft.util.ChunkPosLevelPropagator;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChunkTicketManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int NEARBY_PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.field_12803) - 2;
	private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos = new Long2ObjectOpenHashMap<>();
	private final Long2ObjectOpenHashMap<ObjectSortedSet<ChunkTicket<?>>> ticketsByPosition = new Long2ObjectOpenHashMap<>();
	private final ChunkTicketManager.class_4077 distanceFromTicketTracker = new ChunkTicketManager.class_4077();
	private final ChunkTicketManager.DistanceFromNearestPlayerTracker distanceFromNearestPlayerTracker = new ChunkTicketManager.DistanceFromNearestPlayerTracker(8);
	private final ChunkTicketManager.NearbyChunkTicketUpdater nearbyChunkTicketUpdater = new ChunkTicketManager.NearbyChunkTicketUpdater(33);
	private final Set<ChunkHolder> chunkHolders = Sets.<ChunkHolder>newHashSet();
	private final ChunkHolder.LevelUpdateListener levelUpdateListener;
	private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> playerTicketThrottler;
	private final Actor<ChunkTaskPrioritySystem.SorterMessage> playerTicketThrottlerSorter;
	private final LongSet chunkPositions = new LongOpenHashSet();
	private final Executor mainThreadExecutor;
	private long location;

	protected ChunkTicketManager(Executor executor, Executor executor2) {
		MailboxProcessor<Runnable> mailboxProcessor = MailboxProcessor.create(executor2, "player ticket throttler");
		ChunkTaskPrioritySystem chunkTaskPrioritySystem = new ChunkTaskPrioritySystem(ImmutableList.of(mailboxProcessor), executor, 15);
		this.levelUpdateListener = chunkTaskPrioritySystem;
		this.playerTicketThrottler = chunkTaskPrioritySystem.createExecutingActor(mailboxProcessor, true);
		this.playerTicketThrottlerSorter = chunkTaskPrioritySystem.createSortingActor(mailboxProcessor);
		this.mainThreadExecutor = executor2;
	}

	protected void purge() {
		this.location++;
		ObjectIterator<Entry<ObjectSortedSet<ChunkTicket<?>>>> objectIterator = this.ticketsByPosition.long2ObjectEntrySet().fastIterator();

		while (objectIterator.hasNext()) {
			Entry<ObjectSortedSet<ChunkTicket<?>>> entry = (Entry<ObjectSortedSet<ChunkTicket<?>>>)objectIterator.next();
			if (((ObjectSortedSet)entry.getValue()).removeIf(chunkTicket -> chunkTicket.method_20627(this.location))) {
				this.distanceFromTicketTracker.update(entry.getLongKey(), this.getLevel((ObjectSortedSet<ChunkTicket<?>>)entry.getValue()), false);
			}

			if (((ObjectSortedSet)entry.getValue()).isEmpty()) {
				objectIterator.remove();
			}
		}
	}

	private int getLevel(ObjectSortedSet<ChunkTicket<?>> objectSortedSet) {
		ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
		return objectBidirectionalIterator.hasNext() ? ((ChunkTicket)objectBidirectionalIterator.next()).getLevel() : ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
	}

	protected abstract boolean isUnloaded(long l);

	@Nullable
	protected abstract ChunkHolder getChunkHolder(long l);

	@Nullable
	protected abstract ChunkHolder setLevel(long l, int i, @Nullable ChunkHolder chunkHolder, int j);

	public boolean tick(ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
		this.distanceFromNearestPlayerTracker.updateLevels();
		this.nearbyChunkTicketUpdater.updateLevels();
		int i = Integer.MAX_VALUE - this.distanceFromTicketTracker.method_18746(Integer.MAX_VALUE);
		boolean bl = i != 0;
		if (bl) {
		}

		if (!this.chunkHolders.isEmpty()) {
			this.chunkHolders.forEach(chunkHolderx -> chunkHolderx.tick(threadedAnvilChunkStorage));
			this.chunkHolders.clear();
			return true;
		} else {
			if (!this.chunkPositions.isEmpty()) {
				LongIterator longIterator = this.chunkPositions.iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					if (this.getTicketSet(l).stream().anyMatch(chunkTicket -> chunkTicket.getType() == ChunkTicketType.field_14033)) {
						ChunkHolder chunkHolder = threadedAnvilChunkStorage.getCurrentChunkHolder(l);
						if (chunkHolder == null) {
							throw new IllegalStateException();
						}

						CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getEntityTickingFuture();
						completableFuture.thenAccept(
							either -> this.mainThreadExecutor.execute(() -> this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> {
									}, l, false)))
						);
					}
				}

				this.chunkPositions.clear();
			}

			return bl;
		}
	}

	private void addTicket(long l, ChunkTicket<?> chunkTicket) {
		ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(l);
		ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
		int i;
		if (objectBidirectionalIterator.hasNext()) {
			i = ((ChunkTicket)objectBidirectionalIterator.next()).getLevel();
		} else {
			i = ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
		}

		if (objectSortedSet.add(chunkTicket)) {
		}

		if (chunkTicket.getLevel() < i) {
			this.distanceFromTicketTracker.update(l, chunkTicket.getLevel(), true);
		}
	}

	private void removeTicket(long l, ChunkTicket<?> chunkTicket) {
		ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(l);
		if (objectSortedSet.remove(chunkTicket)) {
		}

		if (objectSortedSet.isEmpty()) {
			this.ticketsByPosition.remove(l);
		}

		this.distanceFromTicketTracker.update(l, this.getLevel(objectSortedSet), false);
	}

	public <T> void addTicketWithLevel(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.addTicket(chunkPos.toLong(), new ChunkTicket<>(chunkTicketType, i, object, this.location));
	}

	public <T> void removeTicketWithLevel(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(chunkTicketType, i, object, this.location);
		this.removeTicket(chunkPos.toLong(), chunkTicket);
	}

	public <T> void addTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.addTicket(chunkPos.toLong(), new ChunkTicket<>(chunkTicketType, 33 - i, object, this.location));
	}

	public <T> void removeTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(chunkTicketType, 33 - i, object, this.location);
		this.removeTicket(chunkPos.toLong(), chunkTicket);
	}

	private ObjectSortedSet<ChunkTicket<?>> getTicketSet(long l) {
		return this.ticketsByPosition.computeIfAbsent(l, lx -> new ObjectAVLTreeSet());
	}

	protected void setChunkForced(ChunkPos chunkPos, boolean bl) {
		ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<>(ChunkTicketType.field_14031, 31, chunkPos, this.location);
		if (bl) {
			this.addTicket(chunkPos.toLong(), chunkTicket);
		} else {
			this.removeTicket(chunkPos.toLong(), chunkTicket);
		}
	}

	public void handleChunkEnter(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
		long l = chunkSectionPos.toChunkPos().toLong();
		this.playersByChunkPos.computeIfAbsent(l, lx -> new ObjectOpenHashSet()).add(serverPlayerEntity);
		this.distanceFromNearestPlayerTracker.update(l, 0, true);
		this.nearbyChunkTicketUpdater.update(l, 0, true);
	}

	public void handleChunkLeave(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
		long l = chunkSectionPos.toChunkPos().toLong();
		ObjectSet<ServerPlayerEntity> objectSet = this.playersByChunkPos.get(l);
		objectSet.remove(serverPlayerEntity);
		if (objectSet.isEmpty()) {
			this.playersByChunkPos.remove(l);
			this.distanceFromNearestPlayerTracker.update(l, Integer.MAX_VALUE, false);
			this.nearbyChunkTicketUpdater.update(l, Integer.MAX_VALUE, false);
		}
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

	class DistanceFromNearestPlayerTracker extends ChunkPosLevelPropagator {
		protected final Long2ByteMap distanceFromNearestPlayer = new Long2ByteOpenHashMap();
		protected final int maxDistance;

		protected DistanceFromNearestPlayerTracker(int i) {
			super(i + 2, 16, 256);
			this.maxDistance = i;
			this.distanceFromNearestPlayer.defaultReturnValue((byte)(i + 2));
		}

		@Override
		protected int getLevel(long l) {
			return this.distanceFromNearestPlayer.get(l);
		}

		@Override
		protected void setLevel(long l, int i) {
			byte b;
			if (i > this.maxDistance) {
				b = this.distanceFromNearestPlayer.remove(l);
			} else {
				b = this.distanceFromNearestPlayer.put(l, (byte)i);
			}

			this.onDistanceChange(l, b, i);
		}

		protected void onDistanceChange(long l, int i, int j) {
		}

		@Override
		protected int getInitialLevel(long l) {
			return this.isPlayerInChunk(l) ? 0 : Integer.MAX_VALUE;
		}

		private boolean isPlayerInChunk(long l) {
			ObjectSet<ServerPlayerEntity> objectSet = ChunkTicketManager.this.playersByChunkPos.get(l);
			return objectSet != null && !objectSet.isEmpty();
		}

		public void updateLevels() {
			this.updateAllRecursively(Integer.MAX_VALUE);
		}
	}

	class NearbyChunkTicketUpdater extends ChunkTicketManager.DistanceFromNearestPlayerTracker {
		private int watchDistance;
		private final Long2IntMap distances = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
		private final LongSet positionsAffected = new LongOpenHashSet();

		protected NearbyChunkTicketUpdater(int i) {
			super(i);
			this.watchDistance = 0;
			this.distances.defaultReturnValue(i + 2);
		}

		@Override
		protected void onDistanceChange(long l, int i, int j) {
			this.positionsAffected.add(l);
		}

		public void setWatchDistance(int i) {
			for (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry entry : this.distanceFromNearestPlayer.long2ByteEntrySet()) {
				byte b = entry.getByteValue();
				long l = entry.getLongKey();
				this.updateTicket(l, b, this.isWithinViewDistance(b), b <= i - 2);
			}

			this.watchDistance = i;
		}

		private void updateTicket(long l, int i, boolean bl, boolean bl2) {
			if (bl != bl2) {
				ChunkTicket<?> chunkTicket = new ChunkTicket<>(
					ChunkTicketType.field_14033, ChunkTicketManager.NEARBY_PLAYER_TICKET_LEVEL, new ChunkPos(l), ChunkTicketManager.this.location
				);
				if (bl2) {
					ChunkTicketManager.this.playerTicketThrottler
						.send(ChunkTaskPrioritySystem.createRunnableMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> {
								ChunkTicketManager.this.addTicket(l, chunkTicket);
								ChunkTicketManager.this.chunkPositions.add(l);
							}), l, () -> i));
				} else {
					ChunkTicketManager.this.playerTicketThrottlerSorter
						.send(
							ChunkTaskPrioritySystem.createSorterMessage(
								() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> ChunkTicketManager.this.removeTicket(l, chunkTicket)), l, true
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
						ChunkTicketManager.this.levelUpdateListener.updateLevel(new ChunkPos(l), () -> this.distances.get(l), j, ix -> this.distances.put(l, ix));
						this.updateTicket(l, j, this.isWithinViewDistance(i), this.isWithinViewDistance(j));
					}
				}

				this.positionsAffected.clear();
			}
		}

		private boolean isWithinViewDistance(int i) {
			return i <= this.watchDistance - 2;
		}
	}

	class class_4077 extends ChunkPosLevelPropagator {
		public class_4077() {
			super(ThreadedAnvilChunkStorage.MAX_LEVEL + 2, 16, 256);
		}

		@Override
		protected int getInitialLevel(long l) {
			ObjectSortedSet<ChunkTicket<?>> objectSortedSet = ChunkTicketManager.this.ticketsByPosition.get(l);
			if (objectSortedSet == null) {
				return Integer.MAX_VALUE;
			} else {
				ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
				return !objectBidirectionalIterator.hasNext() ? Integer.MAX_VALUE : ((ChunkTicket)objectBidirectionalIterator.next()).getLevel();
			}
		}

		@Override
		protected int getLevel(long l) {
			if (!ChunkTicketManager.this.isUnloaded(l)) {
				ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(l);
				if (chunkHolder != null) {
					return chunkHolder.getLevel();
				}
			}

			return ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
		}

		@Override
		protected void setLevel(long l, int i) {
			ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(l);
			int j = chunkHolder == null ? ThreadedAnvilChunkStorage.MAX_LEVEL + 1 : chunkHolder.getLevel();
			if (j != i) {
				chunkHolder = ChunkTicketManager.this.setLevel(l, i, chunkHolder, j);
				if (chunkHolder != null) {
					ChunkTicketManager.this.chunkHolders.add(chunkHolder);
				}
			}
		}

		public int method_18746(int i) {
			return this.updateAllRecursively(i);
		}
	}
}
