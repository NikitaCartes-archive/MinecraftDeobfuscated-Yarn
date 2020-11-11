package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;

public class ChunkUpdateState extends PersistentState {
	private final LongSet all;
	private final LongSet remaining;

	private ChunkUpdateState(LongSet longSet, LongSet longSet2) {
		this.all = longSet;
		this.remaining = longSet2;
	}

	public ChunkUpdateState() {
		this(new LongOpenHashSet(), new LongOpenHashSet());
	}

	public static ChunkUpdateState method_32358(CompoundTag compoundTag) {
		return new ChunkUpdateState(new LongOpenHashSet(compoundTag.getLongArray("All")), new LongOpenHashSet(compoundTag.getLongArray("Remaining")));
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
