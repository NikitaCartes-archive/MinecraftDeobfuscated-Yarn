package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.Locale;
import net.minecraft.world.chunk.ChunkStatus;

public final class class_9767 {
	private final ImmutableList<ChunkStatus> field_51898;
	private final int[] field_51899;

	public class_9767(ImmutableList<ChunkStatus> immutableList) {
		this.field_51898 = immutableList;
		int i = immutableList.isEmpty() ? 0 : ((ChunkStatus)immutableList.getFirst()).getIndex() + 1;
		this.field_51899 = new int[i];

		for (int j = 0; j < immutableList.size(); j++) {
			ChunkStatus chunkStatus = (ChunkStatus)immutableList.get(j);
			int k = chunkStatus.getIndex();

			for (int l = 0; l <= k; l++) {
				this.field_51899[l] = j;
			}
		}
	}

	@VisibleForTesting
	public ImmutableList<ChunkStatus> method_60513() {
		return this.field_51898;
	}

	public int method_60516() {
		return this.field_51898.size();
	}

	public int method_60515(ChunkStatus chunkStatus) {
		int i = chunkStatus.getIndex();
		if (i >= this.field_51899.length) {
			throw new IllegalArgumentException(String.format(Locale.ROOT, "Requesting a ChunkStatus(%s) outside of dependency range(%s)", chunkStatus, this.field_51898));
		} else {
			return this.field_51899[i];
		}
	}

	public int method_60517() {
		return Math.max(0, this.field_51898.size() - 1);
	}

	public ChunkStatus method_60514(int i) {
		return (ChunkStatus)this.field_51898.get(i);
	}

	public String toString() {
		return this.field_51898.toString();
	}
}
