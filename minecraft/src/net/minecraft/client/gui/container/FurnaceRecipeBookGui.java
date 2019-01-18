package net.minecraft.client.gui.container;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;

@Environment(EnvType.CLIENT)
public class FurnaceRecipeBookGui extends AbstractFurnaceRecipeBookGui {
	@Override
	protected boolean isFilteringCraftable() {
		return this.recipeBook.isFurnaceFilteringCraftable();
	}

	@Override
	protected void method_17060(boolean bl) {
		this.recipeBook.setFurnaceFilteringCraftable(bl);
	}

	@Override
	protected boolean isGuiOpen() {
		return this.recipeBook.isFurnaceGuiOpen();
	}

	@Override
	protected void setGuiOpen(boolean bl) {
		this.recipeBook.setFurnaceGuiOpen(bl);
	}

	@Override
	protected String getToggleCraftableButtonText() {
		return "gui.recipebook.toggleRecipes.smeltable";
	}

	@Override
	protected Set<Item> getAllowedFuels() {
		return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
	}
}
