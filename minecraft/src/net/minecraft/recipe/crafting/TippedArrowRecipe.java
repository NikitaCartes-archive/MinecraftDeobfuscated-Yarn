package net.minecraft.recipe.crafting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.AbstractRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeSerializers;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class TippedArrowRecipe extends AbstractRecipe {
	public TippedArrowRecipe(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		if (inventory.getInvWidth() == 3 && inventory.getInvHeight() == 3) {
			for (int i = 0; i < inventory.getInvWidth(); i++) {
				for (int j = 0; j < inventory.getInvHeight(); j++) {
					ItemStack itemStack = inventory.getInvStack(i + j * inventory.getInvWidth());
					if (itemStack.isEmpty()) {
						return false;
					}

					Item item = itemStack.getItem();
					if (i == 1 && j == 1) {
						if (item != Items.field_8150) {
							return false;
						}
					} else if (item != Items.field_8107) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		ItemStack itemStack = inventory.getInvStack(1 + inventory.getInvWidth());
		if (itemStack.getItem() != Items.field_8150) {
			return ItemStack.EMPTY;
		} else {
			ItemStack itemStack2 = new ItemStack(Items.field_8087, 8);
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
		return RecipeSerializers.TIPPED_ARROW;
	}
}
