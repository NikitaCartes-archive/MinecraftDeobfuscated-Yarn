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
	private final Trader field_7858;

	public VillagerOutputSlot(PlayerEntity playerEntity, Trader trader, TraderInventory traderInventory, int i, int j, int k) {
		super(traderInventory, i, j, k);
		this.player = playerEntity;
		this.field_7858 = trader;
		this.villagerInventory = traderInventory;
	}

	@Override
	public boolean method_7680(ItemStack itemStack) {
		return false;
	}

	@Override
	public ItemStack method_7671(int i) {
		if (this.hasStack()) {
			this.amount = this.amount + Math.min(i, this.method_7677().getAmount());
		}

		return super.method_7671(i);
	}

	@Override
	protected void method_7678(ItemStack itemStack, int i) {
		this.amount += i;
		this.method_7669(itemStack);
	}

	@Override
	protected void method_7669(ItemStack itemStack) {
		itemStack.method_7982(this.player.field_6002, this.player, this.amount);
		this.amount = 0;
	}

	@Override
	public ItemStack method_7667(PlayerEntity playerEntity, ItemStack itemStack) {
		this.method_7669(itemStack);
		TraderRecipe traderRecipe = this.villagerInventory.method_7642();
		if (traderRecipe != null) {
			if (!this.field_7858.method_8260().isClient() && traderRecipe.isDisabled()) {
				this.field_7858.method_8262(traderRecipe);
			} else if (!traderRecipe.isDisabled()) {
				ItemStack itemStack2 = this.villagerInventory.method_5438(0);
				ItemStack itemStack3 = this.villagerInventory.method_5438(1);
				if (traderRecipe.depleteBuyItems(itemStack2, itemStack3) || traderRecipe.depleteBuyItems(itemStack3, itemStack2)) {
					this.field_7858.method_8262(traderRecipe);
					playerEntity.method_7281(Stats.field_15378);
					this.villagerInventory.method_5447(0, itemStack2);
					this.villagerInventory.method_5447(1, itemStack3);
				}
			}

			this.field_7858.method_19271(this.field_7858.method_19269() + traderRecipe.method_19279());
		}

		return itemStack;
	}
}
