package net.minecraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.Trader;
import net.minecraft.village.TraderInventory;

public class TradeOutputSlot extends Slot {
	private final TraderInventory traderInventory;
	private final PlayerEntity player;
	private int amount;
	private final Trader trader;

	public TradeOutputSlot(PlayerEntity playerEntity, Trader trader, TraderInventory traderInventory, int i, int j, int k) {
		super(traderInventory, i, j, k);
		this.player = playerEntity;
		this.trader = trader;
		this.traderInventory = traderInventory;
	}

	@Override
	public boolean canInsert(ItemStack itemStack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int i) {
		if (this.hasStack()) {
			this.amount = this.amount + Math.min(i, this.getStack().getCount());
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
		itemStack.onCraft(this.player.world, this.player, this.amount);
		this.amount = 0;
	}

	@Override
	public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
		this.onCrafted(itemStack);
		TradeOffer tradeOffer = this.traderInventory.getTradeOffer();
		if (tradeOffer != null) {
			ItemStack itemStack2 = this.traderInventory.getInvStack(0);
			ItemStack itemStack3 = this.traderInventory.getInvStack(1);
			if (tradeOffer.depleteBuyItems(itemStack2, itemStack3) || tradeOffer.depleteBuyItems(itemStack3, itemStack2)) {
				this.trader.trade(tradeOffer);
				playerEntity.incrementStat(Stats.field_15378);
				this.traderInventory.setInvStack(0, itemStack2);
				this.traderInventory.setInvStack(1, itemStack3);
			}

			this.trader.setExperienceFromServer(this.trader.getExperience() + tradeOffer.getTraderExperience());
		}

		return itemStack;
	}
}
