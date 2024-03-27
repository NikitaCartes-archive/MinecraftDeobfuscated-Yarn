package net.minecraft.village;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.MathHelper;

public class TradeOffer {
	public static final Codec<TradeOffer> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					TradedItem.CODEC.fieldOf("buy").forGetter(tradeOffer -> tradeOffer.firstBuyItem),
					TradedItem.CODEC.lenientOptionalFieldOf("buyB").forGetter(tradeOffer -> tradeOffer.secondBuyItem),
					ItemStack.CODEC.fieldOf("sell").forGetter(tradeOffer -> tradeOffer.sellItem),
					Codec.INT.lenientOptionalFieldOf("uses", Integer.valueOf(0)).forGetter(tradeOffer -> tradeOffer.uses),
					Codec.INT.lenientOptionalFieldOf("maxUses", Integer.valueOf(4)).forGetter(tradeOffer -> tradeOffer.maxUses),
					Codec.BOOL.lenientOptionalFieldOf("rewardExp", Boolean.valueOf(true)).forGetter(tradeOffer -> tradeOffer.rewardingPlayerExperience),
					Codec.INT.lenientOptionalFieldOf("specialPrice", Integer.valueOf(0)).forGetter(tradeOffer -> tradeOffer.specialPrice),
					Codec.INT.lenientOptionalFieldOf("demand", Integer.valueOf(0)).forGetter(tradeOffer -> tradeOffer.demandBonus),
					Codec.FLOAT.lenientOptionalFieldOf("priceMultiplier", Float.valueOf(0.0F)).forGetter(tradeOffer -> tradeOffer.priceMultiplier),
					Codec.INT.lenientOptionalFieldOf("xp", Integer.valueOf(1)).forGetter(tradeOffer -> tradeOffer.merchantExperience)
				)
				.apply(instance, TradeOffer::new)
	);
	public static final PacketCodec<RegistryByteBuf, TradeOffer> PACKET_CODEC = PacketCodec.ofStatic(TradeOffer::write, TradeOffer::read);
	private final TradedItem firstBuyItem;
	private final Optional<TradedItem> secondBuyItem;
	private final ItemStack sellItem;
	private int uses;
	private final int maxUses;
	private final boolean rewardingPlayerExperience;
	private int specialPrice;
	private int demandBonus;
	private final float priceMultiplier;
	private final int merchantExperience;

	private TradeOffer(
		TradedItem firstBuyItem,
		Optional<TradedItem> secondBuyItem,
		ItemStack sellItem,
		int uses,
		int maxUses,
		boolean rewardingPlayerExperience,
		int specialPrice,
		int demandBonus,
		float priceMultiplier,
		int merchantExperience
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
	}

	public TradeOffer(TradedItem buyItem, ItemStack sellItem, int maxUses, int merchantExperience, float priceMultiplier) {
		this(buyItem, Optional.empty(), sellItem, maxUses, merchantExperience, priceMultiplier);
	}

	public TradeOffer(TradedItem firstBuyItem, Optional<TradedItem> secondBuyItem, ItemStack sellItem, int maxUses, int merchantExperience, float priceMultiplier) {
		this(firstBuyItem, secondBuyItem, sellItem, 0, maxUses, merchantExperience, priceMultiplier);
	}

	public TradeOffer(
		TradedItem firstBuyItem, Optional<TradedItem> secondBuyItem, ItemStack sellItem, int uses, int maxUses, int merchantExperience, float priceMultiplier
	) {
		this(firstBuyItem, secondBuyItem, sellItem, uses, maxUses, merchantExperience, priceMultiplier, 0);
	}

	public TradeOffer(
		TradedItem firstBuyItem,
		Optional<TradedItem> secondBuyItem,
		ItemStack sellItem,
		int uses,
		int maxUses,
		int merchantExperience,
		float priceMultiplier,
		int demandBonus
	) {
		this(firstBuyItem, secondBuyItem, sellItem, uses, maxUses, true, 0, demandBonus, priceMultiplier, merchantExperience);
	}

	private TradeOffer(TradeOffer offer) {
		this(
			offer.firstBuyItem,
			offer.secondBuyItem,
			offer.sellItem.copy(),
			offer.uses,
			offer.maxUses,
			offer.rewardingPlayerExperience,
			offer.specialPrice,
			offer.demandBonus,
			offer.priceMultiplier,
			offer.merchantExperience
		);
	}

	/**
	 * Returns the first buy item of this trade offer.
	 */
	public ItemStack getOriginalFirstBuyItem() {
		return this.firstBuyItem.itemStack();
	}

	/**
	 * Returns a copy of the first buy item of this trade offer,
	 * with its price adjusted depending on the demand bonus, the
	 * special price and the price multiplier.
	 */
	public ItemStack getDisplayedFirstBuyItem() {
		return this.firstBuyItem.itemStack().copyWithCount(this.getFirstBuyItemCount(this.firstBuyItem));
	}

	private int getFirstBuyItemCount(TradedItem firstBuyItem) {
		int i = firstBuyItem.count();
		int j = Math.max(0, MathHelper.floor((float)(i * this.demandBonus) * this.priceMultiplier));
		return MathHelper.clamp(i + j + this.specialPrice, 1, firstBuyItem.itemStack().getMaxCount());
	}

	/**
	 * Returns the second buy item of this trade offer.
	 * 
	 * <p>If there is no second buy item, this returns the {@linkplain ItemStack#EMPTY empty
	 * item stack}.
	 */
	public ItemStack getDisplayedSecondBuyItem() {
		return (ItemStack)this.secondBuyItem.map(TradedItem::itemStack).orElse(ItemStack.EMPTY);
	}

	public TradedItem getFirstBuyItem() {
		return this.firstBuyItem;
	}

	public Optional<TradedItem> getSecondBuyItem() {
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

	public boolean matchesBuyItems(ItemStack stack, ItemStack buyItem) {
		if (!this.firstBuyItem.matches(stack) || stack.getCount() < this.getFirstBuyItemCount(this.firstBuyItem)) {
			return false;
		} else {
			return !this.secondBuyItem.isPresent()
				? buyItem.isEmpty()
				: ((TradedItem)this.secondBuyItem.get()).matches(buyItem) && buyItem.getCount() >= ((TradedItem)this.secondBuyItem.get()).count();
		}
	}

	public boolean depleteBuyItems(ItemStack firstBuyStack, ItemStack secondBuyStack) {
		if (!this.matchesBuyItems(firstBuyStack, secondBuyStack)) {
			return false;
		} else {
			firstBuyStack.decrement(this.getDisplayedFirstBuyItem().getCount());
			if (!this.getDisplayedSecondBuyItem().isEmpty()) {
				secondBuyStack.decrement(this.getDisplayedSecondBuyItem().getCount());
			}

			return true;
		}
	}

	public TradeOffer copy() {
		return new TradeOffer(this);
	}

	private static void write(RegistryByteBuf buf, TradeOffer offer) {
		TradedItem.PACKET_CODEC.encode(buf, offer.getFirstBuyItem());
		ItemStack.PACKET_CODEC.encode(buf, offer.getSellItem());
		TradedItem.OPTIONAL_PACKET_CODEC.encode(buf, offer.getSecondBuyItem());
		buf.writeBoolean(offer.isDisabled());
		buf.writeInt(offer.getUses());
		buf.writeInt(offer.getMaxUses());
		buf.writeInt(offer.getMerchantExperience());
		buf.writeInt(offer.getSpecialPrice());
		buf.writeFloat(offer.getPriceMultiplier());
		buf.writeInt(offer.getDemandBonus());
	}

	public static TradeOffer read(RegistryByteBuf buf) {
		TradedItem tradedItem = TradedItem.PACKET_CODEC.decode(buf);
		ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
		Optional<TradedItem> optional = TradedItem.OPTIONAL_PACKET_CODEC.decode(buf);
		boolean bl = buf.readBoolean();
		int i = buf.readInt();
		int j = buf.readInt();
		int k = buf.readInt();
		int l = buf.readInt();
		float f = buf.readFloat();
		int m = buf.readInt();
		TradeOffer tradeOffer = new TradeOffer(tradedItem, optional, itemStack, i, j, k, f, m);
		if (bl) {
			tradeOffer.disable();
		}

		tradeOffer.setSpecialPrice(l);
		return tradeOffer;
	}
}
