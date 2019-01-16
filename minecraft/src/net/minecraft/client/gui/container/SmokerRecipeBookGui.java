package net.minecraft.client.gui.container;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;

@Environment(EnvType.CLIENT)
public class SmokerRecipeBookGui extends AbstractFurnaceRecipeBookGui {
	@Override
	protected boolean isFilteringCraftable() {
		return this.recipeBook.isSmokerFilteringCraftable();
	}

	@Override
	protected void method_17060(boolean bl) {
		this.recipeBook.setSmokerFilteringCraftable(bl);
	}

	@Override
	protected boolean isGuiOpen() {
		return this.recipeBook.isSmokerGuiOpen();
	}

	@Override
	protected void setGuiOpen(boolean bl) {
		this.recipeBook.setSmokerGuiOpen(bl);
	}

	@Override
	protected String getToggleCraftableButtonText() {
		return "gui.recipebook.toggleRecipes.smokable";
	}

	@Override
	protected Set<Item> getAllowedFuels() {
		return AbstractFurnaceBlockEntity.createBurnableMap().keySet();
	}
}
