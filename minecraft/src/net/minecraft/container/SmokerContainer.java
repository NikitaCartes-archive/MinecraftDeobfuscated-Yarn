package net.minecraft.container;

import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.smelting.SmokingRecipe;

public class SmokerContainer extends AbstractFurnaceContainer {
	public SmokerContainer(PlayerInventory playerInventory, Inventory inventory) {
		super(playerInventory, inventory);
	}

	@Override
	protected boolean isSmeltable(ItemStack itemStack) {
		for (Recipe recipe : this.world.getRecipeManager().values()) {
			if (recipe instanceof SmokingRecipe && recipe.getPreviewInputs().get(0).matches(itemStack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean isFuel(ItemStack itemStack) {
		return SmokerBlockEntity.canUseAsFuel(itemStack);
	}
}
