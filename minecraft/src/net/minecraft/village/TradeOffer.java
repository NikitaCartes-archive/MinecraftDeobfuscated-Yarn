package net.minecraft.village;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.MathHelper;

public class TradeOffer {
	public static final Codec<TradeOffer> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ItemStack.CODEC.optionalFieldOf("buy", ItemStack.EMPTY).forGetter(tradeOffer -> tradeOffer.firstBuyItem),
					ItemStack.CODEC.optionalFieldOf("buyB", ItemStack.EMPTY).forGetter(tradeOffer -> tradeOffer.secondBuyItem),
					ItemStack.CODEC.optionalFieldOf("sell", ItemStack.EMPTY).forGetter(tradeOffer -> tradeOffer.sellItem),
					Codec.INT.optionalFieldOf("uses", Integer.valueOf(0)).forGetter(tradeOffer -> tradeOffer.uses),
					Codec.INT.optionalFieldOf("maxUses", Integer.valueOf(4)).forGetter(tradeOffer -> tradeOffer.maxUses),
					Codec.BOOL.optionalFieldOf("rewardExp", Boolean.valueOf(true)).forGetter(tradeOffer -> tradeOffer.rewardingPlayerExperience),
					Codec.INT.optionalFieldOf("specialPrice", Integer.valueOf(0)).forGetter(tradeOffer -> tradeOffer.specialPrice),
					Codec.INT.optionalFieldOf("demand", Integer.valueOf(0)).forGetter(tradeOffer -> tradeOffer.demandBonus),
					Codec.FLOAT.optionalFieldOf("priceMultiplier", Float.valueOf(0.0F)).forGetter(tradeOffer -> tradeOffer.priceMultiplier),
					Codec.INT.optionalFieldOf("xp", Integer.valueOf(1)).forGetter(tradeOffer -> tradeOffer.merchantExperience),
					Codec.BOOL.optionalFieldOf("ignore_tags", Boolean.valueOf(false)).forGetter(tradeOffer -> tradeOffer.ignoreNbt)
				)
				.apply(instance, TradeOffer::new)
	);
	public static final PacketCodec<RegistryByteBuf, TradeOffer> PACKET_CODEC = PacketCodec.ofStatic(TradeOffer::write, TradeOffer::read);
	private final ItemStack firstBuyItem;
	private final ItemStack secondBuyItem;
	private final ItemStack sellItem;
	private int uses;
	private final int maxUses;
	private final boolean rewardingPlayerExperience;
	private int specialPrice;
	private int demandBonus;
	private final float priceMultiplier;
	private final int merchantExperience;
	private final boolean ignoreNbt;

	private TradeOffer(
		ItemStack firstBuyItem,
		ItemStack secondBuyItem,
		ItemStack sellItem,
		int uses,
		int maxUses,
		boolean rewardingPlayerExperience,
		int specialPrice,
		int demandBonus,
		float priceMultiplier,
		int merchantExperience,
		boolean ignoreNbt
	) {
		this.firstBuyItem = firstBuyItem;
		this.secondBuyItem = secondBuyItem;
		this.sellItem = sellItem;
		this.uses = uses;
		this.maxUses = maxUses;
		this.rewardingPlayerExperience = rewardingPlayerExperience;
		this.specialPrice = specialPrice;
		this.demandBonus = demandBonus;
		this.priceMultiplier = priceMultiplier;
		this.merchantExperience = merchantExperience;
		this.ignoreNbt = ignoreNbt;
	}

	public TradeOffer(ItemStack buyItem, ItemStack sellItem, int maxUses, int merchantExperience, float priceMultiplier) {
		this(buyItem, ItemStack.EMPTY, sellItem, maxUses, merchantExperience, priceMultiplier);
	}

	public TradeOffer(ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int maxUses, int merchantExperience, float priceMultiplier) {
		this(firstBuyItem, secondBuyItem, sellItem, 0, maxUses, merchantExperience, priceMultiplier);
	}

	public TradeOffer(ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int uses, int maxUses, int merchantExperience, float priceMultiplier) {
		this(firstBuyItem, secondBuyItem, sellItem, uses, maxUses, merchantExperience, priceMultiplier, 0);
	}

	public TradeOffer(
		ItemStack firstBuyItem, ItemStack secondBuyItem, ItemStack sellItem, int uses, int maxUses, int merchantExperience, float priceMultiplier, int demandBonus
	) {
		this(firstBuyItem, secondBuyItem, sellItem, uses, maxUses, true, 0, demandBonus, priceMultiplier, merchantExperience, false);
	}

	public TradeOffer(
		ItemStack firstBuyItem,
		ItemStack secondBuyItem,
		ItemStack sellItem,
		int uses,
		int maxUses,
		int merchantExperience,
		float priceMultiplier,
		int demandBonus,
		boolean ignoreNbt
	) {
		this(firstBuyItem, secondBuyItem, sellItem, uses, maxUses, true, 0, demandBonus, priceMultiplier, merchantExperience, ignoreNbt);
	}

	private TradeOffer(TradeOffer offer) {
		this(
			offer.firstBuyItem.copy(),
			offer.secondBuyItem.copy(),
			offer.sellItem.copy(),
			offer.uses,
			offer.maxUses,
			offer.rewardingPlayerExperience,
			offer.specialPrice,
			offer.demandBonus,
			offer.priceMultiplier,
			offer.merchantExperience,
			offer.ignoreNbt
		);
	}

	/**
	 * Returns the first buy item of this trade offer.
	 */
	public ItemStack getOriginalFirstBuyItem() {
		return this.firstBuyItem;
	}

	/**
	 * Returns a copy of the first buy item of this trade offer,
	 * with its price adjusted depending on the demand bonus, the
	 * special price and the price multiplier.
	 */
	public ItemStack getAdjustedFirstBuyItem() {
		if (this.firstBuyItem.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			int i = this.firstBuyItem.getCount();
			int j = Math.max(0, MathHelper.floor((float)(i * this.demandBonus) * this.priceMultiplier));
			return this.firstBuyItem.copyWithCount(MathHelper.clamp(i + j + this.specialPrice, 1, this.firstBuyItem.getItem().getMaxCount()));
		}
	}

	/**
	 * Returns the second buy item of this trade offer.
	 * 
	 * <p>If there is no second buy item, this returns the {@linkplain ItemStack#EMPTY empty
	 * item stack}.
	 */
	public ItemStack getSecondBuyItem() {
		return this.secondBuyItem;
	}

	/**
	 * Returns the sell item of this trade offer.
	 */
	public ItemStack getSellItem() {
		return this.sellItem;
	}

	/**
	 * Updates the demand bonus of this trade offer depending on its
	 * previous demand bonus, the number of times it has been used and
	 * its remaining uses.
	 */
	public void updateDemandBonus() {
		this.demandBonus = this.demandBonus + this.uses - (this.maxUses - this.uses);
	}

	/**
	 * Returns a copy of the sell item of this trade offer.
	 */
	public ItemStack copySellItem() {
		return this.sellItem.copy();
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

	public boolean shouldIgnoreNbt() {
		return this.ignoreNbt;
	}

	/**
	 * Increases the special price of this trade offer by {@code increment}.
	 * 
	 * <p>A negative {@code increment} value will decrease the special price.
	 */
	public void increaseSpecialPrice(int increment) {
		this.specialPrice += increment;
	}

	/**
	 * Resets the special price of this trade offer.
	 */
	public void clearSpecialPrice() {
		this.specialPrice = 0;
	}

	/**
	 * Returns the special price of this trade offer. It is used to
	 * adjust the price of its first buy item.
	 * 
	 * <p>The less the special price is, the more the price will be low.
	 */
	public int getSpecialPrice() {
		return this.specialPrice;
	}

	/**
	 * Sets the special price of this trade offer to {@code specialPrice}.
	 */
	public void setSpecialPrice(int specialPrice) {
		this.specialPrice = specialPrice;
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

	public boolean matchesBuyItems(ItemStack offered, ItemStack buyItem) {
		return acceptsBuy(offered, this.getAdjustedFirstBuyItem(), this.ignoreNbt)
			&& offered.getCount() >= this.getAdjustedFirstBuyItem().getCount()
			&& acceptsBuy(buyItem, this.secondBuyItem, this.ignoreNbt)
			&& buyItem.getCount() >= this.secondBuyItem.getCount();
	}

	public static boolean acceptsBuy(ItemStack offered, ItemStack buyItem, boolean ignoreNbt) {
		if (buyItem.isEmpty() && offered.isEmpty()) {
			return true;
		} else {
			ItemStack itemStack = offered.copy();
			ItemStack itemStack2 = buyItem.copy();
			if (itemStack.getItem().isDamageable()) {
				itemStack.setDamage(itemStack.getDamage());
			}

			return ignoreNbt
				? ItemStack.areItemsEqual(itemStack, itemStack2)
				: ItemStack.areItemsEqual(itemStack, itemStack2)
					&& (!itemStack2.hasNbt() || itemStack.hasNbt() && NbtHelper.matches(itemStack2.getNbt(), itemStack.getNbt(), false));
		}
	}

	public boolean depleteBuyItems(ItemStack firstBuyStack, ItemStack secondBuyStack) {
		if (!this.matchesBuyItems(firstBuyStack, secondBuyStack)) {
			return false;
		} else {
			firstBuyStack.decrement(this.getAdjustedFirstBuyItem().getCount());
			if (!this.getSecondBuyItem().isEmpty()) {
				secondBuyStack.decrement(this.getSecondBuyItem().getCount());
			}

			return true;
		}
	}

	public TradeOffer copy() {
		return new TradeOffer(this);
	}

	private static void write(RegistryByteBuf buf, TradeOffer offer) {
		ItemStack.PACKET_CODEC.encode(buf, offer.getOriginalFirstBuyItem());
		ItemStack.PACKET_CODEC.encode(buf, offer.getSellItem());
		ItemStack.PACKET_CODEC.encode(buf, offer.getSecondBuyItem());
		buf.writeBoolean(offer.isDisabled());
		buf.writeInt(offer.getUses());
		buf.writeInt(offer.getMaxUses());
		buf.writeInt(offer.getMerchantExperience());
		buf.writeInt(offer.getSpecialPrice());
		buf.writeFloat(offer.getPriceMultiplier());
		buf.writeInt(offer.getDemandBonus());
		buf.writeBoolean(offer.shouldIgnoreNbt());
	}

	public static TradeOffer read(RegistryByteBuf buf) {
		ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
		ItemStack itemStack2 = ItemStack.PACKET_CODEC.decode(buf);
		ItemStack itemStack3 = ItemStack.PACKET_CODEC.decode(buf);
		boolean bl = buf.readBoolean();
		int i = buf.readInt();
		int j = buf.readInt();
		int k = buf.readInt();
		int l = buf.readInt();
		float f = buf.readFloat();
		int m = buf.readInt();
		boolean bl2 = buf.readBoolean();
		TradeOffer tradeOffer = new TradeOffer(itemStack, itemStack3, itemStack2, i, j, k, f, m, bl2);
		if (bl) {
			tradeOffer.disable();
		}

		tradeOffer.setSpecialPrice(l);
		return tradeOffer;
	}
}
