package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class TippedArrowRecipe extends SpecialCraftingRecipe {
	public TippedArrowRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean method_17741(CraftingInventory craftingInventory, World world) {
		if (craftingInventory.getWidth() == 3 && craftingInventory.getHeight() == 3) {
			for (int i = 0; i < craftingInventory.getWidth(); i++) {
				for (int j = 0; j < craftingInventory.getHeight(); j++) {
					ItemStack itemStack = craftingInventory.getInvStack(i + j * craftingInventory.getWidth());
					if (itemStack.isEmpty()) {
						return false;
					}

					Item item = itemStack.getItem();
					if (i == 1 && j == 1) {
						if (item != Items.LINGERING_POTION) {
							return false;
						}
					} else if (item != Items.ARROW) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public ItemStack method_17740(CraftingInventory craftingInventory) {
		ItemStack itemStack = craftingInventory.getInvStack(1 + craftingInventory.getWidth());
		if (itemStack.getItem() != Items.LINGERING_POTION) {
			return ItemStack.EMPTY;
		} else {
			ItemStack itemStack2 = new ItemStack(Items.TIPPED_ARROW, 8);
			PotionUtil.setPotion(itemStack2, PotionUtil.getPotion(itemStack));
			PotionUtil.setCustomPotionEffects(itemStack2, PotionUtil.getCustomPotionEffects(itemStack));
			return itemStack2;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i >= 2 && j >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.TIPPED_ARROW;
	}
}
