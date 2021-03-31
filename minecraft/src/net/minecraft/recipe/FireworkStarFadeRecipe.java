package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FireworkStarFadeRecipe extends SpecialCraftingRecipe {
	private static final Ingredient INPUT_STAR = Ingredient.ofItems(Items.FIREWORK_STAR);

	public FireworkStarFadeRecipe(Identifier identifier) {
		super(identifier);
	}

	public boolean matches(CraftingInventory craftingInventory, World world) {
		boolean bl = false;
		boolean bl2 = false;

		for (int i = 0; i < craftingInventory.size(); i++) {
			ItemStack itemStack = craftingInventory.getStack(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() instanceof DyeItem) {
					bl = true;
				} else {
					if (!INPUT_STAR.test(itemStack)) {
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

	public ItemStack craft(CraftingInventory craftingInventory) {
		List<Integer> list = Lists.<Integer>newArrayList();
		ItemStack itemStack = null;

		for (int i = 0; i < craftingInventory.size(); i++) {
			ItemStack itemStack2 = craftingInventory.getStack(i);
			Item item = itemStack2.getItem();
			if (item instanceof DyeItem) {
				list.add(((DyeItem)item).getColor().getFireworkColor());
			} else if (INPUT_STAR.test(itemStack2)) {
				itemStack = itemStack2.copy();
				itemStack.setCount(1);
			}
		}

		if (itemStack != null && !list.isEmpty()) {
			itemStack.getOrCreateSubTag("Explosion").putIntArray("FadeColors", list);
			return itemStack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.FIREWORK_STAR_FADE;
	}
}
