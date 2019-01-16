package net.minecraft.client.gui.container;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;

@Environment(EnvType.CLIENT)
public class BlastFurnaceRecipeBookGui extends AbstractFurnaceRecipeBookGui {
	@Override
	protected boolean isFilteringCraftable() {
		return this.recipeBook.isBlastFurnaceFilteringCraftable();
	}

	@Override
	protected void method_17060(boolean bl) {
		this.recipeBook.setBlastFurnaceFilteringCraftable(bl);
	}

	@Override
	protected boolean isGuiOpen() {
		return this.recipeBook.isBlastFurnaceGuiOpen();
	}

	@Override
	protected void setGuiOpen(boolean bl) {
		this.recipeBook.setBlastFurnaceGuiOpen(bl);
	}

	@Override
	protected String getToggleCraftableButtonText() {
		return "gui.recipebook.toggleRecipes.blastable";
	}

	@Override
	protected Set<Item> getAllowedFuels() {
		return AbstractFurnaceBlockEntity.createBurnableMap().keySet();
	}
}
