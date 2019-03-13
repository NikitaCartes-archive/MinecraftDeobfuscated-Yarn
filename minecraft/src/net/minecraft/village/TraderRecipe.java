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
	private int field_18676;
	private int field_18677;
	private float field_18678;
	private int field_18679 = 1;

	public TraderRecipe(CompoundTag compoundTag) {
		this.firstBuyItem = ItemStack.method_7915(compoundTag.getCompound("buy"));
		this.secondBuyItem = ItemStack.method_7915(compoundTag.getCompound("buyB"));
		this.sellItem = ItemStack.method_7915(compoundTag.getCompound("sell"));
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
			this.field_18679 = compoundTag.getInt("xp");
		}

		if (compoundTag.containsKey("priceMultiplier", 5)) {
			this.field_18678 = compoundTag.getFloat("priceMultiplier");
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
		this.field_18679 = k;
		this.field_18678 = f;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getFirstBuyItem() {
		return this.firstBuyItem;
	}

	public ItemStack method_19272() {
		int i = this.firstBuyItem.getAmount();
		ItemStack itemStack = this.firstBuyItem.copy();
		int j = MathHelper.floor((float)(i * this.field_18677) * this.field_18678);
		itemStack.setAmount(MathHelper.clamp(i + j + this.field_18676, 1, 64));
		return itemStack;
	}

	public ItemStack getSecondBuyItem() {
		return this.secondBuyItem;
	}

	public ItemStack getModifiableSellItem() {
		return this.sellItem;
	}

	public void method_19274() {
		this.field_18677 = this.field_18677 + this.uses - (this.maxUses - this.uses);
	}

	public ItemStack getSellItem() {
		return this.sellItem.copy();
	}

	public int getUses() {
		return this.uses;
	}

	public void method_19275() {
		this.uses = 0;
	}

	public int getMaxUses() {
		return this.maxUses;
	}

	public void use() {
		this.uses++;
	}

	public void increasedMaxUses(int i) {
		this.field_18676 += i;
	}

	public void method_19276() {
		this.field_18676 = 0;
	}

	public int method_19277() {
		return this.field_18676;
	}

	public void method_19273(int i) {
		this.field_18676 = i;
	}

	public float method_19278() {
		return this.field_18678;
	}

	public int method_19279() {
		return this.field_18679;
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

	public CompoundTag method_8251() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.method_10566("buy", this.firstBuyItem.method_7953(new CompoundTag()));
		compoundTag.method_10566("sell", this.sellItem.method_7953(new CompoundTag()));
		compoundTag.method_10566("buyB", this.secondBuyItem.method_7953(new CompoundTag()));
		compoundTag.putInt("uses", this.uses);
		compoundTag.putInt("maxUses", this.maxUses);
		compoundTag.putBoolean("rewardExp", this.rewardExp);
		compoundTag.putInt("xp", this.field_18679);
		compoundTag.putFloat("priceMultiplier", this.field_18678);
		return compoundTag;
	}

	public boolean matchesBuyItems(ItemStack itemStack, ItemStack itemStack2) {
		return this.acceptsBuy(itemStack, this.method_19272())
			&& itemStack.getAmount() >= this.method_19272().getAmount()
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
				&& (!itemStack2.hasTag() || itemStack3.hasTag() && TagHelper.method_10687(itemStack2.method_7969(), itemStack3.method_7969(), false));
		}
	}

	public boolean depleteBuyItems(ItemStack itemStack, ItemStack itemStack2) {
		if (!this.matchesBuyItems(itemStack, itemStack2)) {
			return false;
		} else {
			itemStack.subtractAmount(this.method_19272().getAmount());
			if (!this.getSecondBuyItem().isEmpty()) {
				itemStack2.subtractAmount(this.getSecondBuyItem().getAmount());
			}

			return true;
		}
	}
}
