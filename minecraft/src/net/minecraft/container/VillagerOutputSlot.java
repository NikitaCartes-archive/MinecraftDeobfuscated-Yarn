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
			if (villagerRecipe.method_16953(itemStack2, itemStack3) || villagerRecipe.method_16953(itemStack3, itemStack2)) {
				this.villager.useRecipe(villagerRecipe);
				playerEntity.increaseStat(Stats.field_15378);
				this.villagerInventory.setInvStack(0, itemStack2);
				this.villagerInventory.setInvStack(1, itemStack3);
			}
		}

		return itemStack;
	}
}
