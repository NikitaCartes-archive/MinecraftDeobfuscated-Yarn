package net.minecraft.inventory;

import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;

public interface Inventory extends Clearable {
	int getInvSize();

	boolean isInvEmpty();

	ItemStack method_5438(int i);

	ItemStack method_5434(int i, int j);

	ItemStack method_5441(int i);

	void method_5447(int i, ItemStack itemStack);

	default int getInvMaxStackAmount() {
		return 64;
	}

	void markDirty();

	boolean method_5443(PlayerEntity playerEntity);

	default void method_5435(PlayerEntity playerEntity) {
	}

	default void method_5432(PlayerEntity playerEntity) {
	}

	default boolean method_5437(int i, ItemStack itemStack) {
		return true;
	}

	default int method_18861(Item item) {
		int i = 0;

		for (int j = 0; j < this.getInvSize(); j++) {
			ItemStack itemStack = this.method_5438(j);
			if (itemStack.getItem().equals(item)) {
				i += itemStack.getAmount();
			}
		}

		return i;
	}

	default boolean method_18862(Set<Item> set) {
		for (int i = 0; i < this.getInvSize(); i++) {
			ItemStack itemStack = this.method_5438(i);
			if (set.contains(itemStack.getItem()) && itemStack.getAmount() > 0) {
				return true;
			}
		}

		return false;
	}
}
