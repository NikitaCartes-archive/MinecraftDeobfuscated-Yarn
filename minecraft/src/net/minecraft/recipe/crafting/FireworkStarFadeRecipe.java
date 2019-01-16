package net.minecraft.recipe.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FireworkStarFadeRecipe extends SpecialCraftingRecipe {
	private static final Ingredient field_9015 = Ingredient.ofItems(Items.field_8450);

	public FireworkStarFadeRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean method_17711(CraftingInventory craftingInventory, World world) {
		boolean bl = false;
		boolean bl2 = false;

		for (int i = 0; i < craftingInventory.getInvSize(); i++) {
			ItemStack itemStack = craftingInventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() instanceof DyeItem) {
					bl = true;
				} else {
					if (!field_9015.matches(itemStack)) {
						return false;
					}

					if (bl2) {
						return false;
					}

					bl2 = true;
				}
			}
		}

		return bl2 && bl;
	}

	public ItemStack method_17710(CraftingInventory craftingInventory) {
		List<Integer> list = Lists.<Integer>newArrayList();
		ItemStack itemStack = null;

		for (int i = 0; i < craftingInventory.getInvSize(); i++) {
			ItemStack itemStack2 = craftingInventory.getInvStack(i);
			Item item = itemStack2.getItem();
			if (item instanceof DyeItem) {
				list.add(((DyeItem)item).getColor().getFireworkColor());
			} else if (field_9015.matches(itemStack2)) {
				itemStack = itemStack2.copy();
				itemStack.setAmount(1);
			}
		}

		if (itemStack != null && !list.isEmpty()) {
			itemStack.getOrCreateSubCompoundTag("Explosion").putIntArray("FadeColors", list);
			return itemStack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public RecipeSerializer<?> method_8119() {
		return RecipeSerializer.field_9034;
	}
}
