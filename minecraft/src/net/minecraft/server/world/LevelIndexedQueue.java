package net.minecraft.server.world;

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
import net.minecraft.world.chunk.ChunkPos;

public class LevelIndexedQueue<T> {
	public static final int LEVEL_COUNT = ServerChunkManager.LEVEL_COUNT + 2;
	private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> levelToPosToElements = (List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>>)IntStream.range(
			0, LEVEL_COUNT
		)
		.mapToObj(ix -> new Long2ObjectLinkedOpenHashMap())
		.collect(Collectors.toList());
	private volatile int currentLevel = LEVEL_COUNT;
	private final String name;
	private final LongSet chunkPositions = new LongOpenHashSet();
	private final int maxSize;

	public LevelIndexedQueue(String string, int i) {
		this.name = string;
		this.maxSize = i;
	}

	protected void updateLevel(int i, ChunkPos chunkPos, int j) {
		if (i < LEVEL_COUNT) {
			Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap<List<Optional<T>>>)this.levelToPosToElements
				.get(i);
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.remove(chunkPos.toLong());
			if (i == this.currentLevel) {
				while (this.currentLevel < LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(this.currentLevel)).isEmpty()) {
					this.currentLevel++;
				}
			}

			if (list != null && !list.isEmpty()) {
				((List)((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(j)).computeIfAbsent(chunkPos.toLong(), l -> Lists.newArrayList())).addAll(list);
				this.currentLevel = Math.min(this.currentLevel, j);
			}
		}
	}

	protected void method_17274(Optional<T> optional, long l, int i) {
		((List)((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(i)).computeIfAbsent(l, lx -> Lists.newArrayList())).add(optional);
		this.currentLevel = Math.min(this.currentLevel, i);
	}

	protected void method_17609(long l, boolean bl) {
		for (Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap : this.levelToPosToElements) {
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.get(l);
			if (list != null) {
				if (bl) {
					list.clear();
				} else {
					list.removeIf(optional -> !optional.isPresent());
				}

				if (list.isEmpty()) {
					long2ObjectLinkedOpenHashMap.remove(l);
				}
			}
		}

		while (this.currentLevel < LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(this.currentLevel)).isEmpty()) {
			this.currentLevel++;
		}

		this.chunkPositions.remove(l);
	}

	private Runnable createPositionAdder(long l) {
		return () -> this.chunkPositions.add(l);
	}

	@Nullable
	public Stream<Either<T, Runnable>> getNext() {
		if (this.chunkPositions.size() >= this.maxSize) {
			return null;
		} else if (this.currentLevel >= LEVEL_COUNT) {
			return null;
		} else {
			int i = this.currentLevel;
			Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap<List<Optional<T>>>)this.levelToPosToElements
				.get(i);
			long l = long2ObjectLinkedOpenHashMap.firstLongKey();
			List<Optional<T>> list = long2ObjectLinkedOpenHashMap.removeFirst();

			while (this.currentLevel < LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap)this.levelToPosToElements.get(this.currentLevel)).isEmpty()) {
				this.currentLevel++;
			}

			return list.stream().map(optional -> (Either)optional.map(Either::left).orElseGet(() -> Either.right(this.createPositionAdder(l))));
		}
	}

	public String toString() {
		return this.name + " " + this.currentLevel + "...";
	}
}
