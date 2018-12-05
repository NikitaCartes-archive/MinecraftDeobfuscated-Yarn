package net.minecraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.village.Villager;
import net.minecraft.village.VillagerInventory;
import net.minecraft.village.VillagerRecipe;

public class VillagerOutputSlot extends Slot {
	private final VillagerInventory villagerInventory;
	private final PlayerEntity player;
	private int amount;
	private final Villager villager;

	public VillagerOutputSlot(PlayerEntity playerEntity, Villager villager, VillagerInventory villagerInventory, int i, int j, int k) {
		super(villagerInventory, i, j, k);
		this.player = playerEntity;
		this.villager = villager;
		this.villagerInventory = villagerInventory;
	}

	@Override
	public boolean canInsert(ItemStack itemStack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int i) {
		if (this.hasStack()) {
			this.amount = this.amount + Math.min(i, this.getStack().getAmount());
		}

		return super.takeStack(i);
	}

	@Override
	protected void onCrafted(ItemStack itemStack, int i) {
		this.amount += i;
		this.onCrafted(itemStack);
	}

	@Override
	protected void onCrafted(ItemStack itemStack) {
		itemStack.onCrafted(this.player.world, this.player, this.amount);
		this.amount = 0;
	}

	@Override
	public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
		this.onCrafted(itemStack);
		VillagerRecipe villagerRecipe = this.villagerInventory.getVillagerRecipe();
		if (villagerRecipe != null) {
			ItemStack itemStack2 = this.villagerInventory.getInvStack(0);
			ItemStack itemStack3 = this.villagerInventory.getInvStack(1);
			if (this.isValid(villagerRecipe, itemStack2, itemStack3) || this.isValid(villagerRecipe, itemStack3, itemStack2)) {
				this.villager.useRecipe(villagerRecipe);
				playerEntity.method_7281(Stats.field_15378);
				this.villagerInventory.setInvStack(0, itemStack2);
				this.villagerInventory.setInvStack(1, itemStack3);
			}
		}

		return itemStack;
	}

	private boolean isValid(VillagerRecipe villagerRecipe, ItemStack itemStack, ItemStack itemStack2) {
		ItemStack itemStack3 = villagerRecipe.getBuyItem();
		ItemStack itemStack4 = villagerRecipe.getSecondBuyItem();
		if (itemStack.getItem() == itemStack3.getItem() && itemStack.getAmount() >= itemStack3.getAmount()) {
			if (!itemStack4.isEmpty() && !itemStack2.isEmpty() && itemStack4.getItem() == itemStack2.getItem() && itemStack2.getAmount() >= itemStack4.getAmount()) {
				itemStack.subtractAmount(itemStack3.getAmount());
				itemStack2.subtractAmount(itemStack4.getAmount());
				return true;
			}

			if (itemStack4.isEmpty() && itemStack2.isEmpty()) {
				itemStack.subtractAmount(itemStack3.getAmount());
				return true;
			}
		}

		return false;
	}
}
