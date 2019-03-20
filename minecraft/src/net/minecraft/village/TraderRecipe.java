package net.minecraft.village;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.MathHelper;

public class TraderRecipe {
	private final ItemStack firstBuyItem;
	private final ItemStack secondBuyItem;
	private final ItemStack sellItem;
	private int uses;
	private final int maxUses;
	private boolean rewardExp = true;
	private int tax;
	private int demandBonus;
	private float priceMultiplier;
	private int rewardedExp = 1;

	public TraderRecipe(CompoundTag compoundTag) {
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
			this.rewardExp = compoundTag.getBoolean("rewardExp");
		}

		if (compoundTag.containsKey("xp", 3)) {
			this.rewardedExp = compoundTag.getInt("xp");
		}

		if (compoundTag.containsKey("priceMultiplier", 5)) {
			this.priceMultiplier = compoundTag.getFloat("priceMultiplier");
		}
	}

	public TraderRecipe(ItemStack itemStack, ItemStack itemStack2, int i, int j, float f) {
		this(itemStack, ItemStack.EMPTY, itemStack2, i, j, f);
	}

	public TraderRecipe(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3, int i, int j, float f) {
		this(itemStack, itemStack2, itemStack3, 0, i, j, f);
	}

	public TraderRecipe(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3, int i, int j, int k, float f) {
		this.firstBuyItem = itemStack;
		this.secondBuyItem = itemStack2;
		this.sellItem = itemStack3;
		this.uses = i;
		this.maxUses = j;
		this.rewardedExp = k;
		this.priceMultiplier = f;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getFirstBuyItem() {
		return this.firstBuyItem;
	}

	public ItemStack getDiscountedFirstBuyItem() {
		int i = this.firstBuyItem.getAmount();
		ItemStack itemStack = this.firstBuyItem.copy();
		int j = Math.max(0, MathHelper.floor((float)(i * this.demandBonus) * this.priceMultiplier));
		itemStack.setAmount(MathHelper.clamp(i + j + this.tax, 1, 64));
		return itemStack;
	}

	public ItemStack getSecondBuyItem() {
		return this.secondBuyItem;
	}

	public ItemStack getModifiableSellItem() {
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

	public void increaseTax(int i) {
		this.tax += i;
	}

	public void clearTax() {
		this.tax = 0;
	}

	public int getTax() {
		return this.tax;
	}

	public void setTax(int i) {
		this.tax = i;
	}

	public float getPriceMultiplier() {
		return this.priceMultiplier;
	}

	public int getRewardedExp() {
		return this.rewardedExp;
	}

	public boolean isDisabled() {
		return this.uses >= this.maxUses;
	}

	public void clearUses() {
		this.uses = this.maxUses;
	}

	public boolean shouldRewardExp() {
		return this.rewardExp;
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.put("buy", this.firstBuyItem.toTag(new CompoundTag()));
		compoundTag.put("sell", this.sellItem.toTag(new CompoundTag()));
		compoundTag.put("buyB", this.secondBuyItem.toTag(new CompoundTag()));
		compoundTag.putInt("uses", this.uses);
		compoundTag.putInt("maxUses", this.maxUses);
		compoundTag.putBoolean("rewardExp", this.rewardExp);
		compoundTag.putInt("xp", this.rewardedExp);
		compoundTag.putFloat("priceMultiplier", this.priceMultiplier);
		return compoundTag;
	}

	public boolean matchesBuyItems(ItemStack itemStack, ItemStack itemStack2) {
		return this.acceptsBuy(itemStack, this.getDiscountedFirstBuyItem())
			&& itemStack.getAmount() >= this.getDiscountedFirstBuyItem().getAmount()
			&& this.acceptsBuy(itemStack2, this.secondBuyItem)
			&& itemStack2.getAmount() >= this.secondBuyItem.getAmount();
	}

	private boolean acceptsBuy(ItemStack itemStack, ItemStack itemStack2) {
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

	public boolean depleteBuyItems(ItemStack itemStack, ItemStack itemStack2) {
		if (!this.matchesBuyItems(itemStack, itemStack2)) {
			return false;
		} else {
			itemStack.subtractAmount(this.getDiscountedFirstBuyItem().getAmount());
			if (!this.getSecondBuyItem().isEmpty()) {
				itemStack2.subtractAmount(this.getSecondBuyItem().getAmount());
			}

			return true;
		}
	}
}
