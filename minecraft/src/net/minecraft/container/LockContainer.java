package net.minecraft.container;

import javax.annotation.concurrent.Immutable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

@Immutable
public class LockContainer {
	public static final LockContainer EMPTY = new LockContainer("");
	private final String key;

	public LockContainer(String string) {
		this.key = string;
	}

	public boolean isEmpty(ItemStack itemStack) {
		return this.key.isEmpty() || !itemStack.isEmpty() && itemStack.hasDisplayName() && this.key.equals(itemStack.getDisplayName().getString());
	}

	public void serialize(CompoundTag compoundTag) {
		if (!this.key.isEmpty()) {
			compoundTag.putString("Lock", this.key);
		}
	}

	public static LockContainer deserialize(CompoundTag compoundTag) {
		return compoundTag.containsKey("Lock", 8) ? new LockContainer(compoundTag.getString("Lock")) : EMPTY;
	}
}
