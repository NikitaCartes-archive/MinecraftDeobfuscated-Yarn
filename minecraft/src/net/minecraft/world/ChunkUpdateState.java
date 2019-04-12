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
	public void fromTag(CompoundTag compoundTag) {
		this.all = new LongOpenHashSet(compoundTag.getLongArray("All"));
		this.remaining = new LongOpenHashSet(compoundTag.getLongArray("Remaining"));
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		compoundTag.putLongArray("All", this.all.toLongArray());
		compoundTag.putLongArray("Remaining", this.remaining.toLongArray());
		return compoundTag;
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
