package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;

public class ForcedChunkState extends PersistentState {
	private final LongSet chunks;

	private ForcedChunkState(LongSet longSet) {
		this.chunks = longSet;
	}

	public ForcedChunkState() {
		this(new LongOpenHashSet());
	}

	public static ForcedChunkState method_32350(CompoundTag compoundTag) {
		return new ForcedChunkState(new LongOpenHashSet(compoundTag.getLongArray("Forced")));
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putLongArray("Forced", this.chunks.toLongArray());
		return tag;
	}

	public LongSet getChunks() {
		return this.chunks;
	}
}
