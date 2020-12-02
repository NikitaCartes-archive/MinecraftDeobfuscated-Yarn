package net.minecraft.village;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
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
	private int merchantExperience = 1;

	public TradeOffer(CompoundTag tag) {
		this.firstBuyItem = ItemStack.fromTag(tag.getCompound("buy"));
		this.secondBuyItem = ItemStack.fromTag(tag.getCompound("buyB"));
		this.sellItem = ItemStack.fromTag(tag.getCompound("sell"));
		this.uses = tag.getInt("uses");
		if (tag.contains("maxUses", 99)) {
			this.maxUses = tag.getInt("maxUses");
		} else {
			this.maxUses = 4;
		}

		if (tag.contains("rewardExp", 1)) {
			this.rewardingPlayerExperience = tag.getBoolean("rewardExp");
		}

		if (tag.contains("xp", 3)) {
			this.merchantExperience = tag.getInt("xp");
		}

		if (tag.contains("priceMultiplier", 5)) {
			this.priceMultiplier = tag.getFloat("priceMultiplier");
		}

		this.specialPrice = tag.getInt("specialPrice");
		this.demandBonus = tag.getInt("demand");
	}

	public TradeOffer(ItemStack buyItem, ItemStack sellItem, int maxUses, int rewardedExp, float priceMultiplier) {
		this(buyItem, ItemStack.EMPTY, sellItem, maxUses, rewardedExp, priceMultiplier);
	}

	public TradeOffer(ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int maxUses, int rewardedExp, float priceMultiplier) {
		this(firstBuyItem, secondBuyItem, sellItem, 0, maxUses, rewardedExp, priceMultiplier);
	}

	public TradeOffer(ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int uses, int maxUses, int rewardedExp, float priceMultiplier) {
		this(firstBuyItem, secondBuyItem, sellItem, uses, maxUses, rewardedExp, priceMultiplier, 0);
	}

	public TradeOffer(
		ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int uses, int maxUses, int rewardedExp, float priceMultiplier, int demandBonus
	) {
		this.firstBuyItem = firstBuyItem;
		this.secondBuyItem = secondBuyItem;
		this.sellItem = sellItem;
		this.uses = uses;
		this.maxUses = maxUses;
		this.merchantExperience = rewardedExp;
		this.priceMultiplier = priceMultiplier;
		this.demandBonus = demandBonus;
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

	public int getDemandBonus() {
		return this.demandBonus;
	}

	public void increaseSpecialPrice(int increment) {
		this.specialPrice += increment;
	}

	public void clearSpecialPrice() {
		this.specialPrice = 0;
	}

	public int getSpecialPrice() {
		return this.specialPrice;
	}

	public void setSpecialPrice(int specialPrice) {
		this.specialPrice = specialPrice;
	}

	public float getPriceMultiplier() {
		return this.priceMultiplier;
	}

	public int getMerchantExperience() {
		return this.merchantExperience;
	}

	public boolean isDisabled() {
		return this.uses >= this.maxUses;
	}

	public void clearUses() {
		this.uses = this.maxUses;
	}

	public boolean hasAvailableUses() {
		return this.uses > 0;
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
		compoundTag.putInt("xp", this.merchantExperience);
		compoundTag.putFloat("priceMultiplier", this.priceMultiplier);
		compoundTag.putInt("specialPrice", this.specialPrice);
		compoundTag.putInt("demand", this.demandBonus);
		return compoundTag;
	}

	public boolean matchesBuyItems(ItemStack first, ItemStack second) {
		return this.acceptsBuy(first, this.getAdjustedFirstBuyItem())
			&& first.getCount() >= this.getAdjustedFirstBuyItem().getCount()
			&& this.acceptsBuy(second, this.secondBuyItem)
			&& second.getCount() >= this.secondBuyItem.getCount();
	}

	private boolean acceptsBuy(ItemStack given, ItemStack sample) {
		if (sample.isEmpty() && given.isEmpty()) {
			return true;
		} else {
			ItemStack itemStack = given.copy();
			if (itemStack.getItem().isDamageable()) {
				itemStack.setDamage(itemStack.getDamage());
			}

			return ItemStack.areItemsEqualIgnoreDamage(itemStack, sample)
				&& (!sample.hasTag() || itemStack.hasTag() && NbtHelper.matches(sample.getTag(), itemStack.getTag(), false));
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
