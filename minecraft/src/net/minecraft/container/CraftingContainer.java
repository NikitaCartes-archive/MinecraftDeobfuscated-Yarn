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

	public void fillInputSlots(boolean bl, Recipe<?> recipe, ServerPlayerEntity serverPlayerEntity) {
		new InputSlotFiller<>(this).fillInputSlots(serverPlayerEntity, (Recipe<C>)recipe, bl);
	}

	public abstract void populateRecipeFinder(RecipeFinder recipeFinder);

	public abstract void clearCraftingSlots();

	public abstract boolean matches(Recipe<? super C> recipe);

	public abstract int getCraftingResultSlotIndex();

	public abstract int getCraftingWidth();

	public abstract int getCraftingHeight();

	@Environment(EnvType.CLIENT)
	public abstract int getCraftingSlotCount();
}
