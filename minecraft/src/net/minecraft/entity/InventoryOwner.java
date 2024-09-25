package net.minecraft.entity;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;

public interface InventoryOwner {
	String INVENTORY_KEY = "Inventory";

	SimpleInventory getInventory();

	static void pickUpItem(ServerWorld world, MobEntity entity, InventoryOwner inventoryOwner, ItemEntity item) {
		ItemStack itemStack = item.getStack();
		if (entity.canGather(world, itemStack)) {
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

	default void readInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		if (nbt.contains("Inventory", NbtElement.LIST_TYPE)) {
			this.getInventory().readNbtList(nbt.getList("Inventory", NbtElement.COMPOUND_TYPE), registries);
		}
	}

	default void writeInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		nbt.put("Inventory", this.getInventory().toNbtList(registries));
	}
}
