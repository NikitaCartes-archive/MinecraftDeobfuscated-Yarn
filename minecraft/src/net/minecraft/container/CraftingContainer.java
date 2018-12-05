package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1662;
import net.minecraft.recipe.Recipe;

public abstract class CraftingContainer extends Container {
	public abstract void method_7654(class_1662 arg);

	public abstract void clearCraftingSlots();

	public abstract boolean matches(Recipe recipe);

	public abstract int getCraftingResultSlotIndex();

	public abstract int getCraftingWidth();

	public abstract int getCrafitngHeight();

	@Environment(EnvType.CLIENT)
	public abstract int method_7658();
}
