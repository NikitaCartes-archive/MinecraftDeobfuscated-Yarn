package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;

public class ChunkUpdateState extends PersistentState {
	private LongSet all = new LongOpenHashSet();
	private LongSet remaining = new LongOpenHashSet();

	public ChunkUpdateState(String string) {
		super(string);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		this.all = new LongOpenHashSet(tag.getLongArray("All"));
		this.remaining = new LongOpenHashSet(tag.getLongArray("Remaining"));
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putLongArray("All", this.all.toLongArray());
		tag.putLongArray("Remaining", this.remaining.toLongArray());
		return tag;
	}

	public void add(long l) {
		this.all.add(l);
		this.remaining.add(l);
	}

	public boolean contains(long l) {
		return this.all.contains(l);
	}

	public boolean isRemaining(long l) {
		return this.remaining.contains(l);
	}

	public void markResolved(long l) {
		this.remaining.remove(l);
	}

	public LongSet getAll() {
		return this.all;
	}
}
