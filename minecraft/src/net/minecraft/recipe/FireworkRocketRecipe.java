package net.minecraft.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class FireworkRocketRecipe extends SpecialCraftingRecipe {
	private static final Ingredient PAPER = Ingredient.ofItems(Items.PAPER);
	private static final Ingredient DURATION_MODIFIER = Ingredient.ofItems(Items.GUNPOWDER);
	private static final Ingredient FIREWORK_STAR = Ingredient.ofItems(Items.FIREWORK_STAR);

	public FireworkRocketRecipe(CraftingRecipeCategory craftingRecipeCategory) {
		super(craftingRecipeCategory);
	}

	public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
		boolean bl = false;
		int i = 0;

		for (int j = 0; j < craftingRecipeInput.getSize(); j++) {
			ItemStack itemStack = craftingRecipeInput.getStackInSlot(j);
			if (!itemStack.isEmpty()) {
				if (PAPER.test(itemStack)) {
					if (bl) {
						return false;
					}

					bl = true;
				} else if (DURATION_MODIFIER.test(itemStack)) {
					if (++i > 3) {
						return false;
					}
				} else if (!FIREWORK_STAR.test(itemStack)) {
					return false;
				}
			}
		}

		return bl && i >= 1;
	}

	public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
		List<FireworkExplosionComponent> list = new ArrayList();
		int i = 0;

		for (int j = 0; j < craftingRecipeInput.getSize(); j++) {
			ItemStack itemStack = craftingRecipeInput.getStackInSlot(j);
			if (!itemStack.isEmpty()) {
				if (DURATION_MODIFIER.test(itemStack)) {
					i++;
				} else if (FIREWORK_STAR.test(itemStack)) {
					FireworkExplosionComponent fireworkExplosionComponent = itemStack.get(DataComponentTypes.FIREWORK_EXPLOSION);
					if (fireworkExplosionComponent != null) {
						list.add(fireworkExplosionComponent);
					}
				}
			}
		}

		ItemStack itemStack2 = new ItemStack(Items.FIREWORK_ROCKET, 3);
		itemStack2.set(DataComponentTypes.FIREWORKS, new FireworksComponent(i, list));
		return itemStack2;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
		return new ItemStack(Items.FIREWORK_ROCKET);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeSerializer.FIREWORK_ROCKET;
	}
}
