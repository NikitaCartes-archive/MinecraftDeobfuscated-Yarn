package net.minecraft.world.tick;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profiler;

public class WorldTickScheduler<T> implements QueryableTickScheduler<T> {
	private static final Comparator<ChunkTickScheduler<?>> COMPARATOR = (a, b) -> OrderedTick.BASIC_COMPARATOR.compare(a.peekNextTick(), b.peekNextTick());
	private final LongPredicate tickingFutureReadyPredicate;
	private final Supplier<Profiler> profilerGetter;
	private final Long2ObjectMap<ChunkTickScheduler<T>> chunkTickSchedulers = new Long2ObjectOpenHashMap<>();
	private final Long2LongMap nextTriggerTickByChunkPos = Util.make(new Long2LongOpenHashMap(), map -> map.defaultReturnValue(Long.MAX_VALUE));
	private final Queue<ChunkTickScheduler<T>> tickableChunkTickSchedulers = new PriorityQueue(COMPARATOR);
	private final Queue<OrderedTick<T>> tickableTicks = new ArrayDeque();
	private final List<OrderedTick<T>> tickedTicks = new ArrayList();
	private final Set<OrderedTick<?>> copiedTickableTicksList = new ObjectOpenCustomHashSet<>(OrderedTick.HASH_STRATEGY);
	private final BiConsumer<ChunkTickScheduler<T>, OrderedTick<T>> queuedTickConsumer = (chunkTickScheduler, tick) -> {
		if (tick.equals(chunkTickScheduler.peekNextTick())) {
			this.schedule(tick);
		}
	};

	public WorldTickScheduler(LongPredicate tickingFutureReadyPredicate, Supplier<Profiler> profilerGetter) {
		this.tickingFutureReadyPredicate = tickingFutureReadyPredicate;
		this.profilerGetter = profilerGetter;
	}

	public void addChunkTickScheduler(ChunkPos pos, ChunkTickScheduler<T> scheduler) {
		long l = pos.toLong();
		this.chunkTickSchedulers.put(l, scheduler);
		OrderedTick<T> orderedTick = scheduler.peekNextTick();
		if (orderedTick != null) {
			this.nextTriggerTickByChunkPos.put(l, orderedTick.triggerTick());
		}

		scheduler.setTickConsumer(this.queuedTickConsumer);
	}

	public void removeChunkTickScheduler(ChunkPos pos) {
		long l = pos.toLong();
		ChunkTickScheduler<T> chunkTickScheduler = this.chunkTickSchedulers.remove(l);
		this.nextTriggerTickByChunkPos.remove(l);
		if (chunkTickScheduler != null) {
			chunkTickScheduler.setTickConsumer(null);
		}
	}

	@Override
	public void scheduleTick(OrderedTick<T> orderedTick) {
		long l = ChunkPos.toLong(orderedTick.pos());
		ChunkTickScheduler<T> chunkTickScheduler = this.chunkTickSchedulers.get(l);
		if (chunkTickScheduler == null) {
			Util.throwOrPause((T)(new IllegalStateException("Trying to schedule tick in not loaded position " + orderedTick.pos())));
		} else {
			chunkTickScheduler.scheduleTick(orderedTick);
		}
	}

	public void tick(long time, int maxTicks, BiConsumer<BlockPos, T> ticker) {
		Profiler profiler = (Profiler)this.profilerGetter.get();
		profiler.push("collect");
		this.collectTickableTicks(time, maxTicks, profiler);
		profiler.swap("run");
		profiler.visit("ticksToRun", this.tickableTicks.size());
		this.tick(ticker);
		profiler.swap("cleanup");
		this.clear();
		profiler.pop();
	}

	private void collectTickableTicks(long time, int maxTicks, Profiler profiler) {
		this.collectTickableChunkTickSchedulers(time);
		profiler.visit("containersToTick", this.tickableChunkTickSchedulers.size());
		this.addTickableTicks(time, maxTicks);
		this.delayAllTicks();
	}

	private void collectTickableChunkTickSchedulers(long time) {
		ObjectIterator<Entry> objectIterator = Long2LongMaps.fastIterator(this.nextTriggerTickByChunkPos);

		while (objectIterator.hasNext()) {
			Entry entry = (Entry)objectIterator.next();
			long l = entry.getLongKey();
			long m = entry.getLongValue();
			if (m <= time) {
				ChunkTickScheduler<T> chunkTickScheduler = this.chunkTickSchedulers.get(l);
				if (chunkTickScheduler == null) {
					objectIterator.remove();
				} else {
					OrderedTick<T> orderedTick = chunkTickScheduler.peekNextTick();
					if (orderedTick == null) {
						objectIterator.remove();
					} else if (orderedTick.triggerTick() > time) {
						entry.setValue(orderedTick.triggerTick());
					} else if (this.tickingFutureReadyPredicate.test(l)) {
						objectIterator.remove();
						this.tickableChunkTickSchedulers.add(chunkTickScheduler);
					}
				}
			}
		}
	}

	private void addTickableTicks(long time, int maxTicks) {
		ChunkTickScheduler<T> chunkTickScheduler;
		while (this.isTickableTicksCountUnder(maxTicks) && (chunkTickScheduler = (ChunkTickScheduler<T>)this.tickableChunkTickSchedulers.poll()) != null) {
			OrderedTick<T> orderedTick = chunkTickScheduler.pollNextTick();
			this.addTickableTick(orderedTick);
			this.addTickableTicks(this.tickableChunkTickSchedulers, chunkTickScheduler, time, maxTicks);
			OrderedTick<T> orderedTick2 = chunkTickScheduler.peekNextTick();
			if (orderedTick2 != null) {
				if (orderedTick2.triggerTick() <= time && this.isTickableTicksCountUnder(maxTicks)) {
					this.tickableChunkTickSchedulers.add(chunkTickScheduler);
				} else {
					this.schedule(orderedTick2);
				}
			}
		}
	}

	private void delayAllTicks() {
		for (ChunkTickScheduler<T> chunkTickScheduler : this.tickableChunkTickSchedulers) {
			this.schedule(chunkTickScheduler.peekNextTick());
		}
	}

	private void schedule(OrderedTick<T> tick) {
		this.nextTriggerTickByChunkPos.put(ChunkPos.toLong(tick.pos()), tick.triggerTick());
	}

	private void addTickableTicks(Queue<ChunkTickScheduler<T>> tickableChunkTickSchedulers, ChunkTickScheduler<T> chunkTickScheduler, long tick, int maxTicks) {
		if (this.isTickableTicksCountUnder(maxTicks)) {
			ChunkTickScheduler<T> chunkTickScheduler2 = (ChunkTickScheduler<T>)tickableChunkTickSchedulers.peek();
			OrderedTick<T> orderedTick = chunkTickScheduler2 != null ? chunkTickScheduler2.peekNextTick() : null;

			while (this.isTickableTicksCountUnder(maxTicks)) {
				OrderedTick<T> orderedTick2 = chunkTickScheduler.peekNextTick();
				if (orderedTick2 == null || orderedTick2.triggerTick() > tick || orderedTick != null && OrderedTick.BASIC_COMPARATOR.compare(orderedTick2, orderedTick) > 0
					)
				 {
					break;
				}

				chunkTickScheduler.pollNextTick();
				this.addTickableTick(orderedTick2);
			}
		}
	}

	private void addTickableTick(OrderedTick<T> tick) {
		this.tickableTicks.add(tick);
	}

	private boolean isTickableTicksCountUnder(int maxTicks) {
		return this.tickableTicks.size() < maxTicks;
	}

	private void tick(BiConsumer<BlockPos, T> ticker) {
		while (!this.tickableTicks.isEmpty()) {
			OrderedTick<T> orderedTick = (OrderedTick<T>)this.tickableTicks.poll();
			if (!this.copiedTickableTicksList.isEmpty()) {
				this.copiedTickableTicksList.remove(orderedTick);
			}

			this.tickedTicks.add(orderedTick);
			ticker.accept(orderedTick.pos(), orderedTick.type());
		}
	}

	private void clear() {
		this.tickableTicks.clear();
		this.tickableChunkTickSchedulers.clear();
		this.tickedTicks.clear();
		this.copiedTickableTicksList.clear();
	}

	@Override
	public boolean isQueued(BlockPos pos, T type) {
		ChunkTickScheduler<T> chunkTickScheduler = this.chunkTickSchedulers.get(ChunkPos.toLong(pos));
		return chunkTickScheduler != null && chunkTickScheduler.isQueued(pos, type);
	}

	@Override
	public boolean isTicking(BlockPos pos, T type) {
		this.copyTickableTicksList();
		return this.copiedTickableTicksList.contains(OrderedTick.create(type, pos));
	}

	private void copyTickableTicksList() {
		if (this.copiedTickableTicksList.isEmpty() && !this.tickableTicks.isEmpty()) {
			this.copiedTickableTicksList.addAll(this.tickableTicks);
		}
	}

	private void visitChunks(BlockBox box, WorldTickScheduler.ChunkVisitor<T> visitor) {
		int i = ChunkSectionPos.getSectionCoord((double)box.getMinX());
		int j = ChunkSectionPos.getSectionCoord((double)box.getMinZ());
		int k = ChunkSectionPos.getSectionCoord((double)box.getMaxX());
		int l = ChunkSectionPos.getSectionCoord((double)box.getMaxZ());

		for (int m = i; m <= k; m++) {
			for (int n = j; n <= l; n++) {
				long o = ChunkPos.toLong(m, n);
				ChunkTickScheduler<T> chunkTickScheduler = this.chunkTickSchedulers.get(o);
				if (chunkTickScheduler != null) {
					visitor.accept(o, chunkTickScheduler);
				}
			}
		}
	}

	public void clearNextTicks(BlockBox box) {
		Predicate<OrderedTick<T>> predicate = tick -> box.contains(tick.pos());
		this.visitChunks(box, (chunkPos, chunkTickScheduler) -> {
			OrderedTick<T> orderedTick = chunkTickScheduler.peekNextTick();
			chunkTickScheduler.removeTicksIf(predicate);
			OrderedTick<T> orderedTick2 = chunkTickScheduler.peekNextTick();
			if (orderedTick2 != orderedTick) {
				if (orderedTick2 != null) {
					this.schedule(orderedTick2);
				} else {
					this.nextTriggerTickByChunkPos.remove(chunkPos);
				}
			}
		});
		this.tickedTicks.removeIf(predicate);
		this.tickableTicks.removeIf(predicate);
	}

	public void scheduleTicks(BlockBox box, Vec3i offset) {
		this.scheduleTicks(this, box, offset);
	}

	public void scheduleTicks(WorldTickScheduler<T> scheduler, BlockBox box, Vec3i offset) {
		List<OrderedTick<T>> list = new ArrayList();
		Predicate<OrderedTick<T>> predicate = tick -> box.contains(tick.pos());
		scheduler.tickedTicks.stream().filter(predicate).forEach(list::add);
		scheduler.tickableTicks.stream().filter(predicate).forEach(list::add);
		scheduler.visitChunks(box, (chunkPos, chunkTickScheduler) -> chunkTickScheduler.getQueueAsStream().filter(predicate).forEach(list::add));
		LongSummaryStatistics longSummaryStatistics = list.stream().mapToLong(OrderedTick::subTickOrder).summaryStatistics();
		long l = longSummaryStatistics.getMin();
		long m = longSummaryStatistics.getMax();
		list.forEach(
			tick -> this.scheduleTick(new OrderedTick<>((T)tick.type(), tick.pos().add(offset), tick.triggerTick(), tick.priority(), tick.subTickOrder() - l + m + 1L))
		);
	}

	@Override
	public int getTickCount() {
		return this.chunkTickSchedulers.values().stream().mapToInt(TickScheduler::getTickCount).sum();
	}

	@FunctionalInterface
	interface ChunkVisitor<T> {
		void accept(long chunkPos, ChunkTickScheduler<T> chunkTickScheduler);
	}
}
