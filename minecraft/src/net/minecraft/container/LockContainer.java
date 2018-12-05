package net.minecraft.container;

import javax.annotation.concurrent.Immutable;
import net.minecraft.nbt.CompoundTag;

@Immutable
public class LockContainer {
	public static final LockContainer EMPTY = new LockContainer("");
	private final String key;

	public LockContainer(String string) {
		this.key = string;
	}

	public boolean isEmpty() {
		return this.key == null || this.key.isEmpty();
	}

	public String getKey() {
		return this.key;
	}

	public void serialize(CompoundTag compoundTag) {
		compoundTag.putString("Lock", this.key);
	}

	public static LockContainer deserialize(CompoundTag compoundTag) {
		if (compoundTag.containsKey("Lock", 8)) {
			String string = compoundTag.getString("Lock");
			return new LockContainer(string);
		} else {
			return EMPTY;
		}
	}
}
