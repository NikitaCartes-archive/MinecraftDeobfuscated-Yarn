package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.PersistedState;

public class class_3440 extends PersistedState {
	private LongSet all = new LongOpenHashSet();
	private LongSet remaining = new LongOpenHashSet();

	public class_3440(String string) {
		super(string);
	}

	@Override
	public void deserialize(CompoundTag compoundTag) {
		this.all = new LongOpenHashSet(compoundTag.getLongArray("All"));
		this.remaining = new LongOpenHashSet(compoundTag.getLongArray("Remaining"));
	}

	@Override
	public CompoundTag serialize(CompoundTag compoundTag) {
		compoundTag.putLongArray("All", this.all.toLongArray());
		compoundTag.putLongArray("Remaining", this.remaining.toLongArray());
		return compoundTag;
	}

	public void method_14896(long l) {
		this.all.add(l);
		this.remaining.add(l);
	}

	public boolean method_14897(long l) {
		return this.all.contains(l);
	}

	public boolean method_14894(long l) {
		return this.remaining.contains(l);
	}

	public void method_14895(long l) {
		this.remaining.remove(l);
	}

	public LongSet method_14898() {
		return this.all;
	}
}
