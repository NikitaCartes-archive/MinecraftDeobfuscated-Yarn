package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;

public class LevelPrioritizedQueue<T> {
	public static final int LEVEL_COUNT = ThreadedAnvilChunkStorage.MAX_LEVEL + 2;
	private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> levelToPosToElements = (List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>>)IntStream.range(
			0, LEVEL_COUNT
		)
		.mapToObj(i -> new Long2ObjectLinkedOpenHashMap())
		.collect(Collectors.toList());
	private volatile int firstNonEmptyLevel = LEVEL_COUNT;
	private final String name;
	private final LongSet blockingChunks = new LongOpenHashSet();
	private final int maxBlocking;

	public LevelPrioritizedQueue(String name, int maxSize) {
		this.name = name;
		this.maxBlocking = maxSize;
	}

	protected void updateLevel(int fromLevel, ChunkPos pos, int toLevel) {
		if (fromLevel < LEVEL_COUNT) {
			Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap<List<Optional<T>>>)this.levelToPosToElements
				.get(fromLevel);
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.remove(pos.toLong());
			if (fromLevel == this.firstNonEmptyLevel) {
				while (this.method_40574() && ((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(this.firstNonEmptyLevel)).isEmpty()) {
					this.firstNonEmptyLevel++;
				}
			}

			if (list != null && !list.isEmpty()) {
				((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(toLevel))
					.computeIfAbsent(pos.toLong(), (Long2ObjectFunction<? extends List>)(l -> Lists.newArrayList()))
					.addAll(list);
				this.firstNonEmptyLevel = Math.min(this.firstNonEmptyLevel, toLevel);
			}
		}
	}

	protected void add(Optional<T> element, long pos, int level) {
		((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(level))
			.computeIfAbsent(pos, (Long2ObjectFunction<? extends List>)(l -> Lists.newArrayList()))
			.add(element);
		this.firstNonEmptyLevel = Math.min(this.firstNonEmptyLevel, level);
	}

	protected void remove(long pos, boolean removeElement) {
		for (Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap : this.levelToPosToElements) {
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.get(pos);
			if (list != null) {
				if (removeElement) {
					list.clear();
				} else {
					list.removeIf(optional -> !optional.isPresent());
				}

				if (list.isEmpty()) {
					long2ObjectLinkedOpenHashMap.remove(pos);
				}
			}
		}

		while (this.method_40574() && ((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(this.firstNonEmptyLevel)).isEmpty()) {
			this.firstNonEmptyLevel++;
		}

		this.blockingChunks.remove(pos);
	}

	private Runnable createBlockingAdder(long pos) {
		return () -> this.blockingChunks.add(pos);
	}

	@Nullable
	public Stream<Either<T, Runnable>> poll() {
		if (this.blockingChunks.size() >= this.maxBlocking) {
			return null;
		} else if (!this.method_40574()) {
			return null;
		} else {
			int i = this.firstNonEmptyLevel;
			Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap<List<Optional<T>>>)this.levelToPosToElements
				.get(i);
			long l = long2ObjectLinkedOpenHashMap.firstLongKey();
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.removeFirst();

			while (this.method_40574() && ((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(this.firstNonEmptyLevel)).isEmpty()) {
				this.firstNonEmptyLevel++;
			}

			return list.stream().map(optional -> (Either)optional.map(Either::left).orElseGet(() -> Either.right(this.createBlockingAdder(l))));
		}
	}

	public boolean method_40574() {
		return this.firstNonEmptyLevel < LEVEL_COUNT;
	}

	public String toString() {
		return this.name + " " + this.firstNonEmptyLevel + "...";
	}

	@VisibleForTesting
	LongSet getBlockingChunks() {
		return new LongOpenHashSet(this.blockingChunks);
	}
}
