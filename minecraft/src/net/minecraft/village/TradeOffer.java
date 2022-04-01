package net.minecraft.village;

import net.minecraft.class_7317;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

public class TradeOffer {
	private final class_7317 field_38546;
	private final class_7317 field_38547;
	private int uses;
	private final int maxUses;
	private boolean rewardingPlayerExperience = true;
	private int demandBonus;
	private float priceMultiplier;
	private int merchantExperience = 1;

	public TradeOffer(NbtCompound nbt) {
		this.field_38546 = (class_7317)class_7317.field_38543.parse(NbtOps.INSTANCE, nbt.getCompound("buy")).result().orElse(class_7317.method_42845(Blocks.AIR));
		this.field_38547 = (class_7317)class_7317.field_38543.parse(NbtOps.INSTANCE, nbt.getCompound("sell")).result().orElse(class_7317.method_42845(Blocks.AIR));
		this.uses = nbt.getInt("uses");
		if (nbt.contains("maxUses", NbtElement.NUMBER_TYPE)) {
			this.maxUses = nbt.getInt("maxUses");
		} else {
			this.maxUses = 4;
		}

		if (nbt.contains("rewardExp", NbtElement.BYTE_TYPE)) {
			this.rewardingPlayerExperience = nbt.getBoolean("rewardExp");
		}

		if (nbt.contains("xp", NbtElement.INT_TYPE)) {
			this.merchantExperience = nbt.getInt("xp");
		}

		if (nbt.contains("priceMultiplier", NbtElement.FLOAT_TYPE)) {
			this.priceMultiplier = nbt.getFloat("priceMultiplier");
		}

		this.demandBonus = nbt.getInt("demand");
	}

	public TradeOffer(class_7317 arg, class_7317 arg2, int i, int j, float f) {
		this(arg, arg2, 0, i, j, f);
	}

	public TradeOffer(class_7317 arg, class_7317 arg2, int i, int j, int k, float f) {
		this(arg, arg2, i, j, k, f, 0);
	}

	public TradeOffer(class_7317 arg, class_7317 arg2, int i, int j, int k, float f, int l) {
		this.field_38546 = arg;
		this.field_38547 = arg2;
		this.uses = i;
		this.maxUses = j;
		this.merchantExperience = k;
		this.priceMultiplier = f;
		this.demandBonus = l;
	}

	public class_7317 method_42852() {
		return this.field_38546;
	}

	/**
	 * Returns the sell item of this trade offer.
	 */
	public class_7317 getSellItem() {
		return this.field_38547;
	}

	/**
	 * Updates the demand bonus of this trade offer depending on its
	 * previous demand bonus, the number of times it has been used and
	 * its remaining uses.
	 */
	public void updateDemandBonus() {
		this.demandBonus = this.demandBonus + this.uses - (this.maxUses - this.uses);
	}

	public ItemStack method_42856() {
		return this.field_38547.method_42840();
	}

	/**
	 * Returns the number of times this trade offer has been used.
	 */
	public int getUses() {
		return this.uses;
	}

	/**
	 * Resets the number of times this trade offer has been used.
	 */
	public void resetUses() {
		this.uses = 0;
	}

	/**
	 * Returns the maximum number of times this trade offer can be used.
	 */
	public int getMaxUses() {
		return this.maxUses;
	}

	/**
	 * Increments the number of times this trade offer has been used.
	 */
	public void use() {
		this.uses++;
	}

	/**
	 * Returns the demand bonus of this trade offer. It is used to
	 * adjust the price of its first buy item.
	 * 
	 * <p>The more the demand bonus is, the more the price will be high.
	 * 
	 * <p>It is updated when a villager restocks.
	 * 
	 * @see #updateDemandBonus()
	 */
	public int getDemandBonus() {
		return this.demandBonus;
	}

	/**
	 * Returns the price multiplier of this trade offer. It is used to
	 * adjust the price of its first buy item.
	 */
	public float getPriceMultiplier() {
		return this.priceMultiplier;
	}

	/**
	 * Returns the amount of experience that will be given to a merchant
	 * after this trade offer has been used.
	 */
	public int getMerchantExperience() {
		return this.merchantExperience;
	}

	/**
	 * Returns whether this trade offer is disabled.
	 * 
	 * <p>Checks if the number of times this trade offer has been used
	 * is greater or equal to its maximum uses.
	 */
	public boolean isDisabled() {
		return this.uses >= this.maxUses;
	}

	/**
	 * Sets the number of times this trade offer has been used to
	 * its maximum uses, making it disabled.
	 */
	public void disable() {
		this.uses = this.maxUses;
	}

	/**
	 * Returns whether this trade offer has already been used.
	 * 
	 * <p>Checks if the number of times this trade offer has been used
	 * is greater than 0.
	 */
	public boolean hasBeenUsed() {
		return this.uses > 0;
	}

	/**
	 * Returns whether experience should be given to a player when
	 * they use this trade offer.
	 */
	public boolean shouldRewardPlayerExperience() {
		return this.rewardingPlayerExperience;
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		class_7317.field_38543.encodeStart(NbtOps.INSTANCE, this.field_38546).result().ifPresent(nbtElement -> nbtCompound.put("buy", nbtElement));
		class_7317.field_38543.encodeStart(NbtOps.INSTANCE, this.field_38547).result().ifPresent(nbtElement -> nbtCompound.put("sell", nbtElement));
		nbtCompound.putInt("uses", this.uses);
		nbtCompound.putInt("maxUses", this.maxUses);
		nbtCompound.putBoolean("rewardExp", this.rewardingPlayerExperience);
		nbtCompound.putInt("xp", this.merchantExperience);
		nbtCompound.putFloat("priceMultiplier", this.priceMultiplier);
		nbtCompound.putInt("demand", this.demandBonus);
		return nbtCompound;
	}

	public boolean method_42853(class_7317 arg) {
		return this.field_38546.method_42843(arg);
	}
}
