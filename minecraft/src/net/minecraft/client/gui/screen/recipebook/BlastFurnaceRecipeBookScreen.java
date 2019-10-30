package net.minecraft.client.gui.screen.recipebook;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;

@Environment(EnvType.CLIENT)
public class BlastFurnaceRecipeBookScreen extends AbstractFurnaceRecipeBookScreen {
	@Override
	protected boolean isFilteringCraftable() {
		return this.recipeBook.isBlastFurnaceFilteringCraftable();
	}

	@Override
	protected void setFilteringCraftable(boolean filteringCraftable) {
		this.recipeBook.setBlastFurnaceFilteringCraftable(filteringCraftable);
	}

	@Override
	protected boolean isGuiOpen() {
		return this.recipeBook.isBlastFurnaceGuiOpen();
	}

	@Override
	protected void setGuiOpen(boolean opened) {
		this.recipeBook.setBlastFurnaceGuiOpen(opened);
	}

	@Override
	protected String getToggleCraftableButtonText() {
		return "gui.recipebook.toggleRecipes.blastable";
	}

	@Override
	protected Set<Item> getAllowedFuels() {
		return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
	}
}
