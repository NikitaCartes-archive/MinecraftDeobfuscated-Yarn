package net.minecraft.item;

import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class ItemStackSet {
	private static final Strategy<? super ItemStack> HASH_STRATEGY = new Strategy<ItemStack>() {
		public int hashCode(@Nullable ItemStack itemStack) {
			return ItemStack.hashCode(itemStack);
		}

		public boolean equals(@Nullable ItemStack itemStack, @Nullable ItemStack itemStack2) {
			return itemStack == itemStack2
				|| itemStack != null && itemStack2 != null && itemStack.isEmpty() == itemStack2.isEmpty() && ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2);
		}
	};

	public static Set<ItemStack> create() {
		return new ObjectLinkedOpenCustomHashSet<>(HASH_STRATEGY);
	}
}
