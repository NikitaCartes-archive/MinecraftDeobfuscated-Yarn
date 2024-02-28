package net.minecraft.inventory;

import com.mojang.serialization.Codec;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;

public record ContainerLock(String key) {
	/**
	 * An empty container lock that can always be opened.
	 */
	public static final ContainerLock EMPTY = new ContainerLock("");
	public static final Codec<ContainerLock> CODEC = Codec.STRING.xmap(ContainerLock::new, ContainerLock::key);
	public static final String LOCK_KEY = "Lock";

	/**
	 * Returns true if this lock can be opened with the key item stack.
	 * <p>
	 * An item stack is a valid key if the stack name matches the key string of this lock,
	 * or if the key string is empty.
	 */
	public boolean canOpen(ItemStack stack) {
		if (this.key.isEmpty()) {
			return true;
		} else {
			Text text = stack.get(DataComponentTypes.CUSTOM_NAME);
			return text != null && this.key.equals(text.getString());
		}
	}

	/**
	 * Inserts the key string of this lock into the {@code Lock} key of the NBT compound.
	 */
	public void writeNbt(NbtCompound nbt) {
		if (!this.key.isEmpty()) {
			nbt.putString("Lock", this.key);
		}
	}

	/**
	 * Creates a new {@code ContainerLock} from the {@code Lock} key of the NBT compound.
	 * <p>
	 * If the {@code Lock} key is not present, returns an empty lock.
	 */
	public static ContainerLock fromNbt(NbtCompound nbt) {
		return nbt.contains("Lock", NbtElement.STRING_TYPE) ? new ContainerLock(nbt.getString("Lock")) : EMPTY;
	}
}
