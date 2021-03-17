package net.minecraft.inventory;

import javax.annotation.concurrent.Immutable;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

@Immutable
public class ContainerLock {
	/**
	 * An empty container lock that can always be opened.
	 */
	public static final ContainerLock EMPTY = new ContainerLock("");
	private final String key;

	public ContainerLock(String key) {
		this.key = key;
	}

	/**
	 * Returns true if this lock can be opened with the key item stack.
	 * <p>
	 * An item stack is a valid key if the stack name matches the key string of this lock,
	 * or if the key string is empty.
	 * 
	 * @param stack the key item stack
	 */
	public boolean canOpen(ItemStack stack) {
		return this.key.isEmpty() || !stack.isEmpty() && stack.hasCustomName() && this.key.equals(stack.getName().getString());
	}

	/**
	 * Inserts the key string of this lock into the {@code Lock} key of the compound tag.
	 */
	public void writeNbt(NbtCompound tag) {
		if (!this.key.isEmpty()) {
			tag.putString("Lock", this.key);
		}
	}

	/**
	 * Creates a new {@code ContainerLock} from the {@code Lock} key of the compound tag.
	 * <p>
	 * If the {@code Lock} key is not present, returns an empty lock.
	 */
	public static ContainerLock fromNbt(NbtCompound tag) {
		return tag.contains("Lock", NbtTypeIds.STRING) ? new ContainerLock(tag.getString("Lock")) : EMPTY;
	}
}
