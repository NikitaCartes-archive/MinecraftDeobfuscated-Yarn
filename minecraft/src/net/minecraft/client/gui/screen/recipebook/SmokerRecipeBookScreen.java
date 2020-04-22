package net.minecraft.client.gui.screen.recipebook;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SmokerRecipeBookScreen extends AbstractFurnaceRecipeBookScreen {
	@Override
	protected boolean isFilteringCraftable() {
		return this.recipeBook.isSmokerFilteringCraftable();
	}

	@Override
	protected void setFilteringCraftable(boolean filteringCraftable) {
		this.recipeBook.setSmokerFilteringCraftable(filteringCraftable);
	}

	@Override
	protected boolean isGuiOpen() {
		return this.recipeBook.isSmokerGuiOpen();
	}

	@Override
	protected void setGuiOpen(boolean opened) {
		this.recipeBook.setSmokerGuiOpen(opened);
	}

	@Override
	protected Text getToggleCraftableButtonText() {
		return new TranslatableText("gui.recipebook.toggleRecipes.smokable");
	}

	@Override
	protected Set<Item> getAllowedFuels() {
		return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
	}
}
