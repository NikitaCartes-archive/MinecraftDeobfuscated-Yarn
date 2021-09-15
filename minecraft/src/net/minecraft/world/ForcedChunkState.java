package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NbtCompound;

public class ForcedChunkState extends PersistentState {
	public static final String CHUNKS_KEY = "chunks";
	private static final String FORCED_KEY = "Forced";
	private final LongSet chunks;

	private ForcedChunkState(LongSet chunks) {
		this.chunks = chunks;
	}

	public ForcedChunkState() {
		this(new LongOpenHashSet());
	}

	public static ForcedChunkState fromNbt(NbtCompound nbt) {
		return new ForcedChunkState(new LongOpenHashSet(nbt.getLongArray("Forced")));
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putLongArray("Forced", this.chunks.toLongArray());
		return nbt;
	}

	public LongSet getChunks() {
		return this.chunks;
	}
}
