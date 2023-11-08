package net.minecraft.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public abstract class AbstractCookingRecipe implements Recipe<Inventory> {
	protected final RecipeType<?> type;
	protected final CookingRecipeCategory category;
	protected final String group;
	protected final Ingredient ingredient;
	protected final ItemStack result;
	protected final float experience;
	protected final int cookingTime;

	public AbstractCookingRecipe(
		RecipeType<?> type, String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime
	) {
		this.type = type;
		this.category = category;
		this.group = group;
		this.ingredient = ingredient;
		this.result = result;
		this.experience = experience;
		this.cookingTime = cookingTime;
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return this.ingredient.test(inventory.getStack(0));
	}

	@Override
	public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
		return this.result.copy();
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.of();
		defaultedList.add(this.ingredient);
		return defaultedList;
	}

	public float getExperience() {
		return this.experience;
	}

	@Override
	public ItemStack getResult(DynamicRegistryManager registryManager) {
		return this.result;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	public int getCookingTime() {
		return this.cookingTime;
	}

	@Override
	public RecipeType<?> getType() {
		return this.type;
	}

	public CookingRecipeCategory getCategory() {
		return this.category;
	}

	public interface RecipeFactory<T extends AbstractCookingRecipe> {
		T create(String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime);
	}
}
