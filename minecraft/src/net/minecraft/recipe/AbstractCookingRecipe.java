package net.minecraft.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class AbstractCookingRecipe implements Recipe<Inventory> {
	protected final RecipeType<?> type;
	protected final Identifier id;
	protected final String group;
	protected final Ingredient input;
	protected final ItemStack output;
	protected final float experience;
	protected final int cookTime;

	public AbstractCookingRecipe(RecipeType<?> type, Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
		this.type = type;
		this.id = id;
		this.group = group;
		this.input = input;
		this.output = output;
		this.experience = experience;
		this.cookTime = cookTime;
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		return this.input.test(inv.getInvStack(0));
	}

	@Override
	public ItemStack craft(Inventory inv) {
		return this.output.copy();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public DefaultedList<Ingredient> getPreviewInputs() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.of();
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
