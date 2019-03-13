package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class CraftingContainer<C extends Inventory> extends Container {
	public CraftingContainer(ContainerType<?> containerType, int i) {
		super(containerType, i);
	}

	public void method_17697(boolean bl, Recipe<?> recipe, ServerPlayerEntity serverPlayerEntity) {
		new InputSlotFiller<>(this).method_12826(serverPlayerEntity, (Recipe<C>)recipe, bl);
	}

	public abstract void populateRecipeFinder(RecipeFinder recipeFinder);

	public abstract void clearCraftingSlots();

	public abstract boolean method_7652(Recipe<? super C> recipe);

	public abstract int getCraftingResultSlotIndex();

	public abstract int getCraftingWidth();

	public abstract int getCraftingHeight();

	@Environment(EnvType.CLIENT)
	public abstract int getCraftingSlotCount();
}
