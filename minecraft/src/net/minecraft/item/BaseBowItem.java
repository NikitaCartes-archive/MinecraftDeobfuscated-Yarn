package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class BaseBowItem extends Item {
	public BaseBowItem(Item.Settings settings) {
		super(settings);
	}

	protected ItemStack findArrowStack(PlayerEntity playerEntity) {
		if (this.isArrow(playerEntity.getStackInHand(Hand.OFF))) {
			return playerEntity.getStackInHand(Hand.OFF);
		} else if (this.isArrow(playerEntity.getStackInHand(Hand.MAIN))) {
			return playerEntity.getStackInHand(Hand.MAIN);
		} else {
			for (int i = 0; i < playerEntity.inventory.getInvSize(); i++) {
				ItemStack itemStack = playerEntity.inventory.getInvStack(i);
				if (this.isArrow(itemStack)) {
					return itemStack;
				}
			}

			return ItemStack.EMPTY;
		}
	}

	protected boolean isArrow(ItemStack itemStack) {
		return itemStack.getItem() instanceof ArrowItem;
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
