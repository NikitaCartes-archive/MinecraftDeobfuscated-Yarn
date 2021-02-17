package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;

public class ForcedChunkState extends PersistentState {
	private final LongSet chunks;

	private ForcedChunkState(LongSet chunks) {
		this.chunks = chunks;
	}

	public ForcedChunkState() {
		this(new LongOpenHashSet());
	}

	public static ForcedChunkState fromNbt(CompoundTag tag) {
		return new ForcedChunkState(new LongOpenHashSet(tag.getLongArray("Forced")));
	}

	@Override
	public CompoundTag writeNbt(CompoundTag tag) {
		tag.putLongArray("Forced", this.chunks.toLongArray());
		return tag;
	}

	public LongSet getChunks() {
		return this.chunks;
	}
}
