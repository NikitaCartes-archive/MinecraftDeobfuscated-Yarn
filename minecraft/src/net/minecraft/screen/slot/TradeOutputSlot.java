package net.minecraft.screen.slot;

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

	public TradeOutputSlot(PlayerEntity player, Trader trader, TraderInventory traderInventory, int index, int x, int y) {
		super(traderInventory, index, x, y);
		this.player = player;
		this.trader = trader;
		this.traderInventory = traderInventory;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int amount) {
		if (this.hasStack()) {
			this.amount = this.amount + Math.min(amount, this.getStack().getCount());
		}

		return super.takeStack(amount);
	}

	@Override
	protected void onCrafted(ItemStack stack, int amount) {
		this.amount += amount;
		this.onCrafted(stack);
	}

	@Override
	protected void onCrafted(ItemStack stack) {
		stack.onCraft(this.player.world, this.player, this.amount);
		this.amount = 0;
	}

	@Override
	public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
		this.onCrafted(stack);
		TradeOffer tradeOffer = this.traderInventory.getTradeOffer();
		if (tradeOffer != null) {
			ItemStack itemStack = this.traderInventory.getStack(0);
			ItemStack itemStack2 = this.traderInventory.getStack(1);
			if (tradeOffer.depleteBuyItems(itemStack, itemStack2) || tradeOffer.depleteBuyItems(itemStack2, itemStack)) {
				this.trader.trade(tradeOffer);
				player.incrementStat(Stats.TRADED_WITH_VILLAGER);
				this.traderInventory.setStack(0, itemStack);
				this.traderInventory.setStack(1, itemStack2);
			}

			this.trader.setExperienceFromServer(this.trader.getExperience() + tradeOffer.getTraderExperience());
		}

		return stack;
	}
}
