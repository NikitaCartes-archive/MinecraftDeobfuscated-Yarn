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
	protected final RecipeType<?> type;
	protected final Identifier id;
	protected final String group;
	protected final Ingredient input;
	protected final ItemStack output;
	protected final float experience;
	protected final int cookTime;

	public CookingRecipe(RecipeType<?> recipeType, Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		this.type = recipeType;
		this.id = identifier;
		this.group = string;
		this.input = ingredient;
		this.output = itemStack;
		this.experience = f;
		this.cookTime = i;
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.input.method_8093(inventory.getInvStack(0));
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
	public DefaultedList<Ingredient> getPreviewInputs() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.create();
		defaultedList.add(this.input);
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
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeType<?> getType() {
		return this.type;
	}
}
