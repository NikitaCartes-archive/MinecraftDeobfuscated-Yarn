package net.minecraft;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.math.ChunkPos;

public class class_5566<T> {
	private final ChunkPos field_27241;
	private final List<T> field_27242;

	public class_5566(ChunkPos chunkPos, List<T> list) {
		this.field_27241 = chunkPos;
		this.field_27242 = list;
	}

	public ChunkPos method_31741() {
		return this.field_27241;
	}

	public Stream<T> method_31742() {
		return this.field_27242.stream();
	}

	public boolean method_31743() {
		return this.field_27242.isEmpty();
	}
}
