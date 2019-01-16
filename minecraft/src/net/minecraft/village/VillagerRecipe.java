package net.minecraft.village;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;

public class VillagerRecipe {
	private final ItemStack buyItem;
	private final ItemStack secondBuyItem;
	private final ItemStack sellItem;
	private int uses;
	private int maxUses = 7;
	private boolean rewardExp = true;

	public VillagerRecipe(CompoundTag compoundTag) {
		this.buyItem = ItemStack.fromTag(compoundTag.getCompound("buy"));
		this.secondBuyItem = ItemStack.fromTag(compoundTag.getCompound("buyB"));
		this.sellItem = ItemStack.fromTag(compoundTag.getCompound("sell"));
		this.uses = compoundTag.getInt("uses");
		if (compoundTag.containsKey("maxUses", 99)) {
			this.maxUses = compoundTag.getInt("maxUses");
		}

		if (compoundTag.containsKey("rewardExp", 1)) {
			this.rewardExp = compoundTag.getBoolean("rewardExp");
		}
	}

	public VillagerRecipe(ItemStack itemStack, ItemStack itemStack2) {
		this(itemStack, ItemStack.EMPTY, itemStack2);
	}

	public VillagerRecipe(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3) {
		this(itemStack, itemStack2, itemStack3, 0, 7);
	}

	public VillagerRecipe(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3, int i, int j) {
		this.buyItem = itemStack;
		this.secondBuyItem = itemStack2;
		this.sellItem = itemStack3;
		this.uses = i;
		this.maxUses = j;
	}

	public ItemStack getBuyItem() {
		return this.buyItem;
	}

	public ItemStack getSecondBuyItem() {
		return this.secondBuyItem;
	}

	public ItemStack getSellItem() {
		return this.sellItem;
	}

	public int getUses() {
		return this.uses;
	}

	public int getMaxUses() {
		return this.maxUses;
	}

	public void use() {
		this.uses++;
	}

	public void increasedMaxUses(int i) {
		this.maxUses += i;
	}

	public boolean isDisabled() {
		return this.uses >= this.maxUses;
	}

	public void clearUses() {
		this.uses = this.maxUses;
	}

	public boolean getRewardExp() {
		return this.rewardExp;
	}

	public CompoundTag deserialize() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.put("buy", this.buyItem.toTag(new CompoundTag()));
		compoundTag.put("sell", this.sellItem.toTag(new CompoundTag()));
		compoundTag.put("buyB", this.secondBuyItem.toTag(new CompoundTag()));
		compoundTag.putInt("uses", this.uses);
		compoundTag.putInt("maxUses", this.maxUses);
		compoundTag.putBoolean("rewardExp", this.rewardExp);
		return compoundTag;
	}

	public boolean method_16952(ItemStack itemStack, ItemStack itemStack2) {
		return this.method_16954(itemStack, this.buyItem)
			&& itemStack.getAmount() >= this.buyItem.getAmount()
			&& this.method_16954(itemStack2, this.secondBuyItem)
			&& itemStack2.getAmount() >= this.secondBuyItem.getAmount();
	}

	private boolean method_16954(ItemStack itemStack, ItemStack itemStack2) {
		if (itemStack2.isEmpty() && itemStack.isEmpty()) {
			return true;
		} else {
			ItemStack itemStack3 = itemStack.copy();
			if (itemStack3.getItem().canDamage()) {
				itemStack3.setDamage(itemStack3.getDamage());
			}

			return ItemStack.areEqualIgnoreTags(itemStack3, itemStack2)
				&& (!itemStack2.hasTag() || itemStack3.hasTag() && TagHelper.areTagsEqual(itemStack2.getTag(), itemStack3.getTag(), false));
		}
	}

	public boolean method_16953(ItemStack itemStack, ItemStack itemStack2) {
		if (!this.method_16952(itemStack, itemStack2)) {
			return false;
		} else {
			itemStack.subtractAmount(this.getBuyItem().getAmount());
			if (!this.getSecondBuyItem().isEmpty()) {
				itemStack2.subtractAmount(this.getSecondBuyItem().getAmount());
			}

			return true;
		}
	}
}
