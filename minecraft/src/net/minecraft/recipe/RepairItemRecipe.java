package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class RepairItemRecipe extends SpecialCraftingRecipe {
	public RepairItemRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean method_20808(CraftingInventory craftingInventory, World world) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		for (int i = 0; i < craftingInventory.getInvSize(); i++) {
			ItemStack itemStack = craftingInventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				list.add(itemStack);
				if (list.size() > 1) {
					ItemStack itemStack2 = (ItemStack)list.get(0);
					if (itemStack.getItem() != itemStack2.getItem() || itemStack2.getCount() != 1 || itemStack.getCount() != 1 || !itemStack2.getItem().isDamageable()) {
						return false;
					}
				}
			}
		}

		return list.size() == 2;
	}

	public ItemStack method_20807(CraftingInventory craftingInventory) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		for (int i = 0; i < craftingInventory.getInvSize(); i++) {
			ItemStack itemStack = craftingInventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				list.add(itemStack);
				if (list.size() > 1) {
					ItemStack itemStack2 = (ItemStack)list.get(0);
					if (itemStack.getItem() != itemStack2.getItem() || itemStack2.getCount() != 1 || itemStack.getCount() != 1 || !itemStack2.getItem().isDamageable()) {
						return ItemStack.EMPTY;
					}
				}
			}
		}

		if (list.size() == 2) {
			ItemStack itemStack3 = (ItemStack)list.get(0);
			ItemStack itemStack = (ItemStack)list.get(1);
			if (itemStack3.getItem() == itemStack.getItem() && itemStack3.getCount() == 1 && itemStack.getCount() == 1 && itemStack3.getItem().isDamageable()) {
				Item item = itemStack3.getItem();
				int j = item.getMaxDamage() - itemStack3.getDamage();
				int k = item.getMaxDamage() - itemStack.getDamage();
				int l = j + k + item.getMaxDamage() * 5 / 100;
				int m = item.getMaxDamage() - l;
				if (m < 0) {
					m = 0;
				}

				ItemStack itemStack4 = new ItemStack(itemStack3.getItem());
				itemStack4.setDamage(m);
				return itemStack4;
			}
		}

		return ItemStack.EMPTY;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.REPAIR_ITEM;
	}
}