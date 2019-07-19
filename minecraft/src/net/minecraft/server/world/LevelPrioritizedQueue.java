package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
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
	private final LongSet chunkPositions = new LongOpenHashSet();
	private final int maxSize;

	public LevelPrioritizedQueue(String name, int maxSize) {
		this.name = name;
		this.maxSize = maxSize;
	}

	protected void updateLevel(int fromLevel, ChunkPos pos, int toLevel) {
		if (fromLevel < LEVEL_COUNT) {
			Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap<List<Optional<T>>>)this.levelToPosToElements
				.get(fromLevel);
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.remove(pos.toLong());
			if (fromLevel == this.firstNonEmptyLevel) {
				while (this.firstNonEmptyLevel < LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(this.firstNonEmptyLevel)).isEmpty()) {
					this.firstNonEmptyLevel++;
				}
			}

			if (list != null && !list.isEmpty()) {
				((List)((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(toLevel)).computeIfAbsent(pos.toLong(), l -> Lists.newArrayList())).addAll(list);
				this.firstNonEmptyLevel = Math.min(this.firstNonEmptyLevel, toLevel);
			}
		}
	}

	protected void add(Optional<T> element, long pos, int level) {
		((List)((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(level)).computeIfAbsent(pos, l -> Lists.newArrayList())).add(element);
		this.firstNonEmptyLevel = Math.min(this.firstNonEmptyLevel, level);
	}

	protected void clearPosition(long pos, boolean includePresent) {
		for (Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap : this.levelToPosToElements) {
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.get(pos);
			if (list != null) {
				if (includePresent) {
					list.clear();
				} else {
					list.removeIf(optional -> !optional.isPresent());
				}

				if (list.isEmpty()) {
					long2ObjectLinkedOpenHashMap.remove(pos);
				}
			}
		}

		while (this.firstNonEmptyLevel < LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(this.firstNonEmptyLevel)).isEmpty()) {
			this.firstNonEmptyLevel++;
		}

		this.chunkPositions.remove(pos);
	}

	private Runnable createPositionAdder(long pos) {
		return () -> this.chunkPositions.add(pos);
	}

	@Nullable
	public Stream<Either<T, Runnable>> poll() {
		if (this.chunkPositions.size() >= this.maxSize) {
			return null;
		} else if (this.firstNonEmptyLevel >= LEVEL_COUNT) {
			return null;
		} else {
			int i = this.firstNonEmptyLevel;
			Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap<List<Optional<T>>>)this.levelToPosToElements
				.get(i);
			long l = long2ObjectLinkedOpenHashMap.firstLongKey();
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.removeFirst();

			while (this.firstNonEmptyLevel < LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(this.firstNonEmptyLevel)).isEmpty()) {
				this.firstNonEmptyLevel++;
			}

			return list.stream().map(optional -> (Either)optional.map(Either::left).orElseGet(() -> Either.right(this.createPositionAdder(l))));
		}
	}

	public String toString() {
		return this.name + " " + this.firstNonEmptyLevel + "...";
	}

	@VisibleForTesting
	LongSet method_21679() {
		return new LongOpenHashSet(this.chunkPositions);
	}
}
