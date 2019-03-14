package net.minecraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderInventory;
import net.minecraft.village.TraderRecipe;

public class VillagerOutputSlot extends Slot {
	private final TraderInventory villagerInventory;
	private final PlayerEntity player;
	private int amount;
	private final Trader villager;

	public VillagerOutputSlot(PlayerEntity playerEntity, Trader trader, TraderInventory traderInventory, int i, int j, int k) {
		super(traderInventory, i, j, k);
		this.player = playerEntity;
		this.villager = trader;
		this.villagerInventory = traderInventory;
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
		TraderRecipe traderRecipe = this.villagerInventory.getVillagerRecipe();
		if (traderRecipe != null) {
			if (!this.villager.getTraderWorld().isClient() && traderRecipe.isDisabled()) {
				this.villager.useRecipe(traderRecipe);
			} else if (!traderRecipe.isDisabled()) {
				ItemStack itemStack2 = this.villagerInventory.getInvStack(0);
				ItemStack itemStack3 = this.villagerInventory.getInvStack(1);
				if (traderRecipe.depleteBuyItems(itemStack2, itemStack3) || traderRecipe.depleteBuyItems(itemStack3, itemStack2)) {
					this.villager.useRecipe(traderRecipe);
					playerEntity.increaseStat(Stats.field_15378);
					this.villagerInventory.setInvStack(0, itemStack2);
					this.villagerInventory.setInvStack(1, itemStack3);
				}
			}

			this.villager.setExperience(this.villager.getExperience() + traderRecipe.getRewardedExp());
		}

		return itemStack;
	}
}
