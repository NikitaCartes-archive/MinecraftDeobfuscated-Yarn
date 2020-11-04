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

	public TradeOffer(CompoundTag compoundTag) {
		this.firstBuyItem = ItemStack.fromTag(compoundTag.getCompound("buy"));
		this.secondBuyItem = ItemStack.fromTag(compoundTag.getCompound("buyB"));
		this.sellItem = ItemStack.fromTag(compoundTag.getCompound("sell"));
		this.uses = compoundTag.getInt("uses");
		if (compoundTag.contains("maxUses", 99)) {
			this.maxUses = compoundTag.getInt("maxUses");
		} else {
			this.maxUses = 4;
		}

		if (compoundTag.contains("rewardExp", 1)) {
			this.rewardingPlayerExperience = compoundTag.getBoolean("rewardExp");
		}

		if (compoundTag.contains("xp", 3)) {
			this.merchantExperience = compoundTag.getInt("xp");
		}

		if (compoundTag.contains("priceMultiplier", 5)) {
			this.priceMultiplier = compoundTag.getFloat("priceMultiplier");
		}

		this.specialPrice = compoundTag.getInt("specialPrice");
		this.demandBonus = compoundTag.getInt("demand");
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

	public TradeOffer(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3, int i, int j, int k, float f, int l) {
		this.firstBuyItem = itemStack;
		this.secondBuyItem = itemStack2;
		this.sellItem = itemStack3;
		this.uses = i;
		this.maxUses = j;
		this.merchantExperience = k;
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

	public int getDemandBonus() {
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

	public int getMerchantExperience() {
		return this.merchantExperience;
	}

	public boolean isDisabled() {
		return this.uses >= this.maxUses;
	}

	public void clearUses() {
		this.uses = this.maxUses;
	}

	public boolean method_21834() {
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
