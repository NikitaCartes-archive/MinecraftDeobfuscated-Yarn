package net.minecraft.nbt;

public abstract class PersistedState {
	private final String key;
	private boolean dirty;

	public PersistedState(String string) {
		this.key = string;
	}

	public abstract void deserialize(CompoundTag compoundTag);

	public abstract CompoundTag serialize(CompoundTag compoundTag);

	public void markDirty() {
		this.setDirty(true);
	}

	public void setDirty(boolean bl) {
		this.dirty = bl;
	}

	public boolean getDirty() {
		return this.dirty;
	}

	public String getKey() {
		return this.key;
	}
}
