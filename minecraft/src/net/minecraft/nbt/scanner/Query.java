package net.minecraft.nbt.scanner;

import java.util.List;
import net.minecraft.nbt.NbtType;

public record Query(List<String> path, NbtType<?> type, String key) {
	public Query(NbtType<?> type, String key) {
		this(List.of(), type, key);
	}

	public Query(String path, NbtType<?> type, String key) {
		this(List.of(path), type, key);
	}

	public Query(String path1, String path2, NbtType<?> type, String key) {
		this(List.of(path1, path2), type, key);
	}
}
