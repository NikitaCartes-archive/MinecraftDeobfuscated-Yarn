package net.minecraft.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public abstract class AbstractCookingRecipe implements Recipe<SingleStackRecipeInput> {
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

	public boolean matches(SingleStackRecipeInput singleStackRecipeInput, World world) {
		return this.ingredient.test(singleStackRecipeInput.item());
	}

	public ItemStack craft(SingleStackRecipeInput singleStackRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
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
	public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
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
