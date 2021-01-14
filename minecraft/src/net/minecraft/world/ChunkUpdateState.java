package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NbtCompound;

public class ChunkUpdateState extends PersistentState {
	private LongSet all = new LongOpenHashSet();
	private LongSet remaining = new LongOpenHashSet();

	public ChunkUpdateState(String string) {
		super(string);
	}

	@Override
	public void fromTag(NbtCompound tag) {
		this.all = new LongOpenHashSet(tag.getLongArray("All"));
		this.remaining = new LongOpenHashSet(tag.getLongArray("Remaining"));
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putLongArray("All", this.all.toLongArray());
		nbt.putLongArray("Remaining", this.remaining.toLongArray());
		return nbt;
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
