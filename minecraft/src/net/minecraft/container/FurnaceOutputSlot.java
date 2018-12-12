package net.minecraft.container;

import java.util.Map.Entry;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.recipe.smelting.AbstractSmeltingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class FurnaceOutputSlot extends Slot {
	private final PlayerEntity player;
	private int field_7819;

	public FurnaceOutputSlot(PlayerEntity playerEntity, Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
		this.player = playerEntity;
	}

	@Override
	public boolean canInsert(ItemStack itemStack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int i) {
		if (this.hasStack()) {
			this.field_7819 = this.field_7819 + Math.min(i, this.getStack().getAmount());
		}

		return super.takeStack(i);
	}

	@Override
	public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
		this.onCrafted(itemStack);
		super.onTakeItem(playerEntity, itemStack);
		return itemStack;
	}

	@Override
	protected void onCrafted(ItemStack itemStack, int i) {
		this.field_7819 += i;
		this.onCrafted(itemStack);
	}

	@Override
	protected void onCrafted(ItemStack itemStack) {
		itemStack.onCrafted(this.player.world, this.player, this.field_7819);
		if (!this.player.world.isClient) {
			for (Entry<Identifier, Integer> entry : ((AbstractFurnaceBlockEntity)this.inventory).getRecipesUsed().entrySet()) {
				AbstractSmeltingRecipe abstractSmeltingRecipe = (AbstractSmeltingRecipe)this.player.world.getRecipeManager().get((Identifier)entry.getKey());
				float f;
				if (abstractSmeltingRecipe != null) {
					f = abstractSmeltingRecipe.getExperience();
				} else {
					f = 0.0F;
				}

				int i = (Integer)entry.getValue();
				if (f == 0.0F) {
					i = 0;
				} else if (f < 1.0F) {
					int j = MathHelper.floor((float)i * f);
					if (j < MathHelper.ceil((float)i * f) && Math.random() < (double)((float)i * f - (float)j)) {
						j++;
					}

					i = j;
				}

				while (i > 0) {
					int j = ExperienceOrbEntity.roundToOrbSize(i);
					i -= j;
					this.player.world.spawnEntity(new ExperienceOrbEntity(this.player.world, this.player.x, this.player.y + 0.5, this.player.z + 0.5, j));
				}
			}

			((RecipeUnlocker)this.inventory).unlockLastRecipe(this.player);
		}

		this.field_7819 = 0;
	}
}
