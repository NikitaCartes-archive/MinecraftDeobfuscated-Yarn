package net.minecraft.world.storage;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.math.ChunkPos;

public class ChunkDataList<T> {
	private final ChunkPos pos;
	private final List<T> backingList;

	public ChunkDataList(ChunkPos pos, List<T> list) {
		this.pos = pos;
		this.backingList = list;
	}

	public ChunkPos getChunkPos() {
		return this.pos;
	}

	public Stream<T> stream() {
		return this.backingList.stream();
	}

	public boolean isEmpty() {
		return this.backingList.isEmpty();
	}
}
