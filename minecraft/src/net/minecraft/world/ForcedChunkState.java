package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;

public class ForcedChunkState extends PersistentState {
	private LongSet chunks = new LongOpenHashSet();

	public ForcedChunkState() {
		super("chunks");
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		this.chunks = new LongOpenHashSet(compoundTag.getLongArray("Forced"));
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		compoundTag.putLongArray("Forced", this.chunks.toLongArray());
		return compoundTag;
	}

	public LongSet getChunks() {
		return this.chunks;
	}
}
