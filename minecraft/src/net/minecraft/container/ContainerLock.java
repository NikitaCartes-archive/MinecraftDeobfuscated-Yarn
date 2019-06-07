package net.minecraft.container;

import javax.annotation.concurrent.Immutable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

@Immutable
public class ContainerLock {
	public static final ContainerLock NONE = new ContainerLock("");
	private final String key;

	public ContainerLock(String string) {
		this.key = string;
	}

	public boolean isEmpty(ItemStack itemStack) {
		return this.key.isEmpty() || !itemStack.isEmpty() && itemStack.hasCustomName() && this.key.equals(itemStack.getName().getString());
	}

	public void serialize(CompoundTag compoundTag) {
		if (!this.key.isEmpty()) {
			compoundTag.putString("Lock", this.key);
		}
	}

	public static ContainerLock deserialize(CompoundTag compoundTag) {
		return compoundTag.containsKey("Lock", 8) ? new ContainerLock(compoundTag.getString("Lock")) : NONE;
	}
}
