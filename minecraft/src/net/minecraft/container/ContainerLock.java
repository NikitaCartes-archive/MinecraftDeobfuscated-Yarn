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

	public boolean method_5472(ItemStack itemStack) {
		return this.key.isEmpty() || !itemStack.isEmpty() && itemStack.hasDisplayName() && this.key.equals(itemStack.method_7964().getString());
	}

	public void method_5474(CompoundTag compoundTag) {
		if (!this.key.isEmpty()) {
			compoundTag.putString("Lock", this.key);
		}
	}

	public static ContainerLock method_5473(CompoundTag compoundTag) {
		return compoundTag.containsKey("Lock", 8) ? new ContainerLock(compoundTag.getString("Lock")) : NONE;
	}
}
