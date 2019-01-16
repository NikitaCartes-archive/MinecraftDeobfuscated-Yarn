package net.minecraft.world;

import net.minecraft.nbt.CompoundTag;

public abstract class PersistentState {
	private final String key;
	private boolean dirty;

	public PersistentState(String string) {
		this.key = string;
	}

	public abstract void fromTag(CompoundTag compoundTag);

	public abstract CompoundTag toTag(CompoundTag compoundTag);

	public void markDirty() {
		this.setDirty(true);
	}

	public void setDirty(boolean bl) {
		this.dirty = bl;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public String getId() {
		return this.key;
	}
}
