package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.PersistedState;

public class class_1932 extends PersistedState {
	private LongSet field_9213 = new LongOpenHashSet();

	public class_1932(String string) {
		super(string);
	}

	@Override
	public void deserialize(CompoundTag compoundTag) {
		this.field_9213 = new LongOpenHashSet(compoundTag.getLongArray("Forced"));
	}

	@Override
	public CompoundTag serialize(CompoundTag compoundTag) {
		compoundTag.putLongArray("Forced", this.field_9213.toLongArray());
		return compoundTag;
	}

	public LongSet method_8375() {
		return this.field_9213;
	}
}
