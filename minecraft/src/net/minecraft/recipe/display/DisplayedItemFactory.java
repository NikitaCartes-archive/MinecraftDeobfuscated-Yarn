package net.minecraft.recipe.display;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

public interface DisplayedItemFactory<T> {
	public interface FromRemainder<T> extends DisplayedItemFactory<T> {
		T toDisplayed(T input, List<T> remainders);
	}

	public interface FromStack<T> extends DisplayedItemFactory<T> {
		default T toDisplayed(RegistryEntry<Item> item) {
			return this.toDisplayed(new ItemStack(item));
		}

		default T toDisplayed(Item item) {
			return this.toDisplayed(new ItemStack(item));
		}

		T toDisplayed(ItemStack stack);
	}
}
