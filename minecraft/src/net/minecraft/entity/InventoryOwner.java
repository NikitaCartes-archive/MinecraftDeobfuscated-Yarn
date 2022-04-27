package net.minecraft.entity;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public interface InventoryOwner {
	SimpleInventory getInventory();

	static void pickUpItem(MobEntity entity, InventoryOwner inventoryOwner, ItemEntity item) {
		ItemStack itemStack = item.getStack();
		if (entity.canGather(itemStack)) {
			SimpleInventory simpleInventory = inventoryOwner.getInventory();
			boolean bl = simpleInventory.canInsert(itemStack);
			if (!bl) {
				return;
			}

			entity.triggerItemPickedUpByEntityCriteria(item);
			int i = itemStack.getCount();
			ItemStack itemStack2 = simpleInventory.addStack(itemStack);
			entity.sendPickup(item, i - itemStack2.getCount());
			if (itemStack2.isEmpty()) {
				item.discard();
			} else {
				itemStack.setCount(itemStack2.getCount());
			}
		}
	}
}
