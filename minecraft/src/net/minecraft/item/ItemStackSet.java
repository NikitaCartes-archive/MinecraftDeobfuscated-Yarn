package net.minecraft.item;

import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;

public class ItemStackSet {
	private static final Strategy<? super ItemStack> HASH_STRATEGY = new Strategy<ItemStack>() {
		public int hashCode(@Nullable ItemStack itemStack) {
			return ItemStackSet.getHashCode(itemStack);
		}

		public boolean equals(@Nullable ItemStack itemStack, @Nullable ItemStack itemStack2) {
			return itemStack == itemStack2
				|| itemStack != null && itemStack2 != null && itemStack.isEmpty() == itemStack2.isEmpty() && ItemStack.areItemsAndNbtEqual(itemStack, itemStack2);
		}
	};

	static int getHashCode(@Nullable ItemStack stack) {
		if (stack != null) {
			NbtCompound nbtCompound = stack.getNbt();
			int i = 31 + stack.getItem().hashCode();
			return 31 * i + (nbtCompound == null ? 0 : nbtCompound.hashCode());
		} else {
			return 0;
		}
	}

	public static Set<ItemStack> create() {
		return new ObjectLinkedOpenCustomHashSet<>(HASH_STRATEGY);
	}
}
