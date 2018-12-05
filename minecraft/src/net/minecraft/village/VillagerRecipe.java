package net.minecraft.village;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class VillagerRecipe {
	private ItemStack buyItem = ItemStack.EMPTY;
	private ItemStack secondBuyItem = ItemStack.EMPTY;
	private ItemStack sellItem = ItemStack.EMPTY;
	private int uses;
	private int maxUses;
	private boolean rewardExp;

	public VillagerRecipe(CompoundTag compoundTag) {
		this.serialize(compoundTag);
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
		this.rewardExp = true;
	}

	public VillagerRecipe(ItemStack itemStack, ItemStack itemStack2) {
		this(itemStack, ItemStack.EMPTY, itemStack2);
	}

	public VillagerRecipe(ItemStack itemStack, Item item) {
		this(itemStack, new ItemStack(item));
	}

	public ItemStack getBuyItem() {
		return this.buyItem;
	}

	public ItemStack getSecondBuyItem() {
		return this.secondBuyItem;
	}

	public boolean hasSecondBuyItem() {
		return !this.secondBuyItem.isEmpty();
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

	@Environment(EnvType.CLIENT)
	public void clearUses() {
		this.uses = this.maxUses;
	}

	public boolean getRewardExp() {
		return this.rewardExp;
	}

	public void serialize(CompoundTag compoundTag) {
		CompoundTag compoundTag2 = compoundTag.getCompound("buy");
		this.buyItem = ItemStack.fromTag(compoundTag2);
		CompoundTag compoundTag3 = compoundTag.getCompound("sell");
		this.sellItem = ItemStack.fromTag(compoundTag3);
		if (compoundTag.containsKey("buyB", 10)) {
			this.secondBuyItem = ItemStack.fromTag(compoundTag.getCompound("buyB"));
		}

		if (compoundTag.containsKey("uses", 99)) {
			this.uses = compoundTag.getInt("uses");
		}

		if (compoundTag.containsKey("maxUses", 99)) {
			this.maxUses = compoundTag.getInt("maxUses");
		} else {
			this.maxUses = 7;
		}

		if (compoundTag.containsKey("rewardExp", 1)) {
			this.rewardExp = compoundTag.getBoolean("rewardExp");
		} else {
			this.rewardExp = true;
		}
	}

	public CompoundTag deserialize() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.put("buy", this.buyItem.toTag(new CompoundTag()));
		compoundTag.put("sell", this.sellItem.toTag(new CompoundTag()));
		if (!this.secondBuyItem.isEmpty()) {
			compoundTag.put("buyB", this.secondBuyItem.toTag(new CompoundTag()));
		}

		compoundTag.putInt("uses", this.uses);
		compoundTag.putInt("maxUses", this.maxUses);
		compoundTag.putBoolean("rewardExp", this.rewardExp);
		return compoundTag;
	}
}
