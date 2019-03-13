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
import net.minecraft.class_4079;
import net.minecraft.entity.player.ChunkTicketType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Actor;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChunkTicketManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_17452 = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.FULL) - 2;
	private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> field_17453 = new Long2ObjectOpenHashMap<>();
	private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> field_18251 = new Long2ObjectOpenHashMap<>();
	private final Long2ObjectOpenHashMap<ObjectSortedSet<ChunkTicket<?>>> positionToTicketSet = new Long2ObjectOpenHashMap<>();
	private final ChunkTicketManager.class_4077 field_18252 = new ChunkTicketManager.class_4077();
	private final ChunkTicketManager.class_4078 field_18253 = new ChunkTicketManager.class_4078();
	private int field_18254;
	private final ChunkTicketManager.class_3205 field_17454 = new ChunkTicketManager.class_3205(8);
	private final ChunkTicketManager.class_3948 field_17455 = new ChunkTicketManager.class_3948(33);
	private final Set<ChunkHolder> chunkHolders = Sets.<ChunkHolder>newHashSet();
	private final ChunkHolder.LevelUpdateListener levelUpdateListener;
	private final Actor<ChunkTaskPrioritySystem.RunnableMessage<Runnable>> playerTicketThrottler;
	private final Actor<ChunkTaskPrioritySystem.SorterMessage> playerTicketThrottlerSorter;
	private final LongSet chunkPositions = new LongOpenHashSet();
	private final Executor field_17460;
	private long location;

	protected ChunkTicketManager(Executor executor, Executor executor2) {
		MailboxProcessor<Runnable> mailboxProcessor = MailboxProcessor.create(executor2, "player ticket throttler");
		ChunkTaskPrioritySystem chunkTaskPrioritySystem = new ChunkTaskPrioritySystem(ImmutableList.of(mailboxProcessor), executor, 15);
		this.levelUpdateListener = chunkTaskPrioritySystem;
		this.playerTicketThrottler = chunkTaskPrioritySystem.getExecutingActor(mailboxProcessor, true);
		this.playerTicketThrottlerSorter = chunkTaskPrioritySystem.getSortingActor(mailboxProcessor);
		this.field_17460 = executor2;
	}

	protected void method_18738(int i) {
		int j = this.method_18742();
		this.field_18254 = i;
		int k = this.method_18742();

		for (Entry<ObjectSet<ServerPlayerEntity>> entry : this.field_18251.long2ObjectEntrySet()) {
			this.field_18253.method_18750(entry.getLongKey(), k, k < j);
		}
	}

	protected void purge() {
		this.location++;
		ObjectIterator<Entry<ObjectSortedSet<ChunkTicket<?>>>> objectIterator = this.positionToTicketSet.long2ObjectEntrySet().fastIterator();

		while (objectIterator.hasNext()) {
			Entry<ObjectSortedSet<ChunkTicket<?>>> entry = (Entry<ObjectSortedSet<ChunkTicket<?>>>)objectIterator.next();
			if (((ObjectSortedSet)entry.getValue())
				.removeIf(chunkTicket -> chunkTicket.method_14281() == ChunkTicketType.UNKNOWN && chunkTicket.getLocation() != this.location)) {
				this.field_18252.scheduleNewLevelUpdate(entry.getLongKey(), this.getLevel((ObjectSortedSet<ChunkTicket<?>>)entry.getValue()), false);
			}

			if (((ObjectSortedSet)entry.getValue()).isEmpty()) {
				objectIterator.remove();
			}
		}
	}

	private int getLevel(ObjectSortedSet<ChunkTicket<?>> objectSortedSet) {
		ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
		return objectBidirectionalIterator.hasNext() ? ((ChunkTicket)objectBidirectionalIterator.next()).getLevel() : ThreadedAnvilChunkStorage.field_18239 + 1;
	}

	protected abstract boolean method_14035(long l);

	@Nullable
	protected abstract ChunkHolder getChunkHolder(long l);

	@Nullable
	protected abstract ChunkHolder setLevel(long l, int i, @Nullable ChunkHolder chunkHolder, int j);

	public boolean update(ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
		this.field_17454.updateLevels();
		this.field_17455.updateLevels();
		this.field_18253.method_18747();
		int i = Integer.MAX_VALUE - this.field_18252.method_18746(Integer.MAX_VALUE);
		boolean bl = i != 0;
		if (bl) {
		}

		if (!this.chunkHolders.isEmpty()) {
			this.chunkHolders.forEach(chunkHolderx -> chunkHolderx.method_14007(threadedAnvilChunkStorage));
			this.chunkHolders.clear();
			return true;
		} else {
			if (!this.chunkPositions.isEmpty()) {
				LongIterator longIterator = this.chunkPositions.iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					if (this.getTicketSet(l).stream().anyMatch(chunkTicket -> chunkTicket.method_14281() == ChunkTicketType.PLAYER)) {
						ChunkHolder chunkHolder = threadedAnvilChunkStorage.getChunkHolder(l);
						if (chunkHolder == null) {
							throw new IllegalStateException();
						}

						CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.method_14003();
						completableFuture.thenAccept(
							either -> this.field_17460.execute(() -> this.playerTicketThrottlerSorter.method_16901(ChunkTaskPrioritySystem.method_17627(() -> {
									}, l, false)))
						);
					}
				}

				this.chunkPositions.clear();
			}

			return bl;
		}
	}

	private void method_14042(long l, ChunkTicket<?> chunkTicket) {
		ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(l);
		ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
		int i;
		if (objectBidirectionalIterator.hasNext()) {
			i = ((ChunkTicket)objectBidirectionalIterator.next()).getLevel();
		} else {
			i = ThreadedAnvilChunkStorage.field_18239 + 1;
		}

		if (objectSortedSet.add(chunkTicket)) {
		}

		if (chunkTicket.getLevel() < i) {
			this.field_18252.scheduleNewLevelUpdate(l, chunkTicket.getLevel(), true);
		}
	}

	private void method_17645(long l, ChunkTicket<?> chunkTicket) {
		ObjectSortedSet<ChunkTicket<?>> objectSortedSet = this.getTicketSet(l);
		if (objectSortedSet.remove(chunkTicket)) {
		}

		if (objectSortedSet.isEmpty()) {
			this.positionToTicketSet.remove(l);
		}

		this.field_18252.scheduleNewLevelUpdate(l, this.getLevel(objectSortedSet), false);
	}

	public <T> void method_17290(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.method_14042(chunkPos.toLong(), new ChunkTicket<>(chunkTicketType, i, object, this.location));
	}

	public <T> void method_17291(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.method_14042(chunkPos.toLong(), new ChunkTicket<>(chunkTicketType, 33 - i, object, this.location));
	}

	public <T> void method_17292(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(chunkTicketType, 33 - i, object, this.location);
		this.method_17645(chunkPos.toLong(), chunkTicket);
	}

	private ObjectSortedSet<ChunkTicket<?>> getTicketSet(long l) {
		return this.positionToTicketSet.computeIfAbsent(l, lx -> new ObjectAVLTreeSet());
	}

	protected void setChunkForced(ChunkPos chunkPos, boolean bl) {
		ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<>(ChunkTicketType.FORCED, 32, chunkPos, this.location);
		if (bl) {
			this.method_14042(chunkPos.toLong(), chunkTicket);
		} else {
			this.method_17645(chunkPos.toLong(), chunkTicket);
		}
	}

	private int method_18742() {
		return 16 - this.field_18254;
	}

	public void method_14048(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
		long l = chunkSectionPos.toChunkPos().toLong();
		serverPlayerEntity.setChunkPos(chunkSectionPos);
		this.field_18251.computeIfAbsent(chunkSectionPos.asLong(), lx -> new ObjectOpenHashSet()).add(serverPlayerEntity);
		this.field_17453.computeIfAbsent(l, lx -> new ObjectOpenHashSet()).add(serverPlayerEntity);
		this.field_17454.scheduleNewLevelUpdate(l, 0, true);
		this.field_17455.scheduleNewLevelUpdate(l, 0, true);
		this.field_18253.method_18750(chunkSectionPos.asLong(), this.method_18742(), true);
	}

	public void method_14051(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
		long l = chunkSectionPos.toChunkPos().toLong();
		ObjectSet<ServerPlayerEntity> objectSet = this.field_18251.get(chunkSectionPos.asLong());
		if (objectSet != null) {
			objectSet.remove(serverPlayerEntity);
			if (objectSet.isEmpty()) {
				this.field_18251.remove(chunkSectionPos.asLong());
				this.field_18253.method_18750(chunkSectionPos.asLong(), Integer.MAX_VALUE, false);
			}

			ObjectSet<ServerPlayerEntity> objectSet2 = this.field_17453.get(l);
			objectSet2.remove(serverPlayerEntity);
			if (objectSet2.isEmpty()) {
				this.field_17453.remove(l);
				this.field_17454.scheduleNewLevelUpdate(l, Integer.MAX_VALUE, false);
				this.field_17455.scheduleNewLevelUpdate(l, Integer.MAX_VALUE, false);
			}
		}
	}

	protected void method_14049(int i) {
		this.field_17455.method_17658(i);
	}

	public int getLevelCount() {
		this.field_17454.updateLevels();
		return this.field_17454.currentLevels.size();
	}

	public boolean method_18739(ChunkSectionPos chunkSectionPos) {
		return this.field_18253.getCurrentLevelFor(chunkSectionPos.asLong()) <= 16;
	}

	class class_3205 extends ChunkLevelIndexedProcessor {
		protected final Long2ByteMap currentLevels = new Long2ByteOpenHashMap();
		protected final int field_17461;

		protected class_3205(int i) {
			super(i + 2, 16, 256);
			this.field_17461 = i;
			this.currentLevels.defaultReturnValue((byte)(i + 2));
		}

		@Override
		protected int getCurrentLevelFor(long l) {
			return this.currentLevels.get(l);
		}

		@Override
		protected void setLevelFor(long l, int i) {
			byte b;
			if (i > this.field_17461) {
				b = this.currentLevels.remove(l);
			} else {
				b = this.currentLevels.put(l, (byte)i);
			}

			this.method_17657(l, b, i);
		}

		protected void method_17657(long l, int i, int j) {
		}

		@Override
		protected int getLevel(long l) {
			return this.method_14056(l) ? 0 : Integer.MAX_VALUE;
		}

		private boolean method_14056(long l) {
			ObjectSet<ServerPlayerEntity> objectSet = ChunkTicketManager.this.field_17453.get(l);
			return objectSet != null && !objectSet.isEmpty();
		}

		public void updateLevels() {
			this.updateLevels(Integer.MAX_VALUE);
		}
	}

	class class_3948 extends ChunkTicketManager.class_3205 {
		private int viewDistance;
		private final Long2IntMap posToLastLevelUpdatedTo = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
		private final LongSet chunkPositions = new LongOpenHashSet();

		protected class_3948(int i) {
			super(i);
			this.viewDistance = 0;
			this.posToLastLevelUpdatedTo.defaultReturnValue(i + 2);
		}

		@Override
		protected void method_17657(long l, int i, int j) {
			this.chunkPositions.add(l);
		}

		public void method_17658(int i) {
			for (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry entry : this.currentLevels.long2ByteEntrySet()) {
				byte b = entry.getByteValue();
				long l = entry.getLongKey();
				this.method_17660(l, b, this.method_17664(b), b <= i - 2);
			}

			this.viewDistance = i;
		}

		private void method_17660(long l, int i, boolean bl, boolean bl2) {
			if (bl != bl2) {
				ChunkTicket<?> chunkTicket = new ChunkTicket<>(ChunkTicketType.PLAYER, ChunkTicketManager.field_17452, new ChunkPos(l), ChunkTicketManager.this.location);
				if (bl2) {
					ChunkTicketManager.this.playerTicketThrottler
						.method_16901(ChunkTaskPrioritySystem.createRunnableMessage(() -> ChunkTicketManager.this.field_17460.execute(() -> {
								ChunkTicketManager.this.method_14042(l, chunkTicket);
								ChunkTicketManager.this.chunkPositions.add(l);
							}), l, () -> i));
				} else {
					ChunkTicketManager.this.playerTicketThrottlerSorter
						.method_16901(
							ChunkTaskPrioritySystem.method_17627(
								() -> ChunkTicketManager.this.field_17460.execute(() -> ChunkTicketManager.this.method_17645(l, chunkTicket)), l, true
							)
						);
				}
			}
		}

		@Override
		public void updateLevels() {
			super.updateLevels();
			if (!this.chunkPositions.isEmpty()) {
				LongIterator longIterator = this.chunkPositions.iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					int i = this.posToLastLevelUpdatedTo.get(l);
					int j = this.getCurrentLevelFor(l);
					if (i != j) {
						ChunkTicketManager.this.levelUpdateListener
							.updateLevel(new ChunkPos(l), () -> this.posToLastLevelUpdatedTo.get(l), j, ix -> this.posToLastLevelUpdatedTo.put(l, ix));
						this.method_17660(l, j, this.method_17664(i), this.method_17664(j));
					}
				}

				this.chunkPositions.clear();
			}
		}

		private boolean method_17664(int i) {
			return i <= this.viewDistance - 2;
		}
	}

	class class_4077 extends ChunkLevelIndexedProcessor {
		public class_4077() {
			super(ThreadedAnvilChunkStorage.field_18239 + 2, 16, 256);
		}

		@Override
		protected int getLevel(long l) {
			ObjectSortedSet<ChunkTicket<?>> objectSortedSet = ChunkTicketManager.this.positionToTicketSet.get(l);
			if (objectSortedSet == null) {
				return Integer.MAX_VALUE;
			} else {
				ObjectBidirectionalIterator<ChunkTicket<?>> objectBidirectionalIterator = objectSortedSet.iterator();
				return !objectBidirectionalIterator.hasNext() ? Integer.MAX_VALUE : ((ChunkTicket)objectBidirectionalIterator.next()).getLevel();
			}
		}

		@Override
		protected int getCurrentLevelFor(long l) {
			if (!ChunkTicketManager.this.method_14035(l)) {
				ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(l);
				if (chunkHolder != null) {
					return chunkHolder.getLevel();
				}
			}

			return ThreadedAnvilChunkStorage.field_18239 + 1;
		}

		@Override
		protected void setLevelFor(long l, int i) {
			ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(l);
			int j = chunkHolder == null ? ThreadedAnvilChunkStorage.field_18239 + 1 : chunkHolder.getLevel();
			if (j != i) {
				chunkHolder = ChunkTicketManager.this.setLevel(l, i, chunkHolder, j);
				if (chunkHolder != null) {
					ChunkTicketManager.this.chunkHolders.add(chunkHolder);
				}
			}
		}

		public int method_18746(int i) {
			return this.updateLevels(i);
		}
	}

	class class_4078 extends class_4079 {
		protected final Long2ByteMap field_18256 = new Long2ByteOpenHashMap();

		protected class_4078() {
			super(18, 16, 256);
			this.field_18256.defaultReturnValue((byte)18);
		}

		@Override
		protected int getCurrentLevelFor(long l) {
			return this.field_18256.get(l);
		}

		@Override
		protected void setLevelFor(long l, int i) {
			if (i > 16) {
				this.field_18256.remove(l);
			} else {
				this.field_18256.put(l, (byte)i);
			}
		}

		@Override
		protected int method_18749(long l) {
			return this.method_18748(l) ? ChunkTicketManager.this.method_18742() : Integer.MAX_VALUE;
		}

		private boolean method_18748(long l) {
			ObjectSet<ServerPlayerEntity> objectSet = ChunkTicketManager.this.field_18251.get(l);
			return objectSet != null && !objectSet.isEmpty();
		}

		public void method_18747() {
			this.updateLevels(Integer.MAX_VALUE);
		}
	}
}
