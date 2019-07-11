package net.minecraft.village;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.MathHelper;

public class TradeOffer {
	private final ItemStack firstBuyItem;
	private final ItemStack secondBuyItem;
	private final ItemStack sellItem;
	private int uses;
	private final int maxUses;
	private boolean rewardingPlayerExperience = true;
	private int specialPrice;
	private int demandBonus;
	private float priceMultiplier;
	private int traderExperience = 1;

	public TradeOffer(CompoundTag compoundTag) {
		this.firstBuyItem = ItemStack.fromTag(compoundTag.getCompound("buy"));
		this.secondBuyItem = ItemStack.fromTag(compoundTag.getCompound("buyB"));
		this.sellItem = ItemStack.fromTag(compoundTag.getCompound("sell"));
		this.uses = compoundTag.getInt("uses");
		if (compoundTag.containsKey("maxUses", 99)) {
			this.maxUses = compoundTag.getInt("maxUses");
		} else {
			this.maxUses = 4;
		}

		if (compoundTag.containsKey("rewardExp", 1)) {
			this.rewardingPlayerExperience = compoundTag.getBoolean("rewardExp");
		}

		if (compoundTag.containsKey("xp", 3)) {
			this.traderExperience = compoundTag.getInt("xp");
		}

		if (compoundTag.containsKey("priceMultiplier", 5)) {
			this.priceMultiplier = compoundTag.getFloat("priceMultiplier");
		}

		this.specialPrice = compoundTag.getInt("specialPrice");
		this.demandBonus = compoundTag.getInt("demand");
	}

	public TradeOffer(ItemStack itemStack, ItemStack itemStack2, int i, int j, float f) {
		this(itemStack, ItemStack.EMPTY, itemStack2, i, j, f);
	}

	public TradeOffer(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3, int i, int j, float f) {
		this(itemStack, itemStack2, itemStack3, 0, i, j, f);
	}

	public TradeOffer(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3, int i, int j, int k, float f) {
		this(itemStack, itemStack2, itemStack3, i, j, k, f, 0);
	}

	public TradeOffer(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3, int i, int j, int k, float f, int l) {
		this.firstBuyItem = itemStack;
		this.secondBuyItem = itemStack2;
		this.sellItem = itemStack3;
		this.uses = i;
		this.maxUses = j;
		this.traderExperience = k;
		this.priceMultiplier = f;
		this.demandBonus = l;
	}

	public ItemStack getOriginalFirstBuyItem() {
		return this.firstBuyItem;
	}

	public ItemStack getAdjustedFirstBuyItem() {
		int i = this.firstBuyItem.getCount();
		ItemStack itemStack = this.firstBuyItem.copy();
		int j = Math.max(0, MathHelper.floor((float)(i * this.demandBonus) * this.priceMultiplier));
		itemStack.setCount(MathHelper.clamp(i + j + this.specialPrice, 1, this.firstBuyItem.getItem().getMaxCount()));
		return itemStack;
	}

	public ItemStack getSecondBuyItem() {
		return this.secondBuyItem;
	}

	public ItemStack getMutableSellItem() {
		return this.sellItem;
	}

	public void updatePriceOnDemand() {
		this.demandBonus = this.demandBonus + this.uses - (this.maxUses - this.uses);
	}

	public ItemStack getSellItem() {
		return this.sellItem.copy();
	}

	public int getUses() {
		return this.uses;
	}

	public void resetUses() {
		this.uses = 0;
	}

	public int getMaxUses() {
		return this.maxUses;
	}

	public void use() {
		this.uses++;
	}

	public int method_21725() {
		return this.demandBonus;
	}

	public void increaseSpecialPrice(int i) {
		this.specialPrice += i;
	}

	public void clearSpecialPrice() {
		this.specialPrice = 0;
	}

	public int getSpecialPrice() {
		return this.specialPrice;
	}

	public void setSpecialPrice(int i) {
		this.specialPrice = i;
	}

	public float getPriceMultiplier() {
		return this.priceMultiplier;
	}

	public int getTraderExperience() {
		return this.traderExperience;
	}

	public boolean isDisabled() {
		return this.uses >= this.maxUses;
	}

	public void clearUses() {
		this.uses = this.maxUses;
	}

	public boolean shouldRewardPlayerExperience() {
		return this.rewardingPlayerExperience;
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.put("buy", this.firstBuyItem.toTag(new CompoundTag()));
		compoundTag.put("sell", this.sellItem.toTag(new CompoundTag()));
		compoundTag.put("buyB", this.secondBuyItem.toTag(new CompoundTag()));
		compoundTag.putInt("uses", this.uses);
		compoundTag.putInt("maxUses", this.maxUses);
		compoundTag.putBoolean("rewardExp", this.rewardingPlayerExperience);
		compoundTag.putInt("xp", this.traderExperience);
		compoundTag.putFloat("priceMultiplier", this.priceMultiplier);
		compoundTag.putInt("specialPrice", this.specialPrice);
		compoundTag.putInt("demand", this.demandBonus);
		return compoundTag;
	}

	public boolean matchesBuyItems(ItemStack itemStack, ItemStack itemStack2) {
		return this.acceptsBuy(itemStack, this.getAdjustedFirstBuyItem())
			&& itemStack.getCount() >= this.getAdjustedFirstBuyItem().getCount()
			&& this.acceptsBuy(itemStack2, this.secondBuyItem)
			&& itemStack2.getCount() >= this.secondBuyItem.getCount();
	}

	private boolean acceptsBuy(ItemStack itemStack, ItemStack itemStack2) {
		if (itemStack2.isEmpty() && itemStack.isEmpty()) {
			return true;
		} else {
			ItemStack itemStack3 = itemStack.copy();
			if (itemStack3.getItem().isDamageable()) {
				itemStack3.setDamage(itemStack3.getDamage());
			}

			return ItemStack.areItemsEqualIgnoreDamage(itemStack3, itemStack2)
				&& (!itemStack2.hasTag() || itemStack3.hasTag() && TagHelper.areTagsEqual(itemStack2.getTag(), itemStack3.getTag(), false));
		}
	}

	public boolean depleteBuyItems(ItemStack itemStack, ItemStack itemStack2) {
		if (!this.matchesBuyItems(itemStack, itemStack2)) {
			return false;
		} else {
			itemStack.decrement(this.getAdjustedFirstBuyItem().getCount());
			if (!this.getSecondBuyItem().isEmpty()) {
				itemStack2.decrement(this.getSecondBuyItem().getCount());
			}

			return true;
		}
	}
}
