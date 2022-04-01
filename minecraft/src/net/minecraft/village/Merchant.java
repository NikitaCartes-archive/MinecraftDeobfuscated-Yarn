package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;

public interface Merchant {
	void setCustomer(@Nullable PlayerEntity customer);

	@Nullable
	PlayerEntity getCustomer();

	TradeOfferList getOffers();

	void setOffersFromServer(TradeOfferList offers);

	void trade(TradeOffer offer);

	void onSellingItem(ItemStack stack);

	int getExperience();

	void setExperienceFromServer(int experience);

	boolean isLeveledMerchant();

	SoundEvent getYesSound();

	default boolean canRefreshTrades() {
		return false;
	}

	boolean isClient();
}
