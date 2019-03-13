package net.minecraft.recipe.cooking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class CookingRecipe implements Recipe<Inventory> {
	protected final RecipeType<?> field_17544;
	protected final Identifier field_9060;
	protected final String group;
	protected final Ingredient field_9061;
	protected final ItemStack output;
	protected final float experience;
	protected final int cookTime;

	public CookingRecipe(RecipeType<?> recipeType, Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		this.field_17544 = recipeType;
		this.field_9060 = identifier;
		this.group = string;
		this.field_9061 = ingredient;
		this.output = itemStack;
		this.experience = f;
		this.cookTime = i;
	}

	@Override
	public boolean method_8115(Inventory inventory, World world) {
		return this.field_9061.method_8093(inventory.method_5438(0));
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		return this.output.copy();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return true;
	}

	@Override
	public DefaultedList<Ingredient> method_8117() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.create();
		defaultedList.add(this.field_9061);
		return defaultedList;
	}

	public float getExperience() {
		return this.experience;
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String getGroup() {
		return this.group;
	}

	public int getCookTime() {
		return this.cookTime;
	}

	@Override
	public Identifier method_8114() {
		return this.field_9060;
	}

	@Override
	public RecipeType<?> method_17716() {
		return this.field_17544;
	}
}
