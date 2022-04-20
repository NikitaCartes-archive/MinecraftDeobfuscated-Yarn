package net.minecraft.client.gui.screen.recipebook;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SmokerRecipeBookScreen extends AbstractFurnaceRecipeBookScreen {
	private static final Text TOGGLE_SMOKABLE_RECIPES_TEXT = Text.translatable("gui.recipebook.toggleRecipes.smokable");

	@Override
	protected Text getToggleCraftableButtonText() {
		return TOGGLE_SMOKABLE_RECIPES_TEXT;
	}

	@Override
	protected Set<Item> getAllowedFuels() {
		return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
	}
}
